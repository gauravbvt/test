package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.model.AbstractIdentifiable;
import com.mindalliance.channels.core.model.asset.AssetField;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/13/14
 * Time: 12:52 PM
 */
public class MaterialAssetFieldsPanel extends AbstractCommandablePanel {


    public MaterialAssetFieldsPanel( String id, IModel<MaterialAsset> assetModel ) {
        super( id, assetModel);
        init();
    }

    private void init() {
        WebMarkupContainer groupsContainer = new WebMarkupContainer( "groupsContainer" );
        groupsContainer.setOutputMarkupId( true );
        addOrReplace( groupsContainer );
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
    }

    private void addListViewOfFieldsInGroup( String group, ListItem<String> item ) {
        ListView<AssetFieldWrapper> fieldsListView = new ListView<AssetFieldWrapper>(
                "fields",
                getAssetFieldWrappers( group )
        ) {
            @Override
            protected void populateItem( ListItem<AssetFieldWrapper> item ) {
                final AssetFieldWrapper wrapper = item.getModelObject();
                // name
                Label nameLabel = new Label( "name", wrapper.getName() );
                item.add( nameLabel );
                // value
               TextField<String> valueField = new TextField<String>(
                       "value",
                       new PropertyModel<String>( wrapper, "value" )
               );
                valueField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        update( target, new Change( Change.Type.Updated, getMaterialAsset(), "fields") );
                    }
                });
                if ( !wrapper.getDescription().isEmpty() )
                    addInputHint( valueField, wrapper.getDescription() );
                if ( wrapper.isRequired() )
                    valueField.add( new AttributeModifier( "class", "required" ) );
                item.add( valueField );
            }
        };
        item.add( fieldsListView );
    }

    private List<AssetFieldWrapper> getAssetFieldWrappers( String group ) {
        List<AssetFieldWrapper> wrappers = new ArrayList<AssetFieldWrapper>(  );
        List<AssetField> fields = getMaterialAsset().getFieldsInGroup( group );
        for ( int i=0; i< fields.size(); i++ ) {
            AssetFieldWrapper wrapper = new AssetFieldWrapper( fields.get(i), i );
            wrappers.add( wrapper );
        }
        return wrappers;
    }

    public class AssetFieldWrapper extends AbstractIdentifiable {

        private AssetField assetField;
        private int index;

        public AssetFieldWrapper( AssetField assetField, int index ) {
            this.assetField = assetField;
            this.index = index;
        }

        public String getName() {
            return assetField.getName();
        }

        public String getDescription() {
            return assetField.getDescription();
        }

        public boolean isRequired() {
            return assetField.isRequired();
        }

        public String getValue() {
            return assetField.getValue();
        }

        public void setValue( String val ) {
            doCommand(
                   new UpdateModelObject(
                           getUsername(),
                           getMaterialAsset(),
                           "fields["+index+"].value",
                           val,
                           UpdateObject.Action.Set
                   )
            );
        }
    }

    public MaterialAsset getMaterialAsset() {
        return (MaterialAsset) getModel().getObject();
    }

}
