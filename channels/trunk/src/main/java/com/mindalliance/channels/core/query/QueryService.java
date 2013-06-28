package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Agreement;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Dissemination;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.model.Tag;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.db.services.users.UserRecordService;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Data query interface.
 */
public interface QueryService {

    /**
     * Whether the flow could be essential to risk mitigation.
     *
     * @param flow        the flow
     * @param assumeFails whether alternate flows are assumed
     * @return a boolean
     */
    Boolean isEssential( Flow flow, boolean assumeFails );

    /**
     * Get the persistence store accessor.
     *
     * @return the dao
     */
    PlanDao getDao();

    /**
     * Get the plan manager.
     *
     * @return the plan manager.
     */
    PlanManager getPlanManager();

    /**
     * Get default locale.
     * @return a place
     */
    Place getPlanLocale();

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
     * Get all objects of the given class.
     *
     * @param clazz the given subclass of model entity.
     * @param <T>   a subclass of model entity.
     * @return a list
     */
    <T extends ModelEntity> List<T> listKnownEntities( Class<T> clazz );

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
     * Find all actual entities of a given class.
     *
     * @param clazz a class of entities
     * @return a list of entities
     */
    <T extends ModelEntity> List<T> listActualEntities( Class<T> clazz, boolean mustBeReferenced );


    /**
     * Get all referenced objects (known immutable or not orphaned) of the given class.
     *
     * @param clazz the given subclass of model object.
     * @param <T>   a subclass of model entity.
     * @return a list
     */
    <T extends ModelEntity> List<T> listReferencedEntities( Class<T> clazz );

    /**
     * List all known entities, possibly restricted to those referenced by other entities.
     * @param entityClass an entity class
     * @param mustBeReferenced whether it must be referenced to be listed
     * @param includeImmutables as always referenced
     * @param <T> a model entity class
     * @return a list of model entities
     */
    <T extends ModelEntity> List<T> listKnownEntities(
            Class<T> entityClass,
            Boolean mustBeReferenced,
            Boolean includeImmutables );

    /**
     * Get all entities that narrow or equal a given entity.
     *
     * @param entity an entity
     * @return a list of entities
     */
    <T extends ModelEntity> List<T> listEntitiesNarrowingOrEqualTo( T entity );

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
     * @param <T>   a subclass of model object
     * @return the object or null if name is null or empty
     */
    <T extends ModelEntity> T safeFindOrCreateType( Class<T> clazz, String name );

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
    Boolean entityExists( Class<? extends ModelEntity> clazz, String name, ModelEntity.Kind kind );


    /**
     * Find an actual entity by name, if it exists.
     *
     * @param entityClass a model entity class
     * @param name        a string
     * @param <T>         a subclass of model entity
     * @return a model entity or null
     */
    <T extends ModelEntity> T findActualEntity( Class<T> entityClass, String name );

    /**
     * Find an entity type by name, if it exists.
     *
     * @param entityClass a model entity class
     * @param name        a string
     * @param <T>         a subclass of model entity
     * @return a model entity or null
     */
    <T extends ModelEntity> T findEntityType( Class<T> entityClass, String name );


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
     * Find all flows in all plan segments where the part applies as specified (as source or target).
     *
     * @param resourceSpec a resource spec
     * @param asSource     a boolean
     * @return a list of flows
     */
    List<Flow> findAllRelatedFlows( ResourceSpec resourceSpec, Boolean asSource );

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
    List<Play> findAllPlays( Specable resourceSpec, Boolean specific );

    /**
     * Find all contact of specified resources
     *
     * @param specable a specable
     * @param isSelf   find resources specified by spec, or else who specified resources need to know @return a list of ResourceSpec's
     * @return a list of resource specs
     */
    List<ResourceSpec> findAllContacts( Specable specable, Boolean isSelf );

    /**
     * Find all user issues about a model object
     *
     * @param identifiable an object with an id
     * @return list of issues
     */
    List<UserIssue> findAllUserIssues( ModelObject identifiable );

