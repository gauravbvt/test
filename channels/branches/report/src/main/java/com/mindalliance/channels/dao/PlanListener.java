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
     * @param plan the plan
     */
    void loaded( Plan plan );

    /**
     * A plan is about to be removed from memory.
     * @param plan the plan
     */
    void aboutToUnload( Plan plan );
}
