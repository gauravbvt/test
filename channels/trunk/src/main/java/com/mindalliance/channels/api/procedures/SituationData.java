package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web Service data element for the situation in which a procedure is triggered according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 10:26 AM
 */
@XmlRootElement( name = "situation", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {} )
public class SituationData  extends AbstractProcedureElementData {

    public SituationData() {
        // required
    }

    public SituationData( Assignment assignment, PlanService planService ) {
        super( assignment, planService );
    }
}
