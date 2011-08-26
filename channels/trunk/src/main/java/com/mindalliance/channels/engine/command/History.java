package com.mindalliance.channels.engine.command;

import com.mindalliance.channels.core.dao.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stacks of done and undone commands recorded as mementoes.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 28, 2009
 * Time: 2:24:34 PM
 */
public class History {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( History.class );
    /**
     * Maximum size of the done list.
     */
    private static int MaxDoneSize = 100;
    /**
     * Last modified timestamp.
     */
    private long lastModified;
    /**
     * Name of user who made last modification.
     */
    private String lastModifier;
    /**
     * List of mementoes of done commands.
     * A new memento is added at the front of the list.
     */
    private List<Memento> done = new ArrayList<Memento>();
    /**
     * List of mementoes of undone commands.
     * A new memento is added at the front of the list.
     */
    private List<Memento> undone = new ArrayList<Memento>();

    public History() {
        reset();
    }

    /**
     * Record command as done.
     * Disable a redo for the user until an undo is performed.
     *
     * @param command a command
     */
    public void recordDone( Command command ) {
        if ( command.isMemorable() ) {
            Memento memento = new Memento( command );
            addToDone( memento );
        }
        clearUndone( command.getUserName() );
        updateLastModified( command );
    }

    /**
     * Remove all undone mementos by user who executed command.
     * In essence disabling a redo for the user until an undo is performed.
     *
     * @param userName a user name
     */
    @SuppressWarnings( "unchecked" )
    private void clearUndone( final String userName ) {
        List<Memento> kept = new ArrayList<Memento>();
        kept.addAll( CollectionUtils.select( undone, new Predicate() {
            public boolean evaluate( Object obj ) {
                Memento memento = (Memento) obj;
                return !memento.getUserName().equals( userName );
            }
        } ) );
        undone = kept;
    }

    /**
     * Remove all done segment-specific mementos by user who executed command.
     * In essence disabling an undo for the user until a do is performed.
     *
     * @param userName a user name
     * @param all include segment-specific
     */
    @SuppressWarnings( "unchecked" )
    private void clearDoneInSegment( final String userName, final boolean all ) {
        List<Memento> kept = new ArrayList<Memento>();
        kept.addAll( CollectionUtils.select( done, new Predicate() {
            public boolean evaluate( Object obj ) {
                Memento memento = (Memento) obj;
                return !memento.getUserName().equals( userName )
                        || ( !all && !memento.getCommand().isSegmentSpecific() );
            }
        } ) );
        done = kept;
    }

    /**
     * Record memorized command as undone.
     *
     * @param memento        memento of the command undo
     * @param undoingCommand an undoing command
     */
    public void recordUndone( Memento memento, Command undoingCommand ) {
        if ( memento.getCommand().isMemorable() ) {
            done.remove( memento );
            undone.add( 0, new Memento( undoingCommand ) );
            updateLastModified( undoingCommand );
        }
    }

    /**
     * Record memorized command as redone.
     *
     * @param memento     a memento of the undoing command
     * @param redoCommand the command that undoes an undoing command
     */
    public void recordRedone( Memento memento, Command redoCommand ) {
        addToDone( new Memento( redoCommand ) );
        undone.remove( memento );
        updateLastModified( redoCommand );
    }

    /**
     * Get unconflicted memento of command user could possibly undo.
     *
     * @return a memento
     */
    public Memento getUndo() {
        String userName = User.current().getUsername();
        Memento memento = findLastBy( done, userName );
        if ( memento != null && !hasConflict( memento ) )
            return memento;
        else
            return null;
    }

    /**
     * Get unconflicted memento of command user could possibly redo.
     *
     * @return a memento
     */
    public Memento getRedo() {
        String userName = User.current().getUsername();
        Memento memento = findLastBy( undone, userName );
        if ( memento != null && !hasConflict( memento ) )
            return memento;
        else
            return null;
    }

    /**
     * A memento has conflicts if a more recent memento by anyone has intersecting conflict set
     *
     * @param memento a memento
     * @return a boolean
     */
    private boolean hasConflict( Memento memento ) {
        Iterator<Memento> moreRecent = findAllDoneByOtherUserAfter( memento.getTimestamp() );
        while ( moreRecent.hasNext() ) {
            Memento other = moreRecent.next();
            if ( other != memento && !CollectionUtils.intersection(
                    memento.getCommand().getConflictSet(),
                    other.getCommand().getConflictSet() ).isEmpty() ) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings( "unchecked" )
    private Iterator<Memento> findAllDoneByOtherUserAfter( final long timestamp ) {
        return new FilterIterator( done.iterator(), new Predicate() {
            public boolean evaluate( Object obj ) {
                Memento memento = (Memento) obj;
                return !memento.getUserName().equals( User.current().getUsername() )
                        && memento.getTimestamp() >= timestamp;
            }
        } );
    }

    private static Memento findLastBy( List<Memento> mementoes, String userName ) {
        for ( Memento memento : mementoes ) {
            if ( memento.getUserName().equals( userName ) ) return memento;
        }
        return null;
    }

    /**
     * Reset history.
     */
    public void reset() {
        done.clear();
        undone.clear();
        lastModified = System.currentTimeMillis();
        lastModifier = "";
    }

    /**
     * Removes all segment-specific done and undone mementoes of a user.
     *
     * @param userName a user's name
     * @param all include segment-specific commands
     */
    public void resetForUser( String userName, boolean all ) {
        clearUndone( userName );
        clearDoneInSegment( userName, all );
    }

    /**
     * Add a memento at the head of the list and drop the last element if maximum size is exceeded.
     *
     * @param memento a memento
     */
    private void addToDone( Memento memento ) {
        done.add( 0, memento );
        if ( done.size() > MaxDoneSize ) done.remove( done.size() - 1 );
    }

    /**
     * Get timestamp of last modification.
     * If no recorded modification, return system current time.
     * @return a long
     */
    public long getLastModified() {
       return lastModified;
    }

    private void updateLastModified( Command command ) {
        lastModified = System.currentTimeMillis();
        lastModifier = command.getUserName();
        LOG.info("***Last modified: " + lastModified + " from " + command.getName() + " by " + command.getUserName() );
    }

    /**
     * Return name of last user to make a change.
     * @return a string
     */
    public String getLastModifier() {
        return lastModifier;
    }
}
