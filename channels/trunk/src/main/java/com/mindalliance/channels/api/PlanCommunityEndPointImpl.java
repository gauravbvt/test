package com.mindalliance.channels.api;

import com.mindalliance.channels.api.community.CommunitySummariesData;
import com.mindalliance.channels.api.community.CommunitySummaryData;
import com.mindalliance.channels.api.directory.DirectoryData;
import com.mindalliance.channels.api.issues.IssuesData;
import com.mindalliance.channels.api.plan.ModelReleaseData;
import com.mindalliance.channels.api.plan.ModelScopeData;
import com.mindalliance.channels.api.plan.ModelSummariesData;
import com.mindalliance.channels.api.plan.ModelSummaryData;
import com.mindalliance.channels.api.procedures.AllChecklistsData;
import com.mindalliance.channels.api.procedures.ChecklistsData;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.messages.FeedbackService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.social.services.notification.EmailMessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Plan community service implementation.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/11
 * Time: 11:34 AM
 */
@Path( "/isp" )
@WebService(
        endpointInterface = "com.mindalliance.channels.api.PlanCommunityEndPoint"
)
public class PlanCommunityEndPointImpl implements PlanCommunityEndPoint {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanCommunityEndPointImpl.class );

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ModelManager modelManager;
    @Autowired
    private UserRecordService userRecordService;
    @Autowired
    private UserParticipationService userParticipationService;
    @Autowired
    private EmailMessagingService emailMessagingService;
    @Autowired
    private PlanCommunityManager planCommunityManager;
    @Autowired
    private ParticipationManager participationManager;
    @Autowired
    private RegisteredOrganizationService registeredOrganizationService;

    private String serverUrl;

    @Autowired
    private CommunityServiceFactory communityServiceFactory;

    /// PLANS


    @Override
    /**
     * Get summaries of all versions of all plans visible to the user.
     * @return plan summaries
     */

    public ModelSummariesData getModels() {
        LOG.info( "Getting summaries for all visible models" );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        List<ModelSummaryData> result = new ArrayList<ModelSummaryData>();
        for ( CollaborationModel collaborationModel : modelManager.getModels() ) {
            String uri = collaborationModel.getUri();
            if ( !user.getRole( uri ).equals( ChannelsUser.UNAUTHORIZED )
                    && ( user.isDeveloperOrAdmin( uri ) || collaborationModel.isProduction() ) ) {
                user.setCollaborationModel( collaborationModel );
                result.add( new ModelSummaryData(
                        serverUrl,
                        getCommunityService( collaborationModel ) ) );
            }
        }
        return new ModelSummariesData( result );
    }

    @Override
    public ModelSummaryData getModel( String uri, String version ) {
        LOG.info( "Getting summary for " + uri + " and version " + version );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeAccessToPlan( user, uri, version );
            CommunityService communityService = getCommunityService( planCommunity );
            ModelSummaryData modelSummaryData = new ModelSummaryData(
                    serverUrl,
                    communityService );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
            return modelSummaryData;
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() )
                            .build() );
        }

    }

    @Override
    public ModelReleaseData getModelRelease( String uri ) {
        LOG.info( "Getting release info for community " + uri );
        try {
            ChannelsUser user = ChannelsUser.current( userRecordService );
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            CommunityService communityService = getCommunityService( planCommunity );
            ModelReleaseData modelReleaseData = new ModelReleaseData( communityService );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
            return modelReleaseData;
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No release data available for community " + uri )
                            .build() );
        }
    }

    @Override
    /**
     * Get summaries of all versions of all plans visible to the user.
     * @return plan summaries
     */

    public ModelSummariesData getProductionModels() {
        LOG.info( "Getting summaries for all user-visible communities" );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        List<ModelSummaryData> result = new ArrayList<ModelSummaryData>();
        for ( CollaborationModel collaborationModel : modelManager.getModels() ) {
            String uri = collaborationModel.getUri();
            if ( !user.getRole( uri ).equals( ChannelsUser.UNAUTHORIZED )
                    && collaborationModel.isProduction() ) {
                user.setCollaborationModel( collaborationModel );
                result.add( new ModelSummaryData(
                        serverUrl,
                        getCommunityService( collaborationModel ) ) );
            }
        }
        return new ModelSummariesData( result );
    }


    @Override
    /**
     * Get scope of production plan.
     * Available only to its planners.
     * @param uri the plan's URN
     * @param version a plan version
     * @return a plan's scope
     */
    public ModelScopeData getModelScope( String uri, String version ) {
        return modelScope( uri, version, true );
    }

    public ModelScopeData modelScope( String uri, String version, boolean developerOnly ) {
        LOG.info( "Getting scope for plan " + uri + " version " + version );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeAccessToModel( user, uri, version, developerOnly );
            ModelScopeData modelScopeData = new ModelScopeData(
                    serverUrl,
                    getCommunityService( planCommunity ) );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
            return modelScopeData;
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() )
                            .build() );
        }
    }


    @Override
    public IssuesData getModelIssues( String uri, String version ) {
        LOG.info( "Getting issues in model " + uri + " version " + version );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizePlanner( user, uri, version );
            CommunityService communityService = getCommunityService( planCommunity );
            IssuesData issuesData = new IssuesData( serverUrl, communityService );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
            return issuesData;
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for collaboration model" + uri )
                            .build() );
        }
    }

    public void addModelFeedback(
            String uri,
            String type,
            String feedback,
            String urgent ) {
        LOG.info( "Receiving feedback about model " + uri );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            feedbackService.sendFeedback(
                    user.getUsername(),
                    getCommunityService( planCommunity.getModelUri() ),
                    Feedback.Type.valueOf( type ),
                    Feedback.CHECKLISTS,
                    feedback,
                    Boolean.parseBoolean( urgent ) );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for community " + uri )
                            .build() );
        }
    }

    @Override
    public void inviteToModel( String uri, String email, String message ) {
        LOG.info( "Inviting user to participate in community " + uri );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            CommunityService communityService = getCommunityService( planCommunity );
            UserRecord invitedUser = userRecordService.getOrMakeUserFromEmail( email, communityService );
            message = message + makeInvitation( invitedUser, communityService );
            emailMessagingService.sendInvitation( user, invitedUser.getEmail(), message );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for community " + uri )
                            .build() );
        }
    }


    //// COMMUNITIES


    @Override
    public CommunitySummariesData getAllCommunities() {
        LOG.info( "Getting all community summaries" );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        try {
            List<CommunitySummaryData> summaries = new ArrayList<CommunitySummaryData>();
            for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
                if ( !planCommunity.isModelCommunity() ) {
                    CommunityService communityService = getCommunityService( planCommunity );
                    if ( communityService.isCommunityPlanner( user ) ) {
                        summaries.add( new CommunitySummaryData( getServerUrl(), communityService ) );
                    }
                }
            }
            return new CommunitySummariesData( summaries );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for communities" )
                            .build() );
        }
    }

    @Override
    public CommunitySummaryData getCommunity( String communityUri ) {
        LOG.info( "Getting community summary for " + communityUri );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            if ( !planCommunity.isModelCommunity() ) {
                CommunityService communityService = communityServiceFactory.getService( planCommunity );
                CommunitySummaryData communitySummaryData = new CommunitySummaryData( serverUrl, communityService );
                user.setCollaborationModel( oldCollaborationModel );
                user.setPlanCommunityUri( oldPlanCommunityUri );
                return communitySummaryData;
            } else {
                throw new WebApplicationException(
                        Response
                                .status( Response.Status.BAD_REQUEST )
                                .entity( "No such community" + communityUri )
                                .build() );
            }
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for community" + communityUri )
                            .build() );
        }
    }

