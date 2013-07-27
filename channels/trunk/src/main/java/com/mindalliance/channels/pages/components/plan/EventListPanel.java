package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.entities.EntityLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Plan event list panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 7, 2009
 * Time: 1:43:30 PM
 */
public class EventListPanel extends AbstractCommandablePanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EventListPanel.class );


    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    /**
     * Collator.
     */
    private static Collator collator = Collator.getInstance();
    /**
     * Events container.
     */
    private WebMarkupContainer eventsDiv;

    public EventListPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        eventsDiv = new WebMarkupContainer( "eventsDiv" );
        eventsDiv.setOutputMarkupId( true );
        add( eventsDiv );
        eventsDiv.add( makeEventsTable() );
    }

    private ListView<EventWrapper> makeEventsTable() {
        final List<EventWrapper> eventWrappers = getWrappedEvents();
        return new ListView<EventWrapper>( "event", eventWrappers ) {
            /** {@inheritDoc} */
            protected void populateItem( ListItem<EventWrapper> item ) {
                addEventCell( item, eventWrappers.size() );
                addConfirmedCell( item );
            }
        };
    }

    private void addEventCell( final ListItem<EventWrapper> item, int count ) {
        item.setOutputMarkupId( true );
        final EventWrapper wrapper = item.getModelObject();
        WebMarkupContainer nameContainer = new WebMarkupContainer( "name-container" );
        item.add( nameContainer );
        final List<String> choices;
        if ( wrapper.isMarkedForCreation() ) {
            choices = getQueryService().findAllEntityNames( Event.class );
        } else {
            choices = new ArrayList<String>();
        }
        // text field
        AutoCompleteTextField<String> nameField = new AutoCompleteTextField<String>(
                "name-input",
                new PropertyModel<String>( wrapper, "name" ),
                getAutoCompleteSettings() ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        nameField.setOutputMarkupId( true );
        makeVisible( nameField, isLockedByUser( Channels.ALL_EVENTS ) && wrapper.isMarkedForCreation() );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
/*
                addConfirmedCell( item );
                target.add( item );
*/
                eventsDiv.addOrReplace( makeEventsTable() );
                target.add( eventsDiv );
                update( target, new Change(
                        Change.Type.Updated,
                        getPlan(),
                        "incidents"
                ) );
            }
        } );
        nameContainer.add( nameField );
        EntityLink eventLink = new EntityLink( "event-link", new PropertyModel<Event>( wrapper, "event" ) );
        eventLink.setVisible( !wrapper.isMarkedForCreation() );
        nameContainer.add( eventLink );
        item.add( new AttributeModifier(
                "class",
                new Model<String>( itemCssClasses( item.getIndex(), count ) ) ) );
    }

    private String itemCssClasses( int index, int count ) {
        String classes = index % 2 == 0 ? "even" : "odd";
        if ( index == count - 1 ) classes += " last";
        return classes;
    }

    private void addConfirmedCell( ListItem<EventWrapper> item ) {
        final EventWrapper wrapper = item.getModel().getObject();
        final CheckBox confirmedCheckBox = new CheckBox(
                "confirmed",
                new PropertyModel<Boolean>( wrapper, "confirmed" ) );
        makeVisible( confirmedCheckBox, wrapper.canBeConfirmed() );
        item.addOrReplace( confirmedCheckBox );
        confirmedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                eventsDiv.addOrReplace( makeEventsTable() );
                target.add( eventsDiv );
                update( target, new Change(
                        Change.Type.Updated,
                        getPlan(),
                        "incidents"
                ) );
            }
        } );
        confirmedCheckBox.setEnabled(
                isLockedByUser( Channels.ALL_EVENTS ) && ( wrapper.isMarkedForCreation()
                        || !( wrapper.isConfirmed() && getPlan().getIncidents().size() == 1 ) ) );
    }

    /**
     * Get event wrappers.
     *
     * @return a list of plan event wrappers
     */
    public List<EventWrapper> getWrappedEvents() {
        // Existing events
        List<EventWrapper> wrappers = new ArrayList<EventWrapper>();
        for ( Event event : getPlan().getIncidents() ) {
            wrappers.add( new EventWrapper( event, true ) );
        }
        // Defined events that are not incidents
        for ( Event plannedEvent : getQueryService().findPlannedEvents() ) {
            wrappers.add( new EventWrapper( plannedEvent, false ) );
        }
        // Sort
        Collections.sort( wrappers, new Comparator<EventWrapper>() {
            public int compare( EventWrapper pew1, EventWrapper pew2 ) {
                return collator.compare( pew1.getName(), pew2.getName() );
            }
        } );
        // New event
        EventWrapper creationEventWrapper = new EventWrapper( new Event(), false );
        creationEventWrapper.setMarkedForCreation( true );
        wrappers.add( creationEventWrapper );
        return wrappers;
    }

    public class EventWrapper implements Serializable {
        /**
         * Plan event.
         */
        private Event event;
        /**
         * Whether to be created.
         */
        private boolean markedForCreation;
        /**
         * Whether confirmed.
         */
        private boolean confirmed;

        protected EventWrapper( Event event, boolean confirmed ) {
            this.event = event;
            markedForCreation = false;
            this.confirmed = confirmed;
        }

        public Event getEvent() {
            return event;
        }

        public void setEvent( Event event ) {
            this.event = event;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public void setMarkedForCreation( boolean markedForCreation ) {
            this.markedForCreation = markedForCreation;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public void setConfirmed( boolean confirmed ) {
            this.confirmed = confirmed;
            Plan plan = ChannelsUser.plan();
            if ( confirmed ) {
                Event confirmedEvent = doSafeFindOrCreateType( Event.class, getName() );
                doCommand( new UpdatePlanObject( getUser().getUsername(), plan,
                        "incidents",
                        confirmedEvent,
                        UpdateObject.Action.Add ) );

            } else if ( !markedForCreation && getPlan().getIncidents().size() > 1 ) {
                try {
                    Event confirmedEvent = getCommunityService().find( Event.class, getEvent().getId() );
                    doCommand( new UpdatePlanObject( getUser().getUsername(), plan,
                            "incidents",
                            confirmedEvent,
                            UpdateObject.Action.Remove ) );
                    getCommander().cleanup( Event.class, getName() );
                } catch ( NotFoundException e ) {
                    LOG.warn( "event not found with id " + getEvent().getId() );
                }
            }
        }

        /**
         * Get event name.
         *
         * @return a String
         */
        public String getName() {
            return event.getName();
        }

        /**
         * Set to-be-created event name.
         *
         * @param value a string
         */
        public void setName( String value ) {
            if ( value != null && !value.trim().isEmpty() ) {
                event.setName( value );
                setConfirmed( true );
            }
        }

        /**
         * Wrapped event can be confirmed as plan incident?
         *
         * @return a boolean
         */
        public boolean canBeConfirmed() {
            return !event.getName().isEmpty();
        }

    }
}
