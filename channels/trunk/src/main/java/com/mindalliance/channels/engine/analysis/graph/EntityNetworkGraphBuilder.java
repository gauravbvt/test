/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.query.QueryService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * An entity network graph builder.
 */
public class EntityNetworkGraphBuilder implements GraphBuilder<ModelEntity, EntityRelationship> {

    /**
     * An entity.
     */
    private final ModelEntity entity;

    /**
     * A query service.
     */
    private final QueryService queryService;

    /**
     * The analyst.
     */
    private final Analyst analyst;

    public EntityNetworkGraphBuilder( ModelEntity entity, Analyst analyst, QueryService queryService ) {
        this.entity = entity;
        this.analyst = analyst;
        this.queryService = queryService;
    }

    @Override
    public DirectedGraph<ModelEntity, EntityRelationship> buildDirectedGraph() {
        DirectedGraph<ModelEntity, EntityRelationship> digraph =
                new DirectedMultigraph<ModelEntity, EntityRelationship>( new EdgeFactory<ModelEntity, EntityRelationship>() {

                    @Override
                    public EntityRelationship createEdge( ModelEntity entity, ModelEntity otherEntity ) {
                        return new EntityRelationship( entity, otherEntity );
                    }
                } );
        populateGraph( digraph );
        return digraph;
    }

    private void populateGraph( DirectedGraph<ModelEntity, EntityRelationship> digraph ) {
        digraph.addVertex( entity );
        List<EntityRelationship> rels = analyst.findEntityRelationships( null, entity, queryService );
        for ( EntityRelationship entityRel : rels ) {
            digraph.addVertex( (ModelEntity) entityRel.getToIdentifiable( queryService ) );
            digraph.addVertex( (ModelEntity) entityRel.getFromIdentifiable( queryService ) );
        }
        for ( EntityRelationship entityRel : rels )
            if ( entityRel != null )
                digraph.addEdge( (ModelEntity) entityRel.getFromIdentifiable( queryService ),
                                 (ModelEntity) entityRel.getToIdentifiable( queryService ),
                                 entityRel );
    }
}
