package com.mindalliance.channels.pages.reports;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.Collator;

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
        List<Channel> channels = channelable.allChannels();
        Collections.sort( channels, new Comparator<Channel>() {
            /** {@inheritDoc} */
            public int compare( Channel channel1, Channel channel2 ) {
                int comp = Collator.getInstance().compare(
                        channel1.getMedium().getName(),
                        channel2.getMedium().getName() );
                if ( comp == 0 ) {
                    return Collator.getInstance().compare(
                            channel1.getAddress(),
                            channel2.getAddress() );
                } else {
                    return comp;
                }
            }
        } );
        add( new ListView<Channel>( "channels", channels ) {
            protected void populateItem( ListItem<Channel> item ) {
                Channel channel = item.getModelObject();
                Label addressLabel = new Label( "address", channel.getAddress() );
                item.add( addressLabel );
                addressLabel.add( new AttributeModifier(
                        "class",
                        true,
                        new Model<String>( channel.getMedium().getName() ) ) );
            }
        } );
    }


}
