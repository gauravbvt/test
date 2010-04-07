package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.analysis.graph.PlanMapGraphBuilder;
import com.mindalliance.channels.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Segment;
import org.jgrapht.DirectedGraph;
import org.springframework.core.io.Resource;

import java.io.OutputStream;
import java.util.List;

/**
 * A plan map diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 1:58:47 PM
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

    /** Provider of imagemap links. */
    private URLProvider<Segment, SegmentRelationship> uRLProvider;

    public PlanMapDiagram(
            List<Segment> segments,
            boolean groupByPhase,
            boolean groupByEvent,
            ModelObject selectedGroup,
            Segment segment,
            SegmentRelationship scRel,
            double[] diagramSize,
            String orientation ) {

        super( diagramSize, orientation );
        this.segments = segments;
        this.selectedGroup = selectedGroup;
        this.groupByPhase = groupByPhase;
        this.groupByEvent = groupByEvent;
        selectedSegment = segment;
        selectedSgRel = scRel;
    }

    /** {@inheritDoc} */
    public void render( String outputFormat, OutputStream outputStream ) {
        DiagramFactory<Segment, SegmentRelationship> factory = getDiagramFactory();
        GraphRenderer<Segment, SegmentRelationship> renderer = factory.getGraphRenderer();

        renderer.highlight( selectedSegment, selectedSgRel );
        renderer.render(
                createGraph( factory.getQueryService() ),
                createExporter( outputFormat, factory.getImageDirectory() ), outputFormat,
                outputStream );
    }

    private DirectedGraph<Segment, SegmentRelationship> createGraph( QueryService queryService ) {
        return new PlanMapGraphBuilder( segments, queryService ).buildDirectedGraph();
    }

    /**
     * Provide an overridable provider for imagemaps links.
     * @return the URL provider, or null to use the default one.
     */
    public URLProvider<Segment, SegmentRelationship> getURLProvider() {
        return uRLProvider;
    }

    public void setURLProvider( URLProvider<Segment, SegmentRelationship> uRLProvider ) {
        this.uRLProvider = uRLProvider;
    }

    private PlanMapDOTExporter createExporter( String outputFormat, Resource imageDirectory ) {

        PlanMapMetaProvider metaProvider =
            new PlanMapMetaProvider( segments, outputFormat, imageDirectory, getAnalyst() );
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
