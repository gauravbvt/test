package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.core.community.CommunityService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Plan release data.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/6/12
 * Time: 5:02 PM
 */
@XmlRootElement( name = "modelRelease", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType
public class ModelReleaseData implements Serializable {

    private ModelIdentifierData modelIdentifierData;

    public ModelReleaseData() {
        // required
    }

    public ModelReleaseData( CommunityService communityService ) {
       modelIdentifierData = new ModelIdentifierData( communityService );
    }

    @XmlElement
    public ModelIdentifierData getPlanIdentifier() {
        return modelIdentifierData;
    }

}
