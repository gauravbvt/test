package com.mindalliance.channels.pages.components.community;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Community details panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/12/13
 * Time: 10:15 AM
 */
public class CollaborationPlanDetailsPanel extends AbstractCommandablePanel {

    private String name;
    private String description;
    private String localeName;
    private Place namedLocale;
    private AjaxLink<String> cancelButton;
    private AjaxLink<String> acceptButton;

    public CollaborationPlanDetailsPanel( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        name = getPlanCommunity().getName();
        description = getPlanCommunity().getDescription();
        namedLocale = getPlanCommunity().getCommunityLocale() ;
        localeName = namedLocale == null
                ? null
                : namedLocale.getName();
        addUri();
        addName();
        addDescription();
        addLocale();
        addButtons();
    }

    private void addUri() {
        add( new Label( "uri", getPlan().getUri() ) );
    }

    private void addName() {
        add( new TextField<String>( "name", new PropertyModel<String>( this, "name" ) )
                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        // do nothing
                    }
                } ) );
    }

    private void addDescription() {
        add( new TextArea<String>( "description", new PropertyModel<String>( this, "description" ) )
                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        // do nothing
                    }
                } ) );
    }


    private void addButtons() {
        cancelButton = new AjaxLink<String>( "cancel" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Collapsed, getCommunity() ) );
            }
        };
        cancelButton.setOutputMarkupId( true );
        add( cancelButton );

        acceptButton = new AjaxLink<String>( "accept" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                acceptChanges();
                update( target, new Change( Change.Type.Updated, getCommunity() ) );
            }
        };
        acceptButton.setOutputMarkupId( true );
        add( acceptButton );
    }

    private void addLocale() {
        final List<Place> choices = getPlaceCandidates();
        AutoCompleteTextField<String> localeNameField = new AutoCompleteTextField<String>(
                "locale",
                new PropertyModel<String>( this, "localeName" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                if ( choices != null ) {
                    for ( Place entity : choices ) {
                        String choice = entity.getName();
                        if ( getQueryService().likelyRelated( s, choice ) )
                            candidates.add( choice );
                    }
                    Collections.sort( candidates );
                }
                return candidates.iterator();
            }
        };
        localeNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        add( localeNameField );
    }

    private List<Place> getPlaceCandidates() {
        return getCommunityService().listActualEntities( Place.class, true );
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName( String val ) {
        String oldLocaleName = localeName;
        Place oldNamedLocale = namedLocale;
        synchronized ( getCommunityService().getDao() ) {
            localeName = val;
            if ( localeName == null || localeName.isEmpty() ) {
                namedLocale = null;
            } else {
                namedLocale = getCommunityService().findOrCreate( Place.class, localeName );
                Place planLocale = getCommunityService().getPlan().getLocale();
                if ( planLocale != null ) {
                    if ( planLocale.isType() && namedLocale.isInCommunity() && namedLocale.getTypes().isEmpty() ) {
                        namedLocale.addType( planLocale );
                    }
                    if ( !namedLocale.narrowsOrEquals( planLocale, planLocale ) ) {
                        // quietly reject invalid community locale todo - inform user
                        namedLocale = oldNamedLocale;
                        this.localeName = oldLocaleName;
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    private void acceptChanges() {
        if ( hasChanged() ) {
            PlanCommunity planCommunity = getCommunity();
            MultiCommand multiCommand = new MultiCommand( getUsername(), "Update community details" );
            multiCommand.makeUndoable( false );
            multiCommand.setChange( new Change( Change.Type.Updated, getCommunity() ) );
            if ( name != null && !name.equals( planCommunity.getName() ) ) {
                multiCommand.addCommand( new UpdatePlanObject(
                        getUsername(),
                        getCommunity(),
                        "name",
                        name
                ) );
            }
            if ( description != null && !description.equals( planCommunity.getDescription()  ) ) {
                multiCommand.addCommand( new UpdatePlanObject(
                        getUsername(),
                        getCommunity(),
                        "description",
                        description
                ) );
            }
            if ( !ModelObject.areEqualOrNull( namedLocale, planCommunity.getCommunityLocale() ) ) {
                multiCommand.addCommand( new UpdatePlanObject(
                        getUsername(),
                        getCommunity(),
                        "communityLocale",
                        getNamedLocale()
                ) );
            }
            doCommand( multiCommand );
        }
    }

    private boolean hasChanged() {
        PlanCommunity planCommunity = getCommunity();
        return !( name != null && name.equals( planCommunity.getName() )
                && ( description != null && description.equals( planCommunity.getDescription() ) )
                && ModelObject.areEqualOrNull( getNamedLocale(), planCommunity.getCommunityLocale() ) );
    }

    private Place getNamedLocale() {
        return namedLocale;
    }

    private PlanCommunity getCommunity() {
        return (PlanCommunity) getModel().getObject();
    }

}
