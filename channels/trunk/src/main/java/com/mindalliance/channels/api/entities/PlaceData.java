package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Web Service data element for a place object.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/11
 * Time: 2:44 PM
 */
@XmlRootElement( name = "place", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"id", "name", "kind", "categories", "streetAddress", "postalCode", "geolocation"} )
public class PlaceData extends ModelEntityData {

    public PlaceData() {
        // required
    }

    public PlaceData( ModelObject modelObject ) {
        super( modelObject );
    }

    //-------------------------------
    @XmlElement(name = "categoryId")
    public List<Long> getCategories() {
        return super.getCategories();
    }

    @Override
    @XmlElement
    public long getId() {
        return super.getId();
    }

    @Override
    @XmlElement
    public String getKind() {
        return super.getKind();
    }

    @Override
    @XmlElement
    public String getName() {
        return super.getName();
    }

    @XmlElement
    public String getStreetAddress() {
        return getPlace().getStreetAddress() != null
                ? getPlace().getStreetAddress()
                : null;
    }

    @XmlElement
    public String getPostalCode() {
        return getPlace().getPostalCode() != null
                ? getPlace().getPostalCode()
                : null;
    }


    @XmlElement
    public GeoLocationData getGeolocation() {
        return getPlace().getGeoLocation() != null
                ? new GeoLocationData( getPlace().getGeoLocation() )
                : null;
    }
    
    private Place getPlace() {
        return (Place)getModelObject();
    }    

}