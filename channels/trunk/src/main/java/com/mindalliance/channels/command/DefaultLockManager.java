package com.mindalliance.channels.command;

import com.mindalliance.channels.AbstractService;
import com.mindalliance.channels.LockManager;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of LockManager.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 9:33:48 AM
 */
public class DefaultLockManager extends AbstractService implements LockManager {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultLockManager.class );
    /**
     * Query service.
     */
    private QueryService queryService;

    /**
     * The managed locks.
     */
    private Map<Long, Lock> locks = Collections.synchronizedMap( new HashMap<Long, Lock>() );
    /**
     * Dirty locks to be removed.
     */
    private List<Lock> dirtyLocks = new ArrayList<Lock>();

    public DefaultLockManager() {
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    /**
     * {@inheritDoc}
     */
    public Lock grabLockOn( long id ) throws LockingException {
        synchronized ( this ) {
            // Warn if id is stale but don't fail since the model object is gone so there can be no conflict.
            try {
                ModelObject mo = queryService.find( ModelObject.class, id );
                Lock lock = getLock( id );
                if ( lock != null ) {
                    String userName = User.current().getUsername();
                    if ( !lock.isOwnedBy( userName ) ) {
                        throw new LockingException(
                                userName + " is making changes to " + mo.getName() + "." );
                    }
                    // Grab the lock
                } else {
                    lock = new Lock( id );
                    LOG.debug( lock.getUserName() + " grabs lock on " + id);
                    addLock( lock );
                }
                return lock;
            } catch ( NotFoundException e ) {
                LOG.warn( "Could not grab lock: " + id + " not found");
                return null;
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
                String userName = User.current().getUsername();
                if ( !lock.isOwnedBy( userName ) )
                    throw new LockingException(
                            userName + " does not own the lock. " + userName + " does." );
                else {
                    LOG.debug( lock.getUserName() + " releases lock on " + id);
                    locks.remove( id );
                }
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
                        Lock lock = grabLockOn( id );
                        if (lock != null) grabbedLocks.add( lock );
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
                LOG.info(lock.getUserName() + " releases lock on " + lock.getId());
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
        Lock lock = getLock( id );
        return lock != null && lock.isOwnedBy( User.current().getUsername() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean releaseAllLocks( String userName ) {
        synchronized ( this ) {
            removeDirtyLocks();
            List<Lock> locksToRelease = getAllLocks( userName );
            for ( Lock lock : locksToRelease ) {
                LOG.info(userName + " releases lock on " + lock.getId());
                locks.remove( lock.getId() );
            }
            return !locksToRelease.isEmpty();
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
                queryService.find( ModelObject.class, id );
            }
            catch ( NotFoundException e ) {
                // Clean up obsolete lock
                dirtyLocks.add( lock );
                lock = null;
            }
        }
        return lock;
    }

    private void removeDirtyLocks() {
        for ( Lock lock : dirtyLocks ) {
            locks.remove( lock.getId() );
        }
        dirtyLocks = new ArrayList<Lock>();
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
        String userName = User.current().getUsername();
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

    /**
     * {@inheritDoc}
     */
    public boolean isLockedByUser( Identifiable identifiable ) {
        return isUserLocking( identifiable.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean requestLockOn( Identifiable identifiable ) {
        return requestLockOn ( identifiable.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean requestLockOn( Long id ) {
        boolean locked = false;
        try {
            locked = grabLockOn( id ) != null;
        } catch ( LockingException e ) {
            LOG.info( "Failed to grab lock on " + id );
        }
        return locked;
    }

    /**
     * {@inheritDoc}
     */
    public boolean releaseAnyLockOn( Identifiable identifiable ) {
        return releaseAnyLockOn( identifiable.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean releaseAnyLockOn( Long id ) {
        boolean unlocked = false;
        try {
            releaseLockOn( id );
            unlocked = true;
        } catch ( LockingException e ) {
            LOG.info( "Failed to release lock on " + id );
        }
        return unlocked;
    }

}
