package com.mindalliance.channels.command;

import com.mindalliance.channels.pages.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

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
        Memento memento = new Memento( command );
        done.add( 0, memento );
        clearUndone( command.getUserName() );
    }

    /**
     * Remove all undone mementos by user who executed command.
     * In essence disabling a redo for the user until an undo is performed.
     *
     * @param userName a user name
     */
    @SuppressWarnings( "unchecked" )
    private void clearUndone( final String userName ) {
        List<Memento> cleared = new ArrayList<Memento>();
        cleared.addAll( CollectionUtils.select( undone, new Predicate() {
            public boolean evaluate( Object obj ) {
                Memento memento = (Memento) obj;
                return !memento.getUserName().equals( userName );
            }
        } ) );
        undone = cleared;
    }

    /**
     * Record memorized command as undone.
     *
     * @param memento a memento
     */
    public void recordUndone( Memento memento ) {
        done.remove( memento );
        undone.add( 0, memento );
    }

    /**
     * Record memorized command as redone.
     *
     * @param memento a memento
     */
    public void recordRedone( Memento memento ) {
        done.add( 0, memento );
        undone.remove( memento );
    }

    /**
     * Get unconflicted memento of command user could possibly undo.
     *
     * @return a memento
     */
    public Memento getUndo() {
        String userName = Project.getUserName();
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
        String userName = Project.getUserName();
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
        Iterator<Memento> moreRecent = findAllDoneAfter( memento.getDate() );
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
    private Iterator<Memento> findAllDoneAfter( final Date date ) {
        return new FilterIterator( done.iterator(), new Predicate() {
            public boolean evaluate( Object obj ) {
                Memento memento = (Memento) obj;
                return ( !memento.getDate().before( date ) );
            }
        } );
    }

    private static Memento findLastBy( List<Memento> mementoes, String userName ) {
        for ( Memento memento : mementoes ) {
            if ( memento.getUserName().equals( userName ) ) return memento;
        }
        return null;
    }

    public void reset() {
        done.clear();
        undone.clear();
    }
}
