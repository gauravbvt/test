package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Condition;

import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 9:41 PM
 */
@XmlType
public abstract class ConditionData implements Serializable {

    private Condition condition;

    public ConditionData() {
        // required
    }

    public ConditionData( Condition condition,
                          String serverUrl,
                          CommunityService communityService,
                          ChannelsUser user ) {
            initData( condition, serverUrl, communityService, user );
    }

    protected void initData( Condition condition,
                             String serverUrl,
                             CommunityService communityService,
                             ChannelsUser user ) {
        this.condition = condition;
    }

    public String getLabel() {
        return condition.getLabel();
    }

    public Condition getCondition() {
        return condition;
    }

    public Set<Long> allEventIds() {
        return new HashSet<Long>(  );
    }

    public Set<Long> allPhaseIds() {
        return new HashSet<Long>(  );
    }

    public Set<Long> allActorIds() { // todo satisfied info need resource spec....
        return new HashSet<Long>(  );
    }

    public Set<Long> allRoleIds() {  // todo satisfied info need resource spec....
        return new HashSet<Long>(  );
    }

    public Set<Long> allPlaceIds() {  // todo satisfied info need resource spec....
        return new HashSet<Long>(  );
    }

    public Set<Long> allOrganizationIds() {  // todo satisfied info need resource spec....
        return new HashSet<Long>(  );
    }

    public Set<Long> allInfoProductIds() { // todo created info capability resource spec....
        return new HashSet<Long>(  );
    }

    public Set<Long> allAssetIds() {
        return new HashSet<Long>(  );
    }


    public boolean isLocalCondition() {
        return condition.isLocalCondition();
    }

    public boolean isGoalCondition() {
        return condition.isGoalCondition();
    }

    public boolean isEventTimingCondition() {
        return condition.isEventTimingCondition();
    }

    public boolean isNeedSatisfiedCondition() {
        return condition.isNeedSatisfiedCondition();
    }

    public boolean isTaskFailedCondition() {
        return condition.isTaskFailedCondition();
    }

    public boolean isAssetAvailableCondition() { return condition.isAssetAvailableCondition(); }

}
