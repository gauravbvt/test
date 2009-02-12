package com.mindalliance.channels.graph;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;

/**
 * A diagram maker
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 3:12:42 PM
 * @param <Vertex> a vertex class
 * @param <Edge> an edge class
 */
public interface DiagramMaker<Vertex, Edge> {

    /**
     * The PNG format
     */
    String PNG = "png";
    /**
     * The SVG format
     */
    String SVG = "svg";
    /**
     * The image map format
     */
    String IMAGE_MAP = "cmapx";
    /**
     * Left to right graph orientation
     */
    String LEFT_RIGHT = "LR";
    /**
     * Top-bottom graph orientation
     */
    String TOP_BOTTOM = "TB";

    /**
     * Set image directory
     *
     * @param imageDirectory -- where to find images
     */
    void setImageDirectory( String imageDirectory );


    /**
     * Instantiate a flow diagram.
     *
     * @param scenario a scenario
     * @param node     a selected node
     * @return a flow diagram
     */
    FlowDiagram newFlowDiagram( Scenario scenario, Node node );

    /**
     * Instantiate a flow diagram.
     *
     * @param scenario a scenario
     * @return a flow diagram
     */
    FlowDiagram newFlowDiagram( Scenario scenario );

    /**
     * Gets the preset graph renderer.
     *
     * @return a GraphRenderer
     */
    GraphRenderer<Vertex, Edge> getGraphRenderer();

    /**
     * Get preset URL format.
     *
     * @return a String
     */
    String getUrlFormat();

    /**
     * Get preset scenario URL format
     *
     * @return a String
     */
    String getScenarioUrlFormat();

    /**
     * Get preset image directory path
     *
     * @return a String
     */
    String getImageDirectory();
}
