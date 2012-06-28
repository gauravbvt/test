package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.Serializable;

/**
 * Web Service data element for an element of a procedure of an actor according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 10:30 AM
 */
abstract public class AbstractProcedureElementData  implements Serializable {

    private Assignment assignment;
    private ChannelsUser user;
    private Plan plan;

    protected AbstractProcedureElementData() {
    }

    protected AbstractProcedureElementData(
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        this.user = user;
        initData( planService, planParticipationService );
    }

    protected AbstractProcedureElementData(
            Assignment assignment,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        this.assignment = assignment;
        this.user = user;
        initData( planService, planParticipationService );
    }

    private void initData( PlanService planService, PlanParticipationService planParticipationService ) {
        plan = planService.getPlan();
    }

    public Assignment getAssignment() {
        return assignment;
    }

    protected Plan getPlan() {
        return plan;
    }

    protected ChannelsUser getUser() {
        return user;
    }

    protected String getUsername() {
        return user == null ? null : StringEscapeUtils.escapeXml( user.getUsername() );
    }
}
