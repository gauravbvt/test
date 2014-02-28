package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
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
    private ModelManager modelManager;

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
                addIncidentCell( item );
                addDeleteCell( item );
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
        makeVisible( nameField, isPlanner() && getCollaborationModel().isDevelopment() && wrapper.isMarkedForCreation() );
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
                        getCollaborationModel(),
                        "incidents"
                ) );
            }
        } );
        addInputHint( nameField, "Enter the name of a new event (then press enter)" );
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

    private void addIncidentCell( ListItem<EventWrapper> item ) {
        final EventWrapper wrapper = item.getModel().getObject();
        final CheckBox incidentCheckBox = new CheckBox(
                "incident",
                new PropertyModel<Boolean>( wrapper, "incident" ) );
        makeVisible( incidentCheckBox, wrapper.canBeConfirmed() );
        incidentCheckBox.setOutputMarkupId( true );
        item.addOrReplace( incidentCheckBox );
        incidentCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                eventsDiv.addOrReplace( makeEventsTable() );
                target.add( eventsDiv );
                update( target, new Change(
                        Change.Type.Updated,
                        getCollaborationModel(),
                        "incidents"
                ) );
            }
        } );
        boolean mustBeIncident = !wrapper.isMarkedForCreation() && wrapper.isIncident() && !getQueryService().isEventCausedByATask( wrapper.getEvent() );
        incidentCheckBox.setEnabled(
                isLockedByUser( Channels.ALL_EVENTS )
                        && !mustBeIncident
                        && ( wrapper.isMarkedForCreation()
                        || !( wrapper.isIncident() && getCollaborationModel().getIncidents().size() == 1 ) ) );
    }

    private void addDeleteCell( ListItem<EventWrapper> item ) {
        final EventWrapper wrapper = item.getModel().getObject();
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "delete",
                "Delete event?"
        ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                wrapper.removeIncident();
                eventsDiv.addOrReplace( makeEventsTable() );
                target.add( eventsDiv );
                update( target, new Change(
                        Change.Type.Updated,
                        getCollaborationModel(),
                        "incidents"
                ) );
            }
        };
        makeVisible( deleteLink, wrapper.canBeRemoved() );
        deleteLink.setOutputMarkupId( true );
        item.addOrReplace( deleteLink );
    }

    /**
     * Get event wrappers.
     *
     * @return a list of plan event wrappers
     */
    public List<EventWrapper> getWrappedEvents() {
        // Existing events
        List<EventWrapper> wrappers = new ArrayList<EventWrapper>();
        for ( Event event : getCollaborationModel().getIncidents() ) {
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
        private boolean incident;

        protected EventWrapper( Event event, boolean incident ) {
            this.event = event;
            markedForCreation = false;
            this.incident = incident;
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

        public boolean isIncident() {
            return incident;
        }

        public void setIncident( boolean incident ) {
            this.incident = incident;
            CollaborationModel collaborationModel = getCollaborationModel();
            if ( incident ) {
                Event confirmedEvent = doSafeFindOrCreateType( Event.class, getName() );
                doCommand( new UpdateModelObject(
                        getUser().getUsername(),
                        collaborationModel,
                        "incidents",
                        confirmedEvent,
                        UpdateObject.Action.AddUnique ) );

            } else if ( !markedForCreation && getCollaborationModel().getIncidents().size() > 1 ) {
                try {
                    Event confirmedEvent = getCommunityService().find( Event.class, getEvent().getId() );
                    doCommand( new UpdateModelObject(
                            getUser().getUsername(),
                            collaborationModel,
                            "incidents",
                            confirmedEvent,
                            UpdateObject.Action.Remove ) );
                    getCommander().cleanup( Event.class, getName() );
                } catch ( NotFoundException e ) {
                    LOG.warn( "Event not found with id " + getEvent().getId() );
                }
            }
        }

        public void removeIncident() {
            if ( canBeRemoved() ) {
                try {
                    Event confirmedEvent = getCommunityService().find( Event.class, getEvent().getId() );
                    doCommand( new UpdateModelObject(
                            getUser().getUsername(),
                            getCollaborationModel(),
                            "incidents",
                            confirmedEvent,
                            UpdateObject.Action.Remove ) );
                } catch ( NotFoundException e ) {
                    LOG.warn( "Event not found with id " + getEvent().getId() );
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
                setIncident( true );
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

        public boolean canBeRemoved() {
            return isIncident() && getQueryService().countReferences( event ) == 1;
        }

    }
}
