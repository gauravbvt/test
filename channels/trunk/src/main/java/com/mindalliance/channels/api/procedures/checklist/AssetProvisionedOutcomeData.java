package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.model.checklist.AssetProvisionedOutcome;
import com.mindalliance.channels.core.model.checklist.Outcome;

import javax.xml.bind.annotation.XmlElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/27/14
 * Time: 11:14 AM
 */
public class AssetProvisionedOutcomeData extends OutcomeData {

    private long assetId;

    public AssetProvisionedOutcomeData() {
        //required
    }

    public AssetProvisionedOutcomeData( Outcome outcome, String serverUrl, CommunityService communityService, ChannelsUser user ) {
        super( outcome, serverUrl, communityService, user );
    }

    @Override
    protected void initData( Outcome outcome, String serverUrl, CommunityService communityService, ChannelsUser user ) {
        super.initData( outcome, serverUrl, communityService, user );
        MaterialAsset asset = communityService.resolveAsset( ((AssetProvisionedOutcome)getOutcome()).getProvisionedAsset() );
        assetId = asset.getId();
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

    @XmlElement
    public Long getAssetId() {
        return assetId;
    }

    @Override
    public Set<Long> allAssetIds() {
        Set<Long> ids = new HashSet<Long>(  );
        ids.add( assetId );
        return ids;
    }


}
