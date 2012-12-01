package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.model.Plan;

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
    PlanCommunity getPlanCommunity( Plan plan );
}
