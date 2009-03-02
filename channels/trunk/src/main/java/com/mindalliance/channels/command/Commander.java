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

    /**
     * Get the history of executed commands.
     *
     * @return a command history
     */
    History getHistory();

    /**
     * Whether user can undo a previous command.
     * @return a boolean
     */
    boolean canUndo();

    /**
     * Whether user can redo an undone command.
     * @return a boolean
     */
    boolean canRedo();

    /**
     * Undo user's previous command.
     * @throws CommandException if undoing fails or is not allowed.
     */
    void undo() throws CommandException;

    /**
     * Redo user's previous undone command.
     * @throws CommandException if redoing fails or is not allowed.
     */
    void redo() throws CommandException;

}
