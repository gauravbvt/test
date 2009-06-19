package com.mindalliance.channels.model;

import com.mindalliance.channels.GeoLocatable;
import com.mindalliance.channels.QueryService;
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

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A location or jurisdiction.
 */
@Entity
public class Place extends ModelObject implements GeoLocatable {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( Place.class );
    /**
     * Bogus place used to signify that the place is not known...
     */
    public static final Place UNKNOWN;
    /**
     * Unknown place's name.
     */
    public static final String UnknownPlaceName = "(unknown)";

    /**
     * Maximum number fo results retrieved from geonames per search.
     */
    private static final int MAX_SEARCH_ROWS = 10;
    /**
     * Street address.
     */
    private String streetAddress = "";
    /**
     * Postal code
     */
    private String postalCode = "";
    /**
     * A string denoting a geolocation. Null if not set. Set if empty.
     */
    private String geoname;
    /**
     * Geolocation for the geoname, if any.
     */
    private GeoLocation geoLocation;
    /**
     * Candidate geolocations for the geoname, if any.
     */
    private List<GeoLocation> geoLocations;

    static {
        UNKNOWN = new Place( UnknownPlaceName );
        UNKNOWN.setId( 10000000L - 4 );
    }

    public Place() {
    }

    public Place( String name ) {
        this();
        setName( name );
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getGeoMarkerLabel() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    @Override
    public boolean isEntity() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    @Override
    public void setName( String val ) {
        // If geoname was empty or same as name, reset it to null
        if ( geoname != null && ( geoname.isEmpty() || geoname.equals( getName() ) ) ) {
            setGeoname( null );
        }
        super.setName( val );
    }

    private boolean isLikelyGeoname( String val ) {
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

    @Transient
    public List<GeoLocation> getGeoLocations() {
        String gn = getGeoname();
        if ( geoLocations == null && gn != null && !gn.isEmpty() ) {
            geoLocations = findGeoLocations( gn );
        }
        return geoLocations;
    }

    /**
     * {@inheritDoc}
     */
    public void beforeRemove( QueryService queryService ) {
        super.beforeRemove( queryService );
        for ( Job job : queryService.findAllConfirmedJobs( ResourceSpec.with( this ) ) ) {
            job.setJurisdiction( null );
        }
        for ( Part part : queryService.findAllParts( null, ResourceSpec.with( this ) ) ) {
            part.setJurisdiction( null );
        }
        for ( Part part : queryService.findAllPartsWithLocation( this ) ) {
            part.setLocation( null );
        }
    }

    @Transient
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation( GeoLocation geoLocation ) {
        this.geoLocation = geoLocation;
    }

    // Search geonames.org

    private List<GeoLocation> findGeoLocations( String name ) {
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

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress( String streetAddress ) {
        this.streetAddress = streetAddress == null ? "" : streetAddress;
    }

    public String getGeoname() {
        String name = getName();
        if ( geoname == null ) {
            if ( name != null
                    && !name.trim().isEmpty()
                    && isLikelyGeoname( name ) ) {
                geoname = name;
            } else {
                geoname = "";
            }
        }
        return geoname;
    }

    public void setGeoname( String val ) {
        geoLocations = null;
        geoLocation = null;
        geoname = val;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode( String postalCode ) {
        this.postalCode = postalCode == null ? "" : postalCode;
    }

    public void addGeoLocation( GeoLocation geoLocation ) {
        if ( geoLocations == null ) geoLocations = new ArrayList<GeoLocation>();
        geoLocations.add( geoLocation );
    }

    /**
     * Verify the postal code.
     *
     * @return a boolean
     */
    public boolean verifyPostalCode() {
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

    /**
     * Whether this place's geolocation is the same or within another's.
     *
     * @param place a place
     * @return a boolean
     */
    public boolean isWithin( Place place ) {
        return !( geoLocation == null || place.getGeoLocation() == null )
                && geoLocation.isWithin( place.getGeoLocation() );
    }

    /**
     * Get latitude.
     *
     * @return a double
     */
    @Transient
    public double getLatitude() {
        return geoLocation != null ? geoLocation.getLatitude()  : 0;
    }

    /**
     * Get longitude.
     *
     * @return a double
     */
    @Transient
    public double getLongitude() {
        return geoLocation != null ?  geoLocation.getLongitude() : 0;
    }

    /**
     * Whether place has known latitude and longitude.
     *
     * @return a boolean
     */
    public boolean hasLatLong() {
        return geoLocation != null;
    }

}
