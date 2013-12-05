/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.ActorsNetworkGraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.core.query.QueryService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.BlockCutpointGraph;
import org.jgrapht.graph.AsUndirectedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Detects actors wjo are "single points of failure".
 */

public class ActorIsSinglePointOfFailure extends AbstractIssueDetector {

    /**
     * Minimum out degree of a part that is a bottleneck and thus a single point of failure.
     */
    private static final int MINIMUM_DEGREE = 1;

    public ActorIsSinglePointOfFailure() {
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }

    @Override
    protected String getKindLabel() {
        return "Agent is single point of failure";
    }

    /**
     * Detects one or more actors who play parts where they are bottlenecks. A bottleneck is an "articulation vertex" (a
     * point connecting otherwise disjoint subgraphs) with a large enough out degree (count of sends).
     *
     * See http://ravi-bhide.blogspot.com/2011/05/experiments-in-graph-3-coloring-block.html for a discussion of
     * block-cutpoint graphs.
     *
     * @param communityService the community service
     * @param modelObject -- the ModelObject being analyzed
     * @return a list of Issues
     */
    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Plan plan = (Plan) modelObject;
        Set<Actor> spofActors = detectSignificantCutpoints( queryService );
        // Found single points of failure?
        for ( Actor actor : spofActors ) {
            DetectedIssue issue = makeIssue( communityService, Issue.ROBUSTNESS, plan );
            issue.setDescription( actor.getName() + " appears to be a single point of failure." );
            issue.setRemediation( " Generalize task specifications so that " + actor.getName()
                                  + " is not the only agent assigned to a critical task"
                                  + "\n or allow participation as this agent by more than one user"
                                  + "\n or give this agent a job in placeholder organization with multi-participation"
                                  + "\nor add participating agents to the scope with the same role and organization" );
            issue.setSeverity( Level.Medium );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Plan;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    private static Set<Actor> detectSignificantCutpoints( QueryService queryService ) {
        Set<Actor> cutpoints = new HashSet<Actor>();
        GraphBuilder<Actor, EntityRelationship<Actor>> graphBuilder = new ActorsNetworkGraphBuilder( queryService );
        DirectedGraph<Actor, EntityRelationship<Actor>> digraph = graphBuilder.buildDirectedGraph();
        if ( !digraph.edgeSet().isEmpty() ) {
            BlockCutpointGraph<Actor, EntityRelationship<Actor>> bcg =
                    new BlockCutpointGraph<Actor, EntityRelationship<Actor>>( new AsUndirectedGraph<Actor, EntityRelationship<Actor>>( digraph ) );
            for ( Actor actor : bcg.getCutpoints() )
                if ( digraph.outDegreeOf( actor ) >= MINIMUM_DEGREE && digraph.inDegreeOf( actor ) >= MINIMUM_DEGREE )
                    if ( actor.isAbsoluteSingularParticipation( queryService ) )
                        cutpoints.add( actor );
        }
        return cutpoints;
    }
}
