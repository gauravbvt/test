package com.mindalliance.channels.model;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.GeoService;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * A location or jurisdiction.
 */
@Entity
public class Place extends ModelObject implements GeoLocatable {

    /**
     * Bogus place used to signify that the place is not known...
     */
    public static final Place UNKNOWN;
    /**
     * Unknown place's name.
     */
    public static final String UnknownPlaceName = "(unknown)";

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

    @Transient
    public List<GeoLocation> getGeoLocations() {
        String gn = getGeoname();
        if ( geoLocations == null && gn != null && !gn.isEmpty() ) {
            geoLocations = getGeoService().findGeoLocations( gn );
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
                    && getGeoService().isLikelyGeoname( name ) ) {
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

    private GeoService getGeoService() {
        return Channels.instance().getGeoService();
    }

}
