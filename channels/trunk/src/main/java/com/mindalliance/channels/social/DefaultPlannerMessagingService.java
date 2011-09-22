/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.social;

import com.mindalliance.channels.core.PersistentObjectDao;
import com.mindalliance.channels.core.PersistentObjectDaoFactory;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserDao;
import com.mindalliance.channels.core.model.Plan;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the planner messaging service.
 */
public class DefaultPlannerMessagingService implements PlannerMessagingService {
    private static final int SUMMARY_MAX = 25;

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( DefaultPlannerMessagingService.class );
    /**
     * Mail sender.
     */
    private MailSender mailSender;
    private UserDao userDao;
    private PersistentObjectDaoFactory databaseFactory;
    private Map<String,Date> whenLastChanged;

    //-------------------------------
    public DefaultPlannerMessagingService() {
        whenLastChanged = new HashMap<String, Date>();
    }

    //-------------------------------
    @Override
    public void deleteMessage( PlannerMessage message, String urn ) {
        getOdb( urn ).delete( PlannerMessage.class, message.getId() );
        changed( urn );
    }

    @Override
    public boolean email( PlannerMessage message, String urn ) {
        List<User> recipients = new ArrayList<User>();
        String username = message.getToUsername();
        String text = "";
        User currentUser = User.current();
        String summary = StringUtils.abbreviate( message.getText(), SUMMARY_MAX );
        if ( username == null || username.equals( PLANNERS ) )
            recipients = userDao.getPlanners( urn );
        else if ( username.equals( USERS ) )
            recipients = userDao.getUsers( urn );
        else
            recipients.add( userDao.getUserNamed( username ) );

        try {
            Date now = new Date();
            for ( User recipient : recipients ) {
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
                text += "\n\n -- Message first sent in Channels " + getLongTimeElapsedString( message.getDate(), now )
                        + " --";
                email.setText( text );
                mailSender.send( email );
                LOG.info( currentUser.getUsername()
                        + " emailed message to "
                        + recipient.getUsername() );
            }
            getOdb( urn ).update( message.getClass(), message.getId(), "emailed", true );
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

    private PersistentObjectDao getOdb( String urn ) {
        return databaseFactory.getDao( urn );
    }

    @Override
    public Iterator<PlannerMessage> getReceivedMessages( String urn ) {
        return getOdb( urn ).findAllExceptUser(
                PlannerMessage.class, User.current().getUsername(), User.current().isPlanner(), PlannerMessagingService.USERS, PlannerMessagingService.PLANNERS );
    }

    @Override
    public Iterator<PlannerMessage> getSentMessages( String urn ) {
        return getOdb( urn ).findAllFrom( PlannerMessage.class, getUsername() );
    }

    private String getUsername() {
        return User.current().getUsername();
    }

    protected User getUser( String username ) {
        return userDao.getUserNamed( username );
    }

    @Override
    public Date getWhenLastChanged( String urn ) {
        return whenLastChanged.get( urn );
    }

    @Override
    public Date getWhenLastReceived( String urn ) {
        Iterator<PlannerMessage> received = getReceivedMessages( urn );
        if ( received.hasNext() ) {
            return received.next().getDate();
        } else {
            return null;
        }
    }

    @Override
    public boolean sendMessage( PlannerMessage message, boolean emailIt, String urn ) {
        boolean success = true;
        addSentMessage( message, urn );
        if ( emailIt ) {
            success = email( message, urn );
        }
        return success;
    }

    private void addSentMessage( PlannerMessage message, String urn ) {
        getOdb( urn ).store( message );
        changed( urn );
    }

    private void changed( String urn ) {
        whenLastChanged.put( urn, new Date() );
    }

    //-------------------------------
    public MailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender( MailSender mailSender ) {
        this.mailSender = mailSender;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao( UserDao userDao ) {
        this.userDao = userDao;
    }

    public void setDatabaseFactory( PersistentObjectDaoFactory databaseFactory ) {
        this.databaseFactory = databaseFactory;
    }
}
