package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.users.UserRecord;

import java.util.List;

/**
 * Plan participation manager.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/12
 * Time: 2:33 PM
 */
public interface ParticipationManager {

    /**
     * Get participation analyst;
     *
     * @return a participation analyst
     */
    ParticipationAnalyst getParticipationAnalyst();

    /**
     * Get the list of all known agencies (all fixed and registered organizations) to a community.
     *
     * @param communityService a community service
     * @return a list of agencies
     */
    List<Agency> getAllKnownAgencies( CommunityService communityService );

    /**
     * Get the list of all agencies participating as a given placeholder organization.
     *
     * @param placeholder      an organization
     * @param communityService a community service
     * @return a list of agencies
     */
    List<Agency> findAgenciesParticipatingAs( Organization placeholder, CommunityService communityService );

    /**
     * Get the agency given unique name.
     *
     * @param agencyName       an agency name
     * @param communityService a community service
     * @return an agency or null
     */
    Agency findAgencyNamed( String agencyName, CommunityService communityService );

    /**
     * Get the list of all known agents (from all fixed and registered organizations).
     *
     * @param communityService a community service
     * @return a list of agents
     */
    List<Agent> getAllKnownAgents( CommunityService communityService );

    /**
     * Get agent given name.
     *
     * @param name             a unique agent name
     * @param communityService a community service
     * @return an agent or null
     */
    Agent findAgentNamed( String name, CommunityService communityService );

    /**
     * Find all agencies employing a given agent.
     *
     * @param agent            an agent
     * @param communityService a community service
     * @return a list of agencies
     */
    List<Agency> findAllEmployersOfAgent( Agent agent, CommunityService communityService );

    /**
     * Find all employments of an agent in a community.
     *
     * @param agent            an agent
     * @param communityService a community service
     * @return a list of community employments
     */
    List<CommunityEmployment> findAllEmploymentsForAgent( Agent agent, CommunityService communityService );

    /**
     * Lists all agent employments by a given agency.
     *
     * @param agency           an agency
     * @param communityService a community service
     * @return a list of community employments
     */
    List<CommunityEmployment> findAllEmploymentsBy( Agency agency, CommunityService communityService );

    /**
     * Find all agents with unconstrained participation and available to user.
     *
     * @param communityService a community service
     * @param user             a Channels user
     * @return a list of agents
     */
    List<Agent> findSelfAssignableOpenAgents( CommunityService communityService, ChannelsUser user );

    /**
     * Whether a participation as a given agent is available to a given user.
     *
     * @param agent            an agent
     * @param user             a Channels user
     * @param communityService a community service
     * @return a boolean
     */
    boolean isParticipationAvailable( Agent agent, ChannelsUser user, CommunityService communityService );

    /**
     * Whether a given user can self assign a participation as a given agent.
     *
     * @param agent            an agent
     * @param user             a user
     * @param communityService a community service
     * @return a boolean
     */
    boolean isParticipationSelfAssignable( Agent agent, ChannelsUser user, CommunityService communityService );

    /**
     * Whether participating as an agent meets pre-employment requirement given active participations.
     *
     * @param agent                an agent
     * @param activeParticipations list of user participations
     * @param communityService     a community service
     * @return a boolean
     */
    boolean meetsPreEmploymentConstraint(
            Agent agent,
            List<UserParticipation> activeParticipations,
            CommunityService communityService );

    /**
     * Find all agents supervising a given agent.
     *
     * @param agent            an agent
     * @param communityService a community service
     * @return a list of agents
     */
    List<Agent> findAllSupervisorsOf( Agent agent, CommunityService communityService );

    /**
     * Are two users related by commitment or task-co-assignment?
     *
     * @param communityService a community service
     * @param user             a user
     * @param otherUser        another user
     * @return a boolean
     */
    boolean areCollaborators( CommunityService communityService, ChannelsUser user, ChannelsUser otherUser );

    /**
     * Is first user supervised other?
     *
     * @param communityService a community service
     * @param user             a user
     * @param otherUser        another user
     * @return a boolean
     */
    boolean isSupervisedBy( CommunityService communityService, ChannelsUser user, ChannelsUser otherUser );

    /**
     * Is first user supervisor of the other?
     *
     * @param communityService a community service
     * @param user             a user
     * @param otherUser        another user
     * @return a boolean
     */
    boolean isSupervisorOf( CommunityService communityService, ChannelsUser user, ChannelsUser otherUser );

