// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.engine.geo;

import com.mindalliance.channels.core.model.GeoLocation;
import com.mindalliance.channels.core.model.Place;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;
import java.util.List;

/** A proxy that calls the actual geoservice only when configured properly. */
public class ServiceProxy implements GeoService, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger( ServiceProxy.class );

    private DefaultGeoService actual;

    public ServiceProxy() {
    }

    public DefaultGeoService getActual() {
        return actual;
    }

    public void setActual( DefaultGeoService actual ) {
        this.actual = actual;
    }

    public void setGoogleMapsAPIKey( String googleMapsAPIKey ) {
        actual.setGoogleMapsAPIKey( googleMapsAPIKey );
    }

    public void setGeonamesServer( String geonamesServer ) {
        actual.setGeonamesServer( geonamesServer );
    }

    public void setGeonamesUserName( String geonamesUserName ) {
        actual.setGeonamesUserName( geonamesUserName );
    }

    public void setGeonamesToken( String geonamesToken ) {
        actual.setGeonamesToken( geonamesToken );
    }

    @Override
    public List<GeoLocation> findGeoLocations( String name ) {
        if ( isConfigured() )
            return actual.findGeoLocations( name );
        else
            return Collections.emptyList();
    }

    @Override
    public Boolean isLikelyGeoname( String val ) {
        return isConfigured() && actual.isLikelyGeoname( val );
    }

    @Override
    public Boolean verifyPostalCode( String postalCode, GeoLocation geoLocation ) {
        return !isConfigured() || actual.verifyPostalCode( postalCode, geoLocation );
    }

    @Override
    public String getGoogleMapsAPIKey() {
        return actual.getGoogleMapsAPIKey();
    }

    @Override
    public Boolean isPostalCodeInGeoLocation( String postalCode, GeoLocation geoLocation ) {
        return !isConfigured() || actual.isPostalCodeInGeoLocation( postalCode, geoLocation );
    }

    @Override
    public List<String> findNearbyPostalCodes( GeoLocation geoLocation ) {
        if ( isConfigured() )
            return actual.findNearbyPostalCodes( geoLocation );
        else
            return Collections.emptyList();
    }

    @Override
    public void validate( Place place ) {
        if ( isConfigured() )
            actual.validate( place );
    }

    @Override
    public boolean isConfigured() {
        boolean result = actual.isConfigured();
        if ( !result )
            LOG.warn( "Geonames service not configured" );

        return result;
    }

    @Override
    public void afterPropertiesSet() {
        LOG.info( "Geonames service: {}", actual.isConfigured() ? "configured" : "not configured" );
    }
}
