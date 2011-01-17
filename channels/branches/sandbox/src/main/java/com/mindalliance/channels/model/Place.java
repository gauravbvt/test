package com.mindalliance.channels.model;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.geo.GeoService;
import com.mindalliance.channels.query.QueryService;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A location or jurisdiction.
 */
public class Place extends ModelEntity implements GeoLocatable, Specable {

    /**
     * Name of pre-fab administrative area place type.
     */
    public static final String ADMINISTRATIVE_AREA = "Administrative area";

    /**
     * Name of pre-fab country place type.
     */
    public static final String COUNTRY = "Country";

    /**
     * Name of pre-fab state place type.
     */
    public static final String STATE = "State";

    /**
     * Name of pre-fab county place type.
     */
    public static final String COUNTY = "County";

    /**
     * Name of pre-fab city place type.
     */
    public static final String CITY = "City";

    /**
     * Immutable place type.
     */
    public static Place Country;

    /**
     * Immutable place type.
     */
    public static Place State;

    /**
     * Immutable place type.
     */
    public static Place County;

    /**
     * Immutable place type.
     */
    public static Place City;

    /**
     * Bogus place used to signify that the place is not known...
     */
    public static Place UNKNOWN;

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
     * The actual place, if any, this place is directly in.
     */
    private Place within;

    /**
     * The place a place of this type must contain.
     */
    private PlaceReference mustContain = new PlaceReference();

    /**
     * The place a place this type must be contained in.
     */
    private PlaceReference mustBeContainedIn = new PlaceReference();

    public Place() {
    }

