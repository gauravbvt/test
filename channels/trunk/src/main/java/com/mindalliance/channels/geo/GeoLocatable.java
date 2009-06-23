package com.mindalliance.channels.geo;

import java.io.Serializable;

/**
 * A geolocatable object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 18, 2009
 * Time: 12:53:44 PM
 */
public interface GeoLocatable extends Serializable {
    /**
     * Get geolocation if known, else null.
     *
     * @return a geo location
     */
    GeoLocation geoLocate();

    /**
     * Get a geo marker's label.
     *
     * @return a string
     */
    String getGeoMarkerLabel();

}
