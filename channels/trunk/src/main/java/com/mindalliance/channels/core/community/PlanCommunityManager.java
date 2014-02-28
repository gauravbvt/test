package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.command.CommandListener;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.CollaborationModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Plan community manager.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/12
 * Time: 3:15 PM
 */
public interface PlanCommunityManager extends CommandListener {

    /**
     * Get the plan community's DAO.
     *
     * @param planCommunity a plan community
     * @return a community dao
     */
    CommunityDao getDao( PlanCommunity planCommunity );

    /**
     * Get the plan community's DAO.
     *
     * @param uri a plan community's uri
     * @return a community dao
     */
    public CommunityDao getDao( String uri );

    /**
     * Get plan community given uri. If domain plan uri, return plan community fro dev version.
     *
     * @param planCommunityUri a plan community uri (if a plan uri then community for domain planners)
     * @return a plan community
     */
    PlanCommunity getPlanCommunity( String planCommunityUri );


    /**
     * Find a plan community with a given version of their plan (used by planners).
     *
     * @param uri         a community's URI
     * @param planVersion a plan's version
     * @return a plan community or null
     */
    PlanCommunity findPlanCommunity( String uri, int planVersion );

    /**
     * Return the list of all plan communities.
     *
     * @return a plan community.
     */
    List<PlanCommunity> getPlanCommunities();

    /**
     * Return the domain plan community for a plan (community of planners).
     *
     * @param collaborationModel a plan
     * @return a plan community
     */
    PlanCommunity getDomainPlanCommunity( CollaborationModel collaborationModel );

    /**
     * Clears community cache.
     * Via AOP.
     */
    void clearCache();

    /**
     * Create a new community for a given plan.
     *
     * @param collaborationModel    a plan
     * @param founder a user
     * @return a PlanCommunity
     */
    PlanCommunity createNewCommunityFor( CollaborationModel collaborationModel, ChannelsUser founder, CommunityService communityService );

    /**
     * List the usernames who are members of communities having adopted a given plan.
     *
     * @param collaborationModel a plan
     * @return a list of usernames
     */
    List<String> listAllAdopters( CollaborationModel collaborationModel );

    /**
     * Find a plan community with a given plan in which the user is a participant, community planner or template developer.
     *
     * @param collaborationModel a plan
     * @param user a Channels user
     * @return a plan community or null
     */
    PlanCommunity findPlanCommunity( CollaborationModel collaborationModel, ChannelsUser user );

    /**
     * Return the directory where a given plan community is persisted.
     *
     * @param planCommunity a plan community
     * @return a plan community
     */
    File getCommunityDirectory( PlanCommunity planCommunity );

    /**
     * Add a community listener.
     *
     * @param aCommunityListener a community listener
     */
    void addListener( CommunityListener aCommunityListener );

    /**
     * Update a plan community to another version of its plan.
     *
     * @param planCommunity a plan community
     * @param version       a plan version
     * @throws IOException if update failed
     */
    void updateToPlanVersion( PlanCommunity planCommunity, int version ) throws IOException;

}
