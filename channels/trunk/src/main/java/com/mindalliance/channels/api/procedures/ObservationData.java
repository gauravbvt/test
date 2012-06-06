package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.Place;

import javax.xml.bind.annotation.XmlElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/21/12
 * Time: 9:28 PM
 */
public class ObservationData {

    private EventPhase eventPhase;

    public ObservationData() {
    }

    public ObservationData( EventPhase eventPhase  ) {
        this.eventPhase = eventPhase;
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
}
