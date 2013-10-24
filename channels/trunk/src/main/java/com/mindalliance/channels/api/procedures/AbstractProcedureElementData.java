package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Plan;
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

    private CommunityAssignment assignment;
    private ChannelsUser user;
    private Plan plan;

    public AbstractProcedureElementData() {
    }

    public AbstractProcedureElementData(
            CommunityService communityService,
            ChannelsUser user ) {
        this.user = user;
        initData( communityService );
    }

    public AbstractProcedureElementData(
            CommunityService communityService,
            CommunityAssignment assignment,
            ChannelsUser user ) {
        this.assignment = assignment;
        this.user = user;
        initData( communityService );
    }

    private void initData( CommunityService communityService ) {
        plan = communityService.getPlan();
    }

    public CommunityAssignment getAssignment() {
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

    public String getAnchor() {
        return Long.toString( getAssignment().getPart().getId() );
    }

    public String getTaskLabel() {
        return getAssignment().getPart().getTask();
    }

}
