package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.model.ModelObject;
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
public class EntityNetworkGraphBuilder implements GraphBuilder<ModelObject, EntityRelationship> {

    /**
     * A model object.
     */
    private ModelObject entity;
    /**
     * Related entities.
     */
    private List<? extends ModelObject> entities;
    /**
     * A query service.
     */
    private QueryService queryService;

    public EntityNetworkGraphBuilder(
            ModelObject entity,
            List<? extends ModelObject> entities,
            QueryService queryService ) {
        this.entity = entity;
        this.entities = entities;
        this.queryService = queryService;
    }

    public DirectedGraph<ModelObject, EntityRelationship> buildDirectedGraph() {
        DirectedGraph<ModelObject, EntityRelationship> digraph =
                new DirectedMultigraph<ModelObject, EntityRelationship>(
                        new EdgeFactory<ModelObject, EntityRelationship>() {

                            public EntityRelationship createEdge(
                                    ModelObject entity,
                                    ModelObject otherEntity ) {
                                return new EntityRelationship( entity, otherEntity );
                            }

                        } );
        populateGraph( digraph, entity, entities );
        return digraph;
    }

    private void populateGraph(
            DirectedGraph<ModelObject, EntityRelationship> digraph,
            ModelObject entity,
            List<? extends ModelObject> entities ) {
        digraph.addVertex( entity );
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        for ( ModelObject otherEntity : entities ) {
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
            digraph.addVertex( (ModelObject)entityRel.getToIdentifiable( queryService ) );
            digraph.addVertex( (ModelObject)entityRel.getFromIdentifiable( queryService ) );
        }
        for ( EntityRelationship entityRel : rels ) {
            if ( entityRel != null ) {
                digraph.addEdge( (ModelObject)entityRel.getFromIdentifiable( queryService ),
                        (ModelObject)entityRel.getToIdentifiable( queryService ),
                        entityRel );
            }
        }
    }

}
