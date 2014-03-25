package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.MaterialAsset;

/**
 * A checklist step outcome about an asset being produced by the task.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/14
 * Time: 10:47 AM
 */
public class AssetProducedOutcome extends Outcome {

    public static final String REF_PREFIX = "assetProduced|";

    private AssetConnection assetConnection;

    public AssetProducedOutcome( AssetConnection assetConnection ) {
        assert assetConnection.getType() == AssetConnection.Type.Producing;
        this.assetConnection = assetConnection;
    }

    public static boolean isAssetProducedOutcomeRef( String outcomeRef ) {
        return outcomeRef.startsWith( REF_PREFIX );
    }

    public AssetConnection getAssetConnection() {
        return assetConnection;
    }

    public MaterialAsset getProducedAsset() {
        return assetConnection.getAsset();
    }

    @Override
    public boolean isEventTimingOutcome() {
        return false;
    }

    @Override
    public boolean isCapabilityCreatedOutcome() {
        return false;
    }

    @Override
    public boolean isGoalAchievedOutcome() {
        return false;
    }

    @Override
    public boolean isAssetProducedOutcome() {
        return true;
    }

    @Override
    public String getLabel() {
        return getAssetConnection().getStepOutcomeLabel();
    }

    @Override
    public String getRef() {
        return REF_PREFIX
                + getProducedAsset();
    }

    @Override
    public int hashCode() {
        return assetConnection.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof AssetProducedOutcome
                && assetConnection.equals( ( (AssetProducedOutcome) object ).getAssetConnection() );
    }

}
