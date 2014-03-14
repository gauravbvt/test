package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.model.asset.AssetField;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/13/14
 * Time: 12:51 PM
 */
public class MaterialAssetFieldDefinitionsPanel extends AbstractCommandablePanel {

    private WebMarkupContainer groupsContainer;
    private WebMarkupContainer newFieldContainer;
    private AssetField newAssetField;

    public MaterialAssetFieldDefinitionsPanel( String id, IModel<MaterialAsset> assetModel ) {
        super( id, assetModel );
        init();
    }

    private void init() {
        reset();
        addFieldGroups();
        addNewField();
    }

    private void reset() {
        newAssetField = new AssetField();
    }

    private void addFieldGroups() {
        groupsContainer = new WebMarkupContainer( "groupsContainer" );
        groupsContainer.setOutputMarkupId( true );
        ListView<String> assetFieldListView = new ListView<String>(
                "fieldGroups",
                getMaterialAsset().getGroups()
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                String group = item.getModelObject();
                item.add( new Label( "groupName", group ) );
                addListViewOfFieldsInGroup( group, item );
            }
        };
        groupsContainer.add( assetFieldListView );
        addOrReplace( groupsContainer );
    }

    private void addListViewOfFieldsInGroup( String group, ListItem<String> item ) {
        ListView<AssetField> fieldsListView = new ListView<AssetField>(
                "fields",
                getMaterialAsset().getFieldsInGroup( group )
        ) {
            @Override
            protected void populateItem( ListItem<AssetField> item ) {
                final AssetField field = item.getModelObject();
                // name
                Label nameLabel = new Label( "name", field.getName() );
                if ( !field.getDescription().isEmpty() )
                    addTipTitle( nameLabel, field.getDescription() );
                if ( field.isRequired() )
                    nameLabel.add( new AttributeModifier( "class", "required" ) );
                item.add( nameLabel );
                // delete
                AjaxLink<String> deleteLink = new AjaxLink<String>( "delete" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        doCommand(
                                new UpdateModelObject(
                                        getUsername(),
                                        getMaterialAsset(),
                                        "fields",
                                        field,
                                        UpdateObject.Action.Remove
                                )
                        );
                        addFieldGroups();
                        target.add( groupsContainer );
                        update( target, new Change( Change.Type.Updated, getMaterialAsset(), "fields") );
                    }
                };
                makeVisible( deleteLink, isLockedByUser( getMaterialAsset() ) );
                item.add( deleteLink );
            }
        };
        item.add( fieldsListView );
    }

    private void addNewField() {
        newFieldContainer = new WebMarkupContainer(  "newFieldContainer" );
        newFieldContainer.setOutputMarkupId( true );
        makeVisible( newFieldContainer, isLockedByUser( getMaterialAsset() ) );
        addOrReplace( newFieldContainer );
        addRequiredInput();
        addNameInput();
        addGroupInput();
        addDescriptionInput();
        addAddButton();
    }

    private void addRequiredInput() {
        AjaxCheckBox requiredCheckBox = new AjaxCheckBox(
                "required",
                new PropertyModel<Boolean>( this, "fieldRequired" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // Do nothing
            }
        };
        addTipTitle( requiredCheckBox, "whether a value will be required for this property in actual assets categorized by this one." );
        newFieldContainer.add( requiredCheckBox );
    }

    private void addNameInput() {
        TextField<String> nameField = new TextField<String>(
                "name",
                new PropertyModel<String>( this, "fieldName" )
        );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // Do nothing
            }
        });
        addInputHint( nameField, "Enter a unique name" );
        newFieldContainer.add( nameField );
    }

    private void addGroupInput() {
        final List<String> choices = new ArrayList<String>( getMaterialAsset().getGroups() );
        Collections.sort( choices );
        AutoCompleteTextField<String> groupField = new AutoCompleteTextField<String>(
                "group",
                new PropertyModel<String>( this, "fieldGroup"  )
        ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                for ( String candidate : choices ) {
                        if ( getQueryService().likelyRelated( input, candidate ) )
                            candidates.add( candidate );
                    }
                    Collections.sort( candidates );
                return candidates.iterator();
            }
        };
        groupField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // Do nothing
            }
        });
        addInputHint( groupField, "Name the group" );
         newFieldContainer.add( groupField );
    }



    private void addDescriptionInput() {
        TextField<String> descriptionField = new TextField<String>(
                "description",
                new PropertyModel<String>( this, "fieldDescription" )
        );
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // Do nothing
            }
        } );
        addInputHint( descriptionField, "Enter a brief hint for the property" );
        newFieldContainer.add( descriptionField );
    }

    private void addAddButton() {
        AjaxLink<String> addLink = new AjaxLink<String>( "add" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( canAddNewField() ) {
                    doCommand(
                            new UpdateModelObject(
                                    getUsername(),
                                    getMaterialAsset(),
                                    "fields",
                                    newAssetField,
                                    UpdateObject.Action.AddUnique
                            )
                    );
                    reset();
                    addFieldGroups();
                    target.add( groupsContainer );
                    addNewField();
                    target.add( newFieldContainer );
                    update( target, new Change( Change.Type.Updated, getMaterialAsset(), "fields"));
                } else {
                    Change change = Change.message( "Please the new field a unique name." );
                    update( target, change );
                }
            }
        };
        newFieldContainer.add( addLink );
    }

    private boolean canAddNewField() {
        return newAssetField.getName() != null && !newAssetField.getName().isEmpty();
    }

    public boolean isFieldRequired() {
        return newAssetField.isRequired();
    }

    public void setFieldRequired( boolean val ) {
        newAssetField.setRequired( val );
    }

    public String getFieldName() {
        return newAssetField.getName();
    }

    public void setFieldName( String val ) {
        newAssetField.setName( val );
    }

    public String getFieldGroup() {
        return newAssetField.getGroup();
    }

    public void setFieldGroup( String val ) {
        newAssetField.setGroup( val );
    }

    public String getFieldDescription() {
        return newAssetField.getDescription();
    }

    public void setFieldDescription( String val ) {
        newAssetField.setDescription( val );
    }



    public MaterialAsset getMaterialAsset() {
        return (MaterialAsset) getModel().getObject();
    }
}
