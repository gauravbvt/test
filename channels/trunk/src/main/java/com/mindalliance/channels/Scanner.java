package com.mindalliance.channels;

import com.mindalliance.channels.model.Plan;

/**
 * A scanner service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 7, 2009
 * Time: 10:05:57 AM
 */
public interface Scanner extends Service {
    /**
     * Start scan of all plans.
     */
    void scan();

    /**
     * Abort scan of plan and restart it.
     *
     * @param plan a plan
     */
    void rescan( Plan plan );

    /**
     * Abort all scans.
     */
    void terminate();
}
