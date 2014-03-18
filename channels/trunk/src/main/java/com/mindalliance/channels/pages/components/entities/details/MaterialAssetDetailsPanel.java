package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.model.AbstractIdentifiable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/11/14
 * Time: 7:40 PM
 */
public class MaterialAssetDetailsPanel extends EntityDetailsPanel implements Guidable {

    private WebMarkupContainer moDetailsDiv;
    private MaterialAsset newDependency;
    private AssetDependenciesPanel assetDependenciesPanel;
    private Component assetFieldsPanel;
    private WebMarkupContainer newDependencyContainer;

    public MaterialAssetDetailsPanel( String id, IModel<? extends ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    @Override
    public String getHelpSectionId() {
        return "profiling";
    }

    @Override
    public String getHelpTopicId() {
        return "profiling-asset";
    }

    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addAssetDependenciesPanel();
        addAssetFieldsPanel();
    }

    private void addAssetDependenciesPanel() {
        assetDependenciesPanel = new AssetDependenciesPanel( "dependencies" );
        moDetailsDiv.addOrReplace( assetDependenciesPanel );
    }

    private void addAssetFieldsPanel() {
        if ( getMaterialAsset().isType() ) {
            assetFieldsPanel = new MaterialAssetFieldDefinitionsPanel(
                    "properties",
                    new PropertyModel<MaterialAsset>( this, "materialAsset" )
            );
        } else {
            assetFieldsPanel = new MaterialAssetFieldsPanel(
                    "properties",
                    new PropertyModel<MaterialAsset>( this, "materialAsset" )
            );
        }
        moDetailsDiv.addOrReplace( assetFieldsPanel );
    }

    public MaterialAsset getMaterialAsset() {
        return (MaterialAsset)getModel().getObject();
    }


    public class NewDependencyWrapper extends AbstractIdentifiable {

        public NewDependencyWrapper(  ) {
        }

        public MaterialAsset getAsset() {
            return newDependency;
        }

        public void setAsset( MaterialAsset asset ) {
            newDependency = asset;
        }
    }

    private class AssetDependenciesPanel extends AbstractCommandablePanel {

        private WebMarkupContainer dependenciesContainer;

        private AssetDependenciesPanel( String id ) {
            super( id );
            initPanel();
        }

        private void initPanel() {
            reset();
            addDependenciesContainer();
        }

        private void reset() {
            newDependency = null;
        }

        private void addDependenciesContainer() {
            dependenciesContainer = new WebMarkupContainer( "dependenciesContainer" );
            dependenciesContainer.setOutputMarkupId( true );
            addOrReplace( dependenciesContainer );
            addDependenciesList();
            addNewDependency();
        }

        private void addDependenciesList() {
            ListView<MaterialAsset> dependenciesListView = new ListView<MaterialAsset>(
                    "dependencies",
                    getMaterialAsset().getDependencies()
            ) {
                @Override
                protected void populateItem( ListItem<MaterialAsset> item ) {
                    final MaterialAsset dependency = item.getModelObject();
                    // link
                    ModelObjectLink assetLink = new ModelObjectLink(
                            "dependency",
                            new Model<MaterialAsset>( dependency ),
                            new Model<String>((dependency.isType() ? "Any kind of " : "" )  + dependency.getName() ) );
                    item.add( assetLink );
                    // delete
                    AjaxLink<String> deleteLink = new AjaxLink<String>( "delete" ) {
                        @Override
                        public void onClick( AjaxRequestTarget target ) {
                            doCommand( new UpdateModelObject(
                                    getUsername(),
                                    getMaterialAsset(),
                                    "dependencies",
                                    dependency,
                                    UpdateObject.Action.Remove
                            ) );
                            refresh( target );
                            update( target, new Change( Change.Type.Updated, getMaterialAsset() ) );
                        }
                    };
                    makeVisible( deleteLink, isLockedByUser( getMaterialAsset() ) );
                    item.add( deleteLink );
                }
            };
            dependenciesContainer.add( dependenciesListView );
        }

        private void addNewDependency() {
            newDependencyContainer = new WebMarkupContainer( "newDependencyContainer" );
            newDependencyContainer.setOutputMarkupId( true );
            makeVisible( newDependencyContainer, isLockedByUser( getMaterialAsset() ) );
            dependenciesContainer.add( newDependencyContainer );
            // new dependent asset
            EntityReferencePanel<MaterialAsset> newDependencyPanel = new EntityReferencePanel<MaterialAsset>(
                    "newDependency",
                    new Model<Identifiable>( new NewDependencyWrapper() ),
                    getCandidateDependencies(),
                    "asset",
                    MaterialAsset.class
            );
            newDependencyContainer.add( newDependencyPanel );
        }

        private List<String> getCandidateDependencies() {
            List<String> candidates = new ArrayList<String>(  );
            List<MaterialAsset> dependencies = getMaterialAsset().getDependencies();
            for ( final MaterialAsset asset : getQueryService().list( MaterialAsset.class ) ) {
                if (!CollectionUtils.exists(
                        dependencies,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return asset.narrowsOrEquals( ((MaterialAsset)object) );
                            }
                        }
                ) ) {
                    candidates.add( asset.getName() );
                }
            }
            Collections.sort( candidates );
            return candidates;
        }

        /*
         *
         *     @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUpdated() && change.isForProperty( "types" ) ) {
            addTypesPanel();
            target.add( typesPanel );
            typesChanged( target );
        }

         */

        @Override
        public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
            if ( change.isForProperty( "asset" ) ) {
                doCommand(
                        new UpdateModelObject(
                                getUsername(),
                                getMaterialAsset(),
                                "dependencies",
                                newDependency,
                                UpdateObject.Action.AddUnique
                        )
                );
                refresh( target );
            } else if ( change.isUpdated() && change.isForProperty( "types" ) ) {
                addAssetFieldsPanel();
                target.add( assetFieldsPanel );
                super.updateWith( target, change, updated );
            }
            super.updateWith( target, change, updated );
        }

        private void refresh( AjaxRequestTarget target ) {
            reset();
            addDependenciesContainer();
            target.add( dependenciesContainer );
            update( target, new Change( Change.Type.Updated, getMaterialAsset() ) );
        }

    }
}