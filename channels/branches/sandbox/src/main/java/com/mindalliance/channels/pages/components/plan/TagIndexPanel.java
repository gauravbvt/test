package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Tag;
import com.mindalliance.channels.model.Taggable;
import com.mindalliance.channels.model.TransmissionMedium;
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
            {ALL, ACTORS, EVENTS, FLOWS, MEDIA, PHASES, PLACES, ORGANIZATIONS, ROLES, SEGMENTS, TASKS};

    private IModel<Tag> tagModel;

    public TagIndexPanel( String id, IModel<Tag> tagModel ) {
        super( id, null );
        this.tagModel = tagModel;
        doInit();
    }

    protected void init() {
        // do nothing
    }

    private void doInit() {
        super.init();
    }

    private Tag getTag() {
        return tagModel.getObject();
    }

    /**
     * {@inheritDoc}
     */
    protected List<String> getIndexingChoices() {
        return Arrays.asList( indexingChoices );
    }

    private <T extends Taggable> List<T> selectTagged( List<T> taggables ) {
        List<T> tagged = new ArrayList<T>();
        if ( getTag() != null ) {
            for ( T taggable : taggables ) {
                if ( taggable.isTaggedWith( getTag() ) ) {
                    tagged.add( taggable );
                }
            }
        }
        return tagged;
    }

    /**
     * {@inheritDoc}
     */
    protected List<Actor> findIndexedActors() {
        return selectTagged(
                getQueryService().listReferencedEntities( Actor.class ) );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Event> findIndexedEvents() {
        return selectTagged( getQueryService().listReferencedEntities( Event.class ) );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Organization> findIndexedOrganizations() {
        return selectTagged( getQueryService().listReferencedEntities( Organization.class ) );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Phase> findIndexedPhases() {
        return selectTagged( getQueryService().listReferencedEntities( Phase.class ) );
    }

    /**
     * {@inheritDoc}
     */
    protected List<TransmissionMedium> findIndexedMedia() {
        return selectTagged( getQueryService().listReferencedEntities( TransmissionMedium.class ) );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Place> findIndexedPlaces() {
        return selectTagged( getQueryService().listReferencedEntities( Place.class ) );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Role> findIndexedRoles() {
        return selectTagged( getQueryService().listReferencedEntities( Role.class ) );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Flow> findIndexedFlows() {
        return selectTagged( getQueryService().findAllFlows() );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Part> findIndexedParts() {
        return selectTagged( getQueryService().findAllParts() );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Segment> findIndexedSegments() {
        return selectTagged( getQueryService().list( Segment.class ) );
    }


}
