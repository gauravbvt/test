package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.LocalCondition;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 9:45 PM
 */
@XmlType( name = "localCondition" , propOrder= {"label","state"})
public class LocalConditionData extends ConditionData {

    public LocalConditionData() {
        // required
    }

    public LocalConditionData( Condition condition,
                               String serverUrl,
                               CommunityService communityService,
                               ChannelsUser user) {
        super( condition, serverUrl, communityService, user );
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

    @XmlElement
    public String getState() {
        return ((LocalCondition)getCondition()).getState();
    }
}
