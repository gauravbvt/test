// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.profiles.Actor;
import com.mindalliance.channels.data.support.GUID;

/**
 * The execution of a task by one or more roles or teams. Activities
 * are created during scenario analysis by matching the agents of a
 * task with persons within the project's scope. A single task can
 * imply many activities that carry it out.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Activity extends Occurrence {

    private Task task;
    private List<Actor> actors = new ArrayList<Actor>();

    /**
     * Default constructor.
     */
    public Activity() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Activity( GUID guid ) {
        super( guid );
    }

    /**
     * Return the actors.
     */
    public List<Actor> getActors() {
        return actors;
    }

    /**
     * Set the actors.
     * @param actors the actors to set
     */
    public void setActors( List<Actor> actors ) {
        this.actors = actors;
    }

    /**
     * Add an actor.
     * @param actor the actor
     */
    public void addActor( Actor actor ) {
        actors.add( actor );
    }

    /**
     * Remove an actor.
     * @param actor the actor
     */
    public void removeActor( Actor actor ) {
        actors.remove( actor );
    }

    /**
     * Return the task.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Set the task.
     * @param task the task
     */
    public void setTask( Task task ) {
        this.task = task;
    }

}
