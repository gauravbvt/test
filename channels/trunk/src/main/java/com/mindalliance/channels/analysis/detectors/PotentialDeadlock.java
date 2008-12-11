package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.graph.GraphBuilder;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.DirectedGraph;

import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

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
     * @return a list of Issues or null of none detected
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = null;
        Scenario scenario = (Scenario) modelObject;
        GraphBuilder graphBuilder = Project.graphBuilder();
        DirectedGraph<Node, Flow> digraph = graphBuilder.buildDirectedGraph( scenario );
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
                    // Verify if all nodes in cycle has critical requirements and collect them
                    Iterator<Node> nodes = cycle.iterator();
                    while ( allCritical && nodes.hasNext() ) {
                        Node node = nodes.next();
                        // All critical requirements in a cycle
                        boolean nodeHasCritical = false;
                        Iterator<Flow> requirements = node.requirements();
                        while ( requirements.hasNext() ) {
                            Flow requirement = requirements.next();
                            if ( requirement.isCritical() && cycle.contains( requirement.getSource() ) ) {
                                nodeHasCritical = true;
                                criticalRequirementsInCycle.add( requirement );
                            }
                        }
                        if ( !nodeHasCritical ) {
                            allCritical = false;
                        }
                    }
                    // This is a "critical" cycle
                    if ( allCritical ) {
                        if ( issues == null ) issues = new ArrayList<Issue>();
                        Issue issue = new Issue( Issue.SYSTEMIC, scenario );
                        issue.setDescription( "Potential deadlock if "
                                + getRequirementDescriptions( criticalRequirementsInCycle )
                                + " fails." );
                        issue.setRemediation( "Provide redundancy for these critical flows." );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
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
