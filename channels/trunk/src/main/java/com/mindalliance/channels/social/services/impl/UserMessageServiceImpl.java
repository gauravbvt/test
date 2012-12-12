package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.UserMessage;
import com.mindalliance.channels.social.services.UserMessageService;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 3:51 PM
 */

@Repository
public class UserMessageServiceImpl extends GenericSqlServiceImpl<UserMessage, Long> implements UserMessageService {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( UserMessageServiceImpl.class );
    /**
     * Mail sender.
     */
    @Autowired
    private MailSender mailSender;
    @Autowired
    private ChannelsUserDao userDao;
    private Map<String, Date> whenLastChanged = new HashMap<String, Date>();

    @Override
    @Transactional
    public void sendMessage( UserMessage message, boolean emailIt ) {
        message.setSendNotification( emailIt );
        save( message );
        changed( message.getPlanUri() );
    }

    @Override
    @Transactional
    public void deleteMessage( UserMessage message ) {
            delete( message );
            changed( message.getPlanUri() );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public Iterator<UserMessage> getReceivedMessages( final String username, final PlanCommunity planCommunity ) {
        String[] toValues = new String[3];
        toValues[0] = username;
        toValues[1] = ChannelsUserInfo.PLANNERS;
        toValues[2] = ChannelsUserInfo.USERS;
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.in( "toUsername", toValues ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (Iterator<UserMessage>) IteratorUtils.filteredIterator(
                criteria.list().iterator(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserMessage userMessage = (UserMessage) object;
                        return ( !userMessage.isToAllPlanners() || userDao.isPlanner( username, planCommunity.getPlan().getUri() ) )
                                && ( !userMessage.isToAllUsers() || userDao.isParticipant( username, planCommunity.getPlan().getUri() ) );
                    }
                } );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public Iterator<UserMessage> getSentMessages( String username, PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "username", username ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (Iterator<UserMessage>) criteria.list().iterator();
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public Iterator<UserMessage> listMessagesToSend( String communityUri ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityUri ) );
        criteria.add( Restrictions.eq( "sendNotification", true ) );
        criteria.add( Restrictions.isNull( "whenNotificationSent" ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (Iterator<UserMessage>) criteria.list().iterator();
    }

    @Override
    @Transactional
    public void markToNotify( UserMessage message ) {
        message.setSendNotification( true );
        save( message );
    }

    @Override
    public int countNewFeedbackReplies( PlanCommunity planCommunity, ChannelsUser user ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "toUsername", user.getUsername() ) );
        criteria.add( Restrictions.isNotNull( "feedback" ) );
        criteria.add( Restrictions.eq( "read", false ) );
        return criteria.list().size();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional
    public void markFeedbackRepliesRead( Feedback feedback ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "feedback", feedback ) );
        for (UserMessage replyMessage : (List<UserMessage>)criteria.list()) {
            replyMessage.setRead( true );
            save( replyMessage );
        }
    }

    @Override
    @Transactional( readOnly = true )
    public Date getWhenLastReceived( String username, PlanCommunity planCommunity ) {
        Iterator<UserMessage> received = getReceivedMessages( username, planCommunity );
        if ( received.hasNext() ) {
            return received.next().getCreated();
        } else {
            return null;
        }
    }

    @Override
    public Date getWhenLastChanged( String planUri ) {
        return whenLastChanged.get( planUri );
    }

    @Override
    @Transactional
    public void markSent( UserMessage message ) {
        message.setWhenNotificationSent( new Date() );
        save( message );
    }

    private void changed( String urn ) {
        whenLastChanged.put( urn, new Date() );
    }

}
