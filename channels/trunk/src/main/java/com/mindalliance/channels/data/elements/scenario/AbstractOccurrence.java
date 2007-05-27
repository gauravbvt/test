// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.DisplayAs;
import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.data.elements.Occurrence;
import com.mindalliance.channels.data.reference.Location;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.util.GUID;

/**
 * Something that happens in a scenario.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class AbstractOccurrence extends AbstractScenarioElement
        implements Occurrence {

    private Duration duration = Duration.NONE;
    private Location location;
    private Cause cause;

    /**
     * Default constructor.
     */
    public AbstractOccurrence() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public AbstractOccurrence( GUID guid ) {
        super( guid );
        cause = new Cause();
    }

    /**
     * Test if this is an incident.
     */
    public boolean isIncident() {
        return getCause() == null
            || getCause().getOccurrence() == null;
    }

    /**
     * Set the duration.
     * @param duration the duration to set
     */
    public void setDuration( Duration duration ) {
        this.duration = duration;
    }

    /**
     * Get the duration.
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Test if this occurence starts after the end of another occurrence.
     * @param occurrence the other occurrence
     */
    public boolean isAfter( Occurrence occurrence ) {
        return getTime().getMsecs() > occurrence.getEnd().getMsecs();
    }

    /**
     * Test if this occurence starts before the end of another occurrence.
     * @param occurrence the other occurrence
     */
    public boolean isBefore( Occurrence occurrence ) {
        return getTime().getMsecs() < occurrence.getEnd().getMsecs();
    }

    /**
     * Test if this occurence starts and ends within the life of another
     * occurrence.
     * @param occurrence the other occurrence
     */
    public boolean isDuring( Occurrence occurrence ) {
        return getTime().getMsecs() >= occurrence.getTime().getMsecs()
                && getEnd().getMsecs() <= occurrence.getEnd().getMsecs();
    }

    /**
     * Set the location.
     * @param location the location to set
     */
    public void setLocation( Location location ) {
        this.location = location;
    }

    /**
     * Get the location of this occurrence.
     */
    @DisplayAs( direct = "located in {1}",
                reverse = "includes {1}",
                reverseMany = "includes:" )
    public Location getLocation() {
        return location;
    }

    /**
     * Return the end time.
     */
    public Duration getEnd() {
        return getTime().add( duration );
    }

    /**
     * Return the time.
     */
    public Duration getTime() {
        return getCause() == null ?
                Duration.NONE : getCause().getTime();
    }

    /**
     * Set the cause.
     * @param cause the cause to set
     */
    public void setCause( Cause cause ) {
        this.cause = cause;
    }

    /**
     * Get the cause.
     */
    @DisplayAs( direct = "caused by {1}",
                reverse = "causes {1}",
                reverseMany = "causes:" )
    public Cause getCause() {
        return this.cause;
    }

}
