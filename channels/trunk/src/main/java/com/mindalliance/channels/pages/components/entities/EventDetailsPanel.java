package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.util.SemMatch;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Plan event details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 9:22:26 AM
 */
public class EventDetailsPanel extends EntityDetailsPanel implements Filterable {
    /**
     * Web markup container.
     */
    private WebMarkupContainer moDetailsDiv;
    /**
     * Event references table.
     */
    private EventReferenceTable eventReferenceTable;

    /**
     * Model objects filtered on (show only where so and so is the actor etc.)
     */
    private List<Identifiable> filters;
    /**
     * Text field for scope as name of place.
     */
    private TextField scopeField;
    /**
     * Maximum number of rows in event references table.
     */
    private static final int MAX_ROWS = 20;

    public EventDetailsPanel( String id, IModel<? extends ModelObject> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        moDetailsDiv.add(
                new ModelObjectLink( "scope-link",
                        new PropertyModel<Organization>( getPlanEvent(), "scope" ),
                        new Model<String>( "Location" ) ) );
        final List<String> choices = getQueryService().findAllNames( Place.class );
        scopeField = new AutoCompleteTextField<String>( "scope",
                new PropertyModel<String>( this, "scopeName" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( SemMatch.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        scopeField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPlanEvent(), "scope" ) );
            }
        } );
        moDetailsDiv.add( scopeField );
        filters = new ArrayList<Identifiable>();
        adjustFields();
        addEventReferenceTable();
    }

    private void adjustFields() {
        scopeField.setEnabled( isLockedByUser( getEvent() ) );
    }

    private void addEventReferenceTable() {
        eventReferenceTable = new EventReferenceTable(
                "eventReferences",
                new PropertyModel<List<EventReference>>( this, "eventReferences" ),
                MAX_ROWS
        );
        eventReferenceTable.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( eventReferenceTable );
    }

    /**
     * Set organization's location from name, if not null or empty.
     *
     * @param name a String
     */
    public void setScopeName( String name ) {
        Event event = getPlanEvent();
        Place oldPlace = event.getScope();
        String oldName = oldPlace == null ? "" : oldPlace.getName();
        Place newPlace = null;
        if ( name == null || name.trim().isEmpty() )
            newPlace = null;
        else {
            if ( oldPlace == null || !isSame( name, oldName ) )
                newPlace = getQueryService().findOrCreate( Place.class, name );
        }
        doCommand( new UpdatePlanObject( event, "scope", newPlace ) );
        getCommander().cleanup( Place.class, oldName );
    }

    /**
     * Get organization's location's name.
     *
     * @return a String
     */
    public String getScopeName() {
        Place scope = ( getPlanEvent() ).getScope();
        return scope == null ? "" : scope.getName();
    }

    private Event getPlanEvent() {
        return (Event) getEntity();
    }

    /**
     * Get all references to the event that are not filtered out.
     *
     * @return a list of event references
     */
    @SuppressWarnings( "unchecked" )
    public List<EventReference> getEventReferences() {
        List<EventReference> eventReferences = new ArrayList<EventReference>();
        Event event = getEvent();
        QueryService queryService = getQueryService();
        for ( Scenario scenario : queryService.findScenariosRespondingTo( event ) ) {
            eventReferences.add( new EventReference( scenario ) );
            for ( Part part : queryService.findPartsTerminatingEventIn( scenario ) ) {
                eventReferences.add( new EventReference( part, EventRelation.Terminates ) );
            }
            for ( Part part : queryService.findPartsStartingWithEventIn( scenario ) ) {
                eventReferences.add( new EventReference( part, EventRelation.StartsWith ) );
            }
        }
        for ( Part part : queryService.findPartsInitiatingEvent( event ) ) {
            eventReferences.add( new EventReference( part, EventRelation.Initiates ) );
        }
        return (List<EventReference>) CollectionUtils.select(
                eventReferences,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !isFilteredOut( (EventReference) obj );
                    }
                } );
    }

    private boolean isFilteredOut( EventReference eventReference ) {
        Scenario sc = eventReference.getScenario();
        return !filters.isEmpty() && ( sc == null || !filters.contains( sc ) );
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        // Property ignored since no two properties filtered are ambiguous on type.
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( identifiable );
        } else {
            filters.add( identifiable );
        }
        addEventReferenceTable();
        target.addComponent( eventReferenceTable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return filters.contains( identifiable );
    }

    private Event getEvent() {
        return (Event) getEntity();
    }

    /**
     * Event reference table.
     */
    public class EventReferenceTable extends AbstractTablePanel<EventReference> {
        /**
         * Event reference model.
         */
        private IModel<List<EventReference>> eventReferencesModel;

        public EventReferenceTable(
                String id,
                IModel<List<EventReference>> eventReferencesModel,
                int pageSize ) {
            super( id, null, pageSize, null );
            this.eventReferencesModel = eventReferencesModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeFilterableLinkColumn(
                    "Scenario",
                    "scenario",
                    "scenario.name",
                    EMPTY,
                    EventDetailsPanel.this ) );
            columns.add( makeLinkColumn(
                    "Task",
                    "part",
                    "part.title",
                    EMPTY ) );
            columns.add( makeColumn(
                    "Relationship",
                    "relation",
                    "relation",
                    EMPTY ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "eventReferences",
                    columns,
                    new SortableBeanProvider<EventReference>(
                            eventReferencesModel.getObject(),
                            "scenario.name" ),
                    getPageSize() ) );

        }
    }

    /**
     * A scenario that responds to the event or a part that either terminates or initiates it.
     */
    public class EventReference implements Serializable {

        /**
         * Scenario responding to event.
         */
        private Scenario scenario;
        /**
         * Part either initating or terminating event.
         */
        private Part part;
        /**
         * Whether part is initiating or terminating.
         */
        private EventRelation relation;

        public EventReference( Scenario scenario ) {
            this.scenario = scenario;
            relation = EventRelation.RespondsTo;
        }

        public EventReference( Part part, EventRelation relation ) {
            this.part = part;
            this.relation = relation;
        }

        public Scenario getScenario() {
            return scenario;
        }

        public Part getPart() {
            return part;
        }

        /**
         * Get the relationship to the event.
         *
         * @return a string
         */
        public String getRelation() {
            switch ( relation ) {
                case Initiates:
                    return "initiates the event";
                case Terminates:
                    return "terminates the event";
                case StartsWith:
                    return "is started by the event";
                case RespondsTo:
                    return "responds to the event";
                default:
                    return null;
            }
        }


    }

    /**
     * Enumeration of possible relationships to an event.
     */
    public enum EventRelation {
        /**
         * In reponse to the event.
         */
        RespondsTo,
        /**
         * Initiates the event.
         */
        Initiates,
        /**
         * Terminates the event.
         */
        Terminates,
        /**
         * Starts with the event.
         */
        StartsWith
    }


}
