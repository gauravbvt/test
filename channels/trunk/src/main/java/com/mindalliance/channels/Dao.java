package com.mindalliance.channels;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Protocol for manipulating objects.
 * Implementations should ensure that there is at least one scenario available.
 * @see Scenario#createDefault()
 */
public interface Dao extends Serializable {

    /**
     * The initial number of scenario slots.
     * This should be close to the observed average scenarios. */
    int INITIAL_CAPACITY = 10;

    /**
     * Create a brand new scenario.
     * @return an initialized default scenario
     */
    Scenario createScenario();

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

    /**
     * @return the total number of scenarios.
     */
    int getScenarioCount();

    /**
     * @return a new default part.
     */
    Part createPart();

    /**
     * @return a new connector.
     */
    Connector createConnector();

    /**
     * Iterator on all roles
     * @return a role iterator
     */
    Iterator<Role> roles();

    /**
     * Find a role given its id.
     * @param id the id
     * @return the corresponding role, or null if not found.
     * @throws NotFoundException when not found
     */
    Role findRole( long id ) throws NotFoundException;

    /**
     * Finds a role named name. If none, creates it.
     * @param name the role's name
     * @return a role
     */
    Role findOrMakeRole( String name );

    /**
     * Remove a role
     * @param role role to be removed
     */
    void removeRole( Role role );
}
