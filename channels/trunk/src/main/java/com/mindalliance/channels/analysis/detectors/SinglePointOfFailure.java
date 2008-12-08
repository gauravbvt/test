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

/**
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
    private static final int MINIMUM_OUT_DEGREE = 2;

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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = null;
        Part part = (Part)modelObject;
        Scenario scenario = part.getScenario();
        GraphBuilder graphBuilder = Project.graphBuilder();
        // TODO -- cache graphs when scenario change notification implemented
        DirectedGraph<Node, Flow> digraph = graphBuilder.buildDirectedGraph( scenario );
        BlockCutpointGraph<Node, Flow> bcg = new BlockCutpointGraph<Node, Flow>(
                new AsUndirectedGraph<Node, Flow>( digraph ) );
        Set<Node> cutpoints = bcg.getCutpoints();
        Iterator<Node> nodes = cutpoints.iterator();
        Set<Node> actorNodes = new HashSet<Node>();
        // Keep only cutpoints (articulation vertices) that are parts with actors
        // and with a minimum number of outcomes.
        while ( nodes.hasNext() ) {
            Node node = nodes.next();
            if ( node == part ) {
                if ( part.getActor() != null
                        && digraph.outDegreeOf( part ) >= MINIMUM_OUT_DEGREE ) {
                    actorNodes.add( part );
                }
            }
        }
        // Found single points of failure?
        if ( actorNodes.size() == 1 ) {
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
     * Tests whether the detector applies to the model object
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return whether the detector applies
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    /**
     * Gets the name of the specific property tested, if applicable
     *
     * @return the name of a property or null if test applies to some combination of properties
     */
    public String getTestedProperty() {
        return null;
    }
}
