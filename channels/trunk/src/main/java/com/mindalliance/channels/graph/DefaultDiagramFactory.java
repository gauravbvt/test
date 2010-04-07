package com.mindalliance.channels.graph;

import com.mindalliance.channels.AbstractService;
import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.graph.diagrams.EntityNetworkDiagram;
import com.mindalliance.channels.graph.diagrams.FailureImpactsDiagram;
import com.mindalliance.channels.graph.diagrams.FlowMapDiagram;
import com.mindalliance.channels.graph.diagrams.HierarchyDiagram;
import com.mindalliance.channels.graph.diagrams.PlanMapDiagram;
import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * The default implementation of DiagramFactory
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 3:15:10 PM
 * @param <V> a vertex class
 * @param <E> an edge class
 */
public class DefaultDiagramFactory<V, E> extends AbstractService implements DiagramFactory {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultDiagramFactory.class );
    /**
     * A GraphRenderer for nodes and flows
     */
    private GraphRenderer<V, E> graphRenderer;
    /**
     * Path to image directory
     */
    private Resource imageDirectory;
    /**
     * Query service.
     */
    private QueryService queryService;

    public DefaultDiagramFactory() {
    }

    public void setGraphRenderer( GraphRenderer<V, E> graphRenderer ) {
        this.graphRenderer = graphRenderer;
    }

    /**
     * {@inheritDoc}
     */
    public GraphRenderer<V, E> getGraphRenderer() {
        return graphRenderer;
    }

    public Resource getImageDirectory() {
        return imageDirectory;
    }

    public void setImageDirectory( Resource imageDirectory ) {
        this.imageDirectory = imageDirectory;
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    /**
     * {@inheritDoc}
     */
    public Diagram newFlowMapDiagram(
            Segment segment,
            Node node,
            double[] diagramSize,
            String orientation ) {
        LOG.debug("Making flow map on " + segment + "/" + node);
        return new FlowMapDiagram( segment, node, diagramSize, orientation );
    }


    public Diagram newEntityNetworkDiagram(
            ModelEntity entity,
            EntityRelationship selectedEntityRel,
            double[] diagramSize,
            String orientation ) {
        LOG.debug("Making entity network diagram on " + entity);
        return new EntityNetworkDiagram( entity, selectedEntityRel, diagramSize, orientation );
    }

    /**
      * {@inheritDoc}
      */
    @SuppressWarnings( "unchecked" )
    // TODO - why can't I say:  List<Segment> segments ?   -- ? extends...
    public Diagram newPlanMapDiagram(
            List segments,
            boolean groupByPhase,
            boolean groupByEvent,
            ModelEntity group,
            Segment segment,
            SegmentRelationship scRel,
            double[] diagramSize,
            String orientation ) {
        LOG.debug( "Making plan diagram" );
        return new PlanMapDiagram(
                segments,
                groupByPhase,
                groupByEvent,
                group,
                segment,
                scRel,
                diagramSize,
                orientation );
    }

    public Diagram newHierarchyDiagram(
            Hierarchical hierarchical,
            double[] diagramSize,
            String orientation ) {
        LOG.debug("Making hierarchy diagram on " + hierarchical.getName() );
        return new HierarchyDiagram( hierarchical, diagramSize, orientation );
    }

    public Diagram newEssentialFlowMapDiagram(
            SegmentObject segmentObject,
            boolean assumeFails,
            double[] diagramSize,
            String orientation ) {
        LOG.debug("Making critical flow map diagram" );
        return new FailureImpactsDiagram( segmentObject, assumeFails, diagramSize, orientation );
    }


}
