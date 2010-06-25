package com.mindalliance.channels.geo;

import com.mindalliance.channels.query.QueryService;
import org.geonames.InsufficientStyleException;
import org.geonames.Toponym;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A geolocation.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 16, 2009
 * Time: 12:52:36 PM
 */
public class GeoLocation implements Serializable {

    private static List<String> CityCodes = Arrays.asList( "PPL", "PPLA", "PPLC" );

    private int geonameId = -1;
    private String country;
    private String state;
    private String county;
    private String city;
    private String countryCode;
    private String stateCode;
    private String countyCode;
    private String cityCode;
    private long population;
    private double latitude;
    private double longitude;
    private String streetAddress;
    private String postalCode;
    private int precision = 0;

    public GeoLocation() {
    }

    public GeoLocation( Toponym topo ) {
        country = topo.getCountryName();
        state = getStateName( topo );
        county = getCountyName( topo );
        city = getCityName( topo );
        countryCode = topo.getCountryCode();
        stateCode = getStateCode( topo );
        countyCode = getCountyCode( topo );
        cityCode = getCityCode( topo );
        // population = getPopulation( topo );
        latitude = topo.getLatitude();
        longitude = topo.getLongitude();
        geonameId = topo.getGeoNameId();
    }

    private String getStateName( Toponym topo ) {
        try {
            return topo.getAdminName1();
        } catch ( InsufficientStyleException e ) {
            return null;
        }
    }

    private String getCountyName( Toponym topo ) {
        try {
            return topo.getAdminName2();
        } catch ( InsufficientStyleException e ) {
            return null;
        }
    }


    private String getCityName( Toponym topo ) {
        if ( CityCodes.contains( topo.getFeatureCode() ) ) {
            return topo.getName();
        } else {
            return null;
        }
    }


    private String getStateCode( Toponym topo ) {
        try {
            return topo.getAdminCode1();
        } catch ( InsufficientStyleException e ) {
            return null;
        }
    }


    private String getCountyCode( Toponym topo ) {
        try {
            return topo.getAdminCode2();
        } catch ( InsufficientStyleException e ) {
            return null;
        }
    }


    private String getCityCode( Toponym topo ) {
        if ( CityCodes.contains( topo.getFeatureCode() ) ) {
            return topo.getFeatureCode();
        } else {
            return null;
        }
    }

    private Long getPopulation( Toponym topo ) {
        try {
            return topo.getPopulation();
        } catch ( Exception e ) {
            return null;
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry( String country ) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState( String state ) {
        this.state = state;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty( String county ) {
        this.county = county;
    }

    public String getCity() {
        return city;
    }

    public void setCity( String city ) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode( String countryCode ) {
        this.countryCode = countryCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode( String stateCode ) {
        this.stateCode = stateCode;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode( String countyCode ) {
        this.countyCode = countyCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode( String cityCode ) {
        this.cityCode = cityCode;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation( Integer population ) {
        this.population = population;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude( double latitude ) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude( double longitude ) {
        this.longitude = longitude;
    }

    public int getGeonameId() {
        return geonameId;
    }

    public void setGeonameId( int geonameId ) {
        this.geonameId = geonameId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode( String postalCode ) {
        this.postalCode = postalCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress( String streetAddress ) {
        this.streetAddress = streetAddress;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision( int precision ) {
        this.precision = precision;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if ( city != null ) {
            sb.append( ( ( sb.length() > 0 ) ? ", " : "" ) );
            sb.append( city );
        }
        if ( state != null ) {
            sb.append( ( ( sb.length() > 0 ) ? ", " : "" ) );
            sb.append( state );
        }
        if ( county != null ) {
            sb.append( ( ( sb.length() > 0 ) ? ", " : "" ) );
            sb.append( county );
        }
        if ( country != null ) {
            sb.append( ( ( sb.length() > 0 ) ? ", " : "" ) );
            sb.append( country );
        }
        return sb.toString();
    }

    /**
     * Whether this geolocation is the same or within another.
     *
     * @param geoLoc a geolocation
     * @return a boolean
     */
    public boolean isSameAsOrInside( GeoLocation geoLoc ) {
        return areasMatch( country, geoLoc.getCountry() )
                && areasMatch( state, geoLoc.getState() )
                && areasMatch( county, geoLoc.getCounty() )
                && areasMatch( city, geoLoc.getCity() );
    }

    private boolean areasMatch( String area, String otherArea ) {
        return otherArea == null || area != null && area.equals( otherArea );
    }

    /**
     * Whether the geolocation has no country.
     *
     * @return a boolean
     */
    public boolean isAnywhere() {
        return country == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        return this == obj
                || obj instanceof GeoLocation
                && longitude == ( (GeoLocation) obj ).getLongitude()
                && latitude == ( (GeoLocation) obj ).getLatitude();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Double.toString( longitude ).hashCode();
        hash = hash * 31 + Double.toString( latitude ).hashCode();
        return hash;
    }


    public boolean isRefinedTo( String addressOrNull, String codeOrNull ) {
        if ( streetAddress == null || postalCode == null ) return false;
        String address = ( addressOrNull == null ? "" : addressOrNull );
        String code = ( codeOrNull == null ? "" : codeOrNull );
        return streetAddress.equals( address ) && postalCode.equals( code );
    }

    /**
     * Find all implied geolocations for a geolocatable.
     *
     * @param geoLocatable a geolocatable
     * @param queryService a query service
     * @return a list ofr geolocations
     */
    public static List<GeoLocation> getImpliedGeoLocations(
            GeoLocatable geoLocatable,
            QueryService queryService ) {
        List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
        for ( GeoLocatable geo : geoLocatable.getImpliedGeoLocatables( queryService ) ) {
            GeoLocation geoLocation = geo.geoLocate();
            if ( geoLocation != null ) {
                geoLocations.add( geoLocation );
            }
        }
        return geoLocations;
    }

    /**
     * Whether this is a country.
     *
     * @return a boolean
     */
    public boolean isCountry() {
        return isSet( country ) && !isSet( state ) && !isSet( county ) && !isSet( city );
    }

    /**
     * Whether this is a state.
     *
     * @return a boolean
     */
    public boolean isState() {
        return isSet( state ) && !isSet( county ) && !isSet( city );
    }

    /**
     * Whether this is a county.
     *
     * @return a boolean
     */
    public boolean isCounty() {
        return isSet( county ) && !isSet( city );
    }

    /**
     * Whether this is a city.
     *
     * @return a boolean
     */
    public boolean isCity() {
        return isSet( city );
    }

    private boolean isSet( String name ) {
        return name != null && !name.trim().isEmpty();
    }

}
