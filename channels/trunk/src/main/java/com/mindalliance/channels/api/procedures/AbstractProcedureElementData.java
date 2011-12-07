package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.query.PlanService;

/**
 * Web Service data element for an element of a procedure of an actor according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 10:30 AM
 */
abstract public class AbstractProcedureElementData {

    private Assignment assignment;
    private PlanService planService;

    protected AbstractProcedureElementData() {
    }

    protected AbstractProcedureElementData(
            Assignment assignment,
            PlanService planService ) {
        this.assignment = assignment;
        this.planService = planService;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public PlanService getPlanService() {
        return planService;
    }
}
