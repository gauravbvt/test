// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.community.CommunityDao;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Plan;

/**
 * A listener to important plan management business.
 */
public interface PlanListener {

    /**
     * A plan is about to be put in production.
     *
     * @param devPlan the development plan
     */
    void aboutToProductize( Plan devPlan );

    /**
     * A new plan was put in production.
     *
     * @param plan the new plan
     */
    void productized( Plan plan );

    /**
     * A new development plan was created.
     *
     * @param devPlan the new plan.
     */
    void created( Plan devPlan );

    /**
     * A plan community was created.
     *
     * @param planCommunity the new plan community.
     */
    void created( PlanCommunity planCommunity );

    /**
     * A development plan has been loaded.
     *
     * @param planDao the plan dao
     */
    void loaded( PlanDao planDao );

    /**
     * A plan community has been loaded.
     *
     * @param communityDao the community dao
     */
    void loaded( CommunityDao communityDao );

    /**
     * A plan is about to be removed from memory.
     *
     * @param planDao the plan dao
     */
    void aboutToUnload( PlanDao planDao );
}
