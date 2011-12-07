package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.ElementOfInformation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for an element of information.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 2:06 PM
 */
@XmlRootElement( name = "eoi", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"name", "classifications"} )
public class ElementOfInformationData {

    private ElementOfInformation eoi;

    public ElementOfInformationData() {
        // required
    }

    public ElementOfInformationData( ElementOfInformation eoi ) {
        this.eoi = eoi;
    }

    @XmlElement
    public String getName() {
        return eoi.getContent();
    }

    @XmlElement( name = "classification" )
    public List<SecurityClassificationData> getClassifications() {
        List<SecurityClassificationData> classifications = new ArrayList<SecurityClassificationData>(  );
        for ( Classification classification : eoi.getClassifications() ) {
            classifications.add(  new SecurityClassificationData( classification ) );
        }
        return classifications;
    }
}
