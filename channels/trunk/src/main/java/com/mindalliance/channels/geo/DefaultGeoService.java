package com.mindalliance.channels.geo;

import com.mindalliance.channels.AbstractService;
import com.mindalliance.channels.GeoService;
import com.mindalliance.channels.model.Place;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.geonames.PostalCode;
import org.geonames.PostalCodeSearchCriteria;
import org.geonames.Style;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Geo service implementation.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2009
 * Time: 1:30:08 PM
 */
public class DefaultGeoService extends AbstractService implements GeoService {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultGeoService.class );
    /**
     * Maximum number fo results retrieved from geonames per search.
     */
    private static final int MAX_SEARCH_ROWS = 10;

    /**
     * Google map's API key.
     */
    private String googleMapsAPIKey;

    public DefaultGeoService() {
    }

    public String getGoogleMapsAPIKey() {
        return googleMapsAPIKey;
    }

    public void setGoogleMapsAPIKey( String googleMapsAPIKey ) {
        this.googleMapsAPIKey = googleMapsAPIKey;
    }


    /**
     * {@inheritDoc}
     */
    public List<GeoLocation> findGeoLocations( String name ) {
        List<String> geoLocStrings = new ArrayList<String>();
        List<GeoLocation> results = new ArrayList<GeoLocation>();
        try {
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
            searchCriteria.setQ( name );
            searchCriteria.setStyle( Style.FULL );
            searchCriteria.setMaxRows( MAX_SEARCH_ROWS );
            ToponymSearchResult searchResult = WebService.search( searchCriteria );
            LOG.debug( "Found " + searchResult.getTotalResultsCount() + " toponyms for " + name );
            for ( Toponym topo : searchResult.getToponyms() ) {
                GeoLocation geoLoc = new GeoLocation( topo );
                String s = geoLoc.toString();
                if ( !geoLocStrings.contains( s ) ) {
                    results.add( geoLoc );
                    geoLocStrings.add( s );
                }
            }
        } catch ( Exception e ) {
            LOG.warn( "Geonames search failed.", e );
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLikelyGeoname( String val ) {
        if ( val == null || val.isEmpty() || val.equals( Place.UnknownPlaceName ) ) return false;
        List<GeoLocation> geoLocs = findGeoLocations( val );
        final List<String> geonameTokens = Arrays.asList( StringUtils.split( val, ",. :;-'\"" ) );
        if ( geoLocs.isEmpty() ) return false;
        GeoLocation match = (GeoLocation) CollectionUtils.find(
                geoLocs,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        List<String> geoLocTokens = Arrays.asList( StringUtils.split(
                                obj.toString(),
                                ",. :;-'\"" ) );
                        return !CollectionUtils.intersection( geonameTokens, geoLocTokens ).isEmpty();
                    }
                } );
        return match != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean verifyPostalCode( final String postalCode, GeoLocation geoLocation ) {
        if ( postalCode.isEmpty() || geoLocation == null ) return false;
        try {
            PostalCodeSearchCriteria criteria = new PostalCodeSearchCriteria();
//            criteria.setCountryCode( geoLocation.getCountryCode() );
//            criteria.setAdminCode1( geoLocation.getStateCode() );
            criteria.setLatitude( geoLocation.getLatitude() );
            criteria.setLongitude( geoLocation.getLongitude() );
            // criteria.setPostalCode( postalCode );
            criteria.setStyle( Style.FULL );
            List<PostalCode> postalCodes = WebService.findNearbyPostalCodes( criteria );
            PostalCode match = (PostalCode) CollectionUtils.find(
                    postalCodes,
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return ( (PostalCode) obj ).getPostalCode().equals( postalCode );
                        }
                    } );
            return match != null;
        } catch ( Exception e ) {
            LOG.warn( "Failed to verify postal code " + postalCode + " in " + geoLocation, e );
            return false;
        }
    }


}
