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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/11/14
 * Time: 1:49 PM
 */
public class ConnectedAssetsPanel extends AbstractCommandablePanel implements TabIndexable {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ConnectedAssetsPanel.class );

    private WebMarkupContainer assetConnectionsContainer;
    private AssetConnection newAssetConnection;
    private List<AssetConnection.Type> excludedConnectionTypes = new ArrayList<AssetConnection.Type>();
    private TabIndexer tabIndexer;

    public ConnectedAssetsPanel( String id,
                                 IModel<? extends AssetConnectable> iModel,
                                 List<AssetConnection.Type> excludedConnectionTypes ) {
        super( id, iModel );
        this.excludedConnectionTypes = excludedConnectionTypes;
        init();
    }

    public ConnectedAssetsPanel( String id, IModel<? extends AssetConnectable> iModel ) {
        super( id, iModel );
        init();
    }

    @Override
    public void initTabIndexing( TabIndexer tabIndexer ) {
        this.tabIndexer = tabIndexer;
    }

    private void init() {
        reset();
        addAssetConnectionsContainer();
    }

    private void reset() {
        newAssetConnection = new AssetConnection();
        newAssetConnection.setType( getDefaultAssetConnectionType( getAssetConnectable() ) );
    }

    private AssetConnection.Type getDefaultAssetConnectionType( AssetConnectable assetConnectable ) {
        List<AssetConnection.Type> typeChoices = getTypeChoicesFor( assetConnectable );
        if ( typeChoices.size() == 1 ) {
            return typeChoices.get( 0 );
        } else {
            return assetConnectable.getDefaultAssetConnectionType();
        }
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
                item.setOutputMarkupId( true );
                addConnection( item );
                addAsset( item );
                addConnectionProperties( item );
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
                if ( !wrapper.isMarkedForCreation() ) {
                    update( target, new Change( Change.Type.Updated, getAssetConnectable(), "assets" ) );
                }
                addAssetConnectionsContainer();
                target.add( assetConnectionsContainer );
            }
        } );
        connectionChoice.setEnabled( isLockedByUser( getAssetConnectable() ) );
        item.add( connectionChoice );
        applyTabIndexTo( connectionChoice, tabIndexer );
    }

    @SuppressWarnings( "unchecked" )
    private List<String> getTypeLabelsChoicesFor( ListItem<AssetConnectionWrapper> item ) {
        List<String> choices = (List<String>) CollectionUtils.collect(
                getTypeChoicesFor( getAssetConnectable() ),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return StringUtils.capitalize( AssetConnection.getLabelFor( (AssetConnection.Type) input ) );
                    }
                }
        );
        Collections.sort( choices );
        return choices;
    }

    private List<AssetConnection.Type> getTypeChoicesFor( AssetConnectable assetConnectable ) {
        List<AssetConnection.Type> choices = new ArrayList<AssetConnection.Type>( AssetConnection.getTypeChoicesFor( getAssetConnectable() ) );
        choices.removeAll( excludedConnectionTypes );
        return choices;
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
            assetPanel.initTabIndexing( tabIndexer );
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
        WebMarkupContainer propertiesContainer = new WebMarkupContainer( "properties" );
        item.add( propertiesContainer );
        makeVisible( propertiesContainer, !wrapper.isMarkedForCreation() && wrapper.hasUsageProperties() );
        addCritical( propertiesContainer, wrapper );
        addConsumes( propertiesContainer, wrapper );
        addForwarding( propertiesContainer, wrapper );
    }


    private void addCritical( WebMarkupContainer propertiesContainer, AssetConnectionWrapper wrapper ) {
        WebMarkupContainer criticalContainer = new WebMarkupContainer( "criticalContainer" );
        makeVisible( criticalContainer, wrapper.getType() == AssetConnection.Type.Using );
        propertiesContainer.add( criticalContainer );
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
        criticalContainer.add( criticalCheckBox );
        applyTabIndexTo( criticalCheckBox, tabIndexer );
    }

    private void addConsumes( WebMarkupContainer propertiesContainer, AssetConnectionWrapper wrapper ) {
        WebMarkupContainer consumesContainer = new WebMarkupContainer( "consumesContainer" );
        makeVisible( consumesContainer, wrapper.getType() == AssetConnection.Type.Using );
        propertiesContainer.add( consumesContainer );
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
        consumesContainer.add( consumesCheckBox );
        applyTabIndexTo( consumesCheckBox, tabIndexer );
    }

    private void addForwarding( WebMarkupContainer propertiesContainer, AssetConnectionWrapper wrapper ) {
        WebMarkupContainer forwardingContainer = new WebMarkupContainer( "forwardingContainer" );
        makeVisible( forwardingContainer, wrapper.getType() == AssetConnection.Type.Demanding );
        propertiesContainer.add( forwardingContainer );
        AjaxCheckBox forwardingCheckBox = new AjaxCheckBox(
                "forwarding",
                new PropertyModel<Boolean>( wrapper, "forwarding" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getAssetConnectable(), "assets" ) );
            }
        };
        forwardingCheckBox.setEnabled( isLockedByUser( getAssetConnectable() ) );
        forwardingContainer.add( forwardingCheckBox );
        applyTabIndexTo( forwardingCheckBox, tabIndexer );
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
            AssetConnection connection = allConnections.get( i );
            if ( !excludedConnectionTypes.contains( connection.getType() ) )
                wrappers.add( new AssetConnectionWrapper( connection, i ) );
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
            else {
                if ( wrapper.isCanBeAdded() ) {
                    wrapper.add();
                    reset();
                    addAssetConnectionsContainer();
                    target.add( assetConnectionsContainer );
                    update( target, new Change( Change.Type.Updated, getAssetConnectable(), "assets" ) );

                }
            }
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
            return StringUtils.capitalize( getAssetConnection().getTypeLabel() );
        }

        private AssetConnection.Type getType() {
            return getAssetConnection().getType();
        }

        public void setTypeLabel( String typeLabel ) {
            if ( isMarkedForCreation() ) {
                assetConnection.setTypeLabel( typeLabel );
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
            if ( isMarkedForCreation() ) {
                return null;
            } else {
                MaterialAsset asset = assetConnection.getAsset();
                return asset.isUnknown() ? null : asset;
            }
        }

        public void setAsset( MaterialAsset asset ) {
            if ( isMarkedForCreation() ) {
                getAssetConnection().setAsset( asset );
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
            return getAssetConnection().hasProperty( AssetConnection.CRITICAL );
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

        public boolean isForwarding() {
            return getAssetConnection().hasProperty( AssetConnection.FORWARDING );
        }

        public void setForwarding( boolean val ) {
            if ( isMarkedForCreation() ) {
                getAssetConnection().setForwarding( val );
            } else {
                try {
                    doCommand( UpdateObject.makeCommand(
                            getUsername(),
                            getAssetConnectable(),
                            "assetConnections.all[" + index + "].forwarding",
                            val,
                            UpdateObject.Action.Set
                    ) );
                } catch ( CommandException e ) {
                    LOG.warn( "Failed to update connected asset forwarding property" );
                }
            }
        }


        public boolean hasUsageProperties() {
            return getType() != null
                    && getType() == AssetConnection.Type.Using || getType() == AssetConnection.Type.Demanding;
        }

        public void add() {
            if ( isMarkedForCreation() && isCanBeAdded() ) {
                try {
                    doCommand( UpdateObject.makeCommand(
                            getUsername(),
                            getAssetConnectable(),
                            "assetConnections.all",
                            assetConnection,
                            UpdateObject.Action.AddUnique
                    ) );
                } catch ( CommandException e ) {
                    LOG.warn( "Failed to add connected asset" );
                }
            }
        }

        private boolean isCanBeAdded() {
            return getAssetConnection().getAsset() != null
                    && getAssetConnection().getType() != null;
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
