package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EventData;
import com.mindalliance.channels.api.entities.PhaseData;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Web Service data element for a discovery made by an agent.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 12:49 PM
 */
@XmlRootElement( name = "discovery", namespace = "http://mind-alliance.com/api/isp/v1/" )
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
    public EventData getEvent() {
        if ( eventPhase != null )
            return new EventData( eventPhase.getEvent(), planService.getPlan() );
        else
            return null;
    }

    @XmlElement
    public PhaseData getPhase() {
        if ( eventPhase != null )
            return new PhaseData( eventPhase.getPhase() );
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
