package com.mindalliance.channels.pages.components.entities.analytics;

import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/6/12
 * Time: 12:02 PM
 */
public class EventAnalyticsPanel extends AbstractUpdatablePanel  implements Filterable, Guidable {

    /**
     * Maximum number of rows in event references table.
     */
    private static final int MAX_ROWS = 20;

    /**
     * Event references table.
     */
    private EventReferenceTable eventReferenceTable;

    /**
     * Model objects filtered on (show only where so and so is the actor etc.)
     */
    private List<Identifiable> filters;


    public EventAnalyticsPanel( String id, IModel<ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "analyzing";
    }

    @Override
    public String getHelpTopicId() {
        return "entity-analytics";
    }


    private void init() {
        filters = new ArrayList<Identifiable>();
        addEventReferenceTable();
    }

    private void addEventReferenceTable() {
        eventReferenceTable = new EventReferenceTable(
                "eventReferences",
                new PropertyModel<List<EventReference>>( this, "eventReferences" ),
                MAX_ROWS
        );
        eventReferenceTable.setOutputMarkupId( true );
        addOrReplace( eventReferenceTable );
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
        for ( Segment segment : queryService.findSegmentsRespondingTo( event ) ) {
            eventReferences.add( new EventReference( segment ) );
            for ( Part part : queryService.findPartsTerminatingEventPhaseIn( segment ) ) {
                eventReferences.add( new EventReference( part, EventRelation.Terminates ) );
            }
            for ( Part part : queryService.findPartsStartingWithEventIn( segment ) ) {
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
        Segment sc = eventReference.getSegment();
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
        target.add( eventReferenceTable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return filters.contains( identifiable );
    }

    private Event getEvent() {
        return (Event) getModel().getObject();
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
                    "Segment",
                    "segment",
                    "segment.name",
                    EMPTY,
                    EventAnalyticsPanel.this ) );
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
                            "segment.name" ),
                    getPageSize() ) );

        }
    }

    /**
     * A segment that responds to the event or a part that either terminates or initiates it.
     */
    public class EventReference implements Serializable {

        /**
         * Segment responding to event.
         */
        private Segment segment;
        /**
         * Part either initiating or terminating event.
         */
        private Part part;
        /**
         * Whether part is initiating or terminating.
         */
        private EventRelation relation;

        public EventReference( Segment segment ) {
            this.segment = segment;
            relation = EventRelation.RespondsTo;
        }

        public EventReference( Part part, EventRelation relation ) {
            this.part = part;
            this.relation = relation;
        }

        public Segment getSegment() {
            return segment;
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
                    return "terminates the event phase";
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
         * Terminates the event phase.
         */
        Terminates,
        /**
         * Starts with the event.
         */
        StartsWith
    }


}
