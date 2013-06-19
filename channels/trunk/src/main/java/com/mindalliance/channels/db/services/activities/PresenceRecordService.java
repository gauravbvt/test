package com.mindalliance.channels.db.services.activities;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.db.data.activities.PresenceRecord;
import com.mindalliance.channels.db.services.DataService;
import com.mindalliance.channels.social.PresenceListener;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/18/13
 * Time: 5:26 PM
 */
public interface PresenceRecordService extends DataService<PresenceRecord>, PresenceListener {

    /**
     * Find latest presence change for a user in a plan (entering or leaving).
     *
     * @param username a string
     * @param communityUri a planCommunity URI
     * @return a presence event
     */
    PresenceRecord findLatestPresence( String username, String communityUri );

    /**
     * Has a keep-alive heartbeat been heard recently?
     *
     * @param username a string
     * @param communityUri a planCommunity URI
     * @return a boolean
     */
    boolean isAlive( String username, String communityUri );

    void recordAbsence( String username, PlanCommunity planCommunity );

    void recordPresence( String username, PlanCommunity planCommunity );

}
