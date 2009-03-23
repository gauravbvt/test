package com.mindalliance.channels.command;

import com.mindalliance.channels.Service;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;

import java.util.Collection;
import java.util.Map;

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
     * Service.
     */
    private Service service;
    /**
     * An id translation map.
     */
    private Map<String, Long> idMap = null;

    public DefaultCommander() {
    }

    public void setLockManager( LockManager lockManager ) {
        this.lockManager = lockManager;
    }

    public void setService( Service service ) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }

    public void setIdMap( Map<String, Long> idMap ) {
        this.idMap = idMap;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T resolve( Class<T> clazz, Long id ) throws NotFoundException {
        return getService().find( clazz, resolveId(id) );
    }

    /**
     * {@inheritDoc}
     */
    public Long resolveId( Long id ) throws NotFoundException {
        if ( idMap == null ) {
            return id ;
        } else {
            Long realId = idMap.get( id.toString() );
            if ( realId == null )
                throw new NotFoundException();
            else {
                return realId;
            }
        }
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
                Collection<Lock> grabbedLocks = lockManager.grabLocksOn( command.getLockingSet() );
                change = command.execute( this );
                lockManager.releaseLocks( grabbedLocks );
                if ( !isReplayingJournal() ) getService().getDao().onAfterCommand( command );
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
            LOG.info( "Doing: " + command.toString() );
            Change change = execute( command );
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
            LOG.info( "Undoing: " + undoCommand.toString() );
            Change change = execute( undoCommand );
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
        idMap = null;
        history.reset();
        lockManager.reset();
    }

    /**
      * {@inheritDoc}
      */
    public void mapId( Long oldId, Long newId ) {
        if (idMap != null && oldId != null) {
             idMap.put(oldId.toString(), newId);
        }
    }

    /**
      * {@inheritDoc}
      */
    public boolean isReplayingJournal() {
        return idMap != null;
    }
}
