/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;

/**
 * Spatial coordinates.
 * 
 * @author jf
 */
public class LatLong implements Serializable {

    private Double latitude;
    private Double longitude;

    /**
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude( Double latitude ) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude( Double longitude ) {
        this.longitude = longitude;
    }

}
