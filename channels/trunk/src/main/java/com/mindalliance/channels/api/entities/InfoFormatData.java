package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.Plan;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Web Service data element for an info format.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/4/12
 * Time: 2:32 PM
 */
@XmlType( propOrder = {"name", "id", "categories", "documentation"} )
public class InfoFormatData extends ModelEntityData {

    public InfoFormatData() {
        // required
    }

    public InfoFormatData( String serverUrl, InfoFormat infoFormat, Plan plan ) {
        super( serverUrl, infoFormat, plan );
    }

    @Override
    @XmlElement
    public long getId() {
        return super.getId();
    }

    @Override
    @XmlElement
    public String getName() {
        return super.getName();
    }

    @Override
    @XmlElement( name = "categoryId" )
    public List<Long> getCategories() {
        return super.getCategories();
    }

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

}
