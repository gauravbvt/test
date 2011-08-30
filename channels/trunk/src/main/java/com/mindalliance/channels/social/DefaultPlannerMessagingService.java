package com.mindalliance.channels.social;

import com.mindalliance.channels.core.PersistentObjectDao;
import com.mindalliance.channels.core.PersistentObjectDaoFactory;
import com.mindalliance.channels.core.dao.PlanDefinition;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserService;
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

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( DefaultPlannerMessagingService.class );
    /**
     * Mail sender.
     */
    private MailSender mailSender;
    private UserService userService;
    private PersistentObjectDaoFactory databaseFactory;
    private Map<String,Date> whenLastChanged;
    private static final int SUMMARY_MAX = 25;

    public DefaultPlannerMessagingService() {
        whenLastChanged = new HashMap<String,Date>();
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

    public void setDatabaseFactory( PersistentObjectDaoFactory databaseFactory ) {
        this.databaseFactory = databaseFactory;
    }

    private void addSentMessage( PlannerMessage message, Plan plan ) {
        getOdb( plan ).store( message );
        changed( plan );
    }

    private void changed( Plan plan ) {
        whenLastChanged.put( plan.getUri(), new Date() );
    }

    @Override
    public boolean sendMessage( PlannerMessage message, boolean emailIt, Plan plan ) {
        boolean success = true;
        addSentMessage( message, plan );
        if ( emailIt ) {
            success = email( message, plan );
        }
        return success;
    }

    @Override
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
            Date now = new Date();
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
                text += "\n\n -- Message first sent in Channels " + getLongTimeElapsedString( message.getDate(), now )
                        + " --";
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

    @Override
    public void deleteMessage( PlannerMessage message, Plan plan ) {
        getOdb( plan ).delete( PlannerMessage.class, message.getId() );
        changed( plan );
    }

    @Override
    public Iterator<PlannerMessage> getReceivedMessages( Plan plan ) {
        return getOdb( plan ).findAllExceptUser(
                PlannerMessage.class, User.current().getUsername(), User.current().isPlanner(), PlannerMessagingService.USERS, PlannerMessagingService.PLANNERS );
    }

    private PersistentObjectDao getOdb( Plan plan ) {
        String planUri = plan.getUri();
        return databaseFactory.getDao( PlanDefinition.sanitize( planUri ) );
    }

    @Override
    public Iterator<PlannerMessage> getSentMessages( Plan plan ) {
        return getOdb( plan ).findAllFrom( PlannerMessage.class, getUsername() );
    }

    @Override
    public Date getWhenLastChanged( Plan plan ) {
        return whenLastChanged.get( plan.getUri() );
    }

    @Override
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
