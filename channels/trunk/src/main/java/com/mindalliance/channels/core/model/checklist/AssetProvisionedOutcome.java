package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.MaterialAsset;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/27/14
 * Time: 10:57 AM
 */
public class AssetProvisionedOutcome extends Outcome {

    public static final String REF_PREFIX = "assetProvisioned|";

    private AssetConnection assetConnection;

    public AssetProvisionedOutcome( AssetConnection assetConnection ) {
        assert assetConnection.isProvisioning();
        this.assetConnection = assetConnection;
    }

    public static boolean isAssetProvisionedOutcomeRef( String outcomeRef ) {
        return outcomeRef.startsWith( REF_PREFIX );
    }

    public AssetConnection getAssetConnection() {
        return assetConnection;
    }

    public MaterialAsset getProvisionedAsset() {
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
        return false;
    }

    @Override
    public boolean isAssetProvisionedOutcome() {
        return true;
    }

    @Override
    public String getLabel() {
        return getAssetConnection().getStepOutcomeLabel();
    }

    @Override
    public String getRef() {
        return REF_PREFIX
                + getProvisionedAsset();
    }

    @Override
    public int hashCode() {
        int hash = REF_PREFIX.hashCode();
        hash = hash + 31 * assetConnection.hashCode();
        return hash;
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof AssetProvisionedOutcome
                && assetConnection.equals( ( (AssetProvisionedOutcome) object ).getAssetConnection() );
    }
}
