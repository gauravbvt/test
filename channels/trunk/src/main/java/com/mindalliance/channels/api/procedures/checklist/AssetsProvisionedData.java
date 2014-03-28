package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.AbstractProcedureElementData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
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
 * Asset received from or sent in context of a checklist of a task with commitments.
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
        assetProvisionedDataList = new ArrayList<AssetProvisionedData>();
        for ( CommunityCommitment benefitingCommitment : benefitingCommitments ) {
            Flow sharing = benefitingCommitment.getSharing();
            for ( AssetConnection connection : sharing.getAssetConnections().provisioning() ) {
                boolean assetsIncoming = sharing.isAskedFor();
                AssetProvisionedData assetProvisionedData = new AssetProvisionedData(
                        serverUrl,
                        connection,
                        resolveContact( connection, assetsIncoming, benefitingCommitment, true, communityService ),
                        assetsIncoming, // assets out if receiving notification, in if receiving reply (sending request)
                        communityService,
                        user
                );
                if ( !assetProvisionedData.getContacts().isEmpty() )
                    assetProvisionedDataList.add( assetProvisionedData );
            }
        }
        for ( CommunityCommitment committingCommitment : committingCommitments ) {
            Flow sharing = committingCommitment.getSharing();
            for ( AssetConnection connection : sharing.getAssetConnections().provisioning() ) {
                boolean assetsIncoming = sharing.isNotification();
                AssetProvisionedData assetProvisionedData = new AssetProvisionedData(
                        serverUrl,
                        connection,
                        resolveContact( connection, assetsIncoming, committingCommitment, false, communityService ),
                        assetsIncoming, // assets in if sending notification, out if sending reply (receiving request)
                        communityService,
                        user
                );
                if ( !assetProvisionedData.getContacts().isEmpty() )
                    assetProvisionedDataList.add( assetProvisionedData );
                assetProvisionedDataList.add( assetProvisionedData );
            }
        }
    }

    // The contact is the other party in the commitment.
    // If the task is context is benefiting from the commitment, then the other party is the committer.
    // If the task in context is committing, then the other party is the beneficiary.
    private List<CommunityEmployment> resolveContact( AssetConnection connection,
                                                boolean assetIncoming,
                                                CommunityCommitment commitment,
                                                boolean benefiting,
                                                CommunityService communityService ) {
        CommunityAssignment communityAssignment = benefiting
                ? commitment.getCommitter()
                : commitment.getBeneficiary();
        List<CommunityEmployment> communityEmployments = new ArrayList<CommunityEmployment>(  );
        if ( !connection.isForwarding() ) {
            communityEmployments.add( communityAssignment.getCommunityEmployment() );
        } else {
            List<CommunityAssignment> assignments = communityService
                    .resolveForwarding( communityAssignment, connection, assetIncoming ); // can return nothing if no demand is being forwarded
            for ( CommunityAssignment assignment : assignments ) {
                communityEmployments.add( assignment.getCommunityEmployment() );
            }
        }
        return communityEmployments;
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
