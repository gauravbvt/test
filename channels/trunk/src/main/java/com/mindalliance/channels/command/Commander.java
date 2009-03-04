package com.mindalliance.channels.command;

import com.mindalliance.channels.NotFoundException;

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
     * Whether the command could be executed right now.
     * The user is authorized and all required locks could be taken.
     *
     * @param command a command
     * @return a boolean
     */
    boolean canDo( Command command );

    /**
     * Executes a command on behalf of the user.
     * Locks are grabbed
     *
     * @param command a command
     * @return an object
     * @throws CommandException if execution could not proceeed or failed.
     * @throws com.mindalliance.channels.NotFoundException if the command is out of sync with the model
     */
    Object doCommand( Command command ) throws CommandException, NotFoundException;

    /**
     * Whether user could undo a previous command right now.
     * @return a boolean
     */
    boolean canUndo();

    /**
     * Whether user could redo an undone command right now.
     * @return a boolean
     */
    boolean canRedo();

    /**
     * Undo user's previous command.
     * @throws CommandException if undoing fails or is not allowed.
     * @throws com.mindalliance.channels.NotFoundException if the command is out of sync with the model
     */
    void undo() throws CommandException, NotFoundException;

    /**
     * Redo user's previous undone command.
     * @throws CommandException if redoing fails or is not allowed.
     * @throws com.mindalliance.channels.NotFoundException if the command is out of sync with the model
     */
    void redo() throws CommandException, NotFoundException;

}
