package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.PlanParticipation;
import com.mindalliance.channels.core.community.participation.PlanParticipationService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
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

    PlanParticipationService getPlanParticipationService();

    PlanService getPlanService();

    Analyst getAnalyst();

    /**
     * Find all users that participate a a given actor.
     *
     * @param actor an actor
     * @return a list of users
     */
    List<ChannelsUser> findUsersParticipatingAs( Actor actor );

    /**
     * Whether participation as actor possible given current participation.
     * @param actor an actor
     * @param activeParticipations  a list of active participations
     * @return  a boolean -- not cached
     */
    boolean meetsPreEmploymentConstraint( Actor actor,
                                          List<PlanParticipation> activeParticipations );


}