    /**
     * Find all relevant channels for a given resource spec.
     *
     * @param spec the spec
     * @return the channels
     */
    List<Channel> findAllChannelsFor( ResourceSpec spec );

    /**
     * Find all known and non-archetype, actual (non-type) actors that belong to a resource spec.
     *
     * @param resourceSpec a resource spec
     * @return a list of actors
     */
    List<Actor> findAllActualActors( ResourceSpec resourceSpec );

    /**
     * Find all jobs for an organization that are implied by parts but not confirmed.
     *
     * @param organization an organization
     * @return a list of jobs
     */
    List<Job> findUnconfirmedJobs( Organization organization );

    /**
     * Find all actors that directly or indirectly are supervised by a given actor.
     *
     * @param supervisor an actor supervising
     * @return a list of actors supervised
     */
    List<Actor> findSupervised( Actor supervisor );

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
     * Count the number of model objects referencing another.
     *
     * @param mo a model object
     * @return an integer
     */
    Integer countReferences( ModelObject mo );

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
     * Find all actual entities (actors, roles or organizations) of a given class
     * that are involved in the execution of tasks in a segment.
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
     * @param specable a specable
     * @return a list of jobs
     */
    List<Job> findAllConfirmedJobs( Specable specable );

    /**
     * Find all organizations employing a given actor.
     *
     * @param actor an actor
     * @return a list of organizations
     */
    List<Organization> findEmployers( Actor actor );

    /**
     * Find direct and indirect employers given employments.
     * @param employments  a list of employments
     * @return  a list of organziations
     */
    List<Organization> findDirectAndIndirectEmployers( List<Employment> employments );

    /**
     * Find if part is ever started.
     *
     * @param part a part
     * @return a boolean
     */
    Boolean findIfPartStarted( Part part );

    /**
     * Whether the plan segment can ever start.
     *
     * @param segment a plan segment
     * @return a boolean
     */
    Boolean findIfSegmentStarted( Segment segment );

    /**
     * Find all parts in the plan.
     *
     * @return a list of parts
     */
    List<Part> findAllParts();

    /**
     * Find all parts that has the specified resource.
     *
     * @param segment    a plan segment
     * @param specable   a specable
     * @param exactMatch a boolean @return a list of parts
     * @return a list of parts
     */
    List<Part> findAllParts( Segment segment, Specable specable, Boolean exactMatch );

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
    Boolean isInitiated( Segment segment );

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
     * Find all sharing flows in plan or segment.
     *
     * @param segment null or a segment
     * @return a list of flows
     */
    List<Flow> findAllSharingFlows( Segment segment );

    /**
     * Find all roles played by an actor.
     *
     * @param actor an actor
     * @return a list of roles
     */
    List<Role> findAllRolesOf( Actor actor );

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
     * Find all delegated media for a medium.
     *
     * @param medium a medium
     * @return a list of model objects
     */
    List<? extends ModelObject> findAllModelObjectsIn( TransmissionMedium medium );

    /**
     * Find all model objects directly impacted by or impacting an event.
     *
     * @param event an event
     * @return a list of model objects
     */
    List<ModelObject> findAllModelObjectsDirectlyRelatedToEvent( Event event );

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
     * Whether two texts have high semantic proximity.
     *
     * @param text      a string
     * @param otherText a string
     * @return a boolean
     */
    Boolean likelyRelated( String text, String otherText );

    /**
     * Whether two tags have high semantic proximity.
     *
     * @param tag   a tag
     * @param other a tag
     * @return a boolean
     */
    Boolean likelyRelated( Tag tag, Tag other );

    /**
     * Find all sharing flows addressing a given information need.
     *
     * @param need a flow
     * @return a list of flows
     */
    List<Flow> findAllSharingsAddressingNeed( Flow need );

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
     * Find all parts that terminate an event.
     *
     * @param event an event
     * @return a list of parts
     */
    List<Part> findTerminatorsOf( Event event );

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
     * Find all entities in a given medium.
     *
     * @param medium a medium
     * @return a list of entities
     */
    List<? extends ModelEntity> findAllEntitiesIn( TransmissionMedium medium );

