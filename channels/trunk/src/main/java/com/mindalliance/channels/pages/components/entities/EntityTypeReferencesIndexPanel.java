package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Entity type references index panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 15, 2009
 * Time: 4:19:24 PM
 */
public class EntityTypeReferencesIndexPanel extends AbstractIndexPanel {

    public EntityTypeReferencesIndexPanel(
            String id,
            IModel<? extends Identifiable> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    protected List<String> getIndexingChoices() {
        List<String> choices = new ArrayList<String>();
        choices.add( ALL );
        if ( !findIndexedActors().isEmpty() ) choices.add( ACTORS );
        if ( !findIndexedEvents().isEmpty() ) choices.add( EVENTS );
        if ( !findIndexedPhases().isEmpty() ) choices.add( PHASES );
        if ( !findIndexedPlaces().isEmpty() ) choices.add( PLACES );
        if ( !findIndexedOrganizations().isEmpty() ) choices.add( ORGANIZATIONS );
        if ( !findIndexedRoles().isEmpty() ) choices.add( ROLES );
        if ( !findIndexedParts().isEmpty() ) choices.add( TASKS );
        return choices;
    }

    /**
     * {@inheritDoc}
     */
    protected List<Actor> findIndexedActors() {
        return getQueryService().findAllEntitiesReferencingType( getEntity(), Actor.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Event> findIndexedEvents() {
        return getQueryService().findAllEntitiesReferencingType( getEntity(), Event.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Organization> findIndexedOrganizations() {
        return getQueryService().findAllEntitiesReferencingType( getEntity(), Organization.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Phase> findIndexedPhases() {
        return getQueryService().findAllEntitiesReferencingType( getEntity(), Phase.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Place> findIndexedPlaces() {
        return getQueryService().findAllEntitiesReferencingType( getEntity(), Place.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Role> findIndexedRoles() {
        return getQueryService().findAllEntitiesReferencingType( getEntity(), Role.class );
    }

    /**
     * {@inheritDoc}
     */
    protected List<Flow> findIndexedFlows() {
        return new ArrayList<Flow>();
    }

    /**
     * {@inheritDoc}
     */
    protected List<Part> findIndexedParts() {
        return getQueryService().findAllPartsReferencingType( getEntity() );
    }

    private ModelEntity getEntity() {
        return (ModelEntity) getModel().getObject();
    }
}
