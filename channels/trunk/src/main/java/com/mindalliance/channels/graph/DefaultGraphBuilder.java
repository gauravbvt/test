package com.mindalliance.channels.graph;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.Iterator;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

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
     * A timestamped graph
     */
    private static class BuiltGraph<V, N> {

        /**
         * When the graph was built.
         */
        private Date timestamp;
        /**
         * A cached graph
         */
        private Graph<V, N> graph;

        BuiltGraph( Graph<V, N> graph ) {
            this.graph = graph;
            timestamp = new Date();
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public Graph<V, N> getGraph() {
            return graph;
        }

    }

    /**
     * Cached directed graphs
     */
    private Map<Scenario, BuiltGraph<Node, Flow>> digraphs =
            new HashMap<Scenario, BuiltGraph<Node, Flow>>();

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
        DirectedGraph<Node, Flow> digraph = getDirectedGraphFromCache( scenario );
        if ( digraph == null ) {
            digraph = new DefaultDirectedGraph<Node, Flow>(
                    new FlowFactory() );
            populateGraph( digraph, scenario );
            cacheDirectedGraph( scenario, digraph );
        }
        return digraph;
    }

    private DirectedGraph<Node, Flow> getDirectedGraphFromCache( Scenario scenario ) {
        DirectedGraph<Node, Flow> digraph = null;
        BuiltGraph<Node, Flow> builtGraph = digraphs.get( scenario );
        // Return cached graph if not outdated, else return null
        if ( builtGraph != null && !builtGraph.getTimestamp().before( scenario.lastModified() ) ) {
            digraph = (DirectedGraph<Node, Flow>) builtGraph.getGraph();
        }
        return digraph;
    }

    private void cacheDirectedGraph( Scenario scenario, DirectedGraph<Node, Flow> digraph ) {
        digraphs.put( scenario, new BuiltGraph<Node, Flow>( digraph ) );
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
