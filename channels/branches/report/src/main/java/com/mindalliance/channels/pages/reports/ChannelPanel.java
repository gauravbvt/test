// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.query.PlanService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * A table of channels for a given entity.
 */
public class ChannelPanel extends Panel {

    public ChannelPanel( String id, PlanService service, Specable specable ) {
        super( id );

        List<Channel> others = specable == null ? new ArrayList<Channel>()
                               : service.findAllChannelsFor( new ResourceSpec( specable ) );

        List<Channel> phones = select( getType( "phone", service.getPlanManager() ), others );
        List<Channel> emails = select( getType( "email", service.getPlanManager() ), others );
        others.removeAll( phones );
        others.removeAll( emails );

        add(
            new ListView<Channel>( "phones", phones ) {
                @Override
                protected void populateItem( ListItem<Channel> item ) {
                    Channel channel = item.getModelObject();
                    item.add(
                        new Label( "type", channel.getMedium().getName() ),
                        new Label( "value", channel.getAddress() )
                    );
                }
            },

            new ListView<Channel>( "emails", emails ) {
                @Override
                protected void populateItem( ListItem<Channel> item ) {
                    String address = item.getModelObject().getAddress();
                    item.add(
                        new Label( "email", address )
                            .add( new AttributeModifier( "href", true,
                                                         new Model<String>( address ) ) )
                    );
                }
            },

            new ListView<Channel>( "others", others ) {
                @Override
                protected void populateItem( ListItem<Channel> item ) {
                    Channel channel = item.getModelObject();
                    item.add(
                        new Label( "label", channel.getMedium().getName() ),
                        new Label( "address", channel.getAddress() )
                    );
                }
            }
        );


    }

    private static List<Channel> select( TransmissionMedium type, List<Channel> channels ) {

        List<Channel> result = new ArrayList<Channel>();
        for ( Channel channel : channels ) {
            if ( channel.getMedium().narrowsOrEquals( type, null ) )
                result.add( channel );
        }

        return result;
    }

    private static TransmissionMedium getType( String type, PlanManager planManager ) {
        for ( TransmissionMedium medium : planManager.getBuiltInMedia() )
            if ( type.equalsIgnoreCase( medium.getName() ) )
                return medium;

        return null;
    }
}
