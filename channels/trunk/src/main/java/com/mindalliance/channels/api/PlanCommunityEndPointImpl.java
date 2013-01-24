package com.mindalliance.channels.api;

import com.mindalliance.channels.api.directory.DirectoryData;
import com.mindalliance.channels.api.issues.IssuesData;
import com.mindalliance.channels.api.plan.PlanReleaseData;
import com.mindalliance.channels.api.plan.PlanScopeData;
import com.mindalliance.channels.api.plan.PlanSummariesData;
import com.mindalliance.channels.api.plan.PlanSummaryData;
import com.mindalliance.channels.api.procedures.AllProceduresData;
import com.mindalliance.channels.api.procedures.ProceduresData;
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
import com.mindalliance.channels.core.query.PlanService;
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
@Path("/isp")
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
                        planCommunityManager.getPlanCommunityFor( plan ) ) );
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
            return new PlanSummaryData(
                    serverUrl,
                    planCommunity );
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
            return new PlanReleaseData( planCommunity );
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
                        planCommunityManager.getPlanCommunityFor( plan ) ) );
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
                    planCommunity );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() )
                            .build() );
        }
    }

    @Override
    public ProceduresData getActorProcedures( String uri, String actorId ) {    // todo - obsolete?
        LOG.info( "Getting user procedures of actor " + actorId + " for community " + uri );
        try {
            ChannelsUser user = ChannelsUser.current( userDao );
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            PlanService planService = getPlanService( planCommunity.getPlan() );
            Actor actor = planService.find( Actor.class, Long.parseLong( actorId ) );
            if ( !canSeeProcedures( user, actor, planCommunity ) ) {
                throw new Exception( "Procedures not visible to " + user.getUsername() + " for community with uri " + uri );
            }
            return new ProceduresData(
                    serverUrl,
                    planCommunity,
                    new Agent( actor ) );
        } catch ( Exception e ) {
            LOG.warn( "No procedures available for agent " + actorId, e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No procedures available for agent " + actorId )
                            .build() );
        }
    }

    @Override
    public ProceduresData getMyProcedures( String uri ) {
        LOG.info( "Getting user procedures for community " + uri );
        try {
            ChannelsUser user = ChannelsUser.current( userDao );
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            List<UserParticipation> participations = userParticipationService.getActiveUserParticipations(
                    user,
                    planCommunity );
            return new ProceduresData(
                    serverUrl,
                    planCommunity,
                    participations,
                    user );
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No procedures available for community " + uri )
                            .build() );
        }
    }

    @Override
    public ProceduresData getUserProcedures( String uri, String version, String username ) {
        ChannelsUser user = ChannelsUser.current( userDao );
        LOG.info( "Getting " + username + "'s procedures for community " + uri + " version " + version );
        try {
            ChannelsUser protocolsUser = userDao.getUserNamed( username );
            PlanCommunity planCommunity = authorize( user, uri, version );
            protocolsUser.setPlanCommunity( planCommunity );
            List<UserParticipation> participationList = userParticipationService.getActiveUserParticipations(
                    protocolsUser,
                    planCommunity
            );
            if ( participationList.isEmpty() ) {
                throw new Exception( username + " does not participate in community " + uri + " version " + version );
            }
            return new ProceduresData(
                    serverUrl,
                    planCommunity,
                    participationList,
                    protocolsUser );
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No procedures available for community " + uri )
                            .build() );
        }
    }

    @Override
    public AllProceduresData getAllProcedures( String uri, String version ) {
        ChannelsUser user = ChannelsUser.current( userDao );
        LOG.info( "Getting all user procedures for all participants in community " + uri );
        try {
            PlanCommunity planCommunity = authorize( user, uri, version );
            AllProceduresData allProceduresData = new AllProceduresData();
            for ( ChannelsUser channelsUser : userDao.getUsers( uri ) ) {
                channelsUser.setPlanCommunity( planCommunity );
                List<UserParticipation> participationList = userParticipationService.getActiveUserParticipations(
                        channelsUser,
                        planCommunity
                );
                if ( !participationList.isEmpty() ) {
                    ProceduresData proceduresData = getUserProcedures( uri, version, channelsUser.getUsername() );
                    allProceduresData.addProceduresData( proceduresData );
                }
            }
            return allProceduresData;
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No procedures available for community " + uri )
                            .build() );
        }
    }

    @Override
    public ProceduresData getAgentProcedures( String uri, String version, String actorId ) {
        ChannelsUser user = ChannelsUser.current( userDao );
        LOG.info( "Getting procedures of agent " + actorId + " for community " + uri + " and plan version " + version );
        try {
            PlanCommunity planCommunity = authorize( user, uri, version );
            Actor actor = getPlanService( planCommunity.getPlan() ).find( Actor.class, Long.parseLong( actorId ) );
            return new ProceduresData(
                    serverUrl,
                    planCommunity,
                    new Agent( actor ) );

        } catch ( Exception e ) {
            LOG.warn( "No procedures available for agent " + actorId, e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No procedures available for community " + uri )
                            .build() );
        }
    }

    @Override
    public ProceduresData getAgentProtocols( String uri,
                                             String version,
                                             String agentId,
                                             String orgParticipationId ) {
        ChannelsUser user = ChannelsUser.current( userDao );
        LOG.info( "Getting protocols of agent " + agentId
                + " for organization participation" + orgParticipationId
                + " in community " + uri
                + " and plan version " + version );
        try {
            PlanCommunity planCommunity = authorize( user, uri, version );
            Actor actor = getPlanService( planCommunity.getPlan() ).find( Actor.class, Long.parseLong( agentId ) );
            OrganizationParticipation organizationParticipation =
                    organizationParticipationService.load( Long.parseLong( orgParticipationId ) );
            if ( organizationParticipation == null ) throw new NotFoundException();
            return new ProceduresData(
                    serverUrl,
                    planCommunity,
                    new Agent( actor, organizationParticipation, planCommunity ) );
        } catch ( Exception e ) {
            LOG.warn( "No protocols available for agent " + agentId, e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No protocols available for community " + uri )
                            .build() );
        }
    }

    @Override
    public DirectoryData getUserDirectory(
            String uri,
            String version,
            String username ) {
        try {
            ProceduresData proceduresData = getUserProcedures( uri, version, username );
            return new DirectoryData( proceduresData );
        } catch ( Exception e ) {
            LOG.warn( "Failed to retrieve directory", e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No procedures available for community " + uri )
                            .build() );
        }
    }

    @Override
    public DirectoryData getAgentDirectory(
            String uri,
            String version,
            String agentId ) {
        try {
            ProceduresData proceduresData = getAgentProcedures( uri, version, agentId );
            return new DirectoryData( proceduresData );
        } catch ( Exception e ) {
            LOG.warn( "Failed to retrieve directory", e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No procedures available for community " + uri )
                            .build() );
        }
    }

    @Override
    public DirectoryData getMyDirectory( String uri ) {
        LOG.info( "Getting user directory for production version of plan " + uri );
        try {
            ChannelsUser user = ChannelsUser.current( userDao );
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            List<UserParticipation> participations = userParticipationService.getActiveUserParticipations(
                    user,
                    planCommunity );
            if ( participations.isEmpty() ) {
                throw new Exception( user.getUsername() + " does not participate in community " + uri );
            }
            ProceduresData proceduresData = getMyProcedures( uri );
            return new DirectoryData( proceduresData );
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for community " + uri )
                            .build() );
        }
    }

    private PlanCommunity authorizeParticipant( ChannelsUser user, String uri ) throws Exception {
        PlanCommunity planCommunity = planCommunityManager.findPlanCommunity( uri );
        Plan plan = planCommunity.getPlan();
        if ( plan == null || user.getRole( uri ).equals( ChannelsUser.UNAUTHORIZED ) ) { // todo - COMMUNITY - authorize for community not plan
            throw new Exception( user.getUsername() + " is not authorized to access community " + uri );
        }
        user.setPlanCommunity( planCommunity );
        return planCommunity;
    }

    // Only planners can request access to a specific version of a plan.
    private PlanCommunity authorize( ChannelsUser user, String uri, String version ) throws Exception {
        PlanCommunity planCommunity = planCommunityManager.findPlanCommunity( uri, Integer.parseInt( version ) );
        Plan plan = planCommunity.getPlan();
        if ( user == null
                || plan == null
                || ( plan.isDevelopment() && !user.isPlanner( uri ) )
                || ( plan.isProduction() && !user.isParticipant( uri ) ) )
            throw new Exception( "Unauthorized access to plan community " + uri + " and plan version " + version );
        user.setPlanCommunity( planCommunity );
        return planCommunity;
    }

    @Override
    public IssuesData getIssues( String uri, String version ) {
        LOG.info( "Getting issues in plan " + uri + " version " + version );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorize( user, uri, version );
            return new IssuesData( serverUrl, planCommunity );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for plan community" + uri )
                            .build() );
        }
    }

    private boolean canSeeProcedures( ChannelsUser user, Actor actor, PlanCommunity planCommunity ) { // todo -agents and agencies
        // Planner can see any actor's procedures
        Plan plan = planCommunity.getPlan();
        if ( plan.isTemplate() || user.isPlanner( plan.getUri() ) )
            return true;
        // Participating user can see own procedures. Supervisor can procedures of supervised.
        List<UserParticipation> participations = userParticipationService.getActiveUserParticipations(
                user,
                getPlanCommunity( plan ) );
        for ( UserParticipation participation : participations ) {
            Actor participant = participation.getAgent( planCommunity ).getActor();   // todo - agents!
            if ( participant != null
                    && ( participant.equals( actor )
                    || planCommunity.getPlanService().findSupervised( participant ).contains( actor ) ) )
                return true;
        }
        return false;
    }

    @Override
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
                    planCommunity,
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
    public void acceptParticipation( String uri, String agentId ) {
        LOG.info( "Adding user participation in community " + uri );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            Actor actor = planCommunity.getPlanService().find( Actor.class, Long.parseLong( agentId ) ); // todo = agents
            if ( participationManager.isParticipationSelfAssignable( new Agent( actor ), user, planCommunity ) ) {
                UserParticipation participation = new UserParticipation(
                        user.getUsername(),
                        user,
                        new Agent( actor ),
                        planCommunity );
                userParticipationService.accept( participation, planCommunity );
            } else {
                throw new Exception( "Participation was not accepted" );
            }
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for community " + uri )
                            .build() );
        }
    }

    @Override
    public void refuseParticipation( String uri, String agentId ) {
        LOG.info( "Refusing user participation in community " + uri );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            Actor actor = planCommunity.getPlanService().find( Actor.class, Long.parseLong( agentId ) ); // todo - agents!
            UserParticipation userParticipation = userParticipationService.getParticipation(
                    user,
                    new Agent( actor ),
                    planCommunity
            );
            if ( userParticipation != null ) {
                userParticipationService.refuse( userParticipation, planCommunity );
            } else {
                throw new Exception( "Participation was not refused" );
            }
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
            ChannelsUserInfo invitedUser = userDao.getOrMakeUserFromEmail( email, planCommunity.getPlanService() );
            message = message + makeInvitation( invitedUser, planCommunity );
            emailMessagingService.sendInvitation( user, invitedUser.getEmail(), message );

        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for community " + uri )
                            .build() );
        }
    }

    @Override
    public void addContactInfo( String uri, String mediumId, String address ) {
        LOG.info( "Adding user contact info " );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            TransmissionMedium medium = planCommunity.getPlanService().find( TransmissionMedium.class, Long.parseLong( mediumId ) );
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
    public void removeContactInfo( String uri, String mediumId, String address ) {
        LOG.info( "Removing user contact info " );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            PlanCommunity planCommunity = authorizeParticipant( user, uri );
            TransmissionMedium medium = planCommunity.getPlanService().find( TransmissionMedium.class, Long.parseLong( mediumId ) );
            userContactInfoService.removeChannel( user.getUserInfo(), new Channel( medium, address ) );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() )
                            .build() );
        }
    }

    private String makeInvitation( ChannelsUserInfo invitedUser, PlanCommunity planCommunity ) {
        StringBuilder sb = new StringBuilder();
        String homePageUrl = getServerUrl()
                + "home?"
                + AbstractChannelsWebPage.PLAN_PARM
                + "="
                + planCommunity.getUri();
        sb.append( "\n\n------------------- \n\n" )
                .append( "To participate in the plan community " )
                .append( planCommunity.getName() )
                .append( " designed by " )
                .append( planCommunity.getPlan().getClient() )
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

    @WebMethod(exclude = true)
    public void setServerUrl( String serverUrl ) {
        this.serverUrl = serverUrl;
    }

    @WebMethod(exclude = true)
    public String getServerUrl() {
        return serverUrl
                + ( serverUrl.endsWith( "/" ) ? "" : "/" );
    }

    private PlanService getPlanService( Plan plan ) {
        return planServiceFactory.getService( plan );
    }

    private PlanCommunity getPlanCommunity( Plan plan ) {
        return planCommunityManager.getPlanCommunityFor( plan );
    }

}