    /**
     * Find all model objects of a given class that reference a given model object.
     *
     * @param mo    a model object
     * @param clazz a model object class
     * @return a list of model objects
     */
    <T extends ModelObject> List<T> findAllReferencing( ModelObject mo, Class<T> clazz );

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
     * @param part            a part
     * @param includeUnknowns whether to include assignment of unknown actors
     * @return a list of assignments
     */
    List<Assignment> findAllAssignments( Part part, Boolean includeUnknowns );

    /**
     * Find all assignments that match a part, including prohibited.
     *
     * @param part              a part
     * @param includeProhibited a boolean
     * @param includeUnknowns   whether to include assignment of unknown actors
     * @return a list of assignments
     */
    List<Assignment> findAllAssignments( Part part, Boolean includeUnknowns, Boolean includeProhibited );

    /**
     * Find all commitments in the plan.
     *
     * @return a list of commitments
     */
    List<Commitment> findAllCommitments();

    /**
     * Find all commitments in the plan.
     *
     * @param includeToSelf a boolean
     * @return a list of commitments
     */
    List<Commitment> findAllCommitments( Boolean includeToSelf );

    /**
     * Find all commitments.
     *
     * @param allowCommitmentsToSelf a boolean
     * @param includeUnknowns        a boolean
     * @return a list of commitments
     */
    List<Commitment> findAllCommitments( Boolean allowCommitmentsToSelf, Boolean includeUnknowns );

    /**
     * Find all commitments to others implied by a sharing flow.
     *
     * @param flow a flow
     * @return a list of commitments
     */
    List<Commitment> findAllCommitments( Flow flow );

    /**
     * Find all commitments implied by a sharing flow.
     *
     * @param flow                   a flow
     * @param allowCommitmentsToSelf a boolean
     * @return a list of commitments
     */
    List<Commitment> findAllCommitments( Flow flow, Boolean allowCommitmentsToSelf );

    /**
     * Find all commitments implied by a sharing flow.
     *
     * @param flow                   a flow
     * @param allowCommitmentsToSelf a boolean
     * @param includeUnknowns        a boolean
     * @return a list of commitments
     */
    List<Commitment> findAllCommitments( Flow flow, Boolean allowCommitmentsToSelf, Boolean includeUnknowns );

    /**
     * Find all commitments implied by a sharing flow.
     *
     * @param flow        a flow
     * @param selfCommits a boolean
     * @param assignments assignments under consideration
     * @return a list of commitments
     */
    List<Commitment> findAllCommitments( Flow flow, Boolean selfCommits, Assignments assignments );

    /**
     * Find all commitments intermediated by a flow.
     *
     * @param flow a flow
     * @return a list of commitments
     */
    List<Commitment> findAllBypassCommitments( Flow flow );


    List<Commitment> findAllCommitmentsOf(
            Specable specable,
            Assignments assignments,
            List<Flow> allFlows );


    List<Commitment> findAllCommitmentsTo(
            Specable specable,
            Assignments assignments,
            List<Flow> allFlows );

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
     * @param assignments  assignments
     * @param flows        a list of flows
     * @return a list of agreements
     */
    List<Agreement> findAllImpliedAgreementsOf(
            Organization organization,
            Assignments assignments,
            List<Flow> flows );

    /**
     * Whether a commitment is agreed to if required.
     *
     * @param commitment a commitment
     * @return a Boolean
     */
    Boolean isAgreedToIfRequired( Commitment commitment );

    /**
     * Find all commitments covered by an agreement by an organization.
     *
     * @param agreement    an agreement
     * @param organization an organization
     * @param assignments  assignments
     * @param allFLows     list of flows
     * @return a list of commitments
     */
    List<Commitment> findAllCommitmentsCoveredBy(
            Agreement agreement,
            Organization organization,
            Assignments assignments,
            List<Flow> allFLows );

    /**
     * Find all confirmed agreements that cover an information sharing commitment.
     *
     * @param commitment an information sharing commitment
     * @return a list of agreements
     */
    List<Agreement> findAllConfirmedAgreementsCovering( Commitment commitment );

