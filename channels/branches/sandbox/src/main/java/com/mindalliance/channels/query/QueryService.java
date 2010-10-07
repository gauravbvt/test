package com.mindalliance.channels.query;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.Dao;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Agreement;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.nlp.Proximity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Data query interface.
 */
public interface QueryService {

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
     * Find a model object given its id.
     *
     * @param clazz the subclass of modelobject
     * @param id    the id
     * @param <T>   a subclass of modelObject
     * @return the object
     * @throws com.mindalliance.channels.model.NotFoundException
     *          when not found
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
     * Find all type entities of a given class.
     *
     * @param clazz a class of entities
     * @return a list of entities
     */
    <T extends ModelEntity> List<T> listTypeEntities( Class<T> clazz );

    /**
     * Find all actual entities of a given class.
     *
     * @param clazz a class of entities
     * @return a list of entities
     */
    <T extends ModelEntity> List<T> listActualEntities( Class<T> clazz );


    /**
     * Get all referenced objects (known immutable or not orphaned) of the given class.
     *
     * @param clazz the given subclass of model object.
     * @param <T>   a subclass of model entity.
     * @return a list
     */
    <T extends ModelEntity> List<T> listReferencedEntities( Class<T> clazz );

    /**
     * Get all entities that narrow or equal a given entity.
     *
     * @param entity an entity
     * @return a list of entities
     */
    <T extends ModelEntity> List<T> listEntitiesNarrowingOrEqualTo( final T entity );

    /**
     * Iterate on referenced entities, going through referenced entities first and then referencing entities.
     *
     * @return an iterator on  entities
     */
    Iterator<ModelEntity> iterateEntities();

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
     * Last plan segment will not be deleted.
     *
     * @param object the object
     */
    void remove( ModelObject object );

    /**
     * @return a default plan segment
     */
    Segment getDefaultSegment();

    /**
     * Find an entity type by name. If none, create it for given domain,
     * renaming it to avoid conflicts if needed.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @return the object or null if name is null or empty
     */
    <T extends ModelEntity> T safeFindOrCreateType( Class<T> clazz, String name );

