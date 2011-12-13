package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;

/**
 * Web Service data element for a discovery made by an agent.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 12:49 PM
 */
public class DiscoveryData {

    private EventPhase eventPhase;
    private PlanService planService;
    private Commitment notificationToSelf;

    public DiscoveryData() {
        // required
    }

    public DiscoveryData( EventPhase eventPhase, PlanService planService ) {
        this.eventPhase = eventPhase;
        this.planService = planService;
    }

    public DiscoveryData( Commitment notificationToSelf, PlanService planService ) {
        this.notificationToSelf = notificationToSelf;
        this.planService = planService;
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

    @XmlElement
    public InfoDiscoveredData getInformationDiscovered() {
        if ( notificationToSelf != null )
            return new InfoDiscoveredData( notificationToSelf, planService );
        else
            return null;
    }

}
