package com.mindalliance.channels.pages.components.entities.analytics;

import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/6/12
 * Time: 1:30 PM
 */
public class PhaseAnalyticsPanel extends AbstractUpdatablePanel implements Guidable {

    /**
     * Maximum number of rows in phase segment table.
     */
    private static final int MAX_ROWS = 20;

    public PhaseAnalyticsPanel( String id, IModel<ModelEntity> model, Set<Long> expansions ) {
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
        addSegmentsTable();
    }

    private void addSegmentsTable() {
        PhaseSegmentTable phaseSegmentTable = new PhaseSegmentTable(
                "phaseSegmentTable",
                new PropertyModel<List<Segment>>( this, "segments"),
                MAX_ROWS
        );
        add( phaseSegmentTable );
    }

    /**
     * Get all segments in phase.
     * @return a list of segments
     */
    public List<Segment> getSegments() {
        return getQueryService().findAllSegmentsForPhase( getPhase() );
    }

    private Phase getPhase() {
        return (Phase) getModel().getObject();
    }

    /**
     * Table with segments in phase.
     */
    public class PhaseSegmentTable extends AbstractTablePanel<Segment> {
        /**
         * Phase segments model.
         */
        private IModel<List<Segment>> segmentsModel;

        public PhaseSegmentTable( String id, PropertyModel<List<Segment>> segmentsModel, int pageSize ) {
            super( id, null, pageSize, null );
            this.segmentsModel = segmentsModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeLinkColumn(
                    "Segment",
                    "",
                    "name",
                    EMPTY ) );
            columns.add( makeLinkColumn(
                    "for event",
                    "event",
                    "event.name",
                    EMPTY ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "phaseSegments",
                    columns,
                    new SortableBeanProvider<Segment>(
                            segmentsModel.getObject(),
                            "name" ),
                    getPageSize() ) );
        }
    }

}
