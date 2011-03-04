package com.mindalliance.channels.social;

/**
 * Presence event.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 2, 2010
 * Time: 12:42:32 PM
 */
public class PresenceEvent extends PlanningEvent {

    public enum Type {
        Login,
        Logout
    }

    private String username;
    private Type type;

    public PresenceEvent( Type type, String username, long planId ) {
        super( planId );
        this.username = username;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public Type getType() {
        return type;
    }

    public boolean isPresenceEvent() {
        return true;
    }

    public String toString() {
        return username
                + ( type == Type.Login ? " logged in" : " logged out" )
                + " " + super.toString();
    }

    public boolean isLogin() {
        return type == Type.Login;
    }

    public boolean isLogout() {
        return type == Type.Logout;
    }


}
