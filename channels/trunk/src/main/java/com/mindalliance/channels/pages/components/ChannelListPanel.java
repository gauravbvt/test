package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An editable list of channels
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 2, 2009
 * Time: 7:25:50 PM
 */
public class ChannelListPanel extends Panel {

    private final Channelable channelable;

    public ChannelListPanel( String id, IModel<Channelable> model ) {
        super( id, model );
        this.channelable = model.getObject();
        add( new ListView<Wrapper>( "channels", getWrappedChannels( model ) ) {
            protected void populateItem( ListItem<Wrapper> item ) {
                final Wrapper wrapper = item.getModelObject();
                item.add( new DropDownChoice<Medium>(
                        "medium",
                        new PropertyModel<Medium>( wrapper, "medium" ),
                        Project.service().getMedia(),
                        new IChoiceRenderer<Medium>() {
                            public Object getDisplayValue( Medium medium ) {
                                return medium == null ? "Select a medium" : medium.getName();
                            }

                            public String getIdValue( Medium medium, int index ) {
                                return Integer.toString( index );
                            }
                        }
                ) );
                TextField<String> addressField = new TextField<String>(
                        "address",
                        new PropertyModel<String>( wrapper, "address" ) );
                item.add( addressField );
                flagIfInvalid( addressField, wrapper );
                WebMarkupContainer deleteSpan = new WebMarkupContainer( "deleteSpan" );
                CheckBox deleteCheckBox = new CheckBox(
                        "delete",
                        new PropertyModel<Boolean>( wrapper, "markedForDeletion" ) );
                deleteSpan.setVisible( !wrapper.isMarkedForAddition() );
                deleteSpan.add( deleteCheckBox );
                item.add( deleteSpan );
            }
        } );
    }

    private List<Wrapper> getWrappedChannels( IModel<Channelable> model ) {
        final List<Wrapper> list = new ArrayList<Wrapper>();
        for ( Channel channel : model.getObject().getChannels() ) {
            list.add( new Wrapper( channel ) );
        }
        // To-be-added channel if medium is set
        list.add( new Wrapper() );
        return list;
    }

    private void flagIfInvalid( TextField<String> addressField, Wrapper wrapper ) {
        if ( !wrapper.isMarkedForAddition() && !wrapper.getChannel().isValid() ) {
            addressField.add(
                    new AttributeModifier(
                            "class", true, new Model<String>( "error" ) ) );              // NON-NLS
            addressField.add(
                    new AttributeModifier(
                            "title", true, new Model<String>( "Not valid" ) ) );                // NON-NLS
        }
    }


    /**
     * A wrapper to keep track of the deletion state of channel.
     */
    private final class Wrapper implements Serializable {

        /**
         * The underlying channel.
         */
        private Channel channel;

        /**
         * True if user marked item for deletion.
         */
        private boolean markedForDeletion = false;
        /**
         * True when is to be added
         */
        private boolean markedForAddition = false;

        private Wrapper( Channel channel ) {
            this.channel = channel;
        }

        private Wrapper() {
            this.channel = new Channel();
            markedForAddition = true;
        }

        public Channel getChannel() {
            return channel;
        }

        public boolean isMarkedForDeletion() {
            return markedForDeletion;
        }

        public boolean isMarkedForAddition() {
            return markedForAddition;
        }

        public void setMarkedForDeletion( boolean markedForDeletion ) {
            this.markedForDeletion = markedForDeletion;
            if ( markedForDeletion ) {
                channelable.removeChannel( channel );
            }
        }

        public Medium getMedium() {
            return channel.getMedium();
        }

        public void setMedium( Medium medium ) {
            channel.setMedium( medium );
            if ( medium != null && markedForAddition ) {
                channelable.addChannel( channel );
            }
        }

        public String getAddress() {
            return channel.getAddress();
        }

        public void setAddress( String address ) {
            channel.setAddress( address.trim() );
        }

    }
}
