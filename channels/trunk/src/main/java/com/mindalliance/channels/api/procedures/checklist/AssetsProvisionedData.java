package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.AbstractProcedureElementData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.MaterialAsset;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Asset received from or sent in context of a checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/27/14
 * Time: 3:51 PM
 */
@XmlType( propOrder = {"assetProvisionings"} )
public class AssetsProvisionedData extends AbstractProcedureElementData {

    private CommunityCommitments benefitingCommitments;
    private CommunityCommitments committingCommitments;
    private List<AssetProvisionedData> assetProvisionedDataList;

    public AssetsProvisionedData() {
        // required
    }

    public AssetsProvisionedData( String serverUrl,
                                  CommunityAssignment assignment,
                                  CommunityCommitments benefitingCommitments,
                                  CommunityCommitments committingCommitments,
                                  CommunityService communityService,
                                  ChannelsUser user ) {
        super( communityService, assignment, user );
        this.benefitingCommitments = benefitingCommitments;
        this.committingCommitments = committingCommitments;
        initData( serverUrl, communityService, user );
    }

    private void initData( String serverUrl,
                           CommunityService communityService,
                           ChannelsUser user ) {
        assetProvisionedDataList = new ArrayList<AssetProvisionedData>(  );
        for ( CommunityCommitment benefitingCommitment : benefitingCommitments ) {
            Flow sharing = benefitingCommitment.getSharing();
            for ( AssetConnection connection : sharing.getAssetConnections().provisioning() ) {
                AssetProvisionedData assetProvisionedData = new AssetProvisionedData(
                        serverUrl,
                        connection,
                        sharing.isAskedFor()
                         ? benefitingCommitment.getBeneficiary().getCommunityEmployment()
                        : benefitingCommitment.getCommitter().getCommunityEmployment(),
                        sharing.isAskedFor(), // assets out if receiving notification, in if receiving reply (sending request)
                        communityService,
                        user
                );
                assetProvisionedDataList.add( assetProvisionedData );
            }
        }
        for ( CommunityCommitment committingCommitment : committingCommitments ) {
            Flow sharing = committingCommitment.getSharing();
                for ( AssetConnection connection : sharing.getAssetConnections().provisioning() ) {
                    AssetProvisionedData assetProvisionedData = new AssetProvisionedData(
                            serverUrl,
                            connection,
                            sharing.isNotification()
                                    ? committingCommitment.getBeneficiary().getCommunityEmployment()
                                    : committingCommitment.getCommitter().getCommunityEmployment(),
                            sharing.isNotification(), // assets in if sending notification, out if sending reply (receiving request)
                            communityService,
                            user
                    );
                    assetProvisionedDataList.add( assetProvisionedData );
            }
        }

    }

    @XmlElement( name = "assetProvisioning" )
    public List<AssetProvisionedData> getAssetProvisionings() {
        return assetProvisionedDataList;
    }

    public Set<Long> allAssetIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( AssetProvisionedData assetProvisionedData : assetProvisionedDataList ) {
            ids.add( assetProvisionedData.getAssetId() );
        }
        return ids;
    }

    public boolean isEmpty() {
        return assetProvisionedDataList.isEmpty();
    }

    public Set<ContactData> allContacts() {
        Set<ContactData> allContacts = new HashSet<ContactData>();
        for ( AssetProvisionedData assetProvisionedData : assetProvisionedDataList ) {
            allContacts.addAll( assetProvisionedData.getContacts() );
        }
        return allContacts;
    }

    public Map<MaterialAsset, List<AssetProvisionedData>> mapAssetsProvisioned( boolean incoming ) {
        Map<MaterialAsset, List<AssetProvisionedData>> map = new HashMap<MaterialAsset, List<AssetProvisionedData>>();
        for ( AssetProvisionedData assetProvisionedData : assetProvisionedDataList ) {
            if ( assetProvisionedData.isAssetIncoming() == incoming ) {
                MaterialAsset asset = assetProvisionedData.asset();
                if ( !map.containsKey( asset ) ) {
                    map.put( asset, new ArrayList<AssetProvisionedData>() );
                }
                map.get( asset ).add( assetProvisionedData );
            }
        }
        return map;
    }
}
