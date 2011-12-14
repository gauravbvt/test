package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.entities.EventData;
import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.api.entities.PhaseData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.entities.RoleData;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for the scope of a plan.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/11
 * Time: 1:25 PM
 */
@XmlRootElement( name = "planScope", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"identity", "phases", "places", "events", "roles", "organizations", "actors", "employments"} )
public class PlanScopeData {

    private Plan plan;
    private PlanService planService;

    public PlanScopeData() {
        // required for JAXB
    }

    public PlanScopeData( Plan plan, PlanService planService ) {
        this.plan = plan;
        this.planService = planService;
    }

    public void setPlan( Plan plan ) {
        this.plan = plan;
    }

    @XmlElement
    public PlanIdentifierData getIdentity() {
        return new PlanIdentifierData( plan );
    }

    @XmlElement( name = "phase" )
    public List<PhaseData> getPhases() {
        List<PhaseData> phases = new ArrayList<PhaseData>();
        for ( Phase phase : planService.list( Phase.class ) ) {
            if ( !phase.isUnknown() && !phase.isUniversal() )
                phases.add( new PhaseData( phase, plan ) );
        }
        return phases;
    }

    @XmlElement( name = "place" )
    public List<PlaceData> getPlaces() {
        List<PlaceData> places = new ArrayList<PlaceData>();
        for ( Place place : planService.list( Place.class ) ) {
            if ( !place.isUnknown() && !place.isUniversal() )
                places.add( new PlaceData( place, plan ) );
        }
        return places;
    }

    @XmlElement( name = "event" )
    public List<EventData> getEvents() {
        List<EventData> events = new ArrayList<EventData>();
        for ( Event event : planService.list( Event.class ) ) {
            if ( !event.isUnknown() && !event.isUniversal() )
                events.add( new EventData( event, plan ) );
        }
        return events;
    }

    @XmlElement( name = "role" )
    public List<RoleData> getRoles() {
        List<RoleData> roles = new ArrayList<RoleData>();
        for ( Role role : planService.list( Role.class ) ) {
            if ( !role.isUnknown() && !role.isUniversal() )
                roles.add( new RoleData( role, plan ) );
        }
        return roles;
    }

    @XmlElement( name = "organization" )
    public List<OrganizationData> getOrganizations() {
        List<OrganizationData> orgs = new ArrayList<OrganizationData>();
        for ( Organization org : planService.list( Organization.class ) ) {
            if ( !org.isUnknown() && !org.isUniversal() )
                orgs.add( new OrganizationData( org, planService ) );
        }
        return orgs;
    }

    @XmlElement( name = "agent" )
    public List<AgentData> getActors() {
        List<AgentData> actors = new ArrayList<AgentData>();
        for ( Actor actor : planService.list( Actor.class ) ) {
            if ( !actor.isUnknown() && !actor.isUniversal() )
                actors.add( new AgentData( actor, plan ) );
        }
        return actors;
    }

    @XmlElement( name = "employment" )
    public List<EmploymentData> getEmployments() {
        List<EmploymentData> employments = new ArrayList<EmploymentData>(  );
        for ( Organization org : planService.list( Organization.class ) ) {
            if ( !org.isUnknown() && !org.isUniversal() )
                for( Employment employment : planService.findAllEmploymentsIn( org ) ) {
                    employments.add( new EmploymentData( employment ) );
                }
        }
        return employments;
    }


}
