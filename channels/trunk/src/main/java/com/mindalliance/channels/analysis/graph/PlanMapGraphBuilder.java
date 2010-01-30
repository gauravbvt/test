package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.model.Segment;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Plan map graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 2, 2009
 * Time: 10:16:21 AM
 */
public class PlanMapGraphBuilder implements GraphBuilder<Segment, SegmentRelationship> {
    /**
     * All plan segments.
     */
    private List<Segment> segments;
    /**
     * Query service.
     */
    private QueryService queryService;

    public PlanMapGraphBuilder( List<Segment> segments, QueryService queryService ) {
        this.segments = segments;
        this.queryService = queryService;
    }

    /**
     * {@inheritDoc}
     */
    public DirectedGraph<Segment, SegmentRelationship> buildDirectedGraph() {
        DirectedGraph<Segment, SegmentRelationship> digraph =
                new DirectedMultigraph<Segment, SegmentRelationship>(
                        new EdgeFactory<Segment, SegmentRelationship>() {

                            public SegmentRelationship createEdge( Segment segment, Segment otherSegment ) {
                                return new SegmentRelationship( segment, otherSegment );
                            }

                        } );
        populateGraph( digraph, segments );
        return digraph;
    }

    private void populateGraph(
            DirectedGraph<Segment, SegmentRelationship> digraph,
            List<Segment> segments ) {
        for ( Segment segment : segments ) {
            digraph.addVertex( segment );
        }
        for ( Segment segment : segments ) {
            for ( Segment other : segments ) {
                if ( segment != other ) {
                    SegmentRelationship scRel = queryService.findSegmentRelationship( segment, other );
                    if ( scRel != null ) {
                        digraph.addEdge( segment, other, scRel );
                    }
                }
            }
        }
    }
}


