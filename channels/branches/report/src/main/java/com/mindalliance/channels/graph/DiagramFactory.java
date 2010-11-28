package com.mindalliance.channels.graph;

import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.model.Subject;
import com.mindalliance.channels.query.QueryService;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * A diagram maker.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 3:12:42 PM
 *
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
    void setImageDirectory( Resource imageDirectory );


    /**
     * Instantiates a flow map diagram.
     *
     * @param segment     a segment
     * @param node        a selected node
     * @param diagramSize width and height as array of doubles
     * @param orientation a string
     * @return a flow map diagram
     */
    Diagram newFlowMapDiagram(
            Segment segment,
            Node node,
            double[] diagramSize,
            String orientation );

    /**
     * Instantiates a flow map diagram.
     *
     * @param segment           a segment
     * @param node              a selected node
     * @param diagramSize       width and height as array of doubles
     * @param orientation       a string
     * @param showingGoals      whether to show goals
     * @param showingConnectors whether to show connectors
     * @return a flow map diagram
     */
    Diagram newFlowMapDiagram(
            Segment segment,
            Node node,
            double[] diagramSize,
            String orientation,
            boolean showingGoals,
            boolean showingConnectors );

    /**
     * Instantiates a plan map diagram.
     *
     * @param groupByPhase whetner to group segment by phases
     * @param groupByEvent whether to group segments by events addressed
     * @param group        phase or event grouping segments
     * @param segments     list of segments
     * @param sgRel        selected segment relationship
     * @param segment      selected segment
     * @param diagramSize  width and height as array of doubles
     * @param orientation  a string
     * @return a plan map diagram
     */
    Diagram newPlanMapDiagram(
            List<Segment> segments,
            boolean groupByPhase,
            boolean groupByEvent,
            ModelEntity group,
            Segment segment,
            SegmentRelationship sgRel,
            double[] diagramSize,
            String orientation );

    /**
     * Instantiates an entity network diagram.
     *
     * @param entity            the entity at the center of the diagram
     * @param selectedEntityRel an edge selected
     * @param diagramSize       width and height as array of doubles
     * @param orientation       a string
     * @return an entity network diagram
     */
    <T extends ModelEntity> Diagram newEntityNetworkDiagram(
            T entity,
            EntityRelationship<T> selectedEntityRel,
            double[] diagramSize,
            String orientation );

    /**
     * Instantiates a hierarchy diagram.
     *
     * @param hierarchical a hierarchical object
     * @param diagramSize  width and height as array of doubles
     * @param orientation  a string
     * @return a hierarchy diagram
     */
    Diagram newHierarchyDiagram( Hierarchical hierarchical, double[] diagramSize, String orientation );

    /**
     * Instantiates an essential flow map diagram.
     *
     * @param segmentObject a segment object
     * @param assumeFails   whether alternate flows are assumed to fail
     * @param diagramSize   width and height as array of doubles
     * @param orientation   a string
     * @return a critical flow map diagram
     */
    Diagram newEssentialFlowMapDiagram(
            SegmentObject segmentObject,
            boolean assumeFails,
            double[] diagramSize,
            String orientation );

    /**
     * Instantiates an entities network diagram.
     *
     * @param entityClass       entity class
     * @param segment           a segment or null
     * @param selectedEntityRel selected releationship
     * @param diagramSize       width and height as array of doubles
     * @param orientation       a string
     * @return an entities network diagram
     */
    <T extends ModelEntity> Diagram newEntitiesNetworkDiagram(
            Class entityClass,
            Segment segment,
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
    Resource getImageDirectory();

    /**
     * Get query service.
     *
     * @return a query service
     */
    QueryService getQueryService();

    /**
     * Instnatiates a dissemination diagram.
     *
     * @param segmentObject a segment object (part or flow)
     * @param subject       a subject being disseminated
     * @param showTargets   a boolean
     * @param diagramSize   width and height as array of doubles
     * @param orientation   a string
     * @return a dissemination diagram
     */
    Diagram newDisseminationDiagram(
            SegmentObject segmentObject,
            Subject subject,
            boolean showTargets,
            double[] diagramSize,
            String orientation );
}
