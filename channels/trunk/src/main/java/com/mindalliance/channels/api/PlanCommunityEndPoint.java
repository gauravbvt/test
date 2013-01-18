package com.mindalliance.channels.api;

import com.mindalliance.channels.api.directory.DirectoryData;
import com.mindalliance.channels.api.issues.IssuesData;
import com.mindalliance.channels.api.plan.PlanReleaseData;
import com.mindalliance.channels.api.plan.PlanScopeData;
import com.mindalliance.channels.api.plan.PlanSummariesData;
import com.mindalliance.channels.api.plan.PlanSummaryData;
import com.mindalliance.channels.api.procedures.AllProceduresData;
import com.mindalliance.channels.api.procedures.ProceduresData;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Plan community service, with REST endpoints.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/11
 * Time: 10:36 AM
 */
@WebService( targetNamespace = "http://mind-alliance.com/api/isp/v1/" )
public interface PlanCommunityEndPoint {

    @GET
    @Path( "plans" )
    @Produces( MediaType.APPLICATION_XML )      // @Produces( MediaType.APPLICATION_JSON )
    /**
     * Get summaries of all plan versions visible to the authenticated user.
     * @return plan summaries
     */
    PlanSummariesData getPlans();

    @GET
    @Path( "plan/{uri}/version/{version}" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get a plan's summary.
     * @param uri a plan community uri
     * @param version a plan version
     * @return plan summary data
     */
    PlanSummaryData getPlan( @PathParam( "uri" ) String uri, @PathParam( "version" ) String version );

    @GET
    @Path( "plan/{uri}/release" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get a plan's production release data.
     * @param uri a plan uri
     * @return plan release data
     */
    PlanReleaseData getPlanRelease( @PathParam( "uri" ) String uri );

    @GET
    @Path( "myplans" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get summaries of all production plan versions the authenticated user participates or can participate in.
     * @return plan summaries
     */
    PlanSummariesData getProductionPlans();

    @GET
    @Path( "plan/{uri}/version/{version}/scope" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the scope of a version of a plan (for authorized planners only).
     * @param uri a plan's URI
     * @return a plan's scope
     */
    PlanScopeData getPlanScope( @PathParam( "uri" ) String uri, @PathParam( "version" ) String version );


    @GET
    @Path( "plan/{uri}/actor/{actorId}/org/{orgId}/procedures" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the procedures of an agent in a plan (for authorized planners or user participating in plan as the agent).
     * @param uri a plan's URI
     * @param actorId an actor's id
     * @return the procedures of the agent from the plan
     */
    ProceduresData getActorProcedures( @PathParam( "uri" ) String uri,
                                       @PathParam( "actorId" ) String actorId );

    @GET
    @Path( "plan/{uri}/procedures" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the procedures of the user if he/she participates in the identified plan as one or more agents.
     * @param uri a plan's URI
     * @return the procedures of the agents representing the user in the plan
     */
    ProceduresData getMyProcedures( @PathParam( "uri" ) String uri );

    @GET
    @Path( "plan/{uri}/version/{version}/allProcedures" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the procedures of every participating users.
     * @param uri a plan's URI
     * @param version a plan's version
     * @return the procedures of all users participating in the plan
     */
    AllProceduresData getAllProcedures( @PathParam( "uri" ) String uri,
                                     @PathParam( "version" ) String version );


    @GET
    @Path( "plan/{uri}/version/{version}/issues" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the issues reported and detected in a plan (for authorized planners).
     * @param uri a plan's URI
     * @return plan issues
     */
    IssuesData getIssues( @PathParam( "uri" ) String uri, @PathParam( "version" ) String version );

    @GET
    @Path( "plan/{uri}/version/{version}/user/{user}/procedures" )
    @Produces( MediaType.APPLICATION_XML )
    ProceduresData getUserProcedures(
            @PathParam("uri") String uri,
            @PathParam( "version" ) String version,
            @PathParam("user") String username );


    @GET
    @Path( "plan/{uri}/version/{version}/agent/{agentId}/procedures" )
    @Produces( MediaType.APPLICATION_XML )
    ProceduresData getAgentProcedures( @PathParam("uri") String uri,
                                       @PathParam( "version" ) String version,
                                       @PathParam("agentId") String agentId );   // agent in plan-defined organization

    @GET
    @Path("plan/{uri}/version/{version}/agent/{agentId}/org/{orgParticipationId}/protocols")
    @Produces( MediaType.APPLICATION_XML )
    ProceduresData getAgentProtocols( @PathParam("uri") String uri,
                                      @PathParam("version") String version,
                                      @PathParam("agentId") String agentId,
                                      @PathParam("orgParticipationId") String orgParticipationId );  // agent in dynamically participating organization


    @GET
    @Path( "plan/{uri}/version/{version}/user/{user}/directory" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the contacts directory of a user in a plan.
     * @param uri a plan uri
     * @param version a plan version
     * @param username a username
     * @return directory data
    */
    DirectoryData getUserDirectory( @PathParam("uri") String uri,
                                @PathParam( "version" ) String version,
                                @PathParam("user") String username );
    @GET
    @Path( "plan/{uri}/version/{version}/agent/{agentId}/directory" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the contacts directory of a user in a plan.
     * @param uri a plan uri
     * @param version a plan version
     * @param agentId an agent id
     * @return directory data
    */
    DirectoryData getAgentDirectory( @PathParam("uri") String uri,
                                    @PathParam( "version" ) String version,
                                    @PathParam("agentId") String agentId );

    @GET
    @Path( "plan/{uri}/directory" )
    @Produces( MediaType.APPLICATION_XML )
    DirectoryData getMyDirectory( @PathParam("uri") String uri );

    @PUT
    @Path( "plan/{uri}/agent/{agentId}/participation")
    void acceptParticipation( @PathParam( "uri" ) String uri,
                              @PathParam( "agentId" ) String agentId );

    @DELETE
    @Path( "plan/{uri}/agent/{agentId}/participation")
    void refuseParticipation( @PathParam( "uri" ) String uri,
                              @PathParam( "agentId" ) String agentId );

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path( "plan/{uri}/feedback")
    void addFeedback(
            @PathParam( "uri" ) String uri,
            @FormParam( "type" ) String type, // one of "QUESTION", "PROBLEM", "SUGGESTION"
            @FormParam( "feedback" ) String feedback,
            @FormParam( "urgent") String urgent );  // "true" or "false"

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path( "plan/{uri}/invite")
    void invite( @PathParam("uri") String uri,
                      @FormParam("email") String email,
                      @FormParam( "message") String message );

    @PUT
    @Path( "plan/{uri}/contact/medium/{mediumId}/address/{address}")
    void addContactInfo( @PathParam("uri") String uri,
            @PathParam( "mediumId") String mediumId,
            @PathParam("address") String address );

    @DELETE
    @Path( "plan/{uri}/contact/medium/{mediumId}/address/{address}")
    void removeContactInfo( @PathParam("uri") String uri,
            @PathParam( "mediumId") String mediumId,
            @PathParam("address") String address );


    /////////

    @WebMethod( exclude = true )
    String getServerUrl();

}
