package com.mindalliance.channels;

import com.mindalliance.channels.util.Play;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * External service interface.
 */
// @Transactional
public interface Service {

    /**
     * Find a scenario given its name.
     * @param name the name
     * @return the aptly named scenario
     * @throws NotFoundException when not found
     */
//    @Transactional( readOnly = true )
    Scenario findScenario( String name ) throws NotFoundException;

    /**
     * Find a model object given its id.
     * @param clazz the subclass of modelobject
     * @param id the id
     * @param <T> a subclass of modelObject
     * @return the object
     * @throws NotFoundException when not found
     */
//    @Transactional( readOnly = true )
    <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException;

    /**
     * Get all objects of the given class.
     * @param clazz the given subclass of model object.
     * @param <T> a subclass of model object.
     * @return an iterator
     */
//    @Transactional( readOnly = true )
    <T extends ModelObject> List<T> list( Class<T> clazz );

    /**
     * Iterate on ModelObject that are entities.
     * @return an iterator on ModelObjects that are entities
     */
//    @Transactional( readOnly = true )
    Iterator<ModelObject> iterateEntities();


    /**
     * Add a model object to the persistence store.
     * @param object the model object.
     */
    void add( ModelObject object );

    /**
     * Remove a persistent model object.
     * Last scenario will not be deleted.
     * @param object the object
     */
    void remove( ModelObject object );

    /**
     * @return a default scenario
     */
//    @Transactional( readOnly = true )
    Scenario getDefaultScenario();

    /**
     * Find a model object by given name. If none, create it.
     * @param clazz the kind of model object
     * @param name the name
     * @param <T> a subclass of model object
     * @return the object or null if name is null or empty
     */
    <T extends ModelObject> T findOrCreate( Class<T> clazz, String name );

    /**
     * Create a connector in a scenario.
     * @param scenario the scenario
     * @return the new connector
     */
    Connector createConnector( Scenario scenario );

    /**
     * Create a new part in a scenario.
     * @param scenario the scenario
     * @return a new default part.
     */
    Part createPart( Scenario scenario );


    /**
     * Create a flow between two nodes in this scenario, or between a node in this scenario and a
     * connector in another scenario.
     * @param source the source node.
     * @param target the target node.
     * @param name the name of the flow, possibly empty
     * @return a new flow.
     * @throws IllegalArgumentException when nodes are already connected or nodes are not both
     * in this scenario, or one of the node isn't a connector in a different scenario.
     */
    Flow connect( Node source, Node target, String name );

    /**
     * Create a new scenario.
     * @return the new scenario.
     */
    Scenario createScenario();

    /**
     * Get all non-empty resource specs, user-entered or not.
     * @return a new list of resource spec
     */
//    @Transactional( readOnly = true )
    List<ResourceSpec> findAllResourceSpecs();

    /**
     * Find all resources that equal or narrow given resource
     *
     * @param resourceSpec a resource
     * @return a list of implied resources
     */
//    @Transactional( readOnly = true )
    List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( ResourceSpec resourceSpec );

    /**
     * Find all non-empty resources that equal or broaden given resource
     *
     * @param resourceSpec a resource
     * @return a list of implied resources
     */
    @Transactional( readOnly = true )
    List<ResourceSpec> findAllResourcesBroadeningOrEqualTo( ResourceSpec resourceSpec );
    
    /**
     * Find all plays for the resource
     *
     * @param resourceSpec a resource
     * @return a list of plays
     */
//    @Transactional( readOnly = true )
    List<Play> findAllPlays( ResourceSpec resourceSpec );

    /**
     * Find all contact of specified resources
     * @param resourceSpec a resource specification
     * @param isSelf find resources specified by spec, or else who specified resources need to know
     * @return a list of ResourceSpec's
     */
//    @Transactional( readOnly = true )
    List<ResourceSpec> findAllContacts( ResourceSpec resourceSpec, boolean isSelf );

    /**
     * Find all user issues about a model object
     * @param identifiable an object with an id
     * @return list of issues
     */
//    @Transactional( readOnly = true )
    List<Issue> findAllUserIssues( ModelObject identifiable );

    /**
     * Find permanent resource spec matching given one
     * @param resourceSpec a ResourceSpec to match against
     * @return a permanent resource spec or null if none found
     */
//    @Transactional( readOnly = true )
    ResourceSpec findPermanentResourceSpec( ResourceSpec resourceSpec );

    /**
     * Add resource spec if equivalent not permanent yet,
     * else already permament resource from given resource spec
     * @param resourceSpec resource spec to add or update from
     */
    void addOrUpdate( ResourceSpec resourceSpec );

    /**
     * Add some default scenarios, if needed.
     */
    @Transactional
    void initialize();

    /**
     * Find all known actors that belong to a resource spec
     * @param resourceSpec a resource spec
     * @return a list of actors
     */
    List<Actor> findAllActors( ResourceSpec resourceSpec );

    /**
     * Make a replicate of the flow
     *
     * @param flow the flow to replicate
     * @param isOutcome whether to replicate as outcome or requirement
     * @return a created flow
     */
    Flow replicate( Flow flow, boolean isOutcome );
}
