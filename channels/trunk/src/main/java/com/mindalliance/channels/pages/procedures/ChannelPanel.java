// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.procedures;

import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.engine.query.PlanService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
                    item.add( newAttribute( "class", index % 2 == 0 ? "even" : "odd" ) );
                    return item;
                }

                @Override
                protected void populateEmptyItem( Item<ChannelWrapper> item ) {
                    item.add(
                        new Label( "label", "" ),
                        new WebMarkupContainer( "address" ).add(
                            new Label( "type", "" ),
                            new Label( "value", "" )
                            )
                    ).setRenderBodyOnly( true );
                }

                @Override
                protected void populateItem( Item<ChannelWrapper> item ) {
                    ChannelWrapper wrapper = item.getModelObject();
                    WebMarkupContainer address = new WebMarkupContainer( "address" );

                    Label value = new Label( "value", wrapper.getAddress() );
                    item.add(
                        new Label( "label", wrapper.getLabel() ),
                        address.add(
                            value.setRenderBodyOnly( wrapper.isOther() ),
                            new Label( "type", wrapper.getCssClass() )
                                .setVisible( wrapper.isPhone() && wrapper.isPhoneType() ) )

                    ).setRenderBodyOnly( true );

                    if ( wrapper.isPhone() ) {
                        value.add( newAttribute( "href", "tel:" + wrapper.getAddress() ) );
                        address.add( newAttribute( "class", "tel" ) );
                    } else if ( wrapper.isEmail() )
                        value.add( newAttribute( "href", "mailto:" + wrapper.getAddress() ),
                                   newAttribute( "class", "email" ) );
                }
            }.setColumns( 2 )
        );
    }

    private static AttributeModifier newAttribute( String name, String value ) {
        return new AttributeModifier( name, true, new Model<String>( value ) );
    }

    public static List<ChannelWrapper> getChannels( PlanService service, Specable specable ) {
        List<ChannelWrapper> result = new ArrayList<ChannelWrapper>();

        if ( specable != null ) {
            TransmissionMedium phone = getType( "phone", service.getPlanManager() );
            TransmissionMedium email = getType( "email", service.getPlanManager() );

            for ( Channel channel : service.findAllChannelsFor( new ResourceSpec( specable ) ) ) {
                TransmissionMedium medium = channel.getMedium();
                result.add( new ChannelWrapper( channel,
                                                isOfType( phone, medium ) ? Type.PHONE
                                              : isOfType( email, medium ) ? Type.EMAIL
                                                                          : Type.OTHER ) );
            }
        }

        return result;
    }

    private static boolean isOfType( TransmissionMedium type, TransmissionMedium medium ) {
        try {
            String name = medium.getName();
            return medium.narrowsOrEquals( type, null )
                || "phone".equalsIgnoreCase( name )
                || CommitmentReportPanel.PhoneType.valueOf( name.toUpperCase() ) != null ;

        } catch ( IllegalArgumentException ignored ) {
            return false;
        }
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

        public boolean isPhone() {
            return Type.PHONE.equals( type );
        }

        public String getCssClass() {
            return channel.getMedium().getLabel().toString().toLowerCase();
        }

        public boolean isPhoneType() {
            try {
                CommitmentReportPanel.PhoneType.valueOf(
                    channel.getMedium().getLabel().toString().toUpperCase() );
                return true;
            } catch ( IllegalArgumentException ignored ) {
                return false;
            }
        }

        public boolean isEmail() {
            return Type.EMAIL.equals( type );
        }

        public boolean isOther() {
            return Type.OTHER.equals( type );
        }
    }
}
