package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import com.mindalliance.channels.core.query.PlanService;

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
    private OrganizationRegistration organizationRegistration;

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
        this.organizationRegistration = supervisor != null ? supervisor.getOrganizationRegistration() : null;
    }

    public UserParticipation getUserParticipation() {
        return userParticipation;
    }


    public OrganizationRegistration getOrganizationRegistration() {
        return organizationRegistration;
    }

    public Agent getSupervisor( PlanCommunity planCommunity ) {
        Actor actor = getSupervisorActor( planCommunity.getPlanService() );
        if ( actor == null ) return null;
        if ( organizationRegistration == null ) {
            return new Agent( actor );
        } else {
            return new Agent( actor, organizationRegistration, planCommunity );
        }
    }

    private Actor getSupervisorActor( PlanService planService ) {
        if ( supervisorId == -1 ) return null;
        try {
            return planService.find( Actor.class, supervisorId );
        } catch ( NotFoundException e ) {
            return null;
        }
    }

}
