/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.HierarchyGraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.HierarchyRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Hierarchy diagram.
 */
public class HierarchyDiagram extends AbstractDiagram<Hierarchical, HierarchyRelationship> {

    private Hierarchical hierarchical;

    private final String algo;

    public HierarchyDiagram( Hierarchical hierarchical, double[] diagramSize, String orientation ) {
        this( hierarchical, diagramSize, orientation, "dot" );
    }

    public HierarchyDiagram( Hierarchical hierarchical, double[] diagramSize, String orientation, String algo ) throws DiagramException {
        super( diagramSize, orientation );
        this.hierarchical = hierarchical;
        this.algo = algo;
    }

    @Override
    public void render( String ticket, String outputFormat, OutputStream outputStream, Analyst analyst,
                        DiagramFactory diagramFactory, CommunityService communityService ) {
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder<Hierarchical, HierarchyRelationship> hierarchyGraphBuilder =
                new HierarchyGraphBuilder( hierarchical, communityService );
        Graph<Hierarchical, HierarchyRelationship> graph = hierarchyGraphBuilder.buildDirectedGraph();
        GraphRenderer<Hierarchical, HierarchyRelationship> graphRenderer =
                diagramFactory.getGraphRenderer().cloneSelf();
        graphRenderer.setAlgo( algo );
        graphRenderer.resetHighlight();
        graphRenderer.highlightVertex( hierarchical );
        HierarchyMetaProvider metaProvider =
                new HierarchyMetaProvider( outputFormat, diagramFactory.getImageDirectory(), analyst, communityService );
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        HierarchyDOTExporter dotExporter = new HierarchyDOTExporter( metaProvider );
        graphRenderer.render( communityService, graph, dotExporter, outputFormat, ticket, outputStream );
    }
}
