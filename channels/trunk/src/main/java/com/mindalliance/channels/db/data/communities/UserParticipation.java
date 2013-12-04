package com.mindalliance.channels.db.data.communities;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.AbstractChannelsDocument;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Community plan participation by a user as an actor in a registered organization (i.e. "position").
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 2:31 PM
 */
@Document(collection = "communities")
public class UserParticipation extends AbstractChannelsDocument implements Messageable {

    public static final String VALIDATION_REQUESTED = "validation requested";
    public static final String ACCEPTANCE_REQUESTED = "acceptance requested";


    private String participantUsername;
    private long actorId;
    private String registeredOrganizationUid;
    private String supervisorsNotified;
    private boolean accepted;
    private Date whenAccepted;
    private boolean requestNotified;
    private boolean linked = false;

    public UserParticipation() {
    }

    public UserParticipation( String username, ChannelsUser participatingUser, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
        this.participantUsername = participatingUser.getUsername();
    }

    public UserParticipation( String username,
                              ChannelsUser participatingUser,
                              Agent agent,
                              PlanCommunity planCommunity ) {
        this( username, participatingUser, planCommunity );
        this.actorId = agent.getActorId();
        registeredOrganizationUid = agent.getRegisteredOrganizationUid(); // can be null if agent is actor in known organization
    }

    public UserParticipation( UserParticipation participation ) {
        super( participation.getCommunityUri(),
                participation.getPlanUri(),
                participation.getPlanVersion(),
                participation.getUsername() );
        participantUsername = participation.getParticipantUsername();
        actorId = participation.getActorId();
        registeredOrganizationUid = participation.getRegisteredOrganizationUid();
    }

    public String getRegisteredOrganizationUid() {
        return registeredOrganizationUid;
    }

    public boolean isLinked() {
        return linked;
    }

    public void setLinked( boolean linked ) {
        this.linked = linked;
        this.accepted = true;
    }

    public UserRecord getParticipant( CommunityService communityService ) {
        ChannelsUser participant = communityService.getUserRecordService().getUserWithIdentity( participantUsername );
        return participant != null ? participant.getUserRecord() : null;
    }

    public boolean isAccepted() {
        return isLinked() || accepted;
    }

    public void setAccepted( boolean accepted ) {
        this.accepted = accepted;
        whenAccepted = accepted ? new Date() : null;
    }

    public long getActorId() {
        return actorId;
    }

    public Agent getAgent( CommunityService communityService ) {
        Actor actor = getActor( communityService );
        if ( actor == null || registeredOrganizationUid == null ) return null;
        RegisteredOrganization registeredOrganization = getRegisteredOrganization( communityService );
        return registeredOrganization != null
                ? new Agent( actor, registeredOrganizationUid, communityService )
                : null;
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

    private Actor getActor( CommunityService communityService ) {
        try {
            return communityService.find( Actor.class, getActorId(), getCreated() );
        } catch ( NotFoundException e ) {
            return null;
        }
    }

    public boolean isRequested() {
        return !participantUsername.equals( getUsername() );
    }

    public String getParticipantUsername() {
        return participantUsername;
    }

    public boolean isSupervised( CommunityService communityService ) {
        if ( isLinked() ) return false;
        Actor actor = getActor( communityService );
        return actor != null && actor.isSupervisedParticipation();
    }

    public Date getWhenAccepted() {
        return whenAccepted;
    }

    public void setWhenAccepted( Date whenAccepted ) {
        this.whenAccepted = whenAccepted;
    }

    public RegisteredOrganization getRegisteredOrganization( CommunityService communityService ) {
        return registeredOrganizationUid == null
                ? null
                : communityService.getParticipationManager().getRegisteredOrganization( registeredOrganizationUid );
    }


    public boolean isForAgent( Agent agent ) {
        return actorId == agent.getActorId()
                && ( ChannelsUtils.areEqualOrNull( registeredOrganizationUid, agent.getRegisteredOrganizationUid() ) );
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

    public String asString( CommunityService communityService ) {
        StringBuilder sb = new StringBuilder();
        Agent agent = getAgent( communityService );
        UserRecord participant = getParticipant( communityService );
        if ( participant == null ) {
            return "?";
        } else {
            sb.append( participant.getFullName() )
                    .append( " (" )
                    .append( participant.getEmail() )
                    .append( ") participating" )
                    .append( " as " )
                    .append( agent == null ? "?" : agent.getName() );
            return sb.toString();
        }
    }

    public String getParticipantFullName( CommunityService communityService ) {
        return getParticipant( communityService ).getFullName();
    }

    /// Messageable

    @Override
    public String getContent( String topic, Format format, CommunityService communityService ) {
        if ( topic.equals( VALIDATION_REQUESTED ) ) {
            return "As supervisor, you are requested to confirm "
                    + asString( communityService )
                    + " in community " + communityService.getPlanCommunity().getName()
                    + ".\n\nThank you!\n";
        } else if ( topic.equals( ACCEPTANCE_REQUESTED ) ) {
            return "You are requested to participate as "
                    + getAgent( communityService ).getName()
                    + " in community " + communityService.getPlanCommunity().getName()
                    + ". You are invited to either accept or reject the request."
                    + "\n\nThank you!\n";
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    @Override
    public List<String> getToUserNames( String topic, CommunityService communityService ) {
        if ( topic.equals( VALIDATION_REQUESTED ) ) {
            return communityService.getParticipationManager()
                    .listSupervisorsToNotify( this, communityService );
        } else if ( topic.equals( ACCEPTANCE_REQUESTED ) ) {
            List<String> usernames = new ArrayList<String>();
            usernames.add( participantUsername );
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
    public String getSubject( String topic, Format format, CommunityService communityService ) {
        if ( topic.equals( VALIDATION_REQUESTED ) ) {
            return "Request to confirm " + asString( communityService );
        } else if ( topic.equals( ACCEPTANCE_REQUESTED ) ) {
            return "Your participation is requested as " + getAgent( communityService ).getName();
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    @Override
    public String getLabel() {
        return "Participation";
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof UserParticipation ) {
            UserParticipation other = (UserParticipation) object;
            return participantUsername.equals( other.getParticipantUsername() )
                    && actorId == other.getActorId()
                    && ChannelsUtils.areEqualOrNull( registeredOrganizationUid, other.getRegisteredOrganizationUid() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + participantUsername.hashCode();
        hash = hash * 31 + Long.valueOf( actorId ).hashCode();
        if ( registeredOrganizationUid != null ) hash = hash * 31 + registeredOrganizationUid.hashCode();
        return hash;
    }


}
