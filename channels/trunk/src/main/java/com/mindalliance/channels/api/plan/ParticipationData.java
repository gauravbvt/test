package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.entities.ActorData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationConfirmationService;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;

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

    private UserParticipation participation;
    private ChannelsUser user;
    private ActorData agentData;
    private Actor actor;
    private UserData userData;
    private boolean confirmed;
    private boolean active;

    public ParticipationData() {
        // required
    }

    public ParticipationData(
            String serverUrl,
            CommunityService communityService,
            ChannelsUser user,
            UserParticipation participation ) {
        this.participation = participation;
        this.user = user;
        init( serverUrl, communityService );
    }

    private void init( String serverUrl, CommunityService communityService ) {
        UserParticipationService userParticipationService = communityService.getUserParticipationService();
        UserParticipationConfirmationService userParticipationConfirmationService = communityService.getUserParticipationConfirmationService();
        actor = participation.getAgent( communityService).getActor();
        agentData = new ActorData( serverUrl, actor, communityService );
        userData = new UserData( user, communityService );
        confirmed = userParticipationConfirmationService.isConfirmedByAllSupervisors( participation, communityService );
        active = userParticipationService.isActive( participation, communityService );
    }

    @XmlElement
    public UserData getUser() {
        return userData;
    }

    @XmlElement
    public ActorData getAgent() {
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
