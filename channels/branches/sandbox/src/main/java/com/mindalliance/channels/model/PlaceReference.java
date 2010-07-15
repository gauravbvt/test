package com.mindalliance.channels.model;

import java.io.Serializable;

/**
 * A direct reference to a place or an indirect reference to the scope of the whole plan or of an event.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 21, 2009
 * Time: 9:04:47 PM
 */
public class PlaceReference implements Serializable {
    /**
     * Reference to the plan locale.
     */
    private boolean planReferenced = false;
    /**
     * A place.
     */
    private Place place;
    /**
     * Reference to the scope of the event..
     */
    private Event event;

    public boolean isPlanReferenced() {
        return planReferenced;
    }

    public void setPlanReferenced( boolean planReferenced ) {
        this.planReferenced = planReferenced;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace( Place place ) {
        planReferenced = false;
        event = null;
        this.place = place;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent( Event event ) {
        planReferenced = false;
        place = null;
        this.event = event;
    }

    public Place getReferencedPlace( Plan plan ) {
        return isPlanReferenced() ? plan.getLocale()
             : event != null      ? event.getScope()
                                  : place;
    }

    public boolean narrowsOrEquals( PlaceReference other, Plan plan ) {
        return narrowsOrEquals( other.getReferencedPlace( plan ), plan );
    }

    public boolean narrowsOrEquals( Place other, Plan plan ) {
        return other != null
                && getReferencedPlace( plan ) != null
                && getReferencedPlace( plan ).narrowsOrEquals( other, plan );
    }

    public boolean references( ModelObject entity ) {
        return ModelObject.areIdentical( place, entity )
                || ModelObject.areIdentical( event, entity );
    }

    public boolean isSet( Plan plan ) {
        return getReferencedPlace( plan ) != null;
    }

    public boolean isEventReferenced() {
        return event != null;
    }

    public boolean isPlaceReferenced() {
        return place != null;
    }

    public ModelEntity getReference() {
        if ( event != null ) return event;
        if ( place != null ) return place;
        return null;
    }
}
