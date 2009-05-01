package com.mindalliance.channels.query;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.Medium;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.UserIssue;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.dao.EvacuationScenario;
import com.mindalliance.channels.dao.FireScenario;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.util.Play;
import com.mindalliance.channels.util.SemMatch;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Query service instance.
 */
public class DefaultQueryService implements QueryService {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( DefaultQueryService.class );
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

    public DefaultQueryService() {
    }

    public DefaultQueryService( Dao dao ) {
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
        result.setQueryService( this );
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
        object.beforeRemove( this );
        getDao().remove( object );
    }

    /**
     * {@inheritDoc}
     */
    public Scenario getDefaultScenario() {
        List<Scenario> allScenarios = list( Scenario.class );
        Collections.sort( allScenarios, new Comparator<Scenario>() {
            public int compare( Scenario o1, Scenario o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        return allScenarios.get( 0 );
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
                loadScenarios();
            }
            // Make sure there is at least one scenario
            if ( !getDao().list( Scenario.class ).iterator().hasNext() ) {
                createScenario();
            }
        }
        getDao().afterInitialize();
    }

    @SuppressWarnings( "unchecked" )
    private void loadScenarios() {
        if ( importDirectory != null ) {
            File directory = new File( importDirectory );
            if ( directory.exists() && directory.isDirectory() ) {
                File[] files = directory.listFiles( new FilenameFilter() {
                    /** {@inheritDoc} */
                    public boolean accept( File dir, String name ) {
                        return name.endsWith( ".xml" );
                    }
                } );
                Importer importer = Channels.instance().getImporter();
                Map<String, Long> idMap = new HashMap<String, Long>();
                Map<Connector, ConnectionSpecification> proxyConnectors =
                        new HashMap<Connector, ConnectionSpecification>();
                for ( File file : files ) {
                    try {
                        Map<String, Object> results = importer.loadScenario(
                                new FileInputStream( file ) );
                        // Cumulate results
                        idMap.putAll( (Map<String, Long>) results.get( "idMap" ) );
                        proxyConnectors.putAll(
                                (Map<Connector, ConnectionSpecification>) results.get( "proxyConnectors" ) );
                        Scenario scenario = (Scenario) results.get( "scenario" );
                        LOG.info(
                                "Imported scenario "
                                        + scenario.getName()
                                        + " from "
                                        + file.getPath() );
                    } catch ( IOException e ) {
                        LOG.warn( "Failed to import " + file.getPath(), e );
                    }
                }
                // Reconnect external links
                importer.reconnectExternalFlows( idMap, proxyConnectors );
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
        return findAllPlays( resourceSpec, false );
    }

    /**
     * {@inheritDoc}
     */
    public List<Play> findAllPlays( ResourceSpec resourceSpec, boolean specific ) {
        Set<Play> plays = new HashSet<Play>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( Play.hasPlay( flow ) ) {
                    if ( flow.getSource().isPart() ) {
                        Part part = (Part) flow.getSource();
                        if ( part.resourceSpec().matches( resourceSpec, specific ) ) {
                            // sends
                            Play play = new Play( part, flow, true );
                            plays.add( play );
                        }
                    }
                    if ( flow.getTarget().isPart() ) {
                        Part part = (Part) flow.getTarget();
                        if ( part.resourceSpec().matches( resourceSpec, specific ) ) {
                            // receives
                            Play play = new Play( part, flow, false );
                            plays.add( play );
                        }
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
     * {@inheritDoc}
     */
    public List<Issue> findAllIssuesFor( ResourceSpec resourceSpec, boolean specific ) {
        return Channels.instance().getAnalyst().findAllIssuesFor(
                resourceSpec,
                specific );
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResponsibilitiesOf( Actor actor ) {
        List<ResourceSpec> responsibilities = new ArrayList<ResourceSpec>();
        List<ResourceSpec> resourceSpecs = this.findAllResourcesNarrowingOrEqualTo(
                ResourceSpec.with( actor ) );
        for ( ResourceSpec resourceSpec : resourceSpecs ) {
            ResourceSpec responsibility = new ResourceSpec( resourceSpec );
            assert responsibility.getActor() == actor;
            if ( !responsibility.isAnyRole() ) {
                responsibility.setActor( null );
                responsibilities.add( responsibility );
            }
        }
        return responsibilities;
    }

    /**
     * /**
     * {@inheritDoc}
     */
    public ScenarioRelationship findScenarioRelationship(
            Scenario fromScenario,
            Scenario toScenario ) {
        List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
        List<Part> initiators = new ArrayList<Part>();
        Iterator<Flow> flows = fromScenario.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( !flow.isInternal() ) {
                ExternalFlow externalFlow = (ExternalFlow) flow;
                if ( externalFlow.getConnector().getScenario() == toScenario ) {
                    externalFlows.add( externalFlow );
                }
            }
        }
        for ( Part part : toScenario.getInitiators() ) {
            if ( part.getScenario() == fromScenario ) initiators.add( part );
        }
        if ( externalFlows.isEmpty() && initiators.isEmpty() ) {
            return null;
        } else {
            ScenarioRelationship scenarioRelationship = new ScenarioRelationship(
                    fromScenario,
                    toScenario );
            scenarioRelationship.setExternalFlows( externalFlows );
            scenarioRelationship.setInitiators( initiators );
            return scenarioRelationship;
        }
    }


    /**
     * {@inheritDoc}
     */
    public EntityRelationship findEntityRelationship( ModelObject fromEntity, ModelObject toEntity ) {
        List<Flow> entityFlows = new ArrayList<Flow>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( flow.getSource().isPart() && flow.getTarget().isPart() ) {
                    Part sourcePart = (Part) flow.getSource();
                    Part targetPart = (Part) flow.getTarget();
                    if ( sourcePart.resourceSpec().hasEntity( fromEntity )
                            && targetPart.resourceSpec().hasEntity( toEntity ) ) {
                        entityFlows.add( flow );
                    }
                }
            }
        }
        if ( entityFlows.isEmpty() ) {
            return null;
        } else {
            EntityRelationship entityRel = new EntityRelationship( fromEntity, toEntity );
            entityRel.setFlows( entityFlows );
            return entityRel;
        }

    }


    /**
     * {@inheritDoc}
     */
    public List<Flow> findAllRelatedFlows( ResourceSpec resourceSpec, boolean asSource ) {
        List<Flow> relatedFlows = new ArrayList<Flow>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                Node node = asSource ? flow.getSource() : flow.getTarget();
                if ( node.isPart()
                        && resourceSpec.narrowsOrEquals( ( (Part) node ).resourceSpec() ) )
                    relatedFlows.add( flow );
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
        // else it would return every actor known to the app
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
     * Find all relevant channels for a given resource spec.
     *
     * @param spec the spec
     * @return the channels
     */
    public List<Channel> findAllChannelsFor( ResourceSpec spec ) {
        Set<Channel> channels = new HashSet<Channel>();

        for ( Scenario scenario : list( Scenario.class ) )
            for ( Iterator<Flow> flows = scenario.flows(); flows.hasNext(); ) {
                Flow flow = flows.next();
                Part p = flow.getContactedPart();
                if ( p != null && p.resourceSpec().matches( spec, true ) )
                    addUniqueChannels( channels, flow.getEffectiveChannels() );
            }

        if ( spec.getActor() != null ) {
            addUniqueChannels( channels, spec.getActor().getEffectiveChannels() );

            ResourceSpec s = new ResourceSpec( spec );
            s.setActor( null );
            addUniqueChannels( channels, findAllChannelsFor( s ) );
        }

        if ( spec.getJurisdiction() != null ) {
            ResourceSpec s = new ResourceSpec( spec );
            s.setJurisdiction( null );
            addUniqueChannels( channels, findAllChannelsFor( s ) );
        }

        if ( spec.getRole() != null ) {
            ResourceSpec s = new ResourceSpec( spec );
            s.setRole( null );
            addUniqueChannels( channels, findAllChannelsFor( s ) );
        }

        Organization organization = spec.getOrganization();
        if ( organization != null ) {
            addUniqueChannels( channels, organization.getEffectiveChannels() );

            ResourceSpec s = new ResourceSpec( spec );
            s.setOrganization( organization.getParent() );
            addUniqueChannels( channels, findAllChannelsFor( s ) );
        }

        List<Channel> result = new ArrayList<Channel>( channels );
        Collections.sort( result );
        return result;
    }

    private static void addUniqueChannels( Set<Channel> result, List<Channel> candidates ) {
        for ( Channel channel : candidates ) {
            Medium medium = channel.getMedium();
            if ( containsInvalidChannel( result, medium ) )
                result.remove( new Channel( medium, "" ) );
            if ( medium.isBroadcast() || !containsValidChannel( result, medium ) )
                result.add( channel );
        }
    }

    private static boolean containsValidChannel( Set<Channel> channels, Medium medium ) {
        for ( Channel channel : channels )
            if ( channel.getMedium() == medium && channel.isValid() )
                return true;
        return false;
    }

    private static boolean containsInvalidChannel( Set<Channel> channels, Medium medium ) {
        for ( Channel channel : channels )
            if ( channel.getMedium() == medium && !channel.isValid() )
                return true;
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public List<Flow> findAllFlowsContacting( ResourceSpec resourceSpec ) {
        List<Flow> flows = new ArrayList<Flow>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Flow> scenarioFlows = scenario.flows();
            while ( scenarioFlows.hasNext() ) {
                Flow flow = scenarioFlows.next();
                Part contactedPart = flow.getContactedPart();
                if ( contactedPart != null
                        && resourceSpec.narrowsOrEquals( contactedPart.resourceSpec() ) )
                    flows.add( flow );
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
        Set<Job> unconfirmedJobs = new HashSet<Job>();
        List<Job> confirmedJobs = organization.getJobs();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( organization == part.getOrganization() ) {
                    ResourceSpec resourceSpec = part.resourceSpec();
                    if ( resourceSpec.hasJob() ) {
                        Job job = Job.from( resourceSpec );
                        if ( job != null && !confirmedJobs.contains( job ) )
                            unconfirmedJobs.add( job );
                    }
                }
            }
        }
        return new ArrayList<Job>( unconfirmedJobs );
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
    public List<Actor> findActors( Organization organization, Role role ) {
        ResourceSpec resourceSpec = new ResourceSpec();
        resourceSpec.setRole( role );
        resourceSpec.setOrganization( organization );

        // Find all actors in role for organization
        Set<Actor> actors = new HashSet<Actor>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( spec.getActor() != null ) {
                boolean sameOrg = Organization.UNKNOWN.equals( organization ) ?
                        spec.getOrganization() == null
                        : organization.equals( spec.getOrganization() );
                boolean sameRole = Role.UNKNOWN.equals( role ) ?
                        spec.getRole() == null
                        : role.equals( spec.getRole() );
                if ( sameOrg && sameRole )
                    actors.add( spec.getActor() );
            }
        }

        List<Actor> list = new ArrayList<Actor>( actors );
        Collections.sort( list, new Comparator<Actor>() {
            /** {@inheritDoc} */
            public int compare( Actor o1, Actor o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );

        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<Role> findRolesIn( Organization organization ) {
        Set<Role> roles = new HashSet<Role>();
        for ( Scenario scenario : list( Scenario.class ) )
            roles.addAll( scenario.findRoles( organization ) );

        boolean hasUnknown = roles.contains( Role.UNKNOWN );
        roles.remove( Role.UNKNOWN );

        List<Role> list = new ArrayList<Role>( roles );
        Collections.sort( list, new Comparator<Role>() {
            /** {@inheritDoc} */
            public int compare( Role o1, Role o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        if ( hasUnknown )
            list.add( Role.UNKNOWN );
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<Organization> findOrganizations() {
        List<Organization> orgs = new ArrayList<Organization>(
                new HashSet<Organization>( list( Organization.class ) ) );

        Collections.sort( orgs, new Comparator<Organization>() {
            /** {@inheritDoc} */
            public int compare( Organization o1, Organization o2 ) {
                return Collator.getInstance().compare( o1.toString(), o2.toString() );
            }
        } );

        if ( !findRolesIn( Organization.UNKNOWN ).isEmpty() )
            orgs.add( Organization.UNKNOWN );

        return orgs;
    }

    /**
     * {@inheritDoc}
     */
    public List<Actor> findRelevantActors( Part part, Flow flow ) {
        Set<Actor> actors = new HashSet<Actor>();

        boolean partIsSource = flow.getSource().equals( part );
        Node node = partIsSource ? flow.getTarget() : flow.getSource();
        if ( node.isConnector() ) {
            Iterator<ExternalFlow> xFlows = ( (Connector) node ).externalFlows();
            while ( xFlows.hasNext() ) {
                ExternalFlow xFlow = xFlows.next();
                actors.addAll( findAllActors( xFlow.getPart().resourceSpec() ) );
            }
        } else {
            Part otherPart = (Part) node;
            if ( otherPart.getActor() == null )
                actors.addAll( findAllActors( otherPart.resourceSpec() ) );
        }

        List<Actor> list = new ArrayList<Actor>( actors );
        Collections.sort( list, new Comparator<Actor>() {
            public int compare( Actor o1, Actor o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );

        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<Actor> findActors( Organization organization, Role role, Scenario scenario ) {
        Set<Actor> actors = new HashSet<Actor>();
        boolean noActorRoleFound = false;

        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            boolean sameOrg = Organization.UNKNOWN.equals( organization ) ?
                    part.getOrganization() == null
                    : organization.equals( part.getOrganization() );
            boolean sameRole = Role.UNKNOWN.equals( role ) ?
                    part.getRole() == null
                    : role.equals( part.getRole() );

            if ( sameOrg && sameRole ) {
                if ( part.getActor() != null )
                    actors.add( part.getActor() );
                else
                    noActorRoleFound = true;
            }
        }

        if ( noActorRoleFound )
            return findActors( organization, role );
        else {
            List<Actor> list = new ArrayList<Actor>( actors );
            Collections.sort( list, new Comparator<Actor>() {
                /** {@inheritDoc} */
                public int compare( Actor o1, Actor o2 ) {
                    return Collator.getInstance().compare( o1.getName(), o2.getName() );
                }
            } );
            return list;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Job> findAllJobs( Actor actor ) {
        List<Job> jobs = new ArrayList<Job>();
        for ( Organization org : list( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( job.getActor() == actor ) {
                    jobs.add( job );
                }
            }
            for ( Job job : findUnconfirmedJobs( org ) ) {
                if ( job.getActor() == actor ) {
                    jobs.add( job );
                }
            }
        }
        return jobs;
    }

    /**
     * {@inheritDoc}
     */
    public List<Job> findAllConfirmedJobs( ResourceSpec resourceSpec ) {
        List<Job> jobs = new ArrayList<Job>();
        for ( Organization org : list( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( job.resourceSpec( org ).narrowsOrEquals( resourceSpec ) ) {
                    jobs.add( job );
                }
            }
        }
        return jobs;
    }

    /**
     * {@inheritDoc
     */
    public List<String> findJobTitles( Actor actor ) {
        String actorName = actor.getName();
        List<String> titles = new ArrayList<String>();
        for ( Organization org : list( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( job.getActorName().equals( actorName ) ) {
                    String title = job.getTitle();
                    if ( !title.isEmpty() ) titles.add( title );
                }
            }
        }
        return titles;
    }

    /**
     * {@inheritDoc}
     */
    public boolean findIfPartStarted( Part part ) {
        return doFindIfPartStarted( part, new HashSet<ModelObject>() );
    }

    private boolean doFindIfPartStarted( Part part, Set<ModelObject> visited ) {
        if ( visited.contains( part ) ) return false;
        visited.add( part );
        if ( part.isStartsWithScenario() ) {
            return doFindIfScenarioStarted( part.getScenario(), visited );
        } else {
            boolean started = false;
            Iterator<Flow> reqs = part.requirements();
            while ( !started && reqs.hasNext() ) {
                Flow req = reqs.next();
                if ( req.isTriggeringToTarget() ) {
                    Node source = req.getSource();
                    started = source.isPart() && doFindIfPartStarted( (Part) source, visited );
                }
            }
            if ( !started ) {
                Iterator<Flow> outs = part.outcomes();
                while ( !started && outs.hasNext() ) {
                    Flow req = outs.next();
                    // A task-triggering request from target of response.
                    if ( req.isTriggeringToSource() ) {
                        Node target = req.getTarget();
                        started = target.isPart() && doFindIfPartStarted( (Part) target, visited );
                    }
                }
            }
            return started;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean findIfScenarioStarted( Scenario scenario ) {
        return doFindIfScenarioStarted( scenario, new HashSet<ModelObject>() );

    }

    /**
     * {@inheritDoc}
     */
    public List<Part> findAllPartsWith( ResourceSpec resourceSpec ) {
        List<Part> list = new ArrayList<Part>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.resourceSpec().narrowsOrEquals( resourceSpec ) )
                    list.add( part );
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<Part> findAllPartsWithLocation( Place place ) {
        List<Part> list = new ArrayList<Part>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( SemMatch.samePlace( part.getLocation(), place ) )
                    list.add( part );
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<Flow> findUnsatisfiedNeeds( Part part ) {
        List<Flow> unsatisfiedNeeds = new ArrayList<Flow>();
        Iterator<Flow> receives = part.requirements();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            if ( receive.getSource().isConnector() ) {
                if ( !( (Connector) receive.getSource() ).externalFlows().hasNext() ) {
                    Iterator<Flow> others = part.requirements();
                    boolean satisfied = false;
                    while ( !satisfied && others.hasNext() ) {
                        Flow other = others.next();
                        Node source = other.getSource();
                        satisfied = !source.isConnector()
                                && SemMatch.matches( receive.getName(), other.getName() )
                                && SemMatch.matches( receive.getDescription(), other.getDescription() );
                    }
                    if ( !satisfied ) unsatisfiedNeeds.add( receive );
                }
            }
        }
        return unsatisfiedNeeds;
    }

    /**
     * {@inheritDoc}
     */
    public List<Flow> findUnusedCapabilities( Part part ) {
        List<Flow> unusedCapabilities = new ArrayList<Flow>();
        Iterator<Flow> sends = part.outcomes();
        while ( sends.hasNext() ) {
            Flow send = sends.next();
            if ( send.getTarget().isConnector() ) {
                if ( !( (Connector) send.getTarget() ).externalFlows().hasNext() ) {
                    Iterator<Flow> others = part.outcomes();
                    boolean used = false;
                    while ( !used && others.hasNext() ) {
                        Flow other = others.next();
                        Node target = other.getTarget();
                        used = !target.isConnector()
                                && SemMatch.matches( send.getName(), other.getName() )
                                && SemMatch.matches( send.getDescription(), other.getDescription() );
                    }
                    if ( !used ) unusedCapabilities.add( send );
                }
            }
        }
        return unusedCapabilities;
    }

    /**
     * {@inheritDoc}
     */
    public List<Connector> findAllSatificers( Flow need ) {
        List<Connector> connectors = new ArrayList<Connector>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part != need.getTarget() ) {
                    Iterator<Flow> outcomes = part.outcomes();
                    while ( outcomes.hasNext() ) {
                        Flow outcome = outcomes.next();
                        if ( outcome.getTarget().isConnector()
                                && SemMatch.matches( outcome.getName(), need.getName() )
                                && SemMatch.matches( outcome.getDescription(), need.getDescription() ) ) {
                            connectors.add( (Connector) outcome.getTarget() );
                        }
                    }
                }
            }
        }
        return connectors;
    }

    private boolean doFindIfScenarioStarted( Scenario scenario, Set<ModelObject> visited ) {
        if ( scenario.isIncident() ) return true;
        if ( visited.contains( scenario ) ) return false;
        visited.add( scenario );
        boolean started = false;
        Iterator<Part> initiators = scenario.getInitiators().iterator();
        while ( !started && initiators.hasNext() ) {
            started = doFindIfPartStarted( initiators.next(), visited );
        }
        return started;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReferenced( Actor actor ) {
        for ( Organization org : list( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( job.getActor() == actor ) return true;
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
                if ( job.getRole() == role ) return true;
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
                if ( job.getJurisdiction() == place ) return true;
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

