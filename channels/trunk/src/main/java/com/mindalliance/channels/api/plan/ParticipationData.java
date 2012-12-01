package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.PlanParticipation;
import com.mindalliance.channels.core.community.participation.PlanParticipationService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web service data element for a participation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 6:37 PM
 */@XmlType( propOrder = {"user", "agent", "open", "restrictedToEmployed", "singular",
        "supervised", "accepted", "confirmed", "active" } )
public class ParticipationData  implements Serializable {

    private PlanParticipation participation;
    private ChannelsUser user;
    private AgentData agentData;
    private Actor actor;
    private UserData userData;
    private boolean confirmed;
    private boolean active;

    public ParticipationData() {
        // required
    }

    public ParticipationData(
            String serverUrl,
            PlanCommunity planCommunity,
            ChannelsUser user,
            PlanParticipation participation ) {
        this.participation = participation;
        this.user = user;
        init( serverUrl, planCommunity );
    }

    private void init( String serverUrl, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        PlanParticipationService planParticipationService = planCommunity.getPlanParticipationService();
        agentData = new AgentData( serverUrl, participation.getActor( planService), planCommunity.getPlan() );
        actor = participation.getActor( planService );
        userData = new UserData( user, planService );
        confirmed = planParticipationService.isValidatedByAllSupervisors( participation, planCommunity );
        active = planParticipationService.isActive( participation, planCommunity );
    }

    @XmlElement
    public UserData getUser() {
        return userData;
    }

    @XmlElement
    public AgentData getAgent() {
        return agentData;
    }

    @XmlElement
    public boolean getOpen() {
        return actor != null && actor.isOpenParticipation();
    }

    @XmlElement
    public boolean getRestrictedToEmployed() {
        return actor != null && actor.isParticipationRestrictedToEmployed();
    }

    @XmlElement
    public boolean getSupervised() {
        return actor != null && actor.isSupervisedParticipation();
    }

    @XmlElement
    public boolean getAccepted() {
        return participation.isAccepted();
    }

    @XmlElement
    public boolean getConfirmed() {
        return confirmed;
    }

    @XmlElement
    public boolean getSingular() {
        return actor != null && actor.isSingularParticipation();
    }

    @XmlElement
    public boolean getActive() {
        return active;
    }


}
