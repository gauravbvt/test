package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.query.QueryService;
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
    private QueryService queryService;

    public EntitiesNetworkGraphBuilder(
            List<ModelEntity> entities,
            List<EntityRelationship> entityRels,
            QueryService queryService ) {
        this.entities = entities;
        this.entityRels = entityRels;
        this.queryService = queryService;
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
                digraph.addEdge( (ModelEntity) entityRel.getFromIdentifiable( queryService ),
                        (ModelEntity) entityRel.getToIdentifiable( queryService ),
                        entityRel );
            }
        }
    }
}
