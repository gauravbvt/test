package com.mindalliance.channels.command;

import java.util.Collection;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 1:47:58 PM
 */
public class DefaultCommander implements Commander {

    private LockManager lockManager;

    private History history = new History();

    public DefaultCommander() {
    }

    public void setLockManager( LockManager lockManager ) {
        this.lockManager = lockManager;
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
                result = command.execute();
                lockManager.releaseLocks( grabbedLocks );
            }
            catch ( LockingException e ) {
                throw new CommandException( e.getMessage() );
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
                if ( command.isUndoable() )
                    try {
                        canUndo = canDo( command.makeUndoCommand() );
                    } catch ( CommandException e ) {
                        e.printStackTrace();
                        canUndo = false;
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
            Memento memento = history.getRedo();
            return ( memento != null && canDo( memento.getCommand() ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public Object doCommand( Command command ) throws CommandException {
        synchronized ( this ) {
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
            Memento memento = history.getUndo();
            if ( memento == null ) throw new CommandException( "Nothing can be undone right now." );
            execute( memento.getCommand().makeUndoCommand() );
            history.recordUndone( memento );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void redo() throws CommandException {
        synchronized ( this ) {
            Memento memento = history.getRedo();
            if ( memento == null ) throw new CommandException( "Nothing can be redone right now." );
            execute( memento.getCommand() );
            history.recordRedone( memento );
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
