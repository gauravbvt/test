package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.asset.MaterialAsset;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/3/14
 * Time: 9:20 PM
 */
public class AssetSupplyCommitment implements Serializable {

    private Assignment supplier;
    private Assignment supplied;
    private MaterialAsset materialAsset;

    public AssetSupplyCommitment( Assignment supplier, Assignment supplied, MaterialAsset materialAsset ) {
        this.supplier = supplier;
        this.supplied = supplied;
        this.materialAsset = materialAsset;
    }

    public Assignment getSupplier() {
        return supplier;
    }

    public Assignment getSupplied() {
        return supplied;
    }

    public MaterialAsset getMaterialAsset() {
        return materialAsset;
    }

    /// OBJECT

    @Override
    public String toString() {
        return supplier.getFullTitle( "," )
                + " supplies "
                +  materialAsset.getName()
                + " to "
                + supplied.getFullTitle( "," );
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof AssetSupplyCommitment ) {
            AssetSupplyCommitment other = (AssetSupplyCommitment)object;
            return supplier.equals( other.getSupplier() )
                    && supplied.equals( other.getSupplied() )
                    && materialAsset.equals( other.getMaterialAsset() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash + 31 * supplier.hashCode();
        hash = hash + 31 * supplied.hashCode();
        hash = hash + 31 * materialAsset.hashCode();
        return hash;
    }
}
