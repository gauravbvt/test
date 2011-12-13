package com.mindalliance.channels.api;

import com.mindalliance.channels.api.plan.PlanScopeData;
import com.mindalliance.channels.api.plan.PlanSummariesData;
import com.mindalliance.channels.api.procedures.ProceduresData;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Channels Web Service Interface
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/11
 * Time: 10:36 AM
 */
@WebService( targetNamespace = "http://mind-alliance.com/api/isp/v1/" )
public interface ChannelsService {
    /**
     * Get the scopes of al authorized production plans.
     *
     * @return plan scopes
     */
    @GET
    @Path( "planScopes" )
    @Produces( MediaType.APPLICATION_XML )
    List<PlanScopeData> getPlanScopes();

    @GET
    @Path( "plan/{uri}" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get scope of production plan.
     * @param uri a plan's URI
     * @return a plan's scope
     */
    PlanScopeData getPlanScope( @PathParam( "uri" ) String uri );

    @GET
    @Path( "plans" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get summaries of all production plans.
     * @return plan identifiers
     */
    PlanSummariesData getPlans();


    @GET
    @Path( "procedures/{uri}/{actorId}" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get identifiers of all production plans.
     * @param uri a plan's URI
     * @param actorId an actor's id
     * @return plan identifiers
     */
    ProceduresData getProcedures( @PathParam( "uri" ) String uri, @PathParam( "actorId" ) String actorId );

}
