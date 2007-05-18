/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.reference;

import java.util.List;

import com.mindalliance.channels.data.support.Distance;
import com.mindalliance.channels.data.support.LatLong;

/**
 * A place, position etc.
 * 
 * @author jf
 */
public class Location extends TypedReferenceData {

    private LatLong latLong;
    private Distance radius;
    private List<Location> within;
    private List<Location> nextTo;

    /**
     * @return the latLong
     */
    public LatLong getLatLong() {
        return latLong;
    }

    /**
     * @param latLong the latLong to set
     */
    public void setLatLong( LatLong latLong ) {
        this.latLong = latLong;
    }

    /**
     * @return the nextTo
     */
    public List<Location> getNextTo() {
        return nextTo;
    }

    /**
     * @param nextTo the nextTo to set
     */
    public void setNextTo( List<Location> nextTo ) {
        this.nextTo = nextTo;
    }

    /**
     * @param location
     */
    public void addNextTo( Location location ) {
        nextTo.add( location );
    }

    /**
     * @param location
     */
    public void removeNextTo( Location location ) {
        nextTo.remove( location );
    }

    /**
     * @return the radius
     */
    public Distance getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius( Distance radius ) {
        this.radius = radius;
    }

    /**
     * @return the within
     */
    public List<Location> getWithin() {
        return within;
    }

    /**
     * @param within the within to set
     */
    public void setWithin( List<Location> within ) {
        this.within = within;
    }

    /**
     * @param location
     */
    public void addWithin( Location location ) {
        within.add( location );
    }

    /**
     * @param location
     */
    public void removeWithin( Location location ) {
        within.remove( location );
    }

}
