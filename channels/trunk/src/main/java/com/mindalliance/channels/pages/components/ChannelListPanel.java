package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.ModelPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ChannelListPanel.class );


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

    /**
     * New medium type marker.
     */
    private static TransmissionMedium NewMediumType;

    private boolean canAddNewMedium = true;

    /**
     * New format type marker.
     */
    private static InfoFormat NewInfoFormatType;

    private boolean canAddNewInfoFormat = true;

    private boolean restrictToContactInfoMedia = false;

    static {
        NewMediumType = new TransmissionMedium( "New medium" );
        NewMediumType.setType();
        // fake id
        NewMediumType.setId( Long.MAX_VALUE );
        NewInfoFormatType = new InfoFormat( "New format" );
        NewInfoFormatType.setType();
        // fake id
        NewInfoFormatType.setId( Long.MAX_VALUE - 1 );
    }

    public ChannelListPanel( String id,
                             IModel<? extends Channelable> model,
                             boolean canAddNewMediumAndFormat,
                             boolean restrictToContactInfoMedia ) {
        super( id, model, null );
        this.canAddNewMedium = canAddNewMediumAndFormat;
        this.canAddNewInfoFormat = canAddNewMediumAndFormat;
        this.restrictToContactInfoMedia = restrictToContactInfoMedia;
        init();
    }


    public ChannelListPanel( String id, IModel<? extends Channelable> model, boolean canAddNewMediumAndFormat ) {
        super( id, model, null );
        this.canAddNewMedium = canAddNewMediumAndFormat;
        this.canAddNewInfoFormat = canAddNewMediumAndFormat;
        init();
    }

    public ChannelListPanel( String id, IModel<? extends Channelable> model ) {
        this( id, model, true );
    }

    private void init() {
        setRenderBodyOnly( true );
        noChannelList = new WebMarkupContainer( "no-channel" );
        add( noChannelList );
        createChannelList();
        adjustFields();
    }

    private boolean canBeEdited() {
        Channelable channelable = getChannelable();
        return ( !channelable.canBeLocked() || isLockedByUser( channelable ) ) && channelable.canSetChannels();
    }

    private boolean canBeMoved() {
        return getChannelable().isModelObject();
    }


    private void adjustFields() {
        boolean hasChannels = !getWrappedChannels().isEmpty();
        channelsList.setVisible( hasChannels );
        noChannelList.setVisible( !hasChannels );
        channelsList.setEnabled( canBeEdited() );
    }

    private void doUpdate( AjaxRequestTarget target ) {
        wrappedChannels = null;
        createChannelList();
        target.add( channelsList );
        update( target,
                new Change( Change.Type.Updated, getChannelable(),
                        "modifiableChannels" ) );
    }

    private Channelable getChannelable() {
        return (Channelable) getModel().getObject();
    }

    private void createChannelList() {
        channelsList = new WebMarkupContainer( "editable-container" );
        channelsList.setOutputMarkupId( true );
        channelsList.add(
                (ListView<Wrapper>) new WrapperListView(
                        "channels",
                        new PropertyModel<List<Wrapper>>( this, "wrappedChannels" ) ) );
        channelsList.setOutputMarkupId( true );
        addOrReplace( channelsList );
    }

    /**
     * Get the channelable's modifiable channels, wrapped
     *
     * @return a list of Wrappers
     */
    public List<Wrapper> getWrappedChannels() {
        if ( wrappedChannels == null ) {
            Channelable channelable = getChannelable();
            boolean setable = canBeEdited();

            List<Wrapper> list = new ArrayList<Wrapper>();
            for ( Channel channel : channelable.getModifiableChannels() )
                list.add( new Wrapper( channel, !setable ) );

            // To-be-added channel when medium is set
            if ( setable )
                list.add( new Wrapper() );
            wrappedChannels = list;
        }

        return wrappedChannels;
    }

    //====================================================

    /**
     * A wrapper to keep track of the deletion state of channel.
     */
    private class Wrapper implements Serializable {

        /**
         * The underlying channel.
         */
        private Channel channel;

        /**
         * True if user marked item for keeps.
         */
        private boolean included = true;

        /**
         * True when the channel is to be added.
         */
        private boolean markedForCreation;

        /**
         * If the wrapped channel shouldn't be edited.
         */
        private boolean readOnly;

        private Wrapper( Channel channel, boolean readOnly ) {
            this.channel = channel;
            this.readOnly = readOnly;
        }

        private Wrapper() {
            this( new Channel( TransmissionMedium.UNKNOWN ), false );
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
            if ( !markedForCreation ) {
                if ( included ) {
                    doAction( getChannelable(), UpdateObject.Action.AddUnique );
                } else {
                    TransmissionMedium medium = getMedium();
                    doAction( getChannelable(), UpdateObject.Action.Remove );
                    getCommander().cleanup( TransmissionMedium.class, medium.getName() );
                    if ( getFormat() != null )
                        getCommander().cleanup( InfoFormat.class, getFormat().getName() );
                }
            }
        }

        private void doAction( Channelable channelable, UpdateObject.Action action ) {
            // Don't add a redundant channel
            if ( action != UpdateObject.Action.AddUnique
                    || !channelable.getEffectiveChannels().contains( channel ) ) {
                if ( channelable.isModelObject() ) {
                    try {
                        if ( channelable.isModifiableInProduction() ) {
                            doUnsafeCommand(
                                    UpdateObject.makeCommand( getUser().getUsername(), channelable,
                                            "modifiableChannels",
                                            channel,
                                            action ) );
                        } else {
                            doCommand( channelable,
                                    UpdateObject.makeCommand( getUser().getUsername(), channelable,
                                            "modifiableChannels",
                                            channel,
                                            action ) );
                        }
                    } catch ( CommandException e ) {
                        LOG.warn( "Failed to do action " + action, e );
                    }
                } else {
                    if ( action == UpdateObject.Action.AddUnique )
                        channelable.addChannel( channel );
                    else if ( action == UpdateObject.Action.Remove )
                        channelable.removeChannel( channel );
                }
            }
        }

        public TransmissionMedium getMedium() {
            return channel.getMedium();
        }

        public void setMedium( TransmissionMedium value ) {
            Channelable channelable = getChannelable();
            if ( markedForCreation && value != null ) {
                TransmissionMedium medium;
                if ( value.equals( NewMediumType ) ) {
                    medium = doSafeFindOrCreateType(
                            TransmissionMedium.class,
                            getQueryService().makeNameForNewEntity( TransmissionMedium.class ) );
                } else {
                    medium = value;
                }
                channel.setMedium( medium );
                doAction( channelable, UpdateObject.Action.AddUnique );
            }
        }

        public void setFormat( InfoFormat value ) {
            if ( channel != null ) {
                Channelable channelable = getChannelable();
                if ( channelable.canSetFormat() ) {
                    InfoFormat format;
                    if ( value != null && value.equals( NewInfoFormatType ) ) {
                        format = doSafeFindOrCreateType(
                                InfoFormat.class,
                                getQueryService().makeNameForNewEntity( InfoFormat.class ) );
                    } else {
                        format = value == null || value.isUnknown() ? null : value;
                    }
                    int index = channelable.getModifiableChannels().indexOf( channel );
                    if ( index >= 0 ) {
                        String oldFormatName = channel.getFormat() == null ? null : channel.getFormat().getName();
                        try {
                            doCommand( channelable,
                                    UpdateObject.makeCommand( getUser().getUsername(), channelable,
                                            "modifiableChannels[" + index + "].format",
                                            format,
                                            UpdateObject.Action.Set ) );
                        } catch ( CommandException e ) {
                            LOG.warn( "Failed to set format" );
                        }
                        if ( oldFormatName != null ) {
                            getCommander().cleanup( InfoFormat.class, oldFormatName );
                        }
                    }
                }
            }
        }

        public String getAddress() {
            return channel.getAddress();
        }

        public void setAddress( String address ) {
            Channelable channelable = getChannelable();
            if ( channel != null
                    &&  !channelable.getEffectiveChannels().contains( new Channel( channel.getMedium(), address ) ) ) {
                if ( channelable.isModelObject() ) {
                    int index = channelable.getModifiableChannels().indexOf( channel );
                    if ( index >= 0 )
                        try {
                            doCommand( channelable,
                                    UpdateObject.makeCommand( getUser().getUsername(), channelable,
                                            "modifiableChannels[" + index + "].address",
                                            address == null ? "" : address.trim(),
                                            UpdateObject.Action.Set ) );
                        } catch ( CommandException e ) {
                            LOG.warn( "Failed to set address" );
                        }
                } else {
                    channelable.setAddress( channel, address );
                }
            }
        }


        public InfoFormat getFormat() {
            return channel == null ? null : channel.getFormat();
        }


        protected Change doCommand( Channelable channelable, Command command ) {
            if ( channelable.isModifiableInProduction() ) {
                return ChannelListPanel.super.doUnsafeCommand( command );
            } else {
                return ChannelListPanel.super.doCommand( command );
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
            TransmissionMedium medium = wrapper.getMedium();
            item.add( createCheckbox( wrapper ) );
            // int maxLabelSize = maxMediumLabelSize();
            Component mediumLinkOrLabel = findPage() instanceof ModelPage
                    ? new ModelObjectLink( "mediumLink", new Model<TransmissionMedium>( medium ), new Model<String>( medium.getName() ) )
                    : new Label( "mediumLink", medium.getName() );
            mediumLinkOrLabel.setVisible( !wrapper.isMarkedForCreation() );
            item.add( mediumLinkOrLabel );
            Label addressLabel = new Label( "addressString", new Model<String>( wrapper.getAddress() ) );
            addressLabel.setVisible( getChannelable().hasAddresses() && wrapper.isReadOnly() );
            item.add( addressLabel );
            TextField<String> addressField = createAddressField( wrapper );
            item.add( addressField );
            item.add( createMediaChoices( wrapper ) );
            item.add( createFormatContainer( wrapper ) );
            item.add( createMover( wrapper ) );
        }

        private AjaxLink<?> createMover( final Wrapper wrapper ) {
            AjaxLink<?> result = new AjaxLink( "move-to-top" ) {          // NON-NLS

                @Override
                public void onClick( AjaxRequestTarget target ) {
                    wrapper.moveToFirst();
                    doUpdate( target );
                }
            };

            List<Channel> modifiableChannels = getChannelable().getModifiableChannels();
            result.setVisible(
                    !modifiableChannels.isEmpty()
                            && canBeMoved()
                            && canBeEdited()
                            && !wrapper.getChannel().equals( modifiableChannels.get( 0 ) )
                            && !wrapper.isMarkedForCreation() );
            return result;
        }

        private List<TransmissionMedium> getCandidateMedia() {
            List<TransmissionMedium> candidates = new ArrayList<TransmissionMedium>();
            candidates.add( TransmissionMedium.UNKNOWN );
            for ( TransmissionMedium medium : getQueryService().listReferencedEntities( TransmissionMedium.class ) ) {
                if ( !medium.isUnknown() && ( !restrictToContactInfoMedia || medium.isForContactInfo() ) ) {
                    candidates.add( medium );
                }
            }
            Collections.sort( candidates, new Comparator<TransmissionMedium>() {
                public int compare( TransmissionMedium o1, TransmissionMedium o2 ) {
                    return Collator.getInstance().compare( o1.getLabel(), o2.getLabel() );
                }
            } );
            if ( canAddNewMedium )
                candidates.add( NewMediumType );
            return candidates;
        }

        private List<InfoFormat> getCandidateFormats() {
            List<InfoFormat> candidates = new ArrayList<InfoFormat>();
            candidates.add( InfoFormat.UNKNOWN );
            candidates.addAll( getQueryService().listReferencedEntities( InfoFormat.class ) );
            Collections.sort( candidates, new Comparator<InfoFormat>() {
                public int compare( InfoFormat o1, InfoFormat o2 ) {
                    return Collator.getInstance().compare( o1.getLabel(), o2.getLabel() );
                }
            } );
            if ( canAddNewInfoFormat )
                candidates.add( NewInfoFormatType );
            return candidates;
        }


        private DropDownChoice<TransmissionMedium> createMediaChoices(
                final Wrapper wrapper ) {
            final DropDownChoice<TransmissionMedium> mediumDropDownChoice = new DropDownChoice<TransmissionMedium>(
                    "medium",
                    new PropertyModel<TransmissionMedium>( wrapper, "medium" ),
                    getCandidateMedia(),
                    new IChoiceRenderer<TransmissionMedium>() {
                        public Object getDisplayValue( TransmissionMedium medium ) {
                            return medium.isUnknown() ? "Choose One" : medium.getLabel();
                        }

                        public String getIdValue( TransmissionMedium object, int index ) {
                            return Integer.toString( index );
                        }
                    } );
            mediumDropDownChoice.add(
                    new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            doUpdate( target );
                        }
                    } );

            mediumDropDownChoice.setVisible( isEnabled() && wrapper.isMarkedForCreation() );
            return mediumDropDownChoice;
        }

        private WebMarkupContainer createFormatContainer( Wrapper wrapper ) {
            WebMarkupContainer formatContainer = new WebMarkupContainer( "formatContainer" );
            InfoFormat format = wrapper.getFormat();
            // format link
            Component formatLinkOrLabel =
                    format == null
                            ? new Label( "formatLink", "format" )
                            : findPage() instanceof ModelPage
                            ? new ModelObjectLink(
                            "formatLink",
                            new Model<InfoFormat>( format ),
                            new Model<String>( canBeEdited() ? "format" : "format " + format.getName() ) )
                            : new Label( "formatLink", "format " + format.getName() );
            formatLinkOrLabel.setVisible( !wrapper.isMarkedForCreation() );
            formatContainer.add( formatLinkOrLabel );
            // format choices
            final DropDownChoice<InfoFormat> formatDropDownChoice = new DropDownChoice<InfoFormat>(
                    "formatChoice",
                    new PropertyModel<InfoFormat>( wrapper, "format" ),
                    getCandidateFormats(),
                    new IChoiceRenderer<InfoFormat>() {
                        public Object getDisplayValue( InfoFormat format ) {
                            return format.isUnknown() ? "Choose One" : format.getLabel();
                        }

                        public String getIdValue( InfoFormat object, int index ) {
                            return Integer.toString( index );
                        }
                    } );
            formatDropDownChoice.add(
                    new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            doUpdate( target );
                        }
                    } );

            formatDropDownChoice.setVisible( canBeEdited() );
            formatContainer.add( formatDropDownChoice );
            formatContainer.setVisible(
                    getChannelable().canSetFormat()
                            && !( !canBeEdited() && wrapper.getFormat() == null ) // hide when read only and no format
                            && !wrapper.isMarkedForCreation() );
            return formatContainer;
        }

        private TextField<String> createAddressField( Wrapper wrapper ) {
            TextField<String> addressField = new TextField<String>(
                    "address", new PropertyModel<String>( wrapper, "address" ) );
            addressField.add(
                    new AjaxFormComponentUpdatingBehavior( "onchange" ) {                 // NON-NLS

                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            doUpdate( target );
                        }
                    } );

            flagIfInvalid( addressField, wrapper );
            addressField.setVisible( !wrapper.isMarkedForCreation()
                    && getChannelable().hasAddresses() && canBeEdited() );
            //           addressField.setEnabled( canBeEdited() );
            return addressField;
        }

        private CheckBox createCheckbox( Wrapper wrapper ) {
            CheckBox result = new CheckBox(
                    "included", new PropertyModel<Boolean>( wrapper, "included" ) );      // NON-NLS
            addUpdatingBehavior( result );
            // result.setEnabled( !wrapper.isReadOnly() && isEnabled() );
            makeVisible( result, !wrapper.isMarkedForCreation() && !wrapper.isReadOnly() );
            return result;
        }

        private void flagIfInvalid( TextField<String> addressField, Wrapper wrapper ) {
            if ( !wrapper.isMarkedForCreation() ) {
                Channel channel = wrapper.getChannel();
                String problem = getChannelable().validate( channel );
                /*if ( problem == null ) {
                    for ( Channel c : getChannelable().getModifiableChannels() ) {
                        if ( !c.equals( channel ) && c.getAddress().equals( channel.getAddress() ) ) {
                            problem = "Repeated";
                        }
                    }
                }*/
                if ( problem != null ) {
                    addressField.add(
                            new AttributeModifier(
                                    "class",
                                    new Model<String>( "invalid-address" ) ) );           // NON-NLS
                    addTipTitle( addressField, new Model<String>( problem ) );
                }
            }
        }

        private void addUpdatingBehavior( CheckBox checkBox ) {
            checkBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {           // NON-NLS

                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    doUpdate( target );
                }
            } );
        }
    }
}
