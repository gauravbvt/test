package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.core.model.GeoLocation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web Service data element for a geolocation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/11
 * Time: 3:18 PM
 */
@XmlType( propOrder = {"city", "county", "state", "country"} )
public class GeoLocationData  implements Serializable {

    private GeoLocation geoLocation;

    public GeoLocationData() {
        // required
    }

    public GeoLocationData( GeoLocation geoLocation ) {
        this.geoLocation = geoLocation;
    }

    @XmlElement
    public String getCity() {
        return geoLocation.getCity() != null
                ? geoLocation.getCity()
                : null;
    }

    @XmlElement
    public String getCounty() {
        return geoLocation.getCounty() != null
                ? geoLocation.getCounty()
                : null;
    }

    @XmlElement
    public String getState() {
        return geoLocation.getState() != null
                ? geoLocation.getState()
                : null;
    }

    @XmlElement
    public String getCountry() {
        return geoLocation.getCountry() != null
                ? geoLocation.getCountry()
                : null;
    }


}
