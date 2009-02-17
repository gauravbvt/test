package com.mindalliance.channels.graph;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.pages.Project;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;

import org.jgrapht.Graph;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 3:16:51 PM
 */
public class DefaultFlowDiagram implements FlowDiagram {

    /**
     * Diagram size constraint.
     * Diagram takes natural size if null.
     */
    private double[] diagramSize;

    /**
     * The scenario being diagrammed
     */
    private Scenario scenario;
    /**
     * A selected node
     */
    private Node selectedNode;
    /**
     * The diagram's maker
     */
    private DiagramFactory<Node, Flow> diagramFactory;

    /**
     * Whether the direction is LR or top-bottom
     */
    private String orientation;

    public DefaultFlowDiagram(
            Scenario scenario,
            Node selectedNode,
            DiagramFactory<Node, Flow> diagramFactory ) {
        this.scenario = scenario;
        this.selectedNode = selectedNode;
        this.diagramFactory = diagramFactory;
    }


    /**
     * {@inheritDoc}
     */
    public void setDiagramSize( double width, double height ) {
        diagramSize = new double[2];
        diagramSize[0] = width;
        diagramSize[1] = height;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrientation( String orientation ) {
        this.orientation = orientation;
    }

    /**
     * {@inheritDoc}
     */
    public void render( String outputFormat, OutputStream outputStream ) {
        Graph<Node, Flow> graph = Project.graphBuilder().buildDirectedGraph( scenario );
        GraphRenderer<Node, Flow> graphRenderer = diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if (selectedNode != null)  {
            graphRenderer.highlightVertex( selectedNode );
        }
        ScenarioMetaProvider metaProvider = new ScenarioMetaProvider(
                scenario,
                outputFormat,
                diagramFactory.getUrlFormat(),
                diagramFactory.getScenarioUrlFormat(),
                diagramFactory.getImageDirectory(),
                Project.analyst() );
        if ( diagramSize != null ) {
            metaProvider.setGraphSize( diagramSize );
        }
        if ( orientation != null ) {
            metaProvider.setGraphOrientation( orientation );
        }
        ScenarioDOTExporter dotExporter = new ScenarioDOTExporter( metaProvider );
        graphRenderer.render( graph,
                dotExporter,
                outputFormat,
                outputStream
        );

    }

    /**
     * {@inheritDoc}
     */
    public String makeImageMap() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        render( DiagramFactory.IMAGE_MAP, new BufferedOutputStream( baos ) );
        // System.out.println("*** Image map generated");
        return baos.toString();
    }
}
