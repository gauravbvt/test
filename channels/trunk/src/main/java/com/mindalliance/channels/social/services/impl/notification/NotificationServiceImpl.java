package com.mindalliance.channels.social.services.impl.notification;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.core.query.ModelServiceFactory;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.data.messages.UserMessage;
import com.mindalliance.channels.db.data.messages.UserStatement;
import com.mindalliance.channels.db.data.surveys.RFI;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.db.data.users.UserAccess;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.messages.FeedbackService;
import com.mindalliance.channels.db.services.messages.MessageOutboxService;
import com.mindalliance.channels.db.services.messages.UserMessageService;
import com.mindalliance.channels.db.services.surveys.RFIService;
import com.mindalliance.channels.db.services.surveys.RFISurveyService;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.engine.analysis.Analyst;
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
import java.util.concurrent.atomic.AtomicBoolean;

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
    private ModelServiceFactory modelServiceFactory;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserRecordService userDao;

    @Autowired
    private ModelManager modelManager;

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
    @Autowired
    private CommunityServiceFactory communityServiceFactory;
    @Autowired
    private UserRecordService userRecordService;

    private List<MessagingService> messagingServices;

    
    private AtomicBoolean initializing = new AtomicBoolean( true );
    
    private static final long WARNING_DELAY = 1000 * 60 * 60 * 24; // 1 day in msecs

    @Override
    public void afterPropertiesSet() {
        messagingServices = new ArrayList<MessagingService>();
        messagingServices.add( emailMessagingService );
        messagingServices.add( channelsMessagingService );
        initializing.set( false );
    }

    //// USER MESSAGES ////

    @Override
    @Scheduled(fixedDelay = 60000)     // every minute
    public void notifyOfUserMessages() {
        if ( isInitializing() )
            return;
        
        LOG.debug( "Sending out user messages" );
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            CommunityService communityService = getCommunityService( planCommunity );
            sendPendingMessages( userMessageService, communityService );
            sendPendingMessages( feedbackService, communityService );
        }
    }

    private boolean isInitializing() {
        return initializing.get();
    }

    private void sendPendingMessages( MessageOutboxService messageOutboxService, CommunityService communityService ) {
        Iterator<UserMessage> messagesToSend = messageOutboxService.listMessagesToSend( communityService.getPlanCommunity().getUri() );
        while ( messagesToSend.hasNext() ) {
            UserMessage messageToSend = messagesToSend.next();
            List<String> successes = sendMessages(
                    messageToSend,
                    UserStatement.TEXT,
                    EXCLUDE_INTERNAL_MESSAGES,
                    communityService );
            // success = at least one message went out. No retries. Todo: retry each messaging failure?
            if ( !successes.isEmpty() ) {   // todo: assumes all messages sent successfully or none are
                messageOutboxService.markSent( messageToSend );
            }
        }
    }

    //// FEEDBACK ////

    @Override
    @Scheduled(fixedDelay = 60000)     // every minute
     public void notifyOfUrgentFeedback() {
        if ( isInitializing() )
            return;
        LOG.debug( "Sending out urgent feedback" );
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            if ( planCommunity.isModelCommunity() ) {
                CommunityService communityService = getCommunityService( planCommunity );
                List<Feedback> urgentFeedbacks = feedbackService.listNotYetNotifiedUrgentFeedbacks( communityService );
                for ( Feedback urgentFeedback : urgentFeedbacks ) {
                    List<String> successes = sendMessages(
                            urgentFeedback,
                            UserStatement.TEXT,
                            !EXCLUDE_INTERNAL_MESSAGES,
                            communityService );
                    if ( !successes.isEmpty() ) {   // todo: assumes all messages sent successfully or none are
                        urgentFeedback.setWhenNotified( new Date() );
                        feedbackService.save( urgentFeedback );
                    }
                }
            }
        }
    }

    @Override
