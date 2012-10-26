package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.apache.commons.lang.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/12/12
 * Time: 2:24 PM
 */
@Entity
public class PlanParticipation extends AbstractPersistentPlanObject implements Messageable {

    public static final String VALIDATION_REQUESTED = "validation requested";

    @ManyToOne
    private ChannelsUserInfo participant;

    private long actorId;

    private String supervisorsNotified;

    @OneToMany( mappedBy = "planParticipation", cascade = CascadeType.ALL )
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

    public String getSupervisorsNotified() {
        return supervisorsNotified == null ? "" : supervisorsNotified;
    }

    public void setSupervisorsNotified( String supervisorsNotified ) {
        this.supervisorsNotified = supervisorsNotified;
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

    public List<String> usersNotifiedToValidate() {
        return new ArrayList<String>( Arrays.asList( getSupervisorsNotified().split( "," ) ) );
    }

    public void addUserNotifiedToValidate( String username ) {
        List<String> notifiedUsers = usersNotifiedToValidate();
        if ( !username.isEmpty() && !notifiedUsers.contains( username ) ) {
            notifiedUsers.add( username );
        }
        supervisorsNotified = StringUtils.join( notifiedUsers.iterator(), "," );
    }

    public String asString( PlanService planService ) {
        StringBuilder sb = new StringBuilder();
        Actor actor = getActor( planService );
        sb.append( participant.getFullName() )
                .append( " (" )
                .append( participant.getEmail() )
                .append( ") participating as " )
                .append( actor == null ? "?" : actor.getName() );
        return sb.toString();
    }

    /// Messageable

    @Override
    public String getContent( String topic, Format format, PlanService planService ) {
        return "As supervisor, you are requested to confirm "
                + asString( planService )
                + "\n\nThank you!\n"
                + planService.getPlan().getClient();
    }

    @Override
    public List<String> getToUserNames( String topic, PlanService planService ) {
        return planService.getPlanParticipationService()
                .listSupervisorsToNotify( planService.getPlan(), this, planService );
    }

    @Override
    public String getFromUsername( String topic ) {
        return null;
    }

    @Override
    public String getSubject( String topic, Format format, PlanService planService ) {
        return "Request to confirm " + asString( planService );
    }

    @Override
    public String getLabel() {
        return "Participation";
    }
}
