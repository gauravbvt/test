package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
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
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * Detects single points of failure in a scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 3, 2008
 * Time: 7:45:00 PM
 */
public class SinglePointOfFailure extends AbstractIssueDetector {
    /**
     * Timestamped list of cutpoints
     */
    static class FoundCutpoints {
        /**
         * List of cutpoints
         */
        private Set<Node> cutpoints;
        /**
         * Timestamp of list of cutpoints
         */
        private Date timestamp;

        FoundCutpoints( Set<Node> cutpoints ) {
            this.cutpoints = cutpoints;
            timestamp = new Date();
        }

        public Set<Node> getCutpoints() {
            return cutpoints;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Minimum out degree of a part that is a bottleneck and thus a single point of failure.
     */
    private static final int MINIMUM_OUT_DEGREE = 2;
    /**
     * Cached cutpoints
     */
    private Map<Scenario, FoundCutpoints> cachedCutpoints = new HashMap<Scenario, FoundCutpoints>();

    public SinglePointOfFailure() {
    }

    /**
     * Detects one or more actors who play parts where they are bottlenecks.
     * A bottleneck is an "articulation vertex" (a point connecting otherwise
     * disjoint subgraphs) with a large enough out degree (count of outcomes).
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return a list of Issues or null of none detected
     */
    public List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = null;
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
        if ( actorNodes.size() > 0 ) {
            issues = new ArrayList<Issue>();
            for ( Node node : actorNodes ) {
                Issue issue = new Issue( Issue.SYSTEMIC, node );
                issue.setDescription( "Single point of failure" );
                issue.setRemediation( "Delegate responsibilities or add redundancy." );
                issues.add( issue );
            }
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
        Set<Node> cutpoints;
        FoundCutpoints foundCutpoints = cachedCutpoints.get( scenario );
        if ( foundCutpoints != null
                && !foundCutpoints.getTimestamp().before( scenario.lastModified() ) )
        {
            cutpoints = foundCutpoints.getCutpoints();
        } else {
            cutpoints = detectSignificantCutpoints( scenario );
            cachedCutpoints.put( scenario, new FoundCutpoints( cutpoints ) );
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
