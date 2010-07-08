package com.mindalliance.channels.command;

import com.mindalliance.channels.dao.JournalCommand;

import java.util.Map;
import java.util.Set;

/**
 * A command that can be done, undone and redone.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 28, 2009
 * Time: 2:20:01 PM
 */
public interface Command extends JournalCommand {
    /**
     * The command's name.
     *
     * @return a string
     */
    String getName();

    /**
     * Get name of command being undone by this one (if any).
     *
     * @return a string or null
     */
    String getUndoes();
    /**
     * Set name of command being undone by this one.
     *
     * @param name a string
     */
    void setUndoes( String name );

    /**
     * Get name of user who will/did execute this command.
     *
     * @return a string
     */
    String getUserName();

    /**
     * Get preserved description of actual target..
     *
     * @return a string
     */
    String getTargetDescription();

    /**
     * Set a preserved target description.
     *
     * @param subjectDescription a string
     */
    void setTargetDescription( String subjectDescription );


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
     * @return cuased change
     * @throws CommandException if execution fails
     */
    Change execute( Commander commander ) throws CommandException;

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
    Command getUndoCommand( Commander commander ) throws CommandException;

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
     * Whether no lock is required by the nature of the command.
     *
     * @return a boolean
     */
    boolean noLockRequired();

    /**
     * Set arguments.
     *
     * @param fs attributes
     */
    void setArguments( Map<String, Object> fs );

    /**
     * Formatted description.
     *
     * @return a string
     */
    String getTitle();

    /**
     * Return the name of the command it undoes.
     *
     * @param commander a commander
     * @return a string
     */
    String getUndoes( Commander commander );

    /**
     * Whether the command modifies the contents of a plan segment.
     *
     * @return a boolean
     */
    boolean isSegmentSpecific();

    /**
     * Can execute irrespective of locking.
     *
     * @param commander a commander
     * @return a boolean
     */
    boolean canDo( Commander commander );

    /**
     * Not a sub-command,
     *
     * @return a boolean
     */
    boolean isTop();

    /**
     * Whether a command is top command.
     *
     * @param val a boolean
     */
    void setTop( boolean val );

    /**
     * Get label for command.
     * May depend on the state of the command.
     *
     * @param commander a commander
     * @return a string
     * @throws CommandException if the label can not be constructed
     */
    String getLabel( Commander commander ) throws CommandException;
}
