package com.mindalliance.channels;

import com.mindalliance.channels.geo.GeoLocation;

import java.util.List;

/**
 * Geo service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2009
 * Time: 1:46:02 PM
 */
public interface GeoService extends Service {

    /**
     * Find geolocations given a name.
     *
     * @param name a string
     * @return a list of geo locations
     */
    List<GeoLocation> findGeoLocations( String name );

    /**
     * Whether a string likely referes to a geo feature.
     *
     * @param val a string
     * @return a boolean
     */
    boolean isLikelyGeoname( String val );

    /**
     * Verify the postal code.
     *
     * @param postalCode  a string
     * @param geoLocation a geo location
     * @return a boolean
     */
    boolean verifyPostalCode( final String postalCode, GeoLocation geoLocation );

    /**
     * Get Google map API key.
     *
     * @return a string
     */
    String getGoogleMapsAPIKey();
}
