package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
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
    private long flowId; // id of flow demanding the material asset
    private String label = "";
    private String shortLabel = "";

    public AssetProvisioning() {
    }

    public AssetProvisioning( AssetProvisioning assetProvisioning ) {
        assetId = assetProvisioning.getAssetId();
        flowId = assetProvisioning.getFlowId();
    }

    public AssetProvisioning( MaterialAsset assetProvisioned, Flow demandingFlow ) {
        assetId = assetProvisioned.getId();
        flowId = demandingFlow.getId();
    }

    public AssetProvisioning( long assetId, long flowId ) {
        this.assetId = assetId;
        this.flowId = flowId;
    }

    public long getAssetId() {
        return assetId;
    }

    public long getFlowId() {
        return flowId;
    }

    public void setFlowId( long flowId ) {
        this.flowId = flowId;
    }

    public void setAssetId( long assetId ) {
        this.assetId = assetId;
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
        return isFlowValid( checklist ) && isAssetValid( communityService );
    }

    private boolean isFlowValid( Checklist checklist ) {
        Flow flow = getFlow( checklist );
        return flow != null && flow.isSharing( );
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

    public Flow getFlow( Checklist checklist ) {
        Flow flow = checklist.getPart().getSegment().getFlow( flowId );
        return flow != null
                ? flow
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
        Part provisionedPart = getProvisionedPart( checklist );
        Flow flow = getFlow( checklist );
        if ( asset != null && provisionedPart != null && flow != null ) {
            StringBuilder sb = new StringBuilder();
            sb.append( "Deliver \"" )
                    .append( asset.getName() )
                    .append( "\" to \"" )
                    .append( provisionedPart.getTitle() )
                    .append( "\"" );
            if ( !flow.getRestrictions().isEmpty() )
                    sb.append( " (" )
                    .append( flow.getRestrictionString( true ) )
                    .append( ")" );
            label = sb.toString();
            shortLabel = "Provide " + asset.getName();
        } else {
            label = "?";
        }
        return label;
    }

    public Part getProvisionedPart( Checklist checklist ) {
        if ( isFlowValid( checklist ) ) {
            Flow flow = getFlow( checklist );
            if ( flow.isAskedFor() ) {
                return (Part) flow.getTarget();
            } else {
                return (Part) flow.getSource();
            }
        } else {
            return null;
        }
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
            return assetId == other.getAssetId() && flowId == other.getFlowId();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash + 31 * new Long( assetId ).hashCode();
        hash = hash + 31 * new Long( flowId ).hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "Asset " + assetId + " provisioned in response to flow " + flowId;
    }

    public boolean isDefined() {
        return assetId > 0 && flowId > 0;
    }

}
