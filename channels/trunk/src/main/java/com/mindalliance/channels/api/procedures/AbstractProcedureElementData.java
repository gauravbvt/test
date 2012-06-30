package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
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
            QueryService queryService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        this.user = user;
        initData( queryService, planParticipationService );
    }

    protected AbstractProcedureElementData(
            Assignment assignment,
            QueryService queryService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        this.assignment = assignment;
        this.user = user;
        initData( queryService, planParticipationService );
    }

    private void initData( QueryService queryService, PlanParticipationService planParticipationService ) {
        plan = queryService.getPlan();
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
