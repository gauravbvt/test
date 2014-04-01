/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.DirectedMultiGraphWithProperties;
import com.mindalliance.channels.engine.analysis.graph.FlowMapGraphBuilder;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Flow map diagram.
 */
public class FlowMapDiagram extends AbstractDiagram<Node, Flow> {

    /**
     * The segment being diagrammed.
     */
    private Segment segment;

    /**
     * A selected node.
     */
    private Node selectedNode;

    private Flow selectedFlow;
    /**
     * Whether to show goals.
     */
    private boolean showingGoals;
    /**
     * Whether to show assets.
     */
    private boolean showingAssets;

    /**
     * Whether to show connectors.
     */
    private boolean showingConnectors;

    /**
     * Whether to hide conceptual tasks and flows.
     */
    private boolean hidingNoop;
    /**
     * Whether to show a simplified flow map.
     */
    private boolean simplified;

    public FlowMapDiagram( Segment segment, Node selectedNode, Flow selectedFlow, double[] diagramSize, String orientation ) {
        this( segment, selectedNode, selectedFlow, diagramSize, orientation, false, false, false, false, false );
    }

    public FlowMapDiagram( Segment segment, Node selectedNode, Flow selectedFlow, double[] diagramSize, String orientation,
                           boolean showingGoals, boolean showingConnectors, boolean hidingNoop, boolean simplified,
                           boolean showingAssets) {
        super( diagramSize, orientation );
        this.segment = segment;
        this.selectedNode = selectedNode;
        this.selectedFlow = selectedFlow;
        this.showingGoals = showingGoals;
        this.showingAssets = showingAssets;
        this.showingConnectors = showingConnectors;
        this.hidingNoop = hidingNoop;
        this.simplified = simplified;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render( String ticket, String outputFormat, OutputStream outputStream, Analyst analyst,
                        DiagramFactory diagramFactory, CommunityService communityService ) throws DiagramException {
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder flowMapGraphBuilder =
                new FlowMapGraphBuilder( segment, communityService, showingConnectors );
        Graph<Node, Flow> graph = flowMapGraphBuilder.buildDirectedGraph();
        GraphRenderer<Node, Flow> graphRenderer = diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if ( selectedNode != null )
            graphRenderer.highlightVertex( selectedNode );
        if ( selectedFlow != null )
            graphRenderer.highlightEdge( selectedFlow );
        FlowMapMetaProvider metaProvider = new FlowMapMetaProvider( segment,
                outputFormat,
                diagramFactory.getImageDirectory(),
                analyst,
                showingGoals,
                showingConnectors,
                hidingNoop,
                simplified,
                showingAssets,
                communityService );
        metaProvider.setGraphProperties( ( (DirectedMultiGraphWithProperties) graph ).getProperties() );
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        FlowMapDOTExporter dotExporter = new FlowMapDOTExporter( metaProvider );
        graphRenderer.render( communityService, graph, dotExporter, outputFormat, ticket, outputStream );
    }
}
