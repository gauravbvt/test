package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A table of causations between scenarios.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 9, 2009
 * Time: 12:59:18 PM
 */
public class ScenarioCausesPanel extends AbstractTablePanel<ScenarioRelationship> {
    /**
     * List of scenario relationships containing initiators.
     */
    private List<ScenarioRelationship> scRels;

    public ScenarioCausesPanel(
            String id,
            List<ScenarioRelationship> scRels,
            int pageSize,
            Set<Long> expansions ) {
        super( id, null, pageSize, expansions );
        this.scRels = scRels;
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
                new Model<String>( "in scenario" ),
                "cause.scenario.name",
                "cause.scenario.name" ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "has effect" ),
                "effect",
                "effect" ) );
        columns.add( makeLinkColumn(
                "on scenario",
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
        for ( ScenarioRelationship scRel : scRels ) {
            Scenario toScenario = scRel.getToScenario( getQueryService() );
            for ( Part part : scRel.getInitiators() ) {
                causations.add( new Causation( part, Causation.STARTS, toScenario ) );
            }
            for ( Part part : scRel.getTerminators() ) {
                causations.add( new Causation( part, Causation.TERMINATES, toScenario ) );
            }
        }
        return causations;
    }

    /**
     * A part causing a scenario.
     */
    public class Causation implements Serializable {
        /**
         * Part.
         */
        private Part cause;
        /**
         * Scenario caused by part.
         */
        private Scenario effected;
        /**
         * Cause-effect is not triggering (but termination)
         */
        public static final boolean TERMINATES = false;
        /**
         * Cause-effect is triggering
         */
        public static final boolean STARTS = true;
        /**
         * Whether effect on caused scenario is its triggering.
         */
        private boolean starts;

        public Causation( Part cause, boolean starts, Scenario effected ) {
            this.cause = cause;
            this.starts = starts;
            this.effected = effected;
        }

        public Part getCause() {
            return cause;
        }


        public Scenario getEffected() {
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
