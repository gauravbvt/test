package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.ElementOfInformationData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.InfoProduct;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for an info product.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/4/12
 * Time: 2:32 PM
 */
@XmlType( propOrder = {"name", "id", "categories", "documentation", "EOIs"} )

public class InfoProductData extends ModelEntityData {

    private List<ElementOfInformationData> eois;

    public InfoProductData() {
        // required
    }

    public InfoProductData( String serverUrl, InfoProduct infoProduct, CommunityService communityService ) {
        super( serverUrl, infoProduct, communityService );
    }

    @Override
    @XmlElement
    public long getId() {
        return super.getId();
    }

    @Override
    @XmlElement
    public String getName() {
        return super.getName();
    }

    @Override
    @XmlElement( name = "categoryId" )
    public List<Long> getCategories() {
        return super.getCategories();
    }

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    @XmlElement( name = "eoi" )
    public List<ElementOfInformationData> getEOIs() {
        if ( eois == null ) {
            eois = new ArrayList<ElementOfInformationData>();
            for ( ElementOfInformation eoi : ( (InfoProduct) getModelObject() ).getEffectiveEois() ) {
                eois.add( new ElementOfInformationData( eoi ) );
            }
        }
        return eois;
    }

}
