package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.EventPhase;

import javax.xml.bind.annotation.XmlElement;

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


}
