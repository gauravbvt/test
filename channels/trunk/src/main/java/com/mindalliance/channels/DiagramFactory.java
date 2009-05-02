package com.mindalliance.channels;

import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;

import java.util.List;

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
public interface DiagramFactory<Vertex, Edge> extends Service {

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
     * Instantiate a flow map diagram.
     *
     * @param scenario a scenario
     * @param node     a selected node
     * @param diagramSize width and height as array of doubles
     * @param orientation a string
     * @return a flow map diagram
     */
    Diagram newFlowMapDiagram(
            Scenario scenario,
            Node node,
            double[] diagramSize,
            String orientation );

    /**
     * Instantiate a plan map diagram.
     * @param scenarios list of scenarios
     * @param scRel selected scenario relationship
     * @param scenario selected scenario
     * @param diagramSize width and height as array of doubles
     * @param orientation a string
     * @return a plan map diagram
     */
    Diagram newPlanMapDiagram(
            List<Scenario> scenarios,
            Scenario scenario,
            ScenarioRelationship scRel,
            double[] diagramSize,
            String orientation );

    /**
     * Instantiate an entity network diagram.
     * @param entity the entity at the center of the diagram
     * @param selectedEntityRel an edge selected
     * @param diagramSize width and height as array of doubles
     * @param orientation a string
     * @return an entity network diagram
     */
    <T extends ModelObject>Diagram newEntityNetworkDiagram(
            T entity,
            EntityRelationship<T> selectedEntityRel,
            double[] diagramSize,
            String orientation );

    /**
     * Gets the preset graph renderer.
     *
     * @return a GraphRenderer
     */
    GraphRenderer<Vertex, Edge> getGraphRenderer();

    /**
     * Get preset image directory path
     *
     * @return a String
     */
    String getImageDirectory();

    /**
     * Get query service.
     * @return a query service
     */
    QueryService getQueryService();

}