    public Place( String name ) {
        super( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ModelEntity> getImplicitTags() {
        List<ModelEntity> implicitTags = new ArrayList<ModelEntity>();
        if ( isRegion() ) {
            GeoLocation geo = geoLocate();
            if ( geo.isCity() )
                implicitTags.add( City );
            else if ( geo.isCounty() )
                implicitTags.add( County );
            else if ( geo.isState() )
                implicitTags.add( State );
            else if ( geo.isCountry() )
                implicitTags.add( Country );
        }
        return implicitTags;
    }

    /**
     * {@inheritDoc}
     */
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
        this.within = within;
    }

    public PlaceReference getMustContain() {
        return mustContain;
    }

    public void setMustContain( PlaceReference mustContain ) {
        assert isType();
        this.mustContain = mustContain;
    }

    public PlaceReference getMustBeContainedIn() {
        return mustBeContainedIn;
    }

    public void setMustBeContainedIn( PlaceReference mustBeContainedIn ) {
        assert isType();
        this.mustBeContainedIn = mustBeContainedIn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvalid( Place locale ) {
        return super.isInvalid( locale )
            || isCircular( locale );
    }

    /**
     * Whether a place is not specified as contained or containing another,
     * or tagged only with absolute places.
     *
     * @param locale the default locale
     * @return a boolean
     */
    public boolean isAbsolute( Place locale ) {
        if ( mustContain.isSpecified( locale )
             || mustBeContainedIn.isSpecified( locale )
             || within != null && !within.isAbsolute( locale ) )

            return false;

        for ( ModelEntity tag : getAllTags() )
            if ( !( (Place) tag ).isAbsolute( locale ) )
                return false;

        return true;
    }

    private boolean isCircular( Place locale ) {
        return isWithinCircular( new HashSet<Place>() )
            || isMustContainCircular( new HashSet<Place>(), locale )
            || isMustBeContainedInCircular( new HashSet<Place>(), locale );
    }

    private boolean isMustContainCircular( Set<Place> visited, Place locale ) {
        if ( visited.contains( this ) )
            return true;

        visited.add( this );

        Place content = mustContain.getReferencedPlace( locale );
        return content != null
            && ( !mustContain.isPlanReferenced() || content.isAbsolute( locale ) )
            && content.isMustContainCircular( visited, locale );
    }

    private boolean isMustBeContainedInCircular( Set<Place> visited, Place locale ) {
        if ( visited.contains( this ) )
            return true;

        visited.add( this );

        Place container = mustBeContainedIn.getReferencedPlace( locale );
        return container != null
            && ( !mustBeContainedIn.isPlanReferenced() || container.isAbsolute( locale ) )
            && container.isMustBeContainedInCircular( visited, locale );
    }

    private boolean isWithinCircular( Set<Place> visited ) {
        if ( visited.contains( this ) )
            return true;

        visited.add( this );
        return within != null && within.isWithinCircular( visited );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean narrowsOrEquals( ModelEntity other, Place locale ) {
        // a place narrows another place if it or one of its parent is within the other place
        return super.narrowsOrEquals( other, locale )
            || other instanceof Place && !other.isInvalid( locale )
                                      && matchesOrIsInside( (Place) other, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validates( ModelEntity entity, Place locale ) {
        // other mustContain test must be less stringent
        // "must contain NJ" is more stringent than "must contain Morristown" because
        // some places that contain Morristown do not contain NJ, but all places containing
        // NJ contain Morristown. so "must contain NJ" narrows "must contain Morristown"
        // because Morristown narrows NJ
        if ( !super.validates( entity, locale ) )
            return false;

//            TODO implement a cheap way of getting place.getContents()
        if ( mustContain.getReferencedPlace( locale ) != null )
            LoggerFactory.getLogger( getClass() ).warn( "{} has unchecked 'mustContain' specification", this );

        Place place = (Place) entity;
        if ( place.isActual() ) {
            // Check that the place contains a place that it must contain according our spec

//            Place mc = mustContain.getReferencedPlace( locale );
//            if ( mc != null && !mc.matchesOrIsInside( place, locale ) )
//                return false;

            Place mbci = mustBeContainedIn.getReferencedPlace( locale );
            return mbci == null || place.matchesOrIsInside( mbci, locale );
        }

        // assert entity.isType()

        return mustBeContainedIn.narrowsOrEquals( place.getMustBeContainedIn(), locale );

//        Place mc = mustContain.getReferencedPlace( locale );
//        Place pmc = place.getMustContain().getReferencedPlace( locale );
//        return pmc == null || mc != null && pmc.narrowsOrEquals( mc, locale );
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
        return place == null ? other == null || UNKNOWN.equals( other )
                             : other != null && place.equals( other );
    }

    /**
     * Find all places this one is contained in, avoiding circularities.
     *
     * @return a list of places
     */
    public List<Place> containment() {
        return safeContainment( new HashSet<Place>() );
    }

    private List<Place> safeContainment( Set<Place> visited ) {
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
     * @param queryService
     */
    public String getGeoMarkerLabel( QueryService queryService ) {
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
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        String address = getActualStreetAddress();
        if ( address != null ) {
            sb.append( address.trim() );
        }
        GeoLocation geoLoc = geoLocate();
        if ( geoLoc != null ) {
            if ( sb.length() > 0 )
                sb.append( ", " );
            sb.append( geoLoc.toString().trim() );
        }
        String code = getActualPostalCode();
        if ( code != null && code.trim().length() > 0 ) {
            if ( sb.length() > 0 )
                sb.append( ", " );
            sb.append( code.trim() );
        }
        return sb.toString();
    }

    /**
     * Get street address, possibly inherited from containing places.
     *
     * @return a string
     */
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

    public List<GeoLocation> getGeoLocations() {
        return geoLocations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeRemove( QueryService queryService ) {
        super.beforeRemove( queryService );
        for ( Job job : queryService.findAllConfirmedJobs( this ) )
            job.setJurisdiction( null );
        for ( Part part : queryService.findAllParts( null, this, true ) )
            part.setJurisdiction( null );
        for ( Part part : queryService.findAllPartsWithExactLocation( this ) )
            part.setLocation( null );
        for ( Place place : queryService.list( Place.class ) )
            if ( equals( place.getWithin() ) )
                place.setWithin( null );
    }

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
        if ( geoLocations == null )
            geoLocations = new ArrayList<GeoLocation>();
        geoLocations.add( location );
    }

    /**
     * Whether this place is the same as or is inside another by definition or by geolocation.
     *
     * @param place a place
     * @param locale the default locale
     * @return a boolean
     */
    public boolean matchesOrIsInside( Place place, Place locale ) {
        if ( equals( place ) ) {
            return true;
        } else if ( isInside( place, locale ) ) {
            return true;
        } else if ( isActual() && place.isActual() && isGeoLocatedIn( place.geoLocate() ) ) {
            return true;
        } else {
            return false;
        }
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
    public Place getLoopyContainingPlace() {
        return getLoopyContainingPlace( this );
    }

    private Place getLoopyContainingPlace( Place place ) {
        if ( within == null )
            return null;

        if ( within.equals( place ) )
            return this;
        else
            return within.getLoopyContainingPlace( place );
    }

    /**
     * Get latitude.
     *
     * @return a double
     */
    public double getLatitude() {
        GeoLocation geoLoc = geoLocate();
        return geoLoc != null ? geoLoc.getLatitude() : 0.0;
    }

    /**
     * Get longitude.
     *
     * @return a double
     */
    public double getLongitude() {
        GeoLocation geoLoc = geoLocate();
        return geoLoc != null ? geoLoc.getLongitude() : 0.0;
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
     * Whether this place is within another.
     * Checks if any of my parent places is a given place.
     *
     * @param place a place
     * @param locale the default location
     * @return a boolean
     */
    public boolean isInside( Place place, Place locale ) {
        for ( Place otherPlace : containment() )
            if ( otherPlace.narrowsOrEquals( place, locale ) )
                return true;

        return false;
    }

    /**
     * The place defines a geographical area.
     *
     * @return a boolean
     */
    public boolean isRegion() {
        String actualAddress = getActualStreetAddress();
        return ( actualAddress == null || actualAddress.isEmpty() ) && geoLocate() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Attachment.Type> getAttachmentTypes() {
        List<Attachment.Type> types = new ArrayList<Attachment.Type>();
        if ( !hasImage() )
            types.add( Attachment.Type.Image );
        types.addAll( super.getAttachmentTypes() );
        return types;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean references( ModelObject mo ) {
        return super.references( mo ) || ModelObject.areIdentical( within, mo )
               || mustBeContainedIn.references( mo ) || mustContain.references( mo );
    }

    /**
     * Get the implied actor.
     * @return the actor, or null if any
     */
    public Actor getActor() {
        return null;
    }

    /**
     * Get the implied role.
     * @return the role, or null if any
     */
    public Role getRole() {
        return null;
    }

    /**
     * Get the implied organization.
     * @return the organization, or null if any
     */
    public Organization getOrganization() {
        return null;
    }

    /**
     * Get the implied jurisdiction.
     * @return the jurisdiction, or null if any
     */
    public Place getJurisdiction() {
        return this;
    }
}
