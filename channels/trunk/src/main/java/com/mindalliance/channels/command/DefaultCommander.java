package com.mindalliance.channels.command;

import com.mindalliance.channels.Service;

import java.util.Collection;

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

    private static final Logger Log = LoggerFactory.getLogger( DefaultCommander.class );

    private LockManager lockManager;

    private History history = new History();

    private Service service;

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

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Command command ) {
        return lockManager.canGrabLocksOn( command.getLockingSet() );
    }

    /**
     * {@inheritDoc}
     */
    private Object execute( Command command ) throws CommandException {
        Object result;
        if ( command.isAuthorized() ) {
            try {
                Collection<Lock> grabbedLocks = lockManager.grabLocksOn( command.getLockingSet() );
                result = command.execute( this );
                lockManager.releaseLocks( grabbedLocks );
            }
            catch ( LockingException e ) {
                throw new CommandException( e.getMessage(), e );
            }
            catch ( Exception e ) {
                e.printStackTrace();
                throw new CommandException( "Execution failed.", e );
            }
        } else {
            throw new CommandException( "You are not authorized." );
        }
        return result;
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
                        canUndo = command.noLockRequired() || canDo( command.makeUndoCommand( this ) );
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
                        canRedo = command.noLockRequired() || canDo( command.makeUndoCommand( this ) );
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
    public Object doCommand( Command command ) throws CommandException {
        synchronized ( this ) {
            Log.info( "Doing: " + command.toString() );
            Object result = execute( command );
            history.recordDone( command );
            return result;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void undo() throws CommandException {
        synchronized ( this ) {
            // Get memento of command to undo
            Memento memento = history.getUndo();
            if ( memento == null ) throw new CommandException( "Nothing can be undone right now." );
            Command undoCommand = memento.getCommand().makeUndoCommand( this );
            Log.info( "Undoing: " + undoCommand.toString() );
            execute( undoCommand );
            history.recordUndone( memento, undoCommand );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void redo() throws CommandException {
        synchronized ( this ) {
            // Get memento of undoing command
            Memento memento = history.getRedo();
            if ( memento == null ) throw new CommandException( "Nothing can be redone right now." );
            // undo the undoing
            Command redoCommand = memento.getCommand().makeUndoCommand( this );
            Log.info( "Redoing: " + redoCommand.toString() );
            execute( redoCommand );
            history.recordRedone( memento, redoCommand );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
        history.reset();
        lockManager.reset();
    }
}
