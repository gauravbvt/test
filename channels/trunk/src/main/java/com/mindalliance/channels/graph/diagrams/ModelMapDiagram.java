/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.PlanMapGraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.graph.URLProvider;
import org.jgrapht.DirectedGraph;
import org.springframework.core.io.Resource;

import java.io.OutputStream;
import java.util.List;

/**
 * A plan map diagram.
 */
public class ModelMapDiagram extends AbstractDiagram<Segment, SegmentRelationship> {

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

    public ModelMapDiagram( List<Segment> segments, boolean groupByPhase, boolean groupByEvent,
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
                        DiagramFactory diagramFactory, CommunityService communityService ) throws DiagramException {
        GraphRenderer<Segment, SegmentRelationship> renderer = diagramFactory.getGraphRenderer();
        ModelService modelService = communityService.getModelService();
        renderer.highlight( selectedSegment, selectedSgRel );
        renderer.render( communityService,
                         createGraph( communityService, analyst ),
                         createExporter( outputFormat, diagramFactory.getImageDirectory(), analyst, communityService ),
                         outputFormat,
                         ticket,
                         outputStream );
    }

    private DirectedGraph<Segment, SegmentRelationship> createGraph( CommunityService communityService, Analyst analyst ) {
        return new PlanMapGraphBuilder( segments, communityService, analyst ).buildDirectedGraph();
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

    private ModelMapDOTExporter createExporter( String outputFormat, Resource imageDirectory, Analyst analyst,
                                               CommunityService communityService ) {

        ModelMapMetaProvider metaProvider =
                new ModelMapMetaProvider( segments, outputFormat, imageDirectory, analyst, communityService );
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

        return new ModelMapDOTExporter( metaProvider );
    }
}
