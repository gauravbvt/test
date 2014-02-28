package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.ModelObjectData;
import com.mindalliance.channels.api.entities.ActorData;
import com.mindalliance.channels.api.entities.EventData;
import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.api.entities.PhaseData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.entities.RoleData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.query.ModelService;

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
@XmlRootElement(name = "modelScope", namespace = "http://mind-alliance.com/api/isp/v1/")
@XmlType(propOrder = {"date", "identity", "phases", "places", "events", "roles", "organizations", "actors"})
public class ModelScopeData implements Serializable {

    private Map<Long, ModelObjectData> cache;
    private List<PhaseData> phases;
    private List<PlaceData> places;
    private List<EventData> events;
    private List<RoleData> roles;
    private List<OrganizationData> orgs;
    private List<ActorData> actors;
    private ModelIdentifierData modelIdentifierData;

    public ModelScopeData() {
        // required for JAXB
    }

    public ModelScopeData( String serverUrl, CommunityService communityService ) {
        cache = new HashMap<Long, ModelObjectData>();
        init( serverUrl, communityService );
    }

    private void init( String serverUrl, CommunityService communityService ) {
        modelIdentifierData = new ModelIdentifierData( communityService );
        initPhases( serverUrl, communityService );
        initPlaces( serverUrl, communityService );
        initEvents( serverUrl, communityService );
        initRoles( serverUrl, communityService );
        initOrgs( serverUrl, communityService );
        initActors( serverUrl, communityService );
    }

    private void initActors( String serverUrl, CommunityService communityService ) {
        ModelService modelService = communityService.getModelService();
        actors = new ArrayList<ActorData>();
        for ( Actor actor : modelService.list( Actor.class ) ) {
            if ( !actor.isUnknown() && !actor.isUniversal() )
                actors.add( cache( actor, new ActorData( serverUrl, actor, communityService ) ) );
        }

    }

    private void initOrgs( String serverUrl, CommunityService communityService ) {
        ModelService modelService = communityService.getModelService();
        orgs = new ArrayList<OrganizationData>();
        for ( Organization org : modelService.list( Organization.class ) ) {
            if ( !org.isUnknown() && !org.isUniversal() )
                orgs.add( cache( org, new OrganizationData( serverUrl, org, communityService ) ) );
        }

    }

    private void initRoles( String serverUrl, CommunityService communityService ) {
        ModelService modelService = communityService.getModelService();
        roles = new ArrayList<RoleData>();
        for ( Role role : modelService.list( Role.class ) ) {
            if ( !role.isUnknown() && !role.isUniversal() )
                roles.add( cache( role, new RoleData( serverUrl, role, communityService ) ) );
        }

    }

    private void initEvents( String serverUrl, CommunityService communityService ) {
        ModelService modelService = communityService.getModelService();
        events = new ArrayList<EventData>();
        for ( Event event : modelService.list( Event.class ) ) {
            if ( !event.isUnknown() && !event.isUniversal() )
                events.add( cache( event, new EventData( serverUrl, event, communityService ) ) );
        }

    }

    private void initPlaces( String serverUrl, CommunityService communityService ) {
        places = new ArrayList<PlaceData>();
        for ( Place place : communityService.list( Place.class ) ) {
            if ( !place.isUnknown() && !place.isUniversal() )
                places.add( cache( place, new PlaceData( serverUrl, place, communityService ) ) );
        }
    }

    private void initPhases( String serverUrl, CommunityService communityService ) {
        ModelService modelService = communityService.getModelService();
        phases = new ArrayList<PhaseData>();
        for ( Phase phase : modelService.list( Phase.class ) ) {
            if ( !phase.isUnknown() && !phase.isUniversal() )
                phases.add( cache( phase, new PhaseData( serverUrl, phase, communityService ) ) );
        }
    }

    @XmlElement
    public String getDate() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() );
    }

    @XmlElement
    public ModelIdentifierData getIdentity() {
        return modelIdentifierData;
    }

    @XmlElement(name = "phase")
    public List<PhaseData> getPhases() {
        return phases;
    }

    private <T extends ModelObjectData> T cache( ModelObject mo, T moData ) {
        cache.put( mo.getId(), moData );
        return moData;
    }

    @XmlElement(name = "place")
    public List<PlaceData> getPlaces() {
        return places;
    }

    @XmlElement(name = "event")
    public List<EventData> getEvents() {
        return events;
    }

    @XmlElement(name = "role")
    public List<RoleData> getRoles() {
        return roles;
    }

    @XmlElement(name = "organization")
    public List<OrganizationData> getOrganizations() {
        return orgs;
    }

    @XmlElement(name = "agent")    // todo - change name to "actor"
    public List<ActorData> getActors() {
        return actors;
    }

    public <T extends ModelObjectData> T findInScope( Class<T> moDataClass, long moId ) {
        return (T) cache.get( moId );
    }
}
