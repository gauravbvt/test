// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.SortedSet;
import java.util.TreeSet;

import com.mindalliance.channels.util.GUID;

/**
 * What an agent is expected to do in response to events in order
 * to fulfill a given role.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Responsibility extends AbstractModelObject {

    // TODO fix types
    private String type;
    private SortedSet<Response> responses = new TreeSet<Response>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Responsibility( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of responses.
     */
    public SortedSet<Response> getResponses() {
        return this.responses;
    }

    /**
     * Set the value of responses.
     * @param responses The new value of responses
     */
    public void setResponses( SortedSet<Response> responses ) {
        this.responses = responses;
    }

    /**
     * Return the value of type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Set the value of type.
     * @param type The new value of type
     */
    public void setType( String type ) {
        this.type = type;
    }

    //=========================================
    /**
     * A typical response to an event.
     */
    public static class Response {
        private Event event;
        private SortedSet<Task> tasks;

        /**
         * Default constructor.
         */
        public Response() {
        }

        /**
         * Return the value of event.
         */
        public Event getEvent() {
            return this.event;
        }

        /**
         * Set the value of event.
         * @param event The new value of event
         */
        public void setEvent( Event event ) {
            this.event = event;
        }

        /**
         * Return the value of tasks.
         */
        public SortedSet<Task> getTasks() {
            return this.tasks;
        }

        /**
         * Set the value of tasks.
         * @param tasks The new value of tasks
         */
        public void setTasks( SortedSet<Task> tasks ) {
            this.tasks = tasks;
        }
    }
}
