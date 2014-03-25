package com.mindalliance.channels.core.model.asset;

import com.mindalliance.channels.core.model.Identifiable;

/**
 * An object that has something to do with material assets.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/14
 * Time: 4:34 PM
 */
public interface AssetConnectable extends Identifiable {

    /**
     * Assets aspect.
     */
    public static final String ASSETS = "assets";

    /**
     * Whether this can have assets in stock and thus not be expected to plan for their production
     * or replenishing.
     * @return a boolean
     */
    boolean isCanStockAssets();

    /**
     * Whether this can produce assets.
     * @return a boolean
     */
    boolean isCanProduceAssets();

    /**
     * Whether this can use and possibly consume assets.
     * @return a boolean
     */
    boolean isCanUseAssets();
    /**
     * Whether this can provision assets.
     * @return a boolean
     */

     boolean isCanProvisionAssets();
    /**
     * Whether this can lead to the provisioning of assets.
     * @return a boolean
     */
     boolean isCanBeAssetDemand();

    /**
     * Get all asset connection.
     * @return a list of asset connection
     */
    AssetConnections getAssetConnections();

    /**
     * Get display label.
     * @return a string
     */
    String getLabel();

    /**
     * Get default asset connection type.
     * @return an asset connection type
     */
    AssetConnection.Type getDefaultAssetConnectionType();
}
