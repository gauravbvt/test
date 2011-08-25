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
import com.mindalliance.channels.nlp.Matcher;
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
        Matcher instance = Matcher.getInstance();
        if ( getTag() != null ) {
            for ( T taggable : taggables ) {
                if ( isTaggedWith( instance, taggable, getTag() ) ) {
                    tagged.add( taggable );
                }
            }
        }
        return tagged;
    }

    @Override
    protected List<Actor> findIndexedActors() {
        return selectTagged(
                getQueryService().listReferencedEntities( Actor.class ) );
    }

    @Override
    protected List<Event> findIndexedEvents() {
        return selectTagged( getQueryService().listReferencedEntities( Event.class ) );
    }

    @Override
    protected List<Organization> findIndexedOrganizations() {
        return selectTagged( getQueryService().listReferencedEntities( Organization.class ) );
    }

    @Override
    protected List<Phase> findIndexedPhases() {
        return selectTagged( getQueryService().listReferencedEntities( Phase.class ) );
    }

    @Override
    protected List<TransmissionMedium> findIndexedMedia() {
        return selectTagged( getQueryService().listReferencedEntities( TransmissionMedium.class ) );
    }

    @Override
    protected List<Place> findIndexedPlaces() {
        return selectTagged( getQueryService().listReferencedEntities( Place.class ) );
    }

    @Override
    protected List<Role> findIndexedRoles() {
        return selectTagged( getQueryService().listReferencedEntities( Role.class ) );
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
