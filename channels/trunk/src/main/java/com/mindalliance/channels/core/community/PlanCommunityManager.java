package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.command.CommandListener;
import com.mindalliance.channels.core.model.Plan;

import java.util.List;

/**
 * Plan community manager.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/12
 * Time: 3:15 PM
 */
public interface PlanCommunityManager  extends CommandListener {

    /**
     * Get the plan community's DAO.
     * @param planCommunity a plan community
     * @return a community dao
     */
    CommunityDao getDao( PlanCommunity planCommunity );

    /**
     * Get the plan community's DAO.
     * @param uri a plan community's uri
     * @return a community dao
     */
    public CommunityDao getDao( String uri );

    /**
     * Get plan community given uri. If domain plan uri, return plan community fro dev version.
     * @param planCommunityUri a plan community uri (if a plan uri then community for domain planners)
     * @return a plan community
     */
    PlanCommunity getPlanCommunity( String planCommunityUri );


    /**
     * Find a plan community with a given version of their plan (used by planners).
     * @param uri a community's URI
     * @param planVersion  a plan's version
     * @return a plan community or null
     */
    PlanCommunity findPlanCommunity( String uri, int planVersion );

    /**
     * Return the list of all plan communities.
     * @return a plan community.
     */
    List<PlanCommunity> getPlanCommunities();

    /**
     * Return the domain plan community for a plan (community of planners).
     * @param plan a plan
     * @return a plan community
     */
    PlanCommunity getDomainPlanCommunity( Plan plan );

    /**
     * Clears community cache.
     * Via AOP.
     */
    void clearCache();
}
