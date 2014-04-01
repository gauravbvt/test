package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.asset.MaterialAsset;

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
public class AssetSupplyRelationship<T extends Part> extends Relationship {

    private Set<MaterialAsset> assets = new HashSet<MaterialAsset>(  );

    public AssetSupplyRelationship() {
    }

    public AssetSupplyRelationship( T supplier, T supplied ) {
        super( supplier, supplied );
    }

    public Part getSupplier( CommunityService communityService) {
        return (Part)getFromIdentifiable( communityService );
    }

    public Part getSupplied( CommunityService communityService) {
        return (Part)getToIdentifiable( communityService );
    }


    public List<MaterialAsset> getAssets() {
        return new ArrayList<MaterialAsset>(assets);
    }

    public void addAsset( MaterialAsset asset ) {
        assets.add( asset );
    }

    @Override
    public String getUid() {
        return Long.toString( getId() );
    }
}
