package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.TransmissionMedium;
import org.apache.commons.lang.WordUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The channels-only banner.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 6, 2009
 * Time: 10:55:29 AM
 */
public class ChannelsBannerPanel extends Panel {

    /**
     * A resource spec.
     */
    private ResourceSpec spec;

    /**
     * Restrict shown media to these. If null, show everything.
     */
    private final Set<TransmissionMedium> unicasts;

    /**
     * Broadcast channels to include.
     */
    private final Collection<Channel> broadcasts;

    @SpringBean
    private QueryService queryService;

    public ChannelsBannerPanel(
            String id, ResourceSpec spec, Set<TransmissionMedium> unicasts, Collection<Channel> broadcasts ) {
        super( id, new Model<ResourceSpec>( spec ) );
        this.spec = spec;
        this.unicasts = unicasts;
        this.broadcasts = broadcasts;
        init();
    }

    private void init() {
        add( new ListView<Channel>( "channels", getChannels() ) {
            @Override
            protected void populateItem( ListItem<Channel> item ) {
                Channel channel = item.getModelObject();
                TransmissionMedium medium = channel.getMedium();
                String address = medium.isUnknown() ? "" : channel.getAddress();
                Label mediumLabel = new Label( "medium",
                        medium.isUnknown()
                                ? channel.toString()
                                : address.isEmpty()
                                ? channel.toString()
                                : medium.toString() + ": " );

                Label addressLabel = new Label( "address", address );

                mediumLabel.add( new AttributeModifier(
                        "class",
                        true,
                        new Model<String>( medium.isUnicast() ? "unicast" : "broadcast" ) ) );
                addressLabel.add(
                        new AttributeModifier(
                                "class",
                                true,
                                new Model<String>( WordUtils.capitalize(
                                        medium.getName() ).replaceAll( "\\s", "" ) ) ) );

                item.add( mediumLabel );
                item.add( addressLabel );
            }
        } );
    }

    private List<Channel> getChannels() {
        List<Channel> result = new ArrayList<Channel>();
        List<Channel> manualChannels = queryService.findAllChannelsFor( spec );

        if ( unicasts == null )
            result.addAll( manualChannels );
        else
            for ( Channel c : manualChannels )
                if ( unicasts.contains( c.getMedium() ) )
                    result.add( c );
                else if ( broadcasts.contains( c ) )
                    result.add( c );

        if ( result.isEmpty() )
            result.add( Channel.Unknown );
        return result;
    }
}
