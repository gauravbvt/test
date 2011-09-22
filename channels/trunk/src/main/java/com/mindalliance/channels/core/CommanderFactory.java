package com.mindalliance.channels.core;

import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.Plan;

/**
 * ...
 */
public interface CommanderFactory {

    /**
     * Get the dedicated commander for a given plan.
     * @param plan the plan
     * @return the commander
     */
    Commander getCommander( Plan plan );
}
