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
public class AssignmentAssetLink implements Serializable {

    public enum Type {
        SupplyCommitment,
        AvailabilityToUse
    }

    private Assignment fromAssignment;
    private Assignment toAssignment;
    private MaterialAsset materialAsset;
    private Type type;

    public AssignmentAssetLink( Assignment fromAssignment,
                                Assignment toAssignment,
                                MaterialAsset materialAsset,
                                Type type ) {
        this.fromAssignment = fromAssignment;
        this.toAssignment = toAssignment;
        this.materialAsset = materialAsset;
        this.type = type;
    }

    public Assignment getFromAssignment() {
        return fromAssignment;
    }

    public Assignment getToAssignment() {
        return toAssignment;
    }

    public MaterialAsset getMaterialAsset() {
        return materialAsset;
    }

    public Type getType() {
        return type;
    }

    public boolean isSupplyCommitment() {
        return type == Type.SupplyCommitment;
    }

    public boolean isAvailabilityToUse() {
        return type == Type.AvailabilityToUse;
    }

    /// OBJECT

    @Override
    public String toString() {
        return fromAssignment.getFullTitle( "," )
                + (isSupplyCommitment() ? " supplies " : " makes available ")
                + materialAsset.getName()
                + (isSupplyCommitment() ? " to " : " for use by ")
                + toAssignment.getFullTitle( "," );
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof AssignmentAssetLink ) {
            AssignmentAssetLink other = (AssignmentAssetLink) object;
            return fromAssignment.equals( other.getFromAssignment() )
                    && toAssignment.equals( other.getToAssignment() )
                    && materialAsset.equals( other.getMaterialAsset() )
                    && type.equals( other.getType() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash + 31 * fromAssignment.hashCode();
        hash = hash + 31 * toAssignment.hashCode();
        hash = hash + 31 * materialAsset.hashCode();
        hash = hash + 31 * type.hashCode();
        return hash;
    }
}
