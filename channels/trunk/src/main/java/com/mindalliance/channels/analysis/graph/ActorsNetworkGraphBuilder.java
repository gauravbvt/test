package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.model.Actor;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * A graph of all actors in a plan linked by the existence of sharing commitments between them.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 23, 2009
 * Time: 10:17:28 AM
 */
public class ActorsNetworkGraphBuilder implements GraphBuilder<Actor, EntityRelationship<Actor>> {

    /**
     * A query service.
     */
    private QueryService queryService;


    public ActorsNetworkGraphBuilder( QueryService queryService ) {
        this.queryService = queryService;
    }

    /**
     * {@inheritDoc}
     */
    public DirectedGraph<Actor, EntityRelationship<Actor>> buildDirectedGraph() {
        DirectedGraph<Actor, EntityRelationship<Actor>> digraph =
                new DirectedMultigraph<Actor, EntityRelationship<Actor>>(
                        new EdgeFactory<Actor, EntityRelationship<Actor>>() {
                            /**
                             * {@inheritDoc}
                             */
                            public EntityRelationship<Actor> createEdge(
                                    Actor actor,
                                    Actor otherActor ) {
                                return new EntityRelationship<Actor>( actor, otherActor );
                            }

                        } );
        populateGraph( digraph );
        return digraph;
    }

    private void populateGraph( DirectedGraph<Actor, EntityRelationship<Actor>> digraph ) {
        List<Actor> allActors = queryService.list( Actor.class );
        for ( Actor actor : allActors ) {
            digraph.addVertex( actor );
        }
        for ( Actor fromActor : allActors ) {
            for ( Actor toActor : allActors ) {
                if ( !fromActor.equals( toActor ) ) {
                    EntityRelationship<Actor> rel = queryService.findEntityRelationship( fromActor, toActor );
                    if ( rel != null ) {
                        digraph.addEdge(
                                (Actor) rel.getFromIdentifiable( queryService ),
                                (Actor) rel.getToIdentifiable( queryService ),
                                rel
                        );
                    }
                }
            }
        }
    }
}
