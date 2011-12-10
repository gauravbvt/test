package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EventData;
import com.mindalliance.channels.api.entities.PhaseData;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web service data element for all entities referenced by an actor's procedures.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/9/11
 * Time: 3:43 PM
 */
@XmlRootElement( name = "environment", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"events", "phases"/*, "organizations", "actors", "roles", "places", "media"*/} )
public class EnvironmentData {

    private List<ProcedureData> procedures;
    private PlanService planService;

    public EnvironmentData() {
        // required
    }

    public EnvironmentData( List<ProcedureData> procedures, PlanService planService ) {
        this.procedures = procedures;
        this.planService = planService;
    }

    @XmlElement( name = "event" )
    public List<EventData> getEvents() throws NotFoundException {
        List<EventData> events = new ArrayList<EventData>(  );
         for ( Long eventId : allEventIds() ) {
             Event event = planService.find(  Event.class, eventId );
             events.add( new EventData( event, getPlan() ) );
         }
        return events;
    }

    @XmlElement( name = "phase" )
    public List<PhaseData> getPhases() throws NotFoundException {
        List<PhaseData> phases = new ArrayList<PhaseData>(  );
         for ( Long phaseId : allPhaseIds() ) {
             Phase phase = planService.find(  Phase.class, phaseId );
             phases.add( new PhaseData( phase ) );
         }
        return phases;
    }


    private Set<Long> allEventIds() {
        Set<Long> allEventIds = new HashSet<Long>(  );
        for ( ProcedureData procedure : procedures ) {
            allEventIds.addAll( procedure.allEventIds() );
        }
        return allEventIds;
    }

    private Set<Long> allPhaseIds() {
        Set<Long> allPhaseIds = new HashSet<Long>(  );
        for ( ProcedureData procedure : procedures ) {
            allPhaseIds.addAll( procedure.allPhaseIds() );
        }
        return allPhaseIds;
    }


    private Plan getPlan() {
        return planService.getPlan();
    }
}
