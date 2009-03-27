package com.mindalliance.channels;

import com.mindalliance.channels.util.Play;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * External service interface.
 */
public interface Service {

    /**
     * Get the persistence store accessor.
     *
     * @return the dao
     */
    Dao getDao();

    /**
     * Find a scenario given its name.
     *
     * @param name the name
     * @return the aptly named scenario
     * @throws NotFoundException when not found
     */
    Scenario findScenario( String name ) throws NotFoundException;

    /**
     * Find a model object given its id.
     *
     * @param clazz the subclass of modelobject
     * @param id    the id
     * @param <T>   a subclass of modelObject
     * @return the object
     * @throws NotFoundException when not found
     */
    <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException;

    /**
     * Get all objects of the given class.
     *
     * @param clazz the given subclass of model object.
     * @param <T>   a subclass of model object.
     * @return an iterator
     */
    <T extends ModelObject> List<T> list( Class<T> clazz );

    /**
     * Iterate on ModelObject that are entities.
     *
     * @return an iterator on ModelObjects that are entities
     */
    Iterator<ModelObject> iterateEntities();

    /**
     * Add a model object to the persistence store.
     *
     * @param object the model object.
     */
    void add( ModelObject object );

    /**
     * Update a model object in the persistence store.
     *
     * @param object the model object
     */
    void update( ModelObject object );

    /**
     * Remove a persistent model object.
     * Last scenario will not be deleted.
     *
     * @param object the object
     */
    void remove( ModelObject object );

    /**
     * @return a default scenario
     */
    Scenario getDefaultScenario();

    /**
     * Find a model object by given name. If none, create it.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @param <T>   a subclass of model object
     * @return the object or null if name is null or empty
     */
    <T extends ModelObject> T findOrCreate( Class<T> clazz, String name );

    /**
     * Create a connector in a scenario.
     *
     * @param scenario the scenario
     * @return the new connector
     */
    Connector createConnector( Scenario scenario );

    /**
     * Create a new part in a scenario.
     *
     * @param scenario the scenario
     * @return a new default part.
     */
    Part createPart( Scenario scenario );


    /**
     * Create a flow between two nodes in this scenario, or between a node in this scenario and a
     * connector in another scenario.
     *
     * @param source the source node.
     * @param target the target node.
     * @param name   the name of the flow, possibly empty
     * @return a new flow.
     * @throws IllegalArgumentException when nodes are already connected or nodes are not both
     *                                  in this scenario, or one of the node isn't a connector in a different scenario.
     */
    Flow connect( Node source, Node target, String name );

    /**
     * Create a new scenario.
     *
     * @return the new scenario.
     */
    Scenario createScenario();

    /**
     * Get all non-empty resource specs, user-entered or not.
     *
     * @return a new list of resource spec
     */
    List<ResourceSpec> findAllResourceSpecs();

    /**
     * Find all resources that equal or narrow given resource
     *
     * @param resourceSpec a resource
     * @return a list of implied resources
     */
    List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( ResourceSpec resourceSpec );

    /**
     * Find all non-empty resources that equal or broaden given resource
     *
     * @param resourceSpec a resource
     * @return a list of implied resources
     */
    List<ResourceSpec> findAllResourcesBroadeningOrEqualTo( ResourceSpec resourceSpec );

    /**
     * Find all flows in all scenarios where the part applies as specified (as source or target).
     *
     * @param resourceSpec a resource spec
     * @param asSource     a boolean
     * @return a list of flows
     */
    List<Flow> findAllRelatedFlows( ResourceSpec resourceSpec, boolean asSource );

    /**
     * Find all plays for the resource
     *
     * @param resourceSpec a resource
     * @return a list of plays
     */
    List<Play> findAllPlays( ResourceSpec resourceSpec );

    /**
     * Find all plays for the resource
     *
     * @param resourceSpec a resource
     * @param specific whether the plays are specific to the resourceSpec
     * @return a list of plays
     */
    List<Play> findAllPlays( ResourceSpec resourceSpec, boolean specific );

    /**
     * Find all contact of specified resources
     *
     * @param resourceSpec a resource specification
     * @param isSelf       find resources specified by spec, or else who specified resources need to know
     * @return a list of ResourceSpec's
     */
    List<ResourceSpec> findAllContacts( ResourceSpec resourceSpec, boolean isSelf );

    /**
     * Find all user issues about a model object
     *
     * @param identifiable an object with an id
     * @return list of issues
     */
    List<Issue> findAllUserIssues( ModelObject identifiable );

    /**
     * Find candidate channels others than those already assigned.
     *
     * @param channelable a model object that can be assigned channels
     * @return a list of channels
     */
    List<Channel> findAllCandidateChannelsFor( Channelable channelable );

    /**
     * Find all flows across all scenarios that contact a matching part.
     *
     * @param resourceSpec a resource specification
     * @return a list of flows
     */
    List<Flow> findAllFlowsContacting( ResourceSpec resourceSpec );

    /**
     * Add some default scenarios, if needed.
     */
    @Transactional
    void initialize();

    /**
     * Find all known actors that belong to a resource spec
     *
     * @param resourceSpec a resource spec
     * @return a list of actors
     */
    List<Actor> findAllActors( ResourceSpec resourceSpec );

    /**
     * Make a replicate of the flow
     *
     * @param flow      the flow to replicate
     * @param isOutcome whether to replicate as outcome or requirement
     * @return a created flow
     */
    Flow replicate( Flow flow, boolean isOutcome );

    /**
     * Commit changes to persistent store.
     */
    void flush();

    /**
     * Find all jobs for an organization that are implied by parts but not confirmed.
     *
     * @param organization an organization
     * @return a list of jobs
     */
    List<Job> findUnconfirmedJobs( Organization organization );

    /**
     * Find all titles used in organizations, sorted alphabetically.
     *
     * @return a sorted list of strings
     */
    List<String> findAllJobTitles();

    /**
     * Find all tasks in a project, sorted.
     *
     * @return a list of strings
     */
    List<String> findAllTasks();

    /**
     * Find all names, sorted, of known instances of a model object class.
     *
     * @param aClass a model object class
     * @return a list of strings
     */
    List<String> findAllNames( Class<? extends ModelObject> aClass );

    /**
     * Whether the actor is referenced in another model object.
     * @param actor an actor
     * @return a boolean
     */
    boolean isReferenced( Actor actor );

    /**
     * Whether the role is referenced in another model object.
     * @param role a role
     * @return a boolean
     */
    boolean isReferenced( Role role );

    /**
     * Whether the organization is referenced in another model object.
     * @param organization an organization
     * @return a boolean
     */
    boolean isReferenced( Organization organization );

    /**
     * Whether the place is referenced in another model object.
     * @param place a place
     * @return a boolean
     */
    boolean isReferenced( Place place );

    /**
     * Called when application is terminated.
     */
    void onDestroy();

    /**
     * Find all issues attributable to entities and parts matching a resource spec.
     * @param resourceSpec a resource spec
     * @param specific a boolean -- true -> equality match, false -> marrow or equals
     * @return a list of issues
     */
    List<Issue> findAllIssuesFor( ResourceSpec resourceSpec, boolean specific );
}
