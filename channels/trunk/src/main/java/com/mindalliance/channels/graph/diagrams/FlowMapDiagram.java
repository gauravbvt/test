package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.InternalFlow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.network.FlowMapGraphBuilder;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.pages.Project;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.OutputStream;
import java.util.Iterator;

/**
 * Flow map diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 3:16:51 PM
 */
public class FlowMapDiagram extends AbstractDiagram {

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
            DiagramFactory<Node, Flow> diagramFactory ) {
        super( diagramFactory );
        this.scenario = scenario;
        this.selectedNode = selectedNode;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public void render( String outputFormat, OutputStream outputStream ) {
        DiagramFactory<Node, Flow> diagramFactory = (DiagramFactory<Node, Flow>) getDiagramFactory();
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
                Project.analyst() );
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
