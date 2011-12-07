package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.core.model.Plan;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for a a list of plan uris.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 11:20 AM
 */
@XmlRootElement( name = "plan", namespace = "http://mind-alliance.com/api/isp/v1/" )
public class PlanIdentifiersData {

    private List<Plan> plans;

    public PlanIdentifiersData() {
        // required
    }


    public PlanIdentifiersData( List<Plan> plans ) {
        this.plans = plans;
    }

    @XmlElement( name = "identifier" )
    public List<PlanIdentifierData> getPlanIdentifiers() {
        List<PlanIdentifierData> planIdentities = new ArrayList<PlanIdentifierData>( );
        for (Plan plan : plans) {
            planIdentities.add(  new PlanIdentifierData( plan ) );
        }
        return planIdentities;
    }
}
