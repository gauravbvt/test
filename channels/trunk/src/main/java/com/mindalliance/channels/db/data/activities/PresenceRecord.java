package com.mindalliance.channels.db.data.activities;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.db.data.AbstractChannelsDocument;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A user presence record.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/18/13
 * Time: 5:07 PM
 */
@Document( collection = "activities" )
public class PresenceRecord extends AbstractChannelsDocument {

    public PresenceRecord( Type type, String username, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
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
                + " at " + getFormattedCreated();
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
