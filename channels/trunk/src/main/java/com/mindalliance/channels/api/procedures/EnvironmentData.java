package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.ActorData;
import com.mindalliance.channels.api.entities.EventData;
import com.mindalliance.channels.api.entities.FunctionData;
import com.mindalliance.channels.api.entities.InfoFormatData;
import com.mindalliance.channels.api.entities.InfoProductData;
import com.mindalliance.channels.api.entities.MediumData;
import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.api.entities.PhaseData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.entities.RoleData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@XmlType(propOrder = {"events", "phases", "organizations", "actors", "roles", "places", "media",
        "infoProducts", "formats", "functions"})
public class EnvironmentData implements Serializable {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EnvironmentData.class );

    private List<EventData> events;
    private List<PhaseData> phases;
    private List<OrganizationData> orgs;
    private List<ActorData> actors;
    private List<PlaceData> places;
    private List<RoleData> roles;
    private List<MediumData> media;
    private List<InfoProductData> infoProducts;
    private List<InfoFormatData> infoFormats;
    private List<FunctionData> functions;
    private ProtocolsData protocols;

    public EnvironmentData() {
        // required
    }

    public EnvironmentData( String serverUrl, ProtocolsData protocols, CommunityService communityService ) {
        this.protocols = protocols;
        initData( serverUrl, communityService );
    }

    private void initData( String serverUrl, CommunityService communityService ) {
        try {
            initEvents( serverUrl, communityService );
            initPhases( serverUrl, communityService );
            initOrgs( serverUrl, communityService );
            initActors( serverUrl, communityService );
            initPlaces( serverUrl, communityService );
            initRoles( serverUrl, communityService );
            initMedia( serverUrl, communityService );
            initInfoProducts( serverUrl, communityService );
            initInfoFormats( serverUrl, communityService );
            initFunctions( serverUrl, communityService );
        } catch ( NotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    private void initMedia( String serverUrl, CommunityService communityService ) throws NotFoundException {
        PlanService planService = communityService.getPlanService();
        media = new ArrayList<MediumData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allMediumIds() ) {
            TransmissionMedium medium = planService.find( TransmissionMedium.class, id );
            media.add( new MediumData( serverUrl, medium, communityService ) );
            added.add( id );
            for ( ModelEntity category : medium.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    media.add( new MediumData( serverUrl, (TransmissionMedium) category, communityService ) );
                    added.add( category.getId() );
                }
            }
            for ( TransmissionMedium delegate : medium.getEffectiveDelegatedToMedia() ) {
                if ( !added.contains( delegate.getId() ) ) {
                    media.add( new MediumData( serverUrl, delegate, communityService ) );
                    added.add( delegate.getId() );
                }
            }

        }
    }

    private void initRoles( String serverUrl, CommunityService communityService ) throws NotFoundException {
        PlanService planService = communityService.getPlanService();
        roles = new ArrayList<RoleData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allRoleIds() ) {
            Role role = planService.find( Role.class, id );
            roles.add( new RoleData( serverUrl, role, communityService ) );
            added.add( id );
            for ( ModelEntity category : role.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    roles.add( new RoleData( serverUrl, (Role) category, communityService ) );
                    added.add( category.getId() );
                }
            }
        }
    }

    private void initInfoProducts( String serverUrl, CommunityService communityService ) throws NotFoundException {
        PlanService planService = communityService.getPlanService();
        infoProducts = new ArrayList<InfoProductData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allInfoProductIds() ) {
            try {
                InfoProduct infoProduct = planService.find( InfoProduct.class, id );
                infoProducts.add( new InfoProductData( serverUrl, infoProduct, communityService ) );
                added.add( id );
                for ( ModelEntity category : infoProduct.getAllTypes() ) {
                    if ( !added.contains( category.getId() ) ) {
                        infoProducts.add( new InfoProductData( serverUrl, (InfoProduct) category, communityService ) );
                        added.add( category.getId() );
                    }
                }
            } catch ( NotFoundException e ) {
                LOG.warn( "Info product not found at " + id ); // todo - dev vs prod worlds - prevent this at the source
            }
        }
    }

    private void initFunctions( String serverUrl, CommunityService communityService ) throws NotFoundException {
        PlanService planService = communityService.getPlanService();
        functions = new ArrayList<FunctionData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allFunctionIds() ) {
            try {
                Function function = planService.find( Function.class, id );
                functions.add( new FunctionData( serverUrl, function, communityService ) );
                added.add( id );
                for ( ModelEntity category : function.getAllTypes() ) {
                    if ( !added.contains( category.getId() ) ) {
                        functions.add( new FunctionData( serverUrl, (Function) category, communityService ) );
                        added.add( category.getId() );
                    }
                }
            } catch ( NotFoundException e ) {
                LOG.warn( "Function not found at " + id );
            }
        }
    }


    private void initInfoFormats( String serverUrl, CommunityService communityService ) throws NotFoundException {
        PlanService planService = communityService.getPlanService();
        infoFormats = new ArrayList<InfoFormatData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allInfoFormatIds() ) {
            try {
                InfoFormat infoFormat = planService.find( InfoFormat.class, id );
                infoFormats.add( new InfoFormatData( serverUrl, infoFormat, communityService ) );
                added.add( id );
                for ( ModelEntity category : infoFormat.getAllTypes() ) {
                    if ( !added.contains( category.getId() ) ) {
                        infoFormats.add( new InfoFormatData( serverUrl, (InfoFormat) category, communityService ) );
                        added.add( category.getId() );
                    }
                }
            } catch ( NotFoundException e ) {
                LOG.warn( "Info format not found at " + id ); // todo - dev vs prod worlds - prevent this at the source
            }
        }
    }


    private void initPlaces( String serverUrl, CommunityService communityService ) throws NotFoundException {
        places = new ArrayList<PlaceData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allPlaceIds() ) {
            Place place = communityService.find( Place.class, id );
            places.add( new PlaceData( serverUrl, place, communityService ) );
            added.add( id );
            for ( ModelEntity category : place.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    places.add( new PlaceData( serverUrl, (Place) category, communityService ) );
                    added.add( category.getId() );
                }
            }
        }

    }

    private void initActors( String serverUrl, CommunityService communityService ) throws NotFoundException {
        PlanService planService = communityService.getPlanService();
        actors = new ArrayList<ActorData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allActorIds() ) {
            try {
                Actor actor = planService.find( Actor.class, id );
                actors.add( new ActorData( serverUrl, actor, communityService ) );
                added.add( id );
                for ( ModelEntity category : actor.getAllTypes() ) {
                    if ( !added.contains( category.getId() ) ) {
                        actors.add( new ActorData( serverUrl, (Actor) category, communityService ) );
                        added.add( category.getId() );
                    }
                }
            } catch ( NotFoundException e ) {
                LOG.warn( "Actor not found at " + id ); // todo - this can happen when participation set from dev env and data collected on prod.
            }
        }

    }

    private void initOrgs( String serverUrl, CommunityService communityService ) throws NotFoundException {
        PlanService planService = communityService.getPlanService();
        orgs = new ArrayList<OrganizationData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allOrganizationIds() ) {
            try {
                Organization org = planService.find( Organization.class, id );
                orgs.add( new OrganizationData( serverUrl, org, communityService ) );
                added.add( id );
                for ( ModelEntity category : org.getAllTypes() ) {
                    if ( !added.contains( category.getId() ) ) {
                        orgs.add( new OrganizationData( serverUrl, (Organization) category, communityService ) );
                        added.add( category.getId() );
                    }
                }
                for ( Organization parent : org.ancestors() ) {
                    if ( !added.contains( parent.getId() ) ) {
                        orgs.add( new OrganizationData( serverUrl, parent, communityService ) );
                        added.add( parent.getId() );
                    }
                }
            } catch ( NotFoundException e ) {
                LOG.warn( "Organization not found at " + id ); // todo - this can happen when participation set from dev env and data collected on prod.
            }
        }

    }

    private void initPhases( String serverUrl, CommunityService communityService ) throws NotFoundException {
        PlanService planService = communityService.getPlanService();
        phases = new ArrayList<PhaseData>();
        Set<Long> added = new HashSet<Long>();
        for ( Long id : allPhaseIds() ) {
            Phase phase = planService.find( Phase.class, id );
            phases.add( new PhaseData( serverUrl, phase, communityService ) );
            added.add( id );
            for ( ModelEntity category : phase.getAllTypes() ) {
                if ( !added.contains( category.getId() ) ) {
                    phases.add( new PhaseData( serverUrl, (Phase) category, communityService ) );
                    added.add( category.getId() );
                }
            }
        }

    }

    private void initEvents( String serverUrl, CommunityService communityService ) throws NotFoundException {
        PlanService planService = communityService.getPlanService();
        events = new ArrayList<EventData>();
        for ( Long eventId : allEventIds() ) {
            Event event = planService.find( Event.class, eventId );
            events.add( new EventData( serverUrl, event, communityService ) );
        }
    }

    @XmlElement(name = "event")
    public List<EventData> getEvents() {
        return events;
    }

    @XmlElement(name = "phase")
    public List<PhaseData> getPhases() {
        return phases;
    }

    @XmlElement(name = "organization")
    public List<OrganizationData> getOrganizations() {
        return orgs;
    }

    @XmlElement(name = "actor")
    public List<ActorData> getActors() {
        return actors;
    }

    @XmlElement(name = "role")
    public List<RoleData> getRoles() throws NotFoundException {
        return roles;
    }

    @XmlElement(name = "place")
    public List<PlaceData> getPlaces() {
        return places;
    }

    @XmlElement(name = "transmissionMedium")
    public List<MediumData> getMedia() throws NotFoundException {
        return media;
    }

    @XmlElement(name = "infoProduct")
    public List<InfoProductData> getInfoProducts() throws NotFoundException {
        return infoProducts;
    }

    @XmlElement(name = "format")
    public List<InfoFormatData> getFormats() throws NotFoundException {
        return infoFormats;
    }

    @XmlElement(name = "function")
    public List<FunctionData> getFunctions() throws NotFoundException {
        return functions;
    }

    private Set<Long> allEventIds() {
        Set<Long> allIds = new HashSet<Long>();
            allIds.addAll( protocols.allEventsIds() );
        return allIds;
    }

    private Set<Long> allPhaseIds() {
        Set<Long> allIds = new HashSet<Long>();
            allIds.addAll( protocols.allPhaseIds() );
        return allIds;
    }

    private Set<Long> allOrganizationIds() {
        Set<Long> allIds = new HashSet<Long>();
            allIds.addAll( protocols.allOrganizationIds() );
        return allIds;
    }

    private Set<Long> allActorIds() {
        Set<Long> allIds = new HashSet<Long>();
            allIds.addAll( protocols.allActorIds() );
        return allIds;
    }

    private Set<Long> allRoleIds() {
        Set<Long> allIds = new HashSet<Long>();
            allIds.addAll( protocols.allRoleIds() );
        return allIds;
    }

    private Set<Long> allPlaceIds() {
        Set<Long> allIds = new HashSet<Long>();
            allIds.addAll( protocols.allPlaceIds() );
        return allIds;
    }

    private Set<Long> allMediumIds() {
        Set<Long> allIds = new HashSet<Long>();
            allIds.addAll( protocols.allMediumIds() );
        return allIds;
    }

    private Set<Long> allInfoProductIds() {
        Set<Long> allIds = new HashSet<Long>();
            allIds.addAll( protocols.allInfoProductIds() );
        return allIds;
    }

    private Set<Long> allInfoFormatIds() {
        Set<Long> allIds = new HashSet<Long>();
            allIds.addAll( protocols.allInfoFormatIds() );
        return allIds;
    }

    private Set<Long> allFunctionIds() {
        Set<Long> allIds = new HashSet<Long>();
        allIds.addAll( protocols.allFunctionIds() );
        return allIds;
    }
}
