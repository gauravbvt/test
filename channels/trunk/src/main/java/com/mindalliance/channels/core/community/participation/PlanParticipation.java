package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
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
import java.util.Date;
import java.util.List;

/**
 * A user's participation in a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/12/12
 * Time: 2:24 PM
 */
@Entity
public class PlanParticipation extends AbstractPersistentPlanObject implements Messageable {

    public static final String VALIDATION_REQUESTED = "validation requested";
    public static final String ACCEPTANCE_REQUESTED = "acceptance requested";

    @ManyToOne
    private ChannelsUserInfo participant;

    private long actorId;

    private String supervisorsNotified;

    private boolean accepted;

    private Date whenAccepted;

    private boolean requestNotified;

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

    public PlanParticipation( PlanParticipation participation ) {
        super( participation.getPlanUri(), participation.getPlanVersion(), participation.getUsername() );
        this.participant = participation.getParticipant();
        this.actorId = participation.getActorId();
    }

    public ChannelsUserInfo getParticipant() {
        return participant;
    }

    public void setParticipant( ChannelsUserInfo userInfo ) {
        this.participant = userInfo;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted( boolean accepted ) {
        this.accepted = accepted;
        whenAccepted = accepted ? new Date( ) : null;
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

    public boolean isRequestNotified() {
        return requestNotified;
    }

    public void setRequestNotified( boolean requestNotified ) {
        this.requestNotified = requestNotified;
    }

    public Actor getActor( QueryService queryService ) {
        try {
            return queryService.find( Actor.class, getActorId() );
        } catch ( NotFoundException e ) {
            return null;
        }
    }

    public boolean isRequested() {
        return !participant.getUsername().equals( getUsername() );
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

    public String getUserFullName() {
        return getParticipant().getFullName();
    }

    /// Messageable

    @Override
    public String getContent( String topic, Format format, PlanService planService ) {
        if ( topic.equals( VALIDATION_REQUESTED ) ) {
            return "As supervisor, you are requested to confirm "
                    + asString( planService )
                    + "\n\nThank you!\n"
                    + planService.getPlan().getClient();
        } else if ( topic.equals( ACCEPTANCE_REQUESTED ) ) {
            return "You are requested to participate as "
                    + getActor( planService ).getName()
                    + ". It is up to you to accept or not."
                    + "\n\nThank you!\n"
                    + planService.getPlan().getClient();
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    @Override
    public List<String> getToUserNames( String topic, PlanCommunity planCommunity ) {
        if ( topic.equals( VALIDATION_REQUESTED ) ) {
            return planCommunity.getPlanParticipationService()
                .listSupervisorsToNotify( this, planCommunity );
        } else if ( topic.equals( ACCEPTANCE_REQUESTED ) ) {
            List<String> usernames = new ArrayList<String>();
            usernames.add( participant.getUsername() );
            return usernames;
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    @Override
    public String getFromUsername( String topic ) {
        return null;
    }

    @Override
    public String getSubject( String topic, Format format, PlanService planService ) {
        if ( topic.equals( VALIDATION_REQUESTED ) ) {
            return "Request to confirm " + asString( planService );
        } else if ( topic.equals( ACCEPTANCE_REQUESTED ) ) {
            return "Your participation is requested as " + getActor( planService ).getName();
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    @Override
    public String getLabel() {
        return "Participation";
    }

}
