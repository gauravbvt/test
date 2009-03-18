package com.mindalliance.channels.dao;

import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.InternalFlow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An in-memory, no-transactions implementation of a store.
 */
public final class Memory implements Dao {

    /** Counter for generated modelobjects id. */
    private long idCounter = 1L;

    /** The scenarios, for convenience... */
    private Set<Scenario> scenarios = new HashSet<Scenario>();

    /**
     * ModelObjects, indexed by id.
     */
    private Map<Long, ModelObject> idIndex = new HashMap<Long, ModelObject>( INITIAL_CAPACITY );

    public Memory() {
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> List<T> list( final Class<T> clazz ) {
        return (List<T>) CollectionUtils.select( idIndex.values(), new Predicate() {
            public boolean evaluate( Object object ) {
                return clazz.isAssignableFrom( object.getClass() );
            }
        } );
    }

    /** {@inheritDoc} */
    public void add( ModelObject object ) {
        if ( idIndex.containsKey( object.getId() ) )
            throw new DuplicateKeyException();

        object.setId( newId() );
        idIndex.put( object.getId(), object );

        if ( object instanceof Scenario )
            scenarios.add( (Scenario) object );

    }

    /** {@inheritDoc} */
    public void update( ModelObject object ) {
    }

    /** {@inheritDoc} */
    public long getScenarioCount() {
        return (long) scenarios.size();
    }

    /** {@inheritDoc} */
    public Part createPart( Scenario scenario ) {
        Part part = new Part();
        part.setId( newId() );
        part.setScenario( scenario );
        return part;
    }

    /** {@inheritDoc} */
    public Connector createConnector( Scenario scenario ) {
        Connector connector = new Connector();
        connector.setId( newId() );
        connector.setScenario( scenario );
        return connector;
    }

    /** {@inheritDoc} */
    public ExternalFlow createExternalFlow( Node source, Node target, String name ) {
        ExternalFlow externalFlow = new ExternalFlow( source, target, name );
        externalFlow.setId( newId() );
        return externalFlow;
    }

    /** {@inheritDoc} */
    public InternalFlow createInternalFlow( Node source, Node target, String name ) {
        InternalFlow internalFlow = new InternalFlow( source, target, name );
        internalFlow.setId( newId() );
        return internalFlow;
    }

    private long newId() {
        return idCounter++;
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        return (T) find( id );
    }

    /** {@inheritDoc} */
    public void remove( ModelObject object ) {
        if ( object instanceof Scenario )
            remove( (Scenario) object );
        else
            idIndex.remove( object.getId() );
    }

    private void remove( Scenario scenario ) {
        if ( scenarios.size() > 1 ) {
            scenarios.remove( scenario );
            idIndex.remove( scenario.getId() );

            scenario.disconnect();
        }
    }


    private ModelObject find( long id ) throws NotFoundException {
        ModelObject result = idIndex.get( id );
        if ( result == null ) {
            Iterator<Scenario> iterator = list( Scenario.class ).iterator();
            while ( result == null && iterator.hasNext() ) {
                Scenario scenario = iterator.next();
                result = scenario.getNode( id );
                if ( result == null ) {
                    Iterator<Flow> flows = scenario.flows();
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

    /** {@inheritDoc} */
    public <T extends ModelObject> T find( Class<T> clazz, String name ) {
        for ( T object : list( clazz ) ) {
            if ( object.getName().equals( name ) )
                return object;
        }

        return null;
    }

    /** {@inheritDoc} */
    public void flush() {
    }

}
