/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.FailureImpactsGraphBuilder;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Essential flow map diagram.
 */
public class FailureImpactsDiagram extends AbstractDiagram<Node, Flow> {

    private SegmentObject segmentObject;

    private boolean assumeFails;

    public FailureImpactsDiagram( SegmentObject segmentObject, boolean assumeFails, double[] diagramSize,
                                  String orientation ) {
        super( diagramSize, orientation );
        this.segmentObject = segmentObject;
        this.assumeFails = assumeFails;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void render( String ticket, String outputFormat, OutputStream outputStream, Analyst analyst,
                        DiagramFactory diagramFactory, CommunityService communityService ) throws DiagramException {
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        ModelService modelService = communityService.getModelService();
        FailureImpactsGraphBuilder graphBuilder = new FailureImpactsGraphBuilder( segmentObject, assumeFails,
                modelService );
        Graph<Node, Flow> graph = graphBuilder.buildDirectedGraph();
        GraphRenderer<Node, Flow> graphRenderer = diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if ( segmentObject instanceof Part )
            graphRenderer.highlightVertex( (Part) segmentObject );
        else
            graphRenderer.highlightEdge( (Flow) segmentObject );
        FailureImpactsMetaProvider metaProvider = new FailureImpactsMetaProvider( (ModelObject) segmentObject,
                                                                                  outputFormat,
                                                                                  diagramFactory.getImageDirectory(),
                                                                                  analyst,
                communityService );
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        FailureImpactsDOTExporter dotExporter = new FailureImpactsDOTExporter( metaProvider );
        graphRenderer.render( communityService, graph, dotExporter, outputFormat, ticket, outputStream );
    }
}
