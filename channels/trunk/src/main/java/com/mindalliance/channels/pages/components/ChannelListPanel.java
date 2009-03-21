package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
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
import java.util.List;

/**
 * An editable list of channels.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 2, 2009
 * Time: 7:25:50 PM
 */
public class ChannelListPanel extends AbstractCommandablePanel {
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
                            && isLockedByUser( channelable )
                            && flow.canSetChannels() );
            editableChannelsMarkup.setVisible(
                    isLockedByUser( channelable ) &&
                            flow.canSetChannels() );
            nonEditableChannelsMarkup.setVisible(
                    !( flow.canSetChannels() && isLockedByUser( channelable ) )
                            && flow.canGetChannels() );
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
                        update(
                                target,
                                new Change(
                                        Change.Type.Updated,
                                        getChannelable(), "effectiveChannels" ) );
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

    private Channelable getChannelable() {
        return model.getObject();
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
                        update(
                                target,
                                new Change(
                                        Change.Type.Updated,
                                        getChannelable(), "effectiveChannels" ) );
                    }
                } );
                includeSpan.add( includeCheckBox );
                item.add( includeSpan );
                final TextField<String> addressField = new TextField<String>(
                        "address",
                        new PropertyModel<String>( wrapper, "address" ) );
                addressField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    protected void onUpdate( AjaxRequestTarget target ) {
                        target.addComponent( editableChannelsMarkup );
                        update(
                                target,
                                new Change(
                                        Change.Type.Updated,
                                        getChannelable(),
                                        "effectiveChannels" ) );
                    }
                } );
                item.add( addressField );
                final Channel channel = item.getModelObject().getChannel();
                addressField.setEnabled( this.isEnabled() );
                addressField.setVisible( !( channel.isUnicast() && !channelable.canBeUnicast() ) );
                flagIfInvalid( addressField, wrapper );
                final DropDownChoice<Medium> mediumChoices = new DropDownChoice<Medium>(
                        "medium",
                        new PropertyModel<Medium>( wrapper, "medium" ),
                        ( channelable instanceof Flow ) ? Medium.media() : Medium.unicastMedia(),
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
                        addressField.setEnabled( ChannelListPanel.this.isEnabled() );
                        boolean addressAllowed = !( mediumChoices.getModelObject().isUnicast()
                                && !channelable.canBeUnicast() );
                        if ( !addressAllowed ) channel.setAddress( "" );
                        addressField.setVisible( addressAllowed );
                        target.addComponent( addressField );
                        update(
                                target,
                                new Change(
                                        Change.Type.Updated,
                                        getChannelable(),
                                        "effectiveChannels" ) );
                    }
                } );
                item.add( mediumChoices );
                mediumChoices.setEnabled( this.isEnabled() );
                AjaxFallbackLink moveToTopLink = new AjaxFallbackLink( "move-to-top" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        wrapper.moveToFirst();
                        target.addComponent( editableChannelsMarkup );
                        update(
                                target,
                                new Change(
                                        Change.Type.Updated,
                                        getChannelable(),
                                        "effectiveChannels" ) );
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

    /**
     * Get wrapped candidate channels.
     * (Used locally by PropertyModel)
     *
     * @return a list of channel wrappers
     */
    public List<Wrapper> getWrappedCandidateChannels() {
        Channelable channelable = model.getObject();
        List<Channel> candidates = getService().findAllCandidateChannelsFor( channelable );
        // Wrap them as not marked for inclusion
        List<Wrapper> wrappers = new ArrayList<Wrapper>();
        for ( Channel candidate : candidates ) {
            wrappers.add(
                    new Wrapper( candidate, false ) );
        }
        return wrappers;
    }

    private void flagIfInvalid( TextField<String> addressField, Wrapper wrapper ) {
        Channelable channelable = model.getObject();
        if ( !wrapper.isMarkedForCreation() ) {
            Channel channel = wrapper.getChannel();
            String problem = channelable.validate( channel );
            if ( problem == null ) {
                for ( Channel c : channelable.getEffectiveChannels() ) {
                    if ( c != channel && c.equals( channel ) ) {
                        problem = "Repeated";
                    }
                }
            }
            if ( problem != null ) {
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
                    doCommand( UpdateObject.makeCommand(
                            channelable,
                            "effectiveChannels",
                            channel,
                            UpdateObject.Action.Remove ) );
                    // channelable.removeChannel( channel );
                } else {
                    // channelable.addChannel( channel );
                    doCommand( UpdateObject.makeCommand(
                            channelable,
                            "effectiveChannels",
                            channel,
                            UpdateObject.Action.Add ) );
                }
            }
        }

        public Medium getMedium() {
            return channel.getMedium();
        }

        public void setMedium( Medium medium ) {
            Channelable channelable = model.getObject();
            if ( markedForCreation ) {
                channel.setMedium( medium );
                if ( medium != null ) {
                    doCommand( UpdateObject.makeCommand(
                            channelable,
                            "effectiveChannels",
                            channel,
                            UpdateObject.Action.Add ) );
                }
            } else {
                if ( medium == null ) {
                    // channelable.removeChannel( channel );
                    doCommand( UpdateObject.makeCommand(
                            channelable,
                            "effectiveChannels",
                            channel,
                            UpdateObject.Action.Remove ) );
                } else {
                    // channel.setMedium( medium );
                    int index = channelable.getEffectiveChannels().indexOf( channel );
                    if ( index >= 0 )
                        doCommand( UpdateObject.makeCommand(
                                channelable,
                                "effectiveChannels[" + index + "].medium",
                                medium,
                                UpdateObject.Action.Set ) );

                }
            }
        }

        public String getAddress() {
            return channel.getAddress();
        }

        public void setAddress( String address ) {
            if ( channel != null ) {
                Channelable channelable = model.getObject();
                int index = channelable.getEffectiveChannels().indexOf( channel );
                if ( index >= 0 )
                    doCommand( UpdateObject.makeCommand(
                            channelable,
                            "effectiveChannels[" + index + "].address",
                            address == null ? "" : address.trim(),
                            UpdateObject.Action.Set ) );
            }
        }

        /**
         * Make this channel the first one in the list
         */
        public void moveToFirst() {
            Channelable channelable = model.getObject();
            doCommand( UpdateObject.makeCommand(
                    channelable,
                    "effectiveChannels",
                    channel,
                    UpdateObject.Action.Move ) );
        }
    }
}
