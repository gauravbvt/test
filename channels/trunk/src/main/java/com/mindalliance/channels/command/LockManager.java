package com.mindalliance.channels.command;

import com.mindalliance.channels.NotFoundException;

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
     * Grab a write or read lock on a model object.
     * Multiple read locks can be grabbed on a model object if there is no active write lock on it.
     * Only one write lock can be grabbed, and only if there are no read locks
     * Return a lock or throws an exception if it failed.
     *
     * @param isWrite     a boolean
     * @param id a model object id
     * @return a lock
     * @throws LockException if lock could not be grabbed
     * @throws com.mindalliance.channels.NotFoundException if model object with id not found
     */
    Lock grabLock( boolean isWrite, long id ) throws LockException, NotFoundException;

    /**
     * Release a lock.
     * Does nothing if the lock is not active.
     *
     * @param lock a lock
     */
    void releaseLock( Lock lock );

    /**
     * Grab locks on all of a list of model objects.
     * @param isWrite a boolean
     * @param ids a list of model object ids
     * @return a list of locks
     * @throws LockException if any of the locks could not be grabbed
     * @throws com.mindalliance.channels.NotFoundException if model object with id not found
     */
    List<Lock> grabLocks( boolean isWrite, List<Long> ids ) throws LockException, NotFoundException;

    /**
     * Release all listed locks, failing silently if a lock is not active.
     * @param locks a list of locks
     */
    void releaseLocks( List<Lock> locks );

    /**
     * Whether user has read lock on a given model object.
     * @param id a model object id
     * @return a boolean
     */
    boolean canRead( long id );

    /**
     * Whether user has write lock on a given model object.
     * @param id a model object id
     * @return a boolean
     */
    boolean canWrite( long id );


    /**
     * Release all locks held by named user.
     * @param userName a user name
     */
    void releaseAllLocks( String userName );

    /**
     * Whether a given lock is active.
     *
     * @param lock a lock
     * @return a boolean
     */
    boolean isActive( Lock lock );

    /**
     * Get the user's lock on a model object.
     *
     * @param id a model object id
     * @return a lock or null if none
     */
    Lock getLock( long id );

    /**
     * Get all locks on a model object from all users.
     *
     * @param ids a list of model object ids
     * @return a list of locks, possibly empty
     */
    List<Lock> getAllLocks( long ids );

    /**
     * Get all locks on all model objects for a user.
     *
     * @param userName a user's name
     * @return a list of locks, possibly empty
     */
    List<Lock> getAllLocks( String userName );

}
