// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Plan;

/**
 * A listener to important plan management business.
 */
public interface PlanListener {

    /**
     * A plan is about to be put in production.
     * @param devPlan the development plan
     */
    void aboutToProductize( Plan devPlan );

    /**
     * A new plan was put in production.
     * @param plan the new plan
     */
    void productized( Plan plan );

    /**
     * A new development plan was created.
     * @param devPlan the new plan.
     */
    void created( Plan devPlan );

    /**
     * A development plan has been loaded.
     * @param planDao the plan dao
     */
    void loaded( PlanDao planDao );

    /**
     * A plan is about to be removed from memory.
     * @param planDao  the plan dao
     */
    void aboutToUnload( PlanDao planDao );
}
