/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */
package com.mindalliance.channels.engine.geo;

import com.mindalliance.channels.core.model.GeoLocation;
import com.mindalliance.channels.core.model.Place;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.StringUtils;
import org.geonames.InsufficientStyleException;
import org.geonames.InvalidParameterException;
import org.geonames.PostalCode;
import org.geonames.PostalCodeSearchCriteria;
import org.geonames.Style;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Geo service implementation.
 */
public class DefaultGeoService implements GeoService {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultGeoService.class );

    /**
     * Google maps geocoding uri.
     */
    private static final Object GOOGLE_MAP_URI = "http://maps.google.com/maps/geo";

    /**
     * Maximum number fo results retrieved from geonames per search.
     */
    private static final int MAX_SEARCH_ROWS = 15;
    private static final List<String> CITY_CODES = Arrays.asList( "PPL", "PPLA", "PPLC" );

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

    public DefaultGeoService() {
    }

    @Override
    public String getGoogleMapsAPIKey() {
        return googleMapsAPIKey;
    }

    public void setGoogleMapsAPIKey( String googleMapsAPIKey ) {
        this.googleMapsAPIKey = googleMapsAPIKey;
    }

    public void setGeonamesServer( String geonamesServer ) {
        this.geonamesServer = geonamesServer;
        if ( geonamesServer != null )
            WebService.setGeoNamesServer( geonamesServer );
    }

    public void setGeonamesUserName( String geonamesUserName ) {
        this.geonamesUserName = geonamesUserName;
        if ( geonamesUserName != null )
            WebService.setUserName( geonamesUserName );
    }

    public void setGeonamesToken( String geonamesToken ) {
        this.geonamesToken = geonamesToken;
        if ( geonamesToken != null )
            WebService.setToken( geonamesToken );
    }

    @Override
    public List<GeoLocation> findGeoLocations( String name ) {
        List<String> geoLocStrings = new ArrayList<String>();
        List<GeoLocation> results = new ArrayList<GeoLocation>();

        try {
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();

            searchCriteria.setQ( name );
            searchCriteria.setStyle( Style.FULL );
            searchCriteria.setMaxRows( MAX_SEARCH_ROWS );
            LOG.trace( "Searching: toponyms for {}", name );

            ToponymSearchResult searchResult = WebService.search( searchCriteria );

            LOG.debug( "Found {} toponyms for {}", searchResult.getTotalResultsCount(), name );
            for ( Toponym topo : searchResult.getToponyms() ) {
                GeoLocation geoLoc = newGeoLocation( topo );
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

    private static GeoLocation newGeoLocation( Toponym toponym ) {
        GeoLocation location = new GeoLocation();

        location.setCountry( toponym.getCountryName() );
        try {
            location.setState( toponym.getAdminName1() );
        } catch ( InsufficientStyleException ignored ) {
        }

        try {
            location.setCounty( toponym.getAdminName2() );
        } catch ( InsufficientStyleException ignored ) {
        }

        location.setCity( CITY_CODES.contains( toponym.getFeatureCode() ) ? toponym.getName()
                : null );
        location.setCountryCode( toponym.getCountryCode() );
        try {
            location.setStateCode( toponym.getAdminCode1() );
        } catch ( InsufficientStyleException ignored ) {
        }

        try {
            location.setCountyCode( toponym.getAdminCode2() );
        } catch ( InsufficientStyleException ignored ) {
        }

        location.setCityCode( CITY_CODES.contains( toponym.getFeatureCode() ) ? toponym.getFeatureCode()
                : null );
        location.setLatitude( toponym.getLatitude() );
        location.setLongitude( toponym.getLongitude() );
        location.setGeonameId( toponym.getGeoNameId() );
        return location;
    }

    @Override
    public Boolean isLikelyGeoname( String val ) {
        if ( val == null || val.isEmpty() || val.equals( Place.UnknownPlaceName ) )
            return false;

        List<GeoLocation> geoLocs = findGeoLocations( val );

        if ( geoLocs.isEmpty() )
            return false;

        final List<String> geonameTokens = Arrays.asList( StringUtils.split( val, ",. :;-'\"" ) );

        return CollectionUtils.exists(
                geoLocs,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        List<String> geoLocTokens = Arrays.asList( StringUtils.split( object.toString(), ",. :;-'\"" ) );
                        return !CollectionUtils.intersection( geonameTokens, geoLocTokens ).isEmpty();
                    }
                } );
    }

    @Override
    public Boolean verifyPostalCode( String postalCode, GeoLocation geoLocation ) {
        try {
            return isPostalCodeInGeoLocation( postalCode, geoLocation );

        } catch ( Exception e ) {
            LOG.warn( "Failed to verify postal code " + postalCode + " in " + geoLocation, e );
            return false;
        }
    }

    @Override
    public Boolean isPostalCodeInGeoLocation( final String postalCode, GeoLocation geoLocation ) {
        if ( postalCode.isEmpty() || geoLocation == null )
            return false;

        return CollectionUtils.exists(
                findNearbyPostalCodes( geoLocation ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return object.equals( postalCode );
                    }
                } );
    }

    @Override
    public List<String> findNearbyPostalCodes( GeoLocation geoLocation ) {
        try {
            PostalCodeSearchCriteria criteria = new PostalCodeSearchCriteria();

            //            criteria.setCountryCode( geoLocation.getCountryCode() );
            //            criteria.setAdminCode1( geoLocation.getStateCode() );
            criteria.setLatitude( geoLocation.getLatitude() );
            criteria.setLongitude( geoLocation.getLongitude() );
            criteria.setStyle( Style.FULL );
            LOG.trace( "Finding nearby postal codes for {}", geoLocation );

            List<PostalCode> postalCodes = WebService.findNearbyPostalCodes( criteria );

            LOG.debug( "Found {} for {}", postalCodes.size(), geoLocation );

            List<String> answer = new ArrayList<String>( postalCodes.size() );

            CollectionUtils.collect( postalCodes, TransformerUtils.invokerTransformer( "toString" ), answer );
            return answer;

        } catch ( InvalidParameterException e ) {
            throw new RuntimeException( e );

        } catch ( Exception e ) {
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

    private String makeGoogleGeocodingURL( String name, String outputFormat ) {
        StringBuilder sb = new StringBuilder();

        sb.append( GOOGLE_MAP_URI );
        sb.append( '?' );
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

    /**
     * Validate this place using a geoservice.
     *
     * @param place the place to validate
     */
    @Override
    public void validate( Place place ) {
        String geoname = place.getGeoname();

        if ( geoname == null )
            place.setGeoname( getLikelyGeoname( place.getName() ) );
        else if ( !geoname.trim().isEmpty() && place.getGeoLocations() == null )
            place.setGeoLocations( findGeoLocations( geoname ) );

        if ( place.hasAddress() )
            setPosition( place.getGeoLocation(), place.getStreetAddress(), place.getPostalCode() );
    }

    private boolean setPosition( GeoLocation location, String streetAddress, String postalCode ) {
        int responseCode = 0;
        try {
            String coding = getGeoCoding( location, streetAddress, postalCode );
            String[] result = coding.split( "," );
             responseCode = Integer.valueOf( result[0] );
            if ( HttpServletResponse.SC_OK == responseCode ) {
                location.setPosition(
                        Double.valueOf( result[2] ),
                        Double.valueOf( result[3] ),
                        Integer.valueOf( result[1] ) );
                return true;
            }

        } catch ( IOException e ) {
            LOG.warn( "Failed to set position of geolocation "
                    + location + " at "
                    + streetAddress + ", "
                    + postalCode
                    + " [Response code = "
                    + responseCode
                    + "]");
        }
        return false;
    }

    /**
     * Get CSV result from Google geocoder. Example: 200,6,42.730070,-73.690570
     *
     * @param location      the location
     * @param streetAddress a street address
     * @param postalCode    a postal code
     * @return a string "http_code,precision,lat,long"
     * @throws IOException on errors
     */
    private String getGeoCoding( GeoLocation location, String streetAddress, String postalCode ) throws IOException {
        URL restUrl = new URL( makeGoogleGeocodingURL( location, streetAddress, postalCode, "csv" ) );

        LOG.debug( "Google geocoding search: {}", restUrl );

        BufferedReader reader = new BufferedReader( new InputStreamReader( restUrl.openStream() ) );

        try {
            String csv = reader.readLine();

            LOG.debug( "Found {}", csv );
            return csv;

        } finally {
            reader.close();
        }
    }

    @Override
    public boolean isConfigured() {
        return !(
                geonamesServer == null || geonamesUserName == null || geonamesToken == null || googleMapsAPIKey == null
        );
    }

    private String getLikelyGeoname( String name ) {
        return name != null && !name.trim().isEmpty() && isLikelyGeoname( name ) ? name
                : "";
    }
}
