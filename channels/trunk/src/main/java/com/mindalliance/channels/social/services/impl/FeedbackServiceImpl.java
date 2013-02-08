package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.UserMessage;
import com.mindalliance.channels.social.services.FeedbackService;
import com.mindalliance.channels.social.services.UserMessageService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/12
 * Time: 10:42 AM
 */
@Repository
public class FeedbackServiceImpl extends GenericSqlServiceImpl<Feedback, Long> implements FeedbackService {

    @Override
    @Transactional
    public void sendFeedback(
            String username,
            PlanCommunity planCommunity,
            Feedback.Type type,
            String topic,
            String text,
            boolean urgent ) {
        Feedback feedback = new Feedback( username, type, planCommunity );
        feedback.setTopic( topic );
        feedback.setText( text );
        feedback.setUrgent( urgent );
        save( feedback );
    }

    @Override
    @Transactional
    public void sendFeedback(
            String username,
            PlanCommunity planCommunity,
            Feedback.Type type,
            String topic,
            String text,
            boolean urgent,
            ModelObject about ) {
        Feedback feedback = new Feedback( username, type, planCommunity );
        feedback.setTopic( topic );
        feedback.setText( text );
        feedback.setUrgent( urgent );
        feedback.setMoRef( about );
        save( feedback );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true)
    public List<Feedback> listNotYetNotifiedNormalFeedbacks( PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.isNull( "whenNotified" ) );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "urgent", false ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<Feedback>) criteria.list();
    }

    @Override
    @Transactional( readOnly = true)
    @SuppressWarnings( "unchecked" )
    public List<Feedback> listNotYetNotifiedUrgentFeedbacks( PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.isNull( "whenNotified" ) );
        criteria.add( Restrictions.eq( "urgent", true ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<Feedback>) criteria.list();
    }

    @Override
    @Transactional
    public void addReplyTo( Feedback feedback, UserMessage reply, UserMessageService messageService ) {
        messageService.save( reply );
        feedback.addReply( reply );
        reply.setFeedback( feedback );
        feedback.setLastReplied( reply.getCreated() );
        save( feedback );
    }

    @Override
    @Transactional( readOnly = true)
    @SuppressWarnings( "unchecked" )
    public List<Feedback> selectInitialFeedbacks(
            PlanCommunity planCommunity,
            Boolean urgentOnly,
            Boolean notResolvedOnly,
            Boolean notRepliedToOnly,
            String topic,
            String containing,
            String username ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria(  getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        if ( urgentOnly ) {
            criteria.add( Restrictions.eq( "urgent", true ) );
        }
        if ( notResolvedOnly ) {
            criteria.add( Restrictions.eq( "resolved", false ) );
        }
        if ( notRepliedToOnly ) {
            criteria.add( Restrictions.eq( "repliedTo", false ) );
        }
        if ( topic != null && !topic.isEmpty() ) {
            criteria.add( Restrictions.eq( "topic", topic ) );
        }
        if ( containing != null && !containing.isEmpty() ) {
            criteria.add( Restrictions.ilike( "text", "%" + containing + "%" ) );
        }
        if ( username != null && !username.isEmpty() ) {
            criteria.add( Restrictions.eq( "username", username ) );
        }
        criteria.addOrder( Order.desc( "created" ) );
        return (List<Feedback>) criteria.list();
    }

    @Override
    @Transactional
    public void toggleResolved( Feedback feedback ) {
        feedback.setResolved( !feedback.isResolved() );
        save( feedback );
    }

    @Override
    public int countUnresolvedFeedback( CommunityService communityService, ChannelsUser user ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.eq( "username", user.getUsername()) );
        criteria.add( Restrictions.eq( "resolved", false ) );
        return criteria.list().size();
    }

}
