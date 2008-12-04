package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.pages.Project;
import org.jgrapht.alg.BlockCutpointGraph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.AsUndirectedGraph;

import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

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
     * @return an Issue or null of none detected
     */
    public Issue detectIssue( ModelObject modelObject ) {
        Issue issue = null;
        Scenario scenario = (Scenario) modelObject;
        GraphBuilder graphBuilder = Project.graphBuilder();
        DirectedGraph<Node, Flow> digraph = graphBuilder.buildDirectedGraph( scenario );
        BlockCutpointGraph<Node, Flow> bcg = new BlockCutpointGraph<Node, Flow>(
                new AsUndirectedGraph<Node, Flow>( digraph ) );
        Set<Node> cutpoints = bcg.getCutpoints();
        Iterator<Node> nodes = cutpoints.iterator();
        Set<Actor> spfs = new HashSet<Actor>();
        // Keep only cutpoints (articulation vertices) that are parts with actors
        // and with a minimum number of outcomes.
        while ( nodes.hasNext() ) {
            Node node = nodes.next();
            if ( node.isPart() ) {
                Part part = (Part) node;
                if ( part.getActor() != null
                        && digraph.outDegreeOf( part ) >= MINIMUM_OUT_DEGREE ) {
                    spfs.add( part.getActor() );
                }
            }
        }
        // Found single points of failure?
        if ( spfs.size() > 0 ) {
            issue = new Issue( Issue.SYSTEMIC, scenario );
            if (spfs.size() == 1) {
                issue.setDescription( getIssueDescription( spfs ) + " is a single point of failure.");
            }
            else {
                issue.setDescription( getIssueDescription( spfs ) + " are single points of failure.");
            }
            issue.setRemediation( "Delegate responsibilities or add redundancy." );
        }
        return issue;
    }

    /**
     * Constructs a string with the names of all actors in set
     *
     * @param actors -- set of actors
     * @return a String collating the names of given actors
     */
    private String getIssueDescription( Set<Actor> actors ) {
        StringBuilder sb = new StringBuilder();
        Iterator<Actor> iterator = actors.iterator();
        while ( iterator.hasNext() ) {
            Actor actor = iterator.next();
            sb.append( actor.getName() );
            if (iterator.hasNext()) sb.append( "," );
        }
        return sb.toString();
    }

    /**
     * Tests whether the detector applies to the model object
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return whether the detector applies
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Scenario;
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
