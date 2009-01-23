package com.mindalliance.channels;

import com.mindalliance.channels.util.Play;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Protocol for manipulating objects.
 * Implementations should ensure that there is at least one scenario available.
 */
public interface Dao extends Serializable {

    /**
     * The initial number of scenario slots.
     * This should be close to the observed average scenarios.
     */
    int INITIAL_CAPACITY = 10;

    /**
     * Create a brand new scenario.
     *
     * @return an initialized default scenario
     */
    Scenario createScenario();

    /**
     * Find a scenario given its name.
     *
     * @param name the name
     * @return the corresponding scenario, or null if not found.
     * @throws NotFoundException when not found
     */
    Scenario findScenario( String name ) throws NotFoundException;

    /**
     * Find a scenario given its id.
     *
     * @param id the id
     * @return the corresponding scenario, or null if not found.
     * @throws NotFoundException when not found
     */
    Scenario findScenario( long id ) throws NotFoundException;

    /**
     * List scenarios sorted by names. There should always be at least one
     * scenario in this list.
     *
     * @return an iterator on scenarios, sorted by names
     */
    Iterator<Scenario> scenarios();

    /**
     * Delete a scenario. Will not delete the last scenario (silently succeeds).
     *
     * @param scenario the scenario to delete
     */
    void removeScenario( Scenario scenario );

    /**
     * Add a scenario.
     *
     * @param scenario the scenario to add
     * @throws DuplicateKeyException when either name or key is already defined
     */
    void addScenario( Scenario scenario );

    /**
     * Get the designated default scenario.
     *
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
     *
     * @return a role iterator
     */
    Iterator<Role> roles();

    /**
     * Find a role given its id.
     *
     * @param id the id
     * @return the corresponding role.
     * @throws NotFoundException when not found
     */
    Role findRole( long id ) throws NotFoundException;

    /**
     * Finds a role named name. If none, creates it.
     *
     * @param name the role's name
     * @return a role
     */
    Role findOrMakeRole( String name );

    /**
     * Remove a role
     *
     * @param role role to be removed
     */
    void removeRole( Role role );

    /**
     * Iterator on all actors
     *
     * @return an actor iterator
     */
    Iterator<Actor> actors();

    /**
     * Find an actor given its id.
     *
     * @param id the id
     * @return the corresponding actor.
     * @throws NotFoundException when not found
     */
    Actor findActor( long id ) throws NotFoundException;

    /**
     * Finds an actor named name. If none, creates it.
     *
     * @param name the actor's name
     * @return an actor
     */
    Actor findOrMakeActor( String name );

    /**
     * Remove an actor
     *
     * @param actor actor to be removed
     */
    void removeActor( Actor actor );

    /**
     * Iterator on all organizations
     *
     * @return an organization iterator
     */
    Iterator<Organization> organizations();

    /**
     * Find an organization given its id.
     *
     * @param id the id
     * @return the corresponding organization
     * @throws NotFoundException when not found
     */
    Organization findOrganization( long id ) throws NotFoundException;

    /**
     * Finds an organization named name. If none, creates it.
     *
     * @param name the organization's name
     * @return an organization
     */
    Organization findOrMakeOrganization( String name );

    /**
     * Remove an organization
     *
     * @param organization organization to be removed
     */
    void removeOrganization( Organization organization );

    /**
     * Iterator on all places
     *
     * @return a place iterator
     */
    Iterator<Place> places();


    /**
     * Find a place given its id.
     *
     * @param id the id
     * @return the corresponding place
     * @throws NotFoundException when not found
     */
    Place findPlace( long id ) throws NotFoundException;

    /**
     * Finds a place named name. If none, creates it.
     *
     * @param name the places's name
     * @return a place
     */
    Place findOrMakePlace( String name );

    /**
     * Remove a place
     *
     * @param place place to be removed
     */
    void removePlace( Place place );

    /**
     * Find all resources that equal or narrow given resource
     *
     * @param resourceSpec a resource
     * @return a list of implied resources
     */
    List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( ResourceSpec resourceSpec );

    /**
     * Find all plays for the resource
     *
     * @param resourceSpec a resource
     * @return a list of plays
     */
    List<Play> findAllPlays( ResourceSpec resourceSpec );

    /**
     * Find all contact of specified resources
     * @param resourceSpec a resource specification
     * @param isSelf find resources specified by spec, or else who specified resources need to know
     * @return a list of ResourceSpec's
     */
    List<ResourceSpec> findAllContacts( ResourceSpec resourceSpec, boolean isSelf );

    /**
     * Return iterator on all known resource specifications
     * @return an iterator on resource specs
     */
    Iterator<ResourceSpec> resourceSpecs();

    /**
     * Return iterator on all permanent resource specifications
     * @return an iterator on resource specs
     */
    Iterator<ResourceSpec> permanentResourceSpecs();

    /**
     * Add a permanent resource specification
     * if not empty or defined by only one entity (entities are permanent)
     * @param resourceSpec a resource specification
     */
    void addResourceSpec( ResourceSpec resourceSpec );

    /**
     * Remove a permanent resource specification
     * @param resourceSpec a resource specification
     */
    void removeResourceSpec( ResourceSpec resourceSpec );

    /**
     * Whether the resource spec exists independently of parts
     * @param resourceSpec the resource spec
     * @return a boolean
     */
    boolean isPermanent( ResourceSpec resourceSpec );

    /**
     * Find all user issues about a model object
     * @param modelObject the model object
     * @return list of issues
     */
    List<Issue> findAllUserIssues( ModelObject modelObject );

    /**
     * Add a user issue
     * @param userIssue a user issue
     */
    void addUserIssue( UserIssue userIssue );

    /**
     * Remove a user issue
     * @param userIssue a user issue
     */
    void removeUserIssue( UserIssue userIssue );
}
