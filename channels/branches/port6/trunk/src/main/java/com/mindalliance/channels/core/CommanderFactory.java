package com.mindalliance.channels.core;

import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;

/**
 * ...
 */
public interface CommanderFactory {

    /**
     * Get the dedicated commander for a given plan.
     * @param communityService a plan community service
     * @return the commander
     */
    Commander getCommander( CommunityService communityService );

    /**
     * reset commander for given plan community.
     * @param planCommunity a plan community
     */
    void reset( PlanCommunity planCommunity );
}
