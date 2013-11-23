/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Plan map graph builder.
 */
public class PlanMapGraphBuilder implements GraphBuilder<Segment, SegmentRelationship> {

    /**
     * All plan segments.
     */
    private final List<Segment> segments;

    /**
     * Community service.
     */
    private final CommunityService communityService;

    private final Analyst analyst;

    public PlanMapGraphBuilder( List<Segment> segments, CommunityService communityService, Analyst analyst ) {
        this.segments = segments;
        this.communityService = communityService;
        this.analyst = analyst;
    }

    @Override
    public DirectedGraph<Segment, SegmentRelationship> buildDirectedGraph() {
        DirectedGraph<Segment, SegmentRelationship> digraph =
                new DirectedMultigraph<Segment, SegmentRelationship>( new EdgeFactory<Segment, SegmentRelationship>() {
                    @Override
                    public SegmentRelationship createEdge( Segment segment, Segment otherSegment ) {
                        return new SegmentRelationship( segment, otherSegment );
                    }
                } );
        populateGraph( digraph );
        return digraph;
    }

    private void populateGraph( DirectedGraph<Segment, SegmentRelationship> digraph ) {
        for ( Segment segment : segments )
            digraph.addVertex( segment );

        for ( Segment segment : segments ) {
            for ( Segment other : segments ) {
                if ( !segment.equals( other ) ) {
                    SegmentRelationship scRel = analyst.findSegmentRelationship( communityService, segment, other );
                    if ( scRel != null )
                        digraph.addEdge( segment, other, scRel );
                }
            }
        }
    }
}


