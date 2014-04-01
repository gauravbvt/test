package com.mindalliance.channels.core.model.asset;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/10/14
 * Time: 1:40 PM
 */
public class AssetConnections implements Iterable<AssetConnection>, Serializable {

    private List<AssetConnection> assetConnections = new ArrayList<AssetConnection>();

    public AssetConnections() {
    }

    public AssetConnections( AssetConnections assetConnections ) {
        for ( AssetConnection assetConnection : assetConnections ) {
            add( new AssetConnection( assetConnection ) );
        }
    }

    public List<AssetConnection> getAll() {
        return assetConnections == null ? new ArrayList<AssetConnection>() : assetConnections;
    }

    public void add( AssetConnection assetConnection ) {
        if ( assetConnections == null ) {
            assetConnections = new ArrayList<AssetConnection>();
        } else {
            assert isValid( assetConnection );
            if ( !assetConnections.contains( assetConnection ) ) {
                assetConnections.add( assetConnection );
            }
        }
    }

    private boolean isValid( AssetConnection assetConnection ) {
        return assetConnection.getType() != null
                && assetConnection.getAsset() != null;
    }

    public boolean remove( AssetConnection assetConnection ) {
        if ( assetConnections != null ) {
            if ( assetConnection != null ) {
                return assetConnections.remove( assetConnection );
            }
        }
        return false;
    }

    private List<MaterialAsset> findAllAssets( AssetConnection.Type type ) {
        List<MaterialAsset> materialAssets = new ArrayList<MaterialAsset>();
        for ( AssetConnection assetConnection : this ) {
            if ( assetConnection.isOfType( type ) ) {
                MaterialAsset asset = assetConnection.getAsset();
                if ( !asset.isUnknown() )
                    materialAssets.add( asset );
            }
        }
        return materialAssets;
    }

    public List<MaterialAsset> findAssetsProvisioned() {
        return findAllAssets( AssetConnection.Type.Provisioning );
    }

    public boolean hasConnectionProperty( final AssetConnection.Type type,
                                          final MaterialAsset asset,
                                          final String name ) {
        return CollectionUtils.exists(
                getAll(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        AssetConnection assetConnection = (AssetConnection) object;
                        return assetConnection.isOfType( type )
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
                        return ( (AssetConnection) object ).getAsset().equals( mo );
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

    @SuppressWarnings("unchecked")
    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        List<MaterialAsset> assets = findAllMaterialAssets();
        if ( assets.isEmpty() ) {
            sb.append( "None" );
        } else {
            Collections.sort( assets );
            for ( MaterialAsset asset : assets ) {
                if ( sb.length() > 0 )
                    sb.append( ", and " );
                List<String> typeStrings = findConnectionTypesLabelsFor( asset );
                sb.append( ChannelsUtils.listToString( typeStrings, " and " ) )
                        .append( " " )
                        .append( asset.getName() );

            }
        }
        return sb.toString();
    }

    @SuppressWarnings( "unchecked" )
    public String getAssetListLabel() {
        List<MaterialAsset> assets = findAllMaterialAssets();
        Collections.sort( assets );
        List<String> assetNames = (List<String>)CollectionUtils.collect(
                assets,
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ((MaterialAsset)input).getName();
                    }
                }
        );
        return ChannelsUtils.listToString( assetNames, " and " );
    }


    public String getFirstPersonLabel() {
        StringBuilder sb = new StringBuilder();
        List<MaterialAsset> assets = findAllMaterialAssets();
        if ( assets.isEmpty() ) {
            sb.append( "None" );
        } else {
            Collections.sort( assets );
            for ( MaterialAsset asset : assets ) {
                if ( sb.length() > 0 )
                    sb.append( ", and " );
                List<String> typeStrings = findFirstPersonConnectionTypesLabelsFor( asset );
                sb.append( ChannelsUtils.listToString( typeStrings, " and " ) )
                        .append( " " )
                        .append( ChannelsUtils.startsWithVowel( asset.getName() )
                                        ? "an "
                                        : "a "
                        )
                        .append( asset.getName() );

            }
        }
        return sb.toString();
    }

    public String getStepLabel() {
        StringBuilder sb = new StringBuilder();
        List<MaterialAsset> assets = findAllMaterialAssets();
        if ( !assets.isEmpty() ) {
            Collections.sort( assets );
            for ( MaterialAsset asset : assets ) {
                if ( sb.length() > 0 )
                    sb.append( ", and " );
                List<String> typeStrings = findConnectionTypesStepLabelsFor( asset );
                sb.append( ChannelsUtils.listToString( typeStrings, " and " ) )
                        .append( " " )
                        .append( asset.getName() );

            }
        }
        return sb.toString();
    }


    private List<MaterialAsset> findAllMaterialAssets() {
        Set<MaterialAsset> assets = new HashSet<MaterialAsset>();
        for ( AssetConnection assetConnection : this ) {
            assets.add( assetConnection.getAsset() );
        }
        return new ArrayList<MaterialAsset>( assets );
    }

    private List<String> findConnectionTypesLabelsFor( MaterialAsset materialAsset ) {
        List<AssetConnection> assetConnectionList
                = new ArrayList<AssetConnection>( this.about( materialAsset ).getAll()) ;
        AssetConnection.sortOnTypes( assetConnectionList );
        Set<String> typeLabels = new HashSet<String>();
        for ( AssetConnection assetConnection : assetConnectionList ) {
            if ( assetConnection.getAsset().equals( materialAsset ) ) {
                typeLabels.add( assetConnection.getDetailedTypeLabel() );
            }
        }
        List<String> result = new ArrayList<String>( typeLabels );
        Collections.sort( result );
        return result;
    }

