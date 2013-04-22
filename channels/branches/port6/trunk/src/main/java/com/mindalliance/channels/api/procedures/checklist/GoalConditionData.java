package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.procedures.GoalData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.GoalCondition;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 9:47 PM
 */
@XmlType(name = "goalCondition", propOrder = {"label", "goal"})
public class GoalConditionData extends ConditionData {

    private GoalData goal;

    public GoalConditionData() {
        // required
    }

    public GoalConditionData( Condition condition,
                              String serverUrl,
                              CommunityService communityService,
                              ChannelsUser user ) {
        super( condition, serverUrl, communityService, user );
    }

    @Override
    protected void initData( Condition condition,
                             String serverUrl,
                             CommunityService communityService,
                             ChannelsUser user ) {
        super.initData( condition, serverUrl, communityService, user );
        goal = new GoalData( ( (GoalCondition) getCondition() ).getGoal() );
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

    @XmlElement
    public GoalData getGoal() {
        return goal;
    }
}
