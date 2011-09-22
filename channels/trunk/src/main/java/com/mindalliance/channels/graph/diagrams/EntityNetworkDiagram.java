/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.EntityNetworkGraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.core.model.ModelEntity;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Entity network diagram.
 */
public class EntityNetworkDiagram extends AbstractDiagram<ModelEntity, EntityRelationship> {

    private ModelEntity entity;

    private EntityRelationship selectedEntityRel;

    public EntityNetworkDiagram( ModelEntity entity, EntityRelationship selectedEntityRel, double[] diagramSize,
                                 String orientation ) {
        super( diagramSize, orientation );
        this.entity = entity;
        this.selectedEntityRel = selectedEntityRel;
    }

    public void render( String ticket, String outputFormat, OutputStream outputStream, Analyst analyst,
                        DiagramFactory diagramFactory, QueryService queryService ) {

        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder<ModelEntity, EntityRelationship> entityNetworkGraphBuilder =
                new EntityNetworkGraphBuilder( entity, analyst, queryService );
        Graph<ModelEntity, EntityRelationship> graph = entityNetworkGraphBuilder.buildDirectedGraph();
        GraphRenderer<ModelEntity, EntityRelationship> graphRenderer = diagramFactory.getGraphRenderer().cloneSelf();
        graphRenderer.setAlgo( "neato" );
        graphRenderer.resetHighlight();
        graphRenderer.highlightVertex( entity );
        if ( selectedEntityRel != null )
            graphRenderer.highlightEdge( selectedEntityRel );
        EntityNetworkMetaProvider metaProvider =
                new EntityNetworkMetaProvider( outputFormat, diagramFactory.getImageDirectory(), analyst,
                                               queryService );
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        EntityNetworkDOTExporter dotExporter = new EntityNetworkDOTExporter( metaProvider );
        graphRenderer.render( queryService, graph, dotExporter, outputFormat, ticket, outputStream );
    }
}
