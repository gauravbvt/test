package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for information.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 1:59 PM
 */
@XmlType( propOrder = {"name", "EOIs"} )
public class InformationData {

    private Flow sharing;
    private List<ElementOfInformationData> eois;

    public InformationData() {
        // required
    }

    public InformationData( Flow sharing ) {
        this.sharing = sharing;
    }

    @XmlElement
    public String getName() {
        return StringEscapeUtils.escapeXml( sharing.getName() );
    }

    @XmlElement( name = "eoi" )
    public List<ElementOfInformationData> getEOIs() {
        if ( eois == null ) {
            eois = new ArrayList<ElementOfInformationData>();
            for ( ElementOfInformation eoi : sharing.getEois() ) {
                eois.add( new ElementOfInformationData( eoi ) );
            }
        }
        return eois;
    }
}
