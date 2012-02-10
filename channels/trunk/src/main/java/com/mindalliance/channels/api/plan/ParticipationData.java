package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.core.model.Plan;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web service data element for a participation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 6:37 PM
 */@XmlType( propOrder = {"user", "agent"} )
public class ParticipationData {

    private Participation participation;
    private ChannelsUser user;
    private Plan plan;

    public ParticipationData() {
        // required
    }

    public ParticipationData( Participation participation, ChannelsUser user, Plan plan ) {
        this.participation = participation;
        this.user = user;
        this.plan = plan;
    }

    @XmlElement
    public UserData getUser() {
        return new UserData( user );
    }

    @XmlElement
    public AgentData getAgent() {
        return new AgentData( participation.getActor(), plan );
    }
}
