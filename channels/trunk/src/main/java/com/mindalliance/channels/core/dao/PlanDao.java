package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.dao.PlanDefinition.Version;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.InternalFlow;
import com.mindalliance.channels.core.model.InvalidEntityKindException;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.UserIssue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.IteratorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper for per-plan dao-related operations.
 */
public class PlanDao {

    /**
     * Lowest id for mutable model objects.
     */
    public static final long IMMUTABLE_RANGE = -1000L;

    /**
     * Name of the default event.
     */
    private static final String DEFAULT_EVENT_NAME = "unnamed";

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanDao.class );

    private IdGenerator idGenerator;

    /**
     * ModelObjects indexed by id.
     */
    private final Map<Long, ModelObject> indexMap = Collections.synchronizedMap( new HashMap<Long, ModelObject>() );

    /**
     * Pending commands attached to wrapped plan.
     */
    private Journal journal = new Journal();

    /**
     * The wrapped plan.
     */
    private Plan plan;

    /**
     * The user service.
     */
    private ChannelsUserDao userDao;

    /**
     * The plan version.
     */
    private final Version version;

    PlanDao( Version version ) {
        this.version = version;
    }

    public void add( ModelObject object ) {
        add( object, null );
    }

    public void add( ModelObject object, Long id ) {
        synchronized ( indexMap ) {
            if ( id != null && indexMap.containsKey( id ) )
                throw new DuplicateKeyException();

            assignId( object, id, idGenerator );
            indexMap.put( object.getId(), object );
            if ( object instanceof Segment )
                getPlan().addSegment( (Segment) object );
        }
    }

    private <T extends ModelObject> T assignId( T object, Long id, IdGenerator generator ) {
        object.setId( generator.assignId( id, getPlan() ) );
        return object;
    }

    public Flow connect( Node source, Node target, String name, Long id ) {
        Flow result;

        if ( Flow.isInternal( source, target ) ) {
            result = createInternalFlow( source, target, name, id );
            source.addSend( result );
            target.addReceive( result );
        } else if ( Flow.isExternal( source, target ) ) {
            result = createExternalFlow( source, target, name, id );
            if ( source.isConnector() ) {
                target.addReceive( result );
                ( (Connector) source ).addExternalFlow( (ExternalFlow) result );
            } else {
                source.addSend( result );
                ( (Connector) target ).addExternalFlow( (ExternalFlow) result );
            }
        } else
            throw new IllegalArgumentException();

        return result;
    }

    public Connector createConnector( Segment segment, Long id ) {
        return segment.addNode( assignId( new Connector(), id, idGenerator ) );
    }

    public ExternalFlow createExternalFlow( Node source, Node target, String name, Long id ) {
        return assignId( new ExternalFlow( source, target, name ), id, idGenerator );
    }

    public InternalFlow createInternalFlow( Node source, Node target, String name, Long id ) {
        return assignId( new InternalFlow( source, target, name ), id, idGenerator );
    }

    public Part createPart( Segment segment, Long id ) {
        return segment.addNode( assignId( new Part(), id, idGenerator ) );
    }

    public Requirement createRequirement() {
        return createRequirement( null );
    }

    public Requirement createRequirement( Long id ) {
        Requirement requirement = new Requirement();
        assignId( requirement, id, idGenerator );
        add( requirement, id );
        return requirement;
    }

    /**
     * Create and add a new receive.
     *
     * @param node the node
     * @return a flow from a new connector to this node
     */
    public Flow createReceive( Node node ) {
        return connect( createConnector( node.getSegment(), null ), node, Node.DEFAULT_FLOW_NAME, null );
    }

    public Segment createSegment( Long id, Long defaultPartId ) {
        Segment newSegment = new Segment();
        add( newSegment, id );

        newSegment.setName( Segment.DEFAULT_NAME );
        newSegment.setDescription( Segment.DEFAULT_DESCRIPTION );
        // Make sure a plan segment responds to an event.
        newSegment.setEvent( plan.getDefaultEvent() );
        newSegment.setPhase( plan.getDefaultPhase() );
        createPart( newSegment, defaultPartId );
        return newSegment;
    }

    /**
     * Create a new send for a node.
     *
     * @param node the node
     * @return an internal flow to a new connector
     */
    public Flow createSend( Node node ) {
        return connect( node, createConnector( node.getSegment(), null ), Node.DEFAULT_FLOW_NAME, null );
    }

    /**
     * Define all immutable entities (not plan dependent).
     *
     * @param media predefined media
     */
    public void defineImmutableEntities( List<TransmissionMedium> media ) {
        // TODO cleanup UNKNOWN mess
        long id = idGenerator.getLastAssignedId( plan );
        idGenerator.setLastAssignedId( IMMUTABLE_RANGE, plan );
        Actor.UNKNOWN = findOrCreate( Actor.class, Actor.UnknownName, null );
        Actor.UNKNOWN.makeImmutable();
        Event.UNKNOWN = findOrCreateType( Event.class, Event.UnknownName, null );
        Event.UNKNOWN.makeImmutable();
        Organization.UNKNOWN = findOrCreate( Organization.class, Organization.UnknownName, null );
        Organization.UNKNOWN.setActual();
        Organization.UNKNOWN.makeImmutable();
        Phase.UNKNOWN = findOrCreate( Phase.class, Phase.UnknownName, null );
        Phase.UNKNOWN.makeImmutable();
        // Unknown place
        Place.UNKNOWN = findOrCreate( Place.class, Place.UnknownPlaceName, null );
        Place.UNKNOWN.makeImmutable();
        // Administrative area types
        Place administrativeArea = findOrCreateType( Place.class, Place.ADMINISTRATIVE_AREA, null );
        administrativeArea.makeImmutable();
        Place.Country = findOrCreateType( Place.class, Place.COUNTRY, null );
        Place.Country.addType( administrativeArea );
        Place.Country.makeImmutable();
        Place.State = findOrCreateType( Place.class, Place.STATE, null );
        Place.State.addType( administrativeArea );
        Place.State.setWithin( Place.Country );
        Place.State.makeImmutable();
        Place.County = findOrCreateType( Place.class, Place.COUNTY, null );
        Place.County.addType( administrativeArea );
        Place.County.setWithin( Place.State );
        Place.County.makeImmutable();
        Place.City = findOrCreateType( Place.class, Place.CITY, null );
        Place.City.addType( administrativeArea );
        Place.City.setWithin( Place.County );
        Place.City.makeImmutable();
        Role.UNKNOWN = findOrCreate( Role.class, Role.UnknownName, null );
        Role.UNKNOWN.makeImmutable();
        TransmissionMedium.UNKNOWN = findOrCreateType( TransmissionMedium.class, TransmissionMedium.UnknownName, null );
        TransmissionMedium.UNKNOWN.makeImmutable();
        InfoProduct.UNKNOWN = findOrCreateType( InfoProduct.class, InfoProduct.UnknownName, null );
        InfoProduct.UNKNOWN.makeImmutable();
        InfoFormat.UNKNOWN = findOrCreateType( InfoFormat.class, InfoFormat.UnknownName, null );
        InfoFormat.UNKNOWN.makeImmutable();
        Requirement.UNKNOWN = findOrCreate( Requirement.class, Requirement.UnknownName, null );
        for ( TransmissionMedium medium : media ) {
            add( medium );
            medium.makeImmutable();
        }
        idGenerator.setLastAssignedId( id, plan );
    }

    public void disconnect( Connector connector ) {
        for ( ExternalFlow flow : new HashSet<ExternalFlow>( connector.getExternalFlows() ) )
            disconnect( flow );
    }

    public void disconnect( Flow flow ) {
        if ( flow.isExternal() )
            disconnect( (ExternalFlow) flow );
        else
            disconnect( (InternalFlow) flow );
    }

    public void disconnect( InternalFlow internalFlow ) {
        Node s = internalFlow.getSource();
        s.removeSend( internalFlow );
        if ( s.isConnector() )
            removeNode( s, s.getSegment() );
        internalFlow.setSource( null );
        Node t = internalFlow.getTarget();
        t.removeReceive( internalFlow );
        if ( t.isConnector() )
            removeNode( t, t.getSegment() );

        internalFlow.setTarget( null );
    }

    public static void disconnect( ExternalFlow externalFlow ) {
        Part p = externalFlow.getPart();
        p.removeSend( externalFlow );
        p.removeReceive( externalFlow );
        externalFlow.setPart( null );
        Connector c = externalFlow.getConnector();
        c.removeExternalFlow( externalFlow );
        externalFlow.setConnector( null );
    }

    /**
     * Remove any connections to the outside world (essentially, anything connected to an input or output connector).
     *
     * @param segment the segment to disconnect
     */
    public void disconnect( Segment segment ) {
        for ( Iterator<Node> it = segment.nodes(); it.hasNext(); ) {
            Node n = it.next();
            if ( n.isConnector() )
                disconnect( (Connector) n );
        }
    }

    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        return (T) find( id );
    }

    public <T extends ModelObject> T find( Class<T> clazz, String name ) {
        for ( T object : list( clazz ) )
            if ( name.equals( object.getName() ) )
                return object;

        return null;
    }

    /**
     * Find urls of all attachments.
     *
     * @return a list of strings
     */
    public List<String> findAllAttached() {

        Set<Attachable> attachables = new HashSet<Attachable>( list( ModelObject.class ) );
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() )
                attachables.add( parts.next() );

            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() )
                attachables.add( flows.next() );
        }
