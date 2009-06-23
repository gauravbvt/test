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
    /**
     * The place, if any, this one is directly in.
     */
    private Place within;

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
    public GeoLocation geoLocate() {
        if ( geoLocation != null ) {
            return geoLocation;
        } else {
            if ( within != null ) {
                return within.geoLocate();
            } else {
                return null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getGeoMarkerLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append( toString() );
        String fullAddress = getFullAddress();
        if ( !fullAddress.isEmpty() ) {
            sb.append( " at " );
            sb.append( fullAddress );
        }
        return sb.toString();
    }

    /**
     * Get full address.
     *
     * @return a string
     */
    @Transient
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if ( streetAddress != null ) {
            sb.append( streetAddress.trim() );
        }
        GeoLocation geoLoc = geoLocate();
        if ( geoLoc != null ) {
            if (sb.length() > 0 ) sb.append( ", " );
            sb.append( geoLoc.toString().trim() );
        }
        if ( postalCode != null && postalCode.trim().length() > 0 ) {
            if (sb.length() > 0 ) sb.append( ", " );
            sb.append( postalCode.trim() );
        }
        return sb.toString();
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
        // TODO - cleanup within
    }

    @Transient
    public GeoLocation getLocation() {
        return geoLocation;
    }

    public void setGeoLocation( GeoLocation geoLoc ) {
        geoLocation = geoLoc;
        if ( geoLocation != null
                && hasAddress() ) {
            getGeoService().refineWithAddress( geoLocation, streetAddress, postalCode );
        }
    }

    private boolean hasAddress() {
        return geoLocation != null
                && ( ( streetAddress != null && !streetAddress.isEmpty() )
                || ( postalCode != null && !postalCode.isEmpty() ) );
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress( String address ) {
        boolean refine = streetAddress != null && address != null && !streetAddress.equals( address );
        streetAddress = address == null ? "" : address;
        if ( refine && hasAddress() ) {
            getGeoService().refineWithAddress( geoLocation, address, postalCode );
        }
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

    public void setPostalCode( String code ) {
        boolean refine = postalCode != null && code != null && !postalCode.equals( code );
        this.postalCode = code == null ? "" : code;
        if ( refine && hasAddress() ) {
            getGeoService().refineWithAddress( geoLocation, streetAddress, code );
        }
    }

    public Place getWithin() {
        return within;
    }

    public void setWithin( Place within ) {
        this.within = within;
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
        return isWithin( place, new ArrayList<Place>() )
                || isGeoLocatedIn( place.geoLocate() );
    }

    private boolean isGeoLocatedIn( GeoLocation geoLoc ) {
        if ( geoLoc == null ) {
            return false;
        } else {
            GeoLocation myGeoLoc = geoLocate();
            return myGeoLoc != null && myGeoLoc.isWithin( geoLoc );
        }
    }

    private boolean isWithin( Place place, List<Place> contained ) {
        // Protect against circularity
        if ( contained.contains( this ) ) return false;
        if ( within != null ) {
            if ( within.equals( place ) ) {
                return true;
            } else {
                contained.add( this );
                return within.isWithin( place, contained );
            }
        } else {
            return false;
        }
    }

    /**
     * Get a place transitively containing this place that is also within this place.
     *
     * @return a place or null
     */
    @Transient
    public Place getLoopyContainingPlace() {
        if ( within == null ) {
            return null;
        } else
            return within.getLoopyContainingPlace( this );
    }

    private Place getLoopyContainingPlace( Place place ) {
        if ( within != null ) {
            if ( within.equals( place ) ) {
                return this;
            } else {
                return within.getLoopyContainingPlace( place );
            }
        } else {
            return null;
        }
    }

    /**
     * Get latitude.
     *
     * @return a double
     */
    @Transient
    public double getLatitude() {
        GeoLocation geoLoc = geoLocate();
        return geoLoc != null ? geoLoc.getLatitude() : 0;
    }

    /**
     * Get longitude.
     *
     * @return a double
     */
    @Transient
    public double getLongitude() {
        GeoLocation geoLoc = geoLocate();
        return geoLoc != null ? geoLoc.getLongitude() : 0;
    }

    /**
     * Whether place has known latitude and longitude.
     *
     * @return a boolean
     */
    public boolean hasLatLong() {
        return geoLocate() != null;
    }

    @Transient
    private GeoService getGeoService() {
        return Channels.instance().getGeoService();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getName() );
        if ( within != null ) {
            sb.append( " in " );
            sb.append( within.toString() );
        }
        return sb.toString();
    }

}
