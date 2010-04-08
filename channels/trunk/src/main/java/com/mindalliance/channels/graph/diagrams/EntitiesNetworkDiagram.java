package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.analysis.graph.EntitiesNetworkGraphBuilder;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.model.ModelEntity;
import org.jgrapht.Graph;

import java.io.OutputStream;
import java.util.List;

/**
 * Entities network diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2010
 * Time: 8:53:44 PM
 */
public class EntitiesNetworkDiagram extends AbstractDiagram<ModelEntity, EntityRelationship> {

    private List<ModelEntity> entities;
    private EntityRelationship selectedEntityRel;

    public EntitiesNetworkDiagram(
            List<ModelEntity> entities,
            EntityRelationship selectedEntityRel,
            double[] diagramSize,
            String orientation ) {
        super( diagramSize, orientation );
        this.entities = entities;
        this.selectedEntityRel = selectedEntityRel;
    }

    public void render( String outputFormat, OutputStream outputStream ) {
        DiagramFactory<ModelEntity, EntityRelationship> diagramFactory = getDiagramFactory();
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder<ModelEntity, EntityRelationship> entitiesNetworkGraphBuilder =
                new EntitiesNetworkGraphBuilder( entities, getDiagramFactory().getQueryService() );
        Graph<ModelEntity, EntityRelationship> graph =
                entitiesNetworkGraphBuilder.buildDirectedGraph();
        GraphRenderer<ModelEntity, EntityRelationship> graphRenderer =
                diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if ( selectedEntityRel != null ) {
            graphRenderer.highlightEdge( selectedEntityRel );
        }
        EntityNetworkMetaProvider metaProvider = new EntityNetworkMetaProvider(
                outputFormat,
                diagramFactory.getImageDirectory(),
                getAnalyst() );
        if ( diagramSize != null ) {
            metaProvider.setGraphSize( diagramSize );
        }
        if ( orientation != null ) {
            metaProvider.setGraphOrientation( orientation );
        }
        EntityNetworkDOTExporter dotExporter = new EntityNetworkDOTExporter( metaProvider );
        graphRenderer.render( graph,
                dotExporter,
                outputFormat,
                outputStream
        );
    }
}
