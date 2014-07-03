package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.ElementOfInformationData;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InfoProduct;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
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
@XmlType( propOrder = {"name", "EOIs", "infoProductId", "infoProduct"} )
public class SharedInformationData implements Serializable {

    private Flow sharing;
    private List<ElementOfInformationData> eois;
    private InfoProduct infoProduct;

    public SharedInformationData() {
        // required
    }

    public SharedInformationData( Flow sharing ) {
        this.sharing = sharing;
        this.infoProduct = sharing.getInfoProduct();
    }

    @XmlElement
    public String getName() {
        return StringEscapeUtils.escapeXml( sharing.getLabel() );
    }

    @XmlElement( name = "eoi" )
    public List<ElementOfInformationData> getEOIs() {
        if ( eois == null ) {
            eois = new ArrayList<ElementOfInformationData>();
            for ( ElementOfInformation eoi : sharing.getEffectiveEois() ) {
                eois.add( new ElementOfInformationData( eoi ) );
            }
        }
        return eois;
    }

    @XmlElement
    public Long getInfoProductId() {
        return infoProduct != null ? infoProduct.getId() : null;
    }

    @XmlElement
    public String getInfoProduct() {
        return infoProduct != null ? infoProduct.getName() : null;
    }

}
