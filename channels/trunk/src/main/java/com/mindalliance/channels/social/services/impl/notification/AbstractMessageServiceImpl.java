package com.mindalliance.channels.social.services.impl.notification;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.social.services.notification.Messageable;
import com.mindalliance.channels.social.services.notification.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract messaging service implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/8/12
 * Time: 12:21 PM
 */
abstract public class AbstractMessageServiceImpl implements MessagingService {

    @Autowired
    private ModelManager modelManager;

    @Autowired
    private UserRecordService userInfoService;

    protected List<UserRecord> getToUsers(
            Messageable messageable,
            String topic,
            CommunityService communityService ) {
        List<ChannelsUser> toUsers = new ArrayList<ChannelsUser>();
        List<String> toUsernames = messageable.getToUserNames( topic, communityService );
        for ( String toUsername : toUsernames ) {
            String urn = messageable.getPlanUri();
            if ( toUsername.equals( UserRecord.PLANNERS ) )
                toUsers = userInfoService.getDevelopers( urn );
            else if ( toUsername.equals( UserRecord.USERS ) )
                toUsers = userInfoService.getUsers( urn );
            else {
                ChannelsUser aUser = userInfoService.getUserWithIdentity( toUsername );
                if ( aUser != null )
                    toUsers.add( aUser );
            }
        }
        List<UserRecord> answer = new ArrayList<UserRecord>();
        for ( ChannelsUser toUser : toUsers ) {
            answer.add( toUser.getUserRecord() );
        }
        return answer;
    }

    protected UserRecord getFromUser( Messageable messageable, String topic ) {
        String fromUsername = messageable.getFromUsername( topic );
        if ( fromUsername != null ) {
            ChannelsUser fromUser = userInfoService.getUserWithIdentity( fromUsername );
            return fromUser.getUserRecord();
        } else {
            return null;
        }
    }

    protected CollaborationModel getPlan( Messageable messageable ) {
        return modelManager.getModel( messageable.getPlanUri(), messageable.getPlanVersion() );
    }

    protected ModelManager getModelManager() {
        return modelManager;
    }

    protected String getDefaultSupportCommunity() {
        return modelManager.getDefaultSupportCommunity();
    }

    protected String makeReportSubject(
            String planUri,
            List<? extends Messageable> messageables,
            String topic,
            CommunityService communityService) {
        StringBuilder sb = new StringBuilder();
        int n = messageables.size();
        String kind = messageables.get( 0 ).getLabel() + " " + topic;
        sb.append( kind )
                .append( " report" )
                .append( " (" )
                .append( n )
                .append( ") for collaboration " )
                .append( communityService.getPlanCommunity().isModelCommunity() ? "model " : "community " )
                .append( planUri );
        return sb.toString();
    }

    protected String makeReportContent(
            Messageable.Format format,
            List<? extends Messageable> messageables,
            String topic,
            CommunityService communityService ) {
        StringBuilder sb = new StringBuilder();
        for ( Messageable messageable : messageables ) {
            sb.append( messageable.getContent( topic, format, communityService ) );
            sb.append( "\n============================================\n\n" );
        }
        return sb.toString();
    }

    @Override
    public boolean sendInvitation( ChannelsUser fromUser, String emailAddress, String message ) {
        return false;
    }
}
