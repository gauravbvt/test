package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import com.mindalliance.channels.core.query.QueryService;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/12/12
 * Time: 2:24 PM
 */
@Entity
public class PlanParticipation extends AbstractPersistentPlanObject {

    @ManyToOne
    private ChannelsUserInfo participant;

    private long actorId;
    @OneToMany( mappedBy = "planParticipation", cascade = CascadeType.ALL)

    @Transient
    private List<PlanParticipationValidation> participationValidations;

    public PlanParticipation() {
    }

    public PlanParticipation( String username, Plan plan, ChannelsUser participatingUser ) {
        super( plan.getUri(), plan.getVersion(), username );
        this.participant = participatingUser.getUserInfo();
    }

    public PlanParticipation( String username, Plan plan, ChannelsUser participatingUser, Actor actor ) {
        this( username, plan, participatingUser );
        this.actorId = actor.getId();
    }

    public ChannelsUserInfo getParticipant() {
        return participant;
    }

    public void setParticipant( ChannelsUserInfo userInfo ) {
        this.participant = userInfo;
    }

    public long getActorId() {
        return actorId;
    }

    public void setActorId( long actorId ) {
        this.actorId = actorId;
    }

    public Actor getActor( QueryService queryService ) {
        try {
            return queryService.find( Actor.class, getActorId() );
        } catch ( NotFoundException e ) {
            return null;
        }
    }

    public boolean isObsolete( QueryService queryService ) {
        return getActor( queryService ) == null;
    }

    public String getParticipantUsername() {
        return getParticipant().getUsername();
    }

    public boolean isSupervised( QueryService queryService ) {
        Actor actor = getActor( queryService );
        return actor != null && actor.isSupervisedParticipation();
    }

    public List<PlanParticipationValidation> getParticipationValidations() {
        return participationValidations;
    }

    public void setParticipationValidations( List<PlanParticipationValidation> participationValidations ) {
        this.participationValidations = participationValidations;
    }
}
