package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.ActorData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.entities.EventData;
import com.mindalliance.channels.api.entities.InfoFormatData;
import com.mindalliance.channels.api.entities.InfoProductData;
import com.mindalliance.channels.api.entities.MediumData;
import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.api.entities.PhaseData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.entities.RoleData;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
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
import java.io.Serializable;
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
@XmlType( propOrder = {"events", "phases", "organizations", "actors", "roles", "places", "media",
        "infoProducts", "formats"} )
public class EnvironmentData  implements Serializable {

    private ProceduresData procedures;
    private List<EventData> events;
    private List<PhaseData> phases;
    private List<OrganizationData> orgs;
    private List<ActorData> actors;
    private List<PlaceData> places;
    private List<RoleData> roles;
    private List<MediumData> media;
    private List<InfoProductData> infoProducts;
    private List<InfoFormatData> infoFormats;
    private Plan plan;

    public EnvironmentData() {
        // required
    }

    public EnvironmentData( String serverUrl, ProceduresData procedures, PlanCommunity planCommunity ) {
        this.procedures = procedures;
        initData( serverUrl, planCommunity );
    }

    private void initData( String serverUrl, PlanCommunity planCommunity ) {
        plan = planCommunity.getPlan();
        try {
            initEvents( serverUrl, planCommunity );
            initPhases( serverUrl, planCommunity );
            initOrgs( serverUrl,planCommunity );
            initActors( serverUrl,planCommunity );
            initPlaces(serverUrl, planCommunity );
            initRoles( serverUrl,planCommunity );
            initMedia( serverUrl,planCommunity );
            initInfoProducts( serverUrl, planCommunity );
            initInfoFormats( serverUrl, planCommunity );
        } catch ( NotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    private void initMedia( String serverUrl, PlanCommunity planCommunity ) throws NotFoundException {
        PlanService planService = planCommunity.getPlanService();
        media = new ArrayList<MediumData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allMediumIds() ) {
            TransmissionMedium medium = planService.find( TransmissionMedium.class, id );
            media.add( new MediumData( serverUrl,medium, getPlan() ) );
            added.add( id );
            for ( ModelEntity category : medium.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    media.add( new MediumData( serverUrl,(TransmissionMedium) category, getPlan() ) );
                    added.add( category.getId() );
                }
            }
            for ( TransmissionMedium delegate : medium.getEffectiveDelegatedToMedia() ) {
                if ( !added.contains( delegate.getId() ) ) {
                    media.add( new MediumData( serverUrl,delegate, getPlan() ) );
                    added.add( delegate.getId() );
                }
            }

        }
    }

