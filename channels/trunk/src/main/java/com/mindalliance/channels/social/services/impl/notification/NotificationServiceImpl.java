package com.mindalliance.channels.social.services.impl.notification;

import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.UserMessage;
import com.mindalliance.channels.social.services.FeedbackService;
import com.mindalliance.channels.social.services.UserMessageService;
import com.mindalliance.channels.social.services.notification.ChannelsMessagingService;
import com.mindalliance.channels.social.services.notification.EmailMessagingService;
import com.mindalliance.channels.social.services.notification.Messageable;
import com.mindalliance.channels.social.services.notification.MessagingService;
import com.mindalliance.channels.social.services.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * A scheduled notification service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/12
 * Time: 11:15 AM
 */

@Component
public class NotificationServiceImpl implements NotificationService, InitializingBean {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( NotificationServiceImpl.class );

    private final static boolean EXCLUDE_INTERNAL_NOTIFICATIONS = true;


    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ChannelsUserDao userDao;

    @Autowired
    private PlanManager planManager;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private EmailMessagingService emailMessagingService;
    @Autowired
    private ChannelsMessagingService channelsMessagingService;

    private List<MessagingService> messagingServices;

    @Override
    public void afterPropertiesSet() {
        messagingServices = new ArrayList<MessagingService>();
        messagingServices.add( emailMessagingService );
        messagingServices.add( channelsMessagingService );
    }

    //// USER MESSAGES ////

    @Override
    @Scheduled( fixedDelay = 60000 )     // every minute
    @Async
    @Transactional
    public void notifyOfUserMessages() {
        LOG.info( "Sending out notifications of user messages" );
        Iterator<UserMessage> messagesToNotifyOf = userMessageService.listMessagesToNotify();
        while ( messagesToNotifyOf.hasNext() ) {
            UserMessage messageToNotifyOf = messagesToNotifyOf.next();
            boolean success = sendNotifications( messageToNotifyOf, EXCLUDE_INTERNAL_NOTIFICATIONS );
            // success = at least one notification went out. No retries. Todo: retry each notification failure?
            if ( success ) userMessageService.markNotified( messageToNotifyOf );
        }
    }


    //// FEEDBACK ////

    @Override
    @Scheduled( fixedDelay = 60000 )     // each minute
    @Async
    @Transactional
    public void notifyOfUrgentFeedback() {
        LOG.info( "Sending out notifications of urgent feedback" );
        List<Feedback> urgentFeedbacks = feedbackService.listNotYetNotifiedUrgentFeedbacks();
        for ( Feedback urgentFeedback : urgentFeedbacks ) {
            boolean success = sendNotifications( urgentFeedback, !EXCLUDE_INTERNAL_NOTIFICATIONS );
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
    public void reportOnNewFeedback() {
        LOG.info( "Sending out reports of new feedback" );
        for ( Plan plan : planManager.getPlans() ) {
            List<Feedback> normalFeedbacks = feedbackService.listNotYetNotifiedNormalFeedbacks( plan );
            if ( !normalFeedbacks.isEmpty() ) {
                boolean success;
                success = sendReport( plan, getPlanners( plan ), normalFeedbacks );
                if ( success ) {
                    for ( Feedback normalFeedback : normalFeedbacks ) {
                        normalFeedback.setWhenNotified( new Date() );
                        feedbackService.save( normalFeedback );
                    }
                }
            }
        }
    }

    //

    private boolean sendNotifications(
            Messageable messageable,
            boolean excludeInternalNotifications ) {
        boolean notified = false;
        for ( MessagingService messagingService : messagingServices ) {
            if ( !( excludeInternalNotifications && messagingService.isInternal() ) ) {
                boolean success = messagingService.sendMessage( messageable );
                notified = notified || success;
            }
        }
        return notified;
    }


    private boolean sendReport(
            Plan plan,
            List<ChannelsUserInfo> recipients,
            List<? extends Messageable> messageables ) {
        boolean reported = false;
        for ( MessagingService messagingService : messagingServices ) {
            boolean success = messagingService.sendReport( plan, recipients, messageables );
            reported = reported || success;
        }
        return reported;
    }

    private List<ChannelsUserInfo> getPlanners( Plan plan ) {
        List<ChannelsUserInfo> planners = new ArrayList<ChannelsUserInfo>();
        for ( ChannelsUser user : userDao.getPlanners( plan.getUri() ) ) {
            planners.add( user.getUserInfo() );
        }
        return planners;
    }

}
