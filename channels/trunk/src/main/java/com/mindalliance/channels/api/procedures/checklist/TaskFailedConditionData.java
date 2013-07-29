package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Condition;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/29/13
 * Time: 10:37 AM
 */
@XmlType( name = "taskFailedCondition" , propOrder= {"label"})
public class TaskFailedConditionData extends ConditionData {

    public TaskFailedConditionData() {
        // required
    }

    public TaskFailedConditionData( Condition condition,
                                    String serverUrl,
                                    CommunityService communityService,
                                    ChannelsUser user ) {
        super( condition, serverUrl, communityService, user );
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

}
