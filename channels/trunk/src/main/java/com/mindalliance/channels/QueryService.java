package com.mindalliance.channels;

import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.nlp.Proximity;
import com.mindalliance.channels.util.Employment;
import com.mindalliance.channels.util.Play;

import java.util.Iterator;
import java.util.List;

/**
 * Data query interface.
 */
public interface QueryService extends Service {

    /**
     * Get the persistence store accessor.
     *
     * @return the dao
     */
    Dao getDao();

    /**
     * Get the plan manager.
     *
     * @return the plan manager.
     */
    PlanManager getPlanManager();

    /**
     * Get attachment manager.
     *
     * @return an attachment manager
     */
    AttachmentManager getAttachmentManager();

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
     * @return a list
     */
    <T extends ModelObject> List<T> list( Class<T> clazz );

    /**
     * Get all referenced objects (not orphaned) of the given class.
     *
     * @param clazz the given subclass of model object.
     * @param <T>   a subclass of model object.
     * @return a list
     */
    <T extends ModelObject> List<T> listReferenced( Class<T> clazz );

    /**
     * Iterate on ModelObject that are referenced entities.
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
     * Add a model object to the persistence store.
     *
     * @param object the model object.
     * @param id     a Long
     */
    void add( ModelObject object, Long id );

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
     * Find a model object by given name. If none, create it and give it provided id.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @param id    a long
     * @param <T>   a subclass of model object
     * @return the object or null if name is null or empty
     */
    <T extends ModelObject> T findOrCreate( Class<T> clazz, String name, Long id );

    /**
     * Create a connector in a scenario.
     *
     * @param scenario the scenario
     * @return the new connector
     */
    Connector createConnector( Scenario scenario );

    /**
     * Create a connector in a scenario, with prior id.
     *
     * @param scenario the scenario
     * @param id       a Long
     * @return the new connector
     */
    Connector createConnector( Scenario scenario, Long id );

    /**
     * Create a new part in a scenario with a new id.
     *
     * @param scenario the scenario
     * @return a new default part.
     */
    Part createPart( Scenario scenario );

    /**
     * Create a new part in a scenario with given id.
     *
     * @param scenario the scenario
     * @param id       a Long
     * @return a new default part.
     */
    Part createPart( Scenario scenario, Long id );


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
     * Create a flow between two nodes in this scenario, or between a node in this scenario and a
     * connector in another scenario. Use provided id.
     *
     * @param source the source node.
     * @param target the target node.
     * @param name   the name of the flow, possibly empty
     * @param id     a long
     * @return a new flow.
     * @throws IllegalArgumentException when nodes are already connected or nodes are not both
     *                                  in this scenario, or one of the node isn't a connector in a different scenario.
     */
    Flow connect( Node source, Node target, String name, Long id );

    /**
     * Create a new scenario with new id.
     *
     * @return the new scenario.
     */
    Scenario createScenario();

    /**
     * Create a new scenario with old id.
     *
     * @param id a long
     * @return the new scenario.
     */
    Scenario createScenario( Long id );

    /**
     * Create a new scenario with given id and given id for default part.
     *
     * @param id            a long
     * @param defaultPartId a long
     * @return the new scenario.
     */
    Scenario createScenario( Long id, Long defaultPartId );

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
     * @param specific     whether the plays are specific to the resourceSpec
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
     * Find all relevant channels for a given resource spec.
     *
     * @param spec the spec
     * @return the channels
     */
    List<Channel> findAllChannelsFor( ResourceSpec spec );

    /**
     * Find all flows across all scenarios that contact a matching part.
     *
     * @param resourceSpec a resource specification
     * @return a list of flows
     */
    List<Flow> findAllFlowsContacting( ResourceSpec resourceSpec );

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
     * Find all tasks in a plan, sorted.
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
     * Find actors in given organization and role.
     *
     * @param organization the organization, possibly Organization.UNKNOWN
     * @param role         the role, possibly Role.UNKNOWN
     * @return a sorted list of actors
     */
    List<Actor> findActors( Organization organization, Role role );

    /**
     * Find all roles in given organization across all scenarios.
     *
     * @param organization the organization, possibly Organization.UNKNOWN
     * @return a sorted list of roles
     */
    List<Role> findRolesIn( Organization organization );

    /**
     * Find all organizations in plan, including the UNKNOWN organization, if need be.
     *
     * @return a sorted list of organizations
     */
    List<Organization> findOrganizations();

    /**
     * Find actors that should be included in a flow of a part.
     *
     * @param part the part
     * @param flow a flow of the part
     * @return list of actors in plan that applies
     */
    List<Actor> findRelevantActors( Part part, Flow flow );

