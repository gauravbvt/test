package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.asset.AssetField;
import com.mindalliance.channels.core.model.asset.MaterialAsset;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/7/14
 * Time: 2:06 PM
 */
public class AssetBinding implements Serializable {

    private MaterialAsset placeholder;
    private MaterialAsset asset;

    public AssetBinding( MaterialAsset placeholder, MaterialAsset asset ) {
        this.asset = asset;
        this.placeholder = placeholder;
    }

    public AssetBinding( MaterialAsset placeholder ) {
        this.placeholder = placeholder;
    }

    public AssetBinding( AssetBinding assetBinding ) {
        this.placeholder = assetBinding.getPlaceholder();
        this.asset = assetBinding.getAsset();
    }

    public MaterialAsset getAsset() {
        return asset;
    }

    public MaterialAsset getPlaceholder() {
        return placeholder;
    }

    public void setAsset( MaterialAsset asset ) {
        this.asset = asset;
        if ( placeholder != null ) { // inherit all fields of the placeholder
            for ( AssetField assetField : placeholder.getFields() ) {
                asset.addField( new AssetField( assetField ) );
            }
        }
    }

    public boolean isBound() {
        return asset != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "MaterialAsset \"" )
                .append( placeholder.getName() )
                .append( "\" is bound by " );
        if ( asset == null ) {
            sb.append( "nothing" );
        } else {
            sb.append( "asset \"" )
                    .append( asset.getName() );
        }
        return sb.toString();
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof AssetBinding ) {
            AssetBinding other = (AssetBinding)object;
            return placeholder.equals( other.getPlaceholder() )
                    && ModelObject.areEqualOrNull( asset, other.getAsset() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + placeholder.hashCode();
        if ( asset != null )
            hash = hash * 31 + asset.hashCode();
        return hash;
    }

}
