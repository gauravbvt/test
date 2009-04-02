package com.mindalliance.channels.analysis.network;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.graph.GraphBuilder;

import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

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

    private DataQueryObject dqo;

    public PlanMapGraphBuilder( List<Scenario> scenarios, DataQueryObject dqo ) {
        this.scenarios = scenarios;
        this.dqo = dqo;
    }

    /**
     * {@inheritDoc}
     */
    public DirectedGraph<Scenario, ScenarioRelationship> buildDirectedGraph() {
        DirectedGraph<Scenario, ScenarioRelationship> digraph =
                new DirectedMultigraph<Scenario, ScenarioRelationship>(
                        new EdgeFactory<Scenario, ScenarioRelationship>() {
                            /**
                             * Separate id generator for diagram-based flows.
                             */
                            private long IdCounter = 1L;

                            public ScenarioRelationship createEdge( Scenario scenario, Scenario otherScenario ) {
                                ScenarioRelationship scRel = new ScenarioRelationship( scenario, otherScenario );
                                scRel.setId( IdCounter++ );
                                return scRel;
                            }

                        } );
        populateScenariosGraph( digraph, scenarios );
        return digraph;
    }

    private void populateScenariosGraph(
            DirectedGraph<Scenario, ScenarioRelationship> digraph,
            List<Scenario> scenarios ) {
        for ( Scenario scenario : scenarios ) {
            digraph.addVertex( scenario );
            for ( Scenario other : scenarios ) {
                if ( scenario != other ) {
                    ScenarioRelationship scRel = dqo.findScenarioRelationship( scenario, other );
                    if ( scRel != null ) digraph.addEdge( scenario, other, scRel );
                }
            }
        }
    }

}
