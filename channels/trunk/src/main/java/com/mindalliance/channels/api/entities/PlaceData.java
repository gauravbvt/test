package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
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
@XmlType( propOrder = {"id", "name", "kind", "categories", "streetAddress", "postalCode", "geolocation", "documentation"} )
public class PlaceData extends ModelEntityData {

    public PlaceData() {
        // required
    }

    public PlaceData( ModelObject modelObject, Plan plan ) {
        super( modelObject, plan );
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
                ? StringEscapeUtils.escapeXml(  getPlace().getStreetAddress() )
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

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }
    
    private Place getPlace() {
        return (Place)getModelObject();
    }    

}
