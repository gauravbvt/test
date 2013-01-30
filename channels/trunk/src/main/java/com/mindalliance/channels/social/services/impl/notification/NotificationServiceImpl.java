package com.mindalliance.channels.social.services.impl.notification;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private PlanServiceFactory planServiceFactory;

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
    private UserParticipationService userParticipationService;
    @Autowired
    private RFIService rfiService;
    @Autowired
    private RFISurveyService rfiSurveyService;
    @Autowired
    private PlanCommunityManager planCommunityManager;

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
    @Scheduled(fixedDelay = 60000)     // every minute
    @Transactional
    public void notifyOfUserMessages() {
        LOG.debug( "Sending out user messages" );
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            Iterator<UserMessage> messagesToSend = userMessageService.listMessagesToSend( planCommunity.getUri() );
            while ( messagesToSend.hasNext() ) {
                UserMessage messageToSend = messagesToSend.next();
                List<String> successes = sendMessages(
                        messageToSend,
                        UserStatement.TEXT,
                        EXCLUDE_INTERNAL_MESSAGES,
                        planCommunity );
                // success = at least one message went out. No retries. Todo: retry each messaging failure?
                if ( !successes.isEmpty() ) {   // todo: assumes all messages sent successfully or none are
                    userMessageService.markSent( messageToSend );
                }
            }
        }
    }

    //// FEEDBACK ////

    @Override
    @Scheduled(fixedDelay = 60000)     // every minute
    @Transactional
    public void notifyOfUrgentFeedback() {
        LOG.debug( "Sending out urgent feedback" );
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            List<Feedback> urgentFeedbacks = feedbackService.listNotYetNotifiedUrgentFeedbacks( planCommunity );
            for ( Feedback urgentFeedback : urgentFeedbacks ) {
                List<String> successes = sendMessages(
                        urgentFeedback,
                        UserStatement.TEXT,
                        !EXCLUDE_INTERNAL_MESSAGES,
                        planCommunity );
                if ( !successes.isEmpty() ) {   // todo: assumes all messages sent successfully or none are
                    urgentFeedback.setWhenNotified( new Date() );
                    feedbackService.save( urgentFeedback );
                }
            }
        }
    }

    @Override
//    @Scheduled( fixedDelay = 86400000 )   // each day
    @Transactional
    public void reportOnNewFeedback() {
        LOG.debug( "Sending out reports of new feedback" );
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            List<Feedback> normalFeedbacks = feedbackService.listNotYetNotifiedNormalFeedbacks( planCommunity );
            if ( !normalFeedbacks.isEmpty() ) {
                Collections.sort( normalFeedbacks,
                        new Comparator<Feedback>() {
                            @Override
                            public int compare( Feedback f1, Feedback f2 ) {
                                return f1.getCreated().compareTo( f2.getCreated() );
                            }
                        } );
                boolean success = sendReport(
                        getPlanners( planCommunity.getPlan() ),
                        normalFeedbacks,
                        UserStatement.TEXT,
                        planCommunity );
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
    @Scheduled(fixedDelay = 60000)     // every minute
    @Transactional
    public void notifyOfSurveys() {
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            // to survey participants
            sendNags( planCommunity );
            sendDeadlineApproachingNotifications( planCommunity );
            sendNewRFIs( planCommunity );
        }
    }

    private void sendNags( PlanCommunity planCommunity ) {
        LOG.debug( "Sending out nags about overdue RFIs" );
        List<RFI> nagRFIs = rfiService.listRequestedNags( planCommunity );
        SurveysDAO surveysDAO = planCommunity.getPlanService().getSurveysDAO();
        for ( RFI nagRfi : nagRFIs ) {
            if ( !surveysDAO.isCompleted( nagRfi ) ) {
                List<String> successes = sendMessages( nagRfi, RFI.NAG, planCommunity );
                if ( !successes.isEmpty() ) {   // todo: assumes all messages sent successfully or none are
                    nagRfi.nagged();
                    rfiService.save( nagRfi );
                }
            }
        }
    }


    private void sendDeadlineApproachingNotifications( PlanCommunity planCommunity ) {
        LOG.debug( "Sending RFI deadline warnings" );
        List<RFI> deadlineRFIs = rfiService.listApproachingDeadline( planCommunity, WARNING_DELAY );
        SurveysDAO surveysDAO = planCommunity.getPlanService().getSurveysDAO();
        for ( RFI deadlineRFI : deadlineRFIs ) {
            if ( !surveysDAO.isCompleted( deadlineRFI ) ) {
                List<String> successes = sendMessages( deadlineRFI, RFI.DEADLINE, planCommunity );
                if ( !successes.isEmpty() ) {   // todo: assumes all messages sent successfully or none are
                    deadlineRFI.addNotification( RFI.DEADLINE );
                    rfiService.save( deadlineRFI );
                }
            }
        }
    }

    private void sendNewRFIs( PlanCommunity planCommunity ) {
        LOG.debug( "Sending new RFI notices" );
        List<RFI> newRFIs = rfiService.listNewRFIs( planCommunity );
        for ( RFI newRFI : newRFIs ) {
            List<String> successes = sendMessages( newRFI, RFI.NEW, planCommunity );
            if ( !successes.isEmpty() ) {   // todo: assumes all messages sent successfully or none are
                newRFI.addNotification( RFI.NEW );
                rfiService.save( newRFI );
            }
        }

    }


    @Override
//    @Scheduled( fixedDelay = 86400000 )   // each day
    @Transactional
    public void reportOnSurveys() {
        LOG.debug( "Sending out reports of new feedback" );
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            // to survey participants
            sendIncompleteRFIReports( planCommunity );
            // to planners
            sendSurveyStatusReports( planCommunity );
        }
    }

    @Override
    @Scheduled(fixedDelay = 60000)     // every minute
    @Transactional
    public void notifyOfParticipationConfirmation() {
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            ChannelsUser.current().setPlanCommunity( planCommunity );
            UserParticipationService userParticipationService = planCommunity.getUserParticipationService();
            PlanService planService = planCommunity.getPlanService();
            for ( UserParticipation userParticipation : userParticipationService.getAllParticipations( planCommunity ) ) {
                if ( userParticipation.isSupervised( planCommunity ) ) {
                    List<String> successes = sendMessages(
                            userParticipation,
                            UserParticipation.VALIDATION_REQUESTED,
                            false,
                            planCommunity );
                    for ( String username : successes ) {
                        userParticipation.addUserNotifiedToValidate( username );
                    }
                    userParticipationService.save( userParticipation );
                }
            }
        }
    }

    @Override
