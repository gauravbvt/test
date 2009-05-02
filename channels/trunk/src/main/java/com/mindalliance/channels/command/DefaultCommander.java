package com.mindalliance.channels.command;

import com.mindalliance.channels.AbstractService;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.LockManager;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 1:47:58 PM
 */
public class DefaultCommander  extends AbstractService implements Commander {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultCommander.class );
    /**
     * Lock manager.
     */
    private LockManager lockManager;
    /**
     * Done and undone history.
     */
    private History history = new History();
    /**
     * Query service.
     */
    private QueryService queryService;

    private boolean replaying = false;
    /**
     * An id translation map.
     */
    // TODO - this could grow unchecked
    private Map<Long, Long> idMap = Collections.synchronizedMap(new HashMap<Long, Long>());
    /**
     * Record of when users were most recently active.
     */
    private Map<String, Long> whenLastActive = Collections.synchronizedMap(new HashMap<String, Long>());
    /**
     * Users who timed out but have yet to be refreshed.
     */
    private Set<String> timedOut = Collections.synchronizedSet(new HashSet<String>());
    /**
     * Default timeout period  in seconds = 5 minutes
     */
    private int timeout = 300;

    /**
     * A user's copied state of eiher a model object.
     */
    private Map<String, Map<String, Object>> copy = Collections.synchronizedMap(
            new HashMap<String, Map<String, Object>>());

    /**
     * When timeouts were last checked.
     */
    private long whenLastCheckedForTimeouts = System.currentTimeMillis();

    public DefaultCommander() {
    }

    public Map<String, Object> getCopy() {
        return copy.get( Channels.getUserName() );
    }

    public void setCopy( Map<String, Object> state ) {
        copy.put( Channels.getUserName(), state );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPartCopied() {
        Map<String, Object> userCopy = getCopy();
        return userCopy != null && userCopy.get( "partState" ) != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFlowCopied() {
        Map<String, Object> userCopy = getCopy();
        return userCopy != null && userCopy.get( "isOutcome" ) != null;
    }

    public void setLockManager( LockManager lockManager ) {
        this.lockManager = lockManager;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout( int timeout ) {
        this.timeout = timeout;
    }

    public boolean isReplaying() {
        return replaying;
    }

    public void setReplaying( boolean replaying ) {
        this.replaying = replaying;
    }

    public void setIdMap( Map<Long, Long> idMap ) {
        this.idMap = idMap;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T resolve( Class<T> clazz, Long id ) throws CommandException {
        try {
            return getQueryService().find( clazz, resolveId( id ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void mapId( Long oldId, Long newId ) {
        if ( oldId != null && newId != null ) {
            idMap.put( oldId, newId );
        } else {
            LOG.warn( "Attempt to map " + oldId + " and " + newId );
        }
    }

    /**
     * {@inheritDoc}
     */
    public Long resolveId( Long id ) throws CommandException {
        if ( id == null ) return null;
        Long realId = idMap.get( id );
        if ( isReplaying() ) {
            return realId == null ? id : realId;
        } else {
            return realId == null ? id : resolveRealId( realId );
        }
    }

    private Long resolveRealId( Long id ) {
        Long realId = idMap.get( id );
        if ( realId == null )
            return id;
        else {
            // any number of jumps
            return resolveRealId( realId );
        }
    }

    private Set<Long> resolveIds( Set<Long> ids ) throws CommandException {
        Set<Long> resolvedIds = new HashSet<Long>();
        for ( Long id : ids ) {
            resolvedIds.add( resolveId( id ) );
        }
        return resolvedIds;
    }


    /**
     * {@inheritDoc}
     */
    public void resetUserHistory( String userName ) {
        synchronized ( this ) {
            history.resetForUser( userName );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getUndoTitle() {
        String title = "Undo";
        Memento memento = history.getUndo();
        if ( memento != null ) {
            title = title + " " + memento.getCommand().getName();
        }
        return title;
    }

    /**
     * {@inheritDoc}
     */
    public String getRedoTitle() {
        String title = "Redo";
        // memento of a command done to undo another
        Memento memento = history.getRedo();
        if ( memento != null ) {
            title = title + " " + memento.getCommand().getUndoes( this );
        }
        return title;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Command command ) {
        return command.canDo( this ) && lockManager.canGrabLocksOn( command.getLockingSet() );
    }

    /**
     * {@inheritDoc}
     */
    private Change execute( Command command ) throws CommandException {
        Change change;
        if ( command.isAuthorized() ) {
            try {
                Collection<Lock> grabbedLocks = lockManager.grabLocksOn( resolveIds( command.getLockingSet() ) );
                change = command.execute( this );
                lockManager.releaseLocks( grabbedLocks );
                if ( !isReplaying() ) getQueryService().getDao().onAfterCommand( command );
            } catch ( LockingException e ) {
                throw new CommandException( e.getMessage(), e );
            }
        } else {
            throw new CommandException( "You are not authorized." );
        }
        updateUserActive( command.getUserName() );
        return change;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canUndo() {
        synchronized ( this ) {
            boolean canUndo = false;
            Memento memento = history.getUndo();
            if ( memento != null ) {
                Command command = memento.getCommand();
                if ( command.isUndoable() ) {
                    try {
                        canUndo = command.noLockRequired()
                                || canDo( command.makeUndoCommand( this ) );
                    } catch ( CommandException e ) {
                        e.printStackTrace();
                        canUndo = false;
                    }
                }
            }
            return canUndo;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean canRedo() {
        synchronized ( this ) {
            boolean canRedo = false;
            Memento memento = history.getRedo();
            if ( memento != null ) {
                Command command = memento.getCommand();
                if ( command.isUndoable() ) {
                    try {
                        canRedo = command.noLockRequired()
                                || canDo( command.makeUndoCommand( this ) );
                    } catch ( CommandException e ) {
                        e.printStackTrace();
                        canRedo = false;
                    }
                }
            }
            return canRedo;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Change doCommand( Command command ) throws CommandException {
        synchronized ( this ) {
            if ( command instanceof MultiCommand ) LOG.info( "*** START multicommand ***" );
            LOG.info( ( isReplaying() ? "Replaying: " : "Doing: " ) + command.toString() );
            Change change = execute( command );
            if ( command instanceof MultiCommand ) LOG.info( "*** END multicommand ***" );
            history.recordDone( command );
            return change;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Change undo() throws CommandException {
        synchronized ( this ) {
            // Get memento of command to undo
            Memento memento = history.getUndo();
            if ( memento == null ) throw new CommandException( "Nothing can be undone right now." );
            Command undoCommand = memento.getCommand().makeUndoCommand( this );
            if ( undoCommand instanceof MultiCommand ) LOG.info( "*** START multicommand ***" );
            LOG.info( "Undoing: " + undoCommand.toString() );
            Change change = execute( undoCommand );
            if ( undoCommand instanceof MultiCommand ) LOG.info( "*** END multicommand ***" );
            change.setUndoing( true );
            history.recordUndone( memento, undoCommand );
            return change;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Change redo() throws CommandException {
        synchronized ( this ) {
            // Get memento of undoing command
            Memento memento = history.getRedo();
            if ( memento == null ) throw new CommandException( "Nothing can be redone right now." );
            // undo the undoing
            Command redoCommand = memento.getCommand().makeUndoCommand( this );
            LOG.info( "Redoing: " + redoCommand.toString() );
            Change change = execute( redoCommand );
            change.setUndoing( true );
            history.recordRedone( memento, redoCommand );
            return change;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
        replaying = false;
        idMap.clear();
        history.reset();
        lockManager.reset();
    }

    /**
     * {@inheritDoc}
     */
    public void cleanup( Class<? extends ModelObject> clazz, String name ) {
        synchronized ( this ) {
            QueryService queryService = getQueryService();
            if ( name != null && !name.trim().isEmpty() ) {
                ModelObject mo = queryService.getDao().find( clazz, name.trim() );
                if ( mo != null && mo.isUndefined() ) {
                    boolean garbage;
                    if ( mo instanceof Actor )
                        garbage = !queryService.isReferenced( (Actor) mo );
                    else if ( mo instanceof Role )
                        garbage = !queryService.isReferenced( (Role) mo );
                    else if ( mo instanceof Organization )
                        garbage = !queryService.isReferenced( (Organization) mo );
                    else if ( mo instanceof Place )
                        garbage = !queryService.isReferenced( (Place) mo );
                    else throw new IllegalArgumentException( "Can't clean up something of class " + clazz );
                    if ( garbage ) {
                        LOG.info( "Removing unused " + mo.getClass().getSimpleName() + " " + mo );
                        queryService.remove( mo );
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLockedByUser( Identifiable identifiable ) {
        return lockManager.isLockedByUser( identifiable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean requestLockOn( Identifiable identifiable ) {
        if ( isTimedOut() ) return false;
        updateUserActive( Channels.getUserName() );
        return lockManager.requestLockOn( identifiable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean requestLockOn( Long id ) {
        if ( isTimedOut() ) return false;
        updateUserActive( Channels.getUserName() );
        return lockManager.requestLockOn( id );
    }

    /**
     * {@inheritDoc}
     */
    public boolean releaseAnyLockOn( Identifiable identifiable ) {
        return lockManager.releaseAnyLockOn( identifiable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean releaseAnyLockOn( Long id ) {
        return lockManager.releaseAnyLockOn( id );
    }

    /**
     * {@inheritDoc}
     */
    public void releaseAllLocks( String userName ) {
        lockManager.releaseAllLocks( userName );
    }

    /**
     * {@inheritDoc}
     */
    public long getLastModified() {
        return history.getLastModified();
    }

    /**
     * {@inheritDoc}
     */
    public String getLastModifier() {
        return history.getLastModifier();
    }

    private void updateUserActive( String userName ) {
        whenLastActive.put( userName, System.currentTimeMillis() );
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void processTimeOuts() {
        long now = System.currentTimeMillis();
        long timeoutMillis = timeout * 1000;
        if ( ( now - whenLastCheckedForTimeouts ) > timeoutMillis ) {
            for ( String userName : whenLastActive.keySet() ) {
                long time = whenLastActive.get( userName );
                if ( ( now - time ) > timeoutMillis ) {
                    if ( lockManager.releaseAllLocks( userName ) ) {
                        timedOut.add( userName );
                    }
                }
            }
            for ( String userName : timedOut ) {
                whenLastActive.remove( userName );
            }
            whenLastCheckedForTimeouts = now;
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized boolean isTimedOut() {
        return timedOut.contains( Channels.getUserName() );
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void clearTimeOut() {
        timedOut.remove( Channels.getUserName() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUnlocked( ModelObject mo ) {
        return lockManager.getLock( mo.getId() ) == null;
    }
}
