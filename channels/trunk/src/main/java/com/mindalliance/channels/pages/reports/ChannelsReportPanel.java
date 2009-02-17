package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Channelable;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
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
        List<Channel> channels = channelable.allChannels();
        Collections.sort( channels, new Comparator<Channel>() {
            /** {@inheritDoc} */
            public int compare( Channel o1, Channel o2 ) {
                int comp = Collator.getInstance().compare(
                        o1.getMedium().getName(),
                        o2.getMedium().getName() );
                if ( comp == 0 ) {
                    return Collator.getInstance().compare(
                            o1.getAddress(),
                            o2.getAddress() );
                } else {
                    return comp;
                }
            }
        } );
        add( new ListView<Channel>( "channels", channels ) {
            @Override
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
