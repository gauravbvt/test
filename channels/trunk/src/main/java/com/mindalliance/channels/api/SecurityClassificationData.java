package com.mindalliance.channels.api;

import com.mindalliance.channels.core.model.Classification;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web Service data element for a security classification.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 2:17 PM
 */
@XmlType( propOrder = {"system", "level"} )
public class SecurityClassificationData implements Serializable {

    private Classification classification;

    public SecurityClassificationData() {
        // required
    }

    public SecurityClassificationData( Classification classification ) {
        this.classification = classification;
    }

    @XmlElement
    public String getSystem() {
        return classification.getSystem();
    }

    @XmlElement
    public String getLevel() {
        return classification.getName();
    }
}
