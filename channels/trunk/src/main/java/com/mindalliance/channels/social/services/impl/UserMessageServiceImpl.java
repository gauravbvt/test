package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.UserMessage;
import com.mindalliance.channels.social.services.UserMessageService;
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
        message.setEmailIt( emailIt );
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
    @Transactional
    public Iterator<UserMessage> getReceivedMessages( String username, String planUri, int planVersion ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", planUri ) );
        criteria.add( Restrictions.eq( "planVersion", planVersion ) );
        criteria.add( Restrictions.eq( "toUsername", username ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (Iterator<UserMessage>) criteria.list().iterator();
    }

    @Override
    @Transactional
    public Iterator<UserMessage> getSentMessages( String username, String planUri, int planVersion ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", planUri ) );
        criteria.add( Restrictions.eq( "planVersion", planVersion ) );
        criteria.add( Restrictions.eq( "username", username ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (Iterator<UserMessage>) criteria.list().iterator();
    }

    @Override
    public Iterator<UserMessage> listMessagesToEmail() {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "emailIt", true ) );
        criteria.add( Restrictions.isNull( "whenEmailed" ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (Iterator<UserMessage>) criteria.list().iterator();
    }

    @Override
    @Transactional
    public void email( UserMessage message ) {
        message.setEmailIt( true );
        save( message );
    }

    @Override
    @Transactional
    public Date getWhenLastReceived( String username, String planUri , int planVersion) {
        Iterator<UserMessage> received = getReceivedMessages( username, planUri, planVersion );
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
    public void emailed( UserMessage message ) {
        message.setWhenEmailed( new Date() );
        save( message );
    }

    private void changed( String urn ) {
        whenLastChanged.put( urn, new Date() );
    }

}