    private void initRoles( String serverUrl, PlanCommunity planCommunity ) throws NotFoundException {
        PlanService planService = planCommunity.getPlanService();
        roles = new ArrayList<RoleData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allRoleIds() ) {
            Role role = planService.find( Role.class, id );
            roles.add( new RoleData( serverUrl,role, getPlan() ) );
            added.add( id );
            for ( ModelEntity category : role.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    roles.add( new RoleData( serverUrl,(Role) category, getPlan() ) );
                    added.add( category.getId() );
                }
            }
        }
    }

    private void initInfoProducts( String serverUrl, PlanCommunity planCommunity ) throws NotFoundException {
        PlanService planService = planCommunity.getPlanService();
        infoProducts = new ArrayList<InfoProductData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allInfoProductIds() ) {
            InfoProduct infoProduct = planService.find( InfoProduct.class, id );
            infoProducts.add( new InfoProductData( serverUrl,infoProduct, getPlan() ) );
            added.add( id );
            for ( ModelEntity category : infoProduct.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    infoProducts.add( new InfoProductData( serverUrl,(InfoProduct) category, getPlan() ) );
                    added.add( category.getId() );
                }
            }
        }
    }

    private void initInfoFormats( String serverUrl, PlanCommunity planCommunity ) throws NotFoundException {
        PlanService planService = planCommunity.getPlanService();
        infoFormats = new ArrayList<InfoFormatData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allInfoFormatIds() ) {
            InfoFormat infoFormat = planService.find( InfoFormat.class, id );
            infoFormats.add( new InfoFormatData( serverUrl,infoFormat, getPlan() ) );
            added.add( id );
            for ( ModelEntity category : infoFormat.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    infoFormats.add( new InfoFormatData( serverUrl,(InfoFormat) category, getPlan() ) );
                    added.add( category.getId() );
                }
            }
        }
    }



    private void initPlaces( String serverUrl, PlanCommunity planCommunity ) throws NotFoundException {
        PlanService planService = planCommunity.getPlanService();
        places = new ArrayList<PlaceData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allPlaceIds() ) {
            Place place = planService.find( Place.class, id );
            places.add( new PlaceData( serverUrl,place, getPlan() ) );
            added.add( id );
            for ( ModelEntity category : place.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    places.add( new PlaceData( serverUrl,(Place) category, getPlan() ) );
                    added.add( category.getId() );
                }
            }
        }

    }

    private void initActors( String serverUrl, PlanCommunity planCommunity ) throws NotFoundException {
        PlanService planService = planCommunity.getPlanService();
        actors = new ArrayList<ActorData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allActorIds() ) {
            Actor actor = planService.find( Actor.class, id );
            actors.add( new ActorData( serverUrl,actor, getPlan() ) );
            added.add( id );
            for ( ModelEntity category : actor.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    actors.add( new ActorData( serverUrl,(Actor) category, getPlan() ) );
                    added.add( category.getId() );
                }
            }
        }

    }

    private void initOrgs( String serverUrl, PlanCommunity planCommunity ) throws NotFoundException {
        PlanService planService = planCommunity.getPlanService();
        orgs = new ArrayList<OrganizationData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allOrganizationIds() ) {
            Organization org = planService.find( Organization.class, id );
            orgs.add( new OrganizationData( serverUrl,org, planCommunity ) );
            added.add( id );
            for ( ModelEntity category : org.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    orgs.add( new OrganizationData( serverUrl,(Organization) category, planCommunity ) );
                    added.add( category.getId() );
                }
            }
            for ( Organization parent : org.ancestors() ) {
                if ( !added.contains( parent.getId() ) ) {
                    orgs.add( new OrganizationData( serverUrl,parent, planCommunity ) );
                    added.add( parent.getId() );
                }
            }
        }

    }

    private void initPhases( String serverUrl, PlanCommunity planCommunity ) throws NotFoundException {
        PlanService planService = planCommunity.getPlanService();
        phases = new ArrayList<PhaseData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allPhaseIds() ) {
            Phase phase = planService.find( Phase.class, id );
            phases.add( new PhaseData( serverUrl, phase, getPlan() ) );
            added.add( id );
            for ( ModelEntity category : phase.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    phases.add( new PhaseData( serverUrl, (Phase) category, getPlan() ) );
                    added.add( category.getId() );
                }
            }
        }

    }

    private void initEvents(  String serverUrl, PlanCommunity planCommunity ) throws NotFoundException {
        PlanService planService = planCommunity.getPlanService();
        events = new ArrayList<EventData>();
        for ( Long eventId : allEventIds() ) {
            Event event = planService.find( Event.class, eventId );
            events.add( new EventData( serverUrl, event, getPlan() ) );
        }
    }

    @XmlElement( name = "event" )
    public List<EventData> getEvents() {
        return events;
    }

    @XmlElement( name = "phase" )
    public List<PhaseData> getPhases() {
        return phases;
    }

    @XmlElement( name = "organization" )
    public List<OrganizationData> getOrganizations() {
        return orgs;
    }

    @XmlElement( name = "agent" )
    public List<ActorData> getActors() {
        return actors;
    }

    @XmlElement( name = "role" )
    public List<RoleData> getRoles() throws NotFoundException {
        return roles;
    }

    @XmlElement( name = "place" )
    public List<PlaceData> getPlaces() {
        return places;
    }

    @XmlElement( name = "transmissionMedium" )
    public List<MediumData> getMedia() throws NotFoundException {
        return media;
    }

    @XmlElement( name = "infoProduct" )
    public List<InfoProductData> getInfoProducts() throws NotFoundException {
        return infoProducts;
    }

    @XmlElement( name = "format" )
    public List<InfoFormatData> getFormats() throws NotFoundException {
        return infoFormats;
    }



    private Set<Long> allEventIds() {
        Set<Long> allIds = new HashSet<Long>();
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allEventIds() );
        }
        return allIds;
    }

    private Set<Long> allPhaseIds() {
        Set<Long> allIds = new HashSet<Long>();
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allPhaseIds() );
        }
        return allIds;
    }

    private Set<Long> allOrganizationIds() {
        Set<Long> allIds = new HashSet<Long>();
        for ( EmploymentData employment : procedures.getEmployments() ) {
            Long orgId = employment.getOrganizationId();
            if ( orgId != null )
                allIds.add( orgId );
        }
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allOrganizationIds() );
        }
        return allIds;
    }

    private Set<Long> allActorIds() {
        Set<Long> allIds = new HashSet<Long>();
        for ( EmploymentData employment : procedures.getEmployments() ) {
            allIds.addAll( employment.allActorIds() );
            if ( employment.getSupervisorId() != null )
                allIds.add( employment.getSupervisorId() );
        }
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allActorIds() );
        }
        return allIds;
    }

    private Set<Long> allRoleIds() {
        Set<Long> allIds = new HashSet<Long>();
        for ( EmploymentData employment : procedures.getEmployments() ) {
            allIds.add( employment.getRoleId() );
        }
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allRoleIds() );
        }
        return allIds;
    }

    private Set<Long> allPlaceIds() {
        Set<Long> allIds = new HashSet<Long>();
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
        Set<Long> allIds = new HashSet<Long>();
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allMediumIds() );
        }
        return allIds;
    }

    private Set<Long> allInfoProductIds() {
        Set<Long> allIds = new HashSet<Long>();
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allInfoProductIds() );
        }
        return allIds;
    }

    private Set<Long> allInfoFormatIds() {
        Set<Long> allIds = new HashSet<Long>();
        for ( ProcedureData procedure : procedures.getProcedures() ) {
            allIds.addAll( procedure.allInfoFormatIds() );
        }
        return allIds;
    }



    private Plan getPlan() {
        return plan;
    }
}
