package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.model.ModelEntity;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.List;

/**
 * An entity network graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 8:52:20 PM
 */
public class EntityNetworkGraphBuilder implements GraphBuilder<ModelEntity, EntityRelationship> {

    /**
     * An entity.
     */
    private ModelEntity entity;
    /**
     * Related entities.
     */
    private List<? extends ModelEntity> entities;
    /**
     * A query service.
     */
    private QueryService queryService;

    public EntityNetworkGraphBuilder(
            ModelEntity entity,
            List<? extends ModelEntity> entities,
            QueryService queryService ) {
        this.entity = entity;
        this.entities = entities;
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
        populateGraph( digraph, entity, entities );
        return digraph;
    }

    private void populateGraph(
            DirectedGraph<ModelEntity, EntityRelationship> digraph,
            ModelEntity entity,
            List<? extends ModelEntity> entities ) {
        digraph.addVertex( entity );
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        for ( ModelEntity otherEntity : entities ) {
            if ( otherEntity != entity ) {
                EntityRelationship sendRel = queryService.findEntityRelationship( entity, otherEntity );
                if ( sendRel != null ) {
                    rels.add( sendRel );
                }
                EntityRelationship receiveRel = queryService.findEntityRelationship( otherEntity, entity );
                if ( receiveRel != null ) {
                    rels.add( receiveRel );
                }
            }
        }
        for ( EntityRelationship entityRel : rels ) {
            digraph.addVertex( (ModelEntity)entityRel.getToIdentifiable( queryService ) );
            digraph.addVertex( (ModelEntity)entityRel.getFromIdentifiable( queryService ) );
        }
        for ( EntityRelationship entityRel : rels ) {
            if ( entityRel != null ) {
                digraph.addEdge( (ModelEntity)entityRel.getFromIdentifiable( queryService ),
                        (ModelEntity)entityRel.getToIdentifiable( queryService ),
                        entityRel );
            }
        }
    }

}
