/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.core.model.ModelEntity;
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
    private final CommunityService communityService;

    /**
     * The analyst.
     */
    private final Analyst analyst;

    public EntityNetworkGraphBuilder( ModelEntity entity, Analyst analyst, CommunityService communityService ) {
        this.entity = entity;
        this.analyst = analyst;
        this.communityService = communityService;
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
        List<EntityRelationship> rels = analyst.findEntityRelationshipsInPlan( null, entity, communityService );
        for ( EntityRelationship entityRel : rels ) {
            digraph.addVertex( (ModelEntity) entityRel.getToIdentifiable( communityService ) );
            digraph.addVertex( (ModelEntity) entityRel.getFromIdentifiable( communityService ) );
        }
        for ( EntityRelationship entityRel : rels )
            if ( entityRel != null )
                digraph.addEdge( (ModelEntity) entityRel.getFromIdentifiable( communityService ),
                                 (ModelEntity) entityRel.getToIdentifiable( communityService ),
                                 entityRel );
    }
}
