package com.mindalliance.channels.model;

import com.mindalliance.channels.GeoService;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.attachments.Attachment;
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
        super( name );
    }

    /**
     * {@inheritDoc}
     */
    public GeoLocation geoLocate() {
        Place place = this;
        do {
            if ( place.getGeoLocation() == null )
                place = place.getWithin();
            else
                return place.getGeoLocation();

        } while ( place != null );

        return null;
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
        String address = getActualStreetAddress();
        if ( address != null ) {
            sb.append( address.trim() );
        }
        GeoLocation geoLoc = geoLocate();
        if ( geoLoc != null ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( geoLoc.toString().trim() );
        }
        String code = getActualPostalCode();
        if ( code != null && code.trim().length() > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( code.trim() );
        }
        return sb.toString();
    }

    /**
     * Get street address, possibly inherited from place it si within.
     *
     * @return a string
     */
    @Transient
    public String getActualStreetAddress() {
        Place place = this;
        do {
            String address = place.getStreetAddress();
            if ( address == null || address.isEmpty() )
                place = place.getWithin();
            else
                return address;

        } while ( place != null );

        return null;
    }

    /**
     * Get postal code, possibly inherited from place it is within.
     *
     * @return a string
     */
    @Transient
    public String getActualPostalCode() {
        Place place = this;
        do {
            String code = place.getPostalCode();
            if ( code == null ) {
                place = place.getWithin();
            } else {
                return code;
            }
        } while ( place != null );

        return null;
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
    public void setName( String name ) {
        // If geoname was empty or same as name, reset it to null
        if ( geoname != null && ( geoname.isEmpty() || geoname.equals( getName() ) ) )
            setGeoname( null );
        super.setName( name );
    }

    /**
     * Test if this place has been validated by a geoservice.
     * @return true when validate() has been called.
     * @see #validate
     */
    @Transient
    public boolean isValidated() {
        return !( geoname == null || geoLocations == null ) ;
    }

    /**
     * Validate this place using a geoservice.
     * @param service the service
     */
    public void validate( GeoService service ) {
        if ( geoname == null ) {
            String n = getName();
            boolean hasName = !( n == null || n.trim().isEmpty() );
            setGeoname( hasName && service.isLikelyGeoname( n ) ? n : "" );
        }

        boolean hasGeoName = !( geoname == null || geoname.trim().isEmpty() );
        if ( hasGeoName && geoLocations == null )
            geoLocations = service.findGeoLocations( geoname );

        if ( hasAddress() )
            service.refineWithAddress( geoLocation, streetAddress, postalCode );
    }

    @Transient
    public List<GeoLocation> getGeoLocations() {
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
        for ( Place place : queryService.list( Place.class ) ) {
            Place otherWithin = place.getWithin();
            if ( otherWithin != null && otherWithin.equals( this ) ) {
                place.setWithin( null );
            }
        }
    }

    @Transient
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation( GeoLocation geoLoc ) {
        geoLocation = geoLoc;
    }

    private boolean hasAddress() {
        boolean hasStreetAddress = !( streetAddress == null || streetAddress.isEmpty() );
        boolean hasPostalCode = !( postalCode == null || postalCode.isEmpty() );
        return geoLocation != null && ( hasStreetAddress || hasPostalCode );
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress( String address ) {
        streetAddress = address == null ? "" : address;
    }

    public String getGeoname() {
        return geoname;
    }

    /**
     * Pick a specific geoname for this place.
     * Assumes a subsequent call to validate().
     * @param geoname the geoname.
     * @see #validate
     */
    public void setGeoname( String geoname ) {
        geoLocations = null;
        geoLocation = null;
        this.geoname = geoname;
    }

    public String getPostalCode() {
        return postalCode == null ? "" : postalCode;
    }

    public void setPostalCode( String code ) {
        postalCode = code == null ? "" : code;
    }

    public Place getWithin() {
        return within;
    }

    public void setWithin( Place within ) {
        this.within = within;
    }

    /**
     * Add a geolocation to this place.
     * @param location a location
     */
    public void addGeoLocation( GeoLocation location ) {
        if ( geoLocations == null ) geoLocations = new ArrayList<GeoLocation>();
        geoLocations.add( location );
    }

    /**
     * Whether this place is equivalent to or in another.
     *
     * @param place a place
     * @return a boolean
     */
    public boolean isSameAsOrIn( Place place ) {
        return equals( place )
                || isWithin( place )
                || isGeoLocatedIn( place.geoLocate() );
    }

    /**
     * Whether this place's geolocation, if any is the same as or contained in a given geolocation.
     *
     * @param geoLoc a geo location
     * @return a boolean
     */
    public boolean isGeoLocatedIn( GeoLocation geoLoc ) {
        if ( geoLoc == null ) {
            return false;
        } else {
            GeoLocation myGeoLoc = geoLocate();
            return myGeoLoc != null && myGeoLoc.isSameAsOrInside( geoLoc );
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
        return getLoopyContainingPlace( this );
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
        return geoLoc != null ? geoLoc.getLatitude() : 0.0;
    }

    /**
     * Get longitude.
     *
     * @return a double
     */
    @Transient
    public double getLongitude() {
        GeoLocation geoLoc = geoLocate();
        return geoLoc != null ? geoLoc.getLongitude() : 0.0;
    }

    /**
     * Whether place has known latitude and longitude.
     *
     * @return a boolean
     */
    public boolean hasLatLong() {
        return geoLocate() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getName() );
        if ( within != null && getLoopyContainingPlace() == null ) {
            sb.append( " in " );
            sb.append( within.toString() );
        }
        return sb.toString();
    }

    /**
     * Whether this place is transitively within another.
     *
     * @param place a place
     * @return a boolean
     */
    public boolean isWithin( Place place ) {
        return isWithin( place, new ArrayList<Place>() );
    }

    /**
     * The place defines a geographical area.
     *
     * @return a boolean
     */
    @Transient
    public boolean isRegion() {
        String actualAddress = getActualStreetAddress();
        return ( actualAddress == null || actualAddress.isEmpty() ) && geoLocate() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public List<Attachment.Type> getAttachmentTypes() {
        List<Attachment.Type> types = new ArrayList<Attachment.Type>();
        if ( !hasImage() )
            types.add( Attachment.Type.Image );
        types.addAll( super.getAttachmentTypes() );
        return types;
    }

}
