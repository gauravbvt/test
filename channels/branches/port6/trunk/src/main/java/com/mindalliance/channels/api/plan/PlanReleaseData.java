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
@XmlRootElement( name = "planRelease", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType
public class PlanReleaseData  implements Serializable {

    private PlanIdentifierData planIdentifierData;

    public PlanReleaseData() {
        // required
    }

    public PlanReleaseData( CommunityService communityService ) {
       planIdentifierData = new PlanIdentifierData( communityService );
    }

    @XmlElement
    public PlanIdentifierData getPlanIdentifier() {
        return planIdentifierData;
    }

}
