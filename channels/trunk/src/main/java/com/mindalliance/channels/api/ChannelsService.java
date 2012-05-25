package com.mindalliance.channels.api;

import com.mindalliance.channels.api.issues.IssuesData;
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
    @Path( "plans" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get summaries of all plan versions visible to the authenticated user.
     * @return plan summaries
     */
    PlanSummariesData getPlans();

    @GET
    @Path( "plan/{uri}/version/{version}/scope" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the scope of a version of a plan (for authorized planners only).
     * @param uri a plan's URI
     * @return a plan's scope
     */
    PlanScopeData getPlanScope( @PathParam( "uri" ) String uri, @PathParam( "version") String version );


    @GET
    @Path( "plan/{uri}/procedures/agent/{agentId}" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the procedures of an agent in a plan (for authorized planners or user participating in plan as the agent).
     * @param uri a plan's URI
     * @param agentId an agent's id
     * @return the procedures of the agent from the plan
     */
    ProceduresData getProcedures( @PathParam( "uri" ) String uri, @PathParam( "agentId" ) String agentId );

    @GET
    @Path( "plan/{uri}/procedures" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the procedures of the user if he/she participates in the identified plan as an agent.
     * @param uri a plan's URI
     * @return the procedures of the agent representing the user in the plan
     */
    ProceduresData getMyProcedures( @PathParam( "uri" ) String uri );

    @GET
    @Path( "plan/{uri}/version/{version}/issues" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the issues reported and detected in a plan (for authorized planners).
     * @param uri a plan's URI
     * @return plan issues
     */
    IssuesData getIssues( @PathParam( "uri" ) String uri, @PathParam( "version" ) String version );


}
