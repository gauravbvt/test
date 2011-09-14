package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.model.Plan;

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
     * @param plan     a plan
     */
    void killIfAlive( String username, Plan plan );

    /**
     * Take not of heart beat signalling user since in a plan.
     *
     * @param username     a string
     * @param plan         a plan
     * @param refreshDelay the delay between heat beats
     */
    void keepAlive( String username, Plan plan, int refreshDelay );

    /**
     * Give the list of all users who have just left a plan.
     *
     * @param plan a plan
     * @return a list of user names as strings
     */
    List<String> giveMeYourDead( Plan plan );
}
