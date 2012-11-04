package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Modelable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;

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
            {ALL, ACTORS, EOIS, EVENTS, FLOWS, INFO_FORMATS, INFO_PRODUCTS, MEDIA, PHASES,
                    PLACES, ORGANIZATIONS, ROLES, REQUIREMENTS, SEGMENTS, TASKS};


    public PlanIndexPanel( String id,  Set<Long> expansions ) {
        super( id, null, expansions );
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

    @Override
    protected List<InfoProduct> findIndexedInfoProducts() {
        return getQueryService().listReferencedEntities( InfoProduct.class );
    }

    @Override
    protected List<InfoFormat> findIndexedInfoFormats() {
        return getQueryService().listReferencedEntities( InfoFormat.class );
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
    protected List<Modelable> findIndexedEOIs() {
        List<Modelable> eois = new ArrayList<Modelable>();
        for ( Flow flow : findIndexedFlows() ) {
            for ( ElementOfInformation eoi : flow.getEffectiveEois() ) {
                eois.add( new ElementOfInformationInFlow( flow, eoi ) );
            }
        }
        for ( InfoProduct infoProduct : getQueryService().list( InfoProduct.class )) {
            for ( ElementOfInformation eoi: infoProduct.getEffectiveEois() ) {
                eois.add( new ElementOfInformationInInfoProduct( infoProduct, eoi ) );
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

    @Override
    protected List<Requirement> findIndexedRequirements() {
        return getQueryService().list( Requirement.class );
    }


}
