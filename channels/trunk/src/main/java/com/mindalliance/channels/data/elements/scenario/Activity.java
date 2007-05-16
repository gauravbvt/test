/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.elements.resources.AccessibleResource;
import com.mindalliance.channels.util.GUID;

/**
 * The execution of a task by one or more roles or teams. Activities
 * are created during scenario analysis by matching the agents of a
 * task with persons within the project's scope. A single task can
 * imply many activities that carry it out.
 * 
 * @author jf
 */
public class Activity extends AbstractOccurrence {

    private Task task;
    private List<AccessibleResource> actors;

    public Activity() {
        super();
    }

    public Activity( GUID guid ) {
        super( guid );
    }

    /**
     * @return the actors
     */
    public List<AccessibleResource> getActors() {
        return actors;
    }

    /**
     * @param actors the actors to set
     */
    public void setActors( List<AccessibleResource> actors ) {
        this.actors = actors;
    }

    /**
     * @param actor
     */
    public void addActor( AccessibleResource actor ) {
        actors.add( actor );
    }

    /**
     * @param actor
     */
    public void removeActor( AccessibleResource actor ) {
        actors.remove( actor );
    }

    /**
     * @return the task
     */
    public Task getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask( Task task ) {
        this.task = task;
    }

}
