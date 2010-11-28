package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.analysis.GraphBuilder;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.query.QueryService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

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
     * A query service.
     */
    private QueryService queryService;

    /** The analyst. */
    private Analyst analyst;

    public EntityNetworkGraphBuilder(
            ModelEntity entity, Analyst analyst ) {
        this.entity = entity;
        this.analyst = analyst;
        this.queryService = analyst.getQueryService();
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
        populateGraph( digraph, entity );
        return digraph;
    }

    private void populateGraph(
            DirectedGraph<ModelEntity, EntityRelationship> digraph,
            ModelEntity entity ) {
        digraph.addVertex( entity );
        List<EntityRelationship> rels = analyst.findEntityRelationships( null, entity );
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
