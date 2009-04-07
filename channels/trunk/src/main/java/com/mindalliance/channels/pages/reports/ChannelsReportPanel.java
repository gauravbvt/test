package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Medium;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 6, 2009
 * Time: 10:55:29 AM
 */
public class ChannelsReportPanel extends Panel {
    /**
     * A Channelable
     */
    private Channelable channelable;

    public ChannelsReportPanel( String id, IModel<Channelable> model ) {
        super( id, model );
        channelable = model.getObject();
        init();
    }

    private void init() {
        List<Channel> channels = new ArrayList<Channel>( channelable.allChannels() );
        if ( channels.isEmpty() )
            channels.add( Channel.Unknown );

        add( new ListView<Channel>( "channels", channels ) {
            @Override
            protected void populateItem( ListItem<Channel> item ) {
                Channel channel = item.getModelObject();
                Medium medium = channel.getMedium();
                Label mediumLabel = new Label( "medium", medium == null ? ""
                        : medium.toString() + ": " );
                item.add( mediumLabel );
                Label addressLabel = new Label( "address", channel.getAddress() );
                if ( medium != null ) {
                    mediumLabel.add( new AttributeModifier(
                            "class",
                            true,
                            new Model<String>( medium.isUnicast() ? "unicast" : "broadcast" ) ) );
                    addressLabel.add(
                            new AttributeModifier(
                                    "class",
                                    true,
                                    new Model<String>( medium.getLabel() ) ) );
                }
                item.add( addressLabel );
            }
        } );
    }


}
