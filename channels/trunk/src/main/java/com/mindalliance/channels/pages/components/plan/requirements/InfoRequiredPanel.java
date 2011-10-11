package com.mindalliance.channels.pages.components.plan.requirements;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Taggable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.TagsPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Info required panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/3/11
 * Time: 3:19 PM
 */
public class InfoRequiredPanel extends AbstractCommandablePanel {
    private TextField<String> infoField;
    private WebMarkupContainer eoisContainer;

    public InfoRequiredPanel( String id, IModel<Requirement> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        addInfo();
        addInfoTags();
        addEois();
        adjustFields();
    }

    private void adjustFields() {
        Requirement requirement = getRequirement();
        infoField.setEnabled( isLockedByUser( requirement ) );
    }

    private void addInfo() {
        infoField = new TextField<String>(
                "information",
                new PropertyModel<String>( this, "information" ) );
        infoField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getRequirement(), "info" ) );
            }
        } );
        add( infoField );
    }

    private void addInfoTags() {
        AjaxFallbackLink tagsLink = new AjaxFallbackLink( "tagsLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getPlan(), PlanEditPanel.TAGS ) );
            }
        };
        tagsLink.add( new AttributeModifier( "class", true, new Model<String>( "model-object-link" ) ) );
        add( tagsLink );
        TagsPanel tagsPanel = new TagsPanel( "infoTags", new Model<Taggable>( getRequirement() ), "infoTags" );
        add( tagsPanel );
    }

    private void addEois() {
        final Requirement requirement = getRequirement();
        // Container
        eoisContainer = new WebMarkupContainer( "eoisContainer" );
        eoisContainer.setOutputMarkupId( true );
        makeVisible( eoisContainer, !getInformation().isEmpty() );
        addOrReplace( eoisContainer );
        // Eois
        ListView<String> eoisListView = new ListView<String>(
                "eois",
                getRequirement().getEois()
        ) {
            @Override
            protected void populateItem( final ListItem<String> item ) {
                item.add( new Label( "eoiName", item.getModelObject() ) );
                WebMarkupContainer deleteImage = new WebMarkupContainer( "deleteEoi" );
                deleteImage.add( new AjaxEventBehavior( "onclick" ) {
                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        deleteEoi( item.getModelObject() );
                        addEois();
                        target.addComponent( eoisContainer );
                        update( target, new Change( Change.Type.Updated, requirement, "eois" ) );
                    }
                } );
                makeVisible( deleteImage, isLockedByUser( requirement ) );
                item.add( deleteImage );
            }
        };
        eoisContainer.add( eoisListView );
        // New eoi
        final List<String> choices = getQueryService().findAllEoiNames();
        TextField<String> newEoiField = new AutoCompleteTextField<String>(
                "addEoi",
                new PropertyModel<String>( this, "newEoiName"  ) ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( getQueryService().likelyRelated( input, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        newEoiField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addEois();
                target.addComponent( eoisContainer );
                update( target, new Change( Change.Type.Updated, requirement, "eois" ) );
            }
        } );
        makeVisible( newEoiField, isLockedByUser( requirement ) );
        eoisContainer.add( newEoiField );
    }


    public String getInformation() {
        return getRequirement().getInformation();
    }

    public void setInformation( String val ) {
        String info = val == null ? "" : val.trim();
        doCommand( new UpdatePlanObject( getUsername(), getRequirement(), "information", info ) );
    }

    public String getNewEoiName() {
        return "";
    }

    public void setNewEoiName( String name ) {
        if ( name != null && !name.trim().isEmpty() ) {
            doCommand( new UpdatePlanObject(
                    getUsername(),
                    getRequirement(),
                    "eois",
                    name.trim(),
                    UpdateObject.Action.Add ) );
        }
    }

    public void deleteEoi( String eoi ) {
        doCommand( new UpdatePlanObject(
                getUsername(),
                getRequirement(),
                "eois",
                eoi,
                UpdateObject.Action.Remove ) );
    }

    private Requirement getRequirement() {
        return (Requirement) getModel().getObject();
    }

}
