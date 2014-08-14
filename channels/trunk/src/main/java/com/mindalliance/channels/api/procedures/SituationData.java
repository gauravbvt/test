package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Phase;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Web Service data element for the situation in which a procedure is triggered according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 10:26 AM
 */
@XmlType( propOrder = {"label", "eventId", "phaseId", "context"} )
public class SituationData extends AbstractProcedureElementData {

    public SituationData() {
        // required
    }

    public SituationData(
            CommunityService communityService,
            CommunityAssignment assignment,
            ChannelsUser user ) {
        super( communityService, assignment, user );
    }

    @XmlElement
    public Long getEventId() {
        return getAssignment().getEventPhase().getEvent().getId();
    }

    @XmlElement
    public Long getPhaseId() {
        return getAssignment().getEventPhase().getPhase().getId();
    }

    @XmlElement
    public List<EventTimingData> getContext() {
        List<EventTimingData> context = new ArrayList<EventTimingData>();
        for ( EventTiming eventTiming : getAssignment().getPart().getSegment().getContext() ) {
            context.add( new EventTimingData( eventTiming ) );
        }
        return context;
    }

    @XmlElement
    public String getLabel() {
        EventPhase eventPhase = getAssignment().getEventPhase();
        Phase phase = eventPhase.getPhase();
        String suffix = phase.isPreEvent()
                ? "anticipated"
                : phase.isConcurrent()
                ? "occurring"
                : "occurred";
        return getLabel( "", suffix );
    }

    private String getLabel( String prefix, String suffix ) {
        StringBuilder sb = new StringBuilder();
        EventPhase eventPhase = getAssignment().getEventPhase();
        sb.append( prefix );
        if ( prefix != null && !prefix.isEmpty() ) sb.append( " " );
        sb.append( "\"").append( eventPhase.getEvent().getName() ).append( "\"" );
        if ( suffix != null && !suffix.isEmpty() ) sb.append( " " );
        sb.append( suffix );
        List<EventTimingData> eventTimingDataList = getContext();
        if ( getContext() != null && !getContext().isEmpty() ) {
            sb.append( ", " );
            Iterator<EventTimingData> eventTimings = eventTimingDataList.iterator();
            while ( eventTimings.hasNext() ) {
                sb.append( eventTimings.next().getLabel() );
                if ( eventTimings.hasNext() ) {
                    sb.append( " and " );
                }
            }
        }
        return sb.toString();
    }


    public String getSituationLabel() {
        EventPhase eventPhase = getAssignment().getEventPhase();
        Phase phase = eventPhase.getPhase();
        String prefix = phase.isPreEvent()
                ? "before event"
                : phase.isConcurrent()
                ? "during event"
                : "after event";
        return getLabel( prefix, "" );
    }
}
