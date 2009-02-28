package com.mindalliance.channels.command;

import com.mindalliance.channels.ModelObject;

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
     * Return a lock or null if it failed.
     *
     * @param isWrite     a boolean
     * @param modelObject a model object
     * @return a lock or null
     */
    Lock grabLock( boolean isWrite, ModelObject modelObject );

    /**
     * Release a lock.
     * Does nothing if the lock is not active.
     *
     * @param lock a lock
     */
    void releaseLock( Lock lock );

    /**
     * Whether a given lock in active.
     *
     * @param lock a lock
     * @return a boolean
     */
    boolean isActive( Lock lock );

    /**
     * Get the user's lock on a model object.
     *
     * @param modelObject a model object
     * @return a lock or null
     */
    Lock getLock( ModelObject modelObject );

    /**
     * Get all locks on a model object from all users.
     *
     * @param modelObject a model object
     * @return a list of locks
     */
    List<Lock> getAllLocks( ModelObject modelObject );

    /**
     * Get all locks on all model objects for a user.
     *
     * @param userName a user's name
     * @return a list of locks
     */
    List<Lock> getAllLocks( String userName );
}
