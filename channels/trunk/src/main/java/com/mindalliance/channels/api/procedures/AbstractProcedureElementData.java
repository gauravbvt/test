package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Plan;
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
    private PlanParticipationService planParticipationService;
    private ChannelsUser user;

    protected AbstractProcedureElementData() {
    }

    protected AbstractProcedureElementData(
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        this.planService = planService;
        this.planParticipationService = planParticipationService;
        this.user = user;
    }

    protected AbstractProcedureElementData(
            Assignment assignment,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        this.assignment = assignment;
        this.planService = planService;
        this.planParticipationService = planParticipationService;
        this.user = user;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public PlanService getPlanService() {
        return planService;
    }

    public PlanParticipationService getPlanParticipationService() {
        return planParticipationService;
    }

    protected Plan getPlan() {
        return planService.getPlan();
    }

    protected ChannelsUser getUser() {
        return user;
    }

    protected String getUsername() {
        return user == null ? null : user.getUsername();
    }
}
