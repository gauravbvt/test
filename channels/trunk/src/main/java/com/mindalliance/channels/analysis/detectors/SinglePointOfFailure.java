package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.util.SimpleCache;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.pages.Project;
import org.jgrapht.alg.BlockCutpointGraph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.AsUndirectedGraph;

import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * Detects single points of failure in a scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 3, 2008
 * Time: 7:45:00 PM
 */

// NOT USED BECAUSE NOT MEANINGFUL
public class SinglePointOfFailure extends AbstractIssueDetector {

    /**
     * Minimum out degree of a part that is a bottleneck and thus a single point of failure.
     */
    private static final int MINIMUM_OUT_DEGREE = 2;
    /**
     * Cached cutpoints
     */
    private SimpleCache<Scenario, Set<Node>> cachedCutpoints =
            new SimpleCache<Scenario, Set<Node>>();

    public SinglePointOfFailure() {
    }

    /**
     * Detects one or more actors who play parts where they are bottlenecks.
     * A bottleneck is an "articulation vertex" (a point connecting otherwise
     * disjoint subgraphs) with a large enough out degree (count of outcomes).
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return a list of Issues
     */
    public List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        Scenario scenario = part.getScenario();
        Iterator<Node> nodes = getCutpoints( scenario );
        Set<Node> actorNodes = new HashSet<Node>();
        // Keep only cutpoints (articulation vertices) that are parts with actors
        // and with a minimum number of outcomes.
        while ( nodes.hasNext() ) {
            Node node = nodes.next();
            // If the target node is one of the cutpoint nodes
            if ( node == part ) {
                if ( part.getActor() != null ) {
                    actorNodes.add( part );
                }
            }
        }
        // Found single points of failure?
        for ( Node node : actorNodes ) {
            DetectedIssue issue = new DetectedIssue( DetectedIssue.STRUCTURAL, node );
            issue.setDescription( "Single point of failure." );
            issue.setRemediation( "Delegate responsibilities or add redundancy." );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    private Iterator<Node> getCutpoints( Scenario scenario ) {
        Set<Node> cutpoints = cachedCutpoints.get( scenario, scenario.lastModified() );
        if ( cutpoints == null ) {
            cutpoints = detectSignificantCutpoints( scenario );
            cachedCutpoints.put( scenario, cutpoints );
        }
        return cutpoints.iterator();
    }

    private Set<Node> detectSignificantCutpoints( Scenario scenario ) {
        GraphBuilder graphBuilder = Project.graphBuilder();
        final DirectedGraph<Node, Flow> digraph = graphBuilder.buildDirectedGraph( scenario );
        BlockCutpointGraph<Node, Flow> bcg = new BlockCutpointGraph<Node, Flow>(
                new AsUndirectedGraph<Node, Flow>( digraph ) );
        Set<Node> cutpoints = new HashSet<Node>();
        for ( Node node : bcg.getCutpoints() ) {
            if ( digraph.outDegreeOf( node ) >= MINIMUM_OUT_DEGREE ) {
                cutpoints.add( node );
            }
        }
        return cutpoints;
    }
}
