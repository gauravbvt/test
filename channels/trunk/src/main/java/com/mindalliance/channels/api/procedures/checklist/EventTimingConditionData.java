package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.procedures.EventTimingData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.EventTimingCondition;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 9:46 PM
 */
@XmlType( name = "eventTimingCondition", propOrder={"label","eventTiming"})
public class EventTimingConditionData extends ConditionData {

    private EventTimingData eventTiming;

    public EventTimingConditionData() {
        // required
    }

    public EventTimingConditionData( Condition condition,
                                     String serverUrl,
                                     CommunityService communityService,
                                     ChannelsUser user ) {
        super( condition, serverUrl, communityService, user );
    }


    @Override
    protected void initData( Condition condition, String serverUrl, CommunityService communityService, ChannelsUser user ) {
        super.initData( condition, serverUrl, communityService, user );
        eventTiming = new EventTimingData( ((EventTimingCondition)getCondition()).getEventTiming() ) ;
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

    @XmlElement
    public EventTimingData getEventTiming() {
        return eventTiming;
    }

    @Override
    public Set<Long> allEventIds() {
        Set<Long> ids = super.allEventIds();
        ids.add( eventTiming.getEvent() );
        return ids;
    }

}
