package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.ResourceSpec;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/13
 * Time: 2:22 PM
 */
public interface Doctor {

    IssueScanner getIssueScanner();

    /**
     * Whether to include issues that are property-specific.
     */
    boolean INCLUDE_PROPERTY_SPECIFIC = true;

    /**
     * Count the number of unwaived issues for a identifiable in a given test.
     *
     * @param communityService the query service
     * @param identifiable  a identifiable
     * @param test         a type of issue
     * @return an Integer
     */
    Integer countTestFailures( CommunityService communityService, Identifiable identifiable, String test );

    /**
     * Find all issues on all identifiables in the plan.
     *
     * @param communityService the query service
     * @return a list of issues.
     */
    List<Issue> findAllIssues( CommunityService communityService );

    /**
     * Find all issues related to any of the components of a resource.
     *
     * @param communityService the query service
     * @param resource     a resource
     * @param specific     a boolean -- true -> equality match, false -> marrow or equals
     * @return a list of issues
     */
    List<Issue> findAllIssuesFor( CommunityService communityService, ResourceSpec resource, Boolean specific );

    /**
     * Find all unwaived issues on all identifiables in the plan.
     *
     * @param communityService the query service
     * @return a list of issues.
     */
    List<Issue> findAllUnwaivedIssues( CommunityService communityService );

    /**
     * Find all waived issues on all identifiables in the plan.
     *
     * @param communityService the query service
     * @return a list of issues.
     */
    List<Issue> findAllWaivedIssues( CommunityService communityService );

    /**
     * Produces an aggregate description of unwaived issues detected about a identifiable.
     *
     * @param communityService              the community service
     * @param identifiable               -- the identifiable being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesSummary( CommunityService communityService, Identifiable identifiable, Boolean includingPropertySpecific );

    /**
     * Produces an overview of unwaived issues detected about a identifiable.
     *
     * @param communityService              the query service
     * @param identifiable               -- the identifiable being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesOverview( CommunityService communityService, Identifiable identifiable, Boolean includingPropertySpecific );


    /**
     * Produces an aggregate description of unwaived issues detected about an assignment.
     *
     * @param communityService              the query service
     * @param assignment                -- the assignment being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesSummary( CommunityService communityService, Assignment assignment, Boolean includingPropertySpecific );

    /**
     * Produces an aggregate description of unwaived issues detected about a specific property. of a identifiable
     *
     * @param communityService the query service
     * @param identifiable  -- the identifiable being analyzed
     * @param property     -- the name of a property
     * @return an aggregate description of issues or an empty string if none
     */
    String getIssuesSummary( CommunityService communityService, Identifiable identifiable, String property );

    /**
     * Tests whether a specific property of a identifiable has issues.
     *
     * @param communityService the query service
     * @param identifiable  -- the identifiable being analyzed
     * @param property     -- the specifiec property being analyzed
     * @return whether a specifi property of a identifiable has issues
     */
    Boolean hasIssues( CommunityService communityService, Identifiable identifiable, String property );

    /**
     * Tests whether a identifiable has issues.
     *
     * @param communityService              the query service
     * @param identifiable               -- the identifiable being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return whether a identifiable has issues
     */
    Boolean hasIssues( CommunityService communityService, Identifiable identifiable, Boolean includingPropertySpecific );

    /**
     * Tests whether a specific property of a identifiable has unwaived issues.
     *
     * @param communityService the query service
     * @param identifiable  -- the identifiable being analyzed
     * @param property     -- the specifiec property being analyzed
     * @return whether a specifi property of a identifiable has issues
     */
    Boolean hasUnwaivedIssues( CommunityService communityService, Identifiable identifiable, String property );

    /**
     * Tests whether a identifiable has unwaived issues.
     *
     * @param communityService              the query service
     * @param identifiable               -- the identifiable being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return whether a identifiable has issues
     */
    Boolean hasUnwaivedIssues( CommunityService communityService, Identifiable identifiable, Boolean includingPropertySpecific );

    /**
     * Tests whether a identifiable has unwaived issues.
     *
     * @param communityService              the query service
     * @param assignment                -- the assignment being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return whether a identifiable has issues
     */
    Boolean hasUnwaivedIssues( CommunityService communityService, Assignment assignment, Boolean includingPropertySpecific );

