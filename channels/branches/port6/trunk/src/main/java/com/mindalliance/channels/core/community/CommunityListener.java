package com.mindalliance.channels.core.community;

/**
 * A listener to PlanCommunity lifecycle events.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/5/13
 * Time: 11:27 AM
 */
public interface CommunityListener {

    void aboutToUnload( CommunityDao communityDao );

    void created( PlanCommunity planCommunity );

    void loaded( CommunityDao communityDao );
}
