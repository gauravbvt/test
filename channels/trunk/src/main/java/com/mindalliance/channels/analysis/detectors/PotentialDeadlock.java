package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.util.SemMatch;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.analysis.network.FlowMapGraphBuilder;
import com.mindalliance.channels.graph.GraphBuilder;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Detects a potential deadlock in the scenario.
 * A deadlock is a cycle of requirements and outcomes where
 * at least one requirement is critical
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 4, 2008
 * Time: 9:28:29 AM
 */
public class PotentialDeadlock extends AbstractIssueDetector {

    public PotentialDeadlock() {
    }

    /**
     * Detect cycles where all flows are critical.
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return a list of Issues
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        GraphBuilder<Node,Flow> graphBuilder = new FlowMapGraphBuilder( scenario );
        DirectedGraph<Node, Flow> digraph = graphBuilder.buildDirectedGraph( );
        StrongConnectivityInspector<Node, Flow> sci =
                new StrongConnectivityInspector<Node, Flow>( digraph );
        List<Set<Node>> cycles = sci.stronglyConnectedSets();
        if ( !cycles.isEmpty() ) {
            // For each cycle where all nodes have at least one critical requirement,
            // collect all critical requirements of nodes in the cycle.
            for ( Set<Node> cycle : cycles ) {
                if ( cycle.size() > 1 ) {
                    // All nodes in the cycle have at least one ciritical requirement?
                    boolean allCritical = true;
                    // Critical requirements of nodes on the cycle
                    Set<Flow> criticalRequirementsInCycle = new HashSet<Flow>();
                    // Verify if all nodes in cycle has critical requirements
                    // uniquely sourced from another node in the cycle, and collect them
                    Iterator<Node> nodes = cycle.iterator();
                    while ( allCritical && nodes.hasNext() ) {
                        Node node = nodes.next();
                        Set<Flow> requirements = digraph.incomingEdgesOf( node );
                        // Select requirements that are critical and uniquely sourced
                        // from a node in the cycle
                        Set<Flow> criticalAndUnique = findCriticalAndUnique( requirements, cycle );
                        if ( criticalAndUnique.isEmpty() ) {
                            allCritical = false;
                        } else {
                            criticalRequirementsInCycle.addAll( criticalAndUnique );
                        }
                    }
                    // This is a "critical" cycle
                    if ( allCritical ) {
                        DetectedIssue issue = new DetectedIssue( DetectedIssue.STRUCTURAL, scenario );
                        issue.setDescription( "Potential deadlock if any of "
                                + getRequirementDescriptions( criticalRequirementsInCycle )
                                + " fails." );
                        issue.setRemediation( "Provide redundancy for at least one of these critical flows." );
                        issue.setSeverity( Issue.Level.Major );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    @SuppressWarnings( "unchecked" )
    // Select requirements that are critical and uniquely sourced
    // from a node in the cycle
    private Set<Flow> findCriticalAndUnique( final Set<Flow> requirements, final Set<Node> cycle ) {
        Set<Flow> result = new HashSet<Flow>();
        Iterator<Flow> criticalandUniqueFlows =
                new FilterIterator( requirements.iterator(), new Predicate() {
                    public boolean evaluate( Object obj ) {
                        boolean critical = false;
                        boolean unique = true;
                        Flow flow = (Flow) obj;
                        if ( flow.isCritical() && cycle.contains( flow.getSource() ) ) {
                            critical = true;
                            for ( Flow otherFlow : requirements ) {
                                if ( otherFlow != flow
                                        && SemMatch.same( otherFlow.getName(), flow.getName() ) )
                                    unique = false;
                            }
                        }
                        return critical && unique;
                    }
                } );
        while ( criticalandUniqueFlows.hasNext() ) {
            result.add( criticalandUniqueFlows.next() );
        }
        return result;
    }

    /**
     * Construct a string describing the list of requirements.
     *
     * @param requirements -- list of flows
     * @return a string description
     */
    private String getRequirementDescriptions( Set<Flow> requirements ) {
        StringBuilder sb = new StringBuilder();
        Iterator<Flow> iterator = requirements.iterator();
        while ( iterator.hasNext() ) {
            sb.append( '"' );
            sb.append( iterator.next().getRequirementTitle() );
            sb.append( '"' );
            if ( iterator.hasNext() ) sb.append( " or " );
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Scenario;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }
}
