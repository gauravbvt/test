package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * An editable list of channels.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 2, 2009
 * Time: 7:25:50 PM
 */
public class ChannelListPanel extends Panel {
    /**
     * The object which list of channels is being edited.
     */
    private final Channelable channelable;
    /**
     * List of wrapped, candidate channels
     */
    private List<Wrapper> candidates = new ArrayList<Wrapper>();
    /**
     * Markup for candidate channels.
     */
    private WebMarkupContainer candidateChannelsMarkup;
    /**
     * Markup for editable channels.
     */
    private WebMarkupContainer editableChannelsMarkup;
    /**
     * LIst of non-editable channels
     */
    List<Channel> channels;
    /**
     * Markup for non-editable channels
     */
    private WebMarkupContainer nonEditableChannelsMarkup;
    /**
     * No channel message
     */
    private Label noChannelLabel;

    public ChannelListPanel( String id, IModel<Channelable> model ) {
        super( id, model );
        this.channelable = model.getObject();
        init();
    }

    private void init() {
        addCandidateChannels();
        addEditableChannels();
        addNonEditableChannels();
        adjustFields();
    }

    private void adjustFields() {
        if ( channelable instanceof Flow ) {
            Flow flow = (Flow) channelable;
            candidateChannelsMarkup.setVisible( !candidates.isEmpty() && flow.canSetChannels() );
            editableChannelsMarkup.setVisible( flow.canSetChannels() );
            nonEditableChannelsMarkup.setVisible( !flow.canSetChannels() && flow.canGetChannels() );
            noChannelLabel.setVisible( channels.isEmpty() );
        } else {
            nonEditableChannelsMarkup.setVisible( false );
        }
    }

    private void addCandidateChannels() {
        candidateChannelsMarkup = new WebMarkupContainer( "candidates-container" );
        add( candidateChannelsMarkup );
        candidates = getWrappedCandidateChannels( channelable );
        ListView<Wrapper> candidatesList = new ListView<Wrapper>( "candidates", candidates ) {
            @Override
            protected void populateItem( ListItem<Wrapper> item ) {
                Wrapper wrapper = item.getModelObject();
                item.add( new CheckBox(
                        "include-candidate",
                        new PropertyModel<Boolean>( wrapper, "markedForInclusion" ) ) );
                item.add( new Label( "candidate", wrapper.getChannel().toString() ) );
            }
        };
        candidateChannelsMarkup.add( candidatesList );
        candidateChannelsMarkup.setVisible( !candidates.isEmpty() && this.isEnabled() );
    }

    private void addEditableChannels() {
        editableChannelsMarkup = new WebMarkupContainer( "editable-container" );
        add( editableChannelsMarkup );
        final List<Wrapper> setChannels = getWrappedChannels( channelable );
        editableChannelsMarkup.add( new ListView<Wrapper>( "editable-channels", setChannels ) {
            @Override
            protected void populateItem( ListItem<Wrapper> item ) {
                final Wrapper wrapper = item.getModelObject();
                WebMarkupContainer includeSpan = new WebMarkupContainer( "include-span" );
                CheckBox includeCheckBox = new CheckBox(
                        "include",
                        new PropertyModel<Boolean>( wrapper, "markedForInclusion" ) );
                includeSpan.setVisible( !wrapper.isMarkedForCreation() );
                includeCheckBox.setEnabled( this.isEnabled() );
                includeSpan.add( includeCheckBox );
                item.add( includeSpan );
                DropDownChoice<Medium> mediumChoices = new DropDownChoice<Medium>(
                        "medium",
                        new PropertyModel<Medium>( wrapper, "medium" ),
                        Arrays.asList( Medium.values() ),
                        new IChoiceRenderer<Medium>() {
                            public Object getDisplayValue( Medium medium ) {
                                return medium == null ? "Select a medium" : medium.getName();
                            }

                            public String getIdValue( Medium medium, int index ) {
                                return Integer.toString( index );
                            }
                        }
                );
                item.add( mediumChoices );
                mediumChoices.setEnabled( this.isEnabled() );
                TextField<String> addressField = new TextField<String>(
                        "address",
                        new PropertyModel<String>( wrapper, "address" ) );
                item.add( addressField );
                addressField.setEnabled( this.isEnabled() );
                flagIfInvalid( addressField, wrapper );
                // TODO - use AjaxFallbackLink
                Link moveToTopLink = new Link("move-to-top") {
                    public void onClick( ) {
                        wrapper.moveToFirst();
                        PageParameters parameters = getWebPage().getPageParameters();
                        this.setResponsePage( getWebPage().getClass(), parameters );
                    }
                } ;
                item.add( moveToTopLink );
                moveToTopLink.setVisible( wrapper != setChannels.get( 0 ) && !wrapper.isMarkedForCreation() );
            }
        } );
    }

    private void addNonEditableChannels() {
        nonEditableChannelsMarkup = new WebMarkupContainer( "non-editable-container" );
        add( nonEditableChannelsMarkup );
        channels = channelable.getEffectiveChannels();
        noChannelLabel = new Label( "no-channel", "(No channel)" );
        nonEditableChannelsMarkup.add( noChannelLabel );
        nonEditableChannelsMarkup.add( new ListView<Channel>( "non-editable-channels", channels ) {
            protected void populateItem( ListItem<Channel> item ) {
                final Channel channel = item.getModelObject();
                item.add( new Label( "channel-string", new AbstractReadOnlyModel<String>() {
                    public String getObject() {
                        return channel.toString();
                    }
                } ) );
            }
        } );
    }

    private List<Wrapper> getWrappedChannels( Channelable channelable ) {
        final List<Wrapper> list = new ArrayList<Wrapper>();
        List<Channel> setChannels = channelable.getEffectiveChannels();
        for ( Channel channel : setChannels ) {
            // wrap channel as already included
            list.add( new Wrapper( channel, true ) );
        }
        // To-be-added channel if medium is set
        list.add( new Wrapper() );
        return list;
    }

    private List<Wrapper> getWrappedCandidateChannels( Channelable channelable ) {
        Set<Channel> candidates = new HashSet<Channel>();
        List<Channelable> channelables = findRelatedChannelables( channelable );
        List<Channel> alreadySetChannels = channelable.getEffectiveChannels();
        // Get all non-redundant, valid candidate channels
        for ( Channelable aChannelable : channelables ) {
            for ( Channel channel : aChannelable.getEffectiveChannels() ) {
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
                for ( Channel c : channelable.getEffectiveChannels() ) {
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

        /**
         * Make this channel the first one in the list
         */
        public void moveToFirst() {
            channelable.moveToFirst( channel );
        }
    }
}
