package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Waivable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/13
 * Time: 10:58 AM
 */
abstract public class AbstractWaivableIdentifiable implements Waivable {

    @Override
    public boolean isWaived( String detector, CommunityService communityService ) {
        return communityService.getPlanCommunity().hasIssueDetectionWaiver( this, detector );
    }

    @Override
    public void waiveIssueDetection( String detector, CommunityService communityService ) {
        communityService.getPlanCommunity().addIssueDetectionWaiver( this, detector );
    }

    @Override
    public void unwaiveIssueDetection( String detector, CommunityService communityService ) {
        communityService.getPlanCommunity().removeIssueDetectionWaiver( this, detector );
    }

}
