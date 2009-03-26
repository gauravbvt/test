package com.mindalliance.channels.service;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.AbstractUnicastChannelable;
import com.mindalliance.channels.Job;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.dao.EvacuationScenario;
import com.mindalliance.channels.dao.FireScenario;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.util.Play;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Collections;

/**
 * Utility class for common functionality for all Dao implementations.
 * Done this way because of HibernateDao requires a specific superclass
 * and java does not support multiple inheritance...
 */
public class ChannelsServiceImpl implements Service {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( ChannelsServiceImpl.class );
    /**
     * The implementation dao.
     */
    private Dao dao;

    /**
     * True if defaults scenarios will be added when dao is set.
     */
    private boolean addingSamples;

    /**
     * True if exported scenarios are to be imported when dao is set.
     */
    private boolean importingScenarios;

    /**
     * Directory from which to import exported scenarios
     */
    private String importDirectory;

    public ChannelsServiceImpl() {
    }

    public ChannelsServiceImpl( Dao dao ) {
        setAddingSamples( false );
        setDao( dao );
    }

    /**
     * {@inheritDoc}
     */
    public Scenario createScenario() {
        Scenario result = new Scenario();
        getDao().add( result );

        result.setName( Scenario.DEFAULT_NAME );
        result.setDescription( Scenario.DEFAULT_DESCRIPTION );
        result.setService( this );
        createPart( result );

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector( Scenario scenario ) {
        Connector result = getDao().createConnector( scenario );
        scenario.addNode( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Part createPart( Scenario scenario ) {
        Part result = getDao().createPart( scenario );
        scenario.addNode( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Flow connect( Node source, Node target, String name ) {
        Flow result;

        if ( isInternal( source, target ) ) {
            result = getDao().createInternalFlow( source, target, name );
            source.addOutcome( result );
            target.addRequirement( result );

        } else if ( isExternal( source, target ) ) {
            result = getDao().createExternalFlow( source, target, name );
            if ( source.isConnector() ) {
                target.addRequirement( result );
                ( (Connector) source ).addExternalFlow( (ExternalFlow) result );
            } else {
                source.addOutcome( result );
                ( (Connector) target ).addExternalFlow( (ExternalFlow) result );
            }

        } else
            throw new IllegalArgumentException();

        return result;
    }

    private static boolean isInternal( Node source, Node target ) {
        Scenario scenario = source.getScenario();
        return scenario != null && scenario.equals( target.getScenario() );
    }

    private static boolean isExternal( Node source, Node target ) {
        Scenario scenario = source.getScenario();
        return scenario != null
                && !scenario.equals( target.getScenario() )
                && ( target.isConnector() || source.isConnector() );
    }

    /**
     * {@inheritDoc}
     */
    public Scenario findScenario( String name ) throws NotFoundException {
        for ( Scenario s : getDao().list( Scenario.class ) ) {
            if ( name.equals( s.getName() ) )
                return s;
        }

        throw new NotFoundException();
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        return getDao().find( clazz, id );
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> List<T> list( Class<T> clazz ) {
        return getDao().list( clazz );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<ModelObject> iterateEntities() {
        return new FilterIterator( getDao().list( ModelObject.class ).iterator(), new Predicate() {
            public boolean evaluate( Object object ) {
                return ( (ModelObject) object ).isEntity();
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    public void add( ModelObject object ) {
        getDao().add( object );
    }

    /**
     * {@inheritDoc}
     */
    public void update( ModelObject object ) {
        getDao().update( object );
    }

    /**
     * {@inheritDoc}
     */
    public void remove( ModelObject object ) {
        getDao().remove( object );
    }

    /**
     * {@inheritDoc}
     */
    public Scenario getDefaultScenario() {
        return getDao().list( Scenario.class ).iterator().next();
    }

    public Dao getDao() {
        return dao;
    }

    /**
     * Use a specific dao.
     *
     * @param dao the dao
     */
    public void setDao( Dao dao ) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    public void initialize() {
        getDao().load();
        if ( !getDao().list( Scenario.class ).iterator().hasNext() ) {
            if ( addingSamples ) {
                LOG.info( "Adding sample models" );
                Scenario evac = createScenario();
                EvacuationScenario.initialize( evac, this );
                Scenario fireScenario = createScenario();
                FireScenario.initialize( fireScenario, this, evac );
            }
            if ( importingScenarios ) {
                LOG.info( "Importing default models" );
                importScenarios();
            }
            // Make sure there is at least one scenario
            if ( !getDao().list( Scenario.class ).iterator().hasNext() ) {
                createScenario();
            }
        }
        getDao().afterInitialize();
    }

    private void importScenarios() {
        if ( importDirectory != null ) {
            File directory = new File( importDirectory );
            if ( directory.exists() && directory.isDirectory() ) {
                File[] files = directory.listFiles( new FilenameFilter() {
                    /** {@inheritDoc} */
                    public boolean accept( File dir, String name ) {
                        return name.endsWith( ".xml" );
                    }
                } );
                Importer importer = Project.getProject().getImporter();
                for ( File file : files ) {
                    try {
                        Scenario scenario = importer.importScenario( new FileInputStream( file ) );
                        LOG.info(
                                "Imported scenario "
                                        + scenario.getName()
                                        + " from "
                                        + file.getPath() );
                    } catch ( IOException e ) {
                        LOG.warn( "Failed to import " + file.getPath(), e );
                    }
                }
                /*
                Scenario scenario = project.getImporter().importScenario( in );
                 */
            } else {
                LOG.warn( "Directory " + importDirectory + " does not exist." );
            }
        } else {
            LOG.warn( "Import directory is not set." );
        }
    }

    public boolean isAddingSamples() {
        return addingSamples;
    }

    public void setAddingSamples( boolean addingSamples ) {
        this.addingSamples = addingSamples;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T findOrCreate( Class<T> clazz, String name ) {
        if ( name == null || name.isEmpty() )
            return null;

        T result = getDao().find( clazz, name );
        if ( result == null ) {
            try {
                result = clazz.newInstance();
                result.setName( name );
                getDao().add( result );
            } catch ( InstantiationException e ) {
                throw new RuntimeException( e );
            } catch ( IllegalAccessException e ) {
                throw new RuntimeException( e );
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourceSpecs() {
        Set<ResourceSpec> result = new HashSet<ResourceSpec>();
        // Specs from entities
        for ( Actor actor : list( Actor.class ) ) {
            result.add( ResourceSpec.with( actor ) );
        }
        for ( Role role : list( Role.class ) ) {
            result.add( ResourceSpec.with( role ) );
        }
        for ( Organization organization : list( Organization.class ) ) {
            result.add( ResourceSpec.with( organization ) );
            result.addAll( organization.jobResourceSpecs( this ) );
        }
        // Specs from scenario parts
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                ResourceSpec partResourceSpec = part.resourceSpec();
                if ( !partResourceSpec.isAnyone() ) {
                    result.add( partResourceSpec );
                }
            }
        }

        return new ArrayList<ResourceSpec>( result );
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( ResourceSpec resourceSpec ) {
        List<ResourceSpec> list = new ArrayList<ResourceSpec>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( spec.narrowsOrEquals( resourceSpec ) )
                list.add( spec );
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourcesBroadeningOrEqualTo( ResourceSpec resourceSpec ) {
        List<ResourceSpec> list = new ArrayList<ResourceSpec>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( resourceSpec.narrowsOrEquals( spec ) )
                list.add( spec );
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllContacts( ResourceSpec resourceSpec, boolean isSelf ) {
        Set<ResourceSpec> contacts = new HashSet<ResourceSpec>();
        if ( isSelf ) {
            contacts.addAll( findAllResourcesNarrowingOrEqualTo( resourceSpec ) );
        } else {
            List<Play> plays = findAllPlays( resourceSpec );
            for ( Play play : plays ) {
                ResourceSpec partSpec = play.getPart().resourceSpec();
                ResourceSpec otherPartSpec = play.getOtherPart().resourceSpec();
                if ( !partSpec.isAnyone() ) contacts.add( partSpec );
                if ( !otherPartSpec.isAnyone() ) contacts.add( otherPartSpec );
            }
        }
        return new ArrayList<ResourceSpec>( contacts );
    }

    /**
     * {@inheritDoc}
     */
    public List<Play> findAllPlays( ResourceSpec resourceSpec ) {
        Set<Play> plays = new HashSet<Play>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( Play.hasPlay( flow ) ) {
                    if ( flow.getSource().isPart()
                            && ( (Part) flow.getSource() ).resourceSpec().narrowsOrEquals( resourceSpec ) ) {
                        // sends
                        Play play = new Play( (Part) flow.getSource(), flow, true );
                        plays.add( play );
                    }
                    if ( flow.getTarget().isPart()
                            && ( (Part) flow.getTarget() ).resourceSpec().narrowsOrEquals( resourceSpec ) ) {
                        // receives
                        Play play = new Play( (Part) flow.getTarget(), flow, false );
                        plays.add( play );
                    }
                }
            }
        }
        return new ArrayList<Play>( plays );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> findAllUserIssues( ModelObject identifiable ) {
        List<Issue> foundIssues = new ArrayList<Issue>();
        for ( UserIssue userIssue : list( UserIssue.class ) ) {
            if ( userIssue.getAbout().getId() == identifiable.getId() )
                foundIssues.add( userIssue );
        }
        return foundIssues;
    }


    /**
     * Find all flows in all scenarios where the part applies as specified (as source or target).
     *
     * @param resourceSpec a resource spec
     * @param asSource     a boolean
     * @return a list of flows
     */
    public List<Flow> findAllRelatedFlows( ResourceSpec resourceSpec, boolean asSource ) {
        Service service = Project.getProject().getService();
        List<Flow> relatedFlows = new ArrayList<Flow>();
        for ( Scenario scenario : service.list( Scenario.class ) ) {
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                Node node = asSource ? flow.getSource() : flow.getTarget();
                if ( node.isPart()
                        && resourceSpec.narrowsOrEquals( ( (Part) node ).resourceSpec() ) ) {
                    relatedFlows.add( flow );
                }
            }
        }
        return relatedFlows;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public List<Actor> findAllActors( ResourceSpec resourceSpec ) {
        Set<Actor> actors = new HashSet<Actor>();
        // If the resource spec is anyone, then return no actor,
        // else it would return every actor known to the project
        if ( !resourceSpec.isAnyone() ) {
            Iterator<ResourceSpec> actorResourceSpecs = new FilterIterator(
                    findAllResourceSpecs().iterator(),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return ( (ResourceSpec) object ).getActor() != null;
                        }
                    } );
            while ( actorResourceSpecs.hasNext() ) {
                ResourceSpec actorResourceSpec = actorResourceSpecs.next();
                if ( actorResourceSpec.narrowsOrEquals( resourceSpec ) ) {
                    actors.add( actorResourceSpec.getActor() );
                }
            }
        }
        return new ArrayList<Actor>( actors );
    }

    /**
     * {@inheritDoc}
     */
    public List<Channel> findAllCandidateChannelsFor( Channelable channelable ) {
        if ( channelable instanceof Flow ) {
            return findAllCandidateChannelsForFlow( (Flow) channelable );
        } else {
            return findAllCandidateChannelsForUnicastChannelable(
                    (AbstractUnicastChannelable) channelable );
        }
    }

    /**
     * {@inheritDoc}
     */
    private List<Channel> findAllCandidateChannelsForFlow( Flow flow ) {
        final Set<Channel> channels = new HashSet<Channel>();
        List<Channel> currentChannels = flow.getEffectiveChannels();
        Part part = flow.getContactedPart();
        if ( part != null ) {
            List<Flow> relatedFlows = findAllFlowsContacting( part.resourceSpec() );
            for ( Flow relatedFlow : relatedFlows ) {
                if ( relatedFlow != flow ) {
                    for ( Channel channel : relatedFlow.getEffectiveChannels() ) {
                        if ( relatedFlow.validate( channel ) == null
                                && !currentChannels.contains( channel ) )
                            channels.add( new Channel( channel.getMedium(), channel.getAddress() ) );
                    }
                }
            }
        }
        return new ArrayList<Channel>() {
            {
                addAll( channels );
            }
        };
    }

    private List<Channel> findAllCandidateChannelsForUnicastChannelable(
            AbstractUnicastChannelable channelable ) {
        final Set<Channel> channels = new HashSet<Channel>();
        List<Channel> currentChannels = channelable.getEffectiveChannels();
        ResourceSpec resourceSpec = ResourceSpec.with( channelable );
        List<Flow> relatedFlows = findAllFlowsContacting( resourceSpec );
        for ( Flow relatedFlow : relatedFlows ) {
            for ( Channel channel : relatedFlow.getEffectiveChannels() ) {
                if ( channel.isUnicast()
                        && relatedFlow.validate( channel ) == null
                        && !currentChannels.contains( channel ) )
                    channels.add( new Channel( channel.getMedium(), channel.getAddress() ) );
            }
        }
        return new ArrayList<Channel>() {
            {
                addAll( channels );
            }
        };
    }


    /**
     * {@inheritDoc}
     */
    public List<Flow> findAllFlowsContacting( ResourceSpec resourceSpec ) {
        List<Flow> flows = new ArrayList<Flow>();
        for ( Scenario scenario : this.list( Scenario.class ) ) {
            Iterator<Flow> scenarioFlows = scenario.flows();
            while ( scenarioFlows.hasNext() ) {
                Flow flow = scenarioFlows.next();
                Part contactedPart = flow.getContactedPart();
                if ( contactedPart != null
                        && resourceSpec.narrowsOrEquals( contactedPart.resourceSpec() ) ) {
                    flows.add( flow );
                }
            }
        }
        return flows;
    }

    public boolean isImportingScenarios() {
        return importingScenarios;
    }

    public void setImportingScenarios( boolean importingScenarios ) {
        this.importingScenarios = importingScenarios;
    }

    public String getImportDirectory() {
        return importDirectory;
    }

    public void setImportDirectory( String importDirectory ) {
        this.importDirectory = importDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public Flow replicate( Flow flow, boolean isOutcome ) {
        Flow result = isOutcome ? connect( flow.getSource(),
                createConnector( flow.getSource().getScenario() ),
                flow.getName() )
                : connect( createConnector( flow.getTarget().getScenario() ),
                flow.getTarget(),
                flow.getName() );
        result.initFrom( flow );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        getDao().flush();
    }

    /**
     * {@inheritDoc}
     */
    public List<Job> findUnconfirmedJobs( Organization organization ) {
        final Set<Job> unconfirmedJobs = new HashSet<Job>();
        List<Job> confirmedJobs = organization.getJobs();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( organization == part.getOrganization() ) {
                    ResourceSpec resourceSpec = part.resourceSpec();
                    if ( resourceSpec.hasJob() ) {
                        Job job = Job.from( resourceSpec );
                        if ( !confirmedJobs.contains( job ) )
                            unconfirmedJobs.add( job );
                    }
                }
            }
        }
        List<Job> allUnconfirmedJobs = new ArrayList<Job>();
        allUnconfirmedJobs.addAll( unconfirmedJobs );
        return allUnconfirmedJobs;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> findAllJobTitles() {
        Set<String> titles = new HashSet<String>();
        for ( Organization organization : list( Organization.class ) ) {
            for ( Job job : organization.getJobs() ) {
                titles.add( job.getTitle() );
            }
        }
        List<String> allTitles = new ArrayList<String>();
        allTitles.addAll( titles );
        Collections.sort( allTitles );
        return allTitles;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> findAllTasks() {
        Set<String> tasks = new HashSet<String>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                tasks.add( parts.next().getTask() );
            }
        }
        List<String> allTasks = new ArrayList<String>();
        allTasks.addAll( tasks );
        Collections.sort( allTasks );
        return allTasks;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> findAllNames( Class<? extends ModelObject> aClass ) {
        List<String> allNames = new ArrayList<String>();
        for ( ModelObject mo : list( aClass ) ) {
            allNames.add( mo.getName() );
        }
        Collections.sort( allNames );
        return allNames;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReferenced( Actor actor ) {
        for ( Organization org : list( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( job.getActorName().equals( actor.getName() ) ) return true;
            }
        }
        // Look in parts
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                if ( parts.next().getActor() == actor ) return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReferenced( Role role ) {
        for ( Organization org : list( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( job.getRoleName().equals( role.getName() ) ) return true;
            }
        }
        // Look in parts
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                if ( parts.next().getRole() == role ) return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReferenced( Organization organization ) {
        for ( Organization org : list( Organization.class ) ) {
            if ( org.getParent() == organization ) return true;
        }
        // Look in parts
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                if ( parts.next().getOrganization() == organization ) return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReferenced( Place place ) {
        for ( Organization org : list( Organization.class ) ) {
            if ( org.getLocation() == place ) return true;
            else for ( Job job : org.getJobs() ) {
                if ( job.getJurisdictionName().equals( place.getName() ) ) return true;
            }
        }
        // Look in parts
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.getLocation() == place || part.getJurisdiction() == place ) return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void onDestroy() {
        getDao().onDestroy();
    }
}