    /**
     * Find actors in given organization and role in a given scenario.
     *
     * @param organization the organization, possibly Organization.UNKNOWN
     * @param role         the role, possibly Role.UNKNOWN
     * @param scenario     the scenario
     * @return a sorted list of actors
     */
    List<Actor> findActors( Organization organization, Role role, Scenario scenario );

    /**
     * Whether the actor is referenced in another model object.
     *
     * @param actor an actor
     * @return a boolean
     */
    boolean isReferenced( Actor actor );

    /**
     * Whether the role is referenced in another model object.
     *
     * @param role a role
     * @return a boolean
     */
    boolean isReferenced( Role role );

    /**
     * Whether the organization is referenced in another model object.
     *
     * @param organization an organization
     * @return a boolean
     */
    boolean isReferenced( Organization organization );

    /**
     * Whether the place is referenced in another model object.
     *
     * @param place a place
     * @return a boolean
     */
    boolean isReferenced( Place place );

    /**
     * Whether the plan event is referenced in another model object.
     *
     * @param event a plan event
     * @return a boolean
     */
    boolean isReferenced( Event event );

    /**
     * Get reference count for event.
     *
     * @param event an event
     * @return an int
     */
    int getReferenceCount( Event event );

    /**
     * Whether the plan phase is referenced in another model object.
     *
     * @param phase a plan phase
     * @return a boolean
     */
    boolean isReferenced( Phase phase );

    /**
     * Called when application is terminated.
     */
    void onDestroy();

    /**
     * Find all issues attributable to entities and parts matching a resource spec.
     *
     * @param resourceSpec a resource spec
     * @param specific     a boolean -- true -> equality match, false -> marrow or equals
     * @return a list of issues
     */
    List<Issue> findAllIssuesFor( ResourceSpec resourceSpec, boolean specific );

    /**
     * Find all responsibilities of an actor.
     * A responsibility is a found resourceSpec that includes the actor, but with the actor removed.
     *
     * @param actor an actor
     * @return a list of resource specifications
     */
    List<ResourceSpec> findAllResponsibilitiesOf( Actor actor );

    /**
     * Find any relationship between a scenario and another.
     * A relationship is one or more external flow in the from-scenario referencing a connector in the to-scenario.
     *
     * @param fromScenario a scenario
     * @param toScenario   a scenario
     * @return a scenario relationship or null if no link exists
     */
    ScenarioRelationship findScenarioRelationship( Scenario fromScenario, Scenario toScenario );

    /**
     * Find any relationship between an entity and an other.
     * A relationship is one or more flow from the entity to the other.
     *
     * @param fromEntity an entity
     * @param toEntity   an entity
     * @return an entity relationship or null if no link exists
     */
    <T extends ModelObject> EntityRelationship<T> findEntityRelationship( T fromEntity, T toEntity );

    /**
     * Find all confirmed jobs with resource spec
     *
     * @param resourceSpec a resource spec
     * @return a list of jobs
     */
    List<Job> findAllConfirmedJobs( ResourceSpec resourceSpec );

    /**
     * Find all job titles of an actor.
     *
     * @param actor an actor
     * @return a list of strings
     */
    List<String> findJobTitles( Actor actor );

    /**
     * Find all organizations employing a given actor.
     *
     * @param actor an actor
     * @return a list of organizations
     */
    List<Organization> findEmployers( Actor actor );

    /**
     * Find if part is ever started.
     *
     * @param part a part
     * @return a boolean
     */
    boolean findIfPartStarted( Part part );

    /**
     * Whether the scenario can ever start.
     *
     * @param scenario a scenario
     * @return a boolean
     */
    boolean findIfScenarioStarted( Scenario scenario );

    /**
     * Find all parts that has the specified resource.
     *
     * @param scenario     a scenario
     * @param resourceSpec a resource spec
     * @return a list of parts
     */
    List<Part> findAllParts( Scenario scenario, ResourceSpec resourceSpec );

    /**
     * Find all parts located at a given place.
     *
     * @param place a place
     * @return a list of parts
     */
    List<Part> findAllPartsWithLocation( Place place );

    /**
     * Find the unsatisfied needs of a part.
     *
     * @param part a part
     * @return list of needs (flows with connectors as sources)
     */
    List<Flow> findUnconnectedNeeds( Part part );

    /**
     * Find the unused capabilities of a part.
     *
     * @param part a part
     * @return list of capabilities (flows with connectors as targets)
     */
    List<Flow> findUnusedCapabilities( Part part );

    /**
     * Find all connectors for capabilities that match a need.
     *
     * @param need a flow
     * @return a list of connectors
     */
    List<Connector> findAllSatificers( Flow need );