    /**
     * Whether identifiable has user issues.
     * @param communityService a query service
     * @param identifiable a identifiable
     * @return a boolean
     */
    Boolean hasUserIssues( CommunityService communityService, Identifiable identifiable );

    /**
     * Whether the identifiable has no unwaived robustness issue.
     *
     * @param communityService the query service
     * @param identifiable  a identifiable
     * @return a Boolean
     */
    Boolean isRobust( CommunityService communityService, Identifiable identifiable );

//    boolean isSomeMediaDeployed( Commitment commitment, List<TransmissionMedium> mediaUsed, Place planLocale );

    /**
     * Whether the identifiable has no unwaived completeness issue.
     *
     * @param communityService the query service
     * @param identifiable  a identifiable
     * @return a Boolean
     */
    Boolean isComplete( CommunityService communityService, Identifiable identifiable );

    /**
     * Whether the identifiable has no unwaived validity issue.
     *
     * @param communityService the query service
     * @param identifiable  a identifiable
     * @return a Boolean
     */
    Boolean isValid( CommunityService communityService, Identifiable identifiable );

    /**
     * Use all applicable issue detectors to find issues about a identifiable.
     *
     * @param communityService              the query service
     * @param identifiable               -- the identifiable being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<? extends Issue> listIssues( CommunityService communityService, Identifiable identifiable, Boolean includingPropertySpecific );

    /**
     * Use all applicable issue detectors to find issues about a identifiable's property.
     *
     * @param communityService the query service
     * @param identifiable  -- the identifiable being analyzed
     * @param property     -- the name of a property of the identifiable
     * @return a list of issues detected
     */
    List<? extends Issue> listIssues( CommunityService communityService, Identifiable identifiable, String property );

    /**
     * Use all applicable issue detectors to find issues about a identifiable.
     *
     * @param communityService              the query service
     * @param identifiable               -- the identifiable being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @param includingWaived           -- whether to also include waived issues
     * @return a list of issues detected
     */
    List<? extends Issue> listIssues( CommunityService communityService, Identifiable identifiable, Boolean includingPropertySpecific,
                                      Boolean includingWaived );

    /**
     * Use all unwaived issue detectors to find issues about a identifiable.
     *
     * @param communityService              the query service
     * @param identifiable               -- the identifiable being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<? extends Issue> listUnwaivedIssues( CommunityService communityService, Identifiable identifiable,
                                              Boolean includingPropertySpecific );

    /**
     * Use all unwaived issue detectors to find issues about an assignment.
     *
     * @param communityService              the query service
     * @param assignment                -- the assignment being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<? extends Issue> listUnwaivedIssues( CommunityService communityService, Assignment assignment,
                                              Boolean includingPropertySpecific );

    /**
     * Use all unwaived issue detectors to find issues about a identifiable's property.
     *
     * @param communityService the query service
     * @param identifiable  -- the identifiable being analyzed
     * @param property     -- the name of a property of the identifiable
     * @return a list of issues detected
     */
    List<? extends Issue> listUnwaivedIssues( CommunityService communityService, Identifiable identifiable, String property );

    /**
     * Use all waived issue detectors to find issues about a identifiable.
     *
     * @param communityService              the query service
     * @param identifiable               -- the identifiable being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<? extends Issue> listWaivedIssues( CommunityService communityService, Identifiable identifiable,
                                            Boolean includingPropertySpecific );

    /**
     * Use all waived issue detectors to find issues about an assignment.
     *
     * @param communityService              the query service
     * @param assignment                -- the assignment being analyzed
     * @param includingPropertySpecific -- all issues or only those that are not specific to a property
     * @return a list of issues detected
     */
    List<? extends Issue> listWaivedIssues( CommunityService communityService, Assignment assignment, Boolean includingPropertySpecific );

    /**
     * Use all waived issue detectors to find issues about a identifiable's property.
     *
     * @param communityService the query service
     * @param identifiable  -- the identifiable being analyzed
     * @param property     -- the name of a property of the identifiable
     * @return a list of issues detected
     */
    List<? extends Issue> listWaivedIssues( CommunityService communityService, Identifiable identifiable, String property );

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
     * @param communityService a communityService
     * @return a string
     */
    int allIssuesCount( Requirement requirement, CommunityService communityService );

    /**
     * Sets issue scanner.
     *
     * @param issueScanner an issue scanner
     */
    void setIssueScanner( IssueScanner issueScanner );


}
