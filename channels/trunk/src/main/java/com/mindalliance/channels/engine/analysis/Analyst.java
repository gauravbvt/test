/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.command.CommandListener;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelEntity.Kind;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.engine.imaging.ImagingService;

import java.util.List;

/**
 * Analyzes the plan and individual model elements.
 */
public interface Analyst extends CommandListener {

    /**
     * Whether to include issues that are property-specific.
     */
    boolean INCLUDE_PROPERTY_SPECIFIC = true;

    /**
     * Count the number of unwaived issues for a model object in a given test.
     *
     * @param queryService the query service
     * @param modelObject  a model object
     * @param test         a type of issue
     * @return an Integer
     */
    Integer countTestFailures( QueryService queryService, ModelObject modelObject, String test );

    /**
     * Find all issues on all model objects in the plan.
     *
     * @param queryService the query service
     * @return a list of issues.
     */
    List<Issue> findAllIssues( QueryService queryService );

    /**
     * Find all issues related to any of the components of a resource.
     *
     * @param queryService the query service
     * @param resource     a resource
     * @param specific     a boolean -- true -> equality match, false -> marrow or equals
     * @return a list of issues
     */
    List<Issue> findAllIssuesFor( QueryService queryService, ResourceSpec resource, Boolean specific );

    /**
     * Find all unwaived issues on all model objects in the plan.
     *
     * @param queryService the query service
     * @return a list of issues.
     */
    List<Issue> findAllUnwaivedIssues( QueryService queryService );

    /**
     * Find all waived issues on all model objects in the plan.
     *
     * @param queryService the query service
     * @return a list of issues.
     */
    List<Issue> findAllWaivedIssues( QueryService queryService );

    /**
     * Find the causes for the part being conceptual, if any.
     *
     * @param queryService the query service
     * @param part         a part
     * @return a list of strings
     */
    List<String> findConceptualCauses( QueryService queryService, Part part );

    /**
     * Find the causes for the flow being conceptual, if any.
     *
     * @param queryService the query service
     * @param flow         a flow
     * @return a list of strings
     */
    List<String> findConceptualCauses( QueryService queryService, Flow flow );

    /**
     * Find remediations for the part being conceptual, if any.
     *
     * @param queryService the query service
     * @param part         a part
     * @return a list of strings
     */
    List<String> findConceptualRemediations( QueryService queryService, Part part );

    /**
     * Find remediations for the flow being conceptual, if any.
     *
     * @param queryService the query service
     * @param flow         a flow
     * @return a list of strings
     */
    List<String> findConceptualRemediations( QueryService queryService, Flow flow );

    /**
     * Find any relationship between an entity and an other. A relationship is one or more flow from the entity to the
     * other.
     *
     * @param queryService the query service
     * @param fromEntity   an entity
     * @param toEntity     an entity
     * @return an entity relationship or null if no link exists
     */
    <T extends ModelEntity> EntityRelationship<T> findEntityRelationship( QueryService queryService, T fromEntity,
                                                                          T toEntity );

    /**
     * Find any relationship between an entity and an other within a segment. A relationship is one or more flow from
     * the entity to the other.
     *
     * @param queryService the query service
     * @param fromEntity   an entity
     * @param toEntity     an entity
     * @param segment      a segment
     * @return an entity relationship or null if no link exists
     */
    <T extends ModelEntity> EntityRelationship<T> findEntityRelationship( QueryService queryService, T fromEntity,
                                                                          T toEntity, Segment segment );

    /**
     * Find commitment relationships with entities of same kind.
     *
     * @param segment      a segment
     * @param entity       a model entity
     * @param queryService the query service
     * @return a list of relationships with other model entities
     */
    List<EntityRelationship> findEntityRelationships( Segment segment, ModelEntity entity, QueryService queryService );

    /**
     * Find commitment relationships with entities of same kind referenced in a segment.
     *
     * @param queryService the query service
     * @param segment      a segment
     * @param entityClass  an entity class
     * @param kind         a kind of entity (actual or type)
     * @return a list of relationships with other model entities
     */
    List<EntityRelationship> findEntityRelationships( QueryService queryService, Segment segment,
                                                      Class<? extends ModelEntity> entityClass, Kind kind );


    /**
     * Whether a commitment can be realized.
     * @param commitment a commitment
     * @param plan a plan
     * @param queryService a query service
     * @return a Boolean
     */
    Boolean canBeRealized( Commitment commitment, Plan plan, QueryService queryService );

    /**
     * Find problem impeding realization of a commitment.
     *
     * @param plan       a plan
     * @param commitment a communication commitment
     * @param queryService a query service
     * @return a boolean
     */
    List<String> findRealizabilityProblems( Plan plan, Commitment commitment, QueryService queryService );


