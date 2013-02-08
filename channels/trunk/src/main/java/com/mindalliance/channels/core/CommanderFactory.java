package com.mindalliance.channels.core;

import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.CommunityService;

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
}
