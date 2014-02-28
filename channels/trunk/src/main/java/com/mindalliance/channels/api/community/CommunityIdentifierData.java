package com.mindalliance.channels.api.community;

import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.plan.ModelIdentifierData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Place;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/5/13
 * Time: 9:15 AM
 */
@XmlType( propOrder = {"uri", "name", "locale", "modelIdentifier"} )

public class CommunityIdentifierData implements Serializable {

    private String uri;
    private String name;
    private PlaceData locale;
    private ModelIdentifierData modelIdentifier;

    public CommunityIdentifierData() {
        // required
    }

    public CommunityIdentifierData( String serverUrl, CommunityService communityService ) {
        initData( serverUrl, communityService );
    }

    private void initData( String serverUrl, CommunityService communityService ) {
        PlanCommunity planCommunity = communityService.getPlanCommunity();
        uri = planCommunity.getUri();
        name = planCommunity.getName();
        Place place = planCommunity.getLocale( communityService );
        if ( locale != null )
            locale = new PlaceData( serverUrl, place , communityService );
        modelIdentifier = new ModelIdentifierData( communityService );
    }

    @XmlElement
    public PlaceData getLocale() {
        return locale;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    @XmlElement
    public ModelIdentifierData getModelIdentifier() {
        return modelIdentifier;
    }

    @XmlElement
    public String getUri() {
        return uri;
    }
}
