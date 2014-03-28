package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.MaterialAsset;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/27/14
 * Time: 4:21 PM
 */
@XmlType( propOrder = {"label", "assetId", "assetIncoming", "contacts"} )
public class AssetProvisionedData implements Serializable {

    private MaterialAsset asset;
    private List<ContactData> contacts;
    private String label;
    private AssetConnection assetConnection;
    private CommunityEmployment contactEmployment;
    private boolean assetIncoming;

    public AssetProvisionedData() {
        // required
    }

    public AssetProvisionedData( String serverUrl,
                                 AssetConnection assetConnection,
                                 CommunityEmployment communityEmployment,
                                 boolean assetIncoming,
                                 CommunityService communityService,
                                 ChannelsUser user ) {
        this.assetConnection = assetConnection;
        contactEmployment = communityEmployment;
        this.assetIncoming = assetIncoming;
        initData( serverUrl, communityService, user );
    }

    private void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        asset = assetConnection.getAsset();
        label = (assetIncoming ? "Obtaining " : "Supplying ") + asset.getName();
        contacts = ContactData.findContactsFromEmployment(
                serverUrl,
                contactEmployment,
                communityService );
    }

    @XmlElement
    public long getAssetId() {
        return asset.getId();
    }

    @XmlElement( name="contact")
    public List<ContactData> getContacts() {
        return contacts;
    }

    @XmlElement
    public String getLabel() {
        return label;
    }

    @XmlElement
    public boolean isAssetIncoming() {
        return assetIncoming;
    }

    public MaterialAsset asset() {
        return asset;
    }
}
