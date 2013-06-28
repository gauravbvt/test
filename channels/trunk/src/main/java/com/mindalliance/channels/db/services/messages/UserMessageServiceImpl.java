package com.mindalliance.channels.db.services.messages;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.messages.QUserMessage;
import com.mindalliance.channels.db.data.messages.UserMessage;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.repositories.UserMessageRepository;
import com.mindalliance.channels.db.services.AbstractDataService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/26/13
 * Time: 10:35 AM
 */
@Component
public class UserMessageServiceImpl extends AbstractDataService<UserMessage> implements UserMessageService {

    @Autowired
    private UserMessageRepository repository;

    @Autowired
    private UserRecordService userInfoService;

    private Map<String, Date> whenLastChanged = new HashMap<String, Date>();

    @Override
    public void save( UserMessage userMessage ) {
        repository.save( userMessage );
    }

    @Override
    public UserMessage load( String uid ) {
        return repository.findOne( uid );
    }


    @Override
    public int countNewFeedbackReplies( CommunityService communityService, ChannelsUser user ) {
        QUserMessage qUserMessage = QUserMessage.userMessage;
        return toInteger( repository.count(
                qUserMessage.communityUri.eq( communityService.getPlanCommunity().getUri() )
                        .and( qUserMessage.toUsername.eq( user.getUsername() ) )
                        .and( qUserMessage.feedbackId.isNotNull()
                                .and( qUserMessage.read.isFalse() ) )
        ) );
    }

    @Override
    public void sendMessage( UserMessage message, boolean emailIt ) {
        message.setSendNotification( emailIt );
        getDb().insert( message );
        changed( message.getPlanUri() );
    }

    @Override
    public void deleteMessage( UserMessage message ) {
        getDb().remove( message );
        changed( message.getPlanUri() );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Iterator<UserMessage> getReceivedMessages( final String username, final CommunityService communityService ) {
        String[] toValues = new String[3];
        toValues[0] = username;
        toValues[1] = UserRecord.PLANNERS;
        toValues[2] = UserRecord.USERS;
        QUserMessage qUserMessage = QUserMessage.userMessage;
        Iterator<UserMessage> iterator = repository.findAll(
                qUserMessage.communityUri.eq( communityService.getPlanCommunity().getUri() )
                        .and( qUserMessage.toUsername.in( toValues ) ),
                qUserMessage.created.desc()
        ).iterator();
        return (Iterator<UserMessage>) IteratorUtils.filteredIterator(
                iterator,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserMessage userMessage = (UserMessage) object;
                        return ( !userMessage.isToAllPlanners()
                                || userInfoService.isPlanner( username, communityService.getPlan().getUri() ) )
                                && ( !userMessage.isToAllUsers()
                                || userInfoService.isParticipant( username, communityService.getPlan().getUri() ) );
                    }
                } );
    }

    @Override
    public Iterator<UserMessage> getSentMessages( String username, CommunityService communityService ) {
        QUserMessage qUserMessage = QUserMessage.userMessage;
        return repository.findAll(
                qUserMessage.communityUri.eq( communityService.getPlanCommunity().getUri() )
                        .and( qUserMessage.username.eq( username ) ),
                qUserMessage.created.desc()
        ).iterator();
    }

    @Override
    public Date getWhenLastReceived( String username, CommunityService communityService ) {
        Iterator<UserMessage> received = getReceivedMessages( username, communityService );
        if ( received.hasNext() ) {
            return received.next().getCreated();
        } else {
            return null;
        }
    }

    @Override
    public void markSent( UserMessage message ) {
        message.setWhenNotificationSent( new Date() );
        getDb().updateFirst( new Query( where( "_id" ).is( new ObjectId( message.getUid() ) ) ),
                new Update().set( "whenNotificationSent", new Date() ),
                UserMessage.class );
    }

    @Override
    public Iterator<UserMessage> listMessagesToSend( String communityUri ) {
        QUserMessage qUserMessage = QUserMessage.userMessage;
        return repository.findAll(
                qUserMessage.communityUri.eq( communityUri )
                        .and( qUserMessage.sendNotification.isTrue() )
                        .and( qUserMessage.whenNotificationSent.isNull() ),
                qUserMessage.created.desc()
        ).iterator();
    }

    @Override
    public void markToNotify( UserMessage message ) {
        message.setSendNotification( true );
        getDb().updateFirst(
                new Query( where( "_id" ).is( new ObjectId( message.getUid() ) ) ),
                new Update().set( "sendNotification",true ),
                UserMessage.class );
    }

    @Override
    public Date getWhenLastChanged( String planUri ) {
        return whenLastChanged.get( planUri );
    }


    private void changed( String urn ) {
        whenLastChanged.put( urn, new Date() );
    }

}
