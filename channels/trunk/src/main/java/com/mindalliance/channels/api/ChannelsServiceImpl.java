package com.mindalliance.channels.api;

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

    @Override
    /**
     * Get scope of production plan.
     * Available only to its planners.
     * @param uri the plan's URI
     * @return a plan's scope
     */
    public PlanScopeData getPlanScope( String uri ) {
        try {
            User user = User.current();
            Plan plan = planManager.findProductionPlan( uri );
            if ( plan == null )
                throw new Exception( "Plan " + uri + " is not available" );
            if ( user.isPlanner( plan.getUri() ) ) {
                return ( new PlanScopeData( plan, getPlanService( plan ) ) );
            } else {
                throw new Exception( "Plan " + uri + " is not available" );
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
    /**
     * Get summaries of all production plans for which the user is authorized.
     * @return plan identifiers
     */
    public PlanSummariesData getPlans() {
        User user = User.current();
        List<PlanSummaryData> result = new ArrayList<PlanSummaryData>();
        for ( Plan plan : planManager.getPlans() ) {
            String uri = plan.getUri();
            if ( plan.isProduction() && !user.getRole( uri ).equals( User.UNAUTHORIZED ) ) {
                result.add( new PlanSummaryData( getPlanService( plan )));
            }
        }
        return new PlanSummariesData( result );
    }

    @Override
    public ProceduresData getProcedures( String uri, String actorId ) {
        User user = User.current();
        Plan plan = null;
        try {
            plan = planManager.findProductionPlan( uri );
        } catch ( Exception e ) {
            LOG.error( "Plan not found " + uri );
        }
        if ( plan == null || user.getRole( uri ).equals( User.UNAUTHORIZED ) ) {
            LOG.error( user.getUsername() + " is not authorized to access plan " + uri );
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No plan available with uri " + uri )
                            .build() );
        } else {
            PlanService planService = getPlanService( plan );
            try {
                Actor actor = planService.find( Actor.class, Long.parseLong( actorId ) );
                if ( canSeeProcedures( user, actor, planService ) ) {
                    return new ProceduresData(
                            plan,
                            actor,
                            planService );
                } else {
                    LOG.error( user.getUsername()
                            + " is not authorized to access procedures of agent " + actorId
                            + " in plan " + uri );
                    throw new Exception( "Procedures are not visible" );
                }
            } catch ( Exception e ) {
                LOG.error( e.getMessage(), e );
                throw new WebApplicationException(
                        Response
                                .status( Response.Status.BAD_REQUEST )
                                .entity( "No procedures available for agent " + actorId )
                                .build() );
            }
        }
    }

    private boolean canSeeProcedures( User user, Actor actor, PlanService planService ) {
        // Planner can see any actor's procedures
        if ( user.isPlanner( planService.getPlan().getUri() ) )
            return true;
        // Participating user can see own procedures
        Participation participation = planService.findParticipation( user.getUsername() );
        if ( participation != null ) {
            Actor participant = participation.getActor();
            if ( participant.equals(  actor ) )
                return true;
            else
                // or an underling in a common organization
                return planService.findSupervised( participant ).contains( actor );
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

    private PlanService getPlanService( Plan plan ) {
        return new PlanService(
                planManager,
                semanticMatcher,
                userDao,
                attachmentManager,
                plan );
    }
}
