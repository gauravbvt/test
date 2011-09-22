/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.model.Plan;

/**
 * Creator and cache for plan services.
 */
public interface PlanServiceFactory {

    /**
     * Get the dedicated service for a given plan.
     * @param plan the plan
     * @return the service
     */
    PlanService getService( Plan plan );
}
