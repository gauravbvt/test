package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Plan index panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 1, 2009
 * Time: 11:41:35 AM
 */
public class PlanIndexPanel extends AbstractIndexPanel {

    /**
     * Indexing choices.
     */
    private static final String[] indexingChoices =
            {ALL, ACTORS, EOIS, EVENTS, FLOWS, MEDIA, PHASES, PLACES, ORGANIZATIONS, ROLES, SEGMENTS, TASKS};


    public PlanIndexPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    protected List<String> getIndexingChoices() {
        return Arrays.asList( indexingChoices );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Actor> findIndexedActors() {
        return getQueryService().listReferencedEntities( Actor.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Event> findIndexedEvents() {
        return getQueryService().listReferencedEntities( Event.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Organization> findIndexedOrganizations() {
        return getQueryService().listReferencedEntities( Organization.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Phase> findIndexedPhases() {
        return getQueryService().listReferencedEntities( Phase.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<TransmissionMedium> findIndexedMedia() {
        return getQueryService().listReferencedEntities( TransmissionMedium.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Place> findIndexedPlaces() {
        return getQueryService().listReferencedEntities( Place.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Role> findIndexedRoles() {
        return getQueryService().listReferencedEntities( Role.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Flow> findIndexedFlows() {
        return getQueryService().findAllFlows();
    }

    /**
     * {@inheritDoc}
     */
    protected List<ElementOfInformationInFlow> findIndexedEOIs() {
        List<ElementOfInformationInFlow> eois = new ArrayList<ElementOfInformationInFlow>();
        for ( Flow flow : findIndexedFlows() ) {
            for ( ElementOfInformation eoi : flow.getEois() ) {
                eois.add( new ElementOfInformationInFlow( flow, eoi ) );
            }
        }
        return eois;
    }

    /**
     * {@inheritDoc}
     */
    protected List<Part> findIndexedParts() {
        return getQueryService().findAllParts();
    }

    /**
     * {@inheritDoc}
     */
    protected List<Segment> findIndexedSegments() {
        return getQueryService().list( Segment.class );
    }


}
