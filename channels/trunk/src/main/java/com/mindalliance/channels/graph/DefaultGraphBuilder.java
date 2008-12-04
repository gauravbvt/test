package com.mindalliance.channels.graph;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.BlockCutpointGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.jgrapht.graph.Pseudograph;

import java.util.Iterator;

/**
 * Builds the graph structure of a scenario
 * <p/>
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 3:02:40 PM
 */
public class DefaultGraphBuilder implements GraphBuilder {

    /**
     * Constructor
     */
    public DefaultGraphBuilder() {
    }

    /**
     * Build a graph from the scenario
     *
     * @return a DirectedGraph
     */
    public DirectedGraph<Node, Flow> buildDirectedGraph( Scenario scenario ) {
        DirectedGraph<Node, Flow> dgraph = new DefaultDirectedGraph<Node, Flow>( Flow.class );
        populateGraph( dgraph, scenario );
        return dgraph;
    }

    /**
     * Populates a graph from a scenario.
     *
     * @param graph    -- a graph
     * @param scenario -- a scenario
     */
    private void populateGraph( Graph<Node, Flow> graph, Scenario scenario ) {
        // add nodes as vertices
        Iterator<Node> nodes = scenario.nodes();
        while ( nodes.hasNext() ) {
            graph.addVertex( nodes.next() );  // added if not part of a flow
        }
        // add flows as edges
        Iterator<Flow> flows = scenario.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            graph.addEdge( flow.getSource(), flow.getTarget(), flow );
        }
    }


}
