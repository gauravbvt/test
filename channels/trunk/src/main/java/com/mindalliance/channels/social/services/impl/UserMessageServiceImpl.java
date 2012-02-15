package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.UserMessage;
import com.mindalliance.channels.social.services.UserMessageService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    private static final int SUMMARY_MAX = 25;

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
    public boolean sendMessage( UserMessage message, boolean emailIt, ChannelsUser sender ) {
        boolean success = true;
        save( message );
        if ( emailIt ) {
            success = email( message, sender );
        }
        changed( message.getPlanUri() );
        return success;
    }

    @Override
    @Transactional
    public void deleteMessage( UserMessage message ) {
        delete( message );
        changed( message.getPlanUri() );
    }

    @Override
    @Transactional
    public Iterator<UserMessage> getReceivedMessages( String username, String planUri ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", planUri ) );
        criteria.add( Restrictions.eq( "toUsername", username ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (Iterator<UserMessage>) criteria.list().iterator();
    }

    @Override
    @Transactional
    public Iterator<UserMessage> getSentMessages( String username, String planUri ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", planUri ) );
        criteria.add( Restrictions.eq( "username", username ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (Iterator<UserMessage>) criteria.list().iterator();
    }

    @Override
    @Transactional
    public Date getWhenLastReceived( String username, String planUri ) {
        Iterator<UserMessage> received = getReceivedMessages( username, planUri );
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
    public boolean email( UserMessage message, ChannelsUser currentUser ) {
        List<ChannelsUser> recipients = new ArrayList<ChannelsUser>();
        String username = message.getToUsername();
        String text = "";
        String urn = currentUser.getPlanUri();
        String summary = StringUtils.abbreviate( message.getText(), SUMMARY_MAX );
        if ( username == null || username.equals( PLANNERS ) )
            recipients = userDao.getPlanners( urn );
        else if ( username.equals( USERS ) )
            recipients = userDao.getUsers( urn );
        else
            recipients.add( userDao.getUserNamed( username ) );

        try {
            Date now = new Date();
            for ( ChannelsUser recipient : recipients ) {
                String recipientEmailAddress = recipient.getEmail();
                if ( !recipientEmailAddress.isEmpty()
                        && EmailValidator.getInstance().isValid( recipientEmailAddress ) ) {
                    SimpleMailMessage email = new SimpleMailMessage();
                    email.setTo( recipient.getEmail() );
                    email.setSubject( "["
                            + urn
                            + "] "
                            + summary );
                    email.setFrom( currentUser.getEmail() );
                    email.setReplyTo( currentUser.getEmail() );
                    String aboutString = message.getAboutString();
                    if ( !aboutString.isEmpty() )
                        text = "About " + aboutString + "\n\n";

                    text += message.getText();
                    text += "\n\n -- Message first sent in Channels " + getLongTimeElapsedString( message.getCreated(), now )
                            + " --";
                    email.setText( text );
                    mailSender.send( email );
                    LOG.info( currentUser.getUsername()
                            + " emailed message to "
                            + recipient.getUsername() );
                }
            }
            message.setEmailed( true );
            save( message );
            return true;
        } catch ( Exception e ) {
            LOG.warn( currentUser.getUsername()
                    + " failed to email message to "
                    + username, e );
            return false;
        }
    }

    private static String getLongTimeElapsedString( Date start, Date end ) {
        long diffInSeconds = ( end.getTime() - start.getTime() ) / 1000;
        /* sec */
        long seconds = diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* min */
        long minutes = ( diffInSeconds = diffInSeconds / 60 ) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        long hours = ( diffInSeconds = diffInSeconds / 60 ) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        long days = diffInSeconds / 24;

        StringBuilder sb = new StringBuilder();
        if ( days > 0 ) {
            sb.append( days );
            sb.append( " day" );
            sb.append( days > 1 ? "s" : "" );
        }
        if ( hours > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( hours );
            sb.append( " hour" );
            sb.append( hours > 1 ? "s" : "" );
        }
        if ( minutes > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( minutes );
            sb.append( " minute" );
            sb.append( minutes > 1 ? "s" : "" );
        }
        if ( sb.length() == 0 || seconds > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( seconds );
            sb.append( " second" );
            sb.append( seconds > 1 ? "s" : "" );
        }
        sb.append( " ago" );
        return sb.toString();
    }

    private void changed( String urn ) {
        whenLastChanged.put( urn, new Date() );
    }

}
