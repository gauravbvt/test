package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.ActorData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.entities.EventData;
import com.mindalliance.channels.api.entities.MediumData;
import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.api.entities.PhaseData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.entities.RoleData;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
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
@XmlType( propOrder = {"events", "phases", "organizations", "actors", "roles", "places", "media"} )
public class EnvironmentData {

    private ProceduresData procedures;
    private PlanService planService;

    public EnvironmentData() {
        // required
    }

    public EnvironmentData( ProceduresData procedures, PlanService planService ) {
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
        Set<Long> added = new HashSet<Long>(  );
         for ( Long id : allPhaseIds() ) {
             Phase phase = planService.find(  Phase.class, id );
             phases.add( new PhaseData( phase ) );
             added.add( id );
             for ( ModelEntity category : phase.getAllTypes() ) {
                 if (!added.contains( category.getId() ) ) {
                     phases.add( new PhaseData( (Phase)category ) );
                     added.add( category.getId() );
                 }
             }
         }
        return phases;
    }

    @XmlElement( name = "organization" )
    public List<OrganizationData> getOrganizations() throws NotFoundException {
        List<OrganizationData> orgs = new ArrayList<OrganizationData>(  );
        Set<Long> added = new HashSet<Long>(  );
         for ( Long id : allOrganizationIds() ) {
             Organization org = planService.find(  Organization.class, id );
             orgs.add( new OrganizationData( org, planService ) );
             added.add( id );
             for ( ModelEntity category : org.getAllTypes() ) {
                 if (!added.contains( category.getId() ) ) {
                     orgs.add( new OrganizationData( (Organization)category, planService ) );
                     added.add( category.getId() );
                 }
             }
             for ( Organization parent : org.ancestors() ) {
                 if (!added.contains( parent.getId() ) ) {
                     orgs.add( new OrganizationData( parent, planService ) );
                     added.add( parent.getId() );
                 }
             }
         }
        return orgs;
    }

     @XmlElement( name = "agent" )
    public List<ActorData> getActors() throws NotFoundException {
        List<ActorData> actors = new ArrayList<ActorData>(  );
         Set<Long> added = new HashSet<Long>(  );
         for ( Long id : allActorIds() ) {
             Actor actor = planService.find( Actor.class, id );
             actors.add( new ActorData( actor ) );
             added.add( id );
             for ( ModelEntity category : actor.getAllTypes() ) {
                 if (!added.contains( category.getId() ) ) {
                     actors.add( new ActorData( (Actor)category ) );
                     added.add( category.getId() );
                 }
             }
         }
        return actors;
    }

    @XmlElement( name = "role" )
    public List<RoleData> getRoles() throws NotFoundException {
        List<RoleData> roles = new ArrayList<RoleData>(  );
        Set<Long> added = new HashSet<Long>(  );
         for ( Long id : allRoleIds() ) {
             Role role = planService.find( Role.class, id );
             roles.add( new RoleData( role ) );
             added.add( id );
             for ( ModelEntity category : role.getAllTypes() ) {
                 if (!added.contains( category.getId() ) ) {
                     roles.add( new RoleData( (Role)category ) );
                     added.add( category.getId() );
                 }
             }
         }
        return roles;
    }

    @XmlElement( name = "place" )
    public List<PlaceData> getPlaces() throws NotFoundException {
        List<PlaceData> places = new ArrayList<PlaceData>(  );
        Set<Long> added = new HashSet<Long>(  );
         for ( Long id : allPlaceIds() ) {
             Place place = planService.find( Place.class, id );
             places.add( new PlaceData( place ) );
             added.add( id );
             for ( ModelEntity category : place.getAllTypes() ) {
                 if (!added.contains( category.getId() ) ) {
                     places.add( new PlaceData( (Place)category ) );
                     added.add( category.getId() );
                 }
             }
         }
        return places;
    }

    @XmlElement( name = "transmissionMedium" )
    public List<MediumData> getMedia() throws NotFoundException {
        List<MediumData> media = new ArrayList<MediumData>(  );
        Set<Long> added = new HashSet<Long>(  );
         for ( Long id : allMediumIds() ) {
             TransmissionMedium medium = planService.find( TransmissionMedium.class, id );
             media.add( new MediumData( medium ) );
             added.add( id );
             for ( ModelEntity category : medium.getAllTypes() ) {
                 if (!added.contains( category.getId() ) ) {
                     media.add( new MediumData( (TransmissionMedium)category ) );
                     added.add( category.getId() );
                 }
             }
             for ( TransmissionMedium delegate : medium.getEffectiveDelegatedToMedia() ) {
                 if (!added.contains( delegate.getId() ) ) {
                     media.add( new MediumData( delegate ) );
                     added.add( delegate.getId() );
                 }
             }

         }
        return media;
    }

    private Set<Long> allEventIds() {
        Set<Long> allIds = new HashSet<Long>(  );
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allEventIds() );
        }
        return allIds;
    }

    private Set<Long> allPhaseIds() {
        Set<Long> allIds = new HashSet<Long>(  );
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allPhaseIds() );
        }
        return allIds;
    }

    private Set<Long> allOrganizationIds() {
        Set<Long> allIds = new HashSet<Long>(  );
        for ( EmploymentData employment : procedures.getEmployments() ) {
            allIds.add( employment.getOrganizationId() );
        }
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allOrganizationIds() );
        }
        return allIds;
     }

    private Set<Long> allActorIds() {
        Set<Long> allIds = new HashSet<Long>(  );
        for ( EmploymentData employment : procedures.getEmployments() ) {
            allIds.add(  employment.getActorId() );
            if ( employment.getSupervisorId() != null )
                allIds.add( employment.getSupervisorId() );
        }
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allActorIds() );
        }
        return allIds;
     }

    private Set<Long> allRoleIds() {
        Set<Long> allIds = new HashSet<Long>(  );
        for ( EmploymentData employment : procedures.getEmployments() ) {
            allIds.add( employment.getRoleId() );
        }
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allRoleIds() );
        }
        return allIds;
     }

    private Set<Long> allPlaceIds() {
        Set<Long> allIds = new HashSet<Long>(  );
        for ( EmploymentData employment : procedures.getEmployments() ) {
            if ( employment.getJurisdictionId() != null )
                allIds.add( employment.getJurisdictionId() );
        }
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allPlaceIds() );
        }
        return allIds;
     }

    private Set<Long> allMediumIds() {
        Set<Long> allIds = new HashSet<Long>(  );
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allMediumIds() );
        }
        return allIds;
     }

    private Plan getPlan() {
        return planService.getPlan();
    }
}
