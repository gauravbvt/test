package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A material asset provisioned to a task in the same segment.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/14
 * Time: 10:27 AM
 */
public class AssetProvisioning extends AbstractChecklistElement {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( AssetProvisioning.class );

    public static final String REF_PREFIX = "asset_provided|";

    private long assetId = 0;
    private long partId = 0;
    private String label = "";
    private String shortLabel = "";

    public AssetProvisioning() {
    }

    public AssetProvisioning( AssetProvisioning assetProvisioning ) {
        assetId = assetProvisioning.getAssetId();
        partId = assetProvisioning.getPartId();
    }

    public AssetProvisioning( MaterialAsset assetProvisioned, Part provisionedPart ) {
        assetId = assetProvisioned.getId();
        partId = provisionedPart.getId();
    }

    public AssetProvisioning( long assetId, long partId ) {
        this.assetId = assetId;
        this.partId = partId;
    }

    public long getAssetId() {
        return assetId;
    }

    public long getPartId() {
        return partId;
    }

    public void setAssetId( long assetId ) {
        this.assetId = assetId;
    }

    public void setPartId( long partId ) {
        this.partId = partId;
    }

    @Override
    public boolean isAssetProvisioned() {
        return true;
    }

    @Override
    public AssetProvisioning getAssetProvisioning() {
        return this;
    }

    public boolean isValid( Checklist checklist, CommunityService communityService ) {
        return isPartValid( checklist ) && isAssetValid( communityService );
    }

    private boolean isPartValid( Checklist checklist ) {
        return getPart( checklist ) != null;
    }

    private boolean isAssetValid( CommunityService communityService ) {
        return getAsset( communityService ) != null;
    }

    public MaterialAsset getAsset( CommunityService communityService ) {
        try {
            return communityService.find( MaterialAsset.class, assetId );
        } catch ( NotFoundException e ) {
            LOG.warn( "Asset not found at " + assetId );
            return null;
        }
    }

    public Part getPart( Checklist checklist ) {
        Node node = checklist.getPart().getSegment().getNode( partId );
        return node != null
                ? (Part) node
                : null;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public String getShortLabel() {
        return shortLabel == null || shortLabel.isEmpty() ? "?" : shortLabel;
    }

    public String makeLabel( Checklist checklist, CommunityService communityService ) {
        MaterialAsset asset = getAsset( communityService );
        Part part = getPart( checklist );
        if ( asset != null && part != null ) {
            label= "Provide \""
                    + asset.getName()
                    + "\" to \""
                    + part.getTitle() + "\"";
            shortLabel = "Provide " + asset.getName();
        } else {
            label = "?";
        }
        return label;
    }

    @Override
    public String getRef() {
        return REF_PREFIX + getUid();
    }

    public String getLabel( Checklist checklist, CommunityService communityService ) {
        if ( label.isEmpty() ) {
            makeLabel( checklist, communityService );
        }
        return label;
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof AssetProvisioning ) {
            AssetProvisioning other = (AssetProvisioning) object;
            return assetId == other.getAssetId() && partId == other.getPartId();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash + 31 * new Long( assetId ).hashCode();
        hash = hash + 31 * new Long( partId ).hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "Asset " + assetId + " provisioned to task " + partId;
    }

    public boolean isDefined() {
        return assetId > 0 && partId > 0;
    }

}
