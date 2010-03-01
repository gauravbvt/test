package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
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

    /**
     * A segment.
     */
    private Segment segment;

    public FlowMapGraphBuilder( Segment segment ) {
        this.segment = segment;
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
        populateSegmentGraph( digraph, segment );
        return digraph;
    }

    /**
     * {@inheritDoc}
     */
    private void populateSegmentGraph( Graph<Node, Flow> graph, Segment segment ) {
        // add nodes as vertices
        Iterator<Node> nodes = segment.nodes();
        while ( nodes.hasNext() ) {
            final Node node = nodes.next();
            if ( !node.sends().hasNext() && !node.receives().hasNext() )
                // added if not part of a flow
                graph.addVertex( node );
        }
        for ( Part initiator : segment.getQueryService().findInitiators( segment ) ) {
            graph.addVertex( initiator );
        }
        for ( Part terminator : segment.getQueryService().findExternalTerminators( segment ) ) {
            graph.addVertex( terminator );
        }
        // add flows as edges
        Iterator<Flow> flows = segment.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            graph.addVertex( flow.getSource() );
            graph.addVertex( flow.getTarget() );
            graph.addEdge( flow.getSource(), flow.getTarget(), flow );
            // add flows between capability connectors and external parts
            if ( flow.hasConnector() && flow.isCapability() ) {
                Connector connector = (Connector) flow.getTarget();
                Iterator<ExternalFlow> externalFlows = connector.externalFlows();
                while ( externalFlows.hasNext() ) {
                    ExternalFlow externalFlow = externalFlows.next();
                    graph.addVertex( externalFlow.getPart() );
                    graph.addEdge( externalFlow.getSource(), externalFlow.getTarget(), externalFlow );
                }
            }
        }
    }

}
