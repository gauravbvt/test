package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
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
     * @param planCommunity a plan community
     * @return a list of participation issues
     */
    List<ParticipationIssue> detectAllIssues( PlanCommunity planCommunity );

    /**
     *  Detect all issues of a given identifiable.
     *
     * @param identifiable  an identifiable
     * @param planCommunity a plan community
     * @return a list of participation issues
     */
    List<ParticipationIssue> detectIssues( Identifiable identifiable, PlanCommunity planCommunity );

    /**
     * Whether a given identifiable has issues.
     *
     * @param identifiable  an identifiable
     * @param planCommunity a plan community
     * @return a boolean
     */
    boolean hasIssues( Identifiable identifiable, PlanCommunity planCommunity );

    /**
     * Get an overview of the issues of a given identifiable.
     *
     * @param identifiable  an identifiable
     * @param planCommunity a plan community
     * @return a string, empty if no issues
     */
    String getIssuesOverview( Identifiable identifiable, PlanCommunity planCommunity );


    /**
     * Find requirement relationships between organizations.
     *
     * @param timing        a phase timing or null
     * @param event         an event or null
     * @param planCommunity a plan community
     * @return a list of requirement relationships
     */
    List<RequirementRelationship> findRequirementRelationships(
            Phase.Timing timing,
            Event event,
            PlanCommunity planCommunity );

    /**
     * Find requirement relationship from one organization to another.
     *
     * @param fromAgency    an agency
     * @param toAgency      an agency
     * @param timing        a phase timing or null
     * @param event         an event or null
     * @param planCommunity a plan community
     * @return a requirement relationship
     */
    RequirementRelationship findRequirementRelationship(
            Agency fromAgency,
            Agency toAgency,
            Phase.Timing timing,
            Event event,
            PlanCommunity planCommunity );


    /**
     * The level of satisfaction of a requirement by committer organization.
     *
     * @param requirement   a requirement
     * @param extras        extra parameters -- 0=>Phase.Timing, 1=>Event, 2=>PlanCommunity
     * @param planCommunity a plan community
     * @return a requirement satisfaction
     */
    Requirement.Satisfaction committerSatisfaction(
            Requirement requirement,
            Object[] extras,
            PlanCommunity planCommunity );

    /**
     * The level of satisfaction of a requirement by beneficiary organization.
     *
     * @param requirement   a requirement
     * @param extras        extra parameters -- 0=>Phase.Timing, 1=>Event, 2=>PlanCommunity
     * @param planCommunity a plan community
     * @return a requirement satisfaction
     */
    Requirement.Satisfaction beneficiarySatisfaction(
            Requirement requirement,
            Object[] extras,
            PlanCommunity planCommunity );

    /**
     * Number of commitments that fulfill a given requirement.
     * @param requirement a requirement
     * @param extras extra parameters -- 0=>Phase.Timing, 1=>Event, 2=>PlanCommunity
     * @return an int
     */
    int commitmentsCount( Requirement requirement, Object[] extras,  PlanCommunity planCommunity );

    /**
     * Diagnostic about whether a commitment can be realized or is conceptual.
     *
     * @param commitment   an info sharing commitment in a plan community
     * @param planCommunity a plan community
     * @return a string
     */
    String realizability( CommunityCommitment commitment,  PlanCommunity planCommunity );


}
