package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.NeedSatisfiedCondition;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/22/13
 * Time: 4:17 PM
 */
@XmlType( name = "needSatisfiedCondition", propOrder = {"label", "infoNeed"} )
public class NeedSatisfiedConditionData extends ConditionData {

    private InfoNeedData infoNeed;


    public NeedSatisfiedConditionData() {
        // required
    }

    public NeedSatisfiedConditionData( Condition condition,
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
        infoNeed = new InfoNeedData(
                serverUrl,
                ( (NeedSatisfiedCondition) condition ).getInfoNeed(),
                communityService );
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

    @XmlElement
    public InfoNeedData getInfoNeed() {
        return infoNeed;
    }

  }
