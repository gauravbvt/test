package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web Service data element containing plan metrics.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 6:10 PM
 */
@XmlType( propOrder = {"eventCount", "phaseCount", "agentCount", "organizationCount", "roleCount", "placeCount",
        "transmissionMediumCount", "segmentCount", "taskCount", "flowCount", "needsCount", "capabilitiesCount" } )
public class PlanMetricsData {

    private PlanService planService;

    public PlanMetricsData() {
        // required
    }

    public PlanMetricsData( PlanService planService ) {
        this.planService = planService;
    }

    @XmlElement
    public int getEventCount() {
        return planService.list( Event.class ).size();
    }

    @XmlElement
    public int getPhaseCount() {
        return planService.list( Phase.class ).size();
    }

    @XmlElement
    public int getAgentCount() {
        return planService.list( Actor.class ).size();
    }

    @XmlElement
    public int getOrganizationCount() {
        return planService.list( Organization.class ).size();
    }

    @XmlElement
    public int getRoleCount() {
        return planService.list( Role.class ).size();
    }

    @XmlElement
    public int getPlaceCount() {
        return planService.list( Place.class ).size();
    }

    @XmlElement
    public int getTransmissionMediumCount() {
        return planService.list( TransmissionMedium.class ).size();
    }

    @XmlElement
    public int getSegmentCount() {
        return planService.getPlan().getSegmentCount();
    }

    @XmlElement
    public int getTaskCount() {
        int count = 0;
        for ( Segment segment : planService.getPlan().getSegments() ) {
            count += segment.listParts().size();
        }
        return count;
    }

    @XmlElement
    public int getFlowCount() {
        int count = 0;
        for ( Segment segment : planService.getPlan().getSegments() ) {
            count += segment.getAllSharingFlows().size();
        }
        return count;
    }

    @XmlElement
    public int getNeedsCount() {
        int count = 0;
         for ( Segment segment : planService.getPlan().getSegments() ) {
             for ( Flow flow : segment.listFlows() ) {
                 if ( flow.isNeed() ) count++;
             }
         }
         return count;
    }

    @XmlElement
    public int getCapabilitiesCount() {
        int count = 0;
         for ( Segment segment : planService.getPlan().getSegments() ) {
             for ( Flow flow : segment.listFlows() ) {
                 if ( flow.isCapability() ) count++;
             }
         }
         return count;
    }


}
