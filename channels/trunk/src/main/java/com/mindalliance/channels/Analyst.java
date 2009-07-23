package com.mindalliance.channels;

import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.ResourceSpec;

import java.util.Iterator;
import java.util.List;

/**
 * Analyzes model elements to uncover issues and make recommendations for fixing them.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 9:29:14 AM
 */
public interface Analyst extends Service {

    /**
     * Whether to include issues that are property-specific.
     */
    boolean INCLUDE_PROPERTY_SPECIFIC = true;

    /**
     * Use all applicable issue detectors to find issues about a model object.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return an iterator on issues detected
     */
    Iterator<Issue> findIssues( ModelObject modelObject, boolean includingPropertySpecific );

    /**
     * Use all applicable issue detectors to find issues about a model object's property.
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the name of a property of the model object
     * @return an iterator on issues detected
     */
    Iterator<Issue> findIssues( ModelObject modelObject, String property );

    /**
     * Use all applicable issue detectors to find issues about a model object.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<Issue> listIssues( ModelObject modelObject, boolean includingPropertySpecific );

    /**
     * Use all applicable issue detectors to find issues about a model object.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @param includingWaived           -- whether to also include waived issues
     * @return a list of issues detected
     */
    List<Issue> listIssues( ModelObject modelObject, boolean includingPropertySpecific, boolean includingWaived );

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
    List<Issue> listUnwaivedIssues( ModelObject modelObject, boolean includingPropertySpecific );

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
    boolean hasIssues( ModelObject modelObject, String property );

    /**
     * Tests whether a model object has issues.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return whether a model object has issues
     */
    boolean hasIssues( ModelObject modelObject, boolean includingPropertySpecific );

    /**
     * Tests whether a specific property of a model object has unwaived issues.
     *
     * @param modelObject -- the model object being analyzed
     * @param property    -- the specifiec property being analyzed
     * @return whether a specifi property of a model object has issues
     */
    boolean hasUnwaivedIssues( ModelObject modelObject, String property );

    /**
     * Tests whether a model object has unwaived issues.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return whether a model object has issues
     */
    boolean hasUnwaivedIssues( ModelObject modelObject, boolean includingPropertySpecific );

    /**
     * Produces an aggregate description of unwaived issues detected about a model object.
     *
     * @param modelObject               -- the model object being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesSummary( ModelObject modelObject, boolean includingPropertySpecific );

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
     * @return a list of issues
     */
    List<Issue> findAllIssuesFor( ResourceSpec resource );

    /**
     * Find all issues related to any of the components of a resource.
     *
     * @param resource a resource
     * @param specific a boolean -- true -> equality match, false -> marrow or equals
     * @return a list of issues
     */
    List<Issue> findAllIssuesFor( ResourceSpec resource, boolean specific );

    /**
     * Get query service.
     *
     * @return a query service
     */
    QueryService getQueryService();

    /**
     * Whether the model object has no unwaived validity issue.
     * @param modelObject a model object
     * @return a Boolean
     */
    Boolean isValid( ModelObject modelObject );
    /**
     * Whether the model object has no unwaived completeness issue.
     * @param modelObject a model object
     * @return a Boolean
     */
    Boolean isComplete( ModelObject modelObject );
    /**
     * Whether the model object has no unwaived robustness issue.
     * @param modelObject a model object
     * @return a Boolean
     */
    Boolean isRobust( ModelObject modelObject );

    /**
     * Count the number of unwaived issues for a model object in a given test.
     * @param modelObject a model object
     * @param test a type of issue
     * @return an Integer
     */
    Integer countTestFailures( ModelObject modelObject, String test );

}
