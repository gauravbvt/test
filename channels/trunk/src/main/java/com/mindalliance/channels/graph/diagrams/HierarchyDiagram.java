package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.HierarchyGraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.HierarchyRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.core.model.Hierarchical;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Hierarchy diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 26, 2009
 * Time: 8:32:24 AM
 */
public class HierarchyDiagram extends AbstractDiagram<Hierarchical, HierarchyRelationship> {

    private Hierarchical hierarchical;
    private final String algo;

    public HierarchyDiagram( Hierarchical hierarchical, double[] diagramSize, String orientation ) {
        this( hierarchical, diagramSize, orientation, "dot" );
    }

    public HierarchyDiagram(
            Hierarchical hierarchical,
            double[] diagramSize,
            String orientation,
            String algo ) {
        super( diagramSize, orientation );
        this.hierarchical = hierarchical;
        this.algo = algo;
    }

    public void render(
            String ticket, String outputFormat,
            OutputStream outputStream,
            Analyst analyst,
            DiagramFactory diagramFactory ) {
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder<Hierarchical, HierarchyRelationship> hierarchyGraphBuilder =
                new HierarchyGraphBuilder( hierarchical, diagramFactory.getQueryService() );
        Graph<Hierarchical, HierarchyRelationship> graph =
                hierarchyGraphBuilder.buildDirectedGraph();
        GraphRenderer<Hierarchical, HierarchyRelationship> graphRenderer =
                diagramFactory.getGraphRenderer().cloneSelf();
        graphRenderer.setAlgo( algo );
        graphRenderer.resetHighlight();
        graphRenderer.highlightVertex( hierarchical );
        HierarchyMetaProvider metaProvider = new HierarchyMetaProvider(
                outputFormat,
                diagramFactory.getImageDirectory(),
                analyst );
        if ( diagramSize != null ) {
            metaProvider.setGraphSize( diagramSize );
        }
        if ( orientation != null ) {
            metaProvider.setGraphOrientation( orientation );
        }
        HierarchyDOTExporter dotExporter = new HierarchyDOTExporter( metaProvider );
        graphRenderer.render( graph,
                dotExporter,
                outputFormat,
                ticket,
                outputStream
        );
    }
}
