package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.graph.GraphBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.Iterator;

/**
 * Flow map graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 2, 2009
 * Time: 10:13:37 AM
 */
public class FlowMapGraphBuilder implements GraphBuilder<Node, Flow> {

        private Scenario scenario;

        public FlowMapGraphBuilder( Scenario scenario ) {
            this.scenario = scenario;
        }

        public DirectedGraph<Node, Flow> buildDirectedGraph() {
            DirectedGraph<Node, Flow> digraph = new DirectedMultigraph<Node, Flow>(
                    new EdgeFactory<Node, Flow>() {
                        /**
                         * Separate id generator for diagram-based flows.
                         */
                        private long IdCounter = 1L;
                        public Flow createEdge( Node sourceVertex, Node targetVertex ) {
                            InternalFlow flow = new InternalFlow( sourceVertex, targetVertex, "" );
                            flow.setId( IdCounter++ );
                            return flow;
                        }

                    } );
            populateScenarioGraph( digraph, scenario );
            return digraph;
        }

        /**
         * {@inheritDoc}
         */
        private void populateScenarioGraph( Graph<Node, Flow> graph, Scenario scenario ) {
            // add nodes as vertices
            Iterator<Node> nodes = scenario.nodes();
            while ( nodes.hasNext() ) {
                final Node node = nodes.next();
                if ( !node.outcomes().hasNext() && !node.requirements().hasNext() )
                    // added if not part of a flow
                    graph.addVertex( node );
            }
            for ( Part initiator : scenario.getInitiators()) {
                graph.addVertex( initiator );
            }
            // add flows as edges
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                graph.addVertex( flow.getSource() );
                graph.addVertex( flow.getTarget() );
                graph.addEdge( flow.getSource(), flow.getTarget(), flow );
            }
        }

    }
