package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.core.model.asset.AssetField;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web Service data element for a material asset field.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/14
 * Time: 1:25 PM
 */
@XmlType( name="field", propOrder = {"name", "description", "group", "value", "required"} )
public class AssetFieldData implements Serializable {

    private AssetField assetField;

    public AssetFieldData() {
        // required
    }

    public AssetFieldData( AssetField assetField ) {
        this.assetField = assetField;
    }

    @XmlElement
    public String getName() {
        return assetField.getName();
    }

    @XmlElement
    public String getDescription() {
        return assetField.getDescription();
    }

    @XmlElement
    public String getGroup() {
        return assetField.getGroup();
    }

    @XmlElement
    public String getValue() {
        return assetField.getValue();
    }

    @XmlElement
    public boolean getRequired() {
        return assetField.isRequired();
    }

    public String getLabel() {
        StringBuffer sb = new StringBuffer(  );
        sb.append( getName() );
        if ( getValue() != null ) {
            sb.append( " = ")
                    .append( getValue() );
        }
        return sb.toString();
    }
}
