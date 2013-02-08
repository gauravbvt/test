package com.mindalliance.channels.core.community;

/**
 * Community service factory.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/5/13
 * Time: 8:36 PM
 */
public interface CommunityServiceFactory {

    /**
     * Get the dedicated service for a given plan community.
     * @param planCommunity the plan community
     * @return the service
     */
    CommunityService getService( PlanCommunity planCommunity );

}
