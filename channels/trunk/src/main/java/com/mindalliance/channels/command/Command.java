package com.mindalliance.channels.command;

import java.util.Map;
import java.util.Set;
import java.io.Serializable;

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
public interface Command<T> extends Serializable {
    /**
     * The command's name.
     *
     * @return a string
     */
    String getName();

    /**
     * Get name of user who will/did execute this command.
     *
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
     * Get the value of named argument.
     *
     * @param argumentName a string
     * @return an object
     */
    Object get( String argumentName );

    /**
     * Get the value of named argument.
     *
     * @param argumentName a string
     * @param value        an object
     */
    void set( String argumentName, Object value );

    /**
     * Get the ids of model objects on which locks
     * must be acquired for the command to execute.
     *
     * @return a set of model objects
     */
    Set<Long> getLockingSet();

    /**
     * Get the ids of model objects that would cause a conflict in undoing/redoing
     * the command if a more recently executed command has an intersecting conflict set,
     * even if all needed locks can be acquired.
     *
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
     * @param commander a commander executing the command
     * @return an object of class T
     * @throws CommandException if execution fails
     */
    T execute( Commander commander ) throws CommandException;

    /**
     * Whether the command can be undone.
     *
     * @return a boolean
     */
    boolean isUndoable();

    /**
     * Produces a command that, if successfully executed, would reverse the effect of the command.
     *
     * @param commander a commander executing the command
     * @return a command
     * @throws CommandException if undo command can not be made
     */
    Command makeUndoCommand( Commander commander ) throws CommandException;

    /**
     * Whether the command's execution should be remembered.
     *
     * @return a boolean
     */
    boolean isMemorable();

    /**
     * Set whether the command's execution should be remembered.
     *
     * @param value a boolean
     */
    void setMemorable( boolean value );

    /**
     * Preset the undo command.
     * @param command a command
     */
    void setUndoCommand( Command command );

    /**
     * Whether no lock is required by the nature of the command.
     * @return a boolean
     */
    boolean noLockRequired();

    /**
     * Set arguments.
     * @param fs attributes
     */
    void setArguments( Map<String, Object> fs );

    /**
     * Formatted description.
     * @return a string
     */
    String getTitle();
}
