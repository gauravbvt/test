package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.QueryService;
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
 * Asset supply relationship between parts
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/31/14
 * Time: 2:04 PM
 */
public class AssetSupplyRelationship<T extends ModelObject> extends Relationship {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AssetSupplyRelationship.class );

    private Set<MaterialAsset> assets = new HashSet<MaterialAsset>();

    private Set<Flow.Restriction> restrictions = new HashSet<Flow.Restriction>(  );

    public AssetSupplyRelationship() {
    }

    public AssetSupplyRelationship( T supplier, T supplied ) {
        super( supplier, supplied );
    }

    public AssetSupplyRelationship( T supplier, T supplied, Set<Flow.Restriction> restrictions ) {
        super( supplier, supplied );
        this.restrictions = new HashSet<Flow.Restriction>( restrictions );
    }


    @SuppressWarnings( "unchecked" )
    public T getSupplier( QueryService queryService ) {
        try {
            // TODO - Should be find( Identifiable.class,...)
            return (T) queryService.find( ModelObject.class, getFromIdentifiable() );
        } catch ( NotFoundException e ) {
            LOG.warn( "From-identifiable not found", e );
            return null;
        }
    }

    @SuppressWarnings( "unchecked" )
    public T getSupplied( QueryService queryService ) {
        try {
            // TODO - Should be find( Identifiable.class,...)
            return (T) queryService.find( ModelObject.class, getToIdentifiable() );
        } catch ( NotFoundException e ) {
            LOG.warn( "To-identifiable not found", e );
            return null;
        }
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
        return super.equals( obj )
                && CollectionUtils.isEqualCollection( restrictions, ( (AssetSupplyRelationship<T>) obj ).getRestrictions() );
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        int subHash = 0;
        for ( Flow.Restriction restriction : getRestrictions() ) {
            subHash = subHash + restriction.hashCode(); // order does not matter
        }
        hash = hash + 31 * subHash;
        return hash;
    }

    @SuppressWarnings( "unchecked" )
    public List<String> getRestrictionLabels(  ) {
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
