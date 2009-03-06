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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO - Ajax - update display of flow issues when channels list is empty?

/**
 * An editable list of channels.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 2, 2009
 * Time: 7:25:50 PM
 */
public class ChannelListPanel extends AbstractUpdatablePanel {
    /**
     * The object which list of channels is being edited.
     */
    private IModel<Channelable> model;
    /**
     * Markup for candidate channels.
     */
    private WebMarkupContainer candidateChannelsMarkup;
    /**
     * Markup for editable channels.
     */
    private WebMarkupContainer editableChannelsMarkup;
    /**
     * List of non-editable channels.
     */
    private List<Channel> channels;
    /**
     * Markup for non-editable channels.
     */
    private WebMarkupContainer nonEditableChannelsMarkup;
    /**
     * List view on wrapped candidate channels
     */
    private ListView<Wrapper> candidatesList;
    /**
     * No channel message
     */
    private Label noChannelLabel;

    public ChannelListPanel( String id, IModel<Channelable> model ) {
        super( id, model );
        this.model = model;
        init();
    }

    private void init() {
        addCandidateChannels();
        addEditableChannels();
        addNonEditableChannels();
        adjustFields();
    }

    private void adjustFields() {
        Channelable channelable = model.getObject();
        if ( channelable instanceof Flow ) {
            Flow flow = (Flow) channelable;
            candidateChannelsMarkup.setVisible(
                    !candidatesList.getModelObject().isEmpty()
                            && flow.canSetChannels() );
            editableChannelsMarkup.setVisible( flow.canSetChannels() );
            nonEditableChannelsMarkup.setVisible( !flow.canSetChannels() && flow.canGetChannels() );
            noChannelLabel.setVisible( channels.isEmpty() );
        } else {
            nonEditableChannelsMarkup.setVisible( false );
        }
    }

    private void addCandidateChannels() {
        candidateChannelsMarkup = new WebMarkupContainer( "candidates-container" );
        candidateChannelsMarkup.setOutputMarkupId( true );
        add( candidateChannelsMarkup );
        candidatesList = new ListView<Wrapper>(
                "candidates",
                new PropertyModel<List<Wrapper>>( this, "wrappedCandidateChannels" ) ) {
            @Override
            protected void populateItem( ListItem<Wrapper> item ) {
                Wrapper wrapper = item.getModelObject();
                CheckBox candidateCheckBox = new CheckBox(
                        "include-candidate",
                        new PropertyModel<Boolean>( wrapper, "markedForInclusion" ) );
                candidateCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    protected void onUpdate( AjaxRequestTarget target ) {
                        // adjustFields();
                        target.addComponent( candidateChannelsMarkup );
                        target.addComponent( editableChannelsMarkup );
                        updateWith( target );
                    }
                } );
                item.add( candidateCheckBox );
                item.add( new Label( "candidate", wrapper.getChannel().toString() ) );
            }
        };
        candidateChannelsMarkup.add( candidatesList );
        candidateChannelsMarkup.setVisible(
                !candidatesList.getModelObject().isEmpty()
                        && this.isEnabled() );
    }

    private void addEditableChannels() {
        final Channelable channelable = model.getObject();
        editableChannelsMarkup = new WebMarkupContainer( "editable-container" );
        editableChannelsMarkup.setOutputMarkupId( true );
        add( editableChannelsMarkup );
        // final List<Wrapper> setChannels = getWrappedChannels( channelable );
        final ListView<Wrapper> editableChannelsList = new ListView<Wrapper>(
                "editable-channels",
                new PropertyModel<List<Wrapper>>( this, "wrappedChannels" ) ) {
            @Override
            protected void populateItem( ListItem<Wrapper> item ) {
                final Wrapper wrapper = item.getModelObject();
                WebMarkupContainer includeSpan = new WebMarkupContainer( "include-span" );
                CheckBox includeCheckBox = new CheckBox(
                        "include",
                        new PropertyModel<Boolean>( wrapper, "markedForInclusion" ) );
                includeSpan.setVisible( !wrapper.isMarkedForCreation() );
                includeCheckBox.setEnabled( this.isEnabled() );
                includeCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    protected void onUpdate( AjaxRequestTarget target ) {
                        // adjustFields();
                        target.addComponent( candidateChannelsMarkup );
                        target.addComponent( editableChannelsMarkup );
                        updateWith( target );
                    }
                } );
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
                mediumChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    protected void onUpdate( AjaxRequestTarget target ) {
                        target.addComponent( editableChannelsMarkup );
                    }
                } );
                item.add( mediumChoices );
                mediumChoices.setEnabled( this.isEnabled() );
                TextField<String> addressField = new TextField<String>(
                        "address",
                        new PropertyModel<String>( wrapper, "address" ) );
                addressField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    protected void onUpdate( AjaxRequestTarget target ) {
                        target.addComponent( editableChannelsMarkup );
                        updateWith( target );
                    }
                } );
                item.add( addressField );
                addressField.setEnabled( this.isEnabled() );
                flagIfInvalid( addressField, wrapper );
                AjaxFallbackLink moveToTopLink = new AjaxFallbackLink( "move-to-top" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        wrapper.moveToFirst();
                        target.addComponent( editableChannelsMarkup );
                        updateWith( target );
                    }
                };
                item.add( moveToTopLink );
                List<Channel> effectiveChannels = channelable.getEffectiveChannels();
                moveToTopLink.setVisible(
                        !effectiveChannels.isEmpty() &&
                                wrapper.getChannel() != effectiveChannels.get( 0 )
                                && !wrapper.isMarkedForCreation() );
            }
        };
        // editableChannelsList.setOutputMarkupId( true );
        editableChannelsMarkup.add( editableChannelsList );
    }

    private void addNonEditableChannels() {
        Channelable channelable = model.getObject();
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

    /**
     * Get the channelable's effective channels, wrapped
     *
     * @return a list of Wrappers
     */
    public List<Wrapper> getWrappedChannels() {
        Channelable channelable = model.getObject();
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

    public List<Wrapper> getWrappedCandidateChannels() {
        Channelable channelable = model.getObject();
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
            if ( node != null && node.isPart() ) {  // TODO - why can node be null (connecting to internal flow of an external flow)
                Part part = (Part) node;
                ResourceSpec partResourceSpec = part.resourceSpec();
                if ( !partResourceSpec.isAnyone() ) {
                    relatedChannelables.addAll(
                            Project.service().findAllResourcesNarrowingOrEqualTo(
                                    partResourceSpec ) );
                }
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
        Channelable channelable = model.getObject();
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
            Channelable channelable = model.getObject();
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
            Channelable channelable = model.getObject();
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
            Channelable channelable = model.getObject();
            channelable.moveToFirst( channel );
        }
    }
}
