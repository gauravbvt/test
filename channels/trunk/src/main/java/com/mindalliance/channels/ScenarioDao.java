package com.mindalliance.channels;

import com.mindalliance.channels.model.Scenario;

import java.util.Iterator;

/**
 * Protocol for manipulating scenarios.
 */
public interface ScenarioDao {

    /**
     * Find a scenario given its name.
     * @param name the name
     * @return the corresponding scenario, or null if not found.
     */
    Scenario findScenario( String name );

    /**
     * Find a scenario given its id.
     * @param id the id
     * @return the corresponding scenario, or null if not found.
     */
    Scenario findScenario( long id );

    /**
     * List scenarios sorted by names.
     * @return an iterator on scenarios, sorted by names
     */
    Iterator<Scenario> scenarios();

    /**
     * Delete a scenario.
     * @param scenario the scenario to delete
     */
    void removeScenario( Scenario scenario );

    /**
     * Add a scenario.
     * @param scenario the scenario to add
     * @throws DuplicateKeyException when either name or key is already defined
     */
    void addScenario( Scenario scenario ) throws DuplicateKeyException;
}
