package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.orm.service.GenericSqlService;

import java.util.List;

/**
 * Community planner service interface.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/19/13
 * Time: 10:33 AM
 */
public interface CommunityPlannerService  extends GenericSqlService<CommunityPlanner, Long> {

    /**
     * Whether a user was authorized as community planner.
     * @param user a Channels user
     * @param communityService a community service
     * @return a boolean
     */
    boolean isPlanner( ChannelsUser user, CommunityService communityService );

    /**
     * Authorize a user as planner in a community.
     * Can return null if promotion is not allowed.
     * @param username who authorizes
     * @param planner  user authorized
     * @param communityService a community service
     * @return a community planner
     */
    CommunityPlanner authorizePlanner( String username, ChannelsUser planner, CommunityService communityService );

    /**
     * Remove planner status from user in a community.
     * @param username who effected the resignation
     * @param planner resigning user
     * @param communityService a community service
     * @return whether resignation was effective
     */
    boolean resignAsPlanner( String username, ChannelsUser planner, CommunityService communityService );

    /**
     * List all community planners.
     * Excludes the admin who is implicitly a planner in all communities.
     * @param communityService a community service
     * @return a list of community planners
     */
    List<CommunityPlanner> listPlanners( CommunityService communityService );

    /**
     * Whether the user was notified of planner status.
     * @param planner a user to be notified
     * @param communityService a community service
     * @return a boolean
     */
    boolean wasNotified( ChannelsUser planner, CommunityService communityService);

    /**
     * Mark the user as having been notified of the new status as planner.
     * @param planner a user
     * @param communityService a community service
     */
    void setNotified( ChannelsUser planner, CommunityService communityService );

    /**
     * Add a first planner to a plan community.
     * @param founder a Channels user
     * @param planCommunity  a plan community
     */
    void addFounder( ChannelsUser founder, PlanCommunity planCommunity );
}
