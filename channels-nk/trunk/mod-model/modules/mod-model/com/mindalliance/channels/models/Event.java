// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.models;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.support.CollectionType;
import com.mindalliance.channels.support.GUID;
import com.mindalliance.channels.support.Level;

/**
 * An event in a scenario that occurs possibly with some delay. The
 * event may be caused by a task or not (then an incident "caused"
 * implicitly by the start of the scenario it's in). An event may be
 * terminated by any of one or more tasks, or it may terminate on its
 * own after some time.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Event extends Occurrence implements Excludable {

    private Level probability;

    /** Set if a task terminates it. */
    private List<Task> terminatingTasks = new ArrayList<Task>();

    /**
     * Default constructor.
     */
    public Event() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Event( GUID guid ) {
        super( guid );
    }

    /**
     * Return the probability.
     */
    public Level getProbability() {
        return probability;
    }

    /**
     * Set the probability.
     * @param probability the probability to set
     */
    public void setProbability( Level probability ) {
        this.probability = probability;
    }

    /**
     * Return the terminating tasks.
     */
    @CollectionType( type = Task.class )
    public List<Task> getTerminatingTasks() {
        return terminatingTasks;
    }

    /**
     * Set the terminating tasks.
     * @param terminatingTasks the terminatingTasks to set
     */
    public void setTerminatingTasks( List<Task> terminatingTasks ) {
        this.terminatingTasks = terminatingTasks;
    }

    /**
     * Add a terminating task.
     * @param task the task
     */
    public void addTerminatingTask( Task task ) {
        terminatingTasks.add( task );
    }

    /**
     * Remove a terminating task.
     * @param task the task
     */
    public void removeTerminatingTask( Task task ) {
        terminatingTasks.remove( task );
    }
}
