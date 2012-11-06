package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;
import java.util.Set;

/**
 * Plan event details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 9:22:26 AM
 */
public class EventDetailsPanel extends EntityDetailsPanel {
    /**
     * Web markup container.
     */
    private WebMarkupContainer moDetailsDiv;
    /**
     * Entity reference panel for scope of event (a place).
     */
    private EntityReferencePanel<Place> scopePanel;
    /**
     * Self-terminating checkbox.
     */
    private CheckBox selfTerminatingCheckBox;
     /**
     * Location link.
     */
    private ModelObjectLink locationLink;

    public EventDetailsPanel( String id, IModel<? extends ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addSelfTerminatingField();
        addLocationLink();
        addScopePanel();
        adjustFields();
    }

    private void addSelfTerminatingField() {
        selfTerminatingCheckBox = new CheckBox(
                "selfTerminating",
                new PropertyModel<Boolean>( this, "selfTerminating" ) );
        selfTerminatingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change( Change.Type.Updated, getPlanEvent(), "selfTerminating" ) );
            }
        } );
        moDetailsDiv.add( selfTerminatingCheckBox );
    }

    private void addLocationLink() {
        locationLink = new ModelObjectLink( "scope-link",
                new PropertyModel<Organization>( getPlanEvent(), "scope" ),
                new Model<String>( "Location" ) );
        moDetailsDiv.addOrReplace( locationLink );

    }

    private void addScopePanel() {
        final List<String> choices = getQueryService().findAllEntityNames( Place.class );
        scopePanel = new EntityReferencePanel<Place>(
                "scopePanel",
                new Model<Event>( getEvent() ),
                choices,
                "scope",
                Place.class
        );
        moDetailsDiv.add( scopePanel );
    }

    private void adjustFields() {
        scopePanel.enable( isLockedByUser( getEvent() ) );
        selfTerminatingCheckBox.setEnabled( isLockedByUser( getEvent() ) );
    }

     /**
     * Set event as self-terminating.
     *
     * @param val a boolean
     */
    public void setSelfTerminating( boolean val ) {
        Event event = getPlanEvent();
        boolean oldVal = event.isSelfTerminating();
        if ( oldVal != val ) {
            doCommand( new UpdatePlanObject( getUser().getUsername(), event, "selfTerminating", val ) );
        }
    }

    /**
     * Get whether event is self-terminating.
     *
     * @return a boolean
     */
    public boolean isSelfTerminating() {
        return getPlanEvent().isSelfTerminating();
    }

    private Event getPlanEvent() {
        return (Event) getEntity();
    }


    private Event getEvent() {
        return (Event) getEntity();
    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUpdated() ) {
            String property = change.getProperty();
            if ( property.equals( "scope" ) ) {
                addLocationLink();
                target.add( locationLink );
            }
        }
        super.updateWith( target, change, updated );
    }



}
