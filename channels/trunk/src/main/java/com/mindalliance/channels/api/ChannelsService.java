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

    @GET
    @Path( "plan/{uri}/scope" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the scope of a production plan (for authenticated planners only).
     * @param uri a plan's URI
     * @return a plan's scope
     */
    PlanScopeData getPlanScope( @PathParam( "uri" ) String uri );

    @GET
    @Path( "plans" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get summaries of all production plans visible to the authenticated user.
     * @return plan identifiers
     */
    PlanSummariesData getPlans();


    @GET
    @Path( "plan/{uri}/procedures/agent/{agentId}" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the procedures of an agent in a plan (for authenticated planners or user participating in plan as the agent).
     * @param uri a plan's URI
     * @param agentId an agent's id
     * @return plan identifiers
     */
    ProceduresData getProcedures( @PathParam( "uri" ) String uri, @PathParam( "agentId" ) String agentId );

}
