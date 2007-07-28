// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import com.mindalliance.channels.data.support.AbstractJavaBean;
import com.mindalliance.channels.data.support.Duration;

/**
 * The cause of an occurrence which is either the start or end of an
 * occurrence. The creation of the effect may be delayed (e.g. event
 * begins 2 minutes after the task starts).
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 * @param <T> the specific type of occurence
 */
public class Cause<T extends Occurrence> extends AbstractJavaBean {

    /** Is the cause the start of the occurrence or the end? */
    private boolean start;

    /** How long after the start or end of the occurrence before
     * the event begins. */
    private Duration delay;
    private T occurrence;

    /**
     * Default constructor.
     */
    public Cause() {
        start = true;
        delay = new Duration();
    }

    /**
     * Convenience constructor.
     * @param occurrence the occurrence
     */
    public Cause( T occurrence ) {
        this();
        this.occurrence = occurrence;
    }

    /**
     * Get the occurrence that caused this.
     */
    public T getOccurrence() {
        return occurrence;
    }

    /**
     * Set the occurrence.
     * @param occurrence the occurrence to set
     */
    public void setOccurrence( T occurrence ) {
        this.occurrence = occurrence;
    }

    /**
     * Return the delay.
     */
    public Duration getDelay() {
        return delay;
    }

    /**
     * Set the delay.
     * @param delay the delay to set
     */
    public void setDelay( Duration delay ) {
        this.delay = delay;
    }

    /**
     * Return if this is a start cause.
     */
    public boolean isStart() {
        return start;
    }

    /**
     * Set the start.
     * @param start the isStart to set
     */
    public void setStart( boolean start ) {
        this.start = start;
    }

    /**
     * Get the time.
     */
    public Duration getTime() {
        if ( occurrence == null )
            return delay;
        else {
            Duration occTime = occurrence.getTime();
            return start ?
                   occTime.add( delay )
                 : occTime.add( occurrence.getDuration().add( delay ) );
        }
    }

    /** Provide a nice printed form. */
    @Override
    public String toString() {
        return getOccurrence().toString();
    }
}
