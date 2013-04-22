package com.mindalliance.channels.api;

import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.ElementOfInformation;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Web Service data element for an element of information.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 2:06 PM
 */
@XmlType( propOrder = {"name", "description", "classifications", "specialHandling"} )
public class ElementOfInformationData  implements Serializable {

    private ElementOfInformation eoi;

    public ElementOfInformationData() {
        // required
    }

    public ElementOfInformationData( ElementOfInformation eoi ) {
        this.eoi = eoi;
    }

    @XmlElement
    public String getName() {
        return StringEscapeUtils.escapeXml( eoi.getContent() );
    }

    @XmlElement( name = "classification" )
    public List<SecurityClassificationData> getClassifications() {
        List<SecurityClassificationData> classifications = new ArrayList<SecurityClassificationData>();
        for ( Classification classification : eoi.getClassifications() ) {
            classifications.add( new SecurityClassificationData( classification ) );
        }
        return classifications;
    }

    @XmlElement
    public String getSpecialHandling() {
        return eoi.getSpecialHandling().isEmpty()
                ? null
                : StringEscapeUtils.escapeXml( eoi.getSpecialHandling() );
    }

    @XmlElement
    public String getDescription() {
        String description = eoi.getDescription();
        return description.isEmpty()
                ? null
                : description;
    }

    public String getClassificationsLabel() {
        StringBuilder sb = new StringBuilder(  );
        Iterator<SecurityClassificationData> iter = getClassifications().iterator();
        while( iter.hasNext() ) {
            sb.append( iter.next().getLabel() );
            if ( iter.hasNext() ) sb.append( ", " );
        }
        return sb.toString();
    }
}
