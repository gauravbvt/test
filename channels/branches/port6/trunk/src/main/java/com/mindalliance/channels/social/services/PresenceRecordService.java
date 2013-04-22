package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.PresenceListener;
import com.mindalliance.channels.social.model.PresenceRecord;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 3:40 PM
 */
public interface PresenceRecordService extends GenericSqlService<PresenceRecord, Long>, PresenceListener {

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
