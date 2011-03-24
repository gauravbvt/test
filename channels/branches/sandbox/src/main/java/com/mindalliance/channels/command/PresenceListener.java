package com.mindalliance.channels.command;

import com.mindalliance.channels.model.Plan;

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

    void inactive( String username, Plan plan );
    void keepAlive( String username, Plan plan, int refreshDelay );
    List<String> giveMeYourDead( Plan plan );
}
