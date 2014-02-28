package com.mindalliance.channels.core.model;

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
    private boolean modelReferenced;

    /**
     * A place.
     */
    private Place place;

    /**
     * Reference to the scope of the event..
     */
    private Event event;

    public boolean isModelReferenced() {
        return modelReferenced;
    }

    public void setModelReferenced( boolean modelReferenced ) {
        this.modelReferenced = modelReferenced;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace( Place place ) {
        modelReferenced = false;
        event = null;
        this.place = place;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent( Event event ) {
        modelReferenced = false;
        place = null;
        this.event = event;
    }

    public Place getReferencedPlace( Place locale ) {
        if ( isModelReferenced() )
            return locale;
        else if ( event == null ) {
            return place;
        } else {
            return event.getScope();
        }
    }

    public boolean narrowsOrEquals( PlaceReference other, Place locale ) {
        Place p = other.getReferencedPlace( locale );
        return p == null || narrowsOrEquals( p, locale );
    }

    private boolean narrowsOrEquals( Place other, Place locale ) {
        Place referencedPlace = getReferencedPlace( locale );

        if ( referencedPlace == null )
            return false;
        else if ( referencedPlace.narrowsOrEquals( other, locale ) )
            return true;
        else
            return false;
    }

    public boolean references( ModelObject entity ) {
        if ( place != null )
            if ( place.equals( entity ) )
                return true;

        if ( event == null )
            return false;

        return event.equals( entity );
    }

    public boolean isSet( Place locale ) {
        return getReferencedPlace( locale ) != null;
    }

    public boolean isEventReferenced() {
        return event != null;
    }

    public boolean isPlaceReferenced() {
        return place != null;
    }

    public ModelEntity getReference() {
        if ( event == null )
            return place;
        else
            return event;
    }

    /**
     * Test if this reference has been assigned to something.
     * @param locale the default locale
     * @return false if unspecified...
     */
    public boolean isSpecified( Place locale ) {
        return modelReferenced || getReferencedPlace( locale ) != null;
    }
}
