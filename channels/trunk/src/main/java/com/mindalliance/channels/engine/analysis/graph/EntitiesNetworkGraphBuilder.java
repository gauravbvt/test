package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.core.model.ModelEntity;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Entities network graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 7, 2010
 * Time: 11:13:32 AM
 */
public class EntitiesNetworkGraphBuilder implements GraphBuilder<ModelEntity, EntityRelationship> {
    private List<ModelEntity> entities;
    private List<EntityRelationship> entityRels;
    private CommunityService communityService;

    public EntitiesNetworkGraphBuilder(
            List<ModelEntity> entities,
            List<EntityRelationship> entityRels,
            CommunityService communityService ) {
        this.entities = entities;
        this.entityRels = entityRels;
        this.communityService = communityService;
    }

    public DirectedGraph<ModelEntity, EntityRelationship> buildDirectedGraph() {
        DirectedGraph<ModelEntity, EntityRelationship> digraph =
                new DirectedMultigraph<ModelEntity, EntityRelationship>(
                        new EdgeFactory<ModelEntity, EntityRelationship>() {

                            public EntityRelationship createEdge(
                                    ModelEntity entity,
                                    ModelEntity otherEntity ) {
                                return new EntityRelationship( entity, otherEntity );
                            }

                        } );
        populateGraph( digraph, entities );
        return digraph;
    }

    private void populateGraph(
            DirectedGraph<ModelEntity, EntityRelationship> digraph,
            List<? extends ModelEntity> entities ) {
        for ( ModelEntity entity : entities ) {
            digraph.addVertex( entity );
        }
        for ( EntityRelationship entityRel : entityRels ) {
            if ( entityRel != null ) {
                digraph.addEdge( (ModelEntity) entityRel.getFromIdentifiable( communityService ),
                        (ModelEntity) entityRel.getToIdentifiable( communityService ),
                        entityRel );
            }
        }
    }
}
