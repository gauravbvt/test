package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.model.AbstractIdentifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.asset.AssetConnectable;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/11/14
 * Time: 1:49 PM
 */
public class ConnectedAssetsPanel extends AbstractCommandablePanel {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ConnectedAssetsPanel.class );

    private WebMarkupContainer assetConnectionsContainer;
    private AjaxLink<String> addConnectionLink;
    private AssetConnection newAssetConnection;

    public ConnectedAssetsPanel( String id, IModel<? extends AssetConnectable> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        reset();
        addAssetConnectionsContainer();
    }

    private void reset() {
        newAssetConnection = new AssetConnection();
    }

    private void addAssetConnectionsContainer() {
        assetConnectionsContainer = new WebMarkupContainer( "assetConnectionsContainer" );
        assetConnectionsContainer.setOutputMarkupId( true );
        addOrReplace( assetConnectionsContainer );
        addAssetConnections();
    }

    private void addAssetConnections() {
        ListView<AssetConnectionWrapper> assetConnectionListView = new ListView<AssetConnectionWrapper>(
                "assetConnections",
                new PropertyModel<List<AssetConnectionWrapper>>( this, "assetConnections" )
        ) {
            @Override
            protected void populateItem( ListItem<AssetConnectionWrapper> item ) {
                addConnection( item );
                addAssetString( item );
                addAsset( item );
                addConnectionProperties( item );
                addAddConnection( item );
                addDeleteConnection( item );
            }
        };
        assetConnectionListView.setOutputMarkupId( true );
        assetConnectionsContainer.addOrReplace( assetConnectionListView );
    }

    private void addConnection( final ListItem<AssetConnectionWrapper> item ) {
        final AssetConnectionWrapper wrapper = item.getModelObject();
        DropDownChoice<String> connectionChoice = new DropDownChoice<String>(
                "connection",
                new PropertyModel<String>( wrapper, "typeLabel" ),
                getTypeLabelsChoicesFor( item )
        );
        connectionChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                if ( !wrapper.isMarkedForCreation() )
                    update( target, new Change( Change.Type.Updated, getAssetConnectable(), "assets" ) );
            }
        } );
        connectionChoice.setEnabled( isLockedByUser( getAssetConnectable() ) );
        item.add( connectionChoice );
    }

    private void addAssetString( ListItem<AssetConnectionWrapper> item ) {
        AssetConnectionWrapper wrapper = item.getModelObject();
        MaterialAsset asset = wrapper.getAsset();
        String text = asset == null
                ? "asset(s)"
                : asset.isType()
                ? "assets of type"
                : "asset";
        Label label = new Label( "assetString", text );
        item.add( label );
    }

    private List<String> getTypeLabelsChoicesFor( ListItem<AssetConnectionWrapper> item ) {
        return AssetConnection.getTypeLabelsChoicesFor( getAssetConnectable() );
    }

    private void addAsset( ListItem<AssetConnectionWrapper> item ) {
        AssetConnectionWrapper wrapper = item.getModelObject();
        if ( wrapper.isMarkedForCreation() ) {
            final List<String> choices = getQueryService().findAllEntityNames( MaterialAsset.class );
            EntityReferencePanel<MaterialAsset> assetPanel = new EntityReferencePanel<MaterialAsset>(
                    "asset",
                    new Model<AssetConnectionWrapper>( item.getModelObject() ),
                    choices,
                    "asset",
                    MaterialAsset.class );
            assetPanel.enable( isLockedByUser( getAssetConnectable() ) );
            item.add( assetPanel );
        } else {
            MaterialAsset asset = wrapper.getAsset();
            ModelObjectLink assetLink = new ModelObjectLink( "asset",
                    new Model<ModelEntity>( asset ),
                    new Model<String>( asset.getName() ) );
            item.add( assetLink );
        }
    }

    private void addConnectionProperties( ListItem<AssetConnectionWrapper> item ) {
        AssetConnectionWrapper wrapper = item.getModelObject();
        WebMarkupContainer usagePropertiesContainer = new WebMarkupContainer( "usageProperties" );
        item.add( usagePropertiesContainer );
        makeVisible( usagePropertiesContainer, wrapper.hasUsageProperties() );
        // consumes?
        AjaxCheckBox consumesCheckBox = new AjaxCheckBox(
                "consumes",
                new PropertyModel<Boolean>( wrapper, "consumes" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getAssetConnectable(), "assets" ) );
            }
        };
        consumesCheckBox.setEnabled( isLockedByUser( getAssetConnectable() ) );
        usagePropertiesContainer.add( consumesCheckBox );
        // Critical?
        AjaxCheckBox criticalCheckBox = new AjaxCheckBox(
                "critical",
                new PropertyModel<Boolean>( wrapper, "critical" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getAssetConnectable(), "assets" ) );
            }
        };
        criticalCheckBox.setEnabled( isLockedByUser( getAssetConnectable() ) );
        usagePropertiesContainer.add( criticalCheckBox );

    }

    private void addAddConnection( ListItem<AssetConnectionWrapper> item ) {
        final AssetConnectionWrapper wrapper = item.getModelObject();
        addConnectionLink = new AjaxLink<String>( "add" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( wrapper.isCanBeAdded() ) {
                    wrapper.add();
                    reset();
                    addAssetConnectionsContainer();
                    target.add( assetConnectionsContainer );
                    update( target, new Change( Change.Type.Updated, getAssetConnectable(), "assets" ) );
                } else {
                    Change change = Change.message( "Please identify the asset." );
                    update( target, change );
                }
            }
        };
        addConnectionLink.setOutputMarkupId( true );
        makeVisible( addConnectionLink, wrapper.isMarkedForCreation() && isLockedByUser( getAssetConnectable() ) );
        item.addOrReplace( addConnectionLink );
    }

    private void addDeleteConnection( ListItem<AssetConnectionWrapper> item ) {
        final AssetConnectionWrapper wrapper = item.getModelObject();
        AjaxLink<String> addConnectionLink = new AjaxLink<String>( "delete" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                wrapper.delete();
                reset();
                addAssetConnectionsContainer();
                target.add( assetConnectionsContainer );
                update( target, new Change( Change.Type.Updated, getAssetConnectable(), "assets" ) );
            }
        };
        makeVisible( addConnectionLink, !wrapper.isMarkedForCreation() && isLockedByUser( getAssetConnectable() ) );
        item.add( addConnectionLink );
    }

    public List<AssetConnectionWrapper> getAssetConnections() {
        List<AssetConnectionWrapper> wrappers = new ArrayList<AssetConnectionWrapper>();
        List<AssetConnection> allConnections = getAssetConnectable().getAssetConnections().getAll();
        for ( int i = 0; i < allConnections.size(); i++ ) {
            wrappers.add( new AssetConnectionWrapper( allConnections.get( i ), i ) );
        }
        if ( isLockedByUser( getAssetConnectable() ) )
            wrappers.add( new AssetConnectionWrapper() );
        return wrappers;
    }

    public AssetConnectable getAssetConnectable() {
        return (AssetConnectable) getModel().getObject();
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForProperty( "asset" ) ) {
            AssetConnectionWrapper wrapper = (AssetConnectionWrapper) change.getSubject( getCommunityService() );
            if ( !wrapper.isMarkedForCreation() )
                super.updateWith( target, change, updated );
        } else {
            super.updateWith( target, change, updated );
        }
    }

    public class AssetConnectionWrapper extends AbstractIdentifiable {

        private AssetConnection assetConnection;
        private boolean markedForCreation;
        private int index = -1;

        public AssetConnectionWrapper() {
            assetConnection = newAssetConnection;
            markedForCreation = true;
        }

        public AssetConnectionWrapper( AssetConnection assetConnection, int index ) {
            this.assetConnection = assetConnection;
            this.index = index;
            markedForCreation = false;
        }

        public AssetConnection getAssetConnection() {
            return assetConnection;
        }

        public void setAssetConnection( AssetConnection assetConnection ) {
            this.assetConnection = assetConnection;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public String getTypeLabel() {
            return getAssetConnection().getTypeLabel();
        }

        private AssetConnection.Type getType() {
            return getAssetConnection().getType();
        }

        public void setTypeLabel( String typeLabel ) {
            if ( isMarkedForCreation() ) {
                getAssetConnection().setTypeLabel( typeLabel );
            } else {
                try {
                    doCommand( UpdateObject.makeCommand(
                            getUsername(),
                            getAssetConnectable(),
                            "assetConnections.all[" + index + "].typeLabel",
                            typeLabel,
                            UpdateObject.Action.Set
                    ) );
                } catch ( CommandException e ) {
                    LOG.warn( "Failed to update asset connection type" );
                }
            }
        }

        public MaterialAsset getAsset() {
            MaterialAsset asset = assetConnection.getAsset();
            return asset.isUnknown() ? null : asset;
        }

        public void setAsset( MaterialAsset materialAsset ) {
            if ( isMarkedForCreation() ) {
                getAssetConnection().setAsset( materialAsset );
            } else {
                try {
                    doCommand( UpdateObject.makeCommand(
                            getUsername(),
                            getAssetConnectable(),
                            "assetConnections.all[" + index + "].asset",
                            materialAsset,
                            UpdateObject.Action.Set
                    ) );
                } catch ( CommandException e ) {
                    LOG.warn( "Failed to update connected asset" );
                }
            }
        }

        public boolean isConsumes() {
            return getAssetConnection().hasProperty( AssetConnection.CONSUMING );
        }

        public void setConsumes( boolean val ) {
            if ( isMarkedForCreation() ) {
                getAssetConnection().setConsuming( val );
            } else {
                try {
                    doCommand( UpdateObject.makeCommand(
                            getUsername(),
                            getAssetConnectable(),
                            "assetConnections.all[" + index + "].consuming",
                            val,
                            UpdateObject.Action.Set
                    ) );
                } catch ( CommandException e ) {
                    LOG.warn( "Failed to update connected asset consuming property" );
                }
            }
        }

        public boolean isCritical() {
            return getAssetConnection().hasProperty( AssetConnection.CONSUMING );
        }

        public void setCritical( boolean val ) {
            if ( isMarkedForCreation() ) {
                getAssetConnection().setCritical( val );
            } else {
                try {
                    doCommand( UpdateObject.makeCommand(
                            getUsername(),
                            getAssetConnectable(),
                            "assetConnections.all[" + index + "].critical",
                            val,
                            UpdateObject.Action.Set
                    ) );
                } catch ( CommandException e ) {
                    LOG.warn( "Failed to update connected asset critical property" );
                }
            }
        }


        public boolean hasUsageProperties() {
            return getType() != null && getType() == AssetConnection.Type.Using;
        }

        public void add() {
            if ( isMarkedForCreation() && isCanBeAdded() ) {
                try {
                    doCommand( UpdateObject.makeCommand(
                            getUsername(),
                            getAssetConnectable(),
                            "assetConnections.all",
                            assetConnection,
                            UpdateObject.Action.Add
                    ) );
                } catch ( CommandException e ) {
                    LOG.warn( "Failed to add connected asset" );
                }
            }
        }

        private boolean isCanBeAdded() {
            return getAssetConnection().getType() != null && !getAssetConnection().getAsset().isUnknown();
        }

        public void delete() {
            if ( !isMarkedForCreation() ) {
                try {
                    doCommand( UpdateObject.makeCommand(
                            getUsername(),
                            getAssetConnectable(),
                            "assetConnections.all",
                            assetConnection,
                            UpdateObject.Action.Remove
                    ) );
                } catch ( CommandException e ) {
                    LOG.warn( "Failed to remove connected asset" );
                }
            }
        }

        //// Identifiable


        @Override
        public long getId() {
            return index;
        }

    }
}
