package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
     * List of wrapped channels.
     */
    private List<Wrapper> wrappedChannels;

    /**
     * Markup for editable channels.
     */
    private WebMarkupContainer channelsList;

    /**
     * No channel message.
     */
    private WebMarkupContainer noChannelList;


    public ChannelListPanel( String id, IModel<Channelable> model ) {
        super( id, model, null );
        init();
    }

    private void init() {
        setRenderBodyOnly( true );
        noChannelList = new WebMarkupContainer( "no-channel" );                           // NON-NLS
        add( noChannelList );
        createChannelList();
        adjustFields();
    }

    private void adjustFields() {
        boolean hasChannels = !getWrappedChannels().isEmpty();
        channelsList.setVisible( hasChannels );
        noChannelList.setVisible( !hasChannels );

        Channelable channelable = getChannelable();
        channelsList.setEnabled( isLockedByUser( channelable ) && channelable.canSetChannels() );
    }

    private void doUpdate( AjaxRequestTarget target ) {
        wrappedChannels = null;
        target.addComponent( channelsList );
        update( target,
                new Change( Change.Type.Updated, getChannelable(),
                            "effectiveChannels" ) );                                      // NON-NLS
    }

    private Channelable getChannelable() {
        return (Channelable) getModel().getObject();
    }

    private void createChannelList() {
        channelsList = new WebMarkupContainer( "editable-container" );                    // NON-NLS
        channelsList.add(
                (ListView<Wrapper>) new WrapperListView(
                        "channels",                                                       // NON-NLS
                        new PropertyModel<List<Wrapper>>( this, "wrappedChannels" ) ) );  // NON-NLS
        channelsList.setOutputMarkupId( true );
        add( channelsList );
    }

    /**
     * Get the channelable's effective channels, wrapped
     *
     * @return a list of Wrappers
     */
    public List<Wrapper> getWrappedChannels() {
        if ( wrappedChannels == null ) {
            Channelable channelable = getChannelable();
            boolean setable = isLockedByUser( channelable ) && channelable.canSetChannels();

            List<Wrapper> list = new ArrayList<Wrapper>();
            for ( Channel channel : channelable.getEffectiveChannels() )
                list.add( new Wrapper( channel, !setable ) );

            // To-be-added channel when medium is set
            if ( setable )
                list.add( new Wrapper() );
            wrappedChannels = list;
        }

        return wrappedChannels;
    }

    /**
     * Get channels used somewhere else by this channelable.
     *
     * @return a list of channel
     */
    public Set<Channel> getCandidateChannels() {
        return new HashSet<Channel>(
                getDqo().findAllChannelsFor( new ResourceSpec( getChannelable() ) ) );
    }

    //====================================================
    /**
     * A wrapper to keep track of the deletion state of channel.
     */
    private class Wrapper implements Serializable {

        /** The underlying channel. */
        private Channel channel;

        /** True if user marked item for keeps. */
        private boolean included = true;

        /** True when the channel is to be added. */
        private boolean markedForCreation;

        /** If the wrapped channel shouldn't be edited. */
        private boolean readOnly;

        private Wrapper( Channel channel, boolean readOnly ) {
            this.channel = channel;
            this.readOnly = readOnly;
        }

        private Wrapper() {
            this( new Channel(), false );
            markedForCreation = true;
            included = false;
        }

        public Channel getChannel() {
            return channel;
        }

        public boolean isReadOnly() {
            return readOnly;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public boolean isIncluded() {
            return included;
        }

        public void setIncluded( boolean included ) {
            this.included = included;
            if ( !markedForCreation )
                doAction( getChannelable(), included ? UpdateObject.Action.Add
                                                     : UpdateObject.Action.Remove );
        }

        private void doAction( Channelable channelable, UpdateObject.Action action ) {
            doCommand(
                    UpdateObject.makeCommand(
                            channelable, "effectiveChannels",                             // NON-NLS
                            channel, action ) );
        }

        public Medium getMedium() {
            return channel.getMedium();
        }

        public void setMedium( Medium medium ) {
            Channelable channelable = getChannelable();
            if ( markedForCreation ) {
                channel.setMedium( medium );
                if ( medium != null )
                    doAction( channelable, UpdateObject.Action.Add );
            } else {
                if ( medium == null )
                    doAction( channelable, UpdateObject.Action.Remove );
                else {
                    int index = channelable.getEffectiveChannels().indexOf( channel );
                    if ( index >= 0 )
                        doCommand( UpdateObject.makeCommand(
                                channelable,
                                "effectiveChannels[" + index + "].medium",                // NON-NLS
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
                Channelable channelable = getChannelable();
                int index = channelable.getEffectiveChannels().indexOf( channel );
                if ( index >= 0 )
                    doCommand( UpdateObject.makeCommand(
                            channelable,
                            "effectiveChannels[" + index + "].address",                   // NON-NLS
                            address == null ? "" : address.trim(),
                            UpdateObject.Action.Set ) );
            }
        }

        /**
         * Make this channel the first one in the list
         */
        public void moveToFirst() {
            doAction( getChannelable(), UpdateObject.Action.Move );
        }
    }

    //====================================================
    /**
     * Visualizer of a wrapped channel.
     */
    private class WrapperListView extends ListView<Wrapper> {

        private WrapperListView( String id, IModel<List<Wrapper>> model ) {
            super( id, model );
        }

        @Override
        protected void populateItem( ListItem<Wrapper> item ) {
            Wrapper wrapper = item.getModelObject();
            TextField<String> addressField = createAddressField( wrapper );

            Label channelText = new Label( "channel-string",                              // NON-NLS
                                           wrapper.getChannel().toString() );
            channelText.setVisible( wrapper.isReadOnly() );
            item.add( channelText );

            item.add( createCheckbox( wrapper ) );
            item.add( createChoices( wrapper, addressField ) );
            item.add( addressField );
            item.add( createMover( wrapper ) );
        }

        private AjaxFallbackLink<?> createMover( final Wrapper wrapper ) {
            AjaxFallbackLink<?> result = new AjaxFallbackLink( "move-to-top" ) {          // NON-NLS
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    wrapper.moveToFirst();
                    doUpdate( target );
                }
            };

            List<Channel> effectiveChannels = getChannelable().getEffectiveChannels();
            result.setVisible(
                    !effectiveChannels.isEmpty()
                    && !wrapper.getChannel().equals( effectiveChannels.get( 0 ) )
                    && !wrapper.isMarkedForCreation() );
            return result;
        }

        private Set<Medium> getCandidateMedia() {
            Set<Medium> result = EnumSet.noneOf( Medium.class );
            for ( Channel channel : getCandidateChannels() )
                result.add( channel.getMedium() );

            return result;
        }

        private List<String> getCandidateAddresses( Medium medium ) {
            Set<String> addresses = new HashSet<String>();
            if ( medium != null )
                for ( Channel channel : getCandidateChannels() ) {
                    if ( medium.equals( channel.getMedium() ) ) {
                        String address = channel.getAddress();
                        if ( address != null ) {
                            String s = address.trim();
                            if ( !s.isEmpty() )
                                addresses.add( s );
                        }
                    }
                }

            List<String> result = new ArrayList<String>( addresses );
            Collections.sort( result );
            return result;
        }

        private DropDownChoice<Medium> createChoices(
                final Wrapper wrapper, final TextField<String> addressField ) {
            final Set<Medium> candidateMedia = getCandidateMedia();

            final DropDownChoice<Medium> result = new DropDownChoice<Medium>(
                    "medium",                                                             // NON-NLS
                    new PropertyModel<Medium>( wrapper, "medium" ), getMedia( wrapper ),  // NON-NLS
                    new IChoiceRenderer<Medium>() {
                        public Object getDisplayValue( Medium object ) {
                            return object == null ? "Select a medium"
                                 : candidateMedia.contains( object ) ?
                                        MessageFormat.format( "{0} *", object.getLabel() )
                                 : object.getLabel();
                        }

                        public String getIdValue( Medium object, int index ) {
                            return Integer.toString( index );
                        }
                    } );

            result.add(
                    new AjaxFormComponentUpdatingBehavior( "onchange" ) {                 // NON-NLS
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            addressField.setEnabled( ChannelListPanel.this.isEnabled() );
                            boolean addressAllowed = !( result.getModelObject().isUnicast()
                                                        && !getChannelable().canBeUnicast() );
                            if ( !addressAllowed )
                                wrapper.getChannel().setAddress( "" );
                            addressField.setVisible( addressAllowed );
                            target.addComponent( addressField );
                            doUpdate( target );
                        }
                    } );

            result.setVisible( !wrapper.isReadOnly() );
            result.setEnabled( isEnabled() );
            return result;
        }

        private List<Medium> getMedia( Wrapper wrapper ) {
            List<Medium> media = Medium.media();
            Collections.sort( media, new Comparator<Medium>() {
                public int compare( Medium o1, Medium o2 ) {
                    return Collator.getInstance().compare( o1.getLabel(), o2.getLabel() );
                }
            } );

            // Hack for invalid medium in actual data
            Medium medium = wrapper.getMedium();
            if ( medium != null && !media.contains( medium ) )
                media.add( 0, medium );
            return media;
        }

        private TextField<String> createAddressField( Wrapper wrapper ) {
            Medium medium = wrapper.getMedium();
            final List<String> suggestions = getCandidateAddresses( medium );

            AutoCompleteTextField<String> result = new AutoCompleteTextField<String>(
                    "address", new PropertyModel<String>( wrapper, "address" ) ) {        // NON-NLS

                @Override @SuppressWarnings( { "unchecked" } )
                protected Iterator<String> getChoices( String input ) {
                    final String trimmedInput = input.trim();
                    return (Iterator<String>) new FilterIterator(
                            suggestions.iterator(),
                            new Predicate() {
                                public boolean evaluate( Object object ) {
                                    return ( (String) object ).startsWith( trimmedInput );
                                }
                            } );
                }
            };

            result.add(
                    new AjaxFormComponentUpdatingBehavior( "onchange" ) {                 // NON-NLS
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            doUpdate( target );
                        }
                    } );

            flagIfInvalid( result, wrapper );
            result.setVisible( medium != null &&
                               ( medium.isUnicast() && getChannelable().canBeUnicast()
                                 || medium == Medium.Other || medium == Medium.OtherUnicast ) );
            result.setEnabled( isEnabled() );
            return result;
        }

        private CheckBox createCheckbox( Wrapper wrapper ) {
            CheckBox result = new CheckBox(
                    "included", new PropertyModel<Boolean>( wrapper, "included" ) );      // NON-NLS
            addUpdatingBehavior( result );
            result.setEnabled( !wrapper.isReadOnly() && isEnabled() );
            return result;
        }

        private void flagIfInvalid( TextField<String> addressField, Wrapper wrapper ) {
            if ( !wrapper.isMarkedForCreation() ) {
                Channel channel = wrapper.getChannel();
                String problem = getChannelable().validate( channel );
                if ( problem == null ) {
                    for ( Channel c : getChannelable().getEffectiveChannels() ) {
                        if ( c != channel && c.equals( channel ) ) {
                            problem = "Repeated";
                        }
                    }
                }
                if ( problem != null ) {
                    addressField.add(
                            new AttributeModifier(
                                    "class",                                              // NON-NLS
                                    true,
                                    new Model<String>( "invalid-address" ) ) );           // NON-NLS
                    addressField.add(
                            new AttributeModifier(
                                    "title",                                              // NON-NLS
                                    true,
                                    new Model<String>( problem ) ) );
                }
            }
        }

        private void addUpdatingBehavior( CheckBox checkBox ) {
            checkBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {           // NON-NLS
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    doUpdate( target );
                }
            } );
        }
    }
}
