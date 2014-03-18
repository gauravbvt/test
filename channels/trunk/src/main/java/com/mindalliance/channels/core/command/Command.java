/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.dao.JournalCommand;

import java.util.Map;
import java.util.Set;

/**
 * A command that can be done, undone and redone.
 */
public interface Command extends JournalCommand {

    /**
     * Can execute irrespective of locking.
     *
     * @param commander a commander
     * @return a boolean
     */
    boolean canDo( Commander commander );

    /**
     * Execute the command.
     *
     * @param commander a commander executing the command
     * @return caused change
     * @throws CommandException if execution fails
     */
    Change execute( Commander commander ) throws CommandException;

    /**
     * Get the value of named argument.
     *
     * @param argumentName a string
     * @return an object
     */
    Object get( String argumentName );

    /**
     * Get the command's arguments. Arguments do not contain model objects that need locking.
     *
     * @return a map
     */
    Map<String, Object> getArguments();

    /**
     * Get the ids of model objects that would cause a conflict in undoing/redoing the command if a more recently
     * executed command has an intersecting conflict set, even if all needed locks can be acquired.
     *
     * @return a set of ids (long)
     */
    Set<Long> getConflictSet();

    /**
     * Get label for command. May depend on the state of the command.
     *
     * @param commander a commander
     * @return a string
     * @throws CommandException if the label can not be constructed
     */
    String getLabel( Commander commander ) throws CommandException;

    /**
     * Get the ids of model objects on which locks must be acquired for the command to execute.
     *
     * @return a set of model objects
     */
    Set<Long> getLockingSet();

    /**
     * The command's name.
     *
     * @return a string
     */
    String getName();

    /**
     * Get preserved description of actual target..
     *
     * @return a string
     */
    String getTargetDescription();

    /**
     * Formatted description.
     *
     * @return a string
     */
    String getTitle();

    /**
     * Produces a command that, if successfully executed, would reverse the effect of the command.
     *
     * @param commander a commander executing the command
     * @return a command
     * @throws CommandException if undo command can not be made
     */
    Command getUndoCommand( Commander commander ) throws CommandException;

    /**
     * Get name of command being undone by this one (if any).
     *
     * @return a string or null
     */
    String getUndoes();

    /**
     * Return the name of the command it undoes.
     *
     * @param commander a commander
     * @return a string
     */
    String getUndoes( Commander commander );

    /**
     * Get name of user who will/did execute this command.
     *
     * @return a string
     */
    String getUserName();

    /**
     * Whether the user is allowed to execute this command. Based on the states of user, command and system.
     *
     * @return a boolean
     */
    boolean isAuthorized();

    /**
     * Whether the command modifies the contents of a segment.
     *
     * @return a boolean
     */
    boolean isSegmentSpecific();

    /**
     * Not a sub-command.
     *
     * @return a boolean
     */
    boolean isTop();

    /**
     * Whether the command can be undone.
     *
     * @return a boolean
     */
    boolean isUndoable();

    /**
     * Set the command's undoability.
     * Effective only if property is read/write (MultiCommands only)
     * @param val a boolean
     */
    void makeUndoable( boolean val );

    /**
     * Whether no lock is required by the nature of the command.
     *
     * @return a boolean
     */
    boolean noLockRequired();

    /**
     * Get the value of named argument.
     *
     * @param argumentName a string
     * @param value an object
     */
    void set( String argumentName, Object value );

    /**
     * Set arguments.
     *
     * @param fs attributes
     */
    void setArguments( Map<String, Object> fs );

    /**
     * Set whether the command's execution should be remembered.
     *
     * @param memorable a boolean
     */
    void setMemorable( boolean memorable );

    /**
     * Set a preserved target description.
     *
     * @param subjectDescription a string
     */
    void setTargetDescription( String subjectDescription );

    /**
     * Whether a command is top command.
     *
     * @param top a boolean
     */
    void setTop( boolean top );

    /**
     * Set name of command being undone by this one.
     *
     * @param name a string
     */
    void setUndoes( String name );

    /**
     * Whether execution of this command triggers after command execution processing.
     * @return a boolean
     */
    boolean triggersAfterCommand();
}
