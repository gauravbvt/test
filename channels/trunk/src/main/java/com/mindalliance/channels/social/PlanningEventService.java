package com.mindalliance.channels.social;

import com.mindalliance.channels.core.command.CommandListener;
import com.mindalliance.channels.core.command.PresenceListener;
import com.mindalliance.channels.core.model.Plan;

import java.util.Date;
import java.util.Iterator;

/**
 * Planning event service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2010
 * Time: 2:03:58 PM
 */
public interface PlanningEventService extends CommandListener, PresenceListener {
    /**
     * Get the date when a plan was last changed.
     *
     * @param plan a plan
     * @return a date
     */
    Date getWhenLastChanged( Plan plan );

    /**
     * Get an iterator on the command events recorded for a plan.
     *
     * @param plan a plan
     * @return an iterator on command event
     */
    Iterator<CommandEvent> getCommandEvents( Plan plan );

    /**
     * Find latest presence change for a user in a plan (entering or leaving).
     *
     * @param username a string
     * @param plan     a plan
     * @return a presence event
     */
    PresenceEvent findLatestPresence( String username, Plan plan );

    /**
     * Has a keep-alive heartbeat been heard recently?
     *
     * @param username a string
     * @param plan     a plan
     * @return a boolean
     */
    boolean isAlive( String username, Plan plan );
}
