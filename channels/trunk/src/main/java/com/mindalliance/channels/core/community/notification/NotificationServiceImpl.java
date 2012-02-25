package com.mindalliance.channels.core.community.notification;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.community.feedback.Feedback;
import com.mindalliance.channels.core.community.feedback.FeedbackService;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.util.ChannelsUtils;
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
import java.util.Date;
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

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ChannelsUserDao userDao;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private PlanManager planManager;

    private SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );

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
            String planUri = plan.getUri();
            List<Feedback> normalFeedbacks = feedbackService.listNotYetNotifiedNormalFeedbacks( planUri );
            if ( !normalFeedbacks.isEmpty() ) {
                boolean success = false;
                String subject = makeBatchedFeedbackSubject( planUri, normalFeedbacks );
                String content = makeBatchedFeedbackContent( normalFeedbacks );
                for ( ChannelsUser planner : userDao.getPlanners( planUri ) ) {
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
        if ( ChannelsUtils.isValidEmailAddress ( toAddress ) ) {
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
                + "\nUser: " + feedback.getUsername()
                + "\n"
                + dateFormat.format( feedback.getCreated() )
                + aboutString( feedback )
                + "\n----------------------------------------------------------------------------\n\n"
                + feedback.getContent()
                + "\n\n----------------------------------------------------------------------------\n";

    }

    private String aboutString( Feedback feedback ) {
        String about = feedback.getAbout();
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
        String summary = feedback.getContent().replaceAll( "\\s", " " );
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
