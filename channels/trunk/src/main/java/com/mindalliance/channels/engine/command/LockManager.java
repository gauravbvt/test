package com.mindalliance.channels.engine.command;

import com.mindalliance.channels.core.model.Identifiable;

import java.util.Collection;
import java.util.List;

/**
 * The manager of locks on model objects.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 28, 2009
 * Time: 2:20:28 PM
 */
public interface LockManager {

    /**
     * Grab an exclusive, write lock on a model object.
     * The operation is idempotent if the lock is already grabbed by the user.
     * Return a lock or throws an exception if it failed.
     *
     * @param userName the user grabbing the lock
     * @param id a model object id
     * @return a lock or null when the id doesn't correspond to an actual object
     * @throws LockingException if lock could not be grabbed
     */
    Lock lock( String userName, long id ) throws LockingException;

    /**
     * Grab locks on all of a list of model objects.
     *
     * @param username the user grabbing the lock
     * @param ids a collection of model object ids
     * @return a collection of locks actually grabbed or upgraded
     * @throws LockingException if any of the locks could not be grabbed
     */
    List<Long> lock( String username, Collection<Long> ids ) throws LockingException;

    /**
     * Release all listed locks unconditionally, failing silently if a lock is not active.
     *
     * @param userName the user associated with the locks
     * @param ids a collection of model object ids
     * @throws LockingException if any lock was active but could not be released
     */
    void release( String userName, Collection<Long> ids ) throws LockingException;

    /**
     * Release all locks held by named user.
     *
     * @param userName a user name
     * @return a boolean -- whether any lock was released
     */
    boolean release( String userName );

    /**
     * Get name of user with a lock on model object with given id.
     *
     * @param id a model object id
     * @return a string or null if no lock on model object
     */
    String getLockUser( long id );

    /**
     * Whether all given model objects with given ids could be locked by user.
     *
     * @param userName the user
     * @param ids a collection of model object ids
     * @return a boolean
     */
    boolean isLockableByUser( String userName, Collection<Long> ids );

    /**
     * Resets lock manager.
     */
    void reset();

    /**
     * Attempt to get lock on identitifiable.
     *
     * @param username the user
     * @param id an identifiable object's id
     * @return a boolean indiciating success (true) or failure (false)
     */
    boolean requestLock( String username, Long id );

    /**
     * Attempt to release lock on identifiable, failing silently.
     *
     * @param username the user
     * @param id an identifiable's id
     * @return a boolean - whether a lock was released
     */
    boolean requestRelease( String username, Long id );

    /**
     * Test if a given id is locked.
     * @param id the id
     * @return true if locked
     */
    boolean isLocked( long id );

    /**
     * Whether user has write lock on a given model object.
     *
     * @param username the user
     * @param id a model object id
     * @return a boolean
     */
    boolean isLockedByUser( String username, long id );

    /**
     * Release a single lock.
     * @param userName the user presumed to have the lock
     * @param id the id of the locked object
     * @return true if lock was actually released
     * @throws LockingException when object is locked by another user
     */
    boolean release( String userName, long id ) throws LockingException;

    /**
     * Whether identifiable could be locked by current user.
     * @param identifiable an identifiable
     * @return a boolean
     */
    boolean isLockableByUser( Identifiable identifiable );
}
