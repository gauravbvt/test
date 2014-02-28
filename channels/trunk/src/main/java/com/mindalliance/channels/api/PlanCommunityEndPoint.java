package com.mindalliance.channels.api;

import com.mindalliance.channels.api.community.CommunitySummariesData;
import com.mindalliance.channels.api.community.CommunitySummaryData;
import com.mindalliance.channels.api.directory.DirectoryData;
import com.mindalliance.channels.api.issues.IssuesData;
import com.mindalliance.channels.api.plan.ModelReleaseData;
import com.mindalliance.channels.api.plan.ModelScopeData;
import com.mindalliance.channels.api.plan.ModelSummariesData;
import com.mindalliance.channels.api.plan.ModelSummaryData;
import com.mindalliance.channels.api.procedures.AllChecklistsData;
import com.mindalliance.channels.api.procedures.ChecklistsData;

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

    /// PLANS

    @GET
    @Path( "models" )
    @Produces( MediaType.APPLICATION_XML )
    // @Produces( MediaType.APPLICATION_JSON )
    /**
     * Get summaries of all template versions visible to the authenticated user.
     * @return template summaries
     */
    ModelSummariesData getModels();

    @GET
    @Path( "model/{uri}/version/{version}" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get a template's summary.
     * @param uri a template uri
     * @param version a template version
     * @return template summary data
     */
    ModelSummaryData getModel( @PathParam( "uri" ) String uri, @PathParam( "version" ) String version );

    @GET
    @Path( "model/{uri}/release" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get a template's production release data.
     * @param uri a template uri
     * @return template release data
     */
    ModelReleaseData getModelRelease( @PathParam( "uri" ) String uri );

    @GET
    @Path( "mymodel" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get summaries of all production template versions the authenticated user participates or can participate in.
     * @return template summaries
     */
    ModelSummariesData getProductionModels();

    @GET
    @Path( "model/{uri}/version/{version}/scope" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the scope of a version of a template (for authorized planners only).
     * @param uri a plan's URI
     * @param version the plan's version
     * @return a template's scope
     */
    ModelScopeData getModelScope( @PathParam( "uri" ) String uri, @PathParam( "version" ) String version );

    @GET
    @Path( "model/{uri}/version/{version}/issues" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the issues reported and detected in a template (for authorized planners).
     * @param uri a plan's URI
     * @return plan issues
     */
    IssuesData getModelIssues( @PathParam( "uri" ) String uri,
                               @PathParam( "version" ) String version );

    @POST
    @Consumes( MediaType.APPLICATION_FORM_URLENCODED )
    @Path( "model/{uri}/feedback" )
    void addModelFeedback(
            @PathParam( "uri" ) String uri,
            @FormParam( "type" ) String type, // one of "QUESTION", "PROBLEM", "SUGGESTION"
            @FormParam( "feedback" ) String feedback,
            @FormParam( "urgent" ) String urgent );  // "true" or "false"

    @POST
    @Consumes( MediaType.APPLICATION_FORM_URLENCODED )
    @Path( "model/{uri}/invite" )
    void inviteToModel( @PathParam( "uri" ) String uri,
                        @FormParam( "email" ) String email,
                        @FormParam( "message" ) String message );


    /// PLANS

    @GET
    @Path( "communities" )
    @Produces( MediaType.APPLICATION_XML )
    CommunitySummariesData getAllCommunities();

    @GET
    @Path( "community/{uri}" )
    @Produces( MediaType.APPLICATION_XML )
    CommunitySummaryData getCommunity( @PathParam( "uri" ) String communityUri );

    @GET
    @Path( "community/{uri}/allChecklists" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the checklists of every participating users.
     * @param uri a plan's URI
     * @param version a plan's version
     * @return the checklists of all users participating in the plan
     */
    AllChecklistsData getAllChecklists( @PathParam( "uri" ) String communityUri );

    @GET
    @Path( "community/{uri}/checklists" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the checklists of the user if he/she participates in the identified community as one or more agents.
     * @param uri a community's URI
     * @return the checklists of the agents representing the user in the plan
     */
    ChecklistsData getMyChecklists( @PathParam( "uri" ) String communityUri );

    @GET
    @Path( "community/{uri}/user/{user}/checklists" )
    @Produces( MediaType.APPLICATION_XML )
    ChecklistsData getUserChecklists(
            @PathParam("uri") String communityUri,
            @PathParam("user") String username );


    @GET
    @Path( "community/{uri}/agent/{agentId}/org/{orgParticipationId}/checklists" ) // todo - BROKEN
    @Produces( MediaType.APPLICATION_XML )
/**
 *     Get the checklists of an agent in an organization.
 */
    ChecklistsData getAgentChecklists( @PathParam("uri") String uri,
                                      @PathParam("agentId") String agentId,
                                      @PathParam("orgParticipationId") String orgParticipationId );  // agent in dynamically participating organization

    @GET
    @Path( "community/{uri}/user/{user}/directory" )
    @Produces( MediaType.APPLICATION_XML )
    /**
     * Get the contacts directory of a user in a plan.
     * @param uri a plan uri
     * @param version a plan version
     * @param username a username
     * @return directory data
     */
    DirectoryData getUserDirectory( @PathParam( "uri" ) String communityUri,
                                    @PathParam( "user" ) String username );

    @GET
    @Path( "community/{uri}/directory" )
    @Produces( MediaType.APPLICATION_XML )
    DirectoryData getMyDirectory( @PathParam( "uri" ) String communityUri );

    @PUT
    @Path( "community/{uri}/contact/medium/{mediumId}/address/{address}" )
    void addContactInfo( @PathParam( "uri" ) String communityUri,
                         @PathParam( "mediumId" ) String mediumId,
                         @PathParam( "address" ) String address );

    @DELETE
    @Path( "community/{uri}/contact/medium/{mediumId}/address/{address}" )
    void removeContactInfo( @PathParam( "uri" ) String communityUri,
                            @PathParam( "mediumId" ) String mediumId,
                            @PathParam( "address" ) String address );

    @PUT
    @Path( "community/{uri}/agent/{agentId}/agency/{orgId}/participation" )
    void acceptParticipation( @PathParam( "uri" ) String communityUri,
                              @PathParam( "agentId" ) String agentId,
                              @PathParam( "orgId") String orgId );

    @DELETE
    @Path( "community/{uri}/agent/{agentId}/agency/{orgId}/participation" )
    void refuseParticipation( @PathParam( "uri" ) String communityUri,
                              @PathParam( "agentId" ) String agentId,
                              @PathParam( "orgId") String orgId );


    /////////

    @WebMethod( exclude = true )
    String getServerUrl();

    /**
     * Get the scope of a version of a plan (for authorized planners only).
     *
     * @param uri               a plan's URI
     * @param version           the plan's version
     * @param developerOnly       whether only planners are authorized
     * @return a plan's scope
     */
    ModelScopeData modelScope( String uri, String version, boolean developerOnly );


}
