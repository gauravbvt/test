package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A table of causations between segments.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 9, 2009
 * Time: 12:59:18 PM
 */
public class SegmentCausesPanel extends AbstractTablePanel<SegmentRelationship> {
    /**
     * List of segment relationships containing initiators.
     */
    private List<SegmentRelationship> sgRels;

    public SegmentCausesPanel(
            String id,
            List<SegmentRelationship> sgRels,
            int pageSize,
            Set<Long> expansions ) {
        super( id, null, pageSize, expansions );
        this.sgRels = sgRels;
        init();
    }

    @SuppressWarnings( "unchecked" )
    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        columns.add( makeLinkColumn(
                "Task",
                "cause",
                "cause.title",
                EMPTY ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "in segment" ),
                "cause.segment.name",
                "cause.segment.name" ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "has effect" ),
                "effect",
                "effect" ) );
        columns.add( makeLinkColumn(
                "on segment",
                "effected",
                "effected.name",
                EMPTY ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "because" ),
                "explanation",
                "explanation" ) );
        List<Causation> causations = getCausations();
        add( new AjaxFallbackDefaultDataTable(
                "causes",
                columns,
                new SortableBeanProvider<Causation>( causations, "cause.name" ),
                getPageSize() ) );

    }

    private List<Causation> getCausations() {
        List<Causation> causations = new ArrayList<Causation>();
        for ( SegmentRelationship scRel : sgRels ) {
            Segment toSegment = scRel.getToSegment( getQueryService() );
            for ( Part part : scRel.getInitiators() ) {
                causations.add( new Causation( part, Causation.STARTS, toSegment ) );
            }
            for ( Part part : scRel.getTerminators() ) {
                causations.add( new Causation( part, Causation.TERMINATES, toSegment ) );
            }
        }
        return causations;
    }

    /**
     * A part causing a segment.
     */
    public class Causation implements Serializable {
        /**
         * Part.
         */
        private Part cause;
        /**
         * Segment caused by part.
         */
        private Segment effected;
        /**
         * Cause-effect is not triggering (but termination)
         */
        public static final boolean TERMINATES = false;
        /**
         * Cause-effect is triggering
         */
        public static final boolean STARTS = true;
        /**
         * Whether effect on caused segment is its triggering.
         */
        private boolean starts;

        public Causation( Part cause, boolean starts, Segment effected ) {
            assert cause != null;
            this.cause = cause;
            this.starts = starts;
            assert effected != null;
            this.effected = effected;
        }

        public Part getCause() {
            return cause;
        }


        public Segment getEffected() {
            return effected;
        }

        /**
         * Return name of effect.
         *
         * @return a string
         */
        public String getEffect() {
            return starts ? " triggers " : " terminates ";
        }

        /**
         * Explains causation.
         *
         * @return a string
         */
        public String getExplanation() {
            if ( starts ) {
                return "it " + effected.initiationCause( cause );
            } else {
                return "it " + effected.terminationCause( cause );
            }
        }

    }
}
