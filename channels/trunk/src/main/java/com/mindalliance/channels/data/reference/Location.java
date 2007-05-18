// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

import java.util.List;

import com.mindalliance.channels.DisplayAs;
import com.mindalliance.channels.data.support.Distance;
import com.mindalliance.channels.data.support.LatLong;

/**
 * A place, position etc.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Location extends TypedReferenceData {

    private LatLong latLong;
    private Distance radius;
    private List<Location> within;
    private List<Location> nextTo;

    /**
     * Default constructor.
     */
    public Location() {
    }

    /**
     * Return the Latitude/Longitude.
     */
    public LatLong getLatLong() {
        return latLong;
    }

    /**
     * Set the Latitude/Longitude.
     * @param latLong the latLong to set
     */
    public void setLatLong( LatLong latLong ) {
        this.latLong = latLong;
    }

    /**
     * Return a list of close-by locations.
     */
    public List<Location> getNextTo() {
        return nextTo;
    }

    /**
     * Set the list of close-by locations.
     * @param nextTo the nextTo to set
     */
    public void setNextTo( List<Location> nextTo ) {
        this.nextTo = nextTo;
    }

    /**
     * Add a close-by location.
     * @param location the location
     */
    public void addNextTo( Location location ) {
        nextTo.add( location );
    }

    /**
     * Remove a close-by location.
     * @param location the location
     */
    public void removeNextTo( Location location ) {
        nextTo.remove( location );
    }

    /**
     * Return the radius.
     */
    public Distance getRadius() {
        return radius;
    }

    /**
     * Set the radius.
     * @param radius the radius to set
     */
    public void setRadius( Distance radius ) {
        this.radius = radius;
    }

    /**
     * Get the enclosing locations.
     * @return the within
     */
    @DisplayAs( direct = "located in {1}",
                reverse = "includes {1}",
                directMany = "located in:",
                reverseMany = "includes:" )
    public List<Location> getWithin() {
        return within;
    }

    /**
     * Set the enclosing locations.
     * @param within the within to set
     */
    public void setWithin( List<Location> within ) {
        this.within = within;
    }

    /**
     * Add an enclosed location.
     * @param location the location
     */
    public void addWithin( Location location ) {
        within.add( location );
    }

    /**
     * Remove an enclosed location.
     * @param location the location
     */
    public void removeWithin( Location location ) {
        within.remove( location );
    }

}
