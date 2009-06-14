package com.mindalliance.channels.query;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.dao.EvacuationScenario;
import com.mindalliance.channels.dao.FireScenario;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.Medium;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.UserIssue;
import com.mindalliance.channels.util.Employment;
import com.mindalliance.channels.util.Play;
import com.mindalliance.channels.util.SemMatch;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.FilterIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

/**
 * Query service instance.
 */
public class DefaultQueryService extends Observable implements QueryService {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( DefaultQueryService.class );
    /**
     * Name of the default event.
     */
    public static final String DEFAULT_EVENT_NAME = "UNNAMED";

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

    /**
     * An attachment manager.
     */
    private AttachmentManager attachmentManager;

    public DefaultQueryService() {
    }

    public DefaultQueryService( Dao dao ) {
        setDao( dao );
    }

    // PROPERTIES

    public Dao getDao() {
        return dao;
    }

    /**
     * {@inheritDoc}
     */
    public Long getLastAssignedId() {
        return getDao().getLastAssignedId();
    }

    /**
     * {@inheritDoc}
     */
    public void setLastAssignedId( Long lastId ) {
        getDao().setLastAssignedId( lastId );
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public void setAttachmentManager( AttachmentManager attachmentManager ) {
        this.attachmentManager = attachmentManager;
    }

    /**
     * Use a specific dao.
     *
     * @param dao the dao
     */
    public void setDao( Dao dao ) {
        this.dao = dao;
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

    public boolean isAddingSamples() {
        return addingSamples;
    }

    public void setAddingSamples( boolean addingSamples ) {
        this.addingSamples = addingSamples;
    }

    // LIFECYCLE

    /**
     * {@inheritDoc}
     */
    public void flush() {
        getDao().flush();
    }

    /**
     * {@inheritDoc}
     */
    public void onDestroy() {
        getDao().onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    public void initialize() {
        // TODO - conflict between loaded plan and context-created plan? (ids referenced by plan issues)
        // First make plans initialized from application context persistent
        for ( Plan plan : Channels.instance().getPlans() ) {
            add( plan );
        }
        getDao().load();
        // Make sure there is at least one plan, if not create it, optionally adding/import scenarios into it.
        List<Plan> plans = getChannels().getPlans();
        if ( plans.isEmpty() ) {
            Plan plan = getDao().createPlan();
            plans.add( plan );
        }
        // make sure each plan has at least one event
        for ( Plan plan : getChannels().getPlans() ) {
            if ( plan.getIncidents().isEmpty() ) {
                Event unnamedEvent = findOrCreate( Event.class, DEFAULT_EVENT_NAME );
                plan.addIncident( unnamedEvent );
            }
        }
        if ( plans.size() == 1 && plans.get( 0 ).getScenarios().isEmpty() ) {
            try {
                getChannels().beginUsingPlan( plans.get( 0 ) );
                if ( addingSamples ) {
                    LOG.info( "Adding sample models to default plan" );
                    Scenario evac = createScenario();
                    EvacuationScenario.initialize( evac, this );
                    Scenario fireScenario = createScenario();
                    FireScenario.initialize( fireScenario, this, evac );
                }
                if ( importingScenarios ) {
                    LOG.info( "Importing default models to default plan" );
                    loadScenarios();
                }
            } finally {
                getChannels().endUsingPlan();
            }
        }
        for ( Plan plan : getChannels().getPlans() ) {
            getChannels().beginUsingPlan( plan );
            try {
                // Make sure there is at least one scenario per plan
                if ( !getDao().list( Scenario.class ).iterator().hasNext() ) {
                    createScenario();
                }
                // Remove UNNAMED event if not referenced
                Event event = findOrCreate( Event.class, DEFAULT_EVENT_NAME );
                if ( getReferenceCount( event ) <= 1 ) {
                    plan.getIncidents().remove( event );
                    remove( event );
                }
            } finally {
                getChannels().endUsingPlan();
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
                Importer importer = getChannels().getImporter();
                Map<String, Long> idMap = new HashMap<String, Long>();
                Map<Connector, List<ConnectionSpecification>> proxyConnectors =
                        new HashMap<Connector, List<ConnectionSpecification>>();
                for ( File file : files ) {
                    try {
                        Map<String, Object> results = importer.loadScenario(
                                new FileInputStream( file ) );
                        // Cumulate results
                        idMap.putAll( (Map<String, Long>) results.get( "idMap" ) );
                        proxyConnectors.putAll(
                                (Map<Connector, List<ConnectionSpecification>>) results.get( "proxyConnectors" ) );
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
                importer.reconnectExternalFlows( proxyConnectors, false );
                setChanged();
                notifyObservers( idMap );
            } else {
                LOG.warn( "Directory " + importDirectory + " does not exist." );
            }
        } else {
            LOG.warn( "Import directory is not set." );
        }
    }

    // CRUD

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        try {
            return getDao().find( clazz, id );
        } catch ( NotFoundException e ) {
            return findUnknown( clazz, id );
        }
    }

    @SuppressWarnings( "unchecked" )
    private <T extends ModelObject> T findUnknown( Class<T> clazz, long id ) throws NotFoundException {
        if ( clazz.isAssignableFrom( Actor.class ) && Actor.UNKNOWN.getId() == id )
            return (T) Actor.UNKNOWN;
        else if ( clazz.isAssignableFrom( Event.class ) && Event.UNKNOWN.getId() == id )
            return (T) Event.UNKNOWN;
        else if ( clazz.isAssignableFrom( Organization.class ) && Organization.UNKNOWN.getId() == id )
            return (T) Organization.UNKNOWN;
        else if ( clazz.isAssignableFrom( Place.class ) && Place.UNKNOWN.getId() == id )
            return (T) Place.UNKNOWN;
        else if ( clazz.isAssignableFrom( Role.class ) && Role.UNKNOWN.getId() == id )
            return (T) Role.UNKNOWN;
        else
            throw new NotFoundException();
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
    public <T extends ModelObject> List<T> listReferenced( Class<T> clazz ) {
        return (List<T>) CollectionUtils.select( list( clazz ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return isModelObjectReferenced( (ModelObject) obj );
                    }
                } );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<ModelObject> iterateEntities() {
        return new FilterIterator( listReferenced( ModelObject.class ).iterator(), new Predicate() {
            public boolean evaluate( Object object ) {
                return ( (ModelObject) object ).isEntity();
            }
        } );
    }

    private boolean isModelObjectReferenced( ModelObject modelObject ) {
        if ( modelObject instanceof Actor ) return isReferenced( (Actor) modelObject );
        if ( modelObject instanceof Role ) return isReferenced( (Role) modelObject );
        if ( modelObject instanceof Organization ) return isReferenced( (Organization) modelObject );
        if ( modelObject instanceof Place ) return isReferenced( (Place) modelObject );
        return !( modelObject instanceof Event ) || isReferenced( (Event) modelObject );
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T findOrCreate( Class<T> clazz, String name ) {
        return findOrCreate( clazz, name, null );
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T findOrCreate( Class<T> clazz, String name, Long id ) {
        if ( name == null || name.isEmpty() )
            return null;

        T result = getDao().find( clazz, name );
        if ( result == null ) {
            try {
                result = clazz.newInstance();
                result.setName( name );
                getDao().add( result, id );
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
    public void add( ModelObject object ) {
        getDao().add( object );
    }

    /**
     * {@inheritDoc}
     */
    public void add( ModelObject object, Long id ) {
        getDao().add( object, id );
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
    public Scenario createScenario() {
        return createScenario( null );
    }

    /**
     * {@inheritDoc}
     */
    public Scenario createScenario( Long id ) {
        return createScenario( id, null );
    }

    /**
     * {@inheritDoc}
     */
    public Scenario createScenario( Long id, Long defaultPartId ) {
        Scenario result = new Scenario();
        if ( id == null )
            getDao().add( result );
        else
            getDao().add( result, id );
        result.setName( Scenario.DEFAULT_NAME );
        result.setDescription( Scenario.DEFAULT_DESCRIPTION );
        // Make sure a scenario responds to an event.
        result.setEvent( Channels.getPlan().getDefaultEvent() );
        result.setQueryService( this );
        createPart( result, defaultPartId );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInitiated( Scenario scenario ) {
        return !findInitiators( scenario ).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public List<Part> findInitiators( Scenario scenario ) {
        List<Part> initiators = new ArrayList<Part>();
        Event event = scenario.getEvent();
        for ( Scenario sc : list( Scenario.class ) ) {
            if ( sc != scenario ) {
                Iterator<Part> parts = sc.parts();
                while ( parts.hasNext() ) {
                    Part part = parts.next();
                    if ( part.getInitiatedEvent() == event ) initiators.add( part );
                }
            }
        }
        return initiators;
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector( Scenario scenario ) {
        return createConnector( scenario, null );
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector( Scenario scenario, Long id ) {
        Connector result = getDao().createConnector( scenario, id );
        scenario.addNode( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Part createPart( Scenario scenario ) {
        return createPart( scenario, null );
    }

    /**
     * {@inheritDoc}
     */
    public Part createPart( Scenario scenario, Long id ) {
        Part result = getDao().createPart( scenario, id );
        scenario.addNode( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Flow connect( Node source, Node target, String name ) {
        return connect( source, target, name, null );
    }

    /**
     * {@inheritDoc}
     */
    public Flow connect( Node source, Node target, String name, Long id ) {
        Flow result;

        if ( isInternal( source, target ) ) {
            result = getDao().createInternalFlow( source, target, name, id );
            source.addOutcome( result );
            target.addRequirement( result );

        } else if ( isExternal( source, target ) ) {
            result = getDao().createExternalFlow( source, target, name, id );
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

    private static void addUniqueChannels( Set<Channel> result, List<Channel> candidates ) {
        for ( Channel channel : candidates ) {
            Medium medium = channel.getMedium();
            if ( containsInvalidChannel( result, medium ) )
                result.remove( new Channel( medium, "" ) );
            if ( medium.isBroadcast() || !containsValidChannel( result, medium ) )
                result.add( channel );
        }
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

    // QUERIES (no change to model)

    /**
     * {@inheritDoc}
     */
    public Scenario getDefaultScenario() {
        return toSortedList( list( Scenario.class ) ).get( 0 );
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
        for ( Scenario scenario : list( Scenario.class ) ) {
            // Look in parts
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                if ( parts.next().getOrganization() == organization ) return true;
            }
            // Look in scenario risks
            for ( Risk risk : scenario.getRisks() ) {
                if ( risk.getOrganization() == organization ) return true;
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
        // Look in plan events
        for ( Event event : list( Event.class ) ) {
            if ( event.getScope() == place ) return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReferenced( Event event ) {
        for ( Event incident : Channels.getPlan().getIncidents() ) {
            if ( incident == event ) return true;
        }
        // look in scenarios
        for ( Scenario scenario : list( Scenario.class ) ) {
            if ( scenario.getEvent() == event ) return true;
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.getInitiatedEvent() == event ) return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int getReferenceCount( Event event ) {
        int count = 0;
        for ( Event incident : Channels.getPlan().getIncidents() ) {
            if ( incident == event ) count++;
        }
        // look in scenarios
        for ( Scenario scenario : list( Scenario.class ) ) {
            if ( scenario.getEvent() == event ) count++;
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.getInitiatedEvent() == event ) count++;
            }
        }
        return count;
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
        return getChannels().getAnalyst().findAllIssuesFor(
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
                if ( externalFlow.getConnector().getScenario() == toScenario
                        && !externalFlow.isPartTargeted() ) {
                    externalFlows.add( externalFlow );
                }
            }
        }
        flows = toScenario.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( !flow.isInternal() ) {
                ExternalFlow externalFlow = (ExternalFlow) flow;
                if ( externalFlow.getConnector().getScenario() == fromScenario
                        && externalFlow.isPartTargeted() ) {
                    externalFlows.add( externalFlow );
                }
            }
        }
        for ( Part part : findInitiators( toScenario ) ) {
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
                    if ( isExecutedBy( sourcePart, fromEntity )
                            && isExecutedBy( targetPart, toEntity ) ) {
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

    private boolean isExecutedBy( Part part, ModelObject entity ) {
        ResourceSpec partSpec = part.resourceSpec();
        if ( entity instanceof Actor ) {
            List<Actor> allPlayers = findAllActors( partSpec );
            if ( allPlayers.isEmpty() )
                return entity.isUnknown();
            else
                return allPlayers.contains( (Actor) entity );
        } else {
            return partSpec.hasEntity( entity );
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

    private void visitParts( Set<Part> visited, ResourceSpec spec, Scenario scenario ) {
        for ( Scenario s : getScenarios( scenario ) )
            for ( Iterator<Part> partIterator = s.parts(); partIterator.hasNext(); ) {
                Part part = partIterator.next();
                if ( spec.matches( part.resourceSpec(), true ) )
                    visited.add( part );
            }

        Actor actor = spec.getActor();
        Organization organization = spec.getOrganization();
        Role role = spec.getRole();
        Place jurisdiction = spec.getJurisdiction();

        if ( actor != null && role == null ) {
            // add parts with actor's roles
            for ( ResourceSpec job : findAllJobSpecs( organization, actor ) ) {
                ResourceSpec s = new ResourceSpec( spec );
                s.setRole( job.getRole() );
                s.setJurisdiction( job.getJurisdiction() );
                s.setOrganization( job.getOrganization() );
                visitParts( visited, s, scenario );

                s.setActor( null );
                visitParts( visited, s, scenario );
            }
        }

        if ( organization != null ) {

            if ( role == null && actor == null && jurisdiction == null ) {
                for ( Role r : findRolesIn( organization ) )
                    for ( Actor a : findActors( organization, r, scenario ) ) {
                        ResourceSpec s = new ResourceSpec( spec );
                        if ( Actor.UNKNOWN.equals( a ) )
                            s.setRole( r );
                        else
                            s.setActor( a );
                        visitParts( visited, s, scenario );
                    }
            }

            ResourceSpec s = new ResourceSpec( spec );
            s.setOrganization( organization.getParent() );
            visitParts( visited, s, scenario );

        }

        if ( jurisdiction != null ) {
            // look for parts with no specific jurisdiction
            // TODO process geo inclusions
            ResourceSpec s = new ResourceSpec( spec );
            s.setJurisdiction( null );
            visitParts( visited, s, scenario );
        }
    }

    private List<Scenario> getScenarios( Scenario scenario ) {
        List<Scenario> scenarios;
        if ( scenario == null )
            scenarios = list( Scenario.class );
        else {
            scenarios = new ArrayList<Scenario>();
            scenarios.add( scenario );
        }
        return scenarios;
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
                if ( p != null && spec.equals( p.resourceSpec() ) )
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

        return toSortedList( channels );
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
                if ( organization.equals( part.getOrganizationOrUnknown() ) ) {
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

    private static <T extends Comparable> List<T> toSortedList( Collection<T> objects ) {
        List<T> results = new ArrayList<T>( objects );
        Collections.sort( results );
        return results;
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
        return toSortedList( titles );
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
        return toSortedList( tasks );
    }

    /**
     * {@inheritDoc}
     */
    public List<String> findAllNames( Class<? extends ModelObject> aClass ) {
        Set<String> allNames = new HashSet<String>();
        for ( ModelObject mo : listReferenced( aClass ) ) {
            allNames.add( mo.getName() );
        }
        return toSortedList( allNames );
    }

    /**
     * {@inheritDoc}
     */
    public List<Role> findAllRolesOf( Actor actor ) {
        Set<Role> roles = new HashSet<Role>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( spec.getRole() != null ) {
                if ( spec.getActor() != null && actor.equals( spec.getActor() )
                        || ( actor.isUnknown() && spec.getActor() == null ) )
                    roles.add( spec.getRole() );
            }
        }
        return new ArrayList<Role>( roles );
    }

    /**
     * {@inheritDoc}
     */
    public List<Actor> findAllActorsInOrganization( Organization organization ) {
        Set<Actor> actors = new HashSet<Actor>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( spec.getActor() != null ) {
                if ( spec.getOrganization() != null && organization.equals( spec.getOrganization() )
                        || ( organization.isUnknown() && spec.getOrganization() == null ) )
                    actors.add( spec.getActor() );
            }
        }
        return new ArrayList<Actor>( actors );
    }

    /**
     * {@inheritDoc}
     */
    public List<ModelObject> findAllScenarioObjectsInvolving( ModelObject entity ) {
        Set<ModelObject> scenarioObjects = new HashSet<ModelObject>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.resourceSpec().hasEntity( entity ) ) {
                    scenarioObjects.add( part );
                    Iterator<Flow> outcomes = part.outcomes();
                    while ( outcomes.hasNext() ) scenarioObjects.add( outcomes.next() );
                    Iterator<Flow> requirements = part.requirements();
                    while ( requirements.hasNext() ) scenarioObjects.add( requirements.next() );
                }
            }
        }
        return new ArrayList<ModelObject>( scenarioObjects );
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

        if ( actors.isEmpty() )
            actors.add( Actor.UNKNOWN );

        return toSortedList( actors );
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

        List<Role> list = toSortedList( roles );
        if ( hasUnknown )
            list.add( Role.UNKNOWN );
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<Organization> findOrganizations() {
        List<Organization> orgs = toSortedList( list( Organization.class ) );

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

        return toSortedList( actors );
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
        return noActorRoleFound ? findActors( organization, role )
                : toSortedList( actors );
    }

    private List<Job> findAllJobs( Organization organization, Actor actor ) {
        List<Job> jobs = new ArrayList<Job>();
        List<Organization> orgs;
        if ( organization == null )
            orgs = list( Organization.class );
        else {
            orgs = new ArrayList<Organization>();
            orgs.add( organization );
        }

        for ( Organization org : orgs ) {
            for ( Job job : org.getJobs() ) {
                if ( actor.equals( job.getActor() ) ) {
                    jobs.add( job );
                }
            }
            for ( Job job : findUnconfirmedJobs( org ) ) {
                if ( actor.equals( job.getActor() ) ) {
                    jobs.add( job );
                }
            }
        }
        return jobs;
    }

    private List<ResourceSpec> findAllJobSpecs( Organization organization, Actor actor ) {
        List<ResourceSpec> jobs = new ArrayList<ResourceSpec>();
        List<Organization> orgs;
        if ( organization == null )
            orgs = list( Organization.class );
        else {
            orgs = new ArrayList<Organization>();
            orgs.add( organization );
        }

        for ( Organization org : orgs ) {
            for ( Job job : org.getJobs() ) {
                if ( actor.equals( job.getActor() ) ) {
                    jobs.add( job.resourceSpec( org ) );
                }
            }
            for ( Job job : findUnconfirmedJobs( org ) ) {
                if ( actor.equals( job.getActor() ) ) {
                    jobs.add( job.resourceSpec( org ) );
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
     * {@inheritDoc
     */
    public List<Organization> findEmployers( Actor actor ) {
        List<Organization> employers = new ArrayList<Organization>();
        String actorName = actor.getName();
        for ( Organization org : list( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( job.getActorName().equals( actorName ) ) {
                    if ( !employers.contains( org ) ) employers.add( org );
                }
            }
        }
        return employers;
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
    public List<Part> findAllParts( Scenario scenario, ResourceSpec resourceSpec ) {
        Set<Part> list = new HashSet<Part>();
        visitParts( list, resourceSpec, scenario );

        return toSortedList( list );
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
        if ( Channels.getPlan().isIncident( scenario.getEvent() ) ) return true;
        if ( visited.contains( scenario ) ) return false;
        visited.add( scenario );
        boolean started = false;
        Iterator<Part> initiators = findInitiators( scenario ).iterator();
        while ( !started && initiators.hasNext() ) {
            started = doFindIfPartStarted( initiators.next(), visited );
        }
        return started;
    }

    public Channels getChannels() {
        return Channels.instance();
    }

    public String getTitle( Actor actor ) {
        for ( Job job : findAllJobs( null, actor ) ) {
            String title = job.getTitle().trim();
            if ( !title.isEmpty() )
                return title;
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    public List<Event> findPlannedEvents() {
        Plan plan = Channels.getPlan();
        List<Event> plannedEvents = new ArrayList<Event>();
        for ( Event event : list( Event.class ) ) {
            if ( !plan.isIncident( event ) ) plannedEvents.add( event );
        }
        return plannedEvents;
    }

    /**
     * {@inheritDoc}
     */
    public List<Part> findMitigations( Scenario scenario, Risk risk ) {
        List<Part> mitigators = new ArrayList<Part>();
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isTerminatesEvent() || part.getMitigations().contains( risk ) ) {
                mitigators.add( part );
            }
        }
        return mitigators;
    }

    /**
     * {@inheritDoc}
     */
    public List<Scenario> findScenarios( Actor actor ) {
        List<Scenario> result = new ArrayList<Scenario>();

        ResourceSpec spec = ResourceSpec.with( actor );
        for ( Scenario s : list( Scenario.class ) ) {
            List<Part> parts = findAllParts( s, spec );
            if ( !parts.isEmpty() )
                result.add( s );
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<Actor> findActors( Scenario scenario ) {
        Set<Actor> actors = new HashSet<Actor>();
        for ( Iterator<Part> pi = scenario.parts(); pi.hasNext(); ) {
            Part p = pi.next();
            actors.addAll( findAllActors( p.resourceSpec() ) );
        }

        List<Actor> result = new ArrayList<Actor>( actors );
        Collections.sort( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<String> findAllActorLastNames() {
        return (List<String>) CollectionUtils.collect(
                list( Actor.class ),
                new Transformer() {
                    public Object transform( Object obj ) {
                        return ( (Actor) obj ).getLastName();
                    }
                } );
    }

    /**
     * {@inheritDoc}
     */
    public List<Employment> findAllEmployments() {
        Set<Actor> employed = new HashSet<Actor>();
        List<Employment> employments = new ArrayList<Employment>();
        List<Organization> orgs = new ArrayList<Organization>( list( Organization.class ) );
        orgs.add( Organization.UNKNOWN );
        for ( Organization org : orgs ) {
            for ( Job job : org.getJobs() ) {
                employments.add( new Employment( job.getActor(), org, job ) );
                employed.add( job.getActor() );
            }
            for ( Job job : findUnconfirmedJobs( org ) ) {
                employments.add( new Employment( job.getActor(), org, job ) );
                employed.add( job.getActor() );
            }
        }
        for ( Actor actor : list( Actor.class ) ) {
            if ( !employed.contains( actor ) ) {
                employments.add( new Employment( actor ) );
            }
        }
        return employments;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Employment> findAllEmploymentsForRole( final Role role ) {
        return (List<Employment>) CollectionUtils.select(
                findAllEmployments(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Role empRole = ( (Employment) obj ).getRole();
                        return empRole != null && empRole.equals( role );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Employment> findAllEmploymentsForActor( final Actor actor ) {
        return (List<Employment>) CollectionUtils.select(
                findAllEmployments(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Actor empActor = ( (Employment) obj ).getActor();
                        return empActor != null && empActor.equals( actor );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    public List<String> findAllFlowNames() {
        Set<String> names = new HashSet<String>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                names.add( flows.next().getName() );
            }
        }
        return new ArrayList<String>( names );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> findAllIssues( Analyst analyst ) {
        List<Issue> allIssues = new ArrayList<Issue>();
        for ( ModelObject mo : list( ModelObject.class ) ) {
            allIssues.addAll( analyst.listIssues( mo, true ) );
        }
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                allIssues.addAll( analyst.listIssues( parts.next(), true ) );
            }
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                allIssues.addAll( analyst.listIssues( flows.next(), true ) );
            }
        }
        return allIssues;
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> findAllUnwaivedIssues( Analyst analyst ) {
        List<Issue> allUnwaivedIssues = new ArrayList<Issue>();
        for ( ModelObject mo : list( ModelObject.class ) ) {
            allUnwaivedIssues.addAll( analyst.listUnwaivedIssues( mo, true ) );
        }
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                allUnwaivedIssues.addAll( analyst.listUnwaivedIssues( parts.next(), true ) );
            }
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                allUnwaivedIssues.addAll( analyst.listUnwaivedIssues( flows.next(), true ) );
            }
        }
        return allUnwaivedIssues;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public <T extends ModelObject> List<T> listEntitiesWithUnknown( Class<T> entityClass ) {
        List<T> allEntities = new ArrayList<T>( list( entityClass ) );
        ModelObject unknown =
                entityClass == Actor.class
                        ? Actor.UNKNOWN
                        : entityClass == Event.class
                        ? Event.UNKNOWN
                        : entityClass == Organization.class
                        ? Organization.UNKNOWN
                        : entityClass == Place.class
                        ? Place.UNKNOWN
                        : Role.UNKNOWN;
        allEntities.add( (T) unknown );
        return allEntities;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Scenario> findScenariosRespondingTo( final Event event ) {
        return (List<Scenario>) CollectionUtils.select(
                list( Scenario.class ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Event repondedEvent = ( (Scenario) obj ).getEvent();
                        return repondedEvent != null && repondedEvent.equals( event );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    public List<Part> findPartsTerminatingEventIn( Scenario scenario ) {
        List<Part> terminatingParts = new ArrayList<Part>();
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isTerminatesEvent() )
                terminatingParts.add( part );
        }
        return terminatingParts;
    }

    /**
     * {@inheritDoc}
     */
    public List<Part> findPartsStartingWithEventIn( Scenario scenario ) {
        List<Part> startedParts = new ArrayList<Part>();
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isStartsWithScenario() )
                startedParts.add( part );
        }
        return startedParts;
    }

    /**
     * {@inheritDoc}
     */
    public List<Part> findPartsInitiatingEvent( Event event ) {
        List<Part> initiatingParts = new ArrayList<Part>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                Event initiatedEvent = part.getInitiatedEvent();
                if ( initiatedEvent != null && initiatedEvent.equals( event ) )
                    initiatingParts.add( part );
            }
        }
        return initiatingParts;
    }

    /**
     * {@inheritDoc}
     */
    public List<Attachment> findAllAttachments() {
        List<Attachment> allAttachments = new ArrayList<Attachment>();
        for ( ModelObject mo : findAllModelObjects() ) {
            allAttachments.addAll( mo.getAttachments() );
        }
        return allAttachments;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> findAllAttached() {
        Set<String> allAttachedUrls = new HashSet<String>();
        for ( Attachment attachment : findAllAttachments() ) {
            allAttachedUrls.add( attachment.getUrl() );
        }
        return new ArrayList<String>( allAttachedUrls );
    }

    /**
     * {@inheritDoc}
     */
    public List<ModelObject> findAllModelObjects() {
        List<ModelObject> allModelObjects = new ArrayList<ModelObject>();
        allModelObjects.addAll( list( ModelObject.class ) );
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                allModelObjects.add( parts.next() );
            }
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                allModelObjects.add( flows.next() );
            }
        }
        return allModelObjects;
    }


}

