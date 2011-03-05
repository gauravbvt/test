package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.analysis.graph.FailureImpactsGraphBuilder;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.SegmentObject;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Essential flow map diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 15, 2010
 * Time: 1:44:56 PM
 */
public class FailureImpactsDiagram extends AbstractDiagram<Node, Flow> {

    private SegmentObject segmentObject;
    private boolean assumeFails;

    public FailureImpactsDiagram(
            SegmentObject segmentObject,
            boolean assumeFails,
            double[] diagramSize,
            String orientation ) {
        super( diagramSize, orientation );
        this.segmentObject = segmentObject;
        this.assumeFails = assumeFails;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public void render(
            String ticket, String outputFormat,
            OutputStream outputStream,
            Analyst analyst,
            DiagramFactory diagramFactory ) {
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        FailureImpactsGraphBuilder graphBuilder = new FailureImpactsGraphBuilder( segmentObject, assumeFails );
        graphBuilder.setQueryService( diagramFactory.getQueryService() );
        Graph<Node, Flow> graph = graphBuilder.buildDirectedGraph();
        GraphRenderer<Node, Flow> graphRenderer = diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if ( segmentObject instanceof Part ) {
            graphRenderer.highlightVertex( (Part) segmentObject );
        } else {
            graphRenderer.highlightEdge( (Flow) segmentObject );
        }
        FailureImpactsMetaProvider metaProvider = new FailureImpactsMetaProvider(
                (ModelObject) segmentObject,
                outputFormat,
                diagramFactory.getImageDirectory(),
                analyst );
        if ( diagramSize != null ) {
            metaProvider.setGraphSize( diagramSize );
        }
        if ( orientation != null ) {
            metaProvider.setGraphOrientation( orientation );
        }
        FailureImpactsDOTExporter dotExporter = new FailureImpactsDOTExporter( metaProvider );
        graphRenderer.render( graph,
                dotExporter,
                outputFormat,
                ticket,
                outputStream
        );

    }


}
