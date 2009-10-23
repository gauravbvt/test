package com.mindalliance.channels.model;

import com.mindalliance.channels.GeoService;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * A location or jurisdiction.
 */
@Entity
public class Place extends ModelEntity implements GeoLocatable {

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
     * The actual place, if any, this actual place is directly in.
     */
    private Place within;
    /**
     * The place a place of this type must contain.
     */
    private PlaceReference mustContain = new PlaceReference();
    /**
     * The place a place this type must be within.
     */
    private PlaceReference mustBeWithin = new PlaceReference();

    static {
        UNKNOWN = new Place( UnknownPlaceName );
        UNKNOWN.setActual();
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
    @Transient
    @Override
    public void setName( String name ) {
        // If geoname was empty or same as name, reset it to null
        if ( geoname != null && ( geoname.isEmpty() || geoname.equals( getName() ) ) )
            setGeoname( null );
        super.setName( name );
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
     *
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
        assert within == null || isActual() && within.isActual();
        this.within = within;
    }

    public PlaceReference getMustContain() {
        return mustContain;
    }

    public void setMustContain( PlaceReference mustContain ) {
        assert isType();
        this.mustContain = mustContain;
    }

    public PlaceReference getMustBeWithin() {
        return mustBeWithin;
    }

    public void setMustBeWithin( PlaceReference mustBeWithin ) {
        assert isType();
        this.mustBeWithin = mustBeWithin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean narrowsOrEquals( ModelEntity other ) {
        if ( other == null ) return false;
        if ( isActual() ) {
            if ( other.isActual() ) {
                return isSameAsOrIn( ( (Place) other ) );
            } else {
                // actual matched with a type
                // compatible types?
                if ( !super.narrowsOrEquals( other ) ) return false;
                // Check that this contains any place specified by the place type (aka other)
                PlaceReference contained = ( (Place) other ).getMustContain();
                if ( contained.isSet() ) {
                    if ( !contained.narrowsOrEquals( this ) ) return false;
                }
                // Check that any place specified by the place type (aka other) contains this
                PlaceReference container = ( (Place) other ).getMustBeWithin();
                if ( container.isSet() ) {
                    if ( !narrowsOrEquals( container.getReferencedPlace() ) ) return false;
                }
                return true;
            }
        } else {
            // is type
            if ( !super.narrowsOrEquals( other ) ) return false;
            assert other.isType();
            PlaceReference otherMustContain = ( (Place) other ).getMustContain();
            if ( otherMustContain.isSet() ) {
                if ( !mustContain.isSet() ) return false;
                // other mustContain test must be less stringent
                // "must contain NJ" is more stringent than "must contain Morristown" because
                // some places that contain Morristown do not contain NJ, but all places containing
                // NJ contain Morristown. so "must contain NJ" narrows "must contain Morristown"
                // because Morristown narrows NJ
                if ( !otherMustContain.narrowsOrEquals( mustContain ) ) return false;
            }
            PlaceReference otherMustBeWithin = ( (Place) other ).getMustBeWithin();
            if ( otherMustBeWithin.isSet() ) {
                if ( !mustBeWithin.isSet() ) return false;
                if ( !mustBeWithin.narrowsOrEquals( otherMustBeWithin ) ) return false;
            }
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDefinedUsing( final ModelEntity entity ) {
        return super.isDefinedUsing( entity )
                ||
                CollectionUtils.exists(
                        containment(),
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return ModelEntity.isEquivalentToOrDefinedUsing(
                                        (Place) obj,
                                        entity );
                            }
                        }
                );
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends GeoLocatable> getImpliedGeoLocatables( QueryService queryService ) {
        List<Place> geoLocatables = new ArrayList<Place>();
        for ( Place place : queryService.listEntitiesNarrowingOrEqualTo( this ) ) {
            if ( place.isActual() ) {
                GeoLocation geoLoc = place.geoLocate();
                if ( geoLoc != null ) {
                    geoLocatables.add( place );
                }
            }
        }
        return geoLocatables;
    }

    /**
     * Returns whether places are exactly the same or both are null.
     *
     * @param place -- a string
     * @param other -- another string
     * @return -- whether they are similar
     */
    public static boolean samePlace( Place place, Place other ) {
        if ( place == null && other == null )
            return true;
        if ( place == null || other == null )
            return false;
        return place.equals( other );
    }

    /**
     * Find all places this one is contained in, avoiding circularities.
     *
     * @return a list of places
     */
    public List<Place> containment() {
        return safeContainment( new HashSet<Place>() );
    }

    private List<Place> safeContainment( HashSet<Place> visited ) {
        List<Place> containers = new ArrayList<Place>();
        if ( !visited.contains( this ) ) {
            visited.add( this );
            if ( within != null ) {
                containers.add( within );
                containers.addAll( within.safeContainment( visited ) );
            }
        }
        return containers;
    }


    /**
     * {@inheritDoc}
     */
    public GeoLocation geoLocate() {
        GeoLocation geoLoc = getGeoLocation();
        if ( geoLoc == null ) {
            Iterator<Place> containers = containment().iterator();
            while ( geoLoc == null && containers.hasNext() ) {
                geoLoc = containers.next().getGeoLocation();
            }
        }
        return geoLoc;
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
     * Get street address, possibly inherited from containing places.
     *
     * @return a string
     */
    @Transient
    public String getActualStreetAddress() {
        String street = getStreetAddress();
        if ( street == null || street.isEmpty() ) {
            Iterator<Place> containers = containment().iterator();
            while ( street == null && containers.hasNext() ) {
                street = containers.next().getStreetAddress();
            }
        }
        return street;
    }

    /**
     * Get postal code, possibly inherited from containing places.
     *
     * @return a string
     */
    @Transient
    public String getActualPostalCode() {
        String code = getPostalCode();
        if ( code == null || code.isEmpty() ) {
            Iterator<Place> containers = containment().iterator();
            while ( code == null && containers.hasNext() ) {
                code = containers.next().getPostalCode();
            }
        }
        return code;
    }

    /**
     * Test if this place has been validated by a geoservice.
     *
     * @return true when validate() has been called.
     * @see #validate
     */
    @Transient
    public boolean isValidated() {
        return !( geoname == null || geoLocations == null );
    }

    /**
     * Validate this place using a geoservice.
     *
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
    public List<GeoLocation> getGeoLocations( QueryService queryService ) {
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
        for ( Part part : queryService.findAllPartsWithExactLocation( this ) ) {
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

    /**
     * Add a geolocation to this place.
     *
     * @param location a location
     */
    public void addGeoLocation( GeoLocation location ) {
        if ( geoLocations == null ) geoLocations = new ArrayList<GeoLocation>();
        geoLocations.add( location );
    }

    /**
     * Whether this place is equivalent to or is in another.
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
     * Whether this actual place is within another actual.
     * Checks if any of the places this one is contained in equals a given place.
     *
     * @param place a place
     * @return a boolean
     */
    public boolean isWithin( final Place place ) {
        return isActual() && place.isActual()
                &&
                CollectionUtils.exists(
                        containment(),
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return obj.equals( place );
                            }
                        }
                );
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

    public boolean references( ModelObject mo ) {
        return super.references( mo )
                || ModelObject.areIdentical( within, mo )
                || mustBeWithin.references( mo )
                || mustContain.references( mo );
    }
}
