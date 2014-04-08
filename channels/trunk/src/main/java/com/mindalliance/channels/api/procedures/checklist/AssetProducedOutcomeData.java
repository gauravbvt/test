package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.model.checklist.AssetProducedOutcome;
import com.mindalliance.channels.core.model.checklist.Outcome;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/14
 * Time: 2:21 PM
 */
@XmlType(name = "assetProducedOutcome", propOrder = {"label", "assetId"})
public class AssetProducedOutcomeData extends OutcomeData {

    private long assetId;

    public AssetProducedOutcomeData() {
        //required
    }

    public AssetProducedOutcomeData( Outcome outcome, String serverUrl, CommunityService communityService, ChannelsUser user ) {
        super( outcome, serverUrl, communityService, user );
    }

    @Override
    protected void initData( Outcome outcome, String serverUrl, CommunityService communityService, ChannelsUser user ) {
        super.initData( outcome, serverUrl, communityService, user );
        MaterialAsset asset = communityService.resolveAsset( (( AssetProducedOutcome)getOutcome()).getProducedAsset() );
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