    /**
     * Find any relationship between a plan segment and another. A relationship is one or more external flow in the
     * from-segment referencing a connector in the to-segment.
     *
     * @param queryService the query service
     * @param fromSegment  a plan segment
     * @param toSegment    a plan segment
     * @return a segment relationship or null if no link exists
     */
    SegmentRelationship findSegmentRelationship( QueryService queryService, Segment fromSegment, Segment toSegment );

    /**
     * Get the imaging service.
     *
     * @return the imaging service
     */
    ImagingService getImagingService();

    /**
     * Produces an aggregate description of unwaived issues detected about a model object.
     *
     * @param queryService              the query service
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesSummary( QueryService queryService, ModelObject modelObject, Boolean includingPropertySpecific );

    /**
     * Produces an overview of unwaived issues detected about a model object.
     *
     * @param queryService              the query service
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesOverview( QueryService queryService, ModelObject modelObject, Boolean includingPropertySpecific );


    /**
     * Produces an aggregate description of unwaived issues detected about an assignment.
     *
     * @param queryService              the query service
     * @param assignment                -- the assignment being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesSummary( QueryService queryService, Assignment assignment, Boolean includingPropertySpecific );

    /**
     * Produces an aggregate description of unwaived issues detected about a specific property. of a model object
     *
     * @param queryService the query service
     * @param modelObject  -- the model object being analyzed
     * @param property     -- the name of a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesSummary( QueryService queryService, ModelObject modelObject, String property );

    /**
     * Tests whether a specific property of a model object has issues.
     *
     * @param queryService the query service
     * @param modelObject  -- the model object being analyzed
     * @param property     -- the specifiec property being analyzed
     * @return whether a specifi property of a model object has issues
     */
    Boolean hasIssues( QueryService queryService, ModelObject modelObject, String property );

    /**
     * Tests whether a model object has issues.
     *
     * @param queryService              the query service
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return whether a model object has issues
     */
    Boolean hasIssues( QueryService queryService, ModelObject modelObject, Boolean includingPropertySpecific );

    /**
     * Tests whether a specific property of a model object has unwaived issues.
     *
     * @param queryService the query service
     * @param modelObject  -- the model object being analyzed
     * @param property     -- the specifiec property being analyzed
     * @return whether a specifi property of a model object has issues
     */
    Boolean hasUnwaivedIssues( QueryService queryService, ModelObject modelObject, String property );

    /**
     * Tests whether a model object has unwaived issues.
     *
     * @param queryService              the query service
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return whether a model object has issues
     */
    Boolean hasUnwaivedIssues( QueryService queryService, ModelObject modelObject, Boolean includingPropertySpecific );

    /**
     * Tests whether a model object has unwaived issues.
     *
     * @param queryService              the query service
     * @param assignment                -- the assignment being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return whether a model object has issues
     */
    Boolean hasUnwaivedIssues( QueryService queryService, Assignment assignment, Boolean includingPropertySpecific );

    /**
     * Whether model object has user issues.
     * @param queryService a query service
     * @param modelObject a model object
     * @return a boolean
     */
    Boolean hasUserIssues( QueryService queryService, ModelObject modelObject );

    /**
     * Whether a commitment could be met based on agent qualified to use medium..
     *
     * @param commitment a communication commitment
     * @param mediaUsed  media used in commitment
     * @param planLocale a plan locale
     * @return a boolean
     */
    Boolean isAgentsQualified( Commitment commitment, List<TransmissionMedium> mediaUsed, Place planLocale );

    /**
     * Whether a commitment could be met based on availability.
     *
     * @param commitment a communication commitment
     * @param mediaUsed  media used in commitment
     * @param planLocale a plan locale
     * @return a boolean
     */
    Boolean isAvailabilitiesCoincideIfRequired( Commitment commitment, List<TransmissionMedium> mediaUsed,
                                                Place planLocale );

    /**
     * Whether the model object has no unwaived completeness issue.
     *
     * @param queryService the query service
     * @param modelObject  a model object
     * @return a Boolean
     */
    Boolean isComplete( QueryService queryService, ModelObject modelObject );

    /**
     * Whether a part is effectively conceptual.
     *
     * @param queryService the query service
     * @param part         a part
     * @return a boolean
     */
    Boolean isEffectivelyConceptual( QueryService queryService, Part part );

    /**
     * Whether a flow is effectively conceptual.
     *
     * @param queryService the query service
     * @param flow         a flow
     * @return a boolean
     */
    Boolean isEffectivelyConceptual( QueryService queryService, Flow flow );

    /**
     * Whether a commitment could be met based on known contact info.
     *
     * @param commitment a communication commitment
     * @param mediaUsed  media used in commitment
     * @param planLocale a plan locale
     * @return a boolean
     */
    boolean isReachable( Commitment commitment, List<TransmissionMedium> mediaUsed, Place planLocale );

