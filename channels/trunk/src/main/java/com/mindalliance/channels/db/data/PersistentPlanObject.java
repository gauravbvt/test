package com.mindalliance.channels.db.data;

import com.mindalliance.channels.core.ChannelsLockable;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Identifiable;

import java.util.Date;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 11:16 AM
 */
public interface PersistentPlanObject extends Timestamped, Identifiable, ChannelsLockable {

    /**
     * Get the date associated with this object.
     *
     * @return a creation date, usually
     */
    Date getCreated();

    /**
     * Get the unique id of this object.
     *
     * @return a string
     */
    long getId();

    /**
     * Get the uri of the plan this belongs to.
     *
     * @return string
     */
    String getCommunityUri();

    /**
     * Get the plan's version.
     *
     * @return an int
     */
    int getPlanVersion();

    /**
     * Get the username of the owner.
     *
     * @return a string
     */
    String getUsername();

    /**
     * Get the full name of the owner.
     *
     * @param communityService a community service
     * @return a string
     */
    String getUserFullName( CommunityService communityService );

}
