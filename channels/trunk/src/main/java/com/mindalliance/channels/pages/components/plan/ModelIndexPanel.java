package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Function;
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
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;
import com.mindalliance.channels.pages.components.guide.Guidable;

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
public class ModelIndexPanel extends AbstractIndexPanel implements Guidable {

    private static final boolean EXCLUDE_IMMUTABLE = false;

    /**
     * Indexing choices.
     */
    private static final String[] indexingChoices =
            {ALL, ACTORS, ASSETS, EOIS, EVENTS, FUNCTIONS, FLOWS, INFO_FORMATS, INFO_PRODUCTS, MEDIA, PHASES,
                    PLACES, ORGANIZATIONS, ROLES, SEGMENTS, TASKS};


    public ModelIndexPanel( String id, Set<Long> expansions ) {
        super( id, null, expansions );
    }

    @Override
    public String getHelpSectionId() {
        return "searching";
    }

    @Override
    public String getHelpTopicId() {
        return "search-by-name";
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
        return getQueryService().listKnownEntities( Actor.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Event> findIndexedEvents() {
        return getQueryService().listKnownEntities( Event.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Organization> findIndexedOrganizations() {
        return getQueryService().listKnownEntities( Organization.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Phase> findIndexedPhases() {
        return getQueryService().listKnownEntities( Phase.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE );
    }

    /**
     * {@inheritDoc}
     */
    protected List<TransmissionMedium> findIndexedMedia() {
        return getQueryService().listKnownEntities( TransmissionMedium.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Place> findIndexedPlaces() {
        return getQueryService().listKnownEntities( Place.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Role> findIndexedRoles() {
        return getQueryService().listKnownEntities( Role.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE );
    }

    @Override
    protected List<InfoProduct> findIndexedInfoProducts() {
        return getQueryService().listKnownEntities( InfoProduct.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE );
    }

    @Override
    protected List<InfoFormat> findIndexedInfoFormats() {
        return getQueryService().listKnownEntities( InfoFormat.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE );
    }

    @Override
    protected List<Function> findIndexedFunctions() {
        return getQueryService().listKnownEntities( Function.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE );
    }

    @Override
    protected List<MaterialAsset> findIndexedMaterialAssets() {
        return getQueryService().listKnownEntities( MaterialAsset.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE );
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