//    @Scheduled( fixedDelay = 86400000 )   // each day
    public void reportOnNewFeedback() {
        if ( isInitializing() )
            return;
        LOG.debug( "Sending out reports of new feedback" );
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            CommunityService communityService = getCommunityService( planCommunity );
            if ( planCommunity.isModelCommunity() ) {
                List<Feedback> normalFeedbacks = feedbackService.listNotYetNotifiedNormalFeedbacks( communityService );
                if ( !normalFeedbacks.isEmpty() ) {
                    Collections.sort( normalFeedbacks,
                            new Comparator<Feedback>() {
                                @Override
                                public int compare( Feedback f1, Feedback f2 ) {
                                    return f1.getCreated().compareTo( f2.getCreated() );
                                }
                            } );
                    boolean success = sendReport(
                            getPlanners( getCommunityService( planCommunity ).getPlan() ),
                            normalFeedbacks,
                            UserStatement.TEXT,
                            communityService );
                    if ( success ) {
                        for ( Feedback normalFeedback : normalFeedbacks ) {
                            normalFeedback.setWhenNotified( new Date() );
                            feedbackService.save( normalFeedback );
                        }
                    }
                }
            }
        }
    }

    //// SURVEYS ////

    @Override
    @Scheduled(fixedDelay = 60000)     // every minute
    public void notifyOfSurveys() {
        if ( isInitializing() )
            return;
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            if ( planCommunity.isModelCommunity() ) {
                // to survey participants
                CommunityService communityService = getCommunityService( planCommunity );
                sendNags( communityService );
                sendDeadlineApproachingNotifications( communityService );
                sendNewRFIs( communityService );
            }
        }
    }

    private void sendNags( CommunityService communityService ) {
        LOG.debug( "Sending out nags about overdue RFIs" );
        List<RFI> nagRFIs = rfiService.listRequestedNags( communityService );
        SurveysDAO surveysDAO = communityService.getModelService().getSurveysDAO();
        for ( RFI nagRfi : nagRFIs ) {
            if ( !surveysDAO.isCompleted( nagRfi ) ) {
                List<String> successes = sendMessages( nagRfi, RFI.NAG, communityService );
                if ( !successes.isEmpty() ) {   // todo: assumes all messages sent successfully or none are
                    nagRfi.nagged();
                    rfiService.save( nagRfi );
                }
            }
        }
    }


    private void sendDeadlineApproachingNotifications( CommunityService communityService ) {
        LOG.debug( "Sending RFI deadline warnings" );
        List<RFI> deadlineRFIs = rfiService.listApproachingDeadline( communityService, WARNING_DELAY );
        SurveysDAO surveysDAO = communityService.getModelService().getSurveysDAO();
        for ( RFI deadlineRFI : deadlineRFIs ) {
            if ( !surveysDAO.isCompleted( deadlineRFI ) ) {
                List<String> successes = sendMessages( deadlineRFI, RFI.DEADLINE, communityService );
                if ( !successes.isEmpty() ) {   // todo: assumes all messages sent successfully or none are
                    deadlineRFI.addNotification( RFI.DEADLINE );
                    rfiService.save( deadlineRFI );
                }
            }
        }
    }

    private void sendNewRFIs( CommunityService communityService ) {
        LOG.debug( "Sending new RFI notices" );
        List<RFI> newRFIs = rfiService.listNewRFIs( communityService );
        for ( RFI newRFI : newRFIs ) {
            List<String> successes = sendMessages( newRFI, RFI.NEW, communityService );
            if ( !successes.isEmpty() ) {   // todo: assumes all messages sent successfully or none are
                newRFI.addNotification( RFI.NEW );
                rfiService.save( newRFI );
            }
        }

    }


    @Override
