package com.mindalliance.channels.graph;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.util.SimpleCache;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;

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
     * Cached directed graphs
     */
    private SimpleCache<Scenario,DirectedGraph<Node,Flow>> digraphs =
            new SimpleCache<Scenario,DirectedGraph<Node,Flow>>();

    /**
     * Constructor
     */
    public DefaultGraphBuilder() {
    }

    /**
     * Build a graph from the scenario
     *
     * @param scenario The scenario being graphed
     * @return a DirectedGraph
     */
    public DirectedGraph<Node, Flow> buildDirectedGraph( Scenario scenario ) {
        DirectedGraph<Node, Flow> digraph = digraphs.get( scenario, scenario.getLastModified() );
        if ( digraph == null ) {
            digraph = new DirectedMultigraph<Node, Flow>(
                    new FlowFactory() );
            populateGraph( digraph, scenario );
            digraphs.put( scenario, digraph );
        }
        return digraph;
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
            final Node node = nodes.next();
            if ( !node.outcomes().hasNext() && !node.requirements().hasNext() )
                // added if not part of a flow
                graph.addVertex( node );
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