    private List<String> findFirstPersonConnectionTypesLabelsFor( MaterialAsset materialAsset ) {
        List<AssetConnection> assetConnectionList
                = new ArrayList<AssetConnection>( this.about( materialAsset ).getAll()) ;
        AssetConnection.sortOnTypes( assetConnectionList );
        Set<String> typeLabels = new HashSet<String>();
        for ( AssetConnection assetConnection : assetConnectionList ) {
            if ( assetConnection.getAsset().equals( materialAsset ) ) {
                typeLabels.add( assetConnection.getFirstPersonTypeLabel() );
            }
        }
        List<String> result = new ArrayList<String>( typeLabels );
        Collections.sort( result );
        return result;
    }


    private List<String> findConnectionTypesStepLabelsFor( MaterialAsset materialAsset ) {
        List<AssetConnection> assetConnectionList
                = new ArrayList<AssetConnection>( this.about( materialAsset ).getAll()) ;
        AssetConnection.sortOnTypes( assetConnectionList );
        Set<String> typeLabels = new HashSet<String>();
        for ( AssetConnection assetConnection : assetConnectionList ) {
            if ( assetConnection.getAsset().equals( materialAsset ) ) {
                typeLabels.add( assetConnection.getDetailedTypeStepLabel() );
            }
        }
        List<String> result = new ArrayList<String>( typeLabels );
        Collections.sort( result );
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for ( AssetConnection assetConnection : this ) {
            if ( sb.length() > 0 )
                sb.append( ", " );
            sb.append( assetConnection.toString() );
        }
        if ( sb.length() == 0 )
            sb.append( "None" );
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
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

    public boolean isEmpty() {
        return getAll().isEmpty();
    }

    public AssetConnections copy() {
        AssetConnections copy = new AssetConnections();
        for ( AssetConnection assetConnection : this ) {
            AssetConnection assetConnectionCopy = new AssetConnection();
            assetConnectionCopy.setType( assetConnection.getType() );
            assetConnectionCopy.setAsset( assetConnection.getAsset() );
            copy.add( assetConnectionCopy );
        }
        return copy;
    }

    public AssetConnections visibleTo( Flow flow, boolean isSend ) {
        AssetConnections connections = new AssetConnections();
        for ( AssetConnection connection : this ) {
            if ( isSend && flow.isNotification() || !isSend && flow.isAskedFor() ) { //demands only
                if ( connection.isDemanding() ) {
                    connections.add( connection );
                }
            } else if ( !isSend && flow.isNotification() || isSend && flow.isAskedFor() ) { // provisioning only
                if ( connection.isProvisioning() ) {
                    connections.add( connection );
                }
            }
        }
        return connections;
    }

    // ITERABLE


    @Override
    public Iterator<AssetConnection> iterator() {
        return getAll().iterator();
    }

    public AssetConnections provisioning() {
        AssetConnections result = new AssetConnections();
        for ( AssetConnection assetConnection : this ) {
            if ( assetConnection.isProvisioning() ) {
                result.add( assetConnection );
            }
        }
        return result;
    }

    public AssetConnections producing() {
        AssetConnections result = new AssetConnections();
        for ( AssetConnection assetConnection : this ) {
            if ( assetConnection.isProducing() ) {
                result.add( assetConnection );
            }
        }
        return result;
    }

    public AssetConnections demanding() {
        AssetConnections result = new AssetConnections();
        for ( AssetConnection assetConnection : this ) {
            if ( assetConnection.isDemanding() ) {
                result.add( assetConnection );
            }
        }
        return result;
    }

    public AssetConnections using() {
        AssetConnections result = new AssetConnections();
        for ( AssetConnection assetConnection : this ) {
            if ( assetConnection.isUsing() ) {
                result.add( assetConnection );
            }
        }
        return result;
    }

    public AssetConnections stocking() {
        AssetConnections result = new AssetConnections();
        for ( AssetConnection assetConnection : this ) {
            if ( assetConnection.isStocking() ) {
                result.add( assetConnection );
            }
        }
        return result;
    }

    public AssetConnections about( MaterialAsset materialAsset ) {
        AssetConnections result = new AssetConnections();
        for ( AssetConnection assetConnection : this ) {
            if ( materialAsset.narrowsOrEquals( assetConnection.getAsset() ) ) {
                result.add( assetConnection );
            }
        }
        return result;
    }

    public AssetConnections forwarding() {
        AssetConnections result = new AssetConnections();
        for ( AssetConnection assetConnection : this ) {
            if ( assetConnection.isForwarding() ) {
                result.add( assetConnection );
            }
        }
        return result;
    }

    public void addAll( List<AssetConnection> assetConnectionList ) {
        for ( AssetConnection assetConnection : assetConnectionList ) {
            add( assetConnection );
        }
    }

    public AssetConnection first() {
          return isEmpty() ? null : getAll().get( 0 );
    }

    public boolean forwardsRequestFor( MaterialAsset asset ) {
        return !this.about( asset ).demanding().forwarding().isEmpty();
    }
}
