package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.procedures.EventTimingData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.EventTimingOutcome;
import com.mindalliance.channels.core.model.checklist.Outcome;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/25/13
 * Time: 12:59 PM
 */
@XmlType( name = "eventTimingOutcome", propOrder={"label","eventTiming"})


public class EventTimingOutcomeData extends OutcomeData {

    private EventTimingData eventTiming;

    public EventTimingOutcomeData() {
        // required
    }

    public EventTimingOutcomeData( Outcome outcome,
                                   String serverUrl,
                                   CommunityService communityService,
                                   ChannelsUser user ) {
        super( outcome,  serverUrl, communityService, user );
    }

    @Override
    protected void initData( Outcome outcome,
                             String serverUrl,
                             CommunityService communityService,
                             ChannelsUser user ) {
        super.initData( outcome, serverUrl, communityService, user );
        eventTiming = new EventTimingData( ((EventTimingOutcome)getOutcome()).getEventTiming() ) ;
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
