package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Segment;

import javax.xml.bind.annotation.XmlElement;
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
@XmlType( propOrder = {"id", "name", "description", "categories", "incident", "selfTerminating", "locationId", "plannedPhases", "documentation"} )
public class EventData extends ModelEntityData {

    public EventData() {
    }

    public EventData( String serverUrl, Event event, CommunityService communityService ) {
        super( serverUrl, event, communityService );
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

    @Override
    @XmlElement
    public String getDescription() {
        return super.getDescription();
    }

    @XmlElement
    public Boolean getIncident() {
        return getCollaborationModel().isIncident( getEvent() );
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
        for ( Segment segment : getCollaborationModel().getSegments() ) {
            if (segment.getEvent().equals( getEvent() ) ) {
                phaseIds.add( segment.getPhase().getId() );
            }
        }
        return new ArrayList<Long>( phaseIds );
    }

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    private Event getEvent() {
        return (Event)getModelObject();
    }
}
