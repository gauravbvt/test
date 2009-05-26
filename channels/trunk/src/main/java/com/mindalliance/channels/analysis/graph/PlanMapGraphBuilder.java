package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.model.Scenario;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Plan map graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 2, 2009
 * Time: 10:16:21 AM
 */
public class PlanMapGraphBuilder implements GraphBuilder<Scenario, ScenarioRelationship> {

    private List<Scenario> scenarios;

    private QueryService queryService;

    public PlanMapGraphBuilder( List<Scenario> scenarios, QueryService queryService ) {
        this.scenarios = scenarios;
        this.queryService = queryService;
    }

    /**
     * {@inheritDoc}
     */
    public DirectedGraph<Scenario, ScenarioRelationship> buildDirectedGraph() {
        DirectedGraph<Scenario, ScenarioRelationship> digraph =
                new DirectedMultigraph<Scenario, ScenarioRelationship>(
                        new EdgeFactory<Scenario, ScenarioRelationship>() {

                            public ScenarioRelationship createEdge( Scenario scenario, Scenario otherScenario ) {
                                return new ScenarioRelationship( scenario, otherScenario );
                            }

                        } );
        populateGraph( digraph, scenarios );
        return digraph;
    }

    private void populateGraph(
            DirectedGraph<Scenario, ScenarioRelationship> digraph,
            List<Scenario> scenarios ) {
        for ( Scenario scenario : scenarios ) {
            digraph.addVertex( scenario );
        }
        for ( Scenario scenario : scenarios ) {
            for ( Scenario other : scenarios ) {
                if ( scenario != other ) {
                    ScenarioRelationship scRel = queryService.findScenarioRelationship( scenario, other );
                    if ( scRel != null ) {
                        digraph.addEdge( scenario, other, scRel );
                    }
                }
            }
        }
    }
}


