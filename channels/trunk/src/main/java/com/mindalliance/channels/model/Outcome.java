// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A possible change in the world caused or prevented by a task's successful
 * execution.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @navassoc - terminates * Event
 */
public class Outcome extends Occurence {

    /**
     * Repercussions of an outcome.
     */
    public enum Impact { none, low, moderate, elevated, high, severe };

    private float likelihood;
    private List<Event> terminatedEvents = new ArrayList<Event>();
    private Impact impact;

    /**
     * Default constructor.
     */
    public Outcome() {
        super();
    }

    /**
     * Return the value of impact.
     */
    public Impact getImpact() {
        return this.impact;
    }

    /**
     * Set the value of impact.
     * @param impact The new value of impact
     */
    public void setImpact( Impact impact ) {
        this.impact = impact;
    }

    /**
     * Return the value of likelihood.
     */
    public float getLikelihood() {
        return this.likelihood;
    }

    /**
     * Set the value of likelihood.
     * @param likelihood The new value of likelihood
     */
    public void setLikelihood( float likelihood ) {
        this.likelihood = likelihood;
    }

    /**
     * Return the value of terminatedEvents.
     */
    public List<Event> getTerminatedEvents() {
        return this.terminatedEvents;
    }

    /**
     * Set the value of terminatedEvents.
     * @param terminatedEvents The new value of terminatedEvents
     */
    public void setTerminatedEvents( List<Event> terminatedEvents ) {
        this.terminatedEvents = terminatedEvents;
    }

    /**
     * Add a terminated event.
     * @param event the event
     */
    public void addTerminatedEvent( Event event ) {
        this.terminatedEvents.add( event );
    }

    /**
     * Remove a terminated event.
     * @param event the event
     */
    public void removeTerminatedEvent( Event event ) {
        this.terminatedEvents.remove( event );
    }
}
