package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * An editable list of channels
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 2, 2009
 * Time: 7:25:50 PM
 */
public class ChannelListPanel extends Panel {
    /**
     * The object which list of channels is being edited
     */
    private final Channelable channelable;

    public ChannelListPanel( String id, IModel<Channelable> model ) {
        super( id, model );
        this.channelable = model.getObject();
        final List<Wrapper> candidates = getWrappedCandidateChannels( model );
        WebMarkupContainer candidatesSpan = new WebMarkupContainer( "candidates-span" );
        add( candidatesSpan );
        ListView<Wrapper> candidatesList = new ListView<Wrapper>( "candidates", candidates ) {
            protected void populateItem( ListItem<Wrapper> item ) {
                final Wrapper wrapper = item.getModelObject();
                item.add( new CheckBox(
                        "include-candidate",
                        new PropertyModel<Boolean>( wrapper, "markedForInclusion" ) ) );
                item.add( new Label( "candidate", wrapper.getChannel().toString() ) );
            }
        };
        candidatesSpan.add( candidatesList );
        candidatesSpan.setVisible( !candidates.isEmpty() );
        final List<Wrapper> setChannels = getWrappedChannels( model );
        add( new ListView<Wrapper>( "channels", setChannels ) {
            protected void populateItem( ListItem<Wrapper> item ) {
                final Wrapper wrapper = item.getModelObject();
                WebMarkupContainer includeSpan = new WebMarkupContainer( "include-span" );
                CheckBox includeCheckBox = new CheckBox(
                        "include",
                        new PropertyModel<Boolean>( wrapper, "markedForInclusion" ) );
                includeSpan.setVisible( !wrapper.isMarkedForCreation() );
                includeSpan.add( includeCheckBox );
                item.add( includeSpan );
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
            }
        } );
        Label instructionsLabel = new Label(
                "instructions",
                "Check to include channel. Uncheck to exclude." );
        add( instructionsLabel );
        instructionsLabel.setVisible( !candidates.isEmpty() || setChannels.size() > 1 );
    }

    private List<Wrapper> getWrappedChannels( IModel<Channelable> model ) {
        final List<Wrapper> list = new ArrayList<Wrapper>();
        List<Channel> setChannels = model.getObject().getChannels();
        for ( Channel channel : setChannels ) {
            // wrap channel as already included
            list.add( new Wrapper( channel, true ) );
        }
        // To-be-added channel if medium is set
        list.add( new Wrapper() );
        return list;
    }

    private List<Wrapper> getWrappedCandidateChannels( IModel<Channelable> model ) {
        Set<Channel> candidates = new HashSet<Channel>();
        List<Channelable> channelables = findRelatedChannelables( model.getObject() );
        List<Channel> alreadySetChannels = model.getObject().getChannels();
        // Get all non-redundant, valid candidate channels
        for ( Channelable aChannelable : channelables ) {
            for ( Channel channel : aChannelable.getChannels() ) {
                if ( !alreadySetChannels.contains( channel )
                        && channel.isValid() ) {
                    candidates.add( channel );
                }
            }
        }
        // Wrap them as not marked for inclusion
        List<Wrapper> wrappers = new ArrayList<Wrapper>();
        for ( Channel candidate : candidates ) {
            wrappers.add(
                    new Wrapper(
                            new Channel( candidate.getMedium(), candidate.getAddress() ), false ) );
        }
        return wrappers;
    }
    
    /**
     * Find channelables that have candidate channels for a given channelable
     *
     * @param channelable the given channelable
     * @return a list of Channelables
     */
    private List<Channelable> findRelatedChannelables( Channelable channelable ) {
        List<Channelable> relatedChannelables = new ArrayList<Channelable>();
        if ( channelable instanceof Flow ) {
            Flow flow = (Flow) channelable;
            Node node = flow.isAskedFor() ? flow.getSource() : flow.getTarget();
            if ( node.isPart() ) {
                Part part = (Part) node;
                relatedChannelables.addAll(
                        Project.service().findAllResourcesNarrowingOrEqualTo(
                                part.resourceSpec() ) );
            }
        } else {
            ResourceSpec resourceSpec = (ResourceSpec) channelable;
            if ( !resourceSpec.isAnyone() ) {
                relatedChannelables.addAll(
                        Project.service().findAllResourcesNarrowingOrEqualTo( resourceSpec ) );
            }
        }
        return relatedChannelables;
    }

    private void flagIfInvalid( TextField<String> addressField, Wrapper wrapper ) {
        if ( !wrapper.isMarkedForCreation() ) {
            Channel channel = wrapper.getChannel();
            boolean ok = true;
            String problem = "";
            if ( !channel.isValid() ) {
                ok = false;
                problem = "Not valid";
            } else {
                for ( Channel c : channelable.getChannels() ) {
                    if ( c != channel && c.equals( channel ) ) {
                        ok = false;
                        problem = "Repeated";
                    }
                }
            }
            if ( !ok ) {
                addressField.add(
                        new AttributeModifier(
                                "class",
                                true,
                                new Model<String>( "invalid-address" ) ) );      // NON-NLS
                addressField.add(
                        new AttributeModifier(
                                "title",
                                true,
                                new Model<String>( problem ) ) );             // NON-NLS
            }
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
        private boolean markedForInclusion = true;
        /**
         * True when is to be added
         */
        private boolean markedForCreation;

        private Wrapper( Channel channel, boolean included ) {
            this.channel = channel;
            markedForInclusion = included;
        }

        private Wrapper() {
            this.channel = new Channel();
            markedForCreation = true;
            markedForInclusion = false;
        }

        public Channel getChannel() {
            return channel;
        }

        public boolean isMarkedForInclusion() {
            return markedForInclusion;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public void setMarkedForInclusion( boolean markedForInclusion ) {
            this.markedForInclusion = markedForInclusion;
            if ( !markedForCreation ) {
                if ( !markedForInclusion ) {
                    channelable.removeChannel( channel );
                } else {
                    channelable.addChannel( channel );
                }
            }
        }

        public Medium getMedium() {
            return channel.getMedium();
        }

        public void setMedium( Medium medium ) {
            channel.setMedium( medium );
            if ( markedForCreation ) {
                if ( medium != null ) {
                    channelable.addChannel( channel );
                }
            } else {
                if ( medium == null ) channelable.removeChannel( channel );
            }
        }

        public String getAddress() {
            return channel.getAddress();
        }

        public void setAddress( String address ) {
            if ( channel != null ) {
                channel.setAddress( address == null ? "" : address.trim() );
            }
        }
    }
}
