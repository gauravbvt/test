package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.ElementOfInformationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Information;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/28/13
 * Time: 9:00 AM
 */
@XmlType( propOrder = {"name", "infoProduct", "EOIs"} )
public class InformationData implements Serializable {

    private Information information;
    private InfoProductData infoProduct;
    private List<ElementOfInformationData> eois;

    public InformationData() {
    }

    public InformationData( String serverUrl, Information information, CommunityService communityService ) {
        this.information = information;
        initInfoProduct( serverUrl, communityService );
    }

    private void initInfoProduct( String serverUrl, CommunityService communityService ) {
        if ( information.getInfoProduct() != null ) {
            infoProduct = new InfoProductData( serverUrl, information.getInfoProduct(), communityService );
        }
    }

    @XmlElement
    public String getName() {
        return information.getName();
    }

    @XmlElement
    public InfoProductData getInfoProduct() {
        return infoProduct;
    }

    @XmlElement( name = "eoi" )
    public List<ElementOfInformationData> getEOIs() {
        if ( eois == null ) {
            eois = new ArrayList<ElementOfInformationData>();
            for ( ElementOfInformation eoi : information.getEffectiveEois() ) {
                eois.add( new ElementOfInformationData( eoi ) );
            }
        }
        return eois;
    }
}
