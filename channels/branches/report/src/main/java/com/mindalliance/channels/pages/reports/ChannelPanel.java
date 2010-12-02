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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A table of channels for a given entity.
 */
public class ChannelPanel extends Panel {
    public enum Type { OTHER, PHONE, EMAIL }

    public ChannelPanel( String id, PlanService service, Specable specable ) {
        super( id );
        add(
            new GridView<ChannelWrapper>( "channels",
                    new ListDataProvider<ChannelWrapper>( getChannels( service, specable ) ) ) {
                @Override
                protected Item<?> newRowItem( String id, int index ) {
                    Item<?> item = super.newRowItem( id, index );
                    item.add( new AttributeModifier( "class", true,
                                new Model<String>( index % 2 == 0 ? "even" : "odd" ) ) );
                    return item;
                }

                @Override
                protected void populateEmptyItem( Item<ChannelWrapper> item ) {
                    item.add(
                        new Label( "label", "" ),
                        new Label( "address", "" )
                    ).setRenderBodyOnly( true );
                }

                @Override
                protected void populateItem( Item<ChannelWrapper> item ) {
                    ChannelWrapper wrapper = item.getModelObject();
                    item.add(
                        new Label( "label", wrapper.getLabel() ),
                        new Label( "address", wrapper.getAddress() )
                    ).setRenderBodyOnly( true );
                }
            }.setColumns( 2 )

        );
    }

    public static List<ChannelWrapper> getChannels( PlanService service, Specable specable ) {
        List<ChannelWrapper> result = new ArrayList<ChannelWrapper>();

        if ( specable != null ) {
            TransmissionMedium phone = getType( "phone", service.getPlanManager() );
            TransmissionMedium email = getType( "email", service.getPlanManager() );

            for ( Channel channel : service.findAllChannelsFor( new ResourceSpec( specable ) ) )
                result.add( new ChannelWrapper( channel,
                                                isOfType( phone, channel ) ? Type.PHONE
                                              : isOfType( email, channel ) ? Type.EMAIL
                                                                           : Type.OTHER ) );
        }

        return result;
    }

    private static boolean isOfType( TransmissionMedium type, Channel channel ) {
        return channel.getMedium().narrowsOrEquals( type, null );
    }

    private static TransmissionMedium getType( String type, PlanManager planManager ) {
        for ( TransmissionMedium medium : planManager.getBuiltInMedia() )
            if ( type.equalsIgnoreCase( medium.getName() ) )
                return medium;

        return null;
    }

    //=======================================
    public static class ChannelWrapper implements Serializable {
        private Channel channel;
        private Type type;

        public ChannelWrapper( Channel channel, Type type ) {
            this.channel = channel;
            this.type = type;
        }

        public Type getType() {
            return type;
        }

        public String getLabel() {
            TransmissionMedium medium = channel.getMedium();
            return medium == null ? "Other" : medium.getLabel();
        }

        public String getAddress() {
            return channel.getAddress();
        }
    }
}
