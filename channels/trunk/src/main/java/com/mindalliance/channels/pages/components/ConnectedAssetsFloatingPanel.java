package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.asset.AssetConnectable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/11/14
 * Time: 1:46 PM
 */
public class ConnectedAssetsFloatingPanel extends AbstractFloatingCommandablePanel {

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 500;
    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    private ConnectedAssetsPanel connectedAssetsPanel;
    private boolean assetsUpdated = false;
    private IModel<AssetConnectable> assetConnectableModel;
    private Label aboutAssetConnectableLabel;


    public ConnectedAssetsFloatingPanel( String id,
                                         IModel<AssetConnectable> assetConnectableModel,
                                         Set<Long> readOnlyExpansions ) {
        super( id, assetConnectableModel, readOnlyExpansions );
        this.assetConnectableModel = assetConnectableModel;
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "info-sharing";
    }

    @Override
    public String getHelpTopicId() {
        return "assets";
    }

    private void init() {
        addDoneButton();
        addAboutAssetConnectable();
        addConnectedAssetsPanel( );
    }

    private void addAboutAssetConnectable() {
        aboutAssetConnectableLabel = new Label( "aboutAssetConnectable", getAssetConnectableLabel() );
        aboutAssetConnectableLabel.setOutputMarkupId( true );
        getContentContainer().addOrReplace( aboutAssetConnectableLabel );
    }

    private String getAssetConnectableLabel() {
        return getAssetConnectableString() + "...";
    }

    private  String getAssetConnectableString() {
        AssetConnectable assetConnectable = getAssetConnectable();
        return assetConnectable.getTypeName() + " " + assetConnectable.getLabel();
    }


    private void addConnectedAssetsPanel( ) {
        connectedAssetsPanel = new ConnectedAssetsPanel( "assets", assetConnectableModel );
        getContentContainer().addOrReplace( connectedAssetsPanel );
    }

    /**
     * {@inheritDoc}
     */
    protected String getTitle() {
        return getAssetConnectableString() + " - Assets";
    }

    private void addDoneButton() {
        AjaxLink doneLink = new AjaxLink( "done" ) {
            public void onClick( AjaxRequestTarget target ) {
                close( target );
            }
        };
        getContentContainer().add( doneLink );
    }

    public void changed( Change change ) {
        if ( change.isUpdated() && change.isForInstanceOf( AssetConnectable.class ) ) {
            assetsUpdated = true;
        }
        super.changed( change );
    }

    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        if ( change.isUnknown() || change.isModified() || change.isRefresh() || change.isCollapsed() ) {
            addAboutAssetConnectable();
            addConnectedAssetsPanel( );
            target.add( aboutAssetConnectableLabel );
            target.add( connectedAssetsPanel );
        }
    }


    /**
     * {@inheritDoc}
     */
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectClosed, getAssetConnectable(), AssetConnectable.ASSETS );
        change.addQualifier( "updated", assetsUpdated );
        update( target, change );
    }

    private AssetConnectable getAssetConnectable() {
        return (AssetConnectable) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadTop() {
        return PAD_TOP;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadLeft() {
        return PAD_LEFT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadBottom() {
        return PAD_BOTTOM;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadRight() {
        return PAD_RIGHT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinWidth() {
        return MIN_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinHeight() {
        return MIN_HEIGHT;
    }



}