//    @Scheduled( fixedDelay = 86400000 )   // each day
    @Transactional
    public void reportOnParticipationConfirmation() {
        // todo
    }

    @Override
    @Scheduled(fixedDelay = 60000)     // every minute
    @Transactional
    public void notifyOfParticipationRequest() {
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            PlanService planService = planCommunity.getPlanService();
            ChannelsUser.current().setPlanCommunity( planCommunity );
            UserParticipationService userParticipationService = planCommunity.getUserParticipationService();
            for ( UserParticipation userParticipation : userParticipationService.getAllParticipations( planCommunity ) ) {
                if ( userParticipation.isRequested()
                        && !userParticipation.isAccepted()
                        && !userParticipation.isRequestNotified() ) {
                    List<String> successes = sendMessages(
                            userParticipation,
                            UserParticipation.ACCEPTANCE_REQUESTED,
                            false,
                            planCommunity );
                    if ( !successes.isEmpty() ) {
                        userParticipation.setRequestNotified( true );
                    }
                    userParticipationService.save( userParticipation );
                }
            }
        }
    }

    @Override
//    @Scheduled( fixedDelay = 86400000 )   // each day
    @Transactional
    public void reportOnParticipationRequests() {
        //Todo
    }

    private void sendSurveyStatusReports( PlanCommunity planCommunity ) {
        // to planners
        PlanService planService = planCommunity.getPlanService();
        List<RFISurvey> activeSurveys = rfiSurveyService.listActive( planCommunity );
        Collections.sort(
                activeSurveys,
                new Comparator<RFISurvey>() {
                    @Override
                    public int compare( RFISurvey s1, RFISurvey s2 ) {
                        return s2.getCreated().compareTo( s1.getCreated() );
                    }
                } );
        if ( !activeSurveys.isEmpty() ) {
            sendReport(
                    getPlanners( planService.getPlan() ),
                    activeSurveys,
                    RFISurvey.STATUS,
                    planCommunity );
        }
    }

    private void sendIncompleteRFIReports( PlanCommunity planCommunity ) {
        // to survey participants
        PlanService planService = planCommunity.getPlanService();
        final SurveysDAO surveysDAO = planService.getSurveysDAO();
        List<RFI> incompleteRFIs = surveysDAO.listIncompleteActiveRFIs( planCommunity );
        Map<String, List<RFI>> userRFIs = new HashMap<String, List<RFI>>();
        for ( RFI incompleteRFI : incompleteRFIs ) {
            String surveyedUsername = incompleteRFI.getSurveyedUsername();
            List<RFI> rfis = userRFIs.get( surveyedUsername );
            if ( rfis == null ) {
                rfis = new ArrayList<RFI>();
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
                        } );
                sendReport(
                        user.getUserInfo(),
                        rfis,
                        RFI.TODO,
                        planCommunity
                );
            }
        }
    }


    //


    private List<String> sendMessages( Messageable messageable, String topic, PlanCommunity planCommunity ) {
        return sendMessages( messageable, topic, !EXCLUDE_INTERNAL_MESSAGES, planCommunity );
    }

    private List<String> sendMessages(
            Messageable messageable,
            String topic,
            boolean excludeInternalMessages,
            PlanCommunity planCommunity ) {
        Set<String> allSuccesses = new HashSet<String>();
        for ( MessagingService messagingService : messagingServices ) {
            if ( !( excludeInternalMessages && messagingService.isInternal() ) ) {
                List<String> successes = messagingService.sendMessage(
                        messageable,
                        topic,
                        planCommunity );
                allSuccesses.addAll( successes );
            }
        }
        return new ArrayList<String>( allSuccesses );
    }

    private boolean sendReport(
            ChannelsUserInfo recipient,
            List<? extends Messageable> messageables,
            String topic,
            PlanCommunity planCommunity ) {
        List<ChannelsUserInfo> recipients = new ArrayList<ChannelsUserInfo>();
        recipients.add( recipient );
        return sendReport( recipients, messageables, topic, planCommunity );
    }

    private boolean sendReport(
            List<ChannelsUserInfo> recipients,
            List<? extends Messageable> messageables,
            String topic,
            PlanCommunity planCommunity ) {
        boolean reported = false;
        for ( MessagingService messagingService : messagingServices ) {
            boolean success = messagingService.sendReport(
                    recipients,
                    messageables,
                    topic,
                    planCommunity );
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
