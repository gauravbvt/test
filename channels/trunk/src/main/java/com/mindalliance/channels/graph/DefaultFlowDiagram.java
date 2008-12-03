package com.mindalliance.channels.graph;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import org.jgrapht.Graph;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 2:56:50 PM
 */
public class DefaultFlowDiagram implements FlowDiagram<Node, Flow> {
    /**
     * A graph builder
     */
    private GraphBuilder graphBuilder;
    /**
     * A GraphRenderer for nodes and flows
     */
    private GraphRenderer<Node, Flow> graphRenderer;
    /**
     * 0: scenario id, 1: node id
     */
    private String urlFormat = "?scenario={0}&node={1}";
    /**
     * Path to image directory
     */
    private String imageDirectory;

    /**
     * Constructor
     */
    public DefaultFlowDiagram() {
    }

    public void setGraphBuilder( GraphBuilder graphBuilder ) {
        this.graphBuilder = graphBuilder;
    }

    public void setGraphRenderer( GraphRenderer<Node, Flow> graphRenderer ) {
        this.graphRenderer = graphRenderer;
    }

    public String getUrlFormat() {
        return urlFormat;
    }

    public void setUrlFormat( String urlFormat ) {
        this.urlFormat = urlFormat;
    }

    public String getImageDirectory() {
        return imageDirectory;
    }

    public void setImageDirectory( String imageDirectory ) {
        this.imageDirectory = imageDirectory;
    }

    /**
     * Produces the PNG stream of a directed graph diagram of the scenario.
     *
     * @param scenario     A scenario
     * @param selectedNode The scenario node currently selected
     * @param analyst      The scenario analyst used to detect issues
     * @param pngOut          Output stream contaiing the diagram as PNG
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    public void getPNG( Scenario scenario, Node selectedNode, ScenarioAnalyst analyst, OutputStream pngOut ) throws DiagramException {
        Graph<Node, Flow> graph = graphBuilder.buildScenarioGraph( scenario );
        graphRenderer.highlightVertex( selectedNode );
        render( graph, PNG, scenario, analyst, pngOut );
    }

    /**
     * Produces the SVG stream of a directed graph diagram of the scenario.
     *
     * @param scenario     A scenario
     * @param selectedNode The scenario node currently selected
     * @param analyst      The scenario analyst used to detect issues
     * @param svgOut          Output stream contaiing the diagram as PNG
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    public void getSVG( Scenario scenario, Node selectedNode, ScenarioAnalyst analyst, OutputStream svgOut ) throws DiagramException {
        Graph<Node, Flow> graph = graphBuilder.buildScenarioGraph( scenario );
        graphRenderer.highlightVertex( selectedNode );
        render( graph, SVG, scenario, analyst, svgOut );
    }


    /**
     * Gets an image map component for a directed graph diagram of the scenario
     *
     * @param scenario     A scenario
     * @param analyst      The scenario analyst used to detect issues
     * @return an ImageMap
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    public String getImageMap( Scenario scenario, ScenarioAnalyst analyst ) throws DiagramException {
        Graph<Node, Flow> graph = graphBuilder.buildScenarioGraph( scenario );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        render( graph, IMAGE_MAP, scenario, analyst, new BufferedOutputStream( baos ) );
        return baos.toString();
    }

    private void render( Graph<Node, Flow> graph, String outputFormat,
                         Scenario scenario, ScenarioAnalyst analyst, OutputStream output ) throws DiagramException {
        graphRenderer.render( graph,
                new ScenarioMetaProvider( scenario, outputFormat, urlFormat, imageDirectory, analyst ),
                outputFormat,
                output
        );
    }

}
