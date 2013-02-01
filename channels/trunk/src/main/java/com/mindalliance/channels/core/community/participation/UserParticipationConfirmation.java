package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Plan participation confirmation by supervisors.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/23/12
 * Time: 11:49 AM
 */
@Entity
public class UserParticipationConfirmation extends AbstractPersistentChannelsObject {

    @ManyToOne
    private UserParticipation userParticipation;
    private long supervisorId;
    @ManyToOne
    private OrganizationParticipation organizationParticipation;  // for supervisor

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
        this.userParticipation = userParticipation;
        this.supervisorId =  supervisor != null ? supervisor.getActorId() : -1;
        this.organizationParticipation = supervisor != null ? supervisor.getOrganizationParticipation() : null;
    }

    public UserParticipation getUserParticipation() {
        return userParticipation;
    }


    public OrganizationParticipation getOrganizationParticipation() {
        return organizationParticipation;
    }

    public Agent getSupervisor( PlanCommunity planCommunity ) {
        Actor actor = getSupervisorActor( planCommunity );
        if ( actor == null ) return null;
        if ( organizationParticipation == null ) {
            return new Agent( actor );
        } else {
            return new Agent( actor, organizationParticipation, planCommunity );
        }
    }

    private Actor getSupervisorActor( PlanCommunity planCommunity ) {
        if ( supervisorId == -1 ) return null;
        try {
            return planCommunity.find( Actor.class, supervisorId, getCreated() );
        } catch ( NotFoundException e ) {
            return null;
        }
    }

}
