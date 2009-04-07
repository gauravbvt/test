package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.analysis.network.EntityNetworkGraphBuilder;
import com.mindalliance.channels.analysis.network.EntityRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.pages.Project;
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
public class EntityNetworkDiagram extends AbstractDiagram<ModelObject, EntityRelationship> {

    private ModelObject entity;
    private ModelObject selectedEntity;
    private EntityRelationship selectedEntityRel;

    public EntityNetworkDiagram( ModelObject entity, ModelObject selectedEntity, EntityRelationship selectedEntityRel ) {
        super();
        this.entity = entity;
        this.selectedEntity = selectedEntity;
        this.selectedEntityRel= selectedEntityRel;
    }

    public void render( String outputFormat, OutputStream outputStream ) {
        List<? extends ModelObject> entities = getEntities();
        DiagramFactory<ModelObject,EntityRelationship> diagramFactory = getDiagramFactory();
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder<ModelObject,EntityRelationship> entityNetworkGraphBuilder =
                new EntityNetworkGraphBuilder( entity, entities, getDiagramFactory().getDqo() );
        Graph<ModelObject,EntityRelationship> graph =
                entityNetworkGraphBuilder.buildDirectedGraph();
        GraphRenderer<ModelObject,EntityRelationship> graphRenderer =
                diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if ( selectedEntity != null ) {
            graphRenderer.highlightVertex( selectedEntity );
        }
        if ( selectedEntityRel != null ) {
            graphRenderer.highlightEdge( selectedEntityRel );
        }
        EntityNetworkMetaProvider metaProvider = new EntityNetworkMetaProvider(
                outputFormat,
                diagramFactory.getImageDirectory(),
                Project.analyst() );
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
        );    }

    private List<? extends ModelObject> getEntities() {
        return getDiagramFactory().getDqo().list((Class<? extends ModelObject>)entity.getClass());
    }

}
