/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
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
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.graph.diagrams.ChecklistFlowDiagram;
import com.mindalliance.channels.graph.diagrams.ChecklistsMapDiagram;
import com.mindalliance.channels.graph.diagrams.CommandChainsDiagram;
import com.mindalliance.channels.graph.diagrams.DisseminationDiagram;
import com.mindalliance.channels.graph.diagrams.EntitiesNetworkDiagram;
import com.mindalliance.channels.graph.diagrams.EntityNetworkDiagram;
import com.mindalliance.channels.graph.diagrams.FailureImpactsDiagram;
import com.mindalliance.channels.graph.diagrams.FlowMapDiagram;
import com.mindalliance.channels.graph.diagrams.HierarchyDiagram;
import com.mindalliance.channels.graph.diagrams.ModelMapDiagram;
import com.mindalliance.channels.graph.diagrams.RequiredNetworkingDiagram;
import com.mindalliance.channels.graph.diagrams.SupplyChainsDiagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * The default implementation of DiagramFactory.
 *
 * @param <V> a vertex class
 * @param <E> an edge class
 */
public class DefaultDiagramFactory<V, E> implements DiagramFactory {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultDiagramFactory.class );

    /**
     * A GraphRenderer for nodes and flows.
     */
    private GraphRenderer<V, E> graphRenderer;

    /**
     * Path to image directory.
     */
    private Resource imageDirectory;

    /**
     * Directory for generated user photo icons.
     */
    private Resource userIconDirectory;


    private Analyst analyst;

    public DefaultDiagramFactory() {
    }

    public void setGraphRenderer( GraphRenderer<V, E> graphRenderer ) {
        this.graphRenderer = graphRenderer;
    }

    @Override
    public GraphRenderer<V, E> getGraphRenderer() {
        return graphRenderer;
    }

    @Override
    public Resource getImageDirectory() {
        return imageDirectory;
    }

    @Override
    public void setImageDirectory( Resource imageDirectory ) {
        this.imageDirectory = imageDirectory;
    }

    @Override
    public Resource getUserIconDirectory() {
        return userIconDirectory;
    }

    public void setUserIconDirectory( Resource userIconDirectory ) {
        this.userIconDirectory = userIconDirectory;
    }

    public Analyst getAnalyst() {
        return analyst;
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }

    @Override
    public Diagram newFlowMapDiagram( Segment segment, Node node, Flow flow, double[] diagramSize, String orientation ) {
        return newFlowMapDiagram( segment, node, flow, diagramSize, orientation, false, false, false, false, false );
    }

    @Override
    public Diagram newFlowMapDiagram( Segment segment, Node node, Flow flow, double[] diagramSize, String orientation,
                                      boolean showingGoals, boolean showingConnectors, boolean hidingNoop,
                                      boolean simplified, boolean showingAssets ) {
        LOG.debug( "Making flow map on " + segment + "/" + node );
        return new FlowMapDiagram( segment,
                                   node,
                                   flow,
                                   diagramSize,
                                   orientation,
                                   showingGoals,
                                   showingConnectors,
                                   hidingNoop,
                                   simplified,
                                   showingAssets );
    }

    @Override
    public Diagram newEntityNetworkDiagram( ModelEntity entity, EntityRelationship selectedEntityRel,
                                            double[] diagramSize, String orientation ) {
        LOG.debug( "Making entity network diagram on " + entity );
        return new EntityNetworkDiagram( entity, selectedEntityRel, diagramSize, orientation );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    // TODO - why can't I say:  List<Segment> segments ?   -- ? extends...
    public Diagram newModelMapDiagram( List segments, boolean groupByPhase, boolean groupByEvent, ModelEntity group,
                                       Segment segment, SegmentRelationship scRel, double[] diagramSize,
                                       String orientation ) {
        LOG.debug( "Making model map diagram" );
        return new ModelMapDiagram( segments,
                                   groupByPhase,
                                   groupByEvent,
                                   group,
                                   segment,
                                   scRel,
                                   diagramSize,
                                   orientation );
    }

    @Override
    public Diagram newHierarchyDiagram( Hierarchical hierarchical, double[] diagramSize, String orientation,
                                        String algo ) {
        LOG.debug( "Making hierarchy diagram on " + hierarchical.getName() );
        return new HierarchyDiagram( hierarchical, diagramSize, orientation, algo );
    }

    @Override
    public Diagram newEssentialFlowMapDiagram( SegmentObject segmentObject, boolean assumeFails, double[] diagramSize,
                                               String orientation ) {
        LOG.debug( "Making critical flow map diagram" );
        return new FailureImpactsDiagram( segmentObject, assumeFails, diagramSize, orientation );
    }

    @Override
    public Diagram newEntitiesNetworkDiagram( Class entityClass, Segment segment, EntityRelationship selectedEntityRel,
                                              double[] diagramSize, String orientation ) {
        LOG.debug( "Making entities network diagram" );
        return new EntitiesNetworkDiagram( entityClass, segment, selectedEntityRel, diagramSize, orientation );
    }

    @Override
    public Diagram newDisseminationDiagram( SegmentObject segmentObject, Subject subject, boolean showTargets,
                                            double[] diagramSize, String orientation ) {
        LOG.debug( "Making dissemination diagram" );
        return new DisseminationDiagram( segmentObject, subject, showTargets, diagramSize, orientation );
    }

    @Override
    public Diagram newChecklistsMapDiagram( Segment segment, boolean summarizeByOrgType,
                                            boolean summarizeByOrg, boolean summarizeByRole,
                                            ModelEntity focusEntity, double[] diagramSize,
                                            String orientation ) {
        LOG.debug( "Making checklists map diagram" );
        return new ChecklistsMapDiagram( segment,
                                        summarizeByOrgType,
                                        summarizeByOrg,
                                        summarizeByRole,
                                        focusEntity,
                                        diagramSize,
                                        orientation );
    }

    @Override
    public Diagram newRequiredNetworkingDiagram(
            Phase.Timing timing,
            Event event,
            Agency selectedAgency,
            RequirementRelationship selectedRequirementRel,
            double[] diagramSize,
            String orientation ) {
        LOG.debug( "Making required networking diagram" );
        return new RequiredNetworkingDiagram(
                timing,
                event,
                selectedAgency,
                selectedRequirementRel,
                diagramSize,
                orientation
        );
    }

    @Override
    public Diagram newChecklistFlowDiagram( Part part, double[] diagramSize, String orientation, boolean interactive ) {
        LOG.debug( "Making checklist flow diagram" );
        return new ChecklistFlowDiagram( part, diagramSize, orientation, interactive );
    }

    @Override
    public Diagram newCommandChainsDiagram( ChannelsUser user, double[] diagramSize, String orientation, String algo ) {
        LOG.debug( "Making command chains diagram" );
        return new CommandChainsDiagram( user, diagramSize, orientation, algo );
    }

    @Override
    public Diagram newCommandChainsDiagram( Agent agent, double[] diagramSize, String orientation, String algo ) {
        LOG.debug( "Making command chains diagram" );
        return new CommandChainsDiagram( agent, diagramSize, orientation, algo );
    }

    @Override
    public Diagram newSupplyChainsDiagram( MaterialAsset materialAsset,
                                           boolean summarizeByOrgType,
                                           boolean summarizeByOrg,
                                           boolean summarizeByRole,
                                           double[] diagramSize,
                                           String orientation ) {
        LOG.debug( "Making supply chains diagram");
        return new SupplyChainsDiagram(
                materialAsset,
                summarizeByOrgType,
                summarizeByOrg,
                summarizeByRole,
                diagramSize, orientation );
    }
}
