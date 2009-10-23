package com.mindalliance.channels.model;

import java.io.Serializable;

/**
 * A direct reference to a place or an indirect reference to the scope of an event.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 21, 2009
 * Time: 9:04:47 PM
 */
public class PlaceReference implements Serializable {
    /**
     * A place.
     */
    private Place place;
    /**
     * An event (i.e. the scope of the event).
     */
    private Event event;

    public Place getPlace() {
        return place;
    }

    public void setPlace( Place place ) {
        assert event == null;
        this.place = place;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent( Event event ) {
        assert place == null;
        this.event = event;
    }

    public Place getReferencedPlace() {
        return event != null ? event.getScope() : place;
    }

    public boolean narrowsOrEquals( PlaceReference other ) {
        return narrowsOrEquals( other.getReferencedPlace() );
    }

    public boolean narrowsOrEquals( Place other ) {
        return other != null
                && getReferencedPlace() != null
                && getReferencedPlace().narrowsOrEquals( other );
    }

    public boolean references( ModelObject entity ) {
        return ModelObject.areIdentical( place, entity )
                || ModelObject.areIdentical( event, entity );
    }

    public boolean isSet() {
        return getReferencedPlace() != null;
    }
}
