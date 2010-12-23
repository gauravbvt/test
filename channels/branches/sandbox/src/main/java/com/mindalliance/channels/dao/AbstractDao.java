package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.InvalidEntityKindException;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An in-memory, no-transactions implementation of a store.
 */
abstract class AbstractDao implements Dao {

    /**
     * ModelObjects indexed by id.
     */
    private final Map<Long, ModelObject> indexMap = Collections.synchronizedMap(
                                                        new HashMap<Long, ModelObject>() );

    //=======================================
    protected AbstractDao() {
    }

    /**
     * @return the id generator
     */
    public abstract IdGenerator getIdGenerator();

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> List<T> list( final Class<T> clazz ) {
        synchronized ( indexMap ) {
            return (List<T>) CollectionUtils.select( indexMap.values(), new Predicate() {
                public boolean evaluate( Object object ) {
                    return clazz.isAssignableFrom( object.getClass() );
                }
            } );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void add( ModelObject object ) {
        add( object, null );
    }

    /**
     * {@inheritDoc}
     */
    public void add( ModelObject object, Long id ) {
        synchronized ( indexMap ) {
            if ( id != null && indexMap.containsKey( id ) )
                throw new DuplicateKeyException();

            assignId( object, id, getIdGenerator() );
            indexMap.put( object.getId(), object );
            if ( object instanceof Segment )
                getPlan().addSegment( (Segment) object );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void update( ModelObject object ) {
    }

    /**
     * {@inheritDoc}
     */
    public Part createPart( Segment segment, Long id ) {
        return segment.addNode( assignId( new Part(), id, getIdGenerator() ) );
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector( Segment segment, Long id ) {
        return segment.addNode( assignId( new Connector(), id, getIdGenerator() ) );
    }

    /**
     * {@inheritDoc}
     */
    public ExternalFlow createExternalFlow( Node source, Node target, String name, Long id ) {
        return assignId( new ExternalFlow( source, target, name ), id, getIdGenerator() );
    }

    /**
     * {@inheritDoc}
     */
    public InternalFlow createInternalFlow( Node source, Node target, String name, Long id ) {
        return assignId( new InternalFlow( source, target, name ), id, getIdGenerator() );
    }

    private <T extends ModelObject> T assignId( T object, Long id, IdGenerator generator ) {
        object.setId( generator.assignId( id, getPlan() ) );
        return object;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        return (T) find( id );
    }

    /**
     * {@inheritDoc}
     */
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

    private ModelObject find( long id ) throws NotFoundException {
        if ( getPlan().getId() == id ) return getPlan();

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

        if ( result == null ) throw new NotFoundException();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T find( Class<T> clazz, String name ) {
        for ( T object : list( clazz ) )
            if ( name.equals( object.getName() ) )
                return object;

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T findOrCreate( Class<T> clazz, String name, Long id ) {
        T result = null;

        if ( name != null && !name.isEmpty() ) {

            result = find( clazz, name );
            if ( result == null && id != null )
                try {
                    result = find( clazz, id );
                } catch ( NotFoundException ignored ) {
                    // fall through and create new
                }

            if ( result == null )
                try {
                    // Create new entity with name
                    result = clazz.newInstance();
                    result.setName( name );
                    add( result, id );

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
    public Segment findSegment( String name ) throws NotFoundException {
        for ( Segment s : list( Segment.class ) )
            if ( name.equals( s.getName() ) )
                return s;

        throw new NotFoundException();
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name, Long id ) {
        T entityType = ModelEntity.getUniversalType( name, clazz );
        if ( entityType == null ) {
            entityType = findOrCreate( clazz, name, id );
            if ( entityType.isActual() )
                throw new InvalidEntityKindException(
                        clazz.getSimpleName() + ' ' + name + " is actual" );
            entityType.setType();
        }
        return entityType;
    }

    /**
     * {@inheritDoc}
     */
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

    public void disconnect( ExternalFlow externalFlow ) {
        Part p = externalFlow.getPart();
        p.removeSend( externalFlow );
        p.removeReceive( externalFlow );
        externalFlow.setPart( null );
        Connector c = externalFlow.getConnector();
        c.removeExternalFlow( externalFlow );
        externalFlow.setConnector( null );
    }

    /**
     * Remove any connections to the outside world
     * (essentially, anything connected to an input or output connector).
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

    /**
     * Remove a node from this segment.
     * Quietly succeeds if node is not part of the segment
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

    /**
     * Create a new send for a node.
     *
     * @param node the node
     * @return an internal flow to a new connector
     */
    public Flow createSend( Node node ) {
        return connect( node,
                createConnector( node.getSegment(), null ), Node.DEFAULT_FLOW_NAME, null );
    }

    /**
     * Create and add a new receive.
     *
     * @param node the node
     * @return a flow from a new connector to this node
     */
    public Flow createReceive( Node node ) {
        return connect(
                createConnector( node.getSegment(), null ), node, Node.DEFAULT_FLOW_NAME, null );
    }

}
