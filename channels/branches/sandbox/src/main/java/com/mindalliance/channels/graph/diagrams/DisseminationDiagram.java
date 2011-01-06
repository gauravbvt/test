package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.model.Dissemination;
import com.mindalliance.channels.analysis.graph.DisseminationGraphBuilder;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.model.*;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Dissemination diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 21, 2010
 * Time: 1:31:20 PM
 */
public class DisseminationDiagram extends AbstractDiagram<Node, Dissemination> {

    private SegmentObject segmentObject;
    private Subject subject;
    private boolean showTargets;

    public DisseminationDiagram(
            SegmentObject segmentObject,
            Subject subject,
            boolean showTargets,
            double[] diagramSize,
            String orientation ) {
        super( diagramSize, orientation );
        this.segmentObject = segmentObject;
        this.subject = subject;
        this.showTargets = showTargets;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public void render(
            String outputFormat,
            OutputStream outputStream,
            Analyst analyst,
            DiagramFactory diagramFactory ) {
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        DisseminationGraphBuilder graphBuilder = new DisseminationGraphBuilder(
                segmentObject,
                subject,
                showTargets
        );
        graphBuilder.setQueryService( diagramFactory.getQueryService() );
        Graph<Node, Dissemination> graph = graphBuilder.buildDirectedGraph();
        GraphRenderer<Node, Dissemination> graphRenderer = diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if ( segmentObject instanceof Part ) {
            graphRenderer.highlightVertex( (Part) segmentObject );
        }
        DisseminationMetaProvider metaProvider = new DisseminationMetaProvider(
                segmentObject,
                outputFormat,
                diagramFactory.getImageDirectory(),
                analyst );
        if ( diagramSize != null ) {
            metaProvider.setGraphSize( diagramSize );
        }
        if ( orientation != null ) {
            metaProvider.setGraphOrientation( orientation );
        }
        DisseminationDOTExporter dotExporter = new DisseminationDOTExporter( metaProvider );
        graphRenderer.render( graph,
                dotExporter,
                outputFormat,
                outputStream
        );
    }
}
