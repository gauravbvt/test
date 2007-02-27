// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.util.GUID;

/**
 * A possible and significant consequence of a successful or failed task.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Outcome extends AbstractNamedObject {

    /**
     * Repercussions of an outcome.
     */
    public enum Impact { none, low, moderate, elevated, high, severe }

    private float likelihood;
    private List<Event> raisedEvents = new ArrayList<Event>();
    private List<Event> terminatedEvents = new ArrayList<Event>();
    private Information details;
    private Impact impact;

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Outcome( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of details.
     */
    public Information getDetails() {
        return this.details;
    }

    /**
     * Set the value of details.
     * @param details The new value of details
     */
    public void setDetails( Information details ) {
        this.details = details;
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
     * Return the value of raisedEvents.
     */
    public List<Event> getRaisedEvents() {
        return this.raisedEvents;
    }

    /**
     * Set the value of raisedEvents.
     * @param raisedEvents The new value of raisedEvents
     */
    public void setRaisedEvents( List<Event> raisedEvents ) {
        this.raisedEvents = raisedEvents;
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
}
