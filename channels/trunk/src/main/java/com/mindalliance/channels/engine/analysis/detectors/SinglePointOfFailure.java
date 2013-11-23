/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

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
 * Detects single points of failure in a plan segment.
 */

public class SinglePointOfFailure extends AbstractIssueDetector {

    /**
     * Minimum out degree of a part that is a bottleneck and thus a single point of failure.
     */
    private static final int MINIMUM_DEGREE = 3;

    public SinglePointOfFailure() {
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }

    @Override
    protected String getKindLabel() {
        return "Single point of failure";
    }

    /**
     * Detects one or more actors who play parts where they are bottlenecks. A bottleneck is an "articulation vertex" (a
     * point connecting otherwise disjoint subgraphs) with a large enough out degree (count of sends).
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
                        cutpoints.add( actor );
        }
        return cutpoints;
    }
}
