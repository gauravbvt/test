package com.mindalliance.channels.api;

import com.mindalliance.channels.api.issues.IssuesData;
import com.mindalliance.channels.api.plan.PlanScopeData;
import com.mindalliance.channels.api.plan.PlanSummariesData;
import com.mindalliance.channels.api.plan.PlanSummaryData;
import com.mindalliance.channels.api.procedures.ProceduresData;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.nlp.SemanticMatcher;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    private PlanManager planManager;
    private SemanticMatcher semanticMatcher;
    private UserDao userDao;
    private AttachmentManager attachmentManager;
    private Analyst analyst;

    @Override
    /**
     * Get summaries of all versions of all plans visible to the user.
     * @return plan summaries
     */
    public PlanSummariesData getPlans() {
        LOG.info( "Getting summaries for all visible plans" );
        User user = User.current();
        List<PlanSummaryData> result = new ArrayList<PlanSummaryData>();
        for ( Plan plan : planManager.getPlans() ) {
            String uri = plan.getUri();
            if ( !user.getRole( uri ).equals( User.UNAUTHORIZED )
                    && ( user.isPlanner( uri ) || plan.isProduction() ) ) {
                result.add( new PlanSummaryData( getPlanService( plan ) ) );
            }
        }
        return new PlanSummariesData( result );
    }

    @Override
    /**
     * Get scope of production plan.
     * Available only to its planners.
     * @param uri the plan's URI
     * @param version a plan version
     * @return a plan's scope
     */
    public PlanScopeData getPlanScope( String uri, String version ) {
        LOG.info( "Getting scope for plan " + uri + " version " + version );
        try {
            User user = User.current();
            Plan plan = planManager.getPlan( uri, Integer.parseInt( version ) );
            if ( plan == null || !user.isPlanner( uri ) ) {
                throw new Exception( "Plan " + uri + " is not available" );
            } else {
                return ( new PlanScopeData( plan, getPlanService( plan ) ) );
            }
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
        User user = User.current();
        Plan plan = null;
        try {
            plan = planManager.findProductionPlan( uri );
            if ( plan == null || user.getRole( uri ).equals( User.UNAUTHORIZED ) ) {
                throw new Exception( "No plan available with uri " + uri );
            }
            PlanService planService = getPlanService( plan );
            Actor actor = planService.find( Actor.class, Long.parseLong( actorId ) );
            if ( !canSeeProcedures( user, actor, planService ) ) {
                throw new Exception( "Procedures not visible to " + user.getUsername() + " for plan with uri " + uri );
            }
            return new ProceduresData(
                    plan,
                    actor,
                    planService );
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
            User user = User.current();
            Plan plan = planManager.findProductionPlan( uri );
            if ( plan == null || user.getRole( uri ).equals( User.UNAUTHORIZED ) ) {
                throw new Exception( user.getUsername() + " is not authorized to access production plan " + uri );
            }
            PlanService planService = getPlanService( plan );
            Participation participation = planService.findParticipation( user.getUsername() );
            if ( participation == null || participation.getActor() == null ) {
                throw new Exception( user.getUsername() + " does not participate in production plan " + uri );
            }
            return new ProceduresData(
                    plan,
                    participation.getActor(),
                    planService );
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
    public IssuesData getIssues( String uri, String version ) {
        LOG.info( "Getting issues in plan " + uri + " version " + version );
        User user = User.current();
        Plan plan = null;
        try {
            plan = planManager.getPlan( uri, Integer.parseInt( version ) );
        } catch ( Exception e ) {
            LOG.error( "Plan not found " + uri + " version " + version );
        }
        if ( plan == null || !user.isPlanner( uri ) ) {
            LOG.error( user.getUsername() + " is not authorized to access plan " + uri );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No plan available with uri " + uri )
                            .build() );
        } else {
            PlanService planService = getPlanService( plan );
            return new IssuesData( planService, analyst );
        }
    }

    private boolean canSeeProcedures( User user, Actor actor, PlanService planService ) {
        // Planner can see any actor's procedures
        Plan plan = planService.getPlan();
        if ( plan.isTemplate() || user.isPlanner( plan.getUri() ) )
            return true;
        // Participating user can see own procedures. Supervisor can procedures of supervised.
        Participation participation = planService.findParticipation( user.getUsername() );
        if ( participation != null ) {
            Actor participant = participation.getActor();
            return participant.equals( actor ) || planService.findSupervised( participant ).contains( actor );
        }
        return false;
    }

    @WebMethod( exclude = true )
    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    @WebMethod( exclude = true )
    public void setSemanticMatcher( SemanticMatcher semanticMatcher ) {
        this.semanticMatcher = semanticMatcher;
    }

    @WebMethod( exclude = true )
    public void setUserDao( UserDao userDao ) {
        this.userDao = userDao;
    }

    @WebMethod( exclude = true )
    public void setAttachmentManager( AttachmentManager attachmentManager ) {
        this.attachmentManager = attachmentManager;
    }

    @WebMethod( exclude = true )
    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }


    private PlanService getPlanService( Plan plan ) {
        return new PlanService(
                planManager,
                semanticMatcher,
                userDao,
                attachmentManager,
                plan );
    }
}
