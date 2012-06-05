package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web service data element for a participation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 6:37 PM
 */@XmlType( propOrder = {"user", "agent", "open", "restrictedToEmployed"} )
public class ParticipationData {

    private PlanParticipation participation;
    private ChannelsUser user;
    private PlanService planService;

    public ParticipationData() {
        // required
    }

    public ParticipationData( PlanParticipation participation, ChannelsUser user, PlanService planService ) {
        this.participation = participation;
        this.user = user;
        this.planService = planService;
    }

    @XmlElement
    public UserData getUser() {
        return new UserData( user );
    }

    @XmlElement
    public AgentData getAgent() {
        return new AgentData( participation.getActor( planService), planService.getPlan() );
    }

    @XmlElement
    public boolean getOpen() {
        Actor actor = participation.getActor( planService );
        return actor != null && actor.isOpenParticipation();
    }

    @XmlElement
    public boolean getRestrictedToEmployed() {
        Actor actor = participation.getActor( planService );
        return actor != null && actor.isParticipationRestrictedToEmployed();
    }

}
