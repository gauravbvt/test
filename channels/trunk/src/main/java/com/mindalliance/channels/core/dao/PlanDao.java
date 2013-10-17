package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.ModelObjectContext;
import com.mindalliance.channels.core.dao.PlanDefinition.Version;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.InternalFlow;
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
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.db.services.users.UserRecordService;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Wrapper for per-plan dao-related operations.
 */
public class PlanDao extends AbstractModelObjectDao {


    /**
     * Name of the default event.
     */
    private static final String DEFAULT_EVENT_NAME = "unnamed";

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanDao.class );


    /**
     * The wrapped plan.
     */
    private Plan plan;

    /**
     * The user service.
     */
    private UserRecordService userRecordService;

    /**
     * The plan version.
     */
    private final Version version;

    PlanDao( Version version ) {
        this.version = version;
    }

    @Override
    protected <T extends ModelObject> void setContextKindOf( T object ) {
        object.setInModel();
    }

    @Override
    public ModelObjectContext getModelObjectContext() {
        return getPlan();
    }

    @Override
    public void add( ModelObject object, Long id ) {
        synchronized ( getIndexMap() ) {
            doAdd( object, id );
        }
    }


    @Override
    protected void addSpecific( ModelObject object, Long id ) {
        if ( object instanceof Segment )
            getPlan().addSegment( (Segment) object );
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
        return segment.addNode( assignId( new Connector(), id, getIdGenerator() ) );
    }

    public ExternalFlow createExternalFlow( Node source, Node target, String name, Long id ) {
        return assignId( new ExternalFlow( source, target, name ), id, getIdGenerator() );
    }

    public InternalFlow createInternalFlow( Node source, Node target, String name, Long id ) {
        return assignId( new InternalFlow( source, target, name ), id, getIdGenerator() );
    }

    public Part createPart( Segment segment, Long id ) {
        return segment.addNode( assignId( new Part(), id, getIdGenerator() ) );
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
        // Make sure a segment responds to an event.
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

    @Override
    public synchronized void defineImmutableEntities() {
        // WARNING: Don't change the order and only add at the end!
        getIdGenerator().setImmutableMode();
        if ( Actor.UNKNOWN == null ) {  // only create unknowns once
            Actor.UNKNOWN = findOrCreateActual( Actor.class, Actor.UnknownName, null );
            Actor.UNKNOWN.makeImmutable();
            Event.UNKNOWN = findOrCreateType( Event.class, Event.UnknownName, null );
            Event.UNKNOWN.makeImmutable();
            Organization.UNKNOWN = findOrCreateActual( Organization.class, Organization.UnknownName, null );
            Organization.UNKNOWN.setActual();
            Organization.UNKNOWN.makeImmutable();
            Phase.UNKNOWN = findOrCreateActual( Phase.class, Phase.UnknownName, null );
            Phase.UNKNOWN.makeImmutable();
            // Unknown place
            Place.UNKNOWN = findOrCreateActual( Place.class, Place.UnknownPlaceName, null );
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
            Role.UNKNOWN = findOrCreateType( Role.class, Role.UnknownName, null );  // was findOrCreate
            Role.UNKNOWN.makeImmutable();
            TransmissionMedium.UNKNOWN = findOrCreateType( TransmissionMedium.class, TransmissionMedium.UnknownName, null );
            TransmissionMedium.UNKNOWN.makeImmutable();
            InfoProduct.UNKNOWN = findOrCreateType( InfoProduct.class, InfoProduct.UnknownName, null );
            InfoProduct.UNKNOWN.makeImmutable();
            InfoFormat.UNKNOWN = findOrCreateType( InfoFormat.class, InfoFormat.UnknownName, null );
            InfoFormat.UNKNOWN.makeImmutable();
            Function.UNKNOWN = findOrCreateType( Function.class, Function.UnknownName, null );
            Function.UNKNOWN.makeImmutable();
            if ( Requirement.UNKNOWN == null ) {  // todo - COMMUNITY - remove
                Requirement.UNKNOWN = findOrCreateModelObject( Requirement.class, Requirement.UnknownName, null );
            }
        }
        getIdGenerator().setMutableMode();
    }

    /**
     * Define all immutable entities (not plan dependent).
     *
     * @param media predefined media
     */
    public synchronized void defineImmutableMedia( List<TransmissionMedium> media ) {
        getIdGenerator().setImmutableMode();
        for ( TransmissionMedium medium : media ) {
            add( medium, medium.getId() );
            medium.makeImmutable();
        }
        getIdGenerator().setMutableMode();
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

    public Set<Attachable> findAllAttachables() {
        Set<Attachable> attachables = super.findAllAttachables();
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
        return attachables;
    }

    @SuppressWarnings({"unchecked"})
    // Listing by class. Local model object only
    public <T extends ModelObject> List<T> listLocal( final Class<T> clazz ) {
        List<T> results = new ArrayList<T>();
        synchronized ( getIndexMap() ) {
            if ( Segment.class.isAssignableFrom( clazz ) ) {
                results.addAll( (Set<T>) ( getPlan().getSegments() ) );
            } else if ( SegmentObject.class.isAssignableFrom( clazz ) ) {
                results.addAll( listSegmentObjects( clazz ) );
            } else if ( Plan.class.isAssignableFrom( clazz ) ) {
                results.add( (T)getPlan() );
            }else {
                for ( Object mo : getIndexMap().values() ) {
                    if ( clazz.isAssignableFrom( mo.getClass() ) ) {
                        results.add( (T) mo );
                    }
                }
            }
        }
        return results;
    }

    @SuppressWarnings({"unchecked"})
    private <T extends ModelObject> List<? extends T> listSegmentObjects( Class<T> clazz ) {
        List<T> results = new ArrayList<T>();
        for ( Segment segment : getPlan().getSegments() ) {
            if ( clazz.isAssignableFrom( Part.class ) ) {
                results.addAll( IteratorUtils.toList( segment.parts() ) );
            } else if ( clazz.isAssignableFrom( Flow.class ) ) {
                results.addAll( IteratorUtils.toList( segment.flows() ) );
            }
        }
        return results;
    }

    @Override
    protected ModelObject find( long id ) throws NotFoundException {
        if ( getPlan().getId() == id )
            return getPlan();

        ModelObject result = lookUp( id, getIdGenerator() );
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

    public Segment findSegment( String name ) throws NotFoundException {
        for ( Segment s : list( Segment.class ) )
            if ( name.equals( s.getName() ) )
                return s;

        throw new NotFoundException();
    }


    public UserRecordService getUserDetailsService() {
        return userRecordService;
    }

    @Override
    public boolean isLoaded() {
        return plan != null;
    }

    @Override
    @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
    protected boolean isReferenced( ModelObject mo, Set<? extends ModelObject> referencingObjects ) {
        return plan.references( mo )
                || super.isReferenced( mo, referencingObjects );
    }

    @Override
    public synchronized ModelObjectContext load( Importer importer ) throws IOException {
        return doLoad( importer );
    }


    @Override
    protected void afterLoad() {
        add( plan, plan.getId() );
    }

    @Override
    public synchronized void save( Exporter exporter ) throws IOException {
        doSave( exporter );
    }

    @Override
    protected long getRecordedLastAssignedId() throws IOException {
        return version.getLastId();
    }

    @Override
    protected File getDataFile() throws IOException {
        return version.getDataFile();
    }

    @Override
    protected File getJournalFile() throws IOException {
        return version.getJournalFile();
    }

    @Override
    protected boolean isJournaled() {
        return version.isDevelopment();
    }

    @Override
    public void remove( ModelObject object ) {
        if ( object instanceof Segment )
            removeSegment( (Segment) object );
        else
            super.remove( object );
    }

    @Override
    protected void importModelObjectContext( Importer importer, FileInputStream in ) throws IOException {
        importer.importPlan( in );
    }

    private void removeSegment( Segment segment ) {
        if ( list( Segment.class ).size() > 1 ) {
            super.remove( segment );
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
        plan = version.createPlan( getIdGenerator() );
    }

    @Override
    protected void beforeSnapshot() throws IOException {
        version.setLastId( getIdGenerator().getIdCounter( plan.getUri() ) );
    }

    @Override
    protected void afterSnapshot() throws IOException {
        version.getJournalFile().delete();
    }

    @Override
    protected void beforeSaveJournal() throws IOException {
        version.setLastId( getIdGenerator().getIdCounter( getModelObjectContext().getUri() ) );
    }

    @Override
    protected void afterSaveJournal() throws IOException {
        // do nothing
    }

    public void setUserDetailsService( UserRecordService userDao ) {
        this.userRecordService = userDao;
    }


    /**
     * Validate the underlying plan.
     */
    public void validate() {
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


    public Plan getPlan() {
        if ( plan == null )
            throw new IllegalStateException( "Plan not loaded" );
        return plan;
    }

    @SuppressWarnings({"unchecked"})
    protected <T extends ModelObject> List<T> findAllLocalModelObjects( Class<T> clazz ) {
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
            domain = super.findAllLocalModelObjects( clazz );
        return domain;
    }
}
