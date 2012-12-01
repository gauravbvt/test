package com.mindalliance.channels.api;

import com.mindalliance.channels.api.directory.DirectoryData;
import com.mindalliance.channels.api.issues.IssuesData;
import com.mindalliance.channels.api.plan.PlanReleaseData;
import com.mindalliance.channels.api.plan.PlanScopeData;
import com.mindalliance.channels.api.plan.PlanSummariesData;
import com.mindalliance.channels.api.plan.PlanSummaryData;
import com.mindalliance.channels.api.procedures.ProceduresData;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.community.participation.PlanParticipation;
import com.mindalliance.channels.core.community.participation.PlanParticipationService;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.dao.user.UserContactInfoService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
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
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/11
 * Time: 11:34 AM
 */
@Path( "/isp" )
@WebService(
        endpointInterface = "com.mindalliance.channels.api.ChannelsService"
)
public class ChannelsServiceImpl implements ChannelsService {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ChannelsServiceImpl.class );

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
    private PlanParticipationService planParticipationService;
    @Autowired
    private EmailMessagingService emailMessagingService;
    @Autowired
    private UserContactInfoService userContactInfoService;
    @Autowired
    private PlanCommunityManager planCommunityManager;

    private String serverUrl;


    @Override
    /**
     * Get summaries of all versions of all plans visible to the user.
     * @return plan summaries
     */

    public PlanSummariesData getPlans() {
        LOG.info( "Getting summaries for all visible plans" );
        ChannelsUser user = ChannelsUser.current( userDao );
        List<PlanSummaryData> result = new ArrayList<PlanSummaryData>();
        for ( Plan plan : planManager.getPlans() ) {
            String uri = plan.getUri();
            if ( !user.getRole( uri ).equals( ChannelsUser.UNAUTHORIZED )
                    && ( user.isPlanner( uri ) || plan.isProduction() ) ) {
                user.setPlan( plan );
                result.add( new PlanSummaryData(
                        serverUrl,
                        planCommunityManager.getPlanCommunity( plan ) ) );
            }
        }
        return new PlanSummariesData( result );
    }

    @Override
    public PlanSummaryData getPlan( String uri, String version ) {
        LOG.info( "Getting summary for plan " + uri + " version " + version );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            Plan plan = authorize( user, uri, version );
            return new PlanSummaryData(
                    serverUrl,
                    planCommunityManager.getPlanCommunity( plan ) );
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
        LOG.info( "Getting release info for production version of plan " + uri );
        try {
            ChannelsUser user = ChannelsUser.current( userDao );
            Plan plan = authorizeParticipant( user, uri );
            return new PlanReleaseData( plan );
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No release data available for production plan " + uri )
                            .build() );
        }
    }

    @Override
    /**
     * Get summaries of all versions of all plans visible to the user.
     * @return plan summaries
     */

    public PlanSummariesData getProductionPlans() {
        LOG.info( "Getting summaries for all user-visible production plans" );
        ChannelsUser user = ChannelsUser.current( userDao );
        List<PlanSummaryData> result = new ArrayList<PlanSummaryData>();
        for ( Plan plan : planManager.getPlans() ) {
            String uri = plan.getUri();
            if ( !user.getRole( uri ).equals( ChannelsUser.UNAUTHORIZED )
                    && plan.isProduction() ) {
                user.setPlan( plan );
                result.add( new PlanSummaryData(
                        serverUrl,
                        planCommunityManager.getPlanCommunity( plan ) ) );
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
            Plan plan = authorize( user, uri, version );
            return new PlanScopeData(
                    serverUrl,
                    planCommunityManager.getPlanCommunity( plan ) );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() )
                            .build() );
        }
    }

    @Override
    public ProceduresData getProcedures( String uri, String actorId ) {
        LOG.info( "Getting user procedures of agent " + actorId + " for production version of plan " + uri );
        try {
            ChannelsUser user = ChannelsUser.current( userDao );
            Plan plan = authorizeParticipant( user, uri );
            PlanService planService = getPlanService(  plan );
            Actor actor = getPlanService( plan ).find( Actor.class, Long.parseLong( actorId ) );
            if ( !canSeeProcedures( user, actor, planService ) ) {
                throw new Exception( "Procedures not visible to " + user.getUsername() + " for plan with uri " + uri );
            }
            return new ProceduresData(
                    serverUrl,
                    planCommunityManager.getPlanCommunity( plan ),
                    actor );
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
        LOG.info( "Getting user procedures for production version of plan " + uri );
        try {
            ChannelsUser user = ChannelsUser.current( userDao );
            Plan plan = authorizeParticipant( user, uri );
            List<PlanParticipation> participations = planParticipationService.getActiveUserParticipations(
                    user.getUserInfo(),
                    getPlanCommunity(  plan ) );
            return new ProceduresData(
                    serverUrl,
                    planCommunityManager.getPlanCommunity( plan ),
                    participations,
                    user );
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No procedures available for production plan " + uri )
                            .build() );
        }
    }

    @Override
    public ProceduresData getUserProcedures( String uri, String version, String username ) {
        ChannelsUser user = ChannelsUser.current( userDao );
        LOG.info( "Getting " + username + "'s procedures for plan " + uri + " version " + version );
        try {
            ChannelsUser protocolsUser = userDao.getUserNamed( username );
            Plan plan = authorize( user, uri, version );
            List<PlanParticipation> participationList = planParticipationService.getActiveUserParticipations(
                    protocolsUser.getUserInfo(),
                    getPlanCommunity(  plan )
            );
            if ( participationList.isEmpty() ) {
                throw new Exception( username + " does not participate in plan " + uri + " version " + version );
            }
            return new ProceduresData(
                    serverUrl,
                    planCommunityManager.getPlanCommunity( plan ),
                    participationList,
                    protocolsUser );
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No procedures available for plan " + uri )
                            .build() );
        }
    }

    @Override
    public ProceduresData getAgentProcedures( String uri, String version, String actorId ) {
        ChannelsUser user = ChannelsUser.current( userDao );
        LOG.info( "Getting procedures of agent " + actorId + " for plan " + uri + " version " + version );
        try {
            Plan plan = authorize( user, uri, version );
            Actor actor = getPlanService( plan ).find( Actor.class, Long.parseLong( actorId ) );
            return new ProceduresData(
                    serverUrl,
                    planCommunityManager.getPlanCommunity( plan ),
                    actor );
        } catch ( Exception e ) {
            LOG.warn( "No procedures available for agent " + actorId, e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No procedures available for plan " + uri )
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
                            .entity( "No procedures available for plan " + uri )
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
                            .entity( "No procedures available for plan " + uri )
                            .build() );
        }
    }

    @Override
    public DirectoryData getMyDirectory( String uri ) {
        LOG.info( "Getting user directory for production version of plan " + uri );
        try {
            ChannelsUser user = ChannelsUser.current( userDao );
            Plan plan = authorizeParticipant( user, uri );
            List<PlanParticipation> participations = planParticipationService.getActiveUserParticipations(
                    user.getUserInfo(),
                    getPlanCommunity(  plan ) );
            if ( participations.isEmpty() ) {
                throw new Exception( user.getUsername() + " does not participate in production plan " + uri );
            }
            ProceduresData proceduresData = getMyProcedures( uri );
            return new DirectoryData( proceduresData );
        } catch ( Exception e ) {
            LOG.warn( e.getMessage(), e );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for plan " + uri )
                            .build() );
        }
    }

    private Plan authorizeParticipant( ChannelsUser user, String uri ) throws Exception {
        Plan plan = planManager.findProductionPlan( uri );
        if ( plan == null || user.getRole( uri ).equals( ChannelsUser.UNAUTHORIZED ) ) {
            throw new Exception( user.getUsername() + " is not authorized to access production plan " + uri );
        }
        user.setPlan( plan );
        return plan;
    }

    // Only planners can request access to a specific version of a plan.
    private Plan authorize( ChannelsUser user, String uri, String version ) throws Exception {
        Plan plan = planManager.getPlan( uri, Integer.parseInt( version ) );
        if ( user == null
                || plan == null
                || ( plan.isDevelopment() && !user.isPlanner( uri ) )
                || ( plan.isProduction() && !user.isParticipant( uri ) ) )
            throw new Exception( "Unauthorized access to plan " + uri + " version " + version );
        return plan;
    }

    @Override
    public IssuesData getIssues( String uri, String version ) {
        LOG.info( "Getting issues in plan " + uri + " version " + version );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            Plan plan = authorize( user, uri, version );
            return new IssuesData( serverUrl, planCommunityManager.getPlanCommunity( plan ) );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for plan " + uri )
                            .build() );
        }
    }

    private boolean canSeeProcedures( ChannelsUser user, Actor actor, PlanService planService ) {
        // Planner can see any actor's procedures
        Plan plan = planService.getPlan();
        if ( plan.isTemplate() || user.isPlanner( plan.getUri() ) )
            return true;
        // Participating user can see own procedures. Supervisor can procedures of supervised.
        List<PlanParticipation> participations = planParticipationService.getActiveUserParticipations(
                user.getUserInfo(),
                getPlanCommunity(  plan ) );
        for ( PlanParticipation participation : participations ) {
            Actor participant = participation.getActor( planService );
            if ( participant != null
                    && ( participant.equals( actor )
                    || planService.findSupervised( participant ).contains( actor ) ) )
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
        LOG.info( "Receiving feedback for protocols from production plan " + uri );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            Plan plan = authorizeParticipant( user, uri );
            feedbackService.sendFeedback(
                    user.getUsername(),
                    plan,
                    Feedback.Type.valueOf( type ),
                    Feedback.PROTOCOLS,
                    feedback,
                    Boolean.parseBoolean( urgent ) );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for plan " + uri )
                            .build() );
        }
    }

    @Override
    public void acceptParticipation( String uri, String agentId ) {
        LOG.info( "Adding user participation in production plan " + uri );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            Plan plan = authorizeParticipant( user, uri );
            Actor actor = getPlanService( plan ).find( Actor.class, Long.parseLong( agentId ) );
            if ( planParticipationService.isParticipationOpenAndAvailable( actor, user, getPlanCommunity(  plan ) ) ) {
                PlanParticipation participation = new PlanParticipation( user.getUsername(),
                        plan,
                        user,
                        actor );
                planParticipationService.accept( participation );
            } else {
                throw new Exception( "Participation was not accepted" );
            }
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for plan " + uri )
                            .build() );
        }
    }

    @Override
    public void refuseParticipation( String uri, String agentId ) {
        LOG.info( "Refusing user participation in production plan " + uri );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            Plan plan = authorizeParticipant( user, uri );
            Actor actor = getPlanService( plan ).find( Actor.class, Long.parseLong( agentId ) );
            PlanParticipation planParticipation = planParticipationService.getParticipation(
                    user.getUserInfo(),
                    actor,
                    getPlanCommunity(  plan )
            );
            if ( planParticipation != null ) {
                planParticipationService.refuse( planParticipation );
            } else {
                throw new Exception( "Participation was not refused" );
            }
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for plan " + uri )
                            .build() );
        }
    }

    @Override
    public void invite( String uri, String email, String message ) {
        LOG.info( "Inviting user to participate in plan " + uri );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            Plan plan = authorizeParticipant( user, uri );
            ChannelsUserInfo invitedUser = userDao.getOrMakeUserFromEmail( email, getPlanService( plan ) );
            message = message + makeInvitation( invitedUser, plan );
            emailMessagingService.sendInvitation( user, invitedUser.getEmail(), message );

        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() + " for plan " + uri )
                            .build() );
        }
    }

    @Override
    public void addContactInfo( String uri, String mediumId, String address ) {
        LOG.info( "Adding user contact info " );
        ChannelsUser user = ChannelsUser.current( userDao );
        try {
            Plan plan = authorizeParticipant( user, uri );
            TransmissionMedium medium = getPlanService( plan ).find( TransmissionMedium.class, Long.parseLong( mediumId ) );
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
            Plan plan = authorizeParticipant( user, uri );
            TransmissionMedium medium = getPlanService( plan ).find( TransmissionMedium.class, Long.parseLong( mediumId ) );
            userContactInfoService.removeChannel( user.getUserInfo(), new Channel( medium, address ) );
        } catch ( Exception e ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( e.getMessage() )
                            .build() );
        }
    }

    private String makeInvitation( ChannelsUserInfo invitedUser, Plan plan ) {
        StringBuilder sb = new StringBuilder();
        String homePageUrl = getServerUrl()
                + "home?"
                + AbstractChannelsWebPage.PLAN_PARM
                + "="
                + plan.getUri();
        sb.append( "\n\n------------------- \n\n" )
                .append( "To participate in the collaboration plan " )
                .append( plan.getName() )
                .append( " of " )
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

    @WebMethod( exclude = true )
    public void setServerUrl( String serverUrl ) {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return serverUrl
                + ( serverUrl.endsWith( "/" ) ? "" : "/" );
    }

    private PlanService getPlanService( Plan plan ) {
        return planServiceFactory.getService( plan );
    }

    private PlanCommunity getPlanCommunity( Plan plan ) {
        return planCommunityManager.getPlanCommunity( plan );
    }

}
