package com.mindalliance.channels.command;

import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.Identifiable;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 1:47:58 PM
 */
public class DefaultCommander implements Commander {
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
     * Data query object.
     */
    private DataQueryObject dqo;

    private boolean replaying = false;
    /**
     * An id translation map.
     */
    // TODO - this could grow unchecked
    private Map<Long, Long> idMap = new HashMap<Long, Long>();

    public DefaultCommander() {
    }

    public void setLockManager( LockManager lockManager ) {
        this.lockManager = lockManager;
    }

    public void setDqo( DataQueryObject dqo ) {
        this.dqo = dqo;
    }

    public DataQueryObject getDqo() {
        return dqo;
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
            return getDqo().find( clazz, resolveId( id ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public Long resolveId( Long id ) throws CommandException {
        Long realId = idMap.get( id );
        if ( realId == null )
            return id;
        else {
            if ( isReplaying() ) {
                return realId;
            } else {
                return resolveId( realId );
            }
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
        return lockManager.canGrabLocksOn( command.getLockingSet() );
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
                if ( !isReplaying() ) getDqo().getDao().onAfterCommand( command );
            } catch ( LockingException e ) {
                throw new CommandException( e.getMessage(), e );
            }
        } else {
            throw new CommandException( "You are not authorized." );
        }
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
        idMap = new HashMap<Long, Long>();
        history.reset();
        lockManager.reset();
    }

    /**
     * {@inheritDoc}
     */
    public void mapId( Long oldId, Long newId ) {
        if ( idMap != null && oldId != null ) {
            idMap.put( oldId, newId );
        } else {
            LOG.warn( "Attempt to map " + oldId + " and " + newId );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void cleanup( Class<? extends ModelObject> clazz, String name ) {
        synchronized ( this ) {
            DataQueryObject dqo = getDqo();
            if ( name != null && !name.trim().isEmpty() ) {
                ModelObject mo = dqo.getDao().find( clazz, name.trim() );
                if ( mo != null && mo.isUndefined() ) {
                    boolean garbage;
                    if ( mo instanceof Actor ) garbage = !dqo.isReferenced( (Actor) mo );
                    else if ( mo instanceof Role ) garbage = !dqo.isReferenced( (Role) mo );
                    else if ( mo instanceof Organization ) garbage = !dqo.isReferenced( (Organization) mo );
                    else if ( mo instanceof Place ) garbage = !dqo.isReferenced( (Place) mo );
                    else throw new IllegalArgumentException( "Can't clean up something of class " + clazz );
                    if ( garbage ) {
                        LOG.info( "Removing unused " + mo.getClass().getSimpleName() + " " + mo );
                        dqo.remove( mo );
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
        return lockManager.requestLockOn( identifiable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean requestLockOn( Long id ) {
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

    public String getLastModifier() {
        return history.getLastModifier();
    }
}
