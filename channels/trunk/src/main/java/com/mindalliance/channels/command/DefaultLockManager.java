package com.mindalliance.channels.command;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Default implementation of LockManager.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 9:33:48 AM
 */
public class DefaultLockManager implements LockManager {
    /**
     * Service.
     */
    private Service service;

    /**
     * The managed locks.
     */
    private Map<Long, Lock> locks = new HashMap<Long, Lock>();

    public DefaultLockManager() {
    }

    public void setService( Service service ) {
        this.service = service;
    }

    /**
     * {@inheritDoc}
     */
    public Lock grabLockOn( long id ) throws LockingException {
        synchronized ( this ) {
            // Throws NotFoundException is id is stale
            try {
                ModelObject mo = service.find( ModelObject.class, id );
                Lock lock = getLock( id );
                if ( lock != null ) {
                    String userName = Project.getUserName();
                    if ( !lock.isOwnedBy( userName ) ) {
                        throw new LockingException(
                                userName + " is making changes to " + mo.getName() + "." );
                    }
                    // Grab the lock
                } else {
                    lock = new Lock( id );
                    addLock( lock );
                }
                return lock;
            } catch ( NotFoundException e ) {
                throw new LockingException( "You need to refresh.", e );
            }
        }
    }

    private void addLock( Lock lock ) {
        locks.put( lock.getId(), lock );
    }

    /**
     * {@inheritDoc}
     */
    public boolean releaseLockOn( long id ) throws LockingException {
        synchronized ( this ) {
            Lock lock = getLock( id );
            if ( lock != null ) {
                String userName = Project.getUserName();
                if ( !lock.isOwnedBy( userName ) )
                    throw new LockingException(
                            userName + " does not own the lock. " + userName + " does." );
                else
                    locks.remove( id );
            }
            return lock != null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Lock> grabLocksOn( Collection<Long> ids ) throws LockingException {
        synchronized ( this ) {
            StringBuilder sb = new StringBuilder();
            List<Lock> grabbedLocks = new ArrayList<Lock>();
            for ( long id : ids ) {
                if ( !isUserLocking( id ) ) {
                    try {
                        grabbedLocks.add( grabLockOn( id ) );
                    }
                    catch ( LockingException e ) {
                        sb.append( e.getMessage() );
                        sb.append( '\n' );
                    }
                }
                String messages = sb.toString();
                if ( !messages.isEmpty() ) {
                    throw new LockingException( messages );
                }
            }
            return grabbedLocks;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void releaseLocks( Collection<Lock> locksToRelease ) throws LockingException {
        synchronized ( this ) {
            for ( Lock lock : locksToRelease ) {
                locks.remove( lock.getId() );
            }
        }
    }

    /**
     * Whether user has write lock on a given model object.
     *
     * @param id a model object id
     * @return a boolean
     */
    public boolean isUserLocking( long id ) {
        synchronized ( this ) {
            Lock lock = getLock( id );
            return lock != null && lock.isOwnedBy( Project.getUserName() );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void releaseAllLocks( String userName ) {
        for ( Lock lock : getAllLocks( userName ) ) {
            locks.remove( lock.getId() );
        }
    }

    /**
     * Get name of user with a lock on model object with given id.
     *
     * @param id a model object id
     * @return a string or null if no lock on model object
     */
    public String getLockOwner( long id ) {
        String owner = null;
        Lock lock = getLock( id );
        if ( lock != null ) owner = lock.getUserName();
        return owner;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLocked( long id ) {
        return getLock( id ) != null;
    }

    /**
     * {@inheritDoc}
     */
    public Lock getLock( long id ) {
        Lock lock = locks.get( id );
        if ( lock != null ) {
            try {
                service.find( ModelObject.class, id );
            }
            catch ( NotFoundException e ) {
                // Clean up obsolete lock
                locks.remove( id );
                lock = null;
            }
        }
        return lock;
    }

    /**
     * {@inheritDoc}
     */
    public List<Lock> getAllLocks( String userName ) {
        List<Lock> userLocks = new ArrayList<Lock>();
        for ( long id : locks.keySet() ) {
            Lock lock = getLock( id );
            if ( lock != null && lock.isOwnedBy( userName ) )
                userLocks.add( lock );
        }
        return userLocks;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canGrabLocksOn( Collection<Long> ids ) {
        String userName = Project.getUserName();
        for ( long id : ids ) {
            Lock lock = getLock( id );
            if ( lock != null && !lock.isOwnedBy( userName ) )
                return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
        locks = new HashMap<Long, Lock>();
    }
}
