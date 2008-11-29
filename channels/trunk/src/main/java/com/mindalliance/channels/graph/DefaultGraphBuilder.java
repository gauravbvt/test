package com.mindalliance.channels.graph;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

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
    public DirectedGraph<Node, Flow> buildScenarioGraph( Scenario scenario ) {
        DirectedGraph<Node, Flow> dgraph = new DefaultDirectedGraph<Node, Flow>( Flow.class );
        // add nodes as vertices
        Iterator<Node> nodes = scenario.nodes();
        while ( nodes.hasNext() ) {
            dgraph.addVertex( nodes.next() );  // added if not part of a flow
        }
        // add flows as edges
        Iterator<Flow> flows = scenario.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            dgraph.addEdge( flow.getSource(), flow.getTarget(), flow );
        }
        return dgraph;
    }


}
