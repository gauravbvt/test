package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.core.model.Plan;

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

    private Plan plan;

    public PlanReleaseData() {
        // required
    }

    public PlanReleaseData( Plan plan ) {
        this.plan = plan;
    }

    @XmlElement
    public PlanIdentifierData getPlanIdentifier() {
        return new PlanIdentifierData( plan );
    }

}
