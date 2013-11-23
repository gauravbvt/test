package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Plan;

/**
 * A scanner service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 7, 2009
 * Time: 10:05:57 AM
 */
public interface Scanner {
    /**
     * Start scan of all plans.
     * @param planCommunity a plan community
     */
    void scan( PlanCommunity planCommunity );

    /**
     * Abort scan of plan and restart it.
     *
     * @param planCommunity a plan community
     */
    void rescan( PlanCommunity planCommunity );

    /**
     * Abort all scans.
     */
    void terminate();
}
