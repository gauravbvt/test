/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.model.Plan;

/**
 * A listener to command execution.
 */
public interface CommandListener {

    /**
     * A command was executed causing change.
     *
     * @param command a Command
     * @param change a Change
     * @param plan the plan of concern.
     */
    void commandDone( Command command, Change change, Plan plan );

    /**
     * The execution of a command was undone.
     *
     * @param command a Command
     * @param plan the plan of concern.
     */
    void commandUndone( Command command, Plan plan );

    /**
     * The execution of a command was redone.
     *
     * @param command a Command
     * @param plan the plan of concern.
     */
    void commandRedone( Command command, Plan plan );
}
