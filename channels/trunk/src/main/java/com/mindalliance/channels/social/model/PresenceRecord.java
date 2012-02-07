package com.mindalliance.channels.social.model;

import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;

import javax.persistence.Entity;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 3:10 PM
 */
@Entity
public class PresenceRecord extends AbstractPersistentPlanObject {

    public PresenceRecord( Type type, String username, String uri ) {
        super( uri, username );
        this.type = type;
    }

    public enum Type {
        Active,
        Inactive
    }

    private Type type;

    public PresenceRecord() {}

    public PresenceRecord( Type type ) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String toString() {
        return getUsername()
                + ( type == Type.Active ? " logged in" : " logged out" )
                + " " + super.toString();
    }

    public boolean isEntering() {
        return type == Type.Active;
    }

    public boolean isLeaving() {
        return type == Type.Inactive;
    }

    public void setType( Type type ) {
        this.type = type;
    }
}
