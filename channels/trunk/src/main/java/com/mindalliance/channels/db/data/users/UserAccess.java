package com.mindalliance.channels.db.data.users;

import com.mindalliance.channels.core.util.ChannelsUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/24/13
 * Time: 10:43 AM
 */
public class UserAccess implements Serializable {

    private String contextUri;
    private UserRole userRole = UserRole.Participant;
    private Date date;

    public enum UserRole {
        Disabled,
        Admin,
        Planner,
        Participant;

        public String getLabel() {
            return this == Planner
                    ? "Template developer"
                    : this.name();
        }

        public static UserRole fromLabel( String label ) {
            return label.equals( "Template developer")
                    ? Planner
                    : valueOf( label );
        }

    }

    public UserAccess() {
    }

    public UserAccess( UserRole userRole ) {
        this.userRole = userRole;
        date = new Date();
    }

    public UserAccess ( UserAccess userAccess ) {
        date = userAccess.getDate();
        contextUri = userAccess.getContextUri();
        userRole = userAccess.getUserRole();
    }

    public UserAccess( String contextUri, UserRole userRole ) {
        assert userRole != UserRole.Disabled && userRole != UserRole.Admin;
        this.contextUri = contextUri;
        setUserRole( userRole );
    }

    public boolean isForContext( String uri ) {
        return contextUri != null && contextUri.equals( uri );
    }

    public boolean isAdmin() {
        return userRole == UserRole.Admin;
    }

    public boolean isDisabled() {
        return userRole == UserRole.Disabled;
    }

    public String getContextUri() {
        return contextUri;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole( UserRole userRole ) {
        this.userRole = userRole;
        date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public boolean isParticipantOf( String uri ) {
        return contextUri != null
                && contextUri.equals( uri )
                && isParticipant();
    }

    public boolean isPlannerOf( String uri ) {
        return contextUri != null
                && contextUri.equals( uri )
                && isPlanner();
    }


    public boolean isParticipant() {
        return userRole == UserRole.Participant;
    }

    public boolean isPlanner() {
        return userRole == UserRole.Planner;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( userRole.name() );
        if ( contextUri != null ) {
            sb.append( " for " )
                    .append( contextUri );
        }
        return sb.toString();
    }

    @Override
    public boolean equals( Object object ) {
       if ( object instanceof  UserAccess ) {
          UserAccess other = (UserAccess)object;
           return userRole == other.getUserRole()
                   && ChannelsUtils.areEqualOrNull( contextUri, other.getContextUri() );       } else {
           return false;
       }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash + 31 * userRole.hashCode();
        if ( contextUri != null )
            hash = hash + 31 * contextUri.hashCode();
        return hash;
    }

}
