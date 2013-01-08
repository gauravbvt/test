package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;

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
     * Get the list of all known agencies (all fixed and registered organizations).
     * @param planCommunity a plan community
     * @return a list of agencies
     */
    List<Agency> getAllKnownAgencies( PlanCommunity planCommunity );

    /**
     * Get the agency given unique name.
     * @param agencyName an agency name
     * @param planCommunity a plan community
     * @return an agency or null
     */
    Agency findAgencyNamed( String agencyName, PlanCommunity planCommunity );

    /**
     * Get the list of all known agents (from all fixed and registered organizations).
     * @param planCommunity a plan community
     * @return a list of agents
     */
    List<Agent> getAllKnownAgents( PlanCommunity planCommunity );

    /**
     * Find all agencies employing a given agent.
     * @param agent an agent
     * @param planCommunity a plan community
     * @return a list of agencies
     */
    List<Agency> findAllEmployersOfAgent( Agent agent, PlanCommunity planCommunity );

    /**
     * Find all employments of an agent in a community.
     * @param agent an agent
     * @param planCommunity a plan community
     * @return a list of community employments
     */
    List<CommunityEmployment> findAllEmploymentsForAgent( Agent agent, PlanCommunity planCommunity );

    /**
     * Find all agents with unconstrained participation and available to user.
     * @param planCommunity a plan community
     * @param user a Channels user
     * @return a list of agents
     */
    List<Agent> findSelfAssignableOpenAgents( PlanCommunity planCommunity, ChannelsUser user );

    /**
     * Whether a participation as a given agent is available to a given user.
     * @param agent an agent
     * @param user a Channels user
     * @param planCommunity a plan community
     * @return a boolean
     */
    boolean isParticipationAvailable( Agent agent, ChannelsUser user, PlanCommunity planCommunity );

    /**
     * Whether a given user can self assign a participation as a given agent.
     * @param agent an agent
     * @param user a user
     * @param planCommunity a plan community
     * @return a boolean
     */
    boolean isParticipationSelfAssignable( Agent agent, ChannelsUser user, PlanCommunity planCommunity );

    /**
     * Whether participating as an agent meets pre-employment requirement given active participations.
     * @param agent an agent
     * @param activeParticipations list of user participations
     * @param planCommunity a plan community
     * @return a boolean
     */
    boolean meetsPreEmploymentConstraint(
            Agent agent,
            List<UserParticipation> activeParticipations,
            PlanCommunity planCommunity );

    /**
     * Find all agents supervising a given agent.
     * @param agent an agent
     * @param planCommunity a plan community
     * @return a list of agents
     */
    List<Agent> findAllSupervisorsOf( Agent agent, PlanCommunity planCommunity );

    /**
     * Are two users related by commitment or task-co-assignment?
     * @param planCommunity a plan community
     * @param user a user
     * @param otherUser another user
     * @return a boolean
     */
    boolean areCollaborators( PlanCommunity planCommunity, ChannelsUser user, ChannelsUser otherUser );

    /**
     * Is first user supervised other?
     * @param planCommunity a plan community
     * @param user a user
     * @param otherUser another user
     * @return a boolean
     */
    boolean isSupervisedBy( PlanCommunity planCommunity, ChannelsUser user, ChannelsUser otherUser );

    /**
     * Is first user supervisor of the other?
     * @param planCommunity a plan community
     * @param user a user
     * @param otherUser another user
     * @return a boolean
     */
    boolean isSupervisorOf( PlanCommunity planCommunity, ChannelsUser user, ChannelsUser otherUser );

    /**
     * Do two users share a common employer?
     * @param planCommunity a plan community
     * @param user a user
     * @param otherUser another user
     * @return a boolean
     */
    boolean areColleagues( PlanCommunity planCommunity, ChannelsUser user, ChannelsUser otherUser );

    /**
     * Whether user has authority over a participation.
     * @param planCommunity a plan community
     * @param user a user
     * @param userParticipation a plan participation
     * @return a boolean
     */
    boolean hasAuthorityOverParticipation(
            PlanCommunity planCommunity,
            ChannelsUser user,
            UserParticipation userParticipation );

    Plan getPlan( String communityUri, int planVersion );

    /**
     * Find all agents no user participates as.
     * @param planCommunity a plan community
     * @return a list of agents
     */
    List<Agent> findAllUnassignedAgents( PlanCommunity planCommunity );

    /**
     * Find all unassigned placeholder organizations.
     * @param planCommunity a plan community
     * @return a list of organziations
     */
    List<Organization> findAllUnassignedPlaceholders( PlanCommunity planCommunity );

    /**
     * Get top registered organization given one.
     * @param registeredOrganization a registered organization
     * @param planCommunity a plan community
     * @return a registered organization - the given one if it has no parent
     */
    RegisteredOrganization getTopRegisteredOrganization( RegisteredOrganization registeredOrganization,
                                                         PlanCommunity planCommunity );

    /**
     * Get ancestors of a given registered organization.
     * @param registeredOrganization a registered organization
     * @param planCommunity a plan community
     * @return a list of registered organizations
     */
    List<RegisteredOrganization> ancestorsOf( RegisteredOrganization registeredOrganization,
                                              PlanCommunity planCommunity );

}
