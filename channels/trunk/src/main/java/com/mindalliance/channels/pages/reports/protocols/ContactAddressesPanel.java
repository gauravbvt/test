package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.ChannelData;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Contact addresses panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 10:58 PM
 */
public class ContactAddressesPanel extends AbstractDataPanel {

    private List<ChannelData> channelDataList;

    public ContactAddressesPanel( String id, List<ChannelData> channelDataList, ProtocolsFinder finder ) {
        super( id, finder );
        this.channelDataList = channelDataList;
        init();
    }

    private void init() {
        WebMarkupContainer addressesContainer = new WebMarkupContainer( "addressesContainer" );
        add(  addressesContainer );
        addressesContainer.setVisible( !channelDataList.isEmpty() );
        ListView<ChannelData> addressesListView = new ListView<ChannelData>(
                "addresses",
                channelDataList
        ) {
            @Override
            protected void populateItem( ListItem<ChannelData> item ) {
                ChannelData channelsData = item.getModelObject();
                boolean isEmail = channelsData.getMedium().equalsIgnoreCase( "email" );
                // Named address
                Label addressLabel = new Label( "namedAddress", channelsData.getLabel() );
                addressLabel.setVisible( !isEmail );
                item.add( addressLabel );
                // Linked address
                WebMarkupContainer linkedAddress = new WebMarkupContainer( "linkedAddress" );
                if ( isEmail ) {
                    linkedAddress.add( new AttributeModifier( "href", "email:" + channelsData.getAddress() ) );
                }
                linkedAddress.add(  new Label( "address",
                        isEmail ? channelsData.getAddress() :  channelsData.getLabel() ) );
                linkedAddress.setVisible( isEmail );
                item.add( linkedAddress );
            }
        };
        addressesContainer.add(  addressesListView );
    }
}
