package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.analysis.GraphBuilder;
import com.mindalliance.channels.analysis.graph.ActorsNetworkGraphBuilder;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.BlockCutpointGraph;
import org.jgrapht.graph.AsUndirectedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Detects single points of failure in a plan segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 3, 2008
 * Time: 7:45:00 PM
 */

public class SinglePointOfFailure extends AbstractIssueDetector {

    /**
     * Minimum out degree of a part that is a bottleneck and thus a single point of failure.
     */
    private static final int MINIMUM_DEGREE = 3;

    private Analyst analyst;

    public SinglePointOfFailure() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Single point of failure";
    }

    /**
     * Detects one or more actors who play parts where they are bottlenecks.
     * A bottleneck is an "articulation vertex" (a point connecting otherwise
     * disjoint subgraphs) with a large enough out degree (count of sends).
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return a list of Issues
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Plan plan = (Plan) modelObject;
        Set<Actor> spofActors = detectSignificantCutpoints();
        // Found single points of failure?
        for ( Actor actor : spofActors ) {
            DetectedIssue issue = makeIssue( DetectedIssue.ROBUSTNESS, plan );
            issue.setDescription( actor.getName() + " appears to be a single point of failure." );
            issue.setRemediation( "Delegate responsibilities of this agent\nor share them with other agents." );
            issue.setSeverity( Level.Medium );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Plan;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    private Set<Actor> detectSignificantCutpoints() {
        Set<Actor> cutpoints = new HashSet<Actor>();
        GraphBuilder<Actor, EntityRelationship<Actor>> graphBuilder = new ActorsNetworkGraphBuilder( analyst );
        final DirectedGraph<Actor, EntityRelationship<Actor>> digraph = graphBuilder.buildDirectedGraph();
        if ( !digraph.edgeSet().isEmpty() ) {
            BlockCutpointGraph<Actor, EntityRelationship<Actor>> bcg =
                    new BlockCutpointGraph<Actor, EntityRelationship<Actor>>(
                            new AsUndirectedGraph<Actor, EntityRelationship<Actor>>( digraph ) );
            for ( Actor actor : bcg.getCutpoints() ) {
                if ( digraph.outDegreeOf( actor ) >= MINIMUM_DEGREE
                        && digraph.inDegreeOf( actor ) >= MINIMUM_DEGREE ) {
                    cutpoints.add( actor );
                }
            }
        }
        return cutpoints;
    }

    public Analyst getAnalyst() {
        return analyst;
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }
}
