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
     * Get the command's arguments.
     * Arguments do not contain model objects that need locking.
     *
     * @return a map
     */
    Map<String, Object> getArguments();

    /**
     * Calculate the list of all ids of model object on which write locks
     * must be acquired for the command to execute.
     *
     * @return a set of model objects
     */
    Set<Long> getLockingSet();

    /**
     * Whether the user is allowed to execute this command.
     * Based on the states of user, command and system.
     *
     * @return a boolean
     */
    boolean isAuthorized();

    /**
     * Execute the command given arguments.
     *
     * @return an object of class T
     * @throws com.mindalliance.channels.NotFoundException if some expected model object disappeared
     */
    T execute() throws NotFoundException;

    /**
     * Produces a command that, if successfully executed, would reverse the effect of the command.
     *
     * @return a command
     */
    Command makeUndoCommand();

}
