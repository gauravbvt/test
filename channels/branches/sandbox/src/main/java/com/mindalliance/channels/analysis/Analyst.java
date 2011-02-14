package com.mindalliance.channels.analysis;

import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;

import java.util.List;

/**
 * Analyzes the plan and individual model elements.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 9:29:14 AM
 */
public interface Analyst {

    /**
     * Whether to include issues that are property-specific.
     */
    boolean INCLUDE_PROPERTY_SPECIFIC = true;

    /**
     * Notification of command execution.
     *
     * @param plan a plan
     */
    void onAfterCommand( Plan plan );

    /**
     * Use all applicable issue detectors to find issues about a model object.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<Issue> listIssues( ModelObject modelObject, Boolean includingPropertySpecific );

    /**
     * Use all applicable issue detectors to find issues about a model object.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @param includingWaived           -- whether to also include waived issues
     * @return a list of issues detected
     */
    List<Issue> listIssues( ModelObject modelObject, Boolean includingPropertySpecific, Boolean includingWaived );

    /**
     * Use all applicable issue detectors to find issues about a model object's property.
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the name of a property of the model object
     * @return a list of issues detected
     */
    List<Issue> listIssues( ModelObject modelObject, String property );

    /**
     * Use all unwaived issue detectors to find issues about a model object.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<Issue> listUnwaivedIssues( ModelObject modelObject, Boolean includingPropertySpecific );

    /**
     * Use all unwaived issue detectors to find issues about an assignment.
     *
     * @param assignment               -- the assignment being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<Issue> listUnwaivedIssues( Assignment assignment, Boolean includingPropertySpecific );

    /**
     * Use all unwaived issue detectors to find issues about a model object's property.
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the name of a property of the model object
     * @return a list of issues detected
     */
    List<Issue> listUnwaivedIssues( ModelObject modelObject, String property );

    /**
     * Tests whether a specific property of a model object has issues.
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the specifiec property being analyzed
     * @return whether a specifi property of a model object has issues
     */
    Boolean hasIssues( ModelObject modelObject, String property );

    /**
     * Tests whether a model object has issues.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return whether a model object has issues
     */
    Boolean hasIssues( ModelObject modelObject, Boolean includingPropertySpecific );

    /**
     * Tests whether a specific property of a model object has unwaived issues.
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the specifiec property being analyzed
     * @return whether a specifi property of a model object has issues
     */
    Boolean hasUnwaivedIssues( ModelObject modelObject, String property );

    /**
     * Tests whether a model object has unwaived issues.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return whether a model object has issues
     */
    Boolean hasUnwaivedIssues( ModelObject modelObject, Boolean includingPropertySpecific );

    /**
     * Tests whether a model object has unwaived issues.
     *
     * @param assignment               -- the assignment being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return whether a model object has issues
     */
    Boolean hasUnwaivedIssues( Assignment assignment, Boolean includingPropertySpecific );

    /**
     * Produces an aggregate description of unwaived issues detected about a model object.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesSummary( ModelObject modelObject, Boolean includingPropertySpecific );

    /**
      * Produces an aggregate description of unwaived issues detected about an assignment.
      *
      * @param assignment               -- the assignment being analyzed
      * @param includingPropertySpecific -- all issues or only those that are not specific to a property
      * @return an aggregate description of issues or an empty string if none
      */
     String getIssuesSummary( Assignment assignment, Boolean includingPropertySpecific );

    /**
     * Produces an aggregate description of unwaived issues detected about a specific property.
     * of a model object
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the name of a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesSummary( ModelObject modelObject, String property );


    /**
     * Find all issues related to any of the components of a resource.
     *
     * @param resource a resource
     * @param specific a boolean -- true -> equality match, false -> marrow or equals
     * @return a list of issues
     */
    List<Issue> findAllIssuesFor( ResourceSpec resource, Boolean specific );

    /**
     * Get query service.
     *
     * @return a query service
     */
    QueryService getQueryService();

    /**
     * Whether the model object has no unwaived validity issue.
     *
     * @param modelObject a model object
     * @return a Boolean
     */
    Boolean isValid( ModelObject modelObject );

    /**
     * Whether the model object has no unwaived completeness issue.
     *
     * @param modelObject a model object
     * @return a Boolean
     */
    Boolean isComplete( ModelObject modelObject );

    /**
     * Whether the model object has no unwaived robustness issue.
     *
     * @param modelObject a model object
     * @return a Boolean
     */
    Boolean isRobust( ModelObject modelObject );

    /**
     * Count the number of unwaived issues for a model object in a given test.
     *
     * @param modelObject a model object
     * @param test        a type of issue
     * @return an Integer
     */
    Integer countTestFailures( ModelObject modelObject, String test );

    /**
     * On startup.
     * @param plan a plan
     */
    void onStart( Plan plan );

    /**
     * On stop.
     */
    void onStop();

    /**
     * On destroy.
     */
    void onDestroy();

    /**
     * Sets issue scanner.
     *
     * @param issueScanner an issue scanner
     */
    void setIssueScanner( IssueScanner issueScanner );

    /**
     * Get the imaging service.
     * @return the imaging service
     */
    ImagingService getImagingService();


    /**
     * Find all issues on all model objects in the plan.
     *
     * @return a list of issues.
     */
    List<Issue> findAllIssues();

    /**
     * Find all unwaived issues on all model objects in the plan.
     *
     * @return a list of issues.
     */
    List<Issue> findAllUnwaivedIssues();

    /**
     * Find any relationship between a plan segment and another.
     * A relationship is one or more external flow in the from-segment referencing a connector in
     * the to-segment.
     *
     * @param fromSegment a plan segment
     * @param toSegment   a plan segment
     * @return a segment relationship or null if no link exists
     */
    SegmentRelationship findSegmentRelationship( Segment fromSegment, Segment toSegment );

    /**
     * Find any relationship between an entity and an other.
     * A relationship is one or more flow from the entity to the other.
     *
     * @param fromEntity an entity
     * @param toEntity   an entity
     * @return an entity relationship or null if no link exists
     */
    <T extends ModelEntity> EntityRelationship<T> findEntityRelationship(
            T fromEntity, T toEntity );

    /**
     * Find any relationship between an entity and an other within a segment.
     * A relationship is one or more flow from the entity to the other.
     *
     * @param fromEntity an entity
     * @param toEntity   an entity
     * @param segment    a segment
     * @return an entity relationship or null if no link exists
     */
    <T extends ModelEntity> EntityRelationship<T> findEntityRelationship(
            T fromEntity, T toEntity, Segment segment );

    /**
     * Find relationships with entities of same kind referenced in a segment.
     * @param segment  a segment
     * @param entityClass  an entity class
     * @param kind  a kind of entity (actual or type)
     * @return  a list of relationships with other model entities
     */
    List<EntityRelationship> findEntityRelationships(
            Segment segment, Class<? extends ModelEntity> entityClass, ModelEntity.Kind kind );

    /**
     * Find relationships with entities of same kind.
     * @param segment  a segment
     * @param entity  a model entity
     * @return  a list of relationships with other model entities
     */
    List<EntityRelationship> findEntityRelationships(
            Segment segment, ModelEntity entity );
}
