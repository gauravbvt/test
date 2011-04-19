package com.mindalliance.channels.social;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.odb.ODBAccessor;
import com.mindalliance.channels.odb.ODBTransactionFactory;
import org.apache.commons.lang.StringUtils;
import org.neodatis.odb.core.query.criteria.Where;
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
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 2, 2010
 * Time: 3:36:33 PM
 */
public class DefaultPlannerMessagingService implements PlannerMessagingService {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( DefaultPlannerMessagingService.class );
    /**
     * Mail sender.
     */
    private MailSender mailSender;
    private UserService userService;
    private ODBTransactionFactory databaseFactory;
    private Map<String,Date> whenLastChanged;
    private static final int SUMMARY_MAX = 25;

    public DefaultPlannerMessagingService() {
        whenLastChanged = new HashMap<String,Date>();
    }

    public void setDatabaseFactory( ODBTransactionFactory databaseFactory ) {
        this.databaseFactory = databaseFactory;
    }

    private void addSentMessage( PlannerMessage message, Plan plan ) {
        getOdb( plan ).store( message );
        changed( plan );
    }

    private void changed( Plan plan ) {
        whenLastChanged.put( plan.getUri(), new Date() );
    }

    public void sendMessage( PlannerMessage message, boolean emailIt, Plan plan ) {
        addSentMessage( message, plan );
        if ( emailIt ) {
            email( message, plan );
        }
    }

    public boolean email( PlannerMessage message, Plan plan ) {
        List<User> recipients = new ArrayList<User>();
        String username = message.getToUsername();
        String text = "";
        User currentUser = User.current();
        String summary = StringUtils.abbreviate( message.getText(), SUMMARY_MAX );
        if ( username == null || username.equals( PLANNERS ) ) {
            recipients = userService.getPlanners( plan.getUri() );
        } else if ( username.equals( USERS ) ) {
            recipients = userService.getUsers( plan.getUri() );
        } else {
            recipients.add( userService.getUserNamed( username ) );
        }
        try {
            for ( User recipient : recipients ) {
                SimpleMailMessage email = new SimpleMailMessage();
                email.setTo( recipient.getEmail() );
                email.setSubject( "["
                        + plan.getName()
                        + "] "
                        + summary );
                email.setFrom( currentUser.getEmail() );
                email.setReplyTo( currentUser.getEmail() );
                String aboutString = message.getAboutString();
                if ( !aboutString.isEmpty() ) {
                    text = "About " + aboutString + "\n\n";
                }
                text += message.getText();
                text += "\n\n -- Message first sent in Channels " + message.getLongTimeElapsedString() + " --";
                email.setText( text );
                mailSender.send( email );
                LOG.info( currentUser.getUsername()
                        + " emailed message to "
                        + recipient.getUsername() );
            }
            getOdb( plan ).update( message.getClass(), message.getId(), "emailed", true );
            return true;
        } catch ( Exception e ) {
            LOG.warn( currentUser.getUsername()
                    + " failed to email message to "
                    +  username, e );
            return false;
        }
    }

    protected User getUser( String username ) {
        return userService.getUserNamed( username );
    }

    public void deleteMessage( PlannerMessage message, Plan plan ) {
        getOdb( plan ).delete( PlannerMessage.class, message.getId() );
        changed( plan );
    }

    public Iterator<PlannerMessage> getReceivedMessages( Plan plan ) {
        return getOdb( plan ).iterate(
                PlannerMessage.class,
                Where.and()
                        .add( Where.equal( "planId", plan.getUri() ) )
                        .add(
                        Where.or()
                                .add( Where.equal( "toUsername", getUsername() ) )
                                .add( Where.and()
                                .add( Where.isNull( "toUsername" ) )
                                .add( Where.not( Where.equal( "fromUsername", getUsername() ) ) ) )
                ),
                ODBAccessor.Ordering.Descendant,
                "date"
        );
    }

    private ODBAccessor getOdb( Plan plan ) {
        return databaseFactory.getODBAccessor( plan.getUri() );
    }


    public Iterator<PlannerMessage> getSentMessages( Plan plan ) {
        return getOdb( plan ).iterate(
                PlannerMessage.class,
                Where.and()
                        .add( Where.equal( "planId", plan.getUri() ) )
                        .add( Where.equal( "fromUsername", getUsername() ) ),
                ODBAccessor.Ordering.Descendant,
                "date"
        );
    }

    public Date getWhenLastChanged( Plan plan ) {
        return whenLastChanged.get( plan.getUri() );
    }

    public Date getWhenLastReceived( Plan plan ) {
        Iterator<PlannerMessage> received = getReceivedMessages( plan );
        if ( received.hasNext() ) {
            return received.next().getDate();
        } else {
            return null;
        }
    }

    private String getUsername() {
        return User.current().getUsername();
    }

    public MailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender( MailSender mailSender ) {
        this.mailSender = mailSender;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService( UserService userService ) {
        this.userService = userService;
    }
}
