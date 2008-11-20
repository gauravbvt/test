package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Scenario;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * An in-memory, no-transactions implementation of a store.
 */
public final class Memory implements ScenarioDao {

    /** The initial number of scenario slots. */
    private static final int INITIAL_CAPACITY = 10;

    /** The sorted scenarios. */
    private Set<Scenario> scenarios = new TreeSet<Scenario>();

    /** Scenarios, indexed by name. */
    private Map<String, Scenario> nameIndex = new HashMap<String,Scenario>( INITIAL_CAPACITY );

    /** Scenarios, indexed by id. */
    private Map<Long,Scenario> idIndex = new HashMap<Long,Scenario>( INITIAL_CAPACITY );

    public Memory() {
        addScenario( Scenario.createDefault() );
    }

    /** {@inheritDoc} */
    public Scenario findScenario( String name ) throws NotFoundException {
        final Scenario result = nameIndex.get( name );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    /** {@inheritDoc} */
    public Scenario findScenario( long id ) throws NotFoundException {
        final Scenario result = idIndex.get( id );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    /** {@inheritDoc} */
    public Iterator<Scenario> scenarios() {
        return scenarios.iterator();
    }

    /** {@inheritDoc} */
    public void removeScenario( Scenario scenario ) {
        scenarios.remove( scenario );
        nameIndex.remove( scenario.getName() );
        idIndex.remove( scenario.getId() );
    }

    /** {@inheritDoc} */
    public void addScenario( Scenario scenario ) {
        final String name = scenario.getName();
        final long id = scenario.getId();
        if ( nameIndex.containsKey( name ) || idIndex.containsKey( id ) )
            throw new DuplicateKeyException();
        scenarios.add( scenario );
        nameIndex.put( name, scenario );
        idIndex.put( id, scenario );
    }

    /** {@inheritDoc} */
    public Scenario getDefaultScenario() {
        return scenarios.iterator().next();
    }
}
