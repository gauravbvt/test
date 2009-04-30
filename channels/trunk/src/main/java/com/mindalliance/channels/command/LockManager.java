package com.mindalliance.channels.command;


import com.mindalliance.channels.model.Identifiable;

import java.util.Collection;

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
     * @param id a model object id
     * @return a lock
     * @throws LockingException if lock could not be grabbed
     */
    Lock grabLockOn( long id ) throws LockingException;

    /**
     * Release the user's lock on a model object.
     * Does nothing if the lock is not active.
     *
     * @param id a model object id
     * @return whether the lock needed to be released.
     * @throws LockingException if the lock was active but could not be released
     */
    boolean releaseLockOn( long id ) throws LockingException;

    /**
     * Grab locks on all of a list of model objects.
     *
     * @param ids a collection of model object ids
     * @return a collection of locks actually grabbed or upgraded
     * @throws LockingException if any of the locks could not be grabbed
     */
    Collection<Lock> grabLocksOn( Collection<Long> ids ) throws LockingException;

    /**
     * Release all listed locks unconditionally, failing silently if a lock is not active.
     *
     * @param locks a collection of locks on model objects
     * @throws LockingException if any lock was active but could not be released
     */
    void releaseLocks( Collection<Lock> locks ) throws LockingException;

    /**
     * Whether user has write lock on a given model object.
     *
     * @param id a model object id
     * @return a boolean
     */
    boolean isUserLocking( long id );


    /**
     * Release all locks held by named user.
     *
     * @param userName a user name
     * @return a boolean -- whether any lock was released
     */
    boolean releaseAllLocks( String userName );

    /**
     * Get name of user with a lock on model object with given id.
     *
     * @param id a model object id
     * @return a string or null if no lock on model object
     */
    String getLockOwner( long id );

    /**
     * Get the user's lock on a model object.
     *
     * @param id a model object id
     * @return a lock or null if none
     */
    Lock getLock( long id );

    /**
     * Get all locks on all model objects for a user.
     *
     * @param userName a user's name
     * @return a collection of locks, possibly empty
     */
    Collection<Lock> getAllLocks( String userName );

    /**
     * Whether all given model objects with given ids could be locked by user.
     *
     * @param ids a collection of model object ids
     * @return a boolean
     */
    boolean canGrabLocksOn( Collection<Long> ids );

    /**
     * Resets lock manager
     */
    void reset();

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
     * @param id an identifiable object's id
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
}
