package com.mindalliance.channels.geo;

import com.mindalliance.channels.geo.GeoLocation;

import java.net.URL;
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
    Boolean verifyPostalCode( final String postalCode, GeoLocation geoLocation );

    /**
     * Refine latlong values  from Google geocoder if address or postal code known.
     *
     * @param geoLocation   a geolocation
     * @param streetAddress street address
     * @param postalCode    postal code
     */
    void refineWithAddress( GeoLocation geoLocation, String streetAddress, String postalCode );

    /**
     * Get CSV result from Google geocoder: "http_code,precision,lat,long".
     * Example: 200,6,42.730070,-73.690570
     *
     * @param restUrl a URL
     * @return a string
     */
    String getGeoCoding( URL restUrl );

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
    Boolean isPostalCodeInGeoLocation( final String postalCode, GeoLocation geoLocation );

    /**
     * Finds postal codes nearby a geolocation.
     *
     * @param geoLocation a geo location
     * @return a list of postal codes as strings
     */
    List<String> findNearbyPostalCodes( GeoLocation geoLocation );
}