    /**
     * Do two users share a common employer?
     *
     * @param communityService a community service
     * @param user             a user
     * @param otherUser        another user
     * @return a boolean
     */
    boolean areColleagues( CommunityService communityService, ChannelsUser user, ChannelsUser otherUser );

    /**
     * Whether user has authority over a participation.
     *
     * @param communityService  a community service
     * @param user              a user whose authority is queried
     * @param userParticipation a plan participation
     * @return a boolean
     */
    boolean hasAuthorityOverParticipation(
            CommunityService communityService,
            ChannelsUser user,
            UserParticipation userParticipation );

    /**
     * Whether user has authority over a participation.
     *
     * @param communityService   a community service
     * @param user               a user whose authority is queried
     * @param participantInfo    a participant's user info
     * @param participationAgent the agent the participant participates as or would
     * @return
     */
    boolean hasAuthorityOverParticipation(
            final CommunityService communityService,
            ChannelsUser user,
            UserRecord participantInfo,
            Agent participationAgent );

    Plan getPlan( String communityUri, int planVersion );

    /**
     * Find all agents no user participates as.
     *
     * @param communityService a community service
     * @return a list of agents
     */
    List<Agent> findAllUnassignedAgents( CommunityService communityService );

    /**
     * Find all unassigned placeholder organizations.
     *
     * @param communityService a community service
     * @return a list of organziations
     */
    List<Organization> findAllUnassignedPlaceholders( CommunityService communityService );

    /**
     * Get top registered organization given one.
     *
     * @param registeredOrganization a registered organization
     * @param communityService       a community service
     * @return a registered organization - the given one if it has no parent
     */
    RegisteredOrganization getTopRegisteredOrganization( RegisteredOrganization registeredOrganization,
                                                         CommunityService communityService );

    /**
     * Get ancestors of a given registered organization.
     *
     * @param registeredOrganization a registered organization
     * @param communityService       a community service
     * @return a list of registered organizations
     */
    List<RegisteredOrganization> ancestorsOf( RegisteredOrganization registeredOrganization,
                                              CommunityService communityService );

    /**
     * Finds agency by id.
     *
     * @param agencyId         a string - parse-able to long if fixed, else the uid of an organization participation as placeholder
     * @param communityService a community service
     * @return an agency
     * @throws NotFoundException if not found
     */
    Agency findAgencyById( String agencyId, CommunityService communityService ) throws NotFoundException;

    /**
     * Get registered parent organization.
     *
     * @param uid a db key
     * @return a registered organization
     */
    RegisteredOrganization getRegisteredOrganization( String uid );

    /**
     * Get organization participation given db key.
     *
     * @param uid a db key
     * @return an organization participation
     */
    OrganizationParticipation getOrganizationParticipation( String uid );

    /**
     * Get user participation given db key.
     *
     * @param uid a db key
     * @return a user participation
     */
    UserParticipation getUserParticipation( String uid );

    /**
     * Whether the agency is referenced anywhere (in template, in participation, as parent).
     *
     * @param agency           an agency
     * @param communityService a community service
     * @return a boolean
     */
    boolean isAgencyReferenced( Agency agency, CommunityService communityService );

    /**
     * Can an agency that is not local be made local?
     *
     * @param agency an agency
     * @return a boolean
     */
    Boolean canBeMadeGlobal( Agency agency, CommunityService communityService );

    /**
     * Can an agency that is not global be made global?
     *
     * @param agency an agency
     * @return a boolean
     */
    Boolean canBeMadeLocal( Agency agency, CommunityService communityService );

    /**
     * Whether users can directly participate as a given agent in a given agency.
     *
     * @param agent   an agent
     * @param agency   an agency
     * @param communityService a community service
     * @return a boolean
     */
    Boolean isDirectParticipationAllowed( Agent agent,
                                          Agency agency,
                                          CommunityService communityService );

    /**
     * Find the position to participate as to indirectly participate as a given agent in an agency
     *
     * @param agent   an agent
     * @param communityService a community service
     * @return a community employment
     */
    CommunityEmployment findDirectParticipationEmploymentForParticipationAs( Agent agent,
                                                                             CommunityService communityService );

    /**
     * Find all users participating directly or implicitly as a given agent.
     * @param agent an agent
     * @param communityService a community service
     * @return a list of users
     */
    List<ChannelsUser> findAllUsersParticipatingAs( Agent agent, CommunityService communityService );
}
