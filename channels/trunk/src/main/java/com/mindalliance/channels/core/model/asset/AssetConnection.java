package com.mindalliance.channels.core.model.asset;

import com.mindalliance.channels.core.model.Mappable;
import com.mindalliance.channels.core.model.ModelObject;

import java.util.ArrayList;
import java.util.Arrays;
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
public class AssetConnection implements Mappable {


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
            String lcLabel = label.toLowerCase();
            return lcLabel.equals( DEMANDING_LABEL )
                    ? Type.Demanding
                    : lcLabel.equals( PRODUCING_LABEL )
                    ? Type.Producing
                    : lcLabel.equals( PROVISIONING_LABEL )
                    ? Type.Provisioning
                    : lcLabel.equals( STOCKING_LABEL )
                    ? Type.Stocking
                    : lcLabel.equals( USING_LABEL )
                    ? Type.Using
                    : null;
        }


    }

    private MaterialAsset asset;

    private Type type = Type.Using;

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

    public static String getStepLabelFor( Type type ) {
        return type == null
                ? ""
                : type == Type.Demanding
                ? "requesting"
                : type == Type.Producing
                ? "producing"
                : type == Type.Provisioning
                ? "providing"
                : type == Type.Stocking
                ? "stocking"
                : type == Type.Using
                ? "using"
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
        if ( type != null )
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
        return type != null && type == Type.Demanding;
    }

    public boolean isStocking() {
        return type != null && type == Type.Stocking;
    }

    public boolean isUsing() {
        return type != null && type == Type.Using;
    }

    public boolean isProvisioning() {
        return type != null && type == Type.Provisioning;
    }

    public String getTypeLabel() {
        return getType() == null ? null : getLabelFor( getType() );
    }

    public String getTypeStepLabel() {
        return getType() == null ? null : getStepLabelFor( getType() );
    }


    public String getDetailedTypeLabel() {
        if ( type == Type.Using ) {
            return isConsuming() && isCritical()
                    ? "requires as well as consumes"
                    : isConsuming()
                    ? "consumes"
                    : isCritical()
                    ? "requires"
                    : getTypeLabel();
        } else {
            return getTypeLabel();
        }
    }

    public String getDetailedTypeStepLabel() {
        if ( type == Type.Using ) {
            return isConsuming() && isCritical()
                    ? "requiring as well as consuming"
                    : isConsuming()
                    ? "consuming"
                    : isCritical()
                    ? "requiring"
                    : getTypeStepLabel();
        } else {
            return getTypeStepLabel();
        }
    }

    public String getStepConditionLabel() {
        if ( type == Type.Using ) {
            StringBuilder sb = new StringBuilder(  );
            boolean isAssetType = getAsset().isType();
            sb.append( isAssetType ? "Assets of type \"" : "Asset \"" )
                    .append( getAsset().getLabel() )
                    .append( isAssetType ? "\" are available" : "\" is available" );
            return sb.toString();
        } else {
            throw new RuntimeException( "Unsupported step condition" );
        }
    }



    public String getStepOutcomeLabel() {
        if ( type == Type.Producing ) {
            StringBuilder sb = new StringBuilder(  );
            sb.append( getAsset().getLabel() )
                    .append( " produced" );
            return sb.toString();
        } else {
            throw new RuntimeException( "Unsupported step outcome" );
        }
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

    public String getLabel( ) {
        StringBuilder sb = new StringBuilder();
        if ( asset.isType() ) {
            sb.append( " assets of type \"" );
        } else {
            sb.append( " asset \"" );
        }
        sb.append( asset.getName() ).append( "\"" );
        return sb.toString();
    }

    /**
     * Convert to a serializable map for copy-paste.
     *
     * @return a map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "type", type.name() );
        map.put( "asset",  Arrays.asList( getAsset().getName(), getAsset().isType() ) );
        map.put( "critical", isCritical() );
        map.put( "consuming", isConsuming() );
        return map;
    }

    /////// MAPPABLE ////////

    @Override
    public void map( Map<String, Object> map ) {
        map.put( "type", type );
        map.put( "asset", asset );
        map.put( "properties", properties );
    }


    ////// OBJECT //////

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof AssetConnection ) {
            AssetConnection other = (AssetConnection) object;
            return type == other.getType()
                    && ModelObject.areEqualOrNull( asset, other.getAsset() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash * 31 + type.hashCode();
        hash = hash * 31 + asset.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getTypeLabel() == null ? "? " : getTypeLabel() );
        if ( getAsset().isType() ) {
            sb.append( " type of assets \"" );
        } else {
            sb.append( " actual asset \"" );
        }
        sb.append( getAsset().getName() );
        return sb.toString();
    }

}
