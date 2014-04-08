package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.checklist.AssetProvisionedData;
import com.mindalliance.channels.api.procedures.checklist.AssetsProvisionedData;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/27/14
 * Time: 5:49 PM
 */
public class AssetsProvisionedDataPanel extends AbstractDataPanel {

    private AssetsProvisionedData assetsProvisioned;

    public AssetsProvisionedDataPanel( String id, AssetsProvisionedData assetsProvisioned, ProtocolsFinder finder ) {
        super( id, finder );
        this.assetsProvisioned = assetsProvisioned;
        init();
    }

    private void init() {
        Map<MaterialAsset, List<AssetProvisionedData>> incomingAssets = assetsProvisioned.mapAssetsProvisioned( true );
        Map<MaterialAsset, List<AssetProvisionedData>> outgoingAssets = assetsProvisioned.mapAssetsProvisioned( false );
        addProvisionedAssets( "incoming", incomingAssets );
        addProvisionedAssets( "outgoing", outgoingAssets );
    }

    private void addProvisionedAssets( String id, final Map<MaterialAsset, List<AssetProvisionedData>> assetsProvisionedMap ) {
        WebMarkupContainer provisionedContainer = new WebMarkupContainer( id );
        List<MaterialAsset> provisionedAssets = new ArrayList<MaterialAsset>( assetsProvisionedMap.keySet() );
        Collections.sort( provisionedAssets, new Comparator<MaterialAsset>() {
            @Override
            public int compare( MaterialAsset ma1, MaterialAsset ma2 ) {
                return ma1.getName().compareTo( ma2.getName() );
            }
        } );
        ListView<MaterialAsset> provisionedAssetListView = new ListView<MaterialAsset>(
                "assets",
                provisionedAssets
        ) {
            @Override
            protected void populateItem( ListItem<MaterialAsset> item ) {
                MaterialAsset asset = item.getModelObject();
                String assetName = asset.getName();
                item.add( new Label(
                        "asset",
                        assetName ) );
                item.add( makeContactsListView( assetsProvisionedMap.get( asset)));
            }
        };
        provisionedContainer.add( provisionedAssetListView );
        provisionedContainer.setVisible( !provisionedAssets.isEmpty() );
        add( provisionedContainer );
    }

    private ListView<ContactData> makeContactsListView( List<AssetProvisionedData> assetProvisionedDataList ) {
        Set<ContactData> contacts = new HashSet<ContactData>(  );
        for ( AssetProvisionedData assetProvisionedData : assetProvisionedDataList ) {
            contacts.addAll( assetProvisionedData.getContacts() );
        }
        return new ListView<ContactData>(
                "contacts",
                new ArrayList<ContactData>( contacts )
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                item.add( new ContactLinkPanel( "contact", item.getModelObject(), getFinder() ) );
            }
        };
    }

}
