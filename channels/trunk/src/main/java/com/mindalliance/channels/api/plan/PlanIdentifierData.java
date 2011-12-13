package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.core.model.Plan;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web Service data element for the identity of a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 11:28 AM
 */
@XmlType( propOrder = {"uri", "name", "version"} )
public class PlanIdentifierData {

    private Plan plan;

    public PlanIdentifierData() {
        // required
    }

    public PlanIdentifierData( Plan plan ) {
        this.plan = plan;
    }

    @XmlElement
    public String getUri() {
        return plan.getUri();
    }

    @XmlElement
    public String getName() {
        return plan.getName();
    }

    @XmlElement
    public int getVersion() {
        return plan.getVersion();
    }

}
