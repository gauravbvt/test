package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.analysis.graph.EntityNetworkGraphBuilder;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.model.ModelEntity;
import org.jgrapht.Graph;

import java.io.OutputStream;
import java.util.List;

/**
 * Entity network diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 8:15:12 PM
 */
public class EntityNetworkDiagram extends AbstractDiagram<ModelEntity, EntityRelationship> {

    private ModelEntity entity;
    private EntityRelationship selectedEntityRel;

    public EntityNetworkDiagram(
            ModelEntity entity,
            EntityRelationship selectedEntityRel,
            double[] diagramSize,
            String orientation ) {
        super( diagramSize, orientation );
        this.entity = entity;
        this.selectedEntityRel = selectedEntityRel;
    }

    public void render( String outputFormat, OutputStream outputStream ) {
        List<? extends ModelEntity> entities = getEntities();
        DiagramFactory<ModelEntity, EntityRelationship> diagramFactory = getDiagramFactory();
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder<ModelEntity, EntityRelationship> entityNetworkGraphBuilder =
                new EntityNetworkGraphBuilder( entity, entities, getDiagramFactory().getQueryService() );
        Graph<ModelEntity, EntityRelationship> graph =
                entityNetworkGraphBuilder.buildDirectedGraph();
        GraphRenderer<ModelEntity, EntityRelationship> graphRenderer =
                diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        graphRenderer.highlightVertex( entity );
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

    private List<? extends ModelEntity> getEntities() {
        return getDiagramFactory().getQueryService().listEntitiesWithUnknown( entity.getClass() );
    }

}
