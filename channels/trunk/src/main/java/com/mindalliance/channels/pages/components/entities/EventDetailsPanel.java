package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.util.SemMatch;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
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
public class EventDetailsPanel extends EntityDetailsPanel {

    public EventDetailsPanel( String id, IModel<? extends ModelObject> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        moDetailsDiv.add(
                new ModelObjectLink( "scope-link",
                        new PropertyModel<Organization>( getPlanEvent(), "scope" ),
                        new Model<String>( "Location" ) ) );
        final List<String> choices = getQueryService().findAllNames( Place.class );
        TextField scopeField = new AutoCompleteTextField<String>( "scope",
                new PropertyModel<String>( this, "scopeName" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( SemMatch.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        scopeField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPlanEvent(), "scope" ) );
            }
        } );
        moDetailsDiv.add( scopeField );
    }

    /**
     * Set organization's location from name, if not null or empty.
     *
     * @param name a String
     */
    public void setScopeName( String name ) {
        Event event = getPlanEvent();
        Place oldPlace = event.getScope();
        String oldName = oldPlace == null ? "" : oldPlace.getName();
        Place newPlace = null;
        if ( name == null || name.trim().isEmpty() )
            newPlace = null;
        else {
            if ( oldPlace == null || !isSame( name, oldName ) )
                newPlace = getQueryService().findOrCreate( Place.class, name );
        }
        doCommand( new UpdatePlanObject( event, "scope", newPlace ) );
        getCommander().cleanup( Place.class, oldName );
    }

    /**
     * Get organization's location's name.
     *
     * @return a String
     */
    public String getScopeName() {
        Place scope = ( getPlanEvent() ).getScope();
        return scope == null ? "" : scope.getName();
    }

    private Event getPlanEvent() {
        return (Event) getEntity();
    }


}
