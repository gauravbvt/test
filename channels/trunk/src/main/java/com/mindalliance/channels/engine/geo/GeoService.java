package com.mindalliance.channels.engine.geo;

import com.mindalliance.channels.core.model.GeoLocation;
import com.mindalliance.channels.core.model.Place;

import java.util.List;

/**
 * Geo service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2009
 * Time: 1:46:02 PM
 */
public interface GeoService {

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
    Boolean isLikelyGeoname( String val );

    /**
     * Verify the postal code.
     *
     * @param postalCode  a string
     * @param geoLocation a geo location
     * @return a boolean
     */
    Boolean verifyPostalCode( String postalCode, GeoLocation geoLocation );

    /**
     * Get Google map API key.
     *
     * @return a string
     */
    String getGoogleMapsAPIKey();

    /**
     * Is a postal code nearby a geolocation?
     *
     * @param postalCode  a string
     * @param geoLocation a geo location
     * @return a boolean
     */
    Boolean isPostalCodeInGeoLocation( String postalCode, GeoLocation geoLocation );

    /**
     * Finds postal codes nearby a geolocation.
     *
     * @param geoLocation a geo location
     * @return a list of postal codes as strings
     */
    List<String> findNearbyPostalCodes( GeoLocation geoLocation );

    /**
     * Validate a place using a geoservice.
     *
     * @param place the place
     */
    void validate( Place place);

    /**
     * Query if this service is configured and ready to use.
     * @return true if configured
     */
    boolean isConfigured();
}
