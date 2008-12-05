package com.mindalliance.channels.dao;

import com.mindalliance.channels.Scenario;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An in-memory, no-transactions implementation of a store.
 */
public final class Memory implements ScenarioDao {

    /** The sorted scenarios. */
    private Set<Scenario> scenarios = new HashSet<Scenario>();

    /** Scenarios, indexed by id. */
    private Map<Long,Scenario> idIndex = new HashMap<Long,Scenario>( INITIAL_CAPACITY );

    public Memory() {
        // TODO initialize memory to default scenario instead of test scenario
        // addScenario( Scenario.createDefault() );
        addScenario( new FireScenario() );
    }

    /** {@inheritDoc} */
    public Scenario findScenario( String name ) throws NotFoundException {
        for ( Scenario s : scenarios ) {
            if ( name.equals( s.getName() ) )
                return s;
        }

        throw new NotFoundException();
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
        if ( scenarios.size() > 1 ) {
            scenarios.remove( scenario );
            idIndex.remove( scenario.getId() );
        }
    }

    /** {@inheritDoc} */
    public void addScenario( Scenario scenario ) {
        final long id = scenario.getId();
        if ( idIndex.containsKey( id ) )
            throw new DuplicateKeyException();
        scenarios.add( scenario );
        idIndex.put( id, scenario );
    }

    /** {@inheritDoc} */
    public Scenario getDefaultScenario() {
        return scenarios.iterator().next();
    }

    /** {@inheritDoc} */
    public int getScenarioCount() {
        return scenarios.size();
    }
}
