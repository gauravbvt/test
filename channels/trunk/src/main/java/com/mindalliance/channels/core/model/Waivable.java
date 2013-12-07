package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.community.CommunityService;

/**
 * Supports waiving issues detected about it.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/13
 * Time: 9:19 AM
 */
public interface Waivable extends Identifiable {

    /**
     * Waive a kind of issue detection.
     *
     * @param detection        a string
     * @param communityService a community service
     */
    void waiveIssueDetection( String detection, CommunityService communityService );

    /**
     * Un-waive a kind of issue detection.
     *
     * @param detection        a string
     * @param communityService a community service
     */
    void unwaiveIssueDetection( String detection, CommunityService communityService );

    /**
     * Whether a given issue detection is waived.
     *
     * @param detection        the name of an issue detector
     * @param communityService a community service
     * @return a boolean
     */
    boolean isWaived( String detection, CommunityService communityService );

}
