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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<CommunityEmployment> contactEmployments;
    private boolean assetIncoming;

    public AssetProvisionedData() {
        // required
    }

    public AssetProvisionedData( String serverUrl,
                                 AssetConnection assetConnection,
                                 List<CommunityEmployment> communityEmployments,
                                 boolean assetIncoming,
                                 CommunityService communityService,
                                 ChannelsUser user ) {
        this.assetConnection = assetConnection;
        contactEmployments = communityEmployments;
        this.assetIncoming = assetIncoming;
        initData( serverUrl, communityService, user );
    }

    private void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        asset = communityService.resolveAsset( assetConnection.getAsset() );
        label = (assetIncoming ? "Obtaining " : "Supplying ") + asset.getName();
        Set<ContactData> allContacts = new HashSet<ContactData>(  );
        for ( CommunityEmployment contactEmployment : contactEmployments ) {
            List<ContactData> employmentContacts = ContactData.findContactsFromEmployment(
                    serverUrl,
                    contactEmployment,
                    communityService );
            allContacts.addAll( employmentContacts );
        }
        contacts = new ArrayList<ContactData>( allContacts );
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
