package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Scenario;

import java.util.Iterator;

/**
 * Protocol for manipulating scenarios.
 * Implementations should ensure that there is at least one scenario available.
 * @see Scenario#createDefault()
 */
public interface ScenarioDao {

    /**
     * Find a scenario given its name.
     * @param name the name
     * @return the corresponding scenario, or null if not found.
     * @throws NotFoundException when not found
     */
    Scenario findScenario( String name ) throws NotFoundException;

    /**
     * Find a scenario given its id.
     * @param id the id
     * @return the corresponding scenario, or null if not found.
     * @throws NotFoundException when not found
     */
    Scenario findScenario( long id ) throws NotFoundException;

    /**
     * List scenarios sorted by names. There should always be at least one
     * scenario in this list.
     * @return an iterator on scenarios, sorted by names
     */
    Iterator<Scenario> scenarios();

    /**
     * Delete a scenario. Will not delete the last scenario (silently succeeds).
     * @param scenario the scenario to delete
     */
    void removeScenario( Scenario scenario );

    /**
     * Add a scenario.
     * @param scenario the scenario to add
     * @throws DuplicateKeyException when either name or key is already defined
     */
    void addScenario( Scenario scenario );

    /**
     * Get the designated default scenario.
     * @return a scenario
     */
    Scenario getDefaultScenario();
}
