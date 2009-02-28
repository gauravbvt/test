package com.mindalliance.channels.command;

/**
 * A command execution controller.
 * A commander serializes command executions.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 28, 2009
 * Time: 2:20:10 PM
 */
public interface Commander {
    /**
     * Get the history of executed commands.
     *
     * @return a command history
     */
    History getHistory();

    /**
     * Get undo command available to the user.
     *
     * @return a command or null
     */
    Command getUndo();

    /**
     * Get redo command available to the user.
     *
     * @return a command or null
     */
    Command getRedo();

    /**
     * Whether the command can be executed.
     * All required locks can be taken and the user is authorized.
     *
     * @param command a command
     * @return a boolean
     */
    boolean canExecute( Command command );

    /**
     * Executes a command on behalf of the user.
     * Locks are grabbed
     *
     * @param command a command
     * @return an object
     * @throws CommandException if execution could not proceeed or failed.
     */
    Object execute( Command command ) throws CommandException;

}
