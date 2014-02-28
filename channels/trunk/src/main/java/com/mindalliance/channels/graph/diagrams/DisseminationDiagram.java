/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Dissemination;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.DisseminationGraphBuilder;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Dissemination diagram.
 */
public class DisseminationDiagram extends AbstractDiagram<Node, Dissemination> {

    private SegmentObject segmentObject;

    private Subject subject;

    private boolean showTargets;

    public DisseminationDiagram( SegmentObject segmentObject, Subject subject, boolean showTargets,
                                 double[] diagramSize, String orientation ) {
        super( diagramSize, orientation );
        this.segmentObject = segmentObject;
        this.subject = subject;
        this.showTargets = showTargets;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render( String outputFormat, String ticket, OutputStream outputStream, Analyst analyst,
                        DiagramFactory diagramFactory, CommunityService communityService ) throws DiagramException {
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        DisseminationGraphBuilder graphBuilder = new DisseminationGraphBuilder( segmentObject, subject, showTargets,
                communityService.getModelService() );
        Graph<Node, Dissemination> graph = graphBuilder.buildDirectedGraph();
        GraphRenderer<Node, Dissemination> graphRenderer = diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if ( segmentObject instanceof Part )
            graphRenderer.highlightVertex( (Part) segmentObject );
        DisseminationMetaProvider metaProvider = new DisseminationMetaProvider(
                segmentObject,
                outputFormat,
                diagramFactory.getImageDirectory(),
                analyst,
                communityService );
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        DisseminationDOTExporter dotExporter = new DisseminationDOTExporter( metaProvider );
        graphRenderer.render( communityService, graph, dotExporter, ticket, outputFormat, outputStream );
    }
}