    /**
     * Find all parts in a plan that can start the given scenario
     * because of the event they cause or the event phases they terminate.
     *
     * @param scenario a scenario
     * @return a list of parts
     */
    List<Part> findInitiators( Scenario scenario );

    /**
     * Find all parts in a plan that can terminate the given scenario
     * because of the event they cause or the event phases they terminate.
     *
     * @param scenario a scenario
     * @return a list of parts
     */
    List<Part> findTerminators( Scenario scenario );

    /**
     * Find all external parts in a plan that can terminate the given scenario
     * because of the event they cause or the event phases they terminate.
     *
     * @param scenario a scenario
     * @return a list of parts
     */
    List<Part> findExternalTerminators( Scenario scenario );

    /**
     * Whether a task in another scenario intiates a given scenario.
     *
     * @param scenario a scenario
     * @return a boolean
     */
    boolean isInitiated( Scenario scenario );

    /**
     * Get title for actor.
     *
     * @param actor an actor
     * @return a string
     */
    String getTitle( Actor actor );

    /**
     * Find all events that are not an incident.
     *
     * @return a list of plan events
     */
    List<Event> findPlannedEvents();

    /**
     * Find all parts that mitigate a risk or terminate the event cause.
     *
     * @param scenario a scenario
     * @param risk     a risk
     * @return a list of parts
     */
    List<Part> findMitigations( Scenario scenario, Risk risk );

    /**
     * Find scenarios in which an actor is involved.
     *
     * @param actor an actor
     * @return a list of scenario
     */
    List<Scenario> findScenarios( Actor actor );

    /**
     * Find all actors participating in a scenario.
     *
     * @param scenario the scenario
     * @return a sorted list of actors.
     */
    List<Actor> findActors( Scenario scenario );

    /**
     * Find all actor last names.
     *
     * @return a list of all actor last names (with duplicates)
     */
    List<String> findAllActorLastNames();

    /**
     * Find all jobs, confirmed or not.
     *
     * @return a list of jobs
     */
    List<Employment> findAllEmployments();

    /**
     * Find all employments for a given role.
     *
     * @param role a role
     * @return a list of employments
     */
    List<Employment> findAllEmploymentsForRole( Role role );

    /**
     * Find all employments for a given actor.
     *
     * @param actor an actor
     * @return a list of employments
     */
    List<Employment> findAllEmploymentsForActor( Actor actor );

    /**
     * Find all distinct flow names.
     *
     * @return a list of strings
     */
    List<String> findAllFlowNames();

    /**
     * Find all issues on all model objects in the plan.
     *
     * @param analyst an analyst
     * @return a list of issues.
     */
    List<Issue> findAllIssues( Analyst analyst );

    /**
     * Find all unwaived issues on all model objects in the plan.
     *
     * @param analyst an analyst
     * @return a list of issues.
     */
    List<Issue> findAllUnwaivedIssues( Analyst analyst );

    /**
     * List all entities of a given class, plus the unmnown entity of this class.
     *
     * @param clazz a class extending ModelObject
     * @return a list of entities
     */
    <T extends ModelObject> List<T> listEntitiesWithUnknown( Class<T> clazz );

    /**
     * Find all roles played by an actor.
     *
     * @param actor an actor
     * @return a list of roles
     */
    List<Role> findAllRolesOf( Actor actor );

    /**
     * Find all actors in an organization.
     *
     * @param organization an organization
     * @return a list of organizations
     */
    List<Actor> findAllActorsInOrganization( Organization organization );

    /**
     * Find all parts and flows that directly involve a given entity.
     *
     * @param entity a model object
     * @return a list of model objects (parts and flows)
     */
    List<ModelObject> findAllScenarioObjectsInvolving( ModelObject entity );

    /**
     * Find all scenarios that respond to a given event.
     *
     * @param event an event
     * @return a list of scenarios
     */
    List<Scenario> findScenariosRespondingTo( Event event );

    /**
     * Find all parts that terminate a scenario's event.
     *
     * @param scenario a scenario
     * @return a list of parts
     */
    List<Part> findPartsTerminatingEventPhaseIn( Scenario scenario );

    /**
     * Find all parts initiating a given event.
     *
     * @param event an event
     * @return a list of parts
     */
    List<Part> findPartsInitiatingEvent( Event event );

    /**
     * Find all parts that start with a scenario's event.
     *
     * @param scenario a scenario
     * @return a list of parts
     */
    List<Part> findPartsStartingWithEventIn( Scenario scenario );

    /**
     * Find all attachments.
     *
     * @return a list of attachments.
     */
    List<Attachment> findAllAttachments();

