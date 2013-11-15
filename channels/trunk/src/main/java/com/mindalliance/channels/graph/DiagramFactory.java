/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * A diagram maker.
 *
 * @param <Vertex> a vertex class
 * @param <Edge>   an edge class
 */
public interface DiagramFactory<Vertex, Edge> {

    /**
     * The PNG format.
     */
    String PNG = "png";
    /**
     * The SVG format.
     */
    String SVG = "svg";
    /**
     * The image map format.
     */
    String IMAGE_MAP = "cmapx";
    /**
     * Left to right graph orientation.
     */
    String LEFT_RIGHT = "LR";
    /**
     * Top-bottom graph orientation.
     */
    String TOP_BOTTOM = "TB";

    /**
     * Set image directory.
     *
     * @param imageDirectory -- where to find images
     */
    void setImageDirectory( Resource imageDirectory );

    /**
     * Instantiates a flow map diagram.
     *
     * @param segment     a segment
     * @param node        a selected node
     * @param flow        a selected flow
     * @param diagramSize width and height as array of doubles
     * @param orientation a string
     * @return a flow map diagram
     */
    Diagram newFlowMapDiagram( Segment segment, Node node, Flow flow, double[] diagramSize, String orientation );

    /**
     * Instantiates a flow map diagram.
     *
     * @param segment           a segment
     * @param node              a selected node
     * @param flow              a selected flow
     * @param diagramSize       width and height as array of doubles
     * @param orientation       a string
     * @param showingGoals      whether to show goals
     * @param showingConnectors whether to show connectors
     * @param hidingNoop        whether to hide non-operation tasks and flows
     * @param simplified        whether to show s simplified flow map
     * @return a flow map diagram
     */
    Diagram newFlowMapDiagram( Segment segment, Node node, Flow flow, double[] diagramSize, String orientation,
                               boolean showingGoals, boolean showingConnectors, boolean hidingNoop, boolean simplified );

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
    Diagram newPlanMapDiagram( List<Segment> segments, boolean groupByPhase, boolean groupByEvent, ModelEntity group,
                               Segment segment, SegmentRelationship sgRel, double[] diagramSize, String orientation );

    /**
     * Instantiates an entity network diagram.
     *
     * @param entity            the entity at the center of the diagram
     * @param selectedEntityRel an edge selected
     * @param diagramSize       width and height as array of doubles
     * @param orientation       a string
     * @return an entity network diagram
     */
    <T extends ModelEntity> Diagram newEntityNetworkDiagram( T entity, EntityRelationship<T> selectedEntityRel,
                                                             double[] diagramSize, String orientation );

    /**
     * Instantiates a hierarchy diagram.
     *
     * @param hierarchical a hierarchical object
     * @param diagramSize  width and height as array of doubles
     * @param orientation  a string
     * @param algo         a string - the layout algorithm to use
     * @return a hierarchy diagram
     */
    Diagram newHierarchyDiagram( Hierarchical hierarchical, double[] diagramSize, String orientation, String algo );

    /**
     * Instantiates an essential flow map diagram.
     *
     * @param segmentObject a segment object
     * @param assumeFails   whether alternate flows are assumed to fail
     * @param diagramSize   width and height as array of doubles
     * @param orientation   a string
     * @return a critical flow map diagram
     */
    Diagram newEssentialFlowMapDiagram( SegmentObject segmentObject, boolean assumeFails, double[] diagramSize,
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
    <T extends ModelEntity> Diagram newEntitiesNetworkDiagram( Class entityClass, Segment segment,
                                                               EntityRelationship<T> selectedEntityRel,
                                                               double[] diagramSize, String orientation );

    /**
     * Gets the preset graph renderer.
     *
     * @return a GraphRenderer
     */
    GraphRenderer<Vertex, Edge> getGraphRenderer();

    /**
     * Get preset image directory path.
     *
     * @return a resource
     */
    Resource getImageDirectory();

    /**
     * Get user icon directory.
     * @return a resource
     */
    Resource getUserIconDirectory();

    /**
     * Instantiates a dissemination diagram.
     *
     * @param segmentObject a segment object (part or flow)
     * @param subject       a subject being disseminated
     * @param showTargets   a boolean
     * @param diagramSize   width and height as array of doubles
     * @param orientation   a string
     * @return a dissemination diagram
     */
    Diagram newDisseminationDiagram( SegmentObject segmentObject, Subject subject, boolean showTargets,
                                     double[] diagramSize, String orientation );

    /**
     * Instantiates a checklists map diagram.
     *
     * @param segment            segment as scope, or null for all plan
     * @param summarizeByOrgType a boolean
     * @param summarizeByOrg     a boolean
     * @param summarizeByRole    a boolean
     * @param focusEntity        an organization or agent
     * @param diagramSize        an array of doubles
     * @param orientation        a string
     * @return a checklists map diagram
     */
    Diagram newChecklistsMapDiagram( Segment segment, boolean summarizeByOrgType, boolean summarizeByOrg,
                                     boolean summarizeByRole, ModelEntity focusEntity, double[] diagramSize,
                                     String orientation );

    /**
     * Instantiates a requirement networking  diagram.
     *
     * @param timing                 a phase timing or null for all
     * @param event                  an event or null for any event
     * @param selectedAgency         an organization
     * @param selectedRequirementRel a requirement relationship
     * @param diagramSize            an array of doubles
     * @param orientation            a string
     * @return a procedure map diagram
     */
    Diagram newRequiredNetworkingDiagram(
            Phase.Timing timing,
            Event event,
            Agency selectedAgency,
            RequirementRelationship selectedRequirementRel,
            double[] diagramSize,
            String orientation );

    /**
     * Instantiates a checklist flow diagram.
     *
     * @param part        a part
     * @param diagramSize an array of doubles
     * @param orientation a string
     * @param interactive whether to make step icon interactive
     * @return a checklist flow diagram
     */
    Diagram newChecklistFlowDiagram( Part part, double[] diagramSize, String orientation, boolean interactive );

    /**
     * Instantiates a command chains diagram.
     *
     * @param user        a channels user
     * @param diagramSize an array of doubles
     * @param orientation a string
     * @param algo        the rendering algorithm
     * @return a command chains diagram
     */
    Diagram newUserCommandChainsDiagram( ChannelsUser user, double[] diagramSize, String orientation, String algo );
}
