package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
public class EventDetailsPanel extends EntityDetailsPanel implements Guidable {
    /**
     * Web markup container.
     */
    private WebMarkupContainer moDetailsDiv;
    /**
     * For scope of event (a place).
     */
    private AutoCompleteTextField<String> scopeTextField;
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

    @Override
    public String getHelpSectionId() {
        return "profiling";
    }

    @Override
    public String getHelpTopicId() {
        return "profiling-event";
    }


    /**
     * {@inheritDoc }
     */
    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addSelfTerminatingField();
        addLocationLink();
        addScopeField();
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

    private void addScopeField() {
        final List<Place> choices = getQueryService().listActualEntities( Place.class, true );

        scopeTextField = new AutoCompleteTextField<String>(
                "scope",
                new PropertyModel<String>( this, "scopeName" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                if ( choices != null ) {
                    for ( Place place : choices ) {
                        String choice = place.getName();
                        if ( getQueryService().likelyRelated( s, choice ) )
                            candidates.add( choice );
                    }
                    Collections.sort( candidates );
                }
                return candidates.iterator();
            }
        };
        scopeTextField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addLocationLink();
                target.add( locationLink );
                update( target, new Change( Change.Type.Updated, getPlanEvent(), "scope" ));
            }
        });
        scopeTextField.setEnabled( isLockedByUser( getPlanEvent() ) );
        addInputHint( scopeTextField, "Enter an actual place" );
        moDetailsDiv.add( scopeTextField );
    }

    private void adjustFields() {
        scopeTextField.setEnabled( isLockedByUser( getEvent() ) );
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
            doCommand( new UpdateModelObject( getUser().getUsername(), event, "selfTerminating", val ) );
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

    public String getScopeName() {
        Place place = getPlanEvent().getScope();
        return place == null ? "" : place.getName();
    }

    public void setScopeName( String val ) {
        Place scope;
        if ( val == null || val.isEmpty() ) {
            scope = null;
        } else {
            scope = doSafeFindOrCreateActual( Place.class, val );
        }
        Place oldScope = getPlanEvent().getScope();
        if ( !ModelObject.areEqualOrNull( oldScope, scope ) ) {
            doCommand( new UpdateModelObject(
                    getUser().getUsername(),
                    getPlanEvent(),
                    "scope",
                    scope ) );
            if ( oldScope != null )
                getCommander().cleanup( Place.class, oldScope.getName() );
        }
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