    /**
     * Find urls of all attachments.
     *
     * @return a list of strings
     */
    List<String> findAllAttached();

    /**
     * Find all model objects.
     *
     * @return a list of model objects
     */
    List<ModelObject> findAllModelObjects();

    /**
     * Find all geonames in plan.
     *
     * @return a list of strings
     */
    List<String> findAllGeonames();

    /**
     * Find all places directly or indirectly within a given place.
     *
     * @param place a place
     * @return a list of places
     */
    List<Place> findAllPlacesWithin( Place place );

    /**
     * Find all model objects located inside a given place.
     *
     * @param place a place
     * @return a list of model objects
     */
    List<? extends ModelObject> findAllModelObjectsIn( Place place );

    /**
     * Find all model objects located within a given phase.
     *
     * @param phase a phase
     * @return a list of model objects
     */
    List<? extends ModelObject> findAllModelObjectsIn( Phase phase );

    /**
     * Find all model objects directly impacted by or impacting an event.
     *
     * @param event an event
     * @return a list of model objects
     */
    List<ModelObject> findAllModelObjectsDirectlyRelatedToEvent( Event event );

    /**
     * Find all model objects referencing a given place.
     *
     * @param place a place
     * @return a list of model objects
     */
    List<ModelObject> findAllReferencesTo( Place place );

    /**
     * Find all roots of hierarchy in which a hierarchical object belongs.
     *
     * @param hierarchical a hierarchical object
     * @return a list of a hierarchical objects
     */
    List<Hierarchical> findRoots( Hierarchical hierarchical );

    /**
     * Find all descendants of a hierarchical object.
     *
     * @param hierarchical a hierarchical object
     * @return a list of a hierarchical objects
     */
    List<Hierarchical> findAllDescendants( Hierarchical hierarchical );

    /**
     * Replay journals for all plans and save the results.
     *
     * @param commander the commander for replaying
     */
    void replayJournals( Commander commander );

    /**
     * Whether two string reach a given level of semantic proximity.
     *
     * @param text      a string
     * @param otherText a string
     * @param proximity a proximity level
     * @return a boolean
     */
    boolean isSemanticMatch( String text, String otherText, Proximity proximity );

    /**
     * Whether two texts have high semantic proximity. The texts
     *
     * @param text      a string
     * @param otherText a string
     * @return a boolean
     */
    boolean likelyRelated( String text, String otherText );

    /**
     * Find all sharing commitments addressing a given information need.
     *
     * @param need a flow
     * @return a list of flows
     */
    List<Flow> findAllSharingCommitmentsAddressing( Flow need );

    /**
     * Find parts with anonymous tasks which resourceSpec is narrowed by that of a given part.
     *
     * @param part a part
     * @return a list of parts
     */
    List<Part> findAnonymousPartsMatching( Part part );

    /**
     * Calculate how important a part is in terms of the risks it mitigates directly or indirectly.
     *
     * @param part a part
     * @return an issue level
     */
    Issue.Level getPartPriority( Part part );

    /**
     * Find all actors personifying user with given username.
     *
     * @param userName a string
     * @return a list of actors
     */
    List<Actor> findAllActorsAsUser( String userName );

    /**
     * Get current plan.
     *
     * @return a plan
     */
    Plan getCurrentPlan();

    /**
     * Find icon name for given part.
     *
     * @param part          a part
     * @param imagesDirName the name of the directory with the default icons
     * @return a string
     */
    String findIconName( Part part, String imagesDirName );

    /**
     * Find icon name for given model object.
     *
     * @param modelObject   a model object
     * @param imagesDirName the name of the directory with the default icons
     * @return a string
     */
    String findIconName( ModelObject modelObject, String imagesDirName );

    /**
     * Find user names of all planners for current plan.
     *
     * @return a list of strings
     */
    List<String> findAllPlanners();

    /**
     * Given his/her username find the user's full name.
     *
     * @param userName a string
     * @return a string
     */
    String findUserFullName( String userName );

    /**
     * Given his/her username find the user's normalized full name.
     *
     * @param userName a string
     * @return a string
     */
    String findUserNormalizedFullName( String userName );

    /**
     * Find usernames of all users who can access the current plan.
     *
     * @return a list of strings
     */
    List<String> findAllPlanUsernames();

    /**
     * Find all scenarios for a given phase.
     *
     * @param phase a phase
     * @return a list of scenarios
     */
    List<Scenario> findAllScenariosForPhase( Phase phase );

    /**
     * Find all parts that cause an event.
     *
     * @param event an event
     * @return a list of parts
     */
    List<Part> findCausesOf( Event event );
}
