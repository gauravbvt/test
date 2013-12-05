package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A graph of all actors in a plan linked by the existence of communication commitments between them.
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

    private boolean allowCommitmentsToSelf = false;

    public ActorsNetworkGraphBuilder( QueryService queryService ) {
        this.queryService = queryService;
    }

    public ActorsNetworkGraphBuilder( QueryService queryService, boolean allowCommitmentsToSelf ) {
        this.queryService = queryService;
        this.allowCommitmentsToSelf = allowCommitmentsToSelf;
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
        List<Actor> allActors = queryService.listActualEntities( Actor.class );
        for ( Actor actor : allActors ) {
            digraph.addVertex( actor );
        }

        Map<Actor, Map<Actor, List<Flow>>> relFlows = new HashMap<Actor, Map<Actor, List<Flow>>>();
        Assignments assignments = queryService.getAssignments( false );
        for ( Segment segment : queryService.list( Segment.class ) ) {
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( flow.getSource().isPart() && flow.getTarget().isPart() ) {
                    List<Commitment> commitments = queryService.findAllCommitments( flow,
                                                                                    false,
                                                                                    assignments );
                    for ( Commitment commitment : commitments ) {
                        Actor fromActor = commitment.getCommitter().getActor();
                        Actor toActor = commitment.getBeneficiary().getActor();
                        if ( allowCommitmentsToSelf || !fromActor.equals( toActor ) ) {
                            Map<Actor, List<Flow>> toFlows = relFlows.get( fromActor );
                            if ( toFlows == null ) {
                                toFlows = new HashMap<Actor, List<Flow>>();
                                relFlows.put( fromActor, toFlows );
                            }
                            List<Flow> flowList = toFlows.get( toActor );
                            if ( flowList == null ) {
                                flowList = new ArrayList<Flow>();
                                toFlows.put( toActor, flowList );
                            }
                            if ( !flowList.contains( flow ) ) flowList.add( flow );
                        }
                    }
                }
            }
        }
        for ( Actor fromActor : allActors ) {
            for ( Actor toActor : allActors ) {
                if ( allowCommitmentsToSelf || !fromActor.equals( toActor ) ) {
                    if ( relFlows.containsKey( fromActor ) && relFlows.get( fromActor ).containsKey( toActor ) ) {
                        List<Flow> flows = relFlows.get( fromActor ).get( toActor );
                        if ( !flows.isEmpty() ) {
                            EntityRelationship<Actor> rel = new EntityRelationship<Actor>(
                                    fromActor,
                                    toActor );
                            rel.setFlows( flows );
                            digraph.addEdge(
                                    fromActor,
                                    toActor,
                                    rel
                            );
                        }
                    }
                }
            }
        }
    }
}
