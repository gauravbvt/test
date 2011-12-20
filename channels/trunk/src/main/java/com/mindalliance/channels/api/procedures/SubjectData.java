package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Subject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web service data element for a subject.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/19/11
 * Time: 12:22 PM
 */
@XmlType( propOrder = {"information", "element"} )
public class SubjectData {

    private Subject subject;

    public SubjectData() {
        // required
    }

    public SubjectData( Subject subject ) {
        this.subject = subject;
    }

    @XmlElement
    public String getInformation() {
        return subject.getInfo();
    }

    @XmlElement
    public String getElement() {
        return subject.getContent();
    }
}
