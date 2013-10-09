package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.issues.ParticipationIssue;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;

import java.util.List;

/**
 * Participation analyst.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 9:11 AM
 */
public interface ParticipationAnalyst {

    /**
     * Detect all issues.
     *
     * @param communityService a plan community service
     * @return a list of participation issues
     */
    List<ParticipationIssue> detectAllIssues( CommunityService communityService );

    /**
     * Detect all issues of a given identifiable.
     *
     * @param identifiable  an identifiable
     * @param communityService a plan community service
     * @return a list of participation issues
     */
    List<ParticipationIssue> detectIssues( Identifiable identifiable, CommunityService communityService );

    /**
     * Whether a given identifiable has issues.
     *
     * @param identifiable  an identifiable
     * @param communityService a plan community service
     * @return a boolean
     */
    boolean hasIssues( Identifiable identifiable, CommunityService communityService );

    /**
     * Get an overview of the issues of a given identifiable.
     *
     * @param identifiable  an identifiable
     * @param communityService a plan community service
     * @return a string, empty if no issues
     */
    String getIssuesOverview( Identifiable identifiable, CommunityService communityService );


    /**
     * Find requirement relationships between organizations.
     *
     * @param timing        a phase timing or null
     * @param event         an event or null
     * @param communityService a plan community service
     * @return a list of requirement relationships
     */
    List<RequirementRelationship> findRequirementRelationships(
            Phase.Timing timing,
            Event event,
            CommunityService communityService );

    /**
     * Find requirement relationship from one organization to another.
     *
     * @param fromAgency    an agency
     * @param toAgency      an agency
     * @param timing        a phase timing or null
     * @param event         an event or null
     * @param communityService a plan community service
     * @return a requirement relationship
     */
    RequirementRelationship findRequirementRelationship(
            Agency fromAgency,
            Agency toAgency,
            Phase.Timing timing,
            Event event,
            CommunityService communityService );

    /**
     * The level of satisfaction of a requirement.
     *
     * @param requirement   a requirement
     * @param extras        extra parameters -- 0=>Phase.Timing, 1=>Event
     * @param communityService a plan community service
     * @return a requirement satisfaction
     */
    Requirement.Satisfaction requirementSatisfaction( Requirement requirement,
                                                      Object[] extras,
                                                      CommunityService communityService );

    /**
     * Number of commitments that fulfill a given requirement.
     *
     * @param requirement a requirement
     * @param extras      extra parameters -- 0=>Phase.Timing, 1=>Event, 2=>PlanCommunity
     * @return an int
     */
    int requiredCommitmentsCount( Requirement requirement, Object[] extras, CommunityService communityService );

    /**
     * Summary of why given requirement not fully satisfied.
     * Empty if requirement fully satisfied.
     *
     * @param requirement a requirement
     * @param extras      extra parameters -- 0=>Phase.Timing, 1=>Event, 2=>PlanCommunity
     * @return an int
     */
    String satisfactionSummary( Requirement requirement, Object[] extras, CommunityService communityService );

    /**
     * The percentage by which the requirement is satisfied by pairs of agencies it applies to
     * @param requirement a requirement
     * @param communityService a plan community service
     * @return a string - e.g. "25%"
     */
    String percentSatisfaction( Requirement requirement, CommunityService communityService );

    /**
     * Diagnostic about whether a commitment can be realized or is conceptual.
     * Analysis based entirely on plan definition.
     *
     * @param communityCommitment    an info sharing commitment in a plan community
     * @param communityService a plan community service
     * @return a string
     */
    String realizability( CommunityCommitment communityCommitment, CommunityService communityService );


}