//    @Scheduled( fixedDelay = 86400000 )   // each day
    public void reportOnSurveys() {
        if ( isInitializing() )
            return;
        LOG.debug( "Sending out reports of incomplete surveys and surveys status" );
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            if ( planCommunity.isModelCommunity() ) {
                CommunityService communityService = getCommunityService( planCommunity );
                if ( communityService.getPlan().isDevelopment() ) { // send once per plan - there's exactly one dev version per plan
                    // to survey participants
                    sendIncompleteRFIReports( communityService );
                    // to planners
                    sendSurveyStatusReports( communityService );
                }
            }
        }
    }

    @Override
    @Scheduled(fixedDelay = 60000)     // every minute
    public void notifyOnUserAccessChange() {
        if ( isInitializing() )
            return;
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            CommunityService communityService = getCommunityService( planCommunity );
            String uri = communityService.getPlanCommunity().getUri();
            for ( ChannelsUser user : userRecordService.getUsers( uri ) ) {
                UserRecord userRecord = user.getUserRecord();
                List<UserAccess> toNotifyAbout = new ArrayList<UserAccess>( userRecord.getAccessChangesToNotify( uri ) );
                if ( !toNotifyAbout.isEmpty() ) {
                    List<String> successes = sendMessages(
                            user.getUserRecord(),
                            UserRecord.ACCESS_PRIVILEGES_CHANGED,
                            false,
                            communityService
                    );
                    if ( !successes.isEmpty() ) {
                        userRecord.resetAccessChangeToNotify( uri );
                        userRecordService.save( userRecord );
                    }
                }
            }
        }
    }

    @Override
    @Scheduled(fixedDelay = 60000)     // every minute
    public void notifyOfParticipationConfirmation() {
        if ( isInitializing() )
            return;
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            ChannelsUser.current().setCommunityService( getCommunityService( planCommunity ) );
            CommunityService communityService = getCommunityService( planCommunity );
            ParticipationManager participationManager = communityService.getParticipationManager();
            UserParticipationService userParticipationService = communityService.getUserParticipationService();
            for ( UserParticipation userParticipation : participationManager.getAllParticipations( communityService ) ) {
                if ( userParticipation.isSupervised( communityService )
                        && userParticipation.getSupervisorsNotified().isEmpty() ) { // todo - assumes all then supervisors were notified
                    List<String> successes = sendMessages(
                            userParticipation,
                            UserParticipation.VALIDATION_REQUESTED,
                            false,
                            communityService );
                    for ( String username : successes ) {
                        userParticipation.addUserNotifiedToValidate( username );
                    }
                    userParticipationService.save( userParticipation );
                }
            }
        }
    }

    @Override
    @Scheduled(fixedDelay = 86400000)   // each day
    public void reportOnParticipationConfirmation() {
        if ( !initializing.get() )
            return;
        // todo
    }

    @Override
    @Scheduled(fixedDelay = 60000)     // every minute
    public void notifyOfParticipationRequest() {
        if ( !initializing.get() )
            return;
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            ChannelsUser.current().setCommunityService( getCommunityService( planCommunity ) );
            CommunityService communityService = getCommunityService( planCommunity );
            UserParticipationService userParticipationService = communityService.getUserParticipationService();
            synchronized ( planCommunity ) {
                for ( UserParticipation userParticipation : communityService.getParticipationManager().getAllParticipations( communityService ) ) {
                    if ( userParticipation.isRequested()
                            && !userParticipation.isAccepted()
                            && !userParticipation.isRequestNotified() ) {
                        List<String> successes = sendMessages(
                                userParticipation,
                                UserParticipation.ACCEPTANCE_REQUESTED,
                                false,
                                communityService );
                        if ( !successes.isEmpty() ) {
                            userParticipation.setRequestNotified( true );
                        }
                        userParticipationService.save( userParticipation );
                    }
                }
            }
        }
    }

    @Override
    @Scheduled( fixedDelay = 86400000 )   // each day
    public void reportOnParticipationRequests() {
        if ( !initializing.get() )
            return;
        //Todo
    }

    private void sendSurveyStatusReports( CommunityService communityService ) {
        // to planners
        ModelService modelService = communityService.getModelService();
        List<RFISurvey> activeSurveys = rfiSurveyService.listActive( communityService );
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
                    getPlanners( modelService.getCollaborationModel() ),
                    activeSurveys,
                    RFISurvey.STATUS,
                    communityService );
        }
    }

    private void sendIncompleteRFIReports( CommunityService communityService ) {
        // to survey participants
        ModelService modelService = communityService.getModelService();
        final SurveysDAO surveysDAO = modelService.getSurveysDAO();
        List<RFI> incompleteRFIs = surveysDAO.listIncompleteActiveRFIs( communityService );
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
            ChannelsUser user = userDao.getUserWithIdentity( surveyedUsername );
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
                        user.getUserRecord(),
                        rfis,
                        RFI.TODO,
                        communityService
                );
            }
        }
    }


    //


    private List<String> sendMessages( Messageable messageable, String topic, CommunityService communityService ) {
        return sendMessages( messageable, topic, !EXCLUDE_INTERNAL_MESSAGES, communityService );
    }

    private List<String> sendMessages(
            Messageable messageable,
            String topic,
            boolean excludeInternalMessages,
            CommunityService communityService ) {
        Set<String> allSuccesses = new HashSet<String>();
        for ( MessagingService messagingService : messagingServices ) {
            if ( !( excludeInternalMessages && messagingService.isInternal() ) ) {
                List<String> successes = messagingService.sendMessage(
                        messageable,
                        topic,
                        communityService );
                allSuccesses.addAll( successes );
            }
        }
        return new ArrayList<String>( allSuccesses );
    }

    private boolean sendReport(
            UserRecord recipient,
            List<? extends Messageable> messageables,
            String topic,
            CommunityService communityService ) {
        List<UserRecord> recipients = new ArrayList<UserRecord>();
        recipients.add( recipient );
        return sendReport( recipients, messageables, topic, communityService );
    }

    private boolean sendReport(
            List<UserRecord> recipients,
            List<? extends Messageable> messageables,
            String topic,
            CommunityService communityService ) {
        boolean reported = false;
        for ( MessagingService messagingService : messagingServices ) {
            boolean success = messagingService.sendReport(
                    recipients,
                    messageables,
                    topic,
                    communityService );
            reported = reported || success;
        }
        return reported;
    }

    private List<UserRecord> getPlanners( CollaborationModel collaborationModel ) {
        List<UserRecord> developers = new ArrayList<UserRecord>();
        for ( ChannelsUser user : userDao.getDevelopers( collaborationModel.getUri() ) ) {
            developers.add( user.getUserRecord() );
        }
        return developers;
    }

    private CommunityService getCommunityService( PlanCommunity planCommunity ) {
        return communityServiceFactory.getService( planCommunity );
    }
}
