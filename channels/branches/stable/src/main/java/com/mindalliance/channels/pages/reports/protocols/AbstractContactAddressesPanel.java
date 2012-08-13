package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.ChannelData;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/28/12
 * Time: 5:30 PM
 */
public class AbstractContactAddressesPanel extends AbstractDataPanel {

    private List<ChannelData> channelDataList;

    public AbstractContactAddressesPanel( String id, List<ChannelData> channelDataList, ProtocolsFinder finder ) {
        super( id, finder );
        this.channelDataList = channelDataList;
    }

    public List<ChannelData> getChannelDataList() {
        return channelDataList;
    }

    protected void init() {
        List<ChannelData> channelDataList = getChannelDataList();
        WebMarkupContainer addressesContainer = new WebMarkupContainer( "addressesContainer" );
        add(  addressesContainer );
        addressesContainer.setVisible( !channelDataList.isEmpty() );
        ListView<ChannelData> addressesListView = makeAddressesListView( "addresses" );
        addressesContainer.add(  addressesListView );
    }

    protected ListView<ChannelData> makeAddressesListView( String id ) {
        return new ListView<ChannelData>(
                id,
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
    }
}
