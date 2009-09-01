package com.mindalliance.channels.geo;

import com.mindalliance.channels.AbstractService;
import com.mindalliance.channels.GeoService;
import com.mindalliance.channels.model.Place;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.TransformerUtils;
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
import org.springframework.beans.factory.InitializingBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
public class DefaultGeoService extends AbstractService implements GeoService, InitializingBean {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultGeoService.class );
    /**
     * Maximum number fo results retrieved from geonames per search.
     */
    private static final int MAX_SEARCH_ROWS = 15;
    /**
     * Geonames server to use.
     */
    private String geonamesServer;
    /**
     * Authentication user name.
     */
    private String geonamesUserName;
    /**
     * Authentication token.
     */
    private String geonamesToken;

    /**
     * Google map's API key.
     */
    private String googleMapsAPIKey;
    /**
     * Google maps geocoding uri.
     */
    private static final Object GOOGLE_MAP_URI = "http://maps.google.com/maps/geo";

    public DefaultGeoService() {
    }

    public String getGoogleMapsAPIKey() {
        return googleMapsAPIKey;
    }

    public void setGoogleMapsAPIKey( String googleMapsAPIKey ) {
        this.googleMapsAPIKey = googleMapsAPIKey;
    }

    public void setGeonamesServer( String geonamesServer ) {
        this.geonamesServer = geonamesServer;
    }

    public void setGeonamesUserName( String geonamesUserName ) {
        this.geonamesUserName = geonamesUserName;
    }

    public void setGeonamesToken( String geonamesToken ) {
        this.geonamesToken = geonamesToken;
    }

    public void afterPropertiesSet() throws Exception {
        if ( geonamesServer != null)
            WebService.setGeoNamesServer( geonamesServer );
        if ( geonamesUserName != null)
            WebService.setUserName( geonamesUserName );
        if ( geonamesToken != null)
            WebService.setToken( geonamesToken );
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
            LOG.debug( "Geonames search: toponyms for " + name );
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
    public Boolean isLikelyGeoname( String val ) {
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
    public Boolean verifyPostalCode( final String postalCode, GeoLocation geoLocation ) {
        try {
            return isPostalCodeInGeoLocation( postalCode, geoLocation );
        } catch ( Exception e ) {
            LOG.warn( "Failed to verify postal code " + postalCode + " in " + geoLocation, e );
            return false;
        }
    }

    public Boolean isPostalCodeInGeoLocation( String postalCode, GeoLocation geoLocation ) {
        final String code = postalCode;
        if ( postalCode.isEmpty() || geoLocation == null ) return false;
        List<String> postalCodes = findNearbyPostalCodes( geoLocation );
        String match = (String) CollectionUtils.find(
                postalCodes,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return obj.equals( code );
                    }
                } );
        return match != null;
    }

    /**
     * {@inheritDoc
     */
    @SuppressWarnings( "unchecked" )
    public List<String> findNearbyPostalCodes( GeoLocation geoLocation ) {
        try {
            PostalCodeSearchCriteria criteria = new PostalCodeSearchCriteria();
//            criteria.setCountryCode( geoLocation.getCountryCode() );
//            criteria.setAdminCode1( geoLocation.getStateCode() );
            criteria.setLatitude( geoLocation.getLatitude() );
            criteria.setLongitude( geoLocation.getLongitude() );
            criteria.setStyle( Style.FULL );
            LOG.debug( "Geonames search: finding nearby postal codes for " + geoLocation );
            List<PostalCode> postalCodes = WebService.findNearbyPostalCodes( criteria );
            LOG.debug( "Found " + postalCodes.size() + " for " + geoLocation );
            return (List<String>) CollectionUtils.collect( postalCodes, TransformerUtils.invokerTransformer( "toString" ) );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void refineWithAddress( GeoLocation geoLocation, String streetAddress, String postalCode ) {
        if ( !geoLocation.isRefinedTo( streetAddress, postalCode ) ) {
            // Lat/Long refinement is obsolete, must re-obtain lat/long for address.
            String rest = makeGoogleGeocodingURL( geoLocation, streetAddress, postalCode, "csv" );
            try {
                URL url = new URL( rest );
                String csv = getGeoCoding( url );
                LOG.debug( rest + " => " + csv );
                String[] results = csv.split( "," );
                if ( results[0].equals( "200" ) ) {
                    int precision = Integer.valueOf( results[1] );
                    double latitude = Double.valueOf( results[2] );
                    double longitude = Double.valueOf( results[3] );
                    geoLocation.setPrecision( precision );
                    geoLocation.setLatitude( latitude );
                    geoLocation.setLongitude( longitude );
                    // Record the fact that refinement was done
                    geoLocation.setStreetAddress( streetAddress == null ? "" : streetAddress );
                    geoLocation.setPostalCode( postalCode == null ? "" : postalCode );
                }

            } catch ( MalformedURLException e ) {
                throw new RuntimeException( e );
            } catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getGeoCoding( URL restUrl ) {
        try {
            LOG.debug( "Google geocoding search:" + restUrl );
            InputStream in = restUrl.openStream();
            BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
            String csv = reader.readLine();
            LOG.debug( "Found " + csv );
            return csv;
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    private String makeGoogleGeocodingURL(
            GeoLocation geoLocation,
            String streetAddress,
            String postalCode,
            String outputFormat ) {
        StringBuilder loc = new StringBuilder();
        if ( streetAddress != null ) {
            loc.append( streetAddress );
            loc.append( ", " );
        }
        loc.append( geoLocation.toString() );
        if ( postalCode != null ) {
            loc.append( ", " );
            loc.append( postalCode );
        }
        return makeGoogleGeocodingURL( loc.toString(), outputFormat );
    }

    private String makeGoogleGeocodingURL(
            String name,
            String outputFormat ) {
        StringBuilder sb = new StringBuilder();
        sb.append( GOOGLE_MAP_URI );
        sb.append( "?" );
        sb.append( "q=" );
        try {
            sb.append( URLEncoder.encode( name, "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        }
        sb.append( "&key=" );
        sb.append( googleMapsAPIKey );
        sb.append( "&sensor=false" );
        sb.append( "&output=" );
        sb.append( outputFormat );
        return sb.toString();
    }


}