    /**
     * Find essential flows from a part.
     *
     * @param part                 a part
     * @param assumeAlternatesFail boolean whether downstream alternate flows assumed to fail
     * @return a list of flows
     */
    List<Flow> findEssentialFlowsFrom( Part part, Boolean assumeAlternatesFail );

    /**
     * If a part or sharing flow fail, what risk mitigating parts would also fail?
     *
     * @param segmentObject a part of sharing flow
     * @param assumeFails   whether all alternate sharing flows are assumed to fail (no redundancy)
     * @return a list of risk-mitigating parts that would fail
     */
    List<Part> findFailureImpacts( SegmentObject segmentObject, Boolean assumeFails );

    /**
     * Find all actual entities matching an entity type.
     *
     * @param entityClass a class of enities
     * @param entityType  an entity type
     * @return a list of actual entities
     */
    <T extends ModelEntity> List<T> findAllActualEntitiesMatching(
            Class<T> entityClass,
            T entityType );

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
     * @return a persistent  user details service
     */
    UserRecordService getUserInfoService();


    /**
     * Whether a commitment is covered by an agreement.
     *
     * @param commitment a sharing commitment
     * @return a boolean
     */
    Boolean isCoveredByAgreement( Commitment commitment );

    /**
     * Whether a commitment requires an agreement.
     *
     * @param commitment a sharing commitment
     * @return a boolean
     */
    Boolean isAgreementRequired( Commitment commitment );

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
     * Whether none in a list eois is without a match with some in another list.
     *
     * @param eois     a list of elements of information
     * @param superset a list of elements of information
     * @return a boolean
     */
    Boolean subsetOf(
            List<ElementOfInformation> eois, List<ElementOfInformation> superset );

    <T extends ModelEntity> T retrieveEntity(
            Class<T> entityClass, Map<String, Object> state, String key );

    Boolean isExecutedBy( Part part, ModelEntity entity );

