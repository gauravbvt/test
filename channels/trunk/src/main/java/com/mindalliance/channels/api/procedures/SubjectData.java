package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Subject;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web service data element for a subject.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/19/11
 * Time: 12:22 PM
 */
@XmlType( propOrder = {"information", "element"} )
public class SubjectData  implements Serializable {

    private Subject subject;

    public SubjectData() {
        // required
    }

    public SubjectData( Subject subject ) {
        this.subject = subject;
    }

    @XmlElement
    public String getInformation() {
        return StringEscapeUtils.escapeXml( subject.getInfo() );
    }

    @XmlElement
    public String getElement() {
        return StringEscapeUtils.escapeXml( subject.getContent() );
    }
}
