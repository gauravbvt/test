package com.mindalliance.channels.social.services.impl.notification;

import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.UserMessage;
import com.mindalliance.channels.social.model.UserStatement;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.FeedbackService;
import com.mindalliance.channels.social.services.RFIService;
import com.mindalliance.channels.social.services.RFISurveyService;
import com.mindalliance.channels.social.services.SurveysDAO;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    private final static boolean EXCLUDE_INTERNAL_MESSAGES = true;


    @Autowired
    PlanServiceFactory planServiceFactory;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ChannelsUserDao userDao;

    @Autowired
    private PlanManager planManager;

    @Autowired
    private Analyst analyst;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private EmailMessagingService emailMessagingService;
    @Autowired
    private ChannelsMessagingService channelsMessagingService;
    @Autowired
    private RFIService rfiService;
    @Autowired
    private RFISurveyService rfiSurveyService;
    @Autowired
    private SurveysDAO surveysDAO;

    private List<MessagingService> messagingServices;

    private static final long WARNING_DELAY = 1000 * 60 * 60 * 24; // 1 day in msecs

    @Override
    public void afterPropertiesSet() {
        messagingServices = new ArrayList<MessagingService>();
        messagingServices.add( emailMessagingService );
        messagingServices.add( channelsMessagingService );
    }

    //// USER MESSAGES ////

    @Override
    @Scheduled( fixedDelay = 60000 )     // every minute
    @Transactional
    public void notifyOfUserMessages() {
        LOG.debug( "Sending out user messages" );
        for ( Plan plan : planManager.getPlans() ) {
            PlanService planService = planServiceFactory.getService( plan );
            Iterator<UserMessage> messagesToSend = userMessageService.listMessagesToSend( plan.getUri() );
            while ( messagesToSend.hasNext() ) {
                UserMessage messageToSend = messagesToSend.next();
                boolean success = sendMessages(
                        messageToSend,
                        UserStatement.TEXT,
                        EXCLUDE_INTERNAL_MESSAGES,
                        planService );
                // success = at least one message went out. No retries. Todo: retry each messaging failure?
                if ( success ) userMessageService.markSent( messageToSend );
            }
        }
    }


    //// FEEDBACK ////

    @Override
    @Scheduled( fixedDelay = 60000 )     // each minute
    @Transactional
    public void notifyOfUrgentFeedback() {
        LOG.debug( "Sending out urgent feedback" );
        for ( Plan plan : planManager.getPlans() ) {
            PlanService planService = planServiceFactory.getService( plan );
            List<Feedback> urgentFeedbacks = feedbackService.listNotYetNotifiedUrgentFeedbacks( plan );
            for ( Feedback urgentFeedback : urgentFeedbacks ) {
                boolean success = sendMessages(
                        urgentFeedback,
                        UserStatement.TEXT,
                        !EXCLUDE_INTERNAL_MESSAGES,
                        planService );
                if ( success ) {
                    urgentFeedback.setWhenNotified( new Date() );
                    feedbackService.save( urgentFeedback );
                }
            }
        }
    }

    @Override
    @Scheduled( fixedDelay = 86400000 )   // each day
    @Transactional
    public void reportOnNewFeedback() {
        LOG.debug( "Sending out reports of new feedback" );
        for ( Plan plan : planManager.getPlans() ) {
            PlanService planService = planServiceFactory.getService( plan );
            List<Feedback> normalFeedbacks = feedbackService.listNotYetNotifiedNormalFeedbacks( plan );
            if ( !normalFeedbacks.isEmpty() ) {
                Collections.sort( normalFeedbacks,
                        new Comparator<Feedback>() {
                            @Override
                            public int compare( Feedback f1, Feedback f2 ) {
                                return f1.getCreated().compareTo( f2.getCreated() );
                            }
                        });
                boolean success = sendReport(
                        getPlanners( plan ),
                        normalFeedbacks,
                        UserStatement.TEXT,
                        planService );
                if ( success ) {
                    for ( Feedback normalFeedback : normalFeedbacks ) {
                        normalFeedback.setWhenNotified( new Date() );
                        feedbackService.save( normalFeedback );
                    }
                }
            }
        }
    }

    //// SURVEYS ////

    @Override
    @Scheduled( fixedDelay = 60000 )     // each minute
    @Transactional
    public void notifyOfSurveys() {
        for ( Plan plan : planManager.getPlans() ) {
            PlanService planService = planServiceFactory.getService( plan );
            // to survey participants
            sendNags( planService );
            sendDeadlineApproachingNotifications( planService );
        }
    }

    private void sendNags( PlanService planService ) {
        LOG.debug( "Sending out nags about overdue RFIs" );
        List<RFI> nagRFIs = rfiService.listRequestedNags( planService.getPlan() );
        for ( RFI nagRfi : nagRFIs ) {
            boolean success = sendMessages( nagRfi, RFI.NAG, planService );
            if ( success ) {
                nagRfi.nagged();
                rfiService.save( nagRfi );
            }
        }
    }


    private void sendDeadlineApproachingNotifications( PlanService planService ) {
        LOG.debug( "Sending RFI deadline warnings" );
        List<RFI> deadlineRFIs = rfiService.listApproachingDeadline( planService.getPlan(), WARNING_DELAY );
        for ( RFI deadlineRfi : deadlineRFIs ) {
            boolean success = sendMessages( deadlineRfi, RFI.DEADLINE, planService );
            if ( success ) {
                deadlineRfi.addNotification( RFI.DEADLINE );
                rfiService.save( deadlineRfi );
            }
        }
    }

    @Override
    @Scheduled( fixedDelay = 86400000 )   // each day
    @Transactional
    public void reportOnSurveys() {
        LOG.debug( "Sending out reports of new feedback" );
        for ( Plan plan : planManager.getPlans() ) {
            PlanService planService = planServiceFactory.getService( plan );
            // to survey participants
            sendIncompleteRFIReports( planService );
            // to planners
            sendSurveyStatusReports( planService );
        }
    }

    private void sendSurveyStatusReports( PlanService planService ) {
        // to planners
        List<RFISurvey> activeSurveys = rfiSurveyService.listActive( planService, analyst );
        Collections.sort(
                activeSurveys,
                new Comparator<RFISurvey>() {
                    @Override
                    public int compare( RFISurvey s1, RFISurvey s2 ) {
                        return s2.getCreated().compareTo( s1.getCreated() );
                    }
                });
        if ( !activeSurveys.isEmpty() ) {
            sendReport(
                    getPlanners( planService.getPlan() ),
                    activeSurveys,
                    RFISurvey.STATUS,
                    planService );
        }
    }

    private void sendIncompleteRFIReports( PlanService planService ) {
        // to survey participants
        List<RFI> incompleteRFIs = surveysDAO.listIncompleteActiveRFIs( planService, analyst );
        Map<String,List<RFI>> userRFIs = new HashMap<String, List<RFI>>(  );
        for ( RFI incompleteRFI : incompleteRFIs ) {
            String surveyedUsername = incompleteRFI.getSurveyedUsername();
            List<RFI> rfis = userRFIs.get( surveyedUsername );
            if ( rfis == null ) {
                rfis = new ArrayList<RFI>(  );
                userRFIs.put( surveyedUsername, rfis );
            }
            rfis.add( incompleteRFI );
        }
        for ( String surveyedUsername : userRFIs.keySet() ) {
            ChannelsUser user = userDao.getUserNamed( surveyedUsername );
            if ( user != null ) {
                List<RFI> rfis = userRFIs.get( surveyedUsername );
                Collections.sort(
                        rfis,
                        new Comparator<RFI>() {
                            @Override
                            public int compare( RFI rfi1, RFI rfi2 ) {
                                return rfi1.compareUrgencyTo( rfi2, surveysDAO );
                            }
                        });
                sendReport(
                        user.getUserInfo(),
                        rfis,
                        RFI.TODO,
                        planService
                );
            }
        }
    }


    //


    private boolean sendMessages( Messageable messageable, String topic, PlanService planService ) {
        return sendMessages( messageable, topic, !EXCLUDE_INTERNAL_MESSAGES, planService );
    }

    private boolean sendMessages(
            Messageable messageable,
            String topic,
            boolean excludeInternalMessages,
            PlanService planService ) {
        boolean notified = false;
        for ( MessagingService messagingService : messagingServices ) {
            if ( !( excludeInternalMessages && messagingService.isInternal() ) ) {
                boolean success = messagingService.sendMessage(
                        messageable,
                        topic,
                        planService,
                        surveysDAO );
                notified = notified || success;
            }
        }
        return notified;
    }

    private boolean sendReport(
            ChannelsUserInfo recipient,
            List<? extends Messageable> messageables,
            String topic,
            PlanService planService ) {
        List<ChannelsUserInfo> recipients = new ArrayList<ChannelsUserInfo>(  );
        recipients.add(  recipient );
        return sendReport( recipients, messageables, topic, planService );
    }

        private boolean sendReport(
            List<ChannelsUserInfo> recipients,
            List<? extends Messageable> messageables,
            String topic,
            PlanService planService ) {
        boolean reported = false;
        for ( MessagingService messagingService : messagingServices ) {
            boolean success = messagingService.sendReport(
                    recipients,
                    messageables,
                    topic,
                    planService,
                    surveysDAO );
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
