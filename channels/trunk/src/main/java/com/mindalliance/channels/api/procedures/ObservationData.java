package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/21/12
 * Time: 9:28 PM
 */
public class ObservationData  implements Serializable {

    private EventPhase eventPhase;
    private List<EventTiming> eventTimings;

    public ObservationData() {
    }

    public ObservationData( EventPhase eventPhase, List<EventTiming> eventTimings  ) {
        this.eventPhase = eventPhase;
        this.eventTimings = eventTimings;
    }

    @XmlElement
    public Long getEventId() {
        if ( eventPhase != null )
            return eventPhase.getEvent().getId();
        else
            return null;
    }

    @XmlElement
    public Long getPhaseId() {
        if ( eventPhase != null )
            return eventPhase.getPhase().getId();
        else
            return null;
    }

    @XmlElement( name = "eventTiming")
    public List<EventTimingData> getEventTimings() {
        if ( eventTimings == null || eventTimings.isEmpty() ) {
            return null;
        } else {
            List<EventTimingData> eventTimingDataList = new ArrayList<EventTimingData>(  );
            for ( EventTiming eventTiming : eventTimings ) {
                eventTimingDataList.add( new EventTimingData( eventTiming ) );
            }
            return eventTimingDataList;
        }
    }



    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( eventPhase != null ) {
            Place location = eventPhase.getEvent().getScope();
            if ( location != null ) ids.add( location.getId() );
            Place placeBasis = eventPhase.getEvent().getPlaceBasis();
            if ( placeBasis != null ) ids.add( placeBasis.getId() );
        }
        return ids;
    }

    @XmlElement
    public String getLabel() {
        return getPrefix() + " " + getScenarioLabel();
    }

    public String getPrefix() {
        Phase phase = eventPhase.getPhase();
        return phase.isPreEvent()
                ? "The possibility of"
                : phase.isConcurrent()
                ? "The beginning of"
                : "The ending of event";
    }

    public String getScenarioLabel() {
        List<EventTimingData> eventPhaseContext = getEventTimings();
        StringBuilder sb = new StringBuilder();
        sb.append( eventPhase.getEvent().getLabel() );
        if ( eventPhaseContext != null && !eventPhaseContext.isEmpty() ) {
            sb.append( ", " );
            Iterator<EventTimingData> eventTimings = eventPhaseContext.iterator();
            while ( eventTimings.hasNext() ) {
                sb.append( eventTimings.next().getLabel() );
                if ( eventTimings.hasNext() ) {
                    sb.append( " and " );
                }
            }
        }
        return sb.toString();
    }

    public EventPhase eventPhase() {
        return eventPhase;
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof ObservationData
                && getLabel().equals( ((ObservationData)object).getLabel() );
    }

    @Override
    public int hashCode() {
        return getLabel().hashCode();
    }

}