    List<ModelEntity> findTaskedEntities(
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

    /**
     * Find the EOIs that appear (or seem to) in two flows.
     * If need has no EOI (meaning "any"), take the capability's eois.
     *
     * @param capability a flow
     * @param need       another flow
     * @return a list of EOIs
     */
    List<ElementOfInformation> findCommonEOIs( Flow capability, Flow need );

    /**
     * Find all dissemination from or to a part or flow.
     *
     * @param segmentObject a part or flow
     * @param subject       a subject being disseminated
     * @param showTargets   a boolean
     * @return a list of disseminations
     */
    List<Dissemination> findAllDisseminations(
            SegmentObject segmentObject,
            Subject subject,
            Boolean showTargets );

    /**
     * Return the plan used by this service.
     *
     * @return a plan
     */
    Plan getPlan();

    /**
     * Find all employments for a part given a plan's locale.
     *
     * @param part   a part
     * @param locale a place
     * @return a list of employments
     */
    List<Employment> findAllEmployments( Part part, Place locale );

    /**
     * Get assignments factory.
     * Exclude assignments to unknown actors.
     *
     * @return assignments factory
     */
    Assignments getAssignments();

    /**
     * Get assignments factory.
     *
     * @param includeUnknowns a boolean
     * @return assignments factory
     */
    Assignments getAssignments( Boolean includeUnknowns );

    /**
     * Get assignments factory.
     * Exclude assignments to unknown actors.
     * Include prohibited.
     *
     * @param includeUnknowns   include assignments to unknown actors
     * @param includeProhibited a boolean
     * @return assignments factory
     */
    Assignments getAssignments( Boolean includeUnknowns, Boolean includeProhibited );

    /**
     * Find all tags in domain.
     *
     * @return a list of tags
     */
    List<Tag> findTagDomain();

    /**
     * Find all parts that override a given part.
     *
     * @param part  a part
     * @param parts a list of parts
     * @return a list of parts
     */
    List<Part> findAllOverridingParts( Part part, List<Part> parts );

    /**
     * Find all parts that are overridden by a given part.
     *
     * @param part  a part
     * @param parts a list of parts
     * @return a list of parts
     */
    List<Part> findAllOverriddenParts( Part part, List<Part> parts );

    /**
     * Whether part is overridden by another.
     *
     * @param part a part
     * @return a boolean
     */
    Boolean isOverridden( Part part );

    /**
     * Whether part is overriding another.
     *
     * @param part a part
     * @return a boolean
     */
    Boolean isOverriding( Part part );

    /**
     * Whether flow is overridden by another.
     *
     * @param flow a flow
     * @return a boolean
     */
    Boolean isOverridden( Flow flow );

    /**
     * Whether flow is overriding another.
     *
     * @param flow a flow
     * @return a boolean
     */
    Boolean isOverriding( Flow flow );

    /**
     * Find overridden sharing send flows from overridden parts.
     *
     * @param part a part
     * @return list of flows
     */
    List<Flow> findOverriddenSharingSends( Part part );

    /**
     * Find overridden sharing send flows from overridden parts.
     *
     * @param part a part
     * @return list of flows
     */
    List<Flow> findOverriddenSharingReceives( Part part );

    /**
     * Find all parts matching the task of a part.
     *
     * @param part a part
     * @return a list of parts
     */
    List<Part> findSynonymousParts( Part part );

    boolean allowsCommitment(
            Assignment committer, Assignment beneficiary, Place locale, Flow flow );

    /**
     * Find all parts that initiate an event timing.
     *
     * @param eventTiming an event timing (event + co- or post-event timing + level)
     * @return a list of parts
     */
    List<Part> findAllInitiators( EventTiming eventTiming );

    /**
     * Find all capabilities matching a given name.
     *
     * @param name a string
     * @return a list of flows
     */
    List<Flow> findAllCapabilitiesNamed( String name );


    /**
     * Whether this is a sharing flow where source actor is target actor.
     *
     * @param flow@return a boolean
     */
    boolean isSharingWithSelf( Flow flow );

    Actor getKnownActualActor( Part part );

    /**
     * Get extended title for the part.
     *
     * @param sep  separator string
     * @param part a part
     * @return a string
     */
    String getFullTitle( String sep, Part part );

    /**
     * Get all commitments in the plan.
     *
     * @return commitments
     */
    Commitments getAllCommitments();

    /**
     * Get all commitments in the plan.
     *
     * @param includeToSelf a boolean
     * @return commitments
     */
    Commitments getAllCommitments( Boolean includeToSelf );

    /**
     * Get all commitments in the plan.
     *
     * @param includeToSelf   a boolean
     * @param includeUnknowns a boolean
     * @return commitments
     */
    Commitments getAllCommitments( Boolean includeToSelf, Boolean includeUnknowns );


    /**
     * Find all eoi names used in the plan.
     *
     * @return a sorted list of strings
     */
    List<String> findAllEoiNames();

    /**
     * Find all supervisor actors of a given actor.
     * @param actor an actor
     * @return a list of supervising actors
     */
    List<Actor> findAllSupervisorsOf( Actor actor );

    /**
     * Find all supervisor actors of a given actor in non-placeholder organizations.
     * @param actor an actor
     * @return a list of supervising actors
     */
    List<Actor> findAllFixedSupervisorsOf( Actor actor );


    /**
     * Make a name for a new entity.
     * @param entityClass an entity class
     * @return a string
     */
    String makeNameForNewEntity( Class<? extends ModelEntity> entityClass );

    /**
     * Find all referenced placeholder organizations.
     * @return a list of organizations.
     */
    List<Organization> listPlaceholderOrganizations();

    /**
     * Find all referenced non-placeholder organizations.
     * @return a list of organizations.
     */
    List<Organization> listFixedOrganizations();

    /**
     * Whether a  model object exists of a given kind that had a given id at a certain date.
     * @param clazz a model object class
     * @param id the model object id
     * @param dateOfRecord the date the model object had that id
     * @return a boolean
     */
    boolean exists( Class<? extends ModelObject> clazz, Long id, Date dateOfRecord );

}