/*
    @Override
    public UserParticipationData getMyParticipation( String communityUri ) {
        return null;  // Todo
    }
*/

    @Override
    public AllChecklistsData getAllChecklists( String communityUri ) {
        ChannelsUser user = ChannelsUser.current( userRecordService );
        LOG.info( "Getting all user checklists for all participants in community " + communityUri );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeCommunityLeader( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            AllChecklistsData allChecklistsData = new AllChecklistsData();
            for ( ChannelsUser channelsUser : userRecordService.getUsers( communityUri ) ) {
                channelsUser.setCommunityService( communityService );
                List<UserParticipation> participationList = participationManager.getActiveUserParticipations(
                        channelsUser,
                        communityService
                );
                if ( !participationList.isEmpty() ) {
                    ChecklistsData checklistsData = getUserChecklists( communityUri, channelsUser.getUsername() );
                    allChecklistsData.addChecklistsData( checklistsData );
                }
            }
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
            return allChecklistsData;
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No checklists available for community " + communityUri )
                            .build() );
        }
    }


    @Override
    public ChecklistsData getMyChecklists( String communityUri ) {
        LOG.info( "Getting user checklists for community " + communityUri );
        try {
            ChannelsUser user = ChannelsUser.current( userRecordService );
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            List<UserParticipation> participations = participationManager.getActiveUserParticipations(
                    user,
                    communityService );
            ChecklistsData checklistsData = new ChecklistsData(
                    serverUrl,
                    communityService,
                    participations,
                    user );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
            return checklistsData;
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No checklists available for community " + communityUri )
                            .build() );
        }
    }

    @Override
    public ChecklistsData getUserChecklists( String communityUri,
                                            String username ) {
        LOG.info( "Getting user checklists for community " + communityUri );
        try {
            ChannelsUser user = ChannelsUser.current( userRecordService );
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            List<UserParticipation> participations = participationManager.getActiveUserParticipations(
                    user,
                    communityService );
            ChecklistsData checklistsData = new ChecklistsData(
                    serverUrl,
                    communityService,
                    participations,
                    user );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
            return checklistsData;
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No checklists available for community " + communityUri )
                            .build() );
        }
    }

    @Override
    public ChecklistsData getAgentChecklists( String communityUri,
                                             String actorId,
                                             String registeredOrganizationId ) {
        ChannelsUser user = ChannelsUser.current( userRecordService );
        LOG.info( "Getting checklists of agent " + actorId
                + " for organization participation" + registeredOrganizationId
                + " in community " + communityUri );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            Actor actor = communityService.getModelService().find( Actor.class, Long.parseLong( actorId ) );
            RegisteredOrganization registeredOrganization =
                    registeredOrganizationService.load( registeredOrganizationId );
            if ( registeredOrganization == null ) throw new NotFoundException();
            ChecklistsData checklistsData = new ChecklistsData(
                    serverUrl,
                    communityService,
                    new Agent( actor, registeredOrganization, communityService ),
                    user );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
            return checklistsData;
        } catch ( Exception e ) {
            LOG.warn( "No checklists available for agent " + actorId, e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No checklists available for community " + communityUri )
                            .build() );
        }
    }

    @Override
    public DirectoryData getUserDirectory(
            String communityUri,
            String username ) {
        try {
            ChecklistsData checklistsData = getUserChecklists( communityUri, username );
            return new DirectoryData( checklistsData );
        } catch ( Exception e ) {
            LOG.warn( "Failed to retrieve directory", e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No checklists available for community " + communityUri )
                            .build() );
        }
    }

    @Override
    public DirectoryData getMyDirectory( String communityUri ) {
        LOG.info( "Getting user directory for " + communityUri );
        try {
            ChannelsUser user = ChannelsUser.current( userRecordService );
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            List<UserParticipation> participations = participationManager.getActiveUserParticipations(
                    user,
                    communityService );
            if ( participations.isEmpty() ) {
                throw new Exception( user.getUsername() + " does not participate in community " + communityUri );
            }
            ChecklistsData checklistsData = getMyChecklists( communityUri );
            DirectoryData directoryData = new DirectoryData( checklistsData );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
            return directoryData;
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for community " + communityUri )
                            .build() );
        }
    }

    @Override
    public void addContactInfo( String communityUri, String mediumId, String address ) {
        LOG.info( "Adding user contact info " );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            TransmissionMedium medium = communityService.getModelService().find( TransmissionMedium.class, Long.parseLong( mediumId ) );
            if ( !medium.isAddressValid( address ) ) throw new Exception( "Invalid address" );
            userRecordService.addChannel(
                    user.getUsername(),
                    user.getUserRecord(),
                    new Channel( medium, address ),
                    communityService );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() )
                            .build() );
        }
    }

    @Override
    public void removeContactInfo( String communityUri, String mediumId, String address ) {
        LOG.info( "Removing user contact info " );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            TransmissionMedium medium = communityService.getModelService().find( TransmissionMedium.class, Long.parseLong( mediumId ) );
            userRecordService.removeChannel( user.getUserRecord(), new Channel( medium, address ), communityService );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() )
                            .build() );
        }
    }

    @Override
    public void acceptParticipation( String communityUri, String actorId, String orgId ) {
        LOG.info( "Adding user participation in community " + communityUri );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            Actor actor = getCommunityService( planCommunity ).getModelService().find( Actor.class, Long.parseLong( actorId ) );
            RegisteredOrganization registeredOrganization = registeredOrganizationService.load( orgId );
            if ( actor == null || registeredOrganization == null )
                throw new IllegalArgumentException();
            Agent agent = new Agent( actor, registeredOrganization, communityService );
            if ( participationManager.isParticipationSelfAssignable( agent, user, communityService ) ) {
                UserParticipation participation = new UserParticipation(
                        user.getUsername(),
                        user,
                        agent,
                        planCommunity );
                userParticipationService.accept( participation, communityService );
                user.setCollaborationModel( oldCollaborationModel );
                user.setPlanCommunityUri( oldPlanCommunityUri );
            } else {
                throw new Exception( "Participation was not accepted" );
            }
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for community " + communityUri )
                            .build() );
        }
    }

    @Override
    public void refuseParticipation( String communityUri, String actorId, String orgId ) {
        LOG.info( "Refusing user participation in community " + communityUri );
        ChannelsUser user = ChannelsUser.current( userRecordService );
        try {
            CollaborationModel oldCollaborationModel = user.getCollaborationModel();
            String oldPlanCommunityUri = user.getPlanCommunityUri();
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            Actor actor = communityService.getModelService().find( Actor.class, Long.parseLong( actorId ) );
            RegisteredOrganization registeredOrganization = registeredOrganizationService.load( orgId );
            if ( actor == null || registeredOrganization == null )
                throw new IllegalArgumentException();
            Agent agent = new Agent( actor, registeredOrganization, communityService );
            UserParticipation userParticipation = participationManager.getParticipation(
                    user,
                    agent,
                    communityService
            );
            user.setCollaborationModel( oldCollaborationModel );
            user.setPlanCommunityUri( oldPlanCommunityUri );
            if ( userParticipation != null && !userParticipation.isLinked() ) {
                userParticipationService.refuse( userParticipation, communityService );
            } else {
                throw new Exception( "Participation was not refused" );
            }
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for community " + communityUri )
                            .build() );
        }
    }

