package com.mindalliance.channels.api.community;

import com.mindalliance.channels.api.plan.UserData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.services.communities.UserParticipationConfirmationService;
import com.mindalliance.channels.db.services.communities.UserParticipationService;

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
 */@XmlType( propOrder = {"participant", "agent", "assignedBy", "accepted", "confirmed", "active" } )
public class UserParticipationData implements Serializable {

    private AgentData agent;
    private UserData participant;
    private boolean accepted;
    private boolean confirmed;
    private boolean active;
    private UserData assignedBy;

    public UserParticipationData() {
        // required
    }


    public UserParticipationData( String serverUrl, UserParticipation userParticipation, CommunityService communityService ) {
        UserParticipationService userParticipationService = communityService.getUserParticipationService();
        UserParticipationConfirmationService userParticipationConfirmationService
                = communityService.getUserParticipationConfirmationService();
        ChannelsUser participatingUser = new ChannelsUser( userParticipation.getParticipant( communityService ) );
        participant = new UserData( participatingUser, communityService );
        confirmed = userParticipationConfirmationService.isConfirmedByAllSupervisors( userParticipation, communityService );
        active = userParticipationService.isActive( userParticipation, communityService );
        agent = new AgentData( serverUrl, userParticipation.getAgent( communityService ), communityService );
        ChannelsUser assignedByUser = communityService.getUserRecordService().getUserWithIdentity( userParticipation.getUsername() );
        if ( assignedByUser != null ) {
            assignedBy = new UserData( assignedByUser, communityService );
        }
        accepted = userParticipation.isAccepted(  );
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }

    @XmlElement
    public AgentData getAgent() {
        return agent;
    }

    @XmlElement
    public UserData getAssignedBy() {
        return assignedBy;
    }

    @XmlElement
    public boolean isConfirmed() {
        return confirmed;
    }

    @XmlElement
    public UserData getParticipant() {
        return participant;
    }

    @XmlElement
    public boolean isAccepted() {
        return accepted;
    }
}
