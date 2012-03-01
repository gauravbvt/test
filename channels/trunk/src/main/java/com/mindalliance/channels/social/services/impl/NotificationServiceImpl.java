package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.UserMessage;
import com.mindalliance.channels.social.services.FeedbackService;
import com.mindalliance.channels.social.services.NotificationService;
import com.mindalliance.channels.social.services.UserMessageService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/12
 * Time: 11:15 AM
 */

@Component
public class NotificationServiceImpl implements NotificationService {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( NotificationServiceImpl.class );

    private static final int MAX_SUBJECT_LENGTH = 60;

    /**
     * Simple date format.
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat( "M/d/yyyy HH:mm"  );


    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ChannelsUserDao userDao;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private PlanManager planManager;

    @Autowired
    private UserMessageService userMessageService;

    private static final int SUMMARY_MAX = 25;


    //// USER MESSAGES ////

    @Override
    @Scheduled( fixedDelay = 60000 )     // each minute
    @Async
    @Transactional
    public void emailUserMessages() {
        Iterator<UserMessage> messagesToEmail = userMessageService.listMessagesToEmail();
        while ( messagesToEmail.hasNext() ) {
            UserMessage messageToEmail = messagesToEmail.next();
            boolean success = email( messageToEmail );
            if ( success ) userMessageService.emailed( messageToEmail );
        }
    }

    private boolean email( UserMessage message ) {
        ChannelsUser currentUser = userDao.getUserNamed( message.getFromUsername() );
        List<ChannelsUser> recipients = new ArrayList<ChannelsUser>();
        String toUsername = message.getToUsername();
        String urn = message.getPlanUri();
        String summary = StringUtils.abbreviate( message.getText(), SUMMARY_MAX );
        if ( toUsername == null || toUsername.equals( UserMessageService.PLANNERS ) )
            recipients = userDao.getPlanners( urn );
        else if ( toUsername.equals( UserMessageService.USERS ) )
            recipients = userDao.getUsers( urn );
        else
            recipients.add( userDao.getUserNamed( toUsername ) );

        try {
            for ( ChannelsUser recipient : recipients ) {
                String recipientEmailAddress = recipient.getEmail();
                if ( !recipientEmailAddress.isEmpty()
                        && ChannelsUtils.isValidEmailAddress( recipientEmailAddress ) ) {
                    SimpleMailMessage email = new SimpleMailMessage();
                    email.setTo( recipient.getEmail() );
                    email.setSubject( "["
                            + urn
                            + "] "
                            + summary );
                    String fromAddress = currentUser.getEmail();
                    if ( fromAddress.isEmpty() ) {
                        fromAddress = getDefaultFormAddress( message );
                    }
                    if ( ChannelsUtils.isValidEmailAddress( fromAddress ) ) {
                    email.setFrom( fromAddress );
                    email.setReplyTo( fromAddress );
                    }
                    email.setText( makeContent( message ) );
                    mailSender.send( email );
                    LOG.info( currentUser.getUsername()
                            + " emailed message to "
                            + recipient.getUsername() );
                }
            }
            message.setWhenEmailed( new Date() );
            userMessageService.save( message );
            return true;
        } catch ( Exception e ) {
            LOG.warn( currentUser.getUsername()
                    + " failed to email message to "
                    + toUsername, e );
            return false;
        }
    }

    private String makeContent( UserMessage message ) {
        Date now = new Date();
        StringBuilder sb = new StringBuilder( );
        String aboutString = message.getMoLabel();
        if ( !aboutString.isEmpty() )
            sb.append( "About " ).append( aboutString ).append( "\n\n" );

        sb.append( message.getText() );
        Feedback feedback = message.getFeedback();
        if ( feedback != null ) {
            sb.append( "\n\n -- In response to the " )
                    .append( feedback.getTypeLabel() )
                    .append( " you sent on " );
            sb.append( dateFormat.format( feedback.getCreated() ) );
            sb.append( ":\n\n" );
            sb.append( feedback.getText() );
            sb.append( "\n\n ---------------- " );
        }
        sb.append( "\n\n -- Message first sent in Channels " )
                .append( getLongTimeElapsedString( message.getCreated(), now ) )
                .append( " --" );
         return sb.toString();
    }

    private String getDefaultFormAddress( UserMessage message ) {
        Plan plan = getPlan( message.getPlanUri(), message.getPlanVersion() );
        return plan == null
                ? ""
                : plan.getPlannerSupportCommunity( planManager.getDefaultSupportCommunity() );
    }

    private Plan getPlan( String planUri, int planVersion ) {
        return planManager.getPlan( planUri, planVersion );
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

    //// FEEDBACK ////

    @Override
    @Scheduled( fixedDelay = 60000 )     // each minute
    @Async
    @Transactional
    public void sendUrgentNotifications() {
        List<Feedback> urgentFeedbacks = feedbackService.listNotYetNotifiedUrgentFeedbacks();
        for ( Feedback urgentFeedback : urgentFeedbacks ) {
            boolean success = emailUrgentFeedbackToPlanners( urgentFeedback );
            if ( success ) {
                urgentFeedback.setWhenNotified( new Date() );
                feedbackService.save( urgentFeedback );
            }
        }
    }

    @Override
    @Scheduled( fixedDelay = 86400000 )   // each day
    @Async
    @Transactional
    public void sendBatchedFeedbackNotifications() {
        for ( Plan plan : planManager.getPlans() ) {
            List<Feedback> normalFeedbacks = feedbackService.listNotYetNotifiedNormalFeedbacks( plan );
            if ( !normalFeedbacks.isEmpty() ) {
                boolean success = false;
                String subject = makeBatchedFeedbackSubject( plan.getVersionUri(), normalFeedbacks );
                String content = makeBatchedFeedbackContent( normalFeedbacks );
                for ( ChannelsUser planner : userDao.getPlanners( plan.getUri() ) ) {
                    success = sendEmail(
                            planner.getEmail(),
                            plan.getPlannerSupportCommunity( planManager.getDefaultSupportCommunity() ),
                            subject,
                            content ) || success;
                }
                if ( success ) {
                    for ( Feedback normalFeedback : normalFeedbacks ) {
                        normalFeedback.setWhenNotified( new Date() );
                        feedbackService.save( normalFeedback );
                    }
                }
            }
        }
    }

    private String makeBatchedFeedbackSubject( String planUri, List<Feedback> feedbacks ) {
        StringBuilder sb = new StringBuilder();
        int n = feedbacks.size();
        sb.append( "Latest non-urgent feedback (" );
        sb.append( n );
        sb.append( ") about plan " );
        sb.append( planUri );
        return sb.toString();
    }

    private String makeBatchedFeedbackContent( List<Feedback> feedbacks ) {
        StringBuilder sb = new StringBuilder();
        for ( Feedback feedback : feedbacks ) {
            sb.append( makeContent( feedback ) );
            sb.append( "\n============================================\n\n" );
        }
        return sb.toString();
    }

    private boolean emailUrgentFeedbackToPlanners( Feedback feedback ) {
        boolean success = false;
        for ( ChannelsUser planner : userDao.getPlanners( feedback.getPlanUri() ) ) {
            success = emailUrgentFeedbackToPlanner( planner, feedback ) || success;
        }
        return success;
    }

    private boolean emailUrgentFeedbackToPlanner( ChannelsUser planner, Feedback feedback ) {
        boolean success = false;
        String toAddress = planner.getEmail();
        if ( ChannelsUtils.isValidEmailAddress( toAddress ) ) {
            String fromAddress = feedback.getFromEmail();
            String subject = makeEmailSubject( feedback );
            String content = makeContent( feedback );
            success = sendEmail( toAddress, fromAddress, subject, content );
        }
        return success;
    }

    private boolean sendEmail( String toAddress, String fromAddress, String subject, String content ) {
        boolean success = false;
        if ( ChannelsUtils.isValidEmailAddress( toAddress ) ) {
            try {
                SimpleMailMessage email = new SimpleMailMessage();
                email.setTo( toAddress );
                if ( ChannelsUtils.isValidEmailAddress( fromAddress ) )
                    email.setFrom( fromAddress );
                email.setSubject( subject );
                email.setText( content );
                LOG.info( " emailing \"" + subject + "\" to "
                        + toAddress );
                mailSender.send( email );

                success = true;
            } catch ( Exception e ) {
                LOG.warn( "Failed to email " + subject + " to " + toAddress, e );

            }
        }
        return success;
    }

    private String makeContent( Feedback feedback ) {
        return "Plan: " + feedback.getPlanUri()
                + ":"
                + feedback.getPlanVersion()
                + "\nUser: " + feedback.getUsername()
                + "\n"
                + dateFormat.format( feedback.getCreated() )
                + aboutString( feedback )
                + "\n----------------------------------------------------------------------------\n\n"
                + feedback.getText()
                + "\n\n----------------------------------------------------------------------------\n";

    }

    private String aboutString( Feedback feedback ) {
        String about = feedback.getMoRef();
        if ( about == null ) {
            return "";
        } else {
            ModelObjectRef moRef = ModelObjectRef.fromString( about );
            StringBuilder sb = new StringBuilder();
            sb.append( "\nAbout: " );
            sb.append( moRef.getTypeName() );
            sb.append( " \"" );
            sb.append( moRef.getName() );
            sb.append( "\" [" );
            sb.append( moRef.getId() );
            sb.append( "]" );
            String segmentName = moRef.getSegmentName();
            if ( !segmentName.isEmpty() ) {
                sb.append( " in segment \"" );
                sb.append( segmentName );
            }
            String topic = feedback.getTopic();
            if ( topic != null && !topic.isEmpty() ) {
                sb.append( " (" );
                sb.append( topic );
                sb.append( ')' );
            }
            return sb.toString();
        }
    }


    private String makeEmailSubject( Feedback feedback ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Feedback" );
        if ( feedback.isUrgent() ) sb.append( " [ASAP]" );
        sb.append( " - " );
        sb.append( WordUtils.capitalize( feedback.getType().name() ) );
        sb.append( " - " );
        sb.append( contentAbbreviated( feedback ) );
        return sb.toString();
    }

    private String contentAbbreviated( Feedback feedback ) {
        String summary = feedback.getText().replaceAll( "\\s", " " );
        return StringUtils.abbreviate( summary, MAX_SUBJECT_LENGTH );
    }

    public void setFeedbackService( FeedbackService feedbackService ) {
        this.feedbackService = feedbackService;
    }

    public void setUserDao( ChannelsUserDao userDao ) {
        this.userDao = userDao;
    }

    public void setMailSender( MailSender mailSender ) {
        this.mailSender = mailSender;
    }
}
