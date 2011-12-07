package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.ModelEntityData;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web Service data element for an event.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/11
 * Time: 10:36 AM
 */
@XmlRootElement( name = "event", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"id", "name", "categories", "incident", "selfTerminating", "locationId", "plannedPhases"} )
public class EventData extends ModelEntityData {

    private Plan plan;

    public EventData() {
    }

    public EventData( Event event, Plan plan ) {
        super( event );
        this.plan = plan;
    }

    @Override
    @XmlElement
    public long getId() {
        return super.getId();
    }

    @Override
    @XmlElement
    public String getName() {
        return super.getName();
    }

    @XmlElement
    public Boolean getIncident() {
        return plan.isIncident( getEvent() );
    }

    @XmlElement
    public Long getLocationId() {
        return getEvent().getScope() == null
                ? null
                : getEvent().getScope().getId();
    }

    @XmlElement
    public Boolean getSelfTerminating() {
        return getEvent().isSelfTerminating();
    }

    @XmlElement(name = "categoryId")
    public List<Long> getCategories() {
        return super.getCategories();
    }

    @XmlElement(name = "plannedPhaseId")
    public List<Long> getPlannedPhases() {
        Set<Long> phaseIds = new HashSet<Long>(  );
        for ( Segment segment : plan.getSegments() ) {
            if (segment.getEvent().equals( getEvent() ) ) {
                phaseIds.add( segment.getPhase().getId() );
            }
        }
        return new ArrayList<Long>( phaseIds );
    }

    private Event getEvent() {
        return (Event)getModelObject();
    }
}
