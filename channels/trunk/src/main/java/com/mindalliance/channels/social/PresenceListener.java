package com.mindalliance.channels.social;

import java.util.List;

/**
 * Presence listener.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 2, 2010
 * Time: 11:06:06 AM
 */
public interface PresenceListener {

    /**
     * Record a user explicitly leaving a plan.
     *
     * @param username a string
     * @param uri a sanitized plan uri
     */
    void killIfAlive( String username, String uri );

    /**
     * Take note of heart beat signalling user since in a plan.
     *
     * @param username     a string
     * @param uri a sanitized plan uri
     * @param refreshDelay the delay between heat beats
     */
    void keepAlive( String username, String uri, int refreshDelay );

    /**
     * Give the list of all users who have just left a plan.
     *
     * @param uri a sanitized plan uri
     * @return a list of user names as strings
     */
    List<String> giveMeYourDead( String uri );
}
