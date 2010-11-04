package com.mindalliance.channels.command;

/**
 * Presence listener.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 2, 2010
 * Time: 11:06:06 AM
 */
public interface PresenceListener {

    void loggedIn( String username );
    void loggedOut( String username );
}
