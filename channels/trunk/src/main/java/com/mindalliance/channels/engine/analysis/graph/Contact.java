package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.util.ChannelsUtils;

/**
 * A user as agent or an agent with no associated user.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/13/13
 * Time: 12:25 PM
 */
public class Contact implements Identifiable {

    private ChannelsUser user;
    private Agent agent;

    public Contact( Agent agent, ChannelsUser user ) {
        this.agent = agent;
        this.user = user;
    }

    public Contact( Agent agent ) {
        this.agent = agent;
    }

    public Agent getAgent() {
        return agent;
    }

    public ChannelsUser getUser() {
        return user;
    }

    // Identifiable


    @Override
    public String getClassLabel() {
        return getTypeName();
    }

    @Override
    public long getId() {
        long id = agent.getId();
        if ( user != null ) {
            id += (Long.MAX_VALUE / 2 ) + user.getUserRecord().getId();
        }
        return id;
    }

    @Override
    public String getDescription() {
        return agent.getDescription();
    }

    @Override
    public String getTypeName() {
        return "contact";
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    @Override
    public String getName() {
        return user != null
                ? user.getFullName() + " as " + agent.getName()
                : agent.getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof Contact ) {
            Contact other = (Contact)object;
            return agent.equals( other.getAgent() )
                    && ChannelsUtils.areEqualOrNull( user, other.getUser() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + agent.hashCode();
        if ( user != null )
            hash = hash * 31 + user.hashCode();
        return hash;
    }

    public String getUid() {
        return "_"
                + agent.getRegisteredOrganizationUid()
                + "_" + agent.getActorId()
                + ( user == null ? "" : "_" + user.getUserRecord().getUid() );
    }

    public boolean isForUser( ChannelsUser user ) {
        return this.user != null && this.user.equals( user );
    }
}
