package com.mindalliance.channels.core.model.asset;

import com.mindalliance.channels.core.model.ModelObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/10/14
 * Time: 1:40 PM
 */
public class AssetConnections implements Serializable {

    private List<AssetConnection> assetConnections = new ArrayList<AssetConnection>();

    public AssetConnections() {
    }

    public List<AssetConnection> getAll() {
        return assetConnections == null ? new ArrayList<AssetConnection>() : assetConnections;
    }

    public void add( AssetConnection assetConnection ) {
        if ( assetConnections == null ) {
            assetConnections = new ArrayList<AssetConnection>();
        }
        if ( assetConnection != null && !assetConnections.contains( assetConnection ) ) {
            assetConnections.add( assetConnection );
        }
    }

    public boolean remove( AssetConnection assetConnection ) {
        if ( assetConnections != null ) {
            if ( assetConnection != null ) {
                return assetConnections.remove( assetConnection );
            }
        }
        return false;
    }

    private List<MaterialAsset> findAll( AssetConnection.Type type ) {
        List<MaterialAsset> materialAssets = new ArrayList<MaterialAsset>();
        for ( AssetConnection assetConnection : getAll() ) {
            if ( assetConnection.getType() == type ) {
                materialAssets.add( assetConnection.getAsset() );
            }
        }
        return materialAssets;
    }

    public List<MaterialAsset> findAssetsProduced() {
        return findAll( AssetConnection.Type.Producing );
    }

    public List<MaterialAsset> findAssetsProvisioned() {
        return findAll( AssetConnection.Type.Provisioning );
    }

    public List<MaterialAsset> findAssetsUsed() {
        return findAll( AssetConnection.Type.Using );
    }

    public List<MaterialAsset> findAssetsStocked() {
        return findAll( AssetConnection.Type.Stocking );
    }

    public List<MaterialAsset> findAssetsDemanded() {
        return findAll( AssetConnection.Type.Demanding );
    }

    public boolean hasConnectionProperty( final AssetConnection.Type type, final MaterialAsset asset, final String name ) {
        return CollectionUtils.exists(
                getAll(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        AssetConnection assetConnection = (AssetConnection) object;
                        return assetConnection.getType() == type
                                && assetConnection.getAsset().equals( asset )
                                && assetConnection.hasProperty( name );
                    }
                }
        );
    }

    public boolean references( final ModelObject mo ) {
        return CollectionUtils.exists(
                getAll(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        MaterialAsset asset = ( (AssetConnection) object ).getAsset();
                        return ModelObject.areIdentical( asset, mo );
                    }
                }
        );
    }

    public boolean isConsumed( MaterialAsset asset ) {
        return hasConnectionProperty( AssetConnection.Type.Using, asset, AssetConnection.CONSUMING );
    }

    public boolean isCritical( MaterialAsset asset ) {
        return hasConnectionProperty( AssetConnection.Type.Using, asset, AssetConnection.CRITICAL );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for ( AssetConnection assetConnection : getAll() ) {
            if ( sb.length() > 0 )
                sb.append( ", " );
            sb.append( assetConnection.toString() );
        }
        if ( sb.length() == 0 )
            sb.append( "None" );
        return sb.toString();
    }

    @SuppressWarnings( "unchecked" )
    public List<MaterialAsset> getAllAssets() {
        return (List<MaterialAsset>) CollectionUtils.collect(
                getAll(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (AssetConnection) input ).getAsset();
                    }
                }
        );
    }
}
