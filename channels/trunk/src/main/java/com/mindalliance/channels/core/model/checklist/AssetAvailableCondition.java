package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.MaterialAsset;

/**
 * A checklist step condition about an asset being available to the task using it.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/14
 * Time: 10:46 AM
 */
public class AssetAvailableCondition extends Condition {

    public static final String REF_PREFIX = "assetAvailable|";

    private AssetConnection assetConnection;

    public AssetAvailableCondition( AssetConnection assetConnection ) {
        assert assetConnection.isUsing()
                || assetConnection.isProducing()
                || assetConnection.isProvisioning();
        this.assetConnection = assetConnection;
    }

    public AssetConnection getAssetConnection() {
        return assetConnection;
    }

    public MaterialAsset getAssetAvailable() {
        return assetConnection.getAsset();
    }

    @Override
    public String getRef() {
        return REF_PREFIX
                +  getAssetAvailable();
    }

    @Override
    public boolean isNeedSatisfiedCondition() {
        return false;
    }

    @Override
    public boolean isEventTimingCondition() {
        return false;
    }

    @Override
    public boolean isGoalCondition() {
        return false;
    }

    @Override
    public boolean isLocalCondition() {
        return false;
    }

    @Override
    public boolean isTaskFailedCondition() {
        return false;
    }

    @Override
    public boolean isAssetAvailableCondition() { return true; }

    @Override
    public boolean matches( Outcome outcome ) {
        // meaningful when a step produces an asset needed by another in the same checklist
        if ( outcome.isAssetProducedOutcome() ) {
            AssetProducedOutcome assetProducedOutcome = (AssetProducedOutcome)outcome;
            AssetConnection assetConnection = assetProducedOutcome.getAssetConnection();
            return assetConnection.getAsset().narrowsOrEquals( assetConnection.getAsset() );
        } else {
            return false;
        }
    }

    @Override
    public String getLabel() {
        return getAssetConnection().getStepConditionLabel();
    }

    public static boolean isNeedRef( String conditionRef ) {
        return conditionRef.startsWith( REF_PREFIX );
    }

    @Override
    public int hashCode() {
        return assetConnection.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof AssetAvailableCondition
                && assetConnection.equals( ((AssetAvailableCondition)object).getAssetConnection() );
    }

}
