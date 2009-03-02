package com.mindalliance.channels.command;

import java.util.List;
import java.util.Map;

/**
 * A command that can be done, undone and redone.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 28, 2009
 * Time: 2:20:01 PM
 * @param <T> the class of the return value of executing a command
 */
public interface Command<T> {
    /**
     * The command's name.
     * @return a string
     */
    String getName();

    /**
     * Whether the user is allowed to execute this command.
     * Based on the states of user, command and system.
     * @return a boolean
     */
    boolean isExecutionAllowed();

    /**
     * Execute the command given arguments.
     * @param args Arguments in addition to the model objects that need locks
     * @return an object of class T
     * @throws CommandException if the execution failed
     */
    T execute( Map<String, Object> args ) throws CommandException;

     /**
     * Get the list of all ids of model object on which read locks
     * must be acquired for the command to execute.
     * @return a list of model objects
     */
    List<Long> getRead();

    /**
     * Get the list of all ids of model object on which write locks
     * must be acquired for the command to execute.
     * @return a list of model objects
     */
    List<Long> getWritten();

    /**
      * Produces a command that, if successfully executed, would reverse the effect of the command.
      * @return a command
      */
     Command makeUndoCommand();

}
