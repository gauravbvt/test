package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.entities.EntityLink;
import com.mindalliance.channels.util.Matcher;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

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
        List<EventWrapper> eventWrappers = getWrappedEvents();
        return new ListView<EventWrapper>( "event", eventWrappers ) {
            /** {@inheritDoc} */
            protected void populateItem( ListItem<EventWrapper> item ) {
                addEventCell( item );
                addConfirmedCell( item );
            }
        };
    }

    private void addEventCell( final ListItem<EventWrapper> item ) {
        item.setOutputMarkupId( true );
        final EventWrapper wrapper = item.getModelObject();
        WebMarkupContainer nameContainer = new WebMarkupContainer( "name-container" );
        item.add( nameContainer );
        final List<String> choices;
        if ( wrapper.isMarkedForCreation() ) {
            choices = getQueryService().findAllNames( Event.class );
        } else {
            choices = new ArrayList<String>();
        }
        // text field
        TextField<String> nameField = new AutoCompleteTextField<String>(
                "name-input",
                new PropertyModel<String>( wrapper, "name" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        nameField.setVisible( wrapper.isMarkedForCreation() );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addConfirmedCell( item );
                target.addComponent( item );
            }
        } );
        nameContainer.add( nameField );
        EntityLink eventLink = new EntityLink("event-link", new PropertyModel<Event>(wrapper, "event"));
        eventLink.setVisible( !wrapper.isMarkedForCreation() );
        nameContainer.add( eventLink );
    }

    private void addConfirmedCell( ListItem<EventWrapper> item ) {
        final EventWrapper wrapper = item.getModel().getObject();
        final CheckBox confirmedCheckBox = new CheckBox(
                "confirmed",
                new PropertyModel<Boolean>( wrapper, "confirmed" ) );
        makeVisible( confirmedCheckBox, wrapper.canBeConfirmed() );
        item.addOrReplace( confirmedCheckBox );
        final Plan plan = User.current().getPlan();
        confirmedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                eventsDiv.addOrReplace( makeEventsTable() );
                target.addComponent( eventsDiv );
                update( target, new Change(
                        Change.Type.Updated,
                        plan,
                        "incidents"
                ) );
            }
        } );
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

    private Plan getPlan() {
        return (Plan) getModel().getObject();
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
            if ( confirmed ) {
                Event confirmedEvent = getQueryService().findOrCreate( Event.class, getName() );
                doCommand( new UpdatePlanObject(
                        User.current().getPlan(),
                        "incidents",
                        confirmedEvent,
                        UpdateObject.Action.Add
                ) );

            } else if ( !markedForCreation ) {
                Event confirmedEvent = getQueryService().findOrCreate( Event.class, getName() );
                doCommand( new UpdatePlanObject(
                        User.current().getPlan(),
                        "incidents",
                        confirmedEvent,
                        UpdateObject.Action.Remove
                ) );
                getCommander().cleanup( Event.class, getName() );
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
            event.setName( value );
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
