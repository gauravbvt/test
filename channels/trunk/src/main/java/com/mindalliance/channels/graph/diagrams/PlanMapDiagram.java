/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.PlanMapGraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;
import org.jgrapht.DirectedGraph;
import org.springframework.core.io.Resource;

import java.io.OutputStream;
import java.util.List;

/**
 * A plan map diagram.
 */
public class PlanMapDiagram extends AbstractDiagram<Segment, SegmentRelationship> {

    /**
     * The segments mapped.
     */
    private List<Segment> segments;

    /**
     * Phase or event grouping segments.
     */
    private ModelObject selectedGroup;

    /**
     * Whether to group segments by phases.
     */
    private boolean groupByPhase;

    /**
     * Whether to group segments by events.
     */
    private boolean groupByEvent;

    /**
     * Selected vertex-segment.
     */
    private Segment selectedSegment;

    /**
     * Selected edge-segment relationship.
     */
    private SegmentRelationship selectedSgRel;

    /**
     * Provider of imagemap links.
     */
    private URLProvider<Segment, SegmentRelationship> uRLProvider;

    public PlanMapDiagram( List<Segment> segments, boolean groupByPhase, boolean groupByEvent,
                           ModelObject selectedGroup, Segment segment, SegmentRelationship scRel, double[] diagramSize,
                           String orientation ) {

        super( diagramSize, orientation );
        this.segments = segments;
        this.selectedGroup = selectedGroup;
        this.groupByPhase = groupByPhase;
        this.groupByEvent = groupByEvent;
        selectedSegment = segment;
        selectedSgRel = scRel;
    }

    @Override
    public void render( String ticket, String outputFormat, OutputStream outputStream, Analyst analyst,
                        DiagramFactory diagramFactory, QueryService queryService ) {
        GraphRenderer<Segment, SegmentRelationship> renderer = diagramFactory.getGraphRenderer();

        renderer.highlight( selectedSegment, selectedSgRel );
        renderer.render( queryService,
                         createGraph( queryService, analyst ),
                         createExporter( outputFormat, diagramFactory.getImageDirectory(), analyst, queryService ),
                         outputFormat,
                         ticket,
                         outputStream );
    }

    private DirectedGraph<Segment, SegmentRelationship> createGraph( QueryService queryService, Analyst analyst ) {
        return new PlanMapGraphBuilder( segments, queryService, analyst ).buildDirectedGraph();
    }

    /**
     * Provide an overridable provider for imagemaps links.
     *
     * @return the URL provider, or null to use the default one.
     */
    public URLProvider<Segment, SegmentRelationship> getURLProvider() {
        return uRLProvider;
    }

    public void setURLProvider( URLProvider<Segment, SegmentRelationship> uRLProvider ) {
        this.uRLProvider = uRLProvider;
    }

    private PlanMapDOTExporter createExporter( String outputFormat, Resource imageDirectory, Analyst analyst,
                                               QueryService queryService ) {

        PlanMapMetaProvider metaProvider =
                new PlanMapMetaProvider( segments, outputFormat, imageDirectory, analyst, queryService );
        metaProvider.setGroupByPhase( groupByPhase );
        metaProvider.setGroupByEvent( groupByEvent );
        metaProvider.setSelectedGroup( selectedGroup );
        metaProvider.setURLProvider( getURLProvider() );

        double[] diagramSize = getDiagramSize();
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );

        String orientation = getOrientation();
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );

        return new PlanMapDOTExporter( metaProvider );
    }
}
