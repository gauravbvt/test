package com.mindalliance.channels.analysis.network;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.graph.GraphBuilder;
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
public class EntityNetworkGraphBuilder implements GraphBuilder<ModelObject, EntityRelationship> {

    private ModelObject entity;
    private List<? extends ModelObject> entities;
    private DataQueryObject dqo;

    public EntityNetworkGraphBuilder( ModelObject entity, List<? extends ModelObject> entities, DataQueryObject dqo ) {
        this.entity = entity;
        this.entities = entities;
        this.dqo = dqo;
    }

    public DirectedGraph<ModelObject, EntityRelationship> buildDirectedGraph() {
        DirectedGraph<ModelObject, EntityRelationship> digraph =
                new DirectedMultigraph<ModelObject, EntityRelationship>(
                        new EdgeFactory<ModelObject, EntityRelationship>() {

                            public EntityRelationship createEdge( ModelObject entity, ModelObject otherEntity ) {
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
        for ( ModelObject otherEntity : entities ) {
            if ( otherEntity != entity ) digraph.addVertex( otherEntity );
        }
        for ( ModelObject otherEntity : entities ) {
            if ( otherEntity != entity ) {
                EntityRelationship entityRel = dqo.findEntityRelationship( entity, otherEntity );
                if ( entityRel != null ) {
                    digraph.addEdge( entity, otherEntity, entityRel );
                }
            }
        }
    }


}
