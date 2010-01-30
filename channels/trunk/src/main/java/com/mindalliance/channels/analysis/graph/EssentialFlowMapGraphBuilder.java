package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.SegmentObject;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Critical flow map graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 15, 2010
 * Time: 2:07:20 PM
 */
public class EssentialFlowMapGraphBuilder implements GraphBuilder<Node, Flow> {
    /**
     * Plan segment object presumed to fail.
     */
    private SegmentObject segmentObject;
    /**
     * Whether all alternates to downstream sharing flows are presumed to also fail.
     */
    boolean assumeFails;

    public EssentialFlowMapGraphBuilder( SegmentObject segmentObject, boolean assumeFails ) {
        this.segmentObject = segmentObject;
        this.assumeFails = assumeFails;
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
        populateSegmentGraph( digraph, segmentObject, assumeFails );
        return digraph;
    }

    private void populateSegmentGraph(
            DirectedGraph<Node, Flow> graph,
            SegmentObject segmentObject,
            boolean assumeFails ) {
        List<Flow> essentialFlows = segmentObject.getEssentialFlows( assumeFails );
        if ( segmentObject instanceof Flow ) {
            essentialFlows.add( (Flow) segmentObject );
        } else {
            graph.addVertex( ( Part )segmentObject );
        }
        for ( Flow flow : essentialFlows ) {
            Node source = flow.getSource();
            Node target = flow.getTarget();
            graph.addVertex( source );
            graph.addVertex( target );
            graph.addEdge( source, target, flow );
        }
    }
}
