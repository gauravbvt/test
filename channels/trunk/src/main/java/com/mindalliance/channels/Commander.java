package com.mindalliance.channels;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Identifiable;

import java.util.Map;

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
     * @return an change
     * @throws com.mindalliance.channels.command.CommandException if execution could not proceeed or failed.
     */
    Change doCommand( Command command ) throws CommandException;

    /**
     * Whether user could undo a previous command right now.
     *
     * @return a boolean
     */
    boolean canUndo();

    /**
     * Whether user could redo an undone command right now.
     *
     * @return a boolean
     */
    boolean canRedo();

    /**
     * Undo user's previous command.
     *
     * @return a change
     * @throws CommandException if undoing fails or is not allowed.
     */
    Change undo() throws CommandException;

    /**
     * Redo user's previous undone command.
     *
     * @return a change
     * @throws CommandException if redoing fails or is not allowed.
     */
    Change redo() throws CommandException;

    /**
     * Resets commander
     */
    void reset();

    /**
     * Get a query service
     *
     * @return a query service
     */
    QueryService getQueryService();

    /**
     * Resets history for current user.
     *
     * @param userName a string
     */
    void resetUserHistory( String userName );

    /**
     * Get label for undo command.
     *
     * @return a string
     */
    String getUndoTitle();

    /**
     * Get label for redo command.
     *
     * @return a string
     */
    String getRedoTitle();

    /**
     * Set id translation map.
     *
     * @param idMap an id translation map
     */
    void setIdMap( Map<Long, Long> idMap );

    /**
     * Find a model object from its id, possibly resolving the id first with idMap.
     *
     * @param clazz a model object class
     * @param id    a long
     * @return a model object
     * @throws CommandException if not found
     */
    <T extends ModelObject> T resolve( Class<T> clazz, Long id ) throws CommandException;
    
    /**
     * Resolves an id.
     *
     * @param id a long
     * @return a long
     * @throws CommandException if resolution fails
     */
    Long resolveId( Long id ) throws CommandException;

    /**
     * Map id translation for replay.
     * @param oldId a Long or null
     * @param newId a Long
     */
    void mapId( Long oldId, Long newId );

    /**
     * Whether commander is in journal replay mode.
     * @return a boolean
     */
    boolean isReplaying();

    /**
     * Turns on/off journal replay mode.
     * @param val a boolean
     */
    void setReplaying( boolean val );

    /**
     * Remove entity with old name if not referenced and if not defined.
     *
     * @param clazz  a model object class
     * @param name a string
     */
    void cleanup( Class<? extends ModelObject> clazz, String name );

    /**
     * Whether someone other than the user has a lock on the model object with given id.
     *
     * @param identifiable an identifiable
     * @return a boolean
     */
    boolean isLockedByUser( Identifiable identifiable );

    /**
     * Attempt to get lock on identitifiable
     *
     * @param identifiable an identifiable object
     * @return a boolean indiciating success (true) or failure (false)
     */
    boolean requestLockOn( Identifiable identifiable );

    /**
     * Attempt to get lock on identitifiable
     *
     * @param id an identifiable's id
     * @return a boolean indiciating success (true) or failure (false)
     */
    boolean requestLockOn( Long id );

    /**
     * Attempt to release lock on identifiable, failing silently.
     *
     * @param identifiable an identifiable
     * @return a boolean - whether a lock was released
     */
    boolean releaseAnyLockOn( Identifiable identifiable );

    /**
      * Attempt to release lock on identifiable, failing silently.
      *
      * @param id an identifiable's id
      * @return a boolean - whether a lock was released
      */
     boolean releaseAnyLockOn( Long id );

    /**
     * Release all locks held by named user.
     *
     * @param userName a user name
     */
    void releaseAllLocks( String userName );

    /**
     * System time when last modification was made.
     * @return a long
     */
    long getLastModified();

    /**
     * Get the name of the last user to have made a change.
     * @return a string
     */
    String getLastModifier();

    /**
     * Check for and process user inactivity timeouts.
     */
    void processTimeOuts();

    /**
     * Is current user timed out?
     * @return a boolean
     */
    boolean isTimedOut();

    /**
     * Clear timeout notice for current user.
     */
    void clearTimeOut();

    /**
     * Whether the model object is not locked.
     * @param mo a model object
     * @return a boolean
     */
    boolean isUnlocked( ModelObject mo );

    /**
     * Remember state of part or flow.
     * @param state a map
     */
    void setCopy( Map<String, Object> state );

    /**
     * Get copied state of part or flow.
     * Null if none copied.
     * @return a map
     */
    Map<String, Object> getCopy();

    /**
     * Is a part copied?
     * @return a boolean
     */
    boolean isPartCopied();

    /**
     * Is a flow copied?
     * @return a boolean
     */
    boolean isFlowCopied();
}
