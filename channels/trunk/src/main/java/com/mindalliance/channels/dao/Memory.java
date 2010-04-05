package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.InvalidEntityKindException;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An in-memory, no-transactions implementation of a store.
 */
public abstract class Memory implements Dao {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( Memory.class );

    /**
     * ModelObjects indexed by id.
     */
    private final Map<Long, ModelObject> indexMap = Collections.synchronizedMap(
            new HashMap<Long, ModelObject>() );

    //=======================================
    /**
     * ModelObjects, indexed by id.
     */
    protected Memory() {
    }

    /**
     * The plan for the data managed by this dao.
     *
     * @return he current plan
     */
    public abstract Plan getPlan();

    /**
     * @return the id generator
     */
    public abstract IdGenerator getIdGenerator();

    /**
     * {@inheritDoc}
     */
    public void flush() {
    }

    /**
     * {@inheritDoc}
     */
    public void afterInitialize() {
    }

    /**
     * {@inheritDoc}
     */
    public void onDestroy() {
    }

    /// CRUD

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
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
            if ( id != null && indexMap.containsKey( id ) ) {
                throw new DuplicateKeyException();
            }
            object.setId( getIdGenerator().assignId( id, getPlan() ) );
            indexMap.put( object.getId(), object );
            if ( object instanceof Segment ) {
                getPlan().addSegment( (Segment) object );
            }
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
        Part part = new Part();
        part.setId( getIdGenerator().assignId( id, getPlan() ) );
        part.setSegment( segment );
        segment.addNode( part );
        return part;
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector( Segment segment, Long id ) {
        Connector connector = new Connector();
        connector.setId( getIdGenerator().assignId( id, getPlan() ) );
        connector.setSegment( segment );
        segment.addNode( connector );
        return connector;
    }

    /**
     * {@inheritDoc}
     */
    public ExternalFlow createExternalFlow( Node source, Node target, String name, Long id ) {
        ExternalFlow externalFlow = new ExternalFlow( source, target, name );
        externalFlow.setId( getIdGenerator().assignId( id, getPlan() ) );
        return externalFlow;
    }

    /**
     * {@inheritDoc}
     */
    public InternalFlow createInternalFlow( Node source, Node target, String name, Long id ) {
        InternalFlow internalFlow = new InternalFlow( source, target, name );
        internalFlow.setId( getIdGenerator().assignId( id, getPlan() ) );
        return internalFlow;
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
            segment.disconnect( this );
        }
    }

    private ModelObject find( long id ) throws NotFoundException {
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
                        if ( flow.getId() == id ) result = flow;
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
        for ( T object : list( clazz ) ) {
            if ( name.equals( object.getName() ) )
                return object;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T findOrCreate( Class<T> clazz, String name, Long id ) {
        if ( name == null || name.isEmpty() )
            return null;
        T result = null;
        if ( id != null ) {
            try {
                result = find( clazz, id );
            } catch ( NotFoundException ignored ) {
                // do nothing - reference not yet imported
            }
        }
        if ( result == null ) {
            // Try finding one with the name but already created at a different id during import
            // because of "forward entity reference"
            // (e.g. an event was imported that references a location as its scope
            // before the location is imported)
            result = find( clazz, name );
        }
        if ( result == null ) {
            // Create new entity with name
            try {
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
     * Find a named segment.
     * @param name the name
     * @return the segment, if found
     * @throws NotFoundException if none exists
     */
    public Segment findSegment( String name ) throws NotFoundException {
        for ( Segment s : list( Segment.class ) )
            if ( name.equals( s.getName() ) )
                return s;

        throw new NotFoundException();
    }

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

}
