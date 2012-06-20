package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web service data element for what an issue is about.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 3:13 PM
 */
@XmlType( propOrder = { "id", "type", "name", "description" } )
public class AboutData {

    private ModelObject modelObject;

    public AboutData() {
        // required
    }

    public AboutData( ModelObject modelObject ) {
        this.modelObject = modelObject;
    }

    @XmlElement
    public Long getId() {
        return modelObject.getId();
    }

    @XmlElement
    public String getType() {
        return modelObject.getTypeName();
    }

    @XmlElement
    public String getName() {
        String name =  modelObject instanceof Part
                ? ((Part)modelObject).getTask()
                : modelObject.getName();
        return StringEscapeUtils.escapeXml( name );
    }

    @XmlElement
    public String getDescription() {
        return StringEscapeUtils.escapeXml( modelObject.getDescription() );
    }
}
