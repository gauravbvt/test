package com.mindalliance.channels.command;

import com.mindalliance.channels.Service;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;

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
     * @throws CommandException if execution could not proceeed or failed.
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
     * Get a service
     *
     * @return a service
     */
    Service getService();

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
     * @throws NotFoundException if not found
     */
    <T extends ModelObject> T resolve( Class<T> clazz, Long id ) throws NotFoundException;

    /**
     * Resolves an id.
     *
     * @param id a long
     * @return a long
     * @throws NotFoundException if resolution fails
     */
    Long resolveId( Long id ) throws NotFoundException;

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

}
