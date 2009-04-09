package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    /** Restrict shown media to these. If null, show everything. */
    private Set<Medium> showMedia;

    public ChannelsReportPanel( String id, IModel<Channelable> model, Set<Medium> showMedia ) {
        super( id, model );
        channelable = model.getObject();
        this.showMedia = showMedia;
        init();
    }

    private void init() {
        add( new ListView<Channel>( "channels", getChannels() ) {
            @Override
            protected void populateItem( ListItem<Channel> item ) {
                Channel channel = item.getModelObject();
                Medium medium = channel.getMedium();
                String address = Medium.Other.equals( medium ) ? "" : channel.getAddress();

                Label mediumLabel = new Label( "medium",
                        medium == null    ? ""
                      : Medium.Other.equals( medium ) ? channel.toString()
                      : address.isEmpty() ? channel.toString()
                                          : medium.toString() + ": " );

                Label addressLabel = new Label( "address", address );

                if ( medium != null ) {
                    mediumLabel.add( new AttributeModifier(
                            "class",
                            true,
                            new Model<String>( medium.isUnicast() ? "unicast" : "broadcast" ) ) );
                    addressLabel.add(
                            new AttributeModifier(
                                    "class",
                                    true,
                                    new Model<String>( medium.name() ) ) );
                }

                item.add( mediumLabel );
                item.add( addressLabel );
            }
        } );
    }

    private List<Channel> getChannels() {
        List<Channel> result = new ArrayList<Channel>();
//        List<Channel> manualChannels = channelable.allChannels();
        Project project = (Project) getApplication();
        List<Channel> manualChannels = project.getDqo().findAllCandidateChannelsFor( channelable );

        if ( showMedia == null || showMedia.isEmpty() )
            result.addAll( manualChannels );
        else
            for ( Channel c : manualChannels )
                if ( showMedia.contains( c.getMedium() ) )
                    result.add( c );

        if ( result.isEmpty() )
            result.add( Channel.Unknown );
        return result;
    }
}