    /**
     * Find an entity type by name. If none, create it for given domain.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @return the object or null if name is null or empty
     */
    <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name );

    /**
     * Find an entity type by name. If none, create it for given domain.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @param id    a long
     * @return the object or null if name is null or empty
     */
    <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name, Long id );

    /**
     * Find an actual entity type by given name. If none, create it for given domain,
     * renaming it to avoid conflicts if needed.
     * If id is not null, assign the entity the given id if created.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @param id    an id
     * @param <T>   a subclass of model object
     * @return the object or null if name is null or empty
     */
    <T extends ModelEntity> T safeFindOrCreateType( Class<T> clazz, String name, Long id );

    /**
     * Find an actual entity by given name. If none, create it for given domain,
     * renaming it to avoid conflicts if needed.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @param <T>   a subclass of model object
     * @return the object or null if name is null or empty
     */
    <T extends ModelEntity> T safeFindOrCreate( Class<T> clazz, String name );

    /**
     * Find an actual entity by given name. If none, create it for given domain,
     * renaming it to avoid conflicts if needed.
     * If id is not null, assign the entity the given id if created.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @param id    an id
     * @param <T>   a subclass of model object
     * @return the object or null if name is null or empty
     */
    <T extends ModelEntity> T safeFindOrCreate( Class<T> clazz, String name, Long id );

    /**
     * Find an actual entity by given name. If none, create it.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @param <T>   a subclass of model object
     * @return the object or null if name is null or empty
     */
    <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name );

    /**
     * Find an entity by given name. If none, create it and give it provided id.
     * If entity can only be a type, find or create a type, else find or create an actual.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @param id    a long
     * @param <T>   a subclass of model object
     * @return the entity or null if name is null or empty
     */
    <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name, Long id );

    /**
     * Whether an entity already exists.
     *
     * @param clazz a class of entity
     * @param name  a string
     * @param kind  actual or type
     * @return a boolean
     */
    boolean entityExists( Class<? extends ModelEntity> clazz, String name, ModelEntity.Kind kind );

    /**
     * Create a connector in a plan segment.
     *
     * @param segment the plan segment
     * @return the new connector
     */
    Connector createConnector( Segment segment );

    /**
     * Create a connector in a plan segment, with prior id.
     *
     * @param segment the plan segment
     * @param id      a Long
     * @return the new connector
     */
    Connector createConnector( Segment segment, Long id );

    /**
     * Create a new part in a plan segment with a new id.
     *
     * @param segment the plan segment
     * @return a new default part.
     */
    Part createPart( Segment segment );

    /**
     * Create a new part in a plan segment with given id.
     *
     * @param segment the plan segment
     * @param id      a Long
     * @return a new default part.
     */
    Part createPart( Segment segment, Long id );


    /**
     * Create a flow between two nodes in this plan segment, or between a node in this plan segment and a
     * connector in another plan segment.
     *
     * @param source the source node.
     * @param target the target node.
     * @param name   the name of the flow, possibly empty
     * @return a new flow.
     * @throws IllegalArgumentException when nodes are already connected or nodes are not both
     *                                  in this plan segment, or one of the node isn't a connector in a different plan segment.
     */
    Flow connect( Node source, Node target, String name );

    /**
     * Create a flow between two nodes in this plan segment, or between a node in this plan segment and a
     * connector in another plan segment. Use provided id.
     *
     * @param source the source node.
     * @param target the target node.
     * @param name   the name of the flow, possibly empty
     * @param id     a long
     * @return a new flow.
     * @throws IllegalArgumentException when nodes are already connected or nodes are not both
     *                                  in this plan segment, or one of the node isn't a connector in a different plan segment.
     */
    Flow connect( Node source, Node target, String name, Long id );

    /**
     * Create a new plan segment with new id.
     *
     * @return the new plan segment.
     */
    Segment createSegment();

    /**
     * Create a new plan segment with old id.
     *
     * @param id a long
     * @return the new plan segment.
     */
    Segment createSegment( Long id );

    /**
     * Create a new plan segment with given id and given id for default part.
     *
     * @param id            a long
     * @param defaultPartId a long
     * @return the new plan segment.
     */
    Segment createSegment( Long id, Long defaultPartId );

    /**
     * Get all non-empty resource specs, user-entered or not.
     *
     * @return a new list of resource spec
     */
    List<ResourceSpec> findAllResourceSpecs();

    /**
     * Find all resources that equal or narrow given resource
     *
     * @param specable
     * @return a list of implied resources
     */
    List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( Specable specable );

    /**
     * Find all non-empty resources that equal or broaden given resource
     *
     * @param resourceSpec a resource
     * @return a list of implied resources
     */
    List<ResourceSpec> findAllResourcesBroadeningOrEqualTo( ResourceSpec resourceSpec );

    /**
     * Find all flows in all plan segments where the part applies as specified (as source or target).
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
    List<Play> findAllPlays( Specable resourceSpec );

    /**
     * Find all plays for the resource
     *
     * @param resourceSpec a resource
     * @param specific     whether the plays are specific to the resourceSpec
     * @return a list of plays
     */
    List<Play> findAllPlays( Specable resourceSpec, boolean specific );

    /**
     * Find all contact of specified resources
     *
     * @param specable
     *@param isSelf       find resources specified by spec, or else who specified resources need to know @return a list of ResourceSpec's
     */
    List<ResourceSpec> findAllContacts( Specable specable, boolean isSelf );

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
     * Find all flows across all plan segments that contact a matching part.
     *
     * @param resourceSpec a resource specification
     * @return a list of flows
     */
    List<Flow> findAllFlowsContacting( ResourceSpec resourceSpec );

    /**
     * Find all known and non-archetype, actual (non-type) actors that belong to a resource spec.
     *
     * @param resourceSpec a resource spec
     * @return a list of actors
     */
    List<Actor> findAllActualActors( ResourceSpec resourceSpec );

    /**
     * Find all known,, actual (non-type) organizations that belong to a resource spec.
     *
     * @param resourceSpec a resource spec
     * @return a list of organizations
     */
    List<Organization> findAllActualOrganizations( ResourceSpec resourceSpec );

    /**
     * Make a replicate of the flow.
     *
     * @param flow   the flow to replicate
     * @param isSend whether to replicate as send or receive
     * @return a created flow
     */
    Flow replicate( Flow flow, boolean isSend );

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
    List<String> findAllEntityNames( Class<? extends ModelEntity> aClass );

    /**
     * Find all names, sorted, of known instances of a model object class.
     *
     * @param aClass a model entity class
     * @param kind   a kind of entity
     * @return a list of strings
     */
    List<String> findAllEntityNames( Class<? extends ModelEntity> aClass, ModelEntity.Kind kind );

    /**
     * Find actors in given organization and role.
     *
     * @param organization the organization, possibly Organization.UNKNOWN
     * @param role         the role, possibly Role.UNKNOWN
     * @return a sorted list of actors
     */
    List<Actor> findActualActors( Organization organization, Role role );

    /**
     * Find all roles in given organization and across all plan segment.
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
     * Find all jobs of an actor in an organization or all organizations
     *
     * @param organization an organization or null (for all)
     * @param actor        an actor
     * @return a list of jobs
     */
    List<Job> findAllJobs( Organization organization, Actor actor );

    /**
     * Whether the model object is referenced in another model object.
     *
     * @param mo a model object
     * @return a boolean
     */
    Boolean isReferenced( ModelObject mo );

    /**
     * Whether a classification is referenced within the plan.
     *
     * @param classification a classification
     * @return a boolean
     */
    Boolean isReferenced( Classification classification );

    /**
     * Called when application is terminated.
     */
    void onDestroy();

    /**
     * Find all actual entities of a given class that are involved in the execution of tasks in a segment.
     *
     * @param entityClass a model entity class
     * @param segment     a segment
     * @param kind        actual or type
     * @return a list of model entities
     */
    <T extends ModelEntity> List<T> listEntitiesTaskedInSegment(
            Class<T> entityClass,
            Segment segment,
            ModelEntity.Kind kind );

    /**
     * Find all confirmed jobs with resource spec
     *
     * @param specable
     * @return a list of jobs
     */
    List<Job> findAllConfirmedJobs( Specable specable );

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
     * Whether the plan segment can ever start.
     *
     * @param segment a plan segment
     * @return a boolean
     */
    boolean findIfSegmentStarted( Segment segment );

    /**
     * Find all parts in the plan.
     *
     * @return a list of parts
     */
    List<Part> findAllParts();

    /**
     * Find all parts that has the specified resource.
     *
     * @param segment      a plan segment
     * @param specable
     *@param exactMatch   a boolean @return a list of parts
     */
    List<Part> findAllParts( Segment segment, Specable specable, boolean exactMatch );

    /**
     * Find all parts located at a given place.
     *
     * @param place a place
     * @return a list of parts
     */
    List<Part> findAllPartsWithExactLocation( Place place );

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
     * Find all connectors of capabilities that match a need.
     *
     * @param need a flow
     * @return a list of connectors
     */
    List<Connector> findAllSatificers( Flow need );

    /**
     * Find all parts in a plan that can start the given plan segment
     * because of the event they cause or the event phases they terminate.
     *
     * @param segment a plan segment
     * @return a list of parts
     */
    List<Part> findInitiators( Segment segment );

    /**
     * Find all parts in a plan that can terminate the given plan segment
     * because of the event they cause or the event phases they terminate.
     *
     * @param segment a plan segment
     * @return a list of parts
     */
    List<Part> findTerminators( Segment segment );

    /**
     * Find all external parts in a plan that can terminate the given plan segment
     * because of the event they cause or the event phases they terminate.
     *
     * @param segment a plan segment
     * @return a list of parts
     */
    List<Part> findExternalTerminators( Segment segment );

    /**
     * Whether a task in another plan segment intiates a given plan segment.
     *
     * @param segment a plan segment
     * @return a boolean
     */
    boolean isInitiated( Segment segment );

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
     * @param segment a plan segment
     * @param goal    a goal
     * @return a list of parts
     */
    List<Part> findAchievers( Segment segment, Goal goal );

    /**
     * Find plan segment in which an actor is involved.
     *
     * @param actor an actor
     * @return a list of plan segments
     */
    List<Segment> findSegments( Actor actor );

    /**
     * Find all actors participating in a plan segment.
     *
     * @param segment the plan segment
     * @return a sorted list of actors.
     */
    List<Actor> findActualActors( Segment segment );

    /**
     * Find all organizations participating in a plan segment.
     *
     * @param segment the plan segment
     * @return a sorted list of organizations.
     */
    List<Organization> findActualOrganizations( Segment segment );

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
    List<Employment> findAllEmploymentsWithKnownActors();

    /**
     * FInd all employments in actual or type or organization.
     *
     * @param organization an organization
     * @return a list of employments
     */
    List<Employment> findAllEmploymentsIn( Organization organization );

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
     * FInd all flows in the plan.
     *
     * @return a list of flows
     */
    List<Flow> findAllFlows();

    /**
     * Find all distinct flow names.
     *
     * @return a list of strings
     */
    List<String> findAllFlowNames();

    /**
     * List all entities of a given class, plus the unmnown entity of this class.
     *
     * @param clazz a class extending ModelObject
     * @return a list of entities
     */
    <T extends ModelEntity> List<T> listEntitiesWithUnknown( Class<T> clazz );

    /**
     * Find all roles played by an actor.
     *
     * @param actor an actor
     * @return a list of roles
     */
    List<Role> findAllRolesOf( Actor actor );

    /**
     * Find all actors in an organization or type of organization.
     *
     * @param organization an organization
     * @return a list of organizations
     */
    List<Actor> findAllActorsInOrganization( Organization organization );

    /**
     * Find all parts and flows that directly involve a given entity.
     *
     * @param entity a model entity
     * @return a list of model objects (parts and flows)
     */
    List<ModelObject> findAllSegmentObjectsInvolving( ModelEntity entity );

    /**
     * Find all plan segments that respond to a given event.
     *
     * @param event an event
     * @return a list of plan segments
     */
    List<Segment> findSegmentsRespondingTo( Event event );

    /**
     * Find all parts that terminate a plan segment's event.
     *
     * @param segment a plan segment
     * @return a list of parts
     */
    List<Part> findPartsTerminatingEventPhaseIn( Segment segment );

    /**
     * Find all parts initiating a given event.
     *
     * @param event an event
     * @return a list of parts
     */
    List<Part> findPartsInitiatingEvent( Event event );

    /**
     * Find all parts that start with a plan segment's event.
     *
     * @param segment a plan segment
     * @return a list of parts
     */
    List<Part> findPartsStartingWithEventIn( Segment segment );

    /**
     * Find all model objects.
     *
     * @return a list of model objects
     */
    List<ModelObject> findAllModelObjects();

    /**
     * Find all model objects of a given class.
     *
     * @param clazz a model object class
     * @return a list of model objects
     */
    <T extends ModelObject> List<T> findAllModelObjects( Class<T> clazz );

    /**
     * Find all geonames in plan.
     *
     * @return a list of strings
     */
    List<String> findAllGeonames();

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
     * @param clazz a model object class
     * @return a list of model objects
     */
    <T extends ModelObject> List<T> findAllReferencesTo( Place place, Class<T> clazz );

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
     * Whether two string reach a given level of semantic proximity.
     *
     * @param text      a string
     * @param otherText a string
     * @param proximity a proximity level
     * @return a boolean
     */
    boolean isSemanticMatch( String text, String otherText, Proximity proximity );

    /**
     * Whether two texts have high semantic proximity.
     *
     * @param text      a string
     * @param otherText a string
     * @return a boolean
     */
    boolean likelyRelated( String text, String otherText );

    /**
     * Find all sharing flows addressing a given information need.
     *
     * @param need a flow
     * @return a list of flows
     */
    List<Flow> findAllSharingsAddressingNeed( Flow need );

    /**
     * Find parts with anonymous tasks which resourceSpec is narrowed by that of a given part.
     *
     * @param part a part
     * @return a list of parts
     */
    List<Part> findAnonymousPartsMatching( Part part );

    /**
     * Calculate how important a part is in terms of the goals it helps achieve directly or indirectly.
     *
     * @param part a part
     * @return an issue level
     */
    Level computePartPriority( Part part );

    /**
     * Calculate how important a flow is in terms of the goals it helps achieve directly or indirectly.
     *
     * @param flow a flow
     * @return an issue level
     */
    Level computeSharingPriority( Flow flow );

    /**
     * Find all goals impacted by the failure of a part.
     *
     * @param part a part
     * @return a list of goals
     */
    List<Goal> findAllGoalsImpactedByFailure( Part part );

    /**
     * Get current plan.
     *
     * @return a plan
     */
    Plan getCurrentPlan();

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
     * Given his/her username find the user's email.
     *
     * @param userName a string
     * @return a string
     */
    String findUserEmail( String userName );

    /**
     * Given his/her username find the user's role.
     *
     * @param userName a string
     * @return a string
     */
    String findUserRole( String userName );

    /**
     * Given his/her username find the user's normalized full name.
     *
     * @param userName a string
     * @return a string
     */
    String findUserNormalizedFullName( String userName );

    /**
     * Find all plan segments for a given phase.
     *
     * @param phase a phase
     * @return a list of plan segments
     */
    List<Segment> findAllSegmentsForPhase( Phase phase );

    /**
     * Find all parts that cause an event.
     *
     * @param event an event
     * @return a list of parts
     */
    List<Part> findCausesOf( Event event );

    /**
     * Find all entities in a given place.
     *
     * @param place a place
     * @return a list of entities
     */
    List<? extends ModelEntity> findAllEntitiesIn( Place place );

    /**
     * Find all entities in a given phase.
     *
     * @param phase a phase
     * @return a list of entities
     */
    List<? extends ModelEntity> findAllEntitiesIn( Phase phase );

    /**
     * Find all model objects of a given class that reference a given model object.
     *
     * @param mo    a model object
     * @param clazz a model object class
     * @return a list of model objects
     */
    <T extends ModelObject> List<T> findAllReferencing( ModelObject mo, Class<T> clazz );

    /**
     * Find all entities of a given class that reference an entity type.
     *
     * @param entityType  a model entity that's a type
     * @param entityClass a class of entities
     * @return a list of entities
     */
    <T extends ModelEntity> List<T> findAllEntitiesReferencingType( ModelEntity entityType, Class<T> entityClass );

    /**
     * Find all flows that reference a model entity type.
     *
     * @param entityType a model entity that's a type
     * @return a list of flows
     */
    List<Part> findAllPartsReferencingType( ModelEntity entityType );

    /**
     * Find all entities equal or narrowing another.
     *
     * @param entity a model entity
     * @return a list of model entities
     */
    List<? extends ModelEntity> findAllNarrowingOrEqualTo( ModelEntity entity );

    /**
     * Whether an organization plays at least on part in the plan.
     *
     * @param organization an organization
     * @return a boolean
     */
    Boolean isInvolved( Organization organization );

    /**
     * Whether an organization is expected to play at least one part in the plan.
     *
     * @param organization an organization
     * @return a boolean
     */
    Boolean isInvolvementExpected( Organization organization );

    /**
     * Find all parts played by an organization or one of its children.
     *
     * @param organization an organization
     * @return a list of parts
     */
    List<Part> findAllPartsPlayedBy( Organization organization );

    /**
     * Find all assignments that match a part.
     *
     * @param part                 a part
     * @param includeUnknownActors whether to include assignment of unknown actors
     * @return a list of assignments
     */
    List<Assignment> findAllAssignments( Part part, Boolean includeUnknownActors );

    /**
     * Find all assignments for an actor.
     *
     * @param actor an actor
     * @return a list of assignments
     */
    List<Assignment> findAllAssignments( Actor actor );

    /**
     * Find all assignments for an organization.
     *
     * @param org an organization
     * @return a list of assignments
     */
    List<Assignment> findAllAssignments( Organization org );

    /**
     * Find all assignments for an actor for a segment..
     *
     * @param actor   an actor
     * @param segment segment
     * @return a list of assignments
     */
    List<Assignment> findAllAssignments( Actor actor, Segment segment );

    /**
     * Find all commitments implied by a sharing flow.
     *
     * @param flow a flow
     * @return a list of commitments
     */
    List<Commitment> findAllCommitments( Flow flow );

    /**
     * Find all commitments of an actor.
     *
     * @param actor an actor
     * @return a list of commitments
     */
    List<Commitment> findAllCommitmentsOf( Actor actor );

    /**
     * Find all commitments of an organization.
     *
     * @param organization an organization
     * @return a list of commitments
     */
    List<Commitment> findAllCommitmentsOf( Organization organization );

    /**
     * Find all commitments to an actor.
     *
     * @param actor an actor
     * @return a list of commitments
     */
    List<Commitment> findAllCommitmentsTo( Actor actor );

    /**
     * Find all unconnected need-capability pairs given a part's needs.
     *
     * @param part a part
     * @return a list of arrays of flows (pairs)
     */
    List<Flow[]> findUntappedSatisfactions( Part part );

    /**
     * Find all agreements implied by commitments from an organization.
     *
     * @param organization an organization
     * @return a list of agreements
     */
    List<Agreement> findAllImpliedAgreementsOf( Organization organization );

    /**
     * Find all commitments covered by an agreement by an organization.
     *
     * @param agreement    an agreement
     * @param organization an organization
     * @return a list of commitments
     */
    List<Commitment> findAllCommitmentsCoveredBy(
            Agreement agreement,
            Organization organization );

    /**
     * Find essential flows from a part.
     *
     * @param part        a part
     * @param assumeFails boolean whether downstream alternate flows assumed to fail
     * @return a list of flows
     */
    List<Flow> findEssentialFlowsFrom( Part part, boolean assumeFails );

    /**
     * If a part or sharing flow fail, what risk mitigating parts would also fail?
     *
     * @param segmentObject a part of sharing flow
     * @param assumeFails   whether all alternate sharing flows are assumed to fail (no redundancy)
     * @return a list of risk-mitigating parts that would fail
     */
    List<Part> findFailureImpacts( SegmentObject segmentObject, boolean assumeFails );

    /**
     * Find all actual entities matching an entity type.
     *
     * @param entityClass a class of enities
     * @param entityType  an entity type
     * @return a list of actual entities
     */
    <T extends ModelEntity> List<T> findAllActualEntitiesMatching(
            Class<T> entityClass,
            final T entityType );

    /**
     * Find all parts assigned to an actor.
     *
     * @param actor an actor
     * @return a list of parts
     */
    List<Part> findAllAssignedParts( Actor actor );

    /**
     * Find all parts assigned to an actor in a segment.
     *
     * @param segment a plan segment
     * @param actor   an actor
     * @return a list of parts
     */
    List<Part> findAllAssignedParts( Segment segment, Actor actor );

    /**
     * Find all employments where actors are directly or indirectly supervised by a given actor.
     *
     * @param actor an actor
     * @return a list of actors
     */
    List<Employment> findAllSupervisedBy( Actor actor );

    /**
     * Get file user details service.
     *
     * @return a file user details service
     */
    UserService getUserService();

    /**
     * Find the participation by a user.
     *
     * @param username a user name
     * @return a participation or null
     */
    Participation findParticipation( String username );

    /**
     * Whether an agreement covers a sharing commitment.
     *
     * @param agreement  the agreement
     * @param commitment a commitment
     * @return a boolean
     */
    Boolean covers( Agreement agreement, Commitment commitment );

    /**
     * Whether an agreement encompasses another.
     *
     * @param agreement the agreement
     * @param other     the other agreement
     * @return a boolean
     */
    Boolean encompasses( Agreement agreement, Agreement other );

    /**
     * Whether there are common EOIs in two free-form texts.
     *
     * @param flow      a flow
     * @param otherFlow a flow
     * @return a boolean
     */
    Boolean hasCommonEOIs( Flow flow, Flow otherFlow );

    /**
     * Whether none in a list eois is without a strong match with some in another list.
     *
     * @param eois     a list of elements of information
     * @param superset a list of elements of information
     * @return a boolean
     */
    Boolean subsetOf(
            List<ElementOfInformation> eois, List<ElementOfInformation> superset );

    <T extends ModelEntity> T retrieveEntity(
            Class<T> entityClass, Map<String, Object> state, String key );

    boolean isExecutedBy( Part part, ModelEntity entity );

    @SuppressWarnings( "unchecked" )
    List<ModelEntity> findEntities(
            Segment segment,
            Class entityClass,
            ModelEntity.Kind kind );

    /**
     * Get alternate flows.
     *
     * @param flow a flow
     * @return a list of flows @param flow
     */
    List<Flow> getAlternates( Flow flow );

    /**
     * Instantiate a gaol from a serialization map.
     *
     * @param map a map
     * @return a goal
     */
    Goal goalFromMap( Map<String, Object> map );

    /**
     * Find the family relationship from an organization to another.
     *
     * @param fromOrg an organization
     * @param toOrg   an organization
     * @return a organizational family relationship
     */
    Organization.FamilyRelationship findFamilyRelationship(
            Organization fromOrg,
            Organization toOrg );

    /**
     * Remove entity with old name if not referenced and if not defined.
     *
     * @param clazz a model object class
     * @param name  a string
     * @return a boolean - true if the entity was deleted
     */
    boolean cleanup( Class<? extends ModelObject> clazz, String name );
}
