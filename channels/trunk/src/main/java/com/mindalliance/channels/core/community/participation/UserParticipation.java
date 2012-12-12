package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
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
public class UserParticipation extends AbstractPersistentChannelsObject implements Messageable {

    public static final String VALIDATION_REQUESTED = "validation requested";
    public static final String ACCEPTANCE_REQUESTED = "acceptance requested";

    @ManyToOne
    private ChannelsUserInfo participant;

    private long actorId;

    /**
     * User participates as actor in registered organization derived from actor in placeholder organization.
     */
    @ManyToOne
    private OrganizationRegistration organizationRegistration;

    private String supervisorsNotified;

    private boolean accepted;

    private Date whenAccepted;

    private boolean requestNotified;

    @OneToMany( mappedBy = "userParticipation", cascade = CascadeType.ALL )
    @Transient
    private List<UserParticipationConfirmation> participationConfirmations;


    public UserParticipation() {
    }

    public UserParticipation( String username, ChannelsUser participatingUser, PlanCommunity planCommunity ) {
        super( planCommunity.getPlan().getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
        this.participant = participatingUser.getUserInfo();
    }

    public UserParticipation( String username,
                              ChannelsUser participatingUser,
                              Agent agent,
                              PlanCommunity planCommunity ) {
        this( username, participatingUser, planCommunity );
        this.actorId = agent.getActorId();
        organizationRegistration = agent.getOrganizationRegistration();
    }

    public UserParticipation( UserParticipation participation ) {
        super( participation.getCommunityUri(),
                participation.getPlanUri(),
                participation.getPlanVersion(),
                participation.getUsername() );
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

    public Agent getAgent( PlanCommunity planCommunity ) {
        Actor actor = getActor( planCommunity );
        if ( actor == null ) return null;
        if ( organizationRegistration == null ) {
            return new Agent( actor );
        } else {
            return new Agent( actor, organizationRegistration, planCommunity );
        }
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

    private Actor getActor( PlanCommunity planCommunity ) {
        try {
            return planCommunity.getPlanService().find( Actor.class, getActorId() );
        } catch ( NotFoundException e ) {
            return null;
        }
    }

    public boolean isRequested() {
        return !participant.getUsername().equals( getUsername() );
    }

    public String getParticipantUsername() {
        return getParticipant().getUsername();
    }

    public boolean isSupervised( PlanCommunity planCommunity ) {
        Actor actor = getActor( planCommunity );
        return actor != null && actor.isSupervisedParticipation();
    }

    public List<UserParticipationConfirmation> getParticipationConfirmations() {
        return participationConfirmations;
    }

    public void setParticipationConfirmations( List<UserParticipationConfirmation> participationConfirmations ) {
        this.participationConfirmations = participationConfirmations;
    }

    public Date getWhenAccepted() {
        return whenAccepted;
    }

    public void setWhenAccepted( Date whenAccepted ) {
        this.whenAccepted = whenAccepted;
    }

    public OrganizationRegistration getOrganizationRegistration() {
        return organizationRegistration;
    }

    public void setOrganizationRegistration( OrganizationRegistration organizationRegistration ) {
        this.organizationRegistration = organizationRegistration;
    }

    public boolean isForAgent( Agent agent ) {
        return actorId == agent.getActorId()
                && ( ChannelsUtils.bothNullOrEqual( organizationRegistration, agent.getOrganizationRegistration() ) );
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

    public String asString( PlanCommunity planCommunity ) {
        StringBuilder sb = new StringBuilder();
        Agent agent = getAgent( planCommunity );
        sb.append( participant.getFullName() )
                .append( " (" )
                .append( participant.getEmail() )
                .append( ") participating as " )
                .append( agent == null ? "?" : agent.getName() );
        return sb.toString();
    }

    public String getUserFullName() {
        return getParticipant().getFullName();
    }

    /// Messageable

    @Override
    public String getContent( String topic, Format format, PlanCommunity planCommunity ) {
        if ( topic.equals( VALIDATION_REQUESTED ) ) {
            return "As supervisor, you are requested to confirm "
                    + asString( planCommunity )
                    + "\n\nThank you!\n"
                    + planCommunity.getPlan().getClient();
        } else if ( topic.equals( ACCEPTANCE_REQUESTED ) ) {
            return "You are requested to participate as "
                    + getAgent( planCommunity ).getName()
                    + ". It is up to you to accept or not."
                    + "\n\nThank you!\n"
                    + planCommunity.getPlan().getClient();
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    @Override
    public List<String> getToUserNames( String topic, PlanCommunity planCommunity ) {
        if ( topic.equals( VALIDATION_REQUESTED ) ) {
            return planCommunity.getUserParticipationService()
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
    public String getSubject( String topic, Format format, PlanCommunity planCommunity ) {
        if ( topic.equals( VALIDATION_REQUESTED ) ) {
            return "Request to confirm " + asString( planCommunity );
        } else if ( topic.equals( ACCEPTANCE_REQUESTED ) ) {
            return "Your participation is requested as " + getAgent( planCommunity ).getName();
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    @Override
    public String getLabel() {
        return "Participation";
    }

}
