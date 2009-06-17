package com.mindalliance.channels.model;

import org.geonames.InsufficientStyleException;
import org.geonames.Toponym;

import java.io.Serializable;
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
    private String streetAddress;
    private String postalCode;
    private Long population;
    private double latitude;
    private double longitude;

    public GeoLocation() {
    }

    public GeoLocation( Toponym topo ) {
        country = topo.getCountryName( );
        state = getStateName( topo );
        county = getCountyName( topo );
        city = getCityName( topo );
        countryCode = topo.getCountryCode( );
        stateCode = getStateCode( topo );
        countyCode = getCountyCode( topo );
        cityCode = getCityCode( topo );
        population = getPopulation( topo );
        latitude = topo.getLatitude( );
        longitude = topo.getLongitude( );
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
         } catch ( InsufficientStyleException e ) {
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

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress( String streetAddress ) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode( String postalCode ) {
        this.postalCode = postalCode;
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (streetAddress != null) sb.append( streetAddress );
        if ( city != null ) {
            sb.append( ((sb.length() > 0) ? ", " : ""));
            sb.append( city );
        }
        if ( state != null ) {
            sb.append( ((sb.length() > 0) ? ", " : ""));
            sb.append( state );
        }
        if ( county != null ) {
            sb.append( ((sb.length() > 0) ? ", " : ""));
            sb.append( county );
        }
        if ( country != null ) {
            sb.append( ((sb.length() > 0) ? ", " : ""));
            sb.append( country );
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        return this == obj
                || obj instanceof GeoLocation
                && geonameId == ( (GeoLocation) obj ).getGeonameId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Integer.valueOf( geonameId ).hashCode();
    }

}
