package com.mindalliance.channels.social;

import com.mindalliance.channels.core.command.CommandListener;
import com.mindalliance.channels.core.command.PresenceListener;

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
     * @param uri a sanitized plan uri
     * @return a date
     */
    Date getWhenLastChanged( String uri );

    /**
     * Get an iterator on the command events recorded for a plan.
     *
     * @param uri a string
     * @return an iterator on command event
     */
    Iterator<CommandEvent> getCommandEvents( String uri );

    /**
     * Find latest presence change for a user in a plan (entering or leaving).
     *
     * @param username a string
     * @param uri a sanitized plan uri
     * @return a presence event
     */
    PresenceEvent findLatestPresence( String username, String uri );

    /**
     * Has a keep-alive heartbeat been heard recently?
     *
     * @param username a string
     * @param uri a sanitized plan uri
     * @return a boolean
     */
    boolean isAlive( String username, String uri );
}
