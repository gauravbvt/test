package com.mindalliance.channels.dao;

import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Role;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An in-memory, no-transactions implementation of a store.
 */
public final class Memory implements Dao {

    /** The sorted scenarios. */
    private Set<Scenario> scenarios = new HashSet<Scenario>();

    /** Scenarios, indexed by id. */
    private Map<Long,Scenario> idIndex = new HashMap<Long,Scenario>( INITIAL_CAPACITY );

    public Memory() {
        // TODO initialize memory to default scenario instead of test scenario
        // addScenario( Scenario.createDefault() );
        final EvacuationScenario evac = new EvacuationScenario( this );
        addScenario( evac );
        addScenario( new FireScenario( this, evac ) );
    }

    /** {@inheritDoc} */
    public Scenario createScenario() {
        // TODO factor out id initialization
        final Scenario result = new Scenario();
        result.setDao( this );
        Scenario.initializeScenario( result );
        addScenario( result );
        return result;
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

            final Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                final Part p = parts.next();
                p.removeAllOutcomes();
                p.removeAllRequirements();
            }

            // TODO hook to dao for nodes/flows deletions?
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

    /** {@inheritDoc} */
    public Part createPart() {
        return new Part();
    }

    /** {@inheritDoc} */
    public Connector createConnector() {
        return new Connector();
    }

    /**
     * Find a role given its id.
     *
     * @param id the id
     * @return the corresponding role, or null if not found.
     * @throws com.mindalliance.channels.NotFoundException
     *          when not found
     */
    public Role findRole( long id ) throws NotFoundException {
        return null;  // Todo
    }
}