///////////////

    private PlanCommunity authorizeParticipant( ChannelsUser user, String communityUri ) throws Exception {
        PlanCommunity planCommunity = planCommunityManager.getPlanCommunity( communityUri ); // if domain plan community, development plan community implied
        if ( user == null || planCommunity == null )
            throw new Exception( "No such community " + communityUri );
        CommunityService communityService = getCommunityService( planCommunity );
        CollaborationModel collaborationModel = communityService.getPlan();
        if ( collaborationModel == null || user.getRole( communityUri ).equals( ChannelsUser.UNAUTHORIZED ) ) {
            throw new Exception( user.getUsername() + " is not authorized to access community " + communityUri );
        }
        user.setCommunityService( communityService );
        return planCommunity;
    }

    private PlanCommunity authorizeCommunityLeader( ChannelsUser user, String communityUri ) throws Exception {
         PlanCommunity planCommunity = planCommunityManager.getPlanCommunity( communityUri ); // if domain plan community, development plan community implied
        if ( planCommunity == null )
            throw new Exception( "No such community " + communityUri );
        CommunityService communityService = getCommunityService( planCommunity );
        if ( !communityService.isCommunityPlanner( user ) ) { //
            throw new Exception( user.getUsername() + " is not authorized to access community " + communityUri );
        }
        user.setCommunityService( communityService );
        return planCommunity;
    }

    // Only planners can request access to data about a development version of a plan community.
    private PlanCommunity authorizeAccessToPlan( ChannelsUser user, String uri, String version ) throws Exception {
        return authorizeAccessToModel( user, uri, version, false );
    }

    // Only planners can request access to data about a development version of a plan community.
    private PlanCommunity authorizePlanner( ChannelsUser user, String uri, String version ) throws Exception {
        return authorizeAccessToModel( user, uri, version, true );
    }

    // Only planners can request access to data about a development version of a plan community.
    private PlanCommunity authorizeAccessToModel( ChannelsUser user, String uri, String version, boolean developerOnly ) throws Exception {
        // domain plan community
        PlanCommunity planCommunity = planCommunityManager.findPlanCommunity( uri, Integer.parseInt( version ) );
        if ( planCommunity == null || !planCommunity.isModelCommunity() )
            throw new Exception( "No such collaboration model " + uri );
        CommunityService communityService = getCommunityService( planCommunity );
        CollaborationModel collaborationModel = communityService.getPlan();
        if ( user == null
                || collaborationModel == null
                || ( developerOnly && !user.isDeveloperOrAdmin( uri ) )
                || ( collaborationModel.isDevelopment() && !user.isDeveloperOrAdmin( uri ) ) )
            throw new Exception( "Unauthorized access to collaboration model " + uri + " and version " + version );
        user.setCommunityService( communityService );
        return planCommunity;
    }


    private String makeInvitation( UserRecord invitedUser, CommunityService communityService ) {
        StringBuilder sb = new StringBuilder();
        PlanCommunity planCommunity = communityService.getPlanCommunity();
        CollaborationModel collaborationModel = communityService.getPlan();
        String homePageUrl = getServerUrl()
                + "home?"
                + AbstractChannelsWebPage.MODEL_PARM
                + "="
                + planCommunity.getUri();
        sb.append( "\n\n------------------- \n\n" )
                .append( "To participate in the community " )
                .append( planCommunity.getName() )
                .append( " designed by " )
                .append( collaborationModel.getClient() )
                .append( ",\ngo to " )
                .append( homePageUrl )
                .append( "\nand login with your email address " )
                .append( invitedUser.getEmail() )
                .append( " as user name" );
        String newPassword = invitedUser.getGeneratedPassword();
        if ( newPassword != null ) {
            sb.append( "\nand use this password " )
                    .append( newPassword )
                    .append( " (You can change password once logged in.)" );
        } else {
            sb.append( "." );
        }
        return sb.toString();
    }

    //////////////////////////////////////////////////////////

    @WebMethod( exclude = true )
    public void setServerUrl( String serverUrl ) {
        this.serverUrl = serverUrl;
    }

    @WebMethod( exclude = true )
    public String getServerUrl() {
        return serverUrl
                + ( serverUrl.endsWith( "/" ) ? "" : "/" );
    }


    private CommunityService getCommunityService( PlanCommunity planCommunity ) {
        return communityServiceFactory.getService( planCommunity );
    }

    private CommunityService getCommunityService( CollaborationModel collaborationModel ) {
        return communityServiceFactory.getService( planCommunityManager.getDomainPlanCommunity( collaborationModel ) );
    }

    private CommunityService getCommunityService( String uri ) {
        PlanCommunity planCommunity = planCommunityManager.getPlanCommunity( uri );
        return getCommunityService( planCommunity );
    }

    /// OBSOLETE

}
