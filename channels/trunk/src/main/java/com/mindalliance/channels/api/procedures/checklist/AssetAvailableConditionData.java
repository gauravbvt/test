package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.AssetAvailableCondition;
import com.mindalliance.channels.core.model.checklist.Condition;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

/**
 * Web service data for asset available condition.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/14
 * Time: 2:22 PM
 */
@XmlType( propOrder={"label", "assetId"})
public class AssetAvailableConditionData extends ConditionData {

    private long assetId;

    public AssetAvailableConditionData() {
        // required
    }

    public AssetAvailableConditionData( Condition condition,
                                        String serverUrl,
                                        CommunityService communityService,
                                        ChannelsUser user ) {
        super( condition, serverUrl, communityService, user );
    }

    @Override
    protected void initData( Condition condition, String serverUrl, CommunityService communityService, ChannelsUser user ) {
        super.initData( condition, serverUrl, communityService, user );
        assetId = ((AssetAvailableCondition) getCondition()).getAssetAvailable().getId();
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
