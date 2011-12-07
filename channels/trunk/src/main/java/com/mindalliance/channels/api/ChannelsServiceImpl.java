package com.mindalliance.channels.api;

import com.mindalliance.channels.api.plan.PlanIdentifiersData;
import com.mindalliance.channels.api.plan.PlanScopeData;
import com.mindalliance.channels.api.procedures.ProceduresData;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.nlp.SemanticMatcher;
import com.mindalliance.channels.core.query.PlanService;

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

    private PlanManager planManager;
    private SemanticMatcher semanticMatcher;
    private UserDao userDao;
    private AttachmentManager attachmentManager;

    @Override
    /**
     * Get the scopes of al authorized production plans.
     * @return plan scopes
     */
    public List<PlanScopeData> getPlanScopes() {
        User user = User.current();
        List<PlanScopeData> result = new ArrayList<PlanScopeData>();
        for ( Plan plan : planManager.getPlans() ) {
            if ( plan.isProduction() && !user.getRole( plan.getUri() ).equals( User.UNAUTHORIZED ) ) {
                PlanService planService = new PlanService(
                        planManager,
                        semanticMatcher,
                        userDao,
                        attachmentManager,
                        plan );
                result.add( new PlanScopeData( plan, planService ) );
            }
        }
        return result;
    }

    @Override
    /**
     * Get scope of production plan.
     * @param uri the plan's URI
     * @return a plan's scope
     */
    public PlanScopeData getPlanScope( String uri ) {
        try {
            User user = User.current();
            Plan plan = planManager.findProductionPlan( uri );
            if ( plan == null )
                throw new Exception( "Plan " + uri + " is not available" );
            if ( !user.getRole( plan.getUri() ).equals( User.UNAUTHORIZED ) ) {
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
     * Get identities of all production plans.
     * @return plan identifiers
     */
    public PlanIdentifiersData getPlans() {
        User user = User.current();
        List<Plan> result = new ArrayList<Plan>();
        for ( Plan plan : planManager.getPlans() ) {
            String uri = plan.getUri();
            if ( plan.isProduction() && !user.getRole( uri ).equals( User.UNAUTHORIZED ) ) {
                result.add( plan );
            }
        }
        return new PlanIdentifiersData( result );
    }

    @Override
    public ProceduresData getProcedures( String uri, String actorId ) {
        User user = User.current();
        Plan plan = planManager.findProductionPlan( uri );
        if ( plan == null || user.getRole( uri ).equals( User.UNAUTHORIZED ) ) {
            throw new WebApplicationException(
                    Response
                            .status( Response.Status.BAD_REQUEST )
                            .entity( "No plan available with uri " + uri )
                            .build() );
        } else {
            PlanService planService = getPlanService( plan );
            try {
                return new ProceduresData(
                        plan,
                        planService.find( Actor.class, Long.parseLong( actorId ) ),
                        planService );
            } catch ( Exception e ) {
                throw new WebApplicationException(
                        Response
                                .status( Response.Status.BAD_REQUEST )
                                .entity( "No procedures available for agent " + actorId )
                                .build() );
            }
        }
    }

    @WebMethod(
            exclude = true
    )
    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    @WebMethod(
            exclude = true
    )
    public void setSemanticMatcher( SemanticMatcher semanticMatcher ) {
        this.semanticMatcher = semanticMatcher;
    }

    @WebMethod(
            exclude = true
    )
    public void setUserDao( UserDao userDao ) {
        this.userDao = userDao;
    }

    @WebMethod(
            exclude = true
    )
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
