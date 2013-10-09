package com.mindalliance.channels.db.data.communities;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.db.data.AbstractChannelsDocument;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Community plan participation confirmation by supervisors
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 2:33 PM
 */
@Document( collection = "communities" )
public class UserParticipationConfirmation extends AbstractChannelsDocument {

    private String userParticipationUid;
    private long supervisorId;
    private String registeredOrganizationUid; // for supervisor

    public UserParticipationConfirmation() {
    }

    public UserParticipationConfirmation(
            UserParticipation userParticipation,
            Agent supervisor,
            String username ) {
        super( userParticipation.getCommunityUri(),
                userParticipation.getPlanUri(),
                userParticipation.getPlanVersion(),
                username );
        userParticipationUid = userParticipation.getUid();
        this.supervisorId = supervisor != null ? supervisor.getActorId() : -1;
        registeredOrganizationUid = supervisor != null ? supervisor.getRegisteredOrganizationUid() : null;
    }

    public UserParticipation getUserParticipation( CommunityService communityService ) {
        return communityService.getParticipationManager().getUserParticipation( userParticipationUid );
    }


    public RegisteredOrganization getRegisteredOrganizationn( CommunityService communityService ) {
        return registeredOrganizationUid != null
                ? communityService.getParticipationManager().getRegisteredOrganization( registeredOrganizationUid )
                : null;
    }

    public Agent getSupervisor( CommunityService communityService ) {
        Actor actor = getSupervisorActor( communityService );
        if ( actor == null ) return null;
        RegisteredOrganization registeredOrganization = getRegisteredOrganizationn( communityService );
        if ( registeredOrganization == null ) {
            return null;
        } else {
            return new Agent( actor, registeredOrganization, communityService );
        }
    }

    private Actor getSupervisorActor( CommunityService communityService ) {
        if ( supervisorId == -1 ) return null;
        try {
            return communityService.find( Actor.class, supervisorId, getCreated() );
        } catch ( NotFoundException e ) {
            return null;
        }
    }
}
