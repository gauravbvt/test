package com.mindalliance.channels.core;

import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.PlanCommunity;

/**
 * ...
 */
public interface CommanderFactory {

    /**
     * Get the dedicated commander for a given plan.
     * @param planCommunity a plan community
     * @return the commander
     */
    Commander getCommander( PlanCommunity planCommunity );
}
