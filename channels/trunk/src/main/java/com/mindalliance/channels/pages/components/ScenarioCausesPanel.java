package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
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
    private IModel<ArrayList<ScenarioRelationship>> scRels;

    public ScenarioCausesPanel(
            String id,
            IModel<Project> model,
            IModel<ArrayList<ScenarioRelationship>> scRels,
            int pageSize,
            Set<Long> expansions ) {
        super( id, null, pageSize, expansions );
        this.scRels = scRels;
        init();
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        columns.add( makeLinkColumn( "Part", "part", "part.title", EMPTY ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "in scenario" ),
                "part.scenario.name", "part.scenario.name" ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "causes scenario" ),
                "caused.name", "caused.name" ) );
        List<Causation> causations = getCausations();
        add( new AjaxFallbackDefaultDataTable<Causation>(
                "causes",
                columns,
                new SortableBeanProvider<Causation>( causations, "part.name" ),
                getPageSize() ) );

    }

    private List<Causation> getCausations() {
        List<Causation> causations = new ArrayList<Causation>();
        for ( ScenarioRelationship scRel : scRels.getObject() ) {
            Scenario caused = scRel.getToScenario( getDqo() );
            for ( Part part : scRel.getInitiators() ) {
                causations.add( new Causation( part, caused ) );
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
        private Part part;
        /**
         * Scenario caused by part.
         */
        private Scenario caused;

        public Causation( Part part, Scenario caused ) {
            this.part = part;
            this.caused = caused;
        }

        public Part getPart() {
            return part;
        }


        public Scenario getCaused() {
            return caused;
        }

    }
}
