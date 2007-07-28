// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import java.util.List;

import com.mindalliance.channels.data.profiles.Actor;
import com.mindalliance.channels.data.support.GUID;

/**
 * A specification of which actors execute a task, together or separately.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class Agent extends StorylineElement
    implements Knowledgeable, OptOutable {

    private Task task;

    /**
     * Default constructor.
     */
    public Agent() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Agent( GUID guid ) {
        super( guid );
    }

    /**
     * Return the actors specified by this agent.
     */
    public abstract List<Actor> getActors();

    /**
     * Return the task.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Set the task.
     * @param task the task to set
     */
    public void setTask( Task task ) {
        this.task = task;
    }

}
