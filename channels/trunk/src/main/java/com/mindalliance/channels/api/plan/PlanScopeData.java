package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.ModelObjectData;
import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.entities.EventData;
import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.api.entities.PhaseData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.entities.RoleData;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web Service data element for the scope of a plan.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/11
 * Time: 1:25 PM
 */
@XmlRootElement( name = "planScope", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"date", "identity", "phases", "places", "events", "roles", "organizations", "actors", "employments"} )
public class PlanScopeData  implements Serializable {

    private PlanCommunity planCommunity;
    private Map<Long, ModelObjectData> cache;
    private List<PhaseData> phases;
    private List<PlaceData> places;
    private List<EventData> events;
    private List<RoleData> roles;
    private List<OrganizationData> orgs;
    private List<AgentData> actors;
    private ArrayList<EmploymentData> employments;

    public PlanScopeData() {
        // required for JAXB
    }

    public PlanScopeData( String serverUrl, PlanCommunity planCommunity ) {
        this.planCommunity = planCommunity;
        cache = new HashMap<Long, ModelObjectData>();
        init(  serverUrl, planCommunity );
    }

    private void init( String serverUrl, PlanCommunity planCommunity ) {
        initPhases( serverUrl, planCommunity );
        initPlaces( serverUrl, planCommunity );
        initEvents( serverUrl, planCommunity );
        initRoles( serverUrl,planCommunity );
        initOrgs( serverUrl,planCommunity );
        initActors( serverUrl,planCommunity );
        initEmployments( planCommunity );
    }

    private void initEmployments( PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        employments = new ArrayList<EmploymentData>();
        for ( Organization org : planService.list( Organization.class ) ) {
            if ( !org.isUnknown() && !org.isUniversal() )
                for ( Employment employment : planService.findAllEmploymentsIn( org ) ) {
                    employments.add( new EmploymentData( employment ) );
                }
        }

    }

    private void initActors( String serverUrl, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        actors = new ArrayList<AgentData>();
        for ( Actor actor : planService.list( Actor.class ) ) {
            if ( !actor.isUnknown() && !actor.isUniversal() )
                actors.add( cache( actor, new AgentData( serverUrl,actor, getPlan() ) ) );
        }

    }

    private Plan getPlan() {
        return planCommunity.getPlan();
    }

    private void initOrgs( String serverUrl, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        orgs = new ArrayList<OrganizationData>();
        for ( Organization org : planService.list( Organization.class ) ) {
            if ( !org.isUnknown() && !org.isUniversal() )
                orgs.add( cache( org, new OrganizationData( serverUrl,org, planCommunity ) ) );
        }

    }

    private void initRoles( String serverUrl, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        roles = new ArrayList<RoleData>();
        for ( Role role : planService.list( Role.class ) ) {
            if ( !role.isUnknown() && !role.isUniversal() )
                roles.add( cache( role, new RoleData( serverUrl,role, getPlan() ) ) );
        }

    }

    private void initEvents( String serverUrl, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        events = new ArrayList<EventData>();
        for ( Event event : planService.list( Event.class ) ) {
            if ( !event.isUnknown() && !event.isUniversal() )
                events.add( cache( event, new EventData( serverUrl,event, getPlan() ) ) );
        }

    }

    private void initPlaces( String serverUrl, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        places = new ArrayList<PlaceData>();
        for ( Place place : planService.list( Place.class ) ) {
            if ( !place.isUnknown() && !place.isUniversal() )
                places.add( cache( place, new PlaceData( serverUrl,place, getPlan() ) ) );
        }
    }

    private void initPhases( String serverUrl, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        phases = new ArrayList<PhaseData>();
        for ( Phase phase : planService.list( Phase.class ) ) {
            if ( !phase.isUnknown() && !phase.isUniversal() )
                phases.add( cache( phase, new PhaseData( serverUrl,phase, getPlan() ) ) );
        }
    }

    @XmlElement
    public String getDate() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() );
    }

    @XmlElement
    public PlanIdentifierData getIdentity() {
        return new PlanIdentifierData( planCommunity );
    }

    @XmlElement( name = "phase" )
    public List<PhaseData> getPhases() {
        return phases;
    }

    private <T extends ModelObjectData> T cache( ModelObject mo, T moData ) {
        cache.put( mo.getId(), moData );
        return moData;
    }

    @XmlElement( name = "place" )
    public List<PlaceData> getPlaces() {
        return places;
    }

    @XmlElement( name = "event" )
    public List<EventData> getEvents() {
        return events;
    }

    @XmlElement( name = "role" )
    public List<RoleData> getRoles() {
        return roles;
    }

    @XmlElement( name = "organization" )
    public List<OrganizationData> getOrganizations() {
        return orgs;
    }

    @XmlElement( name = "agent" )
    public List<AgentData> getActors() {
        return actors;
    }

    @XmlElement( name = "employment" )
    public List<EmploymentData> getEmployments() {
        return employments;
    }


    public <T extends ModelObjectData> T findInScope( Class<T> moDataClass, long moId ) {
        return (T) cache.get( moId );
    }
}
