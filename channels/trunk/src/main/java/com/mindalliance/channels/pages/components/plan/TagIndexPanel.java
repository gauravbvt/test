package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Tag;
import com.mindalliance.channels.core.model.Taggable;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/1/11
 * Time: 9:23 PM
 */
public class TagIndexPanel extends AbstractIndexPanel {

    /**
     * Indexing choices.
     */
    private static final String[] indexingChoices =
            {ALL, ACTORS, ASSETS, EVENTS, INFO_FORMATS, INFO_PRODUCTS, FUNCTIONS, FLOWS, MEDIA, PHASES, PLACES, ORGANIZATIONS, ROLES, SEGMENTS, TASKS};

    private IModel<Tag> tagModel;
    private static final boolean EXCLUDE_IMMUTABLE = false;

    public TagIndexPanel( String id, IModel<Tag> tagModel ) {
        super( id, null );
        this.tagModel = tagModel;
        doInit();
    }

    @Override
    protected void init() {
        // do nothing
    }

    private void doInit() {
        super.init();
    }

    private Tag getTag() {
        return tagModel.getObject();
    }

    @Override
    protected List<String> getIndexingChoices() {
        return Arrays.asList( indexingChoices );
    }

    private <T extends Taggable> List<T> selectTagged( List<T> taggables ) {
        List<T> tagged = new ArrayList<T>();
        if ( getTag() != null ) {
            for ( T taggable : taggables ) {
                if ( isTaggedWith( taggable, getTag() ) ) {
                    tagged.add( taggable );
                }
            }
        }
        return tagged;
    }

    @Override
    protected List<Actor> findIndexedActors() {
        return selectTagged(
                getQueryService().listKnownEntities( Actor.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE ) );
    }

    @Override
    protected List<Event> findIndexedEvents() {
        return selectTagged( getQueryService().listKnownEntities( Event.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE ) );
    }

    @Override
    protected List<Organization> findIndexedOrganizations() {
        return selectTagged( getQueryService().listKnownEntities( Organization.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE ) );
    }

    @Override
    protected List<Phase> findIndexedPhases() {
        return selectTagged( getQueryService().listKnownEntities( Phase.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE ) );
    }

    @Override
    protected List<TransmissionMedium> findIndexedMedia() {
        return selectTagged( getQueryService().listKnownEntities( TransmissionMedium.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE ) );
    }

    @Override
    protected List<Place> findIndexedPlaces() {
        return selectTagged( getQueryService().listKnownEntities( Place.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE ) );
    }

    @Override
    protected List<Role> findIndexedRoles() {
        return selectTagged( getQueryService().listKnownEntities( Role.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE ) );
    }

    @Override
    protected List<InfoProduct> findIndexedInfoProducts() {
        return selectTagged( getQueryService().listKnownEntities( InfoProduct.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE ) );
    }

    @Override
    protected List<InfoFormat> findIndexedInfoFormats() {
        return selectTagged( getQueryService().listKnownEntities( InfoFormat.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE ) );
    }

    @Override
    protected List<Function> findIndexedFunctions() {
        return selectTagged( getQueryService().listKnownEntities( Function.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE ) );
    }

    @Override
    protected List<MaterialAsset> findIndexedMaterialAssets() {
        return selectTagged( getQueryService().listKnownEntities( MaterialAsset.class, isMustBeReferenced(), EXCLUDE_IMMUTABLE ) );
    }


    @Override
    protected List<Flow> findIndexedFlows() {
        return selectTagged( getQueryService().findAllFlows() );
    }

    @Override
    protected List<Part> findIndexedParts() {
        return selectTagged( getQueryService().findAllParts() );
    }

    @Override
    protected List<Segment> findIndexedSegments() {
        return selectTagged( getQueryService().list( Segment.class ) );
    }


}
