package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Segment;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web service data element for what an issue is about.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 3:13 PM
 */
@XmlType( propOrder = { "id", "type", "category", "name", "description" } )
public class AboutData  implements Serializable {

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

    // Categories = { plan, segment, task , flow, entity, other }
    @XmlElement
    public String getCategory() {
        return modelObject instanceof CollaborationModel
                ? "model"
                : modelObject instanceof Segment
                ? "segment"
                : modelObject instanceof Part
                ? "task"
                : modelObject instanceof Flow
                ? "flow"
                : modelObject instanceof ModelEntity
                ? "entity"
                : "other";
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