/*
        for ( Organization org : list( Organization.class ) )
            for ( Agreement agreement : org.getAgreements() )
                attachables.add( agreement );
*/

        Set<String> allAttachedUrls = new HashSet<String>();
        for ( Attachable attachable : attachables )
            for ( Attachment attachment : attachable.getAttachments() )
                allAttachedUrls.add( attachment.getUrl() );

        return new ArrayList<String>( allAttachedUrls );
    }

    public List<UserIssue> findAllUserIssues( ModelObject modelObject ) {
        List<UserIssue> foundIssues = new ArrayList<UserIssue>();
        for ( UserIssue userIssue : list( UserIssue.class ) ) {
            if ( userIssue.getAbout().getId() == modelObject.getId() )
                foundIssues.add( userIssue );
        }
        return foundIssues;
    }

    public <T extends ModelObject> T findOrCreate( Class<T> clazz, String name, Long id ) {
        T result = null;

        if ( name != null && !name.isEmpty() ) {

            result = find( clazz, name );
            boolean newId = false;
            if ( result == null && id != null )
                try {
                    ModelObject modelObject = find( id );
                    if ( modelObject.getClass().isAssignableFrom( clazz ) )
                        result = (T) modelObject;
                    else
                        newId = true;
                } catch ( NotFoundException ignored ) {
                    // fall through and create new
                }

            if ( result == null )
                try {
                    // Create new entity with name
                    result = clazz.getConstructor().newInstance();
                    result.setName( name );
                    add( result, newId ? null : id );
                } catch ( InstantiationException e ) {
                    throw new RuntimeException( e );
                } catch ( IllegalAccessException e ) {
                    throw new RuntimeException( e );
                } catch ( NoSuchMethodException e ) {
                    throw new RuntimeException( e );
                } catch ( InvocationTargetException e ) {
                    throw new RuntimeException( e );
                }
        }

        return result;
    }

    private ModelObject find( long id ) throws NotFoundException {
        if ( getPlan().getId() == id )
            return getPlan();

        ModelObject result = indexMap.get( id );
        if ( result == null ) {
            Iterator<Segment> iterator = list( Segment.class ).iterator();
            while ( result == null && iterator.hasNext() ) {
                Segment segment = iterator.next();
                result = segment.getNode( id );
                if ( result == null ) {
                    Iterator<Flow> flows = segment.flows();
                    while ( result == null && flows.hasNext() ) {
                        Flow flow = flows.next();
                        if ( flow.getId() == id )
                            result = flow;
                    }
                }
            }
        }

        if ( result == null )
            throw new NotFoundException();
        return result;
    }
    //---------------- findOrCreate

    public <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name, Long id ) {
        T entityType = ModelEntity.getUniversalType( name, clazz );
        if ( entityType == null ) {
            entityType = findOrCreate( clazz, name, id );
            if ( entityType.isActual() )
                throw new InvalidEntityKindException( clazz.getSimpleName() + ' ' + name + " is actual" );
            entityType.setType();
        }
        return entityType;
    }

    public Segment findSegment( String name ) throws NotFoundException {
        for ( Segment s : list( Segment.class ) )
            if ( name.equals( s.getName() ) )
                return s;

        throw new NotFoundException();
    }

    public ChannelsUserDao getUserDetailsService() {
        return userDao;
    }

    public synchronized boolean isLoaded() {
        return plan != null;
    }

    @SuppressWarnings( {"unchecked"} )
    public Iterator<ModelEntity> iterateEntities() {
        Set<? extends ModelObject> referencers = getReferencingObjects();
        Class<?>[] classes = {
                TransmissionMedium.class, Actor.class, Role.class, Place.class, Organization.class, Event.class,
                Phase.class
        };

        Iterator<? extends ModelEntity>[] iterators = new Iterator[classes.length];
        for ( int i = 0; i < classes.length; i++ ) {
            Class<? extends ModelEntity> clazz = (Class<? extends ModelEntity>) classes[i];
            iterators[i] = listReferencedEntities( clazz, referencers ).iterator();
        }
        return (Iterator<ModelEntity>) new IteratorChain( iterators );
    }

    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelObject> List<T> list( final Class<T> clazz ) {
        synchronized ( indexMap ) {
            return (List<T>) CollectionUtils.select( indexMap.values(), new Predicate() {
                @Override
                public boolean evaluate( Object object ) {
                    return clazz.isAssignableFrom( object.getClass() );
                }
            } );
        }
    }

    private <T extends ModelEntity> List<T> listReferencedEntities(
            Class<T> clazz, Set<? extends ModelObject> referencers ) {

        Collection<T> inputCollection = list( clazz );
        List<T> answer = new ArrayList<T>( inputCollection.size() );

        for ( T item : inputCollection )
            if ( item.isImmutable() && !item.isUnknown() || isReferenced( item, referencers ) )
                answer.add( item );

        return answer;
    }

    @SuppressWarnings( {"unchecked", "RawUseOfParameterizedType"} )
    private boolean isReferenced( ModelObject mo, Set<? extends ModelObject> referencingObjects ) {
        if ( plan.references( mo ) )
            return true;

        for ( ModelObject object : referencingObjects )
            if ( object.references( mo ) )
                return true;

        return false;
    }

    /**
     * Load persisted plan.
     *
     * @param importer what to use for importing
     * @return the loaded plan
     * @throws IOException on error
     */
    public synchronized Plan load( Importer importer ) throws IOException {
        FileInputStream in = null;
        try {
            idGenerator.setLastAssignedId( version.getLastId(), plan );
            File dataFile = version.getDataFile();
            if ( dataFile.exists() ) {
                LOG.info( "Importing snapshot for plan {} from {}", plan, dataFile.getAbsolutePath() );
                in = new FileInputStream( dataFile );
                importer.importPlan( in );

                journal = loadJournal( importer );
            }
            add( plan, plan.getId() );
            validate();
        } finally {
            if ( in != null )
                in.close();
        }

        return plan;
    }

    private Journal loadJournal( Importer importer ) throws IOException {
        FileInputStream inputStream = null;
        if ( version.isDevelopment() && importer != null )
            try {
                File journalFile = version.getJournalFile();
                if ( journalFile.length() > 0L ) {
                    inputStream = new FileInputStream( journalFile );
                    return importer.importJournal( inputStream );
                }
            } finally {
                if ( inputStream != null )
                    inputStream.close();
            }

        return new Journal();
    }

    public void remove( ModelObject object ) {
        if ( object instanceof Segment )
            removeSegment( (Segment) object );
        else
            indexMap.remove( object.getId() );
    }

    private void removeSegment( Segment segment ) {
        if ( list( Segment.class ).size() > 1 ) {
            indexMap.remove( segment.getId() );
            disconnect( segment );
        }
    }

    /**
     * Remove a node from this segment. Quietly succeeds if node is not part of the segment
     *
     * @param node    the node to remove.
     * @param segment the segment
     */
    public void removeNode( Node node, Segment segment ) {
        if ( segment.getNode( node.getId() ) != null && ( node.isConnector() || segment.hasMoreThanOnePart() ) ) {
            Iterator<Flow> ins = node.receives();
            while ( ins.hasNext() )
                disconnect( ins.next() );

            Iterator<Flow> outs = node.sends();
            while ( outs.hasNext() )
                disconnect( outs.next() );

            if ( node.isConnector() ) {
                List<ExternalFlow> toDisconnect = new ArrayList<ExternalFlow>();
                Iterator<ExternalFlow> xf = ( (Connector) node ).externalFlows();
                while ( xf.hasNext() )
                    toDisconnect.add( xf.next() );

                // Avoid ConcurrentModificationException
                for ( ExternalFlow flow : toDisconnect )
                    disconnect( flow );
            }

            remove( node );
            segment.removeNode( node );
        }
    }

    void resetPlan() {
        plan = version.createPlan( idGenerator );
    }

    /**
     * Save the plan to the file repository.
     *
     * @param exporter where to export
     * @throws IOException on errors
     */
    public synchronized void save( Exporter exporter ) throws IOException {
        if ( isLoaded() ) {
            version.setLastId( idGenerator.getLastAssignedId( plan ) );
            takeSnapshot( exporter );
            version.getJournalFile().delete();
            journal.reset();
        }
    }

    private void takeSnapshot( Exporter exporter ) throws IOException {
        LOG.info( "Taking snapshot of plan {}", plan.getUri() );

        // Make backup
        File dataFile = version.getDataFile();
        if ( dataFile.length() > 0L ) {
            String backupPath = dataFile.getAbsolutePath() + '_' + System.currentTimeMillis();
            File backup = new File( backupPath );
            dataFile.renameTo( backup );
        }

        // snap
        FileOutputStream out = null;
        try {
            out = new FileOutputStream( version.getDataFile() );
            exporter.export( out );
        } finally {
            if ( out != null )
                out.close();
        }
    }

    /**
     * Save outstanding journal entries.
     *
     * @param exporter the persistence mechanism
     * @throws IOException on errors
     */
    void saveJournal( Exporter exporter ) throws IOException {
        version.getJournalFile().delete();
        FileOutputStream out = null;
        try {
            version.setLastId( idGenerator.getLastAssignedId( plan ) );
            out = new FileOutputStream( version.getJournalFile() );
            exporter.export( getJournal(), out );
        } finally {
            if ( out != null )
                out.close();
        }
    }

    public void setUserDetailsService( ChannelsUserDao userDao ) {
        this.userDao = userDao;
    }

    public void update( ModelObject object ) {
    }

    /**
     * Validate the underlying plan.
     */
    void validate() {
        // Make sure there is at least one event per plan
        List<Event> incidents = plan.getIncidents();
        if ( incidents.isEmpty() ) {
            Event unnamedEvent = findOrCreate( Event.class, DEFAULT_EVENT_NAME, null );
            unnamedEvent.setType();
            plan.addIncident( unnamedEvent );
        } else if ( incidents.size() > 1 ) {
            // Remove default event if not referenced
            Event event = find( Event.class, DEFAULT_EVENT_NAME );
            if ( event != null && getReferenceCount( event ) <= 1 ) {
                incidents.remove( event );
                remove( event );
            }
        }

        // Make sure there is at least one phase.
        if ( plan.getPhases().isEmpty() )
            addDefaultPhase();

        // Make sure there is at least one segment per plan
        if ( !list( Segment.class ).iterator().hasNext() )
            plan.addSegment( createSegment( null, null ) );
    }

    /**
     * Get reference count for event.
     *
     * @param event an event
     * @return an int
     */
    private int getReferenceCount( Event event ) {
        int count = 0;
        for ( Event incident : plan.getIncidents() ) {
            if ( incident.equals( event ) )
                count++;
        }
        // look in plan segments
        for ( Segment segment : list( Segment.class ) ) {
            if ( event.equals( segment.getEvent() ) )
                count++;
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.initiatesEvent() && event.equals( part.getInitiatedEvent() ) )
                    count++;
            }
        }
        return count;
    }

    /**
     * Add default phase to plan.
     */
    private void addDefaultPhase() {
        Phase phase = findOrCreate( Phase.class, Plan.DEFAULT_PHASE_NAME, null );
        phase.setActual();
        phase.setTiming( Plan.DEFAULT_PHASE_TIMING );

        plan.addPhase( phase );
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator( IdGenerator idGenerator ) {
        this.idGenerator = idGenerator;
    }

    public synchronized Journal getJournal() {
        return journal;
    }

    public synchronized Plan getPlan() {
        if ( plan == null )
            throw new IllegalStateException( "Plan not loaded" );
        return plan;
    }

    @SuppressWarnings( {"unchecked"} )
    private Set<? extends ModelObject> getReferencingObjects() {
        Set<? extends ModelObject> referencingObjects = new HashSet<ModelObject>();
        for ( Class refClass : ModelObject.referencingClasses() )
            referencingObjects.addAll( findAllModelObjects( refClass ) );
        return referencingObjects;
    }

    @SuppressWarnings( {"unchecked"} )
    private <T extends ModelObject> List<T> findAllModelObjects( Class<T> clazz ) {
        List<T> domain;
        boolean isPart = Part.class.isAssignableFrom( clazz );
        boolean isFlow = Flow.class.isAssignableFrom( clazz );

        if ( isPart || isFlow ) {
            domain = new ArrayList<T>();
            for ( Segment segment : list( Segment.class ) ) {
                Iterator<T> items = (Iterator<T>) ( isPart ? segment.parts() : segment.flows() );
                while ( items.hasNext() )
                    domain.add( items.next() );
            }
        } else
            domain = list( clazz );

        return domain;
    }
}
