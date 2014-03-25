package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
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
@XmlType( propOrder = {"id", "name", "description", "kind", "categories", "placeholder", "streetAddress", "postalCode", "geolocation", "documentation"} )
public class PlaceData extends ModelEntityData {

    public PlaceData() {
        // required
    }

    public PlaceData( String serverUrl, ModelObject modelObject, CommunityService communityService ) {
        super( serverUrl, modelObject, communityService );
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

    @Override
    @XmlElement
    public String getDescription() {
        return super.getDescription();
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
    public boolean getPlaceholder() {
        return getPlace().isPlaceholder();
    }

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }
    
    private Place getPlace() {
        return (Place)getModelObject();
    }

    public String getLabel() {
        return getPlace().getLabel();
    }
}
