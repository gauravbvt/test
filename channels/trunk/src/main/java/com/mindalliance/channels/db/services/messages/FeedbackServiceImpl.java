package com.mindalliance.channels.db.services.messages;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.data.messages.QFeedback;
import com.mindalliance.channels.db.data.messages.UserMessage;
import com.mindalliance.channels.db.repositories.FeedbackRepository;
import com.mindalliance.channels.db.services.AbstractDataService;
import com.mysema.query.BooleanBuilder;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 3:54 PM
 */
@Component
public class FeedbackServiceImpl extends AbstractDataService<Feedback> implements FeedbackService {

    @Autowired
    private FeedbackRepository repository;

    public void save( Feedback feedback ) {
        repository.save( feedback );
    }

    @Override
    public Feedback load( String uid ) {
        return repository.findOne( uid );
    }

    @Override
    public void sendFeedback(
            String username,
            CommunityService communityService,
            Feedback.Type type,
            String topic,
            String text,
            boolean urgent ) {
        Feedback feedback = new Feedback( username, type, communityService.getPlanCommunity() );
        feedback.setTopic( topic );
        feedback.setText( text );
        feedback.setUrgent( urgent );
        getDb().insert( feedback );
        communityService.clearCache();
    }

    @Override
    public void sendFeedback(
            String username,
            CommunityService communityService,
            Feedback.Type type,
            String topic,
            String text,
            boolean urgent,
            ModelObject about ) {
        Feedback feedback = new Feedback( username, type, communityService.getPlanCommunity() );
        feedback.setTopic( topic );
        feedback.setText( text );
        feedback.setUrgent( urgent );
        feedback.setMoRef( about );
        getDb().insert( feedback );
        communityService.clearCache();
    }

    @Override
    public void sendFeedback(
            String username,
            CommunityService communityService,
            Feedback.Type type,
            String topic,
            String text,
            boolean urgent,
            ModelObject about,
            String context ) {
        Feedback feedback = new Feedback( username, type, communityService.getPlanCommunity() );
        feedback.setTopic( topic );
        feedback.setText( text );
        feedback.setUrgent( urgent );
        feedback.setMoRef( about );
        feedback.setContext( context );
        getDb().insert( feedback );
        communityService.clearCache();
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<Feedback> listNotYetNotifiedNormalFeedbacks( CommunityService communityService ) {
        QFeedback qFeedback = QFeedback.feedback;
        return toList(
                repository.findAll(
                        qFeedback.whenNotified.isNull()
                                .and( qFeedback.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qFeedback.urgent.isFalse() ),
                        qFeedback.created.desc() )
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Feedback> listNotYetNotifiedUrgentFeedbacks( CommunityService communityService ) {
        QFeedback qFeedback = QFeedback.feedback;
        return toList(
                repository.findAll(
                        qFeedback.whenNotified.isNull()
                                .and( qFeedback.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                .and( qFeedback.urgent.isTrue() ),
                        qFeedback.created.desc() )
        );
    }


    @Override
    public void addReplyTo( Feedback feedback, UserMessage reply, UserMessageService messageService ) {
        reply.setUid( new ObjectId().toString() );
        getDb().updateFirst( new Query( where( "_id" ).is( new ObjectId( feedback.getUid() ) ) ),
                new Update().addToSet( "replies", reply ).set( "lastReplied", new Date() ),
                Feedback.class );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Feedback> selectInitialFeedbacks(
            CommunityService communityService,
            Boolean urgentOnly,
            Boolean notResolvedOnly,
            Boolean notRepliedToOnly,
            String topic,
            String containing,
            String username ) {
        QFeedback qFeedback = QFeedback.feedback;
        BooleanBuilder bb = new BooleanBuilder();
        if ( urgentOnly ) {
            bb.and( qFeedback.urgent.isTrue() );
        }
        if ( notResolvedOnly ) {
            bb.and( qFeedback.resolved.isFalse() );
        }
        if ( notRepliedToOnly ) {
            bb.and( qFeedback.repliedTo.isFalse() );
        }
        if ( topic != null && !topic.isEmpty() ) {
            bb.and( qFeedback.topic.eq( topic ) );
        }
        if ( containing != null && !containing.isEmpty() ) {
            bb.and( qFeedback.text.contains( containing ) );
        }
        if ( username != null && !username.isEmpty() ) {
            bb.and( qFeedback.username.eq( username ) );
        }
        if ( communityService.isForDomain() ) { // get feedback from collaboration template and plans based on it
            bb.and(  qFeedback.planUri.eq( communityService.getPlanCommunity().getModelUri() ) );
        } else { // get feedback from collaboration plan
            bb.and( qFeedback.communityUri.eq( communityService.getPlanCommunity().getUri() ) );
        }
        return toList(
                repository.findAll(
                        bb.getValue(),
                        qFeedback.created.desc() )
        );
    }

    @Override
    public void toggleResolved( CommunityService communityService, Feedback feedback ) {
        getDb().updateFirst( new Query( where( "_id" ).is( new ObjectId( feedback.getUid() ) ) ),
                new Update().set( "resolved", !feedback.isResolved() ),
                Feedback.class );
        communityService.clearCache();
    }

    @Override
    public int countUnresolvedFeedback( CommunityService communityService, ChannelsUser user ) {
        QFeedback qFeedback = QFeedback.feedback;
        return toInteger( repository.count(
                qFeedback.communityUri.eq( communityService.getPlanCommunity().getUri() )
                        .and( qFeedback.username.eq( user.getUsername() ) )
                        .and( qFeedback.resolved.isFalse() ) ) );
    }

    @Override
    public void markFeedbackRepliesRead( Feedback feedback ) {
        getDb().updateFirst(
                new Query( where( "_id" ).is( new ObjectId( feedback.getUid() ) ) ),
                new Update().set( "repliesRead", true ),
                Feedback.class );
    }

    @Override
    public Iterator<UserMessage> listMessagesToSend( String communityUri ) {
        Query query = new Query( where( "communityUri" ).is( communityUri )
                .and( "replies" ).elemMatch( where( "sendNotification" ).is( true ).and( "whenNotificationSent" ).exists( false ) ) );
        query.fields().include( "replies" );
        List<Feedback> list = getDb().find( query, Feedback.class );
        List<UserMessage> toSend = new ArrayList<UserMessage>();
        for ( Feedback f : list ) {
            for ( UserMessage message : f.getReplies() )
                if ( message.isAwaitingNotification() )
                    toSend.add( message );
        }
        return toSend.iterator();
    }

    @Override
    public void markSent( UserMessage message ) {
        message.setWhenNotificationSent( new Date() );
        getDb().updateFirst( new Query( where( "replies._id" ).is( new ObjectId( message.getUid() ) ) ),
                new Update().set( "replies.$.whenNotificationSent", new Date() ),
                Feedback.class );
    }
}
