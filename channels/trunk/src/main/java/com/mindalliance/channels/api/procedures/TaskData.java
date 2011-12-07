package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web Service data element for a task assignment according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 10:27 AM
 */
@XmlRootElement( name = "task", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"name"} )
public class TaskData  extends AbstractProcedureElementData {

    public TaskData() {
        // required
    }

    public TaskData( Assignment assignment, PlanService planService ) {
        super( assignment, planService );
    }

    @XmlElement
    public String getName() {
        return getAssignment().getName();
    }

    // todo: more elements

}
