package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.model.Plan;

import java.util.List;

/**
 * Plan community manager.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/12
 * Time: 3:15 PM
 */
public interface PlanCommunityManager {

    /**
     * For now, there is exactly one implied, unnamed plan community per plan per instance of Channels.
     * @param plan a plan
     * @return a plan community
     */
    PlanCommunity makePlanCommunity( Plan plan );  // todo - change when no longer a single plan community implied by a plan.

    /**
     * Find plan community with production plan.
     * @param uri a community's URI
     * @return a plan community or null
     */
    PlanCommunity findPlanCommunity( String uri );

    /**
     * Find a plan community with a given version of their plan (used by planners).
     * @param uri a community's URI
     * @param planVersion  a plan's version
     * @return a plan community or null
     */
    PlanCommunity findPlanCommunity( String uri, int planVersion );

    /**
     * Return the list of all plan communities.
     * @return a plan community.
     */
    List<PlanCommunity> getPlanCommunities();
}
