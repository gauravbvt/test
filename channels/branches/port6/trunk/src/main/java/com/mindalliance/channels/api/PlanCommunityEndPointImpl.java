package com.mindalliance.channels.api;

import com.mindalliance.channels.api.community.CommunitySummariesData;
import com.mindalliance.channels.api.community.CommunitySummaryData;
import com.mindalliance.channels.api.directory.DirectoryData;
import com.mindalliance.channels.api.issues.IssuesData;
import com.mindalliance.channels.api.plan.PlanReleaseData;
import com.mindalliance.channels.api.plan.PlanScopeData;
import com.mindalliance.channels.api.plan.PlanSummariesData;
import com.mindalliance.channels.api.plan.PlanSummaryData;
import com.mindalliance.channels.api.procedures.AllProtocolsData;
import com.mindalliance.channels.api.procedures.ProtocolsData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.OrganizationParticipation;
import com.mindalliance.channels.core.community.participation.OrganizationParticipationService;
import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.dao.user.UserContactInfoService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.services.FeedbackService;
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
    private PlanServiceFactory planServiceFactory;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private PlanManager planManager;
    @Autowired
    private ChannelsUserDao userDao;
    @Autowired
    private Analyst analyst;
    @Autowired
    private UserParticipationService userParticipationService;
    @Autowired
    private EmailMessagingService emailMessagingService;
    @Autowired
    private UserContactInfoService userContactInfoService;
    @Autowired
    private PlanCommunityManager planCommunityManager;
    @Autowired
    private ParticipationManager participationManager;
    @Autowired
    private OrganizationParticipationService organizationParticipationService;

    private String serverUrl;

    @Autowired
    private CommunityServiceFactory communityServiceFactory;

    /// PLANS


    @Override
    /**
     * Get summaries of all versions of all plans visible to the user.
     * @return plan summaries
     */

    public PlanSummariesData getPlans() {
        LOG.info( "Getting summaries for all visible communities" );
        ChannelsUser user = ChannelsUser.current( userDao );
        List<PlanSummaryData> result = new ArrayList<PlanSummaryData>();
        for ( Plan plan : planManager.getPlans() ) {
            String uri = plan.getUri();
            if ( !user.getRole( uri ).equals( ChannelsUser.UNAUTHORIZED )
                    && ( user.isPlanner( uri ) || plan.isProduction() ) ) {
                user.setPlan( plan );
                result.add( new PlanSummaryData(
                        serverUrl,
                        getCommunityService( plan ) ) );
            }
        }
        return new PlanSummariesData( result );
    }

    @Override
    public PlanSummaryData getPlan( String uri, String version ) {
        LOG.info( "Getting summary for community " + uri + " and plan version " + version );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorize( user, uri, version );
            CommunityService communityService = getCommunityService( planCommunity );
            return new PlanSummaryData(
                    serverUrl,
                    communityService );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() )
                            .build() );
        }

    }

    @Override
    public PlanReleaseData getPlanRelease( String uri ) {
        LOG.info( "Getting release info for community " + uri );
        try {
            ChannelsUser user = ChannelsUser.current( userDao );
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            CommunityService communityService = getCommunityService( planCommunity );
            return new PlanReleaseData( communityService );
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

    public PlanSummariesData getProductionPlans() {
        LOG.info( "Getting summaries for all user-visible communities" );
        ChannelsUser user = ChannelsUser.current( userDao );
        List<PlanSummaryData> result = new ArrayList<PlanSummaryData>();
        for ( Plan plan : planManager.getPlans() ) {
            String uri = plan.getUri();
            if ( !user.getRole( uri ).equals( ChannelsUser.UNAUTHORIZED )
                    && plan.isProduction() ) {
                user.setPlan( plan );
                result.add( new PlanSummaryData(
                        serverUrl,
                        getCommunityService( plan ) ) );
            }
        }
        return new PlanSummariesData( result );
    }


    @Override
    /**
     * Get scope of production plan.
     * Available only to its planners.
     * @param uri the plan's URN
     * @param version a plan version
     * @return a plan's scope
     */
    public PlanScopeData getPlanScope( String uri, String version ) {
        LOG.info( "Getting scope for plan " + uri + " version " + version );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorize( user, uri, version );
            return new PlanScopeData(
                    serverUrl,
                    getCommunityService( planCommunity ) );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() )
                            .build() );
        }
    }

    @Override
    public IssuesData getIssues( String uri, String version ) {
        LOG.info( "Getting issues in plan " + uri + " version " + version );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorize( user, uri, version );
            CommunityService communityService = getCommunityService( planCommunity );
            return new IssuesData( serverUrl, communityService );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for plan community" + uri )
                            .build() );
        }
    }

    public void addFeedback(
            String uri,
            String type,
            String feedback,
            String urgent ) {
        LOG.info( "Receiving feedback for protocols from community " + uri );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            feedbackService.sendFeedback(
                    user.getUsername(),
                    getCommunityService( planCommunity.getPlanUri() ),
                    Feedback.Type.valueOf( type ),
                    Feedback.PROTOCOLS,
                    feedback,
                    Boolean.parseBoolean( urgent ) );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for community " + uri )
                            .build() );
        }
    }

    @Override
    public void invite( String uri, String email, String message ) {
        LOG.info( "Inviting user to participate in community " + uri );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            CommunityService communityService = getCommunityService( planCommunity );
            ChannelsUserInfo invitedUser = userDao.getOrMakeUserFromEmail( email, communityService.getPlanService() );
            message = message + makeInvitation( invitedUser, communityService );
            emailMessagingService.sendInvitation( user, invitedUser.getEmail(), message );

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
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            List<CommunitySummaryData> summaries = new ArrayList<CommunitySummaryData>();
            for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
                if ( !planCommunity.isDomainCommunity() ) {
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
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorizeCommunityLeader( user, communityUri );
            if ( !planCommunity.isDomainCommunity() ) {
                CommunityService communityService = communityServiceFactory.getService( planCommunity );
                return new CommunitySummaryData( serverUrl, communityService );
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
    public AllProtocolsData getAllProtocols( String communityUri ) {
        ChannelsUser user = ChannelsUser.current( userDao );
        LOG.info( "Getting all user protocols for all participants in community " + communityUri );
        try {
            PlanCommunity planCommunity = authorizeCommunityLeader( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            AllProtocolsData allProtocolsData = new AllProtocolsData();
            for ( ChannelsUser channelsUser : userDao.getUsers( communityUri ) ) {
                channelsUser.setCommunityService( communityService );
                List<UserParticipation> participationList = userParticipationService.getActiveUserParticipations(
                        channelsUser,
                        communityService
                );
                if ( !participationList.isEmpty() ) {
                    ProtocolsData protocolsData = getUserProtocols( communityUri, channelsUser.getUsername() );
                    allProtocolsData.addProtocolsData( protocolsData );
                }
            }
            return allProtocolsData;
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No protocols available for community " + communityUri )
                            .build() );
        }
    }


    @Override
    public ProtocolsData getMyProtocols( String communityUri ) {
        LOG.info( "Getting user protocols for community " + communityUri );
        try {
            ChannelsUser user = ChannelsUser.current( userDao );
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            List<UserParticipation> participations = userParticipationService.getActiveUserParticipations(
                    user,
                    communityService );
            return new ProtocolsData(
                    serverUrl,
                    communityService,
                    participations,
                    user );
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No protocols available for community " + communityUri )
                            .build() );
        }
    }

    @Override
    public ProtocolsData getUserProtocols( String communityUri,
                                           String username ) {
        LOG.info( "Getting user protocols for community " + communityUri );
        try {
            ChannelsUser user = ChannelsUser.current( userDao );
            PlanCommunity planCommunity = authorizeCommunityLeader( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            List<UserParticipation> participations = userParticipationService.getActiveUserParticipations(
                    user,
                    communityService );
            return new ProtocolsData(
                    serverUrl,
                    communityService,
                    participations,
                    user );
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No protocols available for community " + communityUri )
                            .build() );
        }
    }

    @Override
    public ProtocolsData getAgentProtocols( String communityUri,
                                            String agentId,
                                            String orgParticipationId ) {
        ChannelsUser user = ChannelsUser.current( userDao );
        LOG.info( "Getting protocols of agent " + agentId
                + " for organization participation" + orgParticipationId
                + " in community " + communityUri );
        try {
            PlanCommunity planCommunity = authorizeCommunityLeader( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            Actor actor = communityService.getPlanService().find( Actor.class, Long.parseLong( agentId ) );
            OrganizationParticipation organizationParticipation =
                    organizationParticipationService.load( Long.parseLong( orgParticipationId ) );
            if ( organizationParticipation == null ) throw new NotFoundException();
            return new ProtocolsData(
                    serverUrl,
                    communityService,
                    new Agent( actor, organizationParticipation, communityService ),
                    user );
        } catch ( Exception e ) {
            LOG.warn( "No protocols available for agent " + agentId, e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No protocols available for community " + communityUri )
                            .build() );
        }
    }

    @Override
    public DirectoryData getUserDirectory(
            String communityUri,
            String username ) {
        try {
            ProtocolsData protocolsData = getUserProtocols( communityUri, username );
            return new DirectoryData( protocolsData );
        } catch ( Exception e ) {
            LOG.warn( "Failed to retrieve directory", e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No procedures available for community " + communityUri )
                            .build() );
        }
    }

    @Override
    public DirectoryData getMyDirectory( String communityUri ) {
        LOG.info( "Getting user directory for production version of plan " + communityUri );
        try {
            ChannelsUser user = ChannelsUser.current( userDao );
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            List<UserParticipation> participations = userParticipationService.getActiveUserParticipations(
                    user,
                    communityService );
            if ( participations.isEmpty() ) {
                throw new Exception( user.getUsername() + " does not participate in community " + communityUri );
            }
            ProtocolsData protocolsData = getMyProtocols( communityUri );
            return new DirectoryData( protocolsData );
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
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            TransmissionMedium medium = communityService.getPlanService().find( TransmissionMedium.class, Long.parseLong( mediumId ) );
            if ( !medium.isAddressValid( address ) ) throw new Exception( "Invalid address" );
            userContactInfoService.addChannel(
                    user.getUsername(),
                    user.getUserInfo(),
                    new Channel( medium, address ) );
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
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            TransmissionMedium medium = communityService.getPlanService().find( TransmissionMedium.class, Long.parseLong( mediumId ) );
            userContactInfoService.removeChannel( user.getUserInfo(), new Channel( medium, address ) );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() )
                            .build() );
        }
    }

    @Override
    public void acceptParticipation( String communityUri, String agentId ) {
        LOG.info( "Adding user participation in community " + communityUri );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            Actor actor = getCommunityService( planCommunity ).getPlanService().find( Actor.class, Long.parseLong( agentId ) ); // todo = agents
            if ( participationManager.isParticipationSelfAssignable( new Agent( actor ), user, communityService ) ) {
                UserParticipation participation = new UserParticipation(
                        user.getUsername(),
                        user,
                        new Agent( actor ),
                        planCommunity );
                userParticipationService.accept( participation, communityService );
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
    public void refuseParticipation( String communityUri, String agentId ) {
        LOG.info( "Refusing user participation in community " + communityUri );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorizeParticipant( user, communityUri );
            CommunityService communityService = getCommunityService( planCommunity );
            Actor actor = communityService.getPlanService().find( Actor.class, Long.parseLong( agentId ) ); // todo - COMMUNITY - agents!
            UserParticipation userParticipation = userParticipationService.getParticipation(
                    user,
                    new Agent( actor ),
                    communityService
            );
            if ( userParticipation != null ) {
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
        if ( planCommunity == null )
            throw new Exception( "No such community " + communityUri );
        CommunityService communityService = getCommunityService( planCommunity );
        Plan plan = communityService.getPlan();
        if ( plan == null || user.getRole( communityUri ).equals( ChannelsUser.UNAUTHORIZED ) ) {
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


    // Only planners can request access to a specific version of a plan.
    private PlanCommunity authorize( ChannelsUser user, String uri, String version ) throws Exception {
        // domain plan community
        PlanCommunity planCommunity = planCommunityManager.findPlanCommunity( uri, Integer.parseInt( version ) );
        if ( planCommunity == null || !planCommunity.isDomainCommunity() )
            throw new Exception( "No such plan " + uri );
        CommunityService communityService = getCommunityService( planCommunity );
        Plan plan = communityService.getPlan();
        if ( user == null
                || plan == null
                || ( plan.isDevelopment() && !user.isPlanner( uri ) )
                || ( plan.isProduction() && !user.isParticipant( uri ) ) )
            throw new Exception( "Unauthorized access to plan community " + uri + " and plan version " + version );
        user.setCommunityService( communityService );
        return planCommunity;
    }

    private String makeInvitation( ChannelsUserInfo invitedUser, CommunityService communityService ) {
        StringBuilder sb = new StringBuilder();
        PlanCommunity planCommunity = communityService.getPlanCommunity();
        Plan plan = communityService.getPlan();
        String homePageUrl = getServerUrl()
                + "home?"
                + AbstractChannelsWebPage.PLAN_PARM
                + "="
                + planCommunity.getUri();
        sb.append( "\n\n------------------- \n\n" )
                .append( "To participate in the plan community " )
                .append( planCommunity.getName() )
                .append( " designed by " )
                .append( plan.getClient() )
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

    private CommunityService getCommunityService( Plan plan ) {
        return communityServiceFactory.getService( planCommunityManager.getDomainPlanCommunity( plan ) );
    }

    private CommunityService getCommunityService( String uri ) {
        PlanCommunity planCommunity = planCommunityManager.getPlanCommunity( uri );
        return getCommunityService( planCommunity );
    }

    /// OBSOLETE

}
