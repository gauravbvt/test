package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.analysis.GraphBuilder;
import com.mindalliance.channels.analysis.data.Dissemination;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.model.Subject;
import com.mindalliance.channels.model.Transformation;
import com.mindalliance.channels.query.QueryService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Dissemination graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 21, 2010
 * Time: 3:10:11 PM
 */
public class DisseminationGraphBuilder implements GraphBuilder<Node, Dissemination> {
    private SegmentObject segmentObject;
    private Subject subject;
    private boolean showTargets;
    private QueryService queryService;

    public DisseminationGraphBuilder() {
    }

    public DisseminationGraphBuilder(
            SegmentObject segmentObject,
            Subject subject,
            boolean showTargets ) {

        this.segmentObject = segmentObject;
        this.subject = subject;
        this.showTargets = showTargets;
    }

    public DirectedGraph<Node, Dissemination> buildDirectedGraph() {
        DirectedGraph<Node, Dissemination> digraph = new DirectedMultigraph<Node, Dissemination>(
                new EdgeFactory<Node, Dissemination>() {
                    /**
                     * Separate id generator for diagram-based flows.
                     */
                    private long IdCounter = 1L;

                    public Dissemination createEdge( Node sourceVertex, Node targetVertex ) {
                        InternalFlow flow = new InternalFlow( sourceVertex, targetVertex, "" );
                        flow.setId( IdCounter++ );
                        return new Dissemination( flow, Transformation.Type.Identity, new Subject(), new Subject() );
                    }

                } );
        populateSegmentGraph( digraph );
        return digraph;
    }

    private void populateSegmentGraph( DirectedGraph<Node, Dissemination> graph ) {
        List<Dissemination> disseminations = getQueryService().findAllDisseminations(
                segmentObject,
                subject,
                showTargets );
        for ( Dissemination dissemination : disseminations ) {
            Node source = dissemination.getFlow().getSource();
            Node target = dissemination.getFlow().getTarget();
            graph.addVertex( source );
            graph.addVertex( target );
            graph.addEdge( source, target, dissemination );
        }
    }


    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

}
