package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.analysis.GraphBuilder;
import com.mindalliance.channels.analysis.graph.EntitiesNetworkGraphBuilder;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jgrapht.Graph;

import java.io.OutputStream;
import java.util.ArrayList;
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

    private Class entityClass;
    private Segment segment;
    private EntityRelationship selectedEntityRel;

    public EntitiesNetworkDiagram(
            Class entityClass,
            Segment segment,
            EntityRelationship selectedEntityRel,
            double[] diagramSize,
            String orientation ) {
        super( diagramSize, orientation );
        this.entityClass = entityClass;
        this.segment = segment;
        this.selectedEntityRel = selectedEntityRel;
    }

    public void render( String outputFormat,
                        OutputStream outputStream,
                        Analyst analyst,
                        DiagramFactory diagramFactory ) {
        QueryService queryService = diagramFactory.getQueryService();
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder<ModelEntity, EntityRelationship> entitiesNetworkGraphBuilder =
                new EntitiesNetworkGraphBuilder(
                        getEntities( queryService ),
                        getEntityRels( queryService, analyst ),
                        diagramFactory.getQueryService() );
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
                analyst );
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

    @SuppressWarnings( "unchecked" )
    private List<ModelEntity> getEntities( QueryService queryService ) {
        if ( segment != null ) {
            return queryService.listEntitiesTaskedInSegment( entityClass, segment, ModelEntity.Kind.Actual );
        } else {
            return (List<ModelEntity>) CollectionUtils.select(
                    queryService.listActualEntities( entityClass ),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return !( (ModelEntity) object ).isUnknown();
                        }
                    }
            );
        }
    }

    private List<EntityRelationship> getEntityRels( QueryService queryService, Analyst analyst ) {
        List<EntityRelationship> entityRels = new ArrayList<EntityRelationship>();
        List<ModelEntity> entities = getEntities( queryService );
        for ( ModelEntity entity : entities ) {
            for ( ModelEntity other : entities ) {
                if ( entity != other ) {
                    EntityRelationship<ModelEntity> entityRel;
                    if ( segment != null ) {
                        entityRel = analyst.findEntityRelationship( entity, other, segment );
                    } else {
                        entityRel = analyst.findEntityRelationship( entity, other );
                    }
                    if ( entityRel != null ) entityRels.add( entityRel );
                }
            }
        }
        return entityRels;
    }


}
