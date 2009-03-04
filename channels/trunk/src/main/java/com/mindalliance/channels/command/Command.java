package com.mindalliance.channels.command;

import com.mindalliance.channels.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A command that can be done, undone and redone.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 28, 2009
 * Time: 2:20:01 PM
 *
 * @param <T> the class of the return value of executing a command
 */
public interface Command<T> {
    /**
     * The command's name.
     *
     * @return a string
     */
    String getName();

    /**
     * Get name of user who will/did execute this command.
     * @return a string
     */
    String getUserName();

    /**
     * Get the command's arguments.
     * Arguments do not contain model objects that need locking.
     *
     * @return a map
     */
    Map<String, Object> getArguments();

    /**
     * Get the ids of model objects on which locks
     * must be acquired for the command to execute.
     *
     * @return a set of model objects
     */
    Set<Long> getLockingSet();

    /**
     * Get the ids of model objects that would cause a conflict in undoing/redoing
     * the command if a more recently executed command has an intersecting conflict set.
     * @return a set of ids (long)
     */
    Set<Long> getConflictSet();

    /**
     * Whether the user is allowed to execute this command.
     * Based on the states of user, command and system.
     *
     * @return a boolean
     */
    boolean isAuthorized();

    /**
     * Execute the command.
     *
     * @return an object of class T
     * @throws com.mindalliance.channels.NotFoundException if some expected model object disappeared
     */
    T execute() throws NotFoundException;

    /**
     * Whether the command can be undone.
     * @return a boolean
     */
    boolean isUndoable();

    /**
     * Produces a command that, if successfully executed, would reverse the effect of the command.
     *
     * @return a command
     * @throws CommandException if undo command can not be made
     */
    Command makeUndoCommand() throws CommandException;

}
