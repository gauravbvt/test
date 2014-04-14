package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Asset supply relationship between assignments
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/31/14
 * Time: 2:04 PM
 */
public class AssetSupplyRelationship extends Relationship<Assignment> {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AssetSupplyRelationship.class );

    private Assignment supplier;

    private Assignment supplied;

    private Set<MaterialAsset> assets = new HashSet<MaterialAsset>();

    private Set<Flow.Restriction> restrictions = new HashSet<Flow.Restriction>();

    public AssetSupplyRelationship() {
    }

    public AssetSupplyRelationship( Assignment supplier, Assignment supplied ) {
        super( supplier, supplied );
        this.supplier = supplier;
        this.supplied = supplied;
    }

    public AssetSupplyRelationship( Assignment supplier, Assignment supplied, Set<Flow.Restriction> restrictions ) {
        this( supplier, supplied );
        this.restrictions = new HashSet<Flow.Restriction>( restrictions );
    }


    @SuppressWarnings( "unchecked" )
    public Assignment getSupplier() {
        return supplier;
    }

    @SuppressWarnings( "unchecked" )
    public Assignment getSupplied() {
        return supplied;
    }


    public List<MaterialAsset> getAssets() {
        return new ArrayList<MaterialAsset>( assets );
    }

    public void addAsset( MaterialAsset asset ) {
        assets.add( asset );
    }

    public void addRestriction( Flow.Restriction restriction ) {
        restrictions.add( restriction );
    }

    public Set<Flow.Restriction> getRestrictions() {
        return restrictions;
    }

    @Override
    public String getUid() {
        return Long.toString( getId() );
    }

    public boolean isAssetSupplied( final MaterialAsset asset ) {
        return CollectionUtils.exists(
                getAssets(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (MaterialAsset) object ).narrowsOrEquals( asset );
                    }
                }
        );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof AssetSupplyRelationship ) {
            AssetSupplyRelationship other = (AssetSupplyRelationship) obj;
            return supplier.equals( other.getSupplier() )
                    && supplied.equals( ( other ).getSupplied() )
                    && CollectionUtils.isEqualCollection( assets, other.getAssets() )
                    && CollectionUtils.isEqualCollection( restrictions, ( (AssetSupplyRelationship) obj ).getRestrictions() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash + 31 * supplier.hashCode();
        hash = hash + 31 * supplied.hashCode();
        for ( Flow.Restriction restriction : getRestrictions() ) {
            hash = hash + restriction.hashCode(); // order does not matter
        }
        for ( MaterialAsset asset: getAssets() ) {
            hash = hash + asset.hashCode(); // order does not matter
        }
        return hash;
    }

    @SuppressWarnings( "unchecked" )
    public List<String> getRestrictionLabels() {
        return (List<String>) CollectionUtils.collect(
                getRestrictions(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (Flow.Restriction) input ).getLabel( true );
                    }
                }
        );

    }

}
