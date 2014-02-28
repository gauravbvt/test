/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.community.CommunityDao;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.AbstractModelObjectDao;
import com.mindalliance.channels.core.dao.Exporter;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.Journal;
import com.mindalliance.channels.core.dao.ModelDao;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.core.query.QueryService;

import java.util.Map;

/**
 * A command execution controller. A commander serializes command executions.
 */
public interface Commander {

    /**
     * Whether the command could be executed right now. The user is authorized and all required locks could be taken.
     *
     * @param command a command
     * @return a boolean
     */
    boolean canDo( Command command );

    /**
     * Executes a command on behalf of the user. Locks are grabbed
     *
     * @param command a command
     * @return an change
     */
    Change doCommand( Command command );

    /**
     * Executes a command on behalf of the user, even in production. Locks are grabbed
     *
     * @param command a command
     * @return an change
     */
    Change doUnsafeCommand( Command command );

    /**
     * Whether user could undo a previous command right now.
     *
     * @param userName the current user
     * @return a boolean
     */
    boolean canUndo( String userName );

    /**
     * Whether user could redo an undone command right now.
     *
     * @param userName the user
     * @return a boolean
     */
    boolean canRedo( String userName );

    /**
     * Undo user's previous command.
     *
     * @param userName the current user
     * @return a change
     */
    Change undo( String userName );

    /**
     * Redo user's previous undone command.
     *
     * @param userName the current user
     * @return a change
     */
    Change redo( String userName );

    /**
     * Resets commander.
     */
    void reset();

    /**
     * Get a query service.
     *
     * @return a query service
     */
    QueryService getQueryService();

    /**
     * Resets history for current user.
     *
     * @param userName a string
     * @param all if true include segment-specific commands
     */
    void resetUserHistory( String userName, boolean all );

    /**
     * Get label for undo command.
     *
     * @param userName the current user
     * @return a string
     */
    String getUndoTitle( String userName );

    /**
     * Get label for redo command.
     *
     * @param userName the current user
     * @return a string
     */
    String getRedoTitle( String userName );

    /**
     * Find a model object from its id, possibly resolving the id first with idMap.
     *
     * @param clazz a model object class
     * @param id a long or int
     * @return a model object
     * @throws CommandException if not found
     */

    <T extends ModelObject> T resolve( Class<T> clazz, Long id ) throws CommandException;

    /**
     * Whether commander is in journal replay mode.
     *
     * @return a boolean
     */
    boolean isReplaying();

    /**
     * Turns on/off journal replay mode.
     *
     * @param replaying a boolean
     */
    void setReplaying( boolean replaying );

    /**
     * Remove entity with old name if not referenced and if not defined.
     *
     * @param clazz a model object class
     * @param name a string
     * @return a boolean - true if the entity was deleted
     */
    boolean cleanup( Class<? extends ModelObject> clazz, String name );

    /**
     * Whether someone other than the user has a lock on the model object with given id.
     *
     * @param userName the current user
     * @param identifiable an identifiable
     * @return a boolean
     */
    boolean isLockedByUser( String userName, Identifiable identifiable );

    /**
     * Attempt to get lock on identifiable.
     *
     * @param userName the current user
     * @param identifiable an identifiable object
     * @return a boolean indiciating success (true) or failure (false)
     */
    boolean requestLockOn( String userName, Identifiable identifiable );

    /**
     * Attempt to get lock on identifiable.
     *
     * @param userName the current user
     * @param id an identifiable's id
     * @return a boolean indicating success (true) or failure (false)
     */
    boolean requestLockOn( String userName, Long id );

    /**
     * Attempt to release lock on identifiable, failing silently.
     *
     * @param userName the current user
     * @param identifiable an identifiable
     * @return a boolean - whether a lock was released
     */
    boolean releaseAnyLockOn( String userName, Identifiable identifiable );

    /**
     * Attempt to release lock on identifiable, failing silently.
     *
     * @param userName the current user
     * @param id an identifiable's id
     * @return a boolean - whether a lock was released
     */
    boolean releaseAnyLockOn( String userName, Long id );

    /**
     * Release all locks held by named user.
     *
     * @param userName a user name
     */
    void releaseAllLocks( String userName );

    /**
     * System time when last modification was made.
     *
     * @return a long
     */
    long getLastModified();

    /**
     * Get the name of the last user to have made a change.
     *
     * @return a string
     */
    String getLastModifier();

    /**
     * Keep current user alive.
     *
     * @param userName a string
     * @param refreshDelay a number of seconds between pings
     */
    void keepAlive( String userName, int refreshDelay );

    /**
     * Mark current user as active.
     *
     * @param userName the current user
     */
    void updateUserActive( String userName );

    /**
     * Check for and process user inactivity timeouts.
     */
    void processTimeOuts();

    /**
     * Is current user timed out?
     *
     * @param userName the current user
     * @return a boolean
     */
    boolean isTimedOut( String userName );

    /**
     * Clear timeout notice for current user.
     *
     * @param userName the current user
     */
    void clearTimeOut( String userName );

    /**
     * Whether the model object is not locked.
     *
     * @param mo a model object
     * @return a boolean
     */
    boolean isUnlocked( ModelObject mo );

    /**
     * Remember state of part or flow.
     *
     * @param userName the current user
     * @param state a map
     */
    void setCopy( String userName, Map<String, Object> state );

    /**
     * Get copied state of part or flow. Null if none copied.
     *
     * @param userName the current user
     * @return a map
     */
    Map<String, Object> getCopy( String userName );

    /**
     * Is a part copied?
     *
     * @param userName the current user
     * @return a boolean
     */
    boolean isPartCopied( String userName );

    /**
     * Is a flow copied?
     *
     * @param userName the current user
     * @return a boolean
     */
    boolean isFlowCopied( String userName );

    /**
     * Is aan attachment copied?
     *
     * @param userName the current user
     * @return a boolean
     */
    boolean isAttachmentCopied( String userName );

    /**
     * Replay all commands in a journal.
     *
     * @param journal the journal
     * @throws CommandException on errors
     */
    void replay( Journal journal ) throws CommandException;

    /**
     * All users are out of sync with plan versions.
     */
    void setPlanResyncRequired();

    /**
     * Whether the current user us out of sync with plan versions.
     *
     * @param userName the current user
     * @return a boolean
     */
    boolean isPlanOutOfSync( String userName );

    /**
     * Signal that the current user is in sync with the plan versions.
     *
     * @param userName the current user
     */
    void resynced( String userName );

    /**
     * Get a proper exporter.
     *
     * @param userName the current user
     * @return an exporter
     */
    Exporter getExporter( String userName );

    /**
     * Initialize the commander when ready to go.
     */
    void initialize();

    AbstractModelObjectDao getDao();

    /**
     * Whether instances of class of given name can be locked.
     *
     * @param className a string
     * @return a boolean
     */
    boolean isLockable( String className );

    ImportExportFactory getImportExportFactory();

    /**
     * User left a plan.
     *
     * @param username a string
     */
    void userLeftCommunity( String username );

    /**
     * Make the appropriate command to remove a flow.
     *
     * @param userName the current user
     * @param flow a flow
     * @return a boolean
     */
    Command makeRemoveFlowCommand( String userName, Flow flow );

    /**
     * Get the lock manager used by this commander.
     * @return a lock manager
     */
    LockManager getLockManager();

    PlanCommunity getPlanCommunity();

    void setCommunityService( CommunityService communityService );

    ModelService getPlanService();

    CollaborationModel getPlan();

    ModelDao getPlanDao();

    CommunityDao getCommunityDao();

    CommunityService getCommunityService();

}
