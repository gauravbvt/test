/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

/**
 * A listener to command execution.
 */
public interface CommandListener {

    /**
     * A command was executed causing change.
     *
     * @param commander responsible of executing the command
     * @param command a Command
     * @param change a Change
     */
    void commandDone( Commander commander, Command command, Change change );

    /**
     * The execution of a command was undone.
     *
     * @param commander responsible of executing the command
     * @param command a Command
     * @param change the resulting change
     */
    void commandUndone( Commander commander, Command command, Change change );

    /**
     * The execution of a command was redone.
     *
     * @param commander responsible of executing the command
     * @param command a Command
     * @param change the resulting change
     */
    void commandRedone( Commander commander, Command command, Change change );

    /**
     * Notification that a new commander was initialized.
     * @param commander the commander
     *
     */
    void started( Commander commander );

 }
