/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.query.QueryService;
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
 */
public class DefaultLockManager implements LockManager {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultLockManager.class );

    /**
     * Query service.
     */
    private final QueryService queryService;

    /**
     * The managed locks, indexed by id.
     */
    private final Map<Long, Lock> locks = Collections.synchronizedMap( new HashMap<Long, Lock>() );

    public DefaultLockManager( QueryService queryService ) {
        this.queryService = queryService;
    }

    @Override
    public Lock lock( String userName, long id ) throws LockingException {
        try {
            ModelObject mo = queryService.find( ModelObject.class, id );

            Lock lock;
            synchronized ( locks ) {
                lock = locks.get( id );
                if ( lock == null ) {
                    lock = new Lock( userName, id );
                    locks.put( id, lock );

                } else if ( !userName.equals( lock.getUserName() ) )
                    throw new LockingException(
                            userName + " can't lock " + mo.getName() + ": it is locked by "
                                    + lock.getUserName() );
            }

            LOG.debug( "{} locks {}", userName, id );
            return lock;

        } catch ( NotFoundException ignored ) {
            LOG.debug( "Could not lock: {} not found (likely deleted by prior subcommand)", id );
            locks.remove( id );
            return null;
        }
    }

    @Override
    public List<Long> lock( String username, Collection<Long> ids ) throws LockingException {
        StringBuilder sb = new StringBuilder();
        List<Long> grabbedLocks = new ArrayList<Long>();

        synchronized ( locks ) {
            for ( long id : ids )
                if ( !isLockedByUser( username, id ) )
                    try {
                        Lock lock = lock( username, id );
                        if ( lock != null )
                            grabbedLocks.add( id );
                    } catch ( LockingException e ) {
                        sb.append( e.getMessage() );
                        sb.append( System.getProperty( "line.separator" ) );
                    }
        }

        String messages = sb.toString();
        if ( !messages.isEmpty() ) {
            release( username, grabbedLocks );
            throw new LockingException( messages );
        }

        return grabbedLocks;
    }

    @Override
    public boolean release( String userName, long id ) throws LockingException {

        synchronized ( locks ) {
            Lock lock = getLock( id );
            if ( lock == null )
                return false;

            if ( !userName.equals( lock.getUserName() ) )
                throw new LockingException(
                        userName + " does not own the lock. " + userName + " does." );

            locks.remove( id );
        }

        LOG.debug( "{} releases lock on {}", userName, id );
        return true;
    }

    @Override
    public void release( String userName, Collection<Long> ids ) throws LockingException {
        for ( Long id : ids )
            release( userName, id );
    }

    @Override
    public boolean release( String userName ) {
        List<Long> released = new ArrayList<Long>();

        synchronized ( locks ) {
            for ( Lock lock : new ArrayList<Lock>( locks.values() ) )
                if ( lock != null && userName.equals( lock.getUserName() ) ) {
                    locks.remove( lock.getId() );
                    released.add( lock.getId() );
                }
        }

        if ( LOG.isInfoEnabled() )
            for ( Long id : released )
                LOG.info( "{} releases lock on {}", userName, id );

        return !released.isEmpty();
    }

    @Override
    public boolean isLocked( long id ) {
        return getLock( id ) != null;
    }

    /**
     * Get name of user with a lock on model object with given id.
     *
     * @param id a model object id
     * @return a string or null if no lock on model object
     */
    @Override
    public String getLockUser( long id ) {
        Lock lock = getLock( id );
        return lock == null ? null : lock.getUserName();
    }

    private Lock getLock( long id ) {
        Lock lock = locks.get( id );
        if ( lock != null )
            try {
                queryService.find( ModelObject.class, id );
            } catch ( NotFoundException ignored ) {
                // Clean up obsolete lock
                locks.remove( id );
                lock = null;
            }

        return lock;
    }

    @Override
    public boolean isLockableByUser( String userName, Identifiable identifiable ) {
        List<Long> ids = new ArrayList<Long>();
        ids.add( identifiable.getId() );
        return isLockableByUser( userName, ids );
    }


    @Override
    public boolean isLockableByUser( String userName, Collection<Long> ids ) {
        for ( long id : ids ) {
            Lock lock = getLock( id );
            if ( lock != null && !userName.equals( lock.getUserName() ) )
                return false;
        }
        return true;
    }

    @Override
    public void reset() {
        locks.clear();
    }

    @Override
    public boolean isLockedByUser( String username, long id ) {
        Lock lock = getLock( id );
        return lock != null && username.equals( lock.getUserName() );
    }

    @Override
    public boolean requestLock( String username, Long id ) {
        try {
            return lock( username, id ) != null;

        } catch ( LockingException ignored ) {
            LOG.info( "Failed to grab lock on {}", id );
            return false;
        }
    }

    @Override
    public boolean requestRelease( String username, Long id ) {
        try {
            release( username, id );
            return true;

        } catch ( LockingException ignored ) {
            LOG.info( "Failed to release lock on {}", id );
            return false;
        }
    }

}
