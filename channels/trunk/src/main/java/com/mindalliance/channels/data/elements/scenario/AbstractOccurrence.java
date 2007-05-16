/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.Occurrence;
import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.data.reference.Location;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.util.GUID;

/**
 * Something that happens in a scenario.
 * 
 * @author jf
 */
public abstract class AbstractOccurrence extends AbstractScenarioElement
        implements Occurrence {

    private Duration duration;
    private Location location;
    private Cause cause;

    public AbstractOccurrence() {
        super();
    }

    public AbstractOccurrence( GUID guid ) {
        super( guid );
        cause = new Cause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Occurrence#isIncident()
     */
    public boolean isIncident() {
        return cause.getOccurrence() == null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Occurrence#getEnd()
     */
    public Duration getEnd() {
        return getTime().add( duration );
    }

    /**
     * Get cause
     */
    public Cause getCause() {
        return cause;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration( Duration duration ) {
        this.duration = duration;
    }

    /**
     * @param location the location to set
     */
    public void setLocation( Location location ) {
        this.location = location;
    }

    public Duration getDuration() {
        return duration;
    }

    public boolean isAfter( Occurrence occurrence ) {
        return getTime().getMsecs() > occurrence.getEnd().getMsecs();
    }

    public boolean isBefore( Occurrence occurrence ) {
        return getTime().getMsecs() < occurrence.getEnd().getMsecs();
    }

    public boolean isDuring( Occurrence occurrence ) {
        return getTime().getMsecs() >= occurrence.getTime().getMsecs()
                && getEnd().getMsecs() <= occurrence.getEnd().getMsecs();
    }

    public Location getLocation() {
        return location;
    }

    /**
     * @return the time
     */
    public Duration getTime() {
        return getCause().getTime();
    }

    /**
     * @param cause the cause to set
     */
    public void setCause( Cause cause ) {
        this.cause = cause;
    }

}
