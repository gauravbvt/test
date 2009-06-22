package com.mindalliance.channels.model;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A plan event.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2009
 * Time: 3:49:53 PM
 */
@Entity
public class Event extends ModelObject implements GeoLocatable {
    /**
     * Where the event is considered to occur.
     * Null means that its scope is universal.
     */
    private Place scope;
    /**
     * Does this even self-terminate?
     */
    private boolean selfTerminating;

    /**
     * Bogus event used to signify that the event is not known...
     */
    public static final Event UNKNOWN;

    static {
        UNKNOWN = new Event();
        UNKNOWN.setName( "(unknown)" );
        UNKNOWN.setId( 10000000L - 2 );
    }

    public Event() {

    }

    public Place getScope() {
        return scope;
    }

    public void setScope( Place scope ) {
        this.scope = scope;
    }

    public boolean isSelfTerminating() {
        return selfTerminating;
    }

    public void setSelfTerminating( boolean selfTerminating ) {
        this.selfTerminating = selfTerminating;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isEntity() {
        return true;
    }

    /**
      * {@inheritDoc}
      */
     @Transient
    public GeoLocation getGeoLocation() {
        return scope != null ? scope.getGeoLocation() : null;
    }

    /**
      * {@inheritDoc}
      */
     @Transient
    public String getGeoMarkerLabel() {
        return scope != null
                ? getName() + " in " + scope.getGeoMarkerLabel()
                : "";
    }
}
