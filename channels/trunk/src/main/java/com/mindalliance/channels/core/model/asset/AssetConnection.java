package com.mindalliance.channels.core.model.asset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/14
 * Time: 4:25 PM
 */
public class AssetConnection implements Serializable {


    public static final String CONSUMING = "consuming";
    public static final String CRITICAL = "critical";

    private static final String DEMANDING_LABEL = "requests";
    private static final String PRODUCING_LABEL = "produces";
    private static final String PROVISIONING_LABEL = "provisions";
    private static final String STOCKING_LABEL = "stocks";
    private static final String USING_LABEL = "uses";

    public enum Type {
        Demanding,
        Producing,
        Provisioning,
        Stocking,
        Using;

        public static Type fromLabel( String label ) {
            return label.equals( DEMANDING_LABEL )
                    ? Type.Demanding
                    : label.equals( PRODUCING_LABEL )
                    ? Type.Producing
                    : label.equals( PROVISIONING_LABEL )
                    ? Type.Provisioning
                    : label.equals( STOCKING_LABEL )
                    ? Type.Stocking
                    : label.equals( USING_LABEL )
                    ? Type.Using
                    : null;
        }


    }

    private MaterialAsset asset = MaterialAsset.UNKNOWN;

    private Type type;

    private Map<String, String> properties = new HashMap<String, String>();

    public AssetConnection() {
    }

    public AssetConnection( Type type, MaterialAsset asset ) {
        this.type = type;
        this.asset = asset;
    }

    public static String getLabelFor( Type type ) {
        return type == null
                ? ""
                : type == Type.Demanding
                ? DEMANDING_LABEL
                : type == Type.Producing
                ? PRODUCING_LABEL
                : type == Type.Provisioning
                ? PROVISIONING_LABEL
                : type == Type.Stocking
                ? STOCKING_LABEL
                : type == Type.Using
                ? USING_LABEL
                : "?";
    }

    public static List<String> getTypeLabelsChoicesFor( AssetConnectable connectable ) {
        List<String> choices = new ArrayList<String>();
        if ( connectable.isCanBeAssetDemand() )
            choices.add( AssetConnection.getLabelFor( Type.Demanding ) );
        if ( connectable.isCanProduceAssets() )
            choices.add( AssetConnection.getLabelFor( Type.Producing ) );
        if ( connectable.isCanProvisionAssets() )
            choices.add( AssetConnection.getLabelFor( Type.Provisioning ) );
        if ( connectable.isCanStockAssets() )
            choices.add( AssetConnection.getLabelFor( Type.Stocking ) );
        if ( connectable.isCanUseAssets() )
            choices.add( AssetConnection.getLabelFor( Type.Using ) );
        Collections.sort( choices );
        return choices;
    }


    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public MaterialAsset getAsset() {
        return asset;
    }

    public void setAsset( MaterialAsset asset ) {
        this.asset = asset;
    }

    public Map<String, String> getProperties() {
        return properties == null ? new HashMap<String, String>() : properties;
    }

    public void setProperties( Map<String, String> properties ) {
        this.properties = properties;
    }

    public void setProperty( String name, String value ) {
        if ( properties == null ) properties = getProperties();
        properties.put( name, value );
    }

    public void removeProperty( String name ) {
        if ( properties != null ) {
            properties.remove( name );
        }
    }

    public String getProperty( String name ) {
        return getProperties().get( name );
    }

    public boolean hasProperty( String name ) {
        return getProperties().containsKey( name );
    }

    public boolean isProducing() {
        return type != null && type == Type.Producing;
    }

    public boolean isDemanding() {
        return type != null &&type == Type.Demanding;
    }

    public boolean isStocking() {
        return type != null &&type == Type.Stocking;
    }

    public boolean isUsing() {
        return type != null &&type == Type.Using;
    }

    public boolean isProvisioning() {
        return type != null &&type == Type.Provisioning;
    }

    public String getTypeLabel() {
        return getType() == null ? null : getLabelFor( getType() );
    }

    public void setTypeLabel( String typeLabel ) {
        type = Type.fromLabel( typeLabel );
    }

    public void setConsuming( boolean val ) {
        if ( val )
            setProperty( CONSUMING, "true" );
        else
            removeProperty( CONSUMING );
    }

    public boolean isConsuming() {
        return hasProperty( CONSUMING );
    }

    public void setCritical( boolean val ) {
        if ( val )
            setProperty( CRITICAL, "true" );
        else
            removeProperty( CRITICAL );
    }

    public boolean isCritical() {
        return hasProperty( CRITICAL );
    }

    ////////////

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof AssetConnection ) {
            AssetConnection other = (AssetConnection) object;
            return type != null && type == other.getType()
                    && asset.equals( other.getAsset() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if ( type != null) hash = hash * 31 + type.hashCode();
        hash = hash * 31 + asset.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getTypeLabel() );
        if ( asset.isType() ) {
            sb.append( " assets of type \"" );
        } else {
            sb.append( " asset \"" );
        }
        sb.append( asset.getName() ).append( "\"" );
        return sb.toString();
    }

}
