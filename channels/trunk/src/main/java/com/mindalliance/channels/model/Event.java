// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.system.MetaInformation;
import com.mindalliance.channels.util.Area;
import com.mindalliance.channels.util.GUID;
import com.mindalliance.channels.util.TimePeriod;

/**
 * Something of consequence that is true for some period of time.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Event extends AbstractNamedObject {

    // TODO fix kind
    private String kind;
    private Area location;
    private TimePeriod timing;
    private MetaInformation details;

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Event( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of details.
     */
    public MetaInformation getDetails() {
        return this.details;
    }

    /**
     * Set the value of details.
     * @param details The new value of details
     */
    public void setDetails( MetaInformation details ) {
        this.details = details;
    }

    /**
     * Return the value of kind.
     */
    public String getKind() {
        return this.kind;
    }

    /**
     * Set the value of kind.
     * @param kind The new value of kind
     */
    public void setKind( String kind ) {
        this.kind = kind;
    }

    /**
     * Return the value of location.
     */
    public Area getLocation() {
        return this.location;
    }

    /**
     * Set the value of location.
     * @param location The new value of location
     */
    public void setLocation( Area location ) {
        this.location = location;
    }

    /**
     * Return the value of timing.
     */
    public TimePeriod getTiming() {
        return this.timing;
    }

    /**
     * Set the value of timing.
     * @param timing The new value of timing
     */
    public void setTiming( TimePeriod timing ) {
        this.timing = timing;
    }
}
