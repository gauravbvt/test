/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.Dissemination;
import com.mindalliance.channels.core.model.InternalFlow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.model.Transformation.Type;
import com.mindalliance.channels.core.model.time.Delay;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Dissemination graph builder.
 */
public class DisseminationGraphBuilder implements GraphBuilder<Node, Dissemination> {

    private final SegmentObject segmentObject;

    private final Subject subject;

    private final boolean showTargets;

    private final QueryService queryService;

    public DisseminationGraphBuilder( SegmentObject segmentObject, Subject subject, boolean showTargets,
                                      QueryService queryService ) {

        this.segmentObject = segmentObject;
        this.subject = subject;
        this.showTargets = showTargets;
        this.queryService = queryService;
    }

    public DirectedGraph<Node, Dissemination> buildDirectedGraph() {
        DirectedGraph<Node, Dissemination> digraph =
                new DirectedMultigraph<Node, Dissemination>( new EdgeFactory<Node, Dissemination>() {
                    /**
                     * Separate id generator for diagram-based flows.
                     */
                    private long IdCounter = 1L;

                    public Dissemination createEdge( Node sourceVertex, Node targetVertex ) {
                        InternalFlow flow = new InternalFlow( sourceVertex, targetVertex, "" );
                        flow.setId( IdCounter++ );
                        return new Dissemination( flow, Type.Identity, new Delay(), new Subject(), new Subject() );
                    }
                } );
        populateDisseminationGraph( digraph );
        return digraph;
    }

    private void populateDisseminationGraph( DirectedGraph<Node, Dissemination> graph ) {
        List<Dissemination> disseminations = queryService.findAllDisseminations( segmentObject, subject, showTargets );
        for ( Dissemination dissemination : disseminations ) {
            Node source = dissemination.getFlow().getSource();
            Node target = dissemination.getFlow().getTarget();
            graph.addVertex( source );
            graph.addVertex( target );
            graph.addEdge( source, target, dissemination );
        }
    }
}
