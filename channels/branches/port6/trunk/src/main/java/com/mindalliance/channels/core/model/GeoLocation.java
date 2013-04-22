package com.mindalliance.channels.core.model;

import java.io.Serializable;

/**
 * A geolocation.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 16, 2009
 * Time: 12:52:36 PM
 */
public class GeoLocation implements Serializable {

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
    private int precision;

    public GeoLocation() {
    }

    /**
     * Set position information.
     *
     * @param latitude the latitude
     * @param longitude the longitude
     * @param precision precision of the above
     */
    public void setPosition( double latitude, double longitude, int precision ) {
        if ( streetAddress == null || postalCode == null ) {
            // Lat/Long refinement is obsolete, must re-obtain latitude/long for address.
            this.precision = precision;
            this.latitude = latitude;
            this.longitude = longitude;

            // Record the fact that refinement was done
            if ( streetAddress == null )
                streetAddress = "";
            if ( postalCode == null )
                postalCode = "";
        }
    }

    private void resetPosition() {
        if ( "".equals( streetAddress ) && "".equals( postalCode ) ) {
            postalCode = null;
            streetAddress = null;
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

    public void setPopulation( Long population ) {
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
        resetPosition();
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress( String streetAddress ) {
        this.streetAddress = streetAddress;
        resetPosition();
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision( int precision ) {
        this.precision = precision;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if ( city != null ) {
            sb.append( sb.length() > 0 ? ", " : "" );
            sb.append( city );
        }
        if ( state != null ) {
            sb.append( sb.length() > 0 ? ", " : "" );
            sb.append( state );
        }
        if ( county != null ) {
            sb.append( sb.length() > 0 ? ", " : "" );
            sb.append( county );
        }
        if ( country != null ) {
            sb.append( sb.length() > 0 ? ", " : "" );
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

    private static boolean areasMatch( String area, String otherArea ) {
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

    @Override
    public boolean equals( Object obj ) {
        return this == obj
            || obj instanceof GeoLocation
                && longitude == ( (GeoLocation) obj ).getLongitude()
                && latitude == ( (GeoLocation) obj ).getLatitude();
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + Double.toString( longitude ).hashCode();
        hash = hash * 31 + Double.toString( latitude ).hashCode();
        return hash;
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

    public boolean isInside( GeoLocation container ) {
        return container != null
            && isSameAsOrInside( container );
    }
}