    /**
     * Whether the model object has no unwaived robustness issue.
     *
     * @param queryService the query service
     * @param modelObject  a model object
     * @return a Boolean
     */
    Boolean isRobust( QueryService queryService, ModelObject modelObject );

//    boolean isSomeMediaDeployed( Commitment commitment, List<TransmissionMedium> mediaUsed, Place planLocale );

    /**
     * Whether the model object has no unwaived validity issue.
     *
     * @param queryService the query service
     * @param modelObject  a model object
     * @return a Boolean
     */
    Boolean isValid( QueryService queryService, ModelObject modelObject );

    /**
     * Use all applicable issue detectors to find issues about a model object.
     *
     * @param queryService              the query service
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<? extends Issue> listIssues( QueryService queryService, ModelObject modelObject, Boolean includingPropertySpecific );

    /**
     * Use all applicable issue detectors to find issues about a model object's property.
     *
     * @param queryService the query service
     * @param modelObject  -- the model object being analyzed
     * @param property     -- the name of a property of the model object
     * @return a list of issues detected
     */
    List<? extends Issue> listIssues( QueryService queryService, ModelObject modelObject, String property );

    /**
     * Use all applicable issue detectors to find issues about a model object.
     *
     * @param queryService              the query service
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @param includingWaived           -- whether to also include waived issues
     * @return a list of issues detected
     */
    List<? extends Issue> listIssues( QueryService queryService, ModelObject modelObject, Boolean includingPropertySpecific,
                                      Boolean includingWaived );

    /**
     * Use all unwaived issue detectors to find issues about a model object.
     *
     * @param queryService              the query service
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<? extends Issue> listUnwaivedIssues( QueryService queryService, ModelObject modelObject,
                                              Boolean includingPropertySpecific );

    /**
     * Use all unwaived issue detectors to find issues about an assignment.
     *
     * @param queryService              the query service
     * @param assignment                -- the assignment being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<? extends Issue> listUnwaivedIssues( QueryService queryService, Assignment assignment,
                                              Boolean includingPropertySpecific );

    /**
     * Use all unwaived issue detectors to find issues about a model object's property.
     *
     * @param queryService the query service
     * @param modelObject  -- the model object being analyzed
     * @param property     -- the name of a property of the model object
     * @return a list of issues detected
     */
    List<? extends Issue> listUnwaivedIssues( QueryService queryService, ModelObject modelObject, String property );

    /**
     * Use all waived issue detectors to find issues about a model object.
     *
     * @param queryService              the query service
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<? extends Issue> listWaivedIssues( QueryService queryService, ModelObject modelObject,
                                            Boolean includingPropertySpecific );

    /**
     * Use all waived issue detectors to find issues about an assignment.
     *
     * @param queryService              the query service
     * @param assignment                -- the assignment being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<? extends Issue> listWaivedIssues( QueryService queryService, Assignment assignment, Boolean includingPropertySpecific );

    /**
     * Use all waived issue detectors to find issues about a model object's property.
     *
     * @param queryService the query service
     * @param modelObject  -- the model object being analyzed
     * @param property     -- the name of a property of the model object
     * @return a list of issues detected
     */
    List<? extends Issue> listWaivedIssues( QueryService queryService, ModelObject modelObject, String property );

    /**
     * Notification of command execution.
     *
     * @param plan a plan
     */
    void onAfterCommand( Plan plan );

    /**
     * On destroy.
     */
    void onDestroy();

    /**
     * On startup.
     *
     * @param plan a plan
     */
    void onStart( Plan plan );

    /**
     * On stop.
     */
    void onStop();

    /**
     * Diagnostic about whether a commitment can be realized or is conceptual.
     *
     * @param commitment   a communication commitment
     * @param communityService a community service
     * @return a string
     */
    String realizability( Commitment commitment, CommunityService communityService );

    /**
     * Number of unwaived issues on a requirement..
     *
     * @param requirement  a requirement
     * @param queryService a queryService
     * @return a string
     */
    int unwaivedIssuesCount( Requirement requirement, QueryService queryService );

    /**
     * Number of unwaived issues on a requirement..
     *
     * @param requirement  a requirement
     * @param communityService a plan community service
     * @return a string
     */
    int unwaivedIssuesCount( Requirement requirement, CommunityService communityService );

    /**
     * Number of issues on a requirement..
     *
     * @param requirement  a requirement
     * @param queryService a queryService
     * @return a string
     */
    int allIssuesCount( Requirement requirement, QueryService queryService );

     /**
     * Sets issue scanner.
     *
     * @param issueScanner an issue scanner
     */
    void setIssueScanner( IssueScanner issueScanner );
}
