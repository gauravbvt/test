package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.OrganizationParticipationService;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationConfirmationService;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignments;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;

import java.util.List;

/**
 * Community service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/12
 * Time: 8:56 AM
 */
public interface CommunityService {

    UserParticipationService getUserParticipationService();

    UserParticipationConfirmationService getUserParticipationConfirmationService();

    OrganizationParticipationService getOrganizationParticipationService();

    PlanService getPlanService();

    void setPlanService( PlanService planService );

    Analyst getAnalyst();

    void setPlanCommunity( PlanCommunity planCommunity );

    /**
     * Find all users that participate a a given actor.
     *
     * @param actor an actor
     * @return a list of users
     */
    List<ChannelsUser> findUsersParticipatingAs( Actor actor );   // todo --obsolete

    /**
     * Whether participation as actor possible given current participation.
     * @param actor an actor
     * @param activeParticipations  a list of active participations
     * @return  a boolean -- not cached
     */
    Boolean meetsPreEmploymentConstraint( Actor actor, // todo -- obsolete
                                          List<UserParticipation> activeParticipations );

    CommunityCommitments getAllCommitments( Boolean includeToSelf );

    CommunityCommitments findAllCommitments( Flow flow, Boolean includeToSelf );

    CommunityAssignments getAllAssignments();

    CommunityCommitments findAllBypassCommitments( final Flow flow );

    void clearCache();
}
