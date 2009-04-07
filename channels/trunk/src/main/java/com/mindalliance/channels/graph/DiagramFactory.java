package com.mindalliance.channels.graph;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.analysis.network.ScenarioRelationship;
import com.mindalliance.channels.analysis.network.EntityRelationship;

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
public interface DiagramFactory<Vertex, Edge> {

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
     * @return a flow map diagram
     */
    Diagram newFlowMapDiagram( Scenario scenario, Node node );

    /**
     * Instantiate a plan map diagram.
     * @param scenarios list of scenarios
     * @param scRel selected scenario relationship
     * @param scenario selected scenario
     * @return a plan map diagram
     */
    Diagram newPlanMapDiagram( List<Scenario> scenarios, Scenario scenario, ScenarioRelationship scRel );

    /**
     * Instantiate an entity network diagram.
     * @param entity the entity at the center of the diagram
     * @param selectedEntity another entity selected
     * @param selectedEntityRel an edge selected
     * @return an entity network diagram
     */
    <T extends ModelObject>Diagram newEntityNetworkDiagram(
            T entity,
            T selectedEntity,
            EntityRelationship<T> selectedEntityRel );

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
     * Get data query object.
     * @return a data query object
     */
    DataQueryObject getDqo();

}
