package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.graph.GraphBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;
import java.util.ArrayList;

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
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        for ( ModelObject otherEntity : entities ) {
            if ( otherEntity != entity ) {
                EntityRelationship sendRel = dqo.findEntityRelationship( entity, otherEntity );
                if ( sendRel != null ) {
                    rels.add( sendRel );
                }
                EntityRelationship receiveRel = dqo.findEntityRelationship( otherEntity, entity );
                if ( receiveRel != null ) {
                    rels.add( receiveRel );
                }
            }
        }
        for ( EntityRelationship entityRel : rels ) {
            digraph.addVertex( entityRel.getToEntity( dqo ) );
            digraph.addVertex( entityRel.getFromEntity( dqo ) );
        }
        for ( EntityRelationship entityRel : rels ) {
            if ( entityRel != null ) {
                digraph.addEdge( entityRel.getFromEntity( dqo ), entityRel.getToEntity( dqo ), entityRel );
            }
        }
    }

}
