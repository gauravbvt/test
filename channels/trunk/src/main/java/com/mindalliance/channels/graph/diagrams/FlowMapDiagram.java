package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.analysis.graph.FlowMapGraphBuilder;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.Channels;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Flow map diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 3:16:51 PM
 */
public class FlowMapDiagram extends AbstractDiagram<Node,Flow> {

    /**
     * The scenario being diagrammed.
     */
    private Scenario scenario;
    /**
     * A selected node.
     */
    private Node selectedNode;

    public FlowMapDiagram(
            Scenario scenario,
            Node selectedNode,
            double[] diagramSize,
            String orientation ) {
        super( diagramSize, orientation );
        this.scenario = scenario;
        this.selectedNode = selectedNode;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public void render( String outputFormat, OutputStream outputStream ) {
        DiagramFactory<Node, Flow> diagramFactory = getDiagramFactory();
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder flowMapGraphBuilder = new FlowMapGraphBuilder( scenario );
        Graph<Node, Flow> graph = flowMapGraphBuilder.buildDirectedGraph();
        GraphRenderer<Node, Flow> graphRenderer = diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if ( selectedNode != null ) {
            graphRenderer.highlightVertex( selectedNode );
        }
        FlowMapMetaProvider metaProvider = new FlowMapMetaProvider(
                scenario,
                outputFormat,
                diagramFactory.getImageDirectory(),
                Channels.analyst() );
        if ( diagramSize != null ) {
            metaProvider.setGraphSize( diagramSize );
        }
        if ( orientation != null ) {
            metaProvider.setGraphOrientation( orientation );
        }
        FlowMapDOTExporter dotExporter = new FlowMapDOTExporter( metaProvider );
        graphRenderer.render( graph,
                dotExporter,
                outputFormat,
                outputStream
        );

    }
    
}
