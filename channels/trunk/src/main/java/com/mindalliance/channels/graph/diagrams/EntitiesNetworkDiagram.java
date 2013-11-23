/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.EntitiesNetworkGraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jgrapht.Graph;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Entities network diagram.
 */
public class EntitiesNetworkDiagram extends AbstractDiagram<ModelEntity, EntityRelationship> {

    private Class entityClass;

    private Segment segment;

    private EntityRelationship selectedEntityRel;

    public EntitiesNetworkDiagram( Class entityClass, Segment segment, EntityRelationship selectedEntityRel,
                                   double[] diagramSize, String orientation ) {
        super( diagramSize, orientation );
        this.entityClass = entityClass;
        this.segment = segment;
        this.selectedEntityRel = selectedEntityRel;
    }

    @Override
    public void render( String ticket, String outputFormat, OutputStream outputStream, Analyst analyst,
                        DiagramFactory diagramFactory, CommunityService communityService ) throws DiagramException {
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder<ModelEntity, EntityRelationship> entitiesNetworkGraphBuilder = new EntitiesNetworkGraphBuilder(
                getEntities( communityService ),
                getEntityRels( communityService, analyst ),
                communityService );
        Graph<ModelEntity, EntityRelationship> graph = entitiesNetworkGraphBuilder.buildDirectedGraph();
        GraphRenderer<ModelEntity, EntityRelationship> graphRenderer = diagramFactory.getGraphRenderer().cloneSelf();
        graphRenderer.setAlgo( "neato" );
        graphRenderer.resetHighlight();
        if ( selectedEntityRel != null )
            graphRenderer.highlightEdge( selectedEntityRel );
        EntityNetworkMetaProvider metaProvider = new EntityNetworkMetaProvider( outputFormat,
                                                                                diagramFactory.getImageDirectory(),
                                                                                analyst,
                communityService );
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        EntityNetworkDOTExporter dotExporter = new EntityNetworkDOTExporter( metaProvider );
        graphRenderer.render( communityService, graph, dotExporter, outputFormat, ticket, outputStream );
    }

    @SuppressWarnings( "unchecked" )
    private List<ModelEntity> getEntities( CommunityService communityService ) {
        return segment == null ?
               (List<ModelEntity>) CollectionUtils.select( communityService.listActualEntities( entityClass, true ),
                                                           new Predicate() {
                                                               @Override
                                                               public boolean evaluate( Object object ) {
                                                                   return !( (ModelEntity) object ).isUnknown();
                                                               }
                                                           } ) :
                communityService.getPlanService().listEntitiesTaskedInSegment( entityClass, segment, ModelEntity.Kind.Actual );
    }

    private List<EntityRelationship> getEntityRels( CommunityService communityService, Analyst analyst ) {
        List<EntityRelationship> entityRels = new ArrayList<EntityRelationship>();
        List<ModelEntity> entities = getEntities( communityService );
        for ( ModelEntity entity : entities ) {
            for ( ModelEntity other : entities ) {
                if ( entity != other ) {
                    EntityRelationship<ModelEntity> entityRel = segment == null ?
                                                                analyst.findEntityRelationship( communityService,
                                                                                                entity,
                                                                                                other ) :
                                                                analyst.findEntityRelationshipInPlan( communityService,
                                                                        entity,
                                                                        other,
                                                                        segment );
                    if ( entityRel != null )
                        entityRels.add( entityRel );
                }
            }
        }
        return entityRels;
    }
}
