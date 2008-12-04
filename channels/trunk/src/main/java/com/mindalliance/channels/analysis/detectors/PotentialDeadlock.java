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
     * Detect cycles where all flows are critical requirements.
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return an Issue or null of none detected
     */
    public Issue detectIssue( ModelObject modelObject ) {
        Issue issue = null;
        Scenario scenario = (Scenario) modelObject;
        GraphBuilder graphBuilder = Project.graphBuilder();
        DirectedGraph<Node, Flow> digraph = graphBuilder.buildDirectedGraph( scenario );
        StrongConnectivityInspector<Node, Flow> sci =
                new StrongConnectivityInspector<Node, Flow>( digraph );
        List<Set<Node>> cycles = sci.stronglyConnectedSets();
        if ( !cycles.isEmpty() ) {
            Set<Flow> cyclicCriticalRequirements = new HashSet<Flow>();
            // For each cycle where all nodes have at least one critical requirement,
            // collect all critical requirements of the nodes in the cycle.
            for ( Set<Node> cycle : cycles ) {
                if ( cycle.size() > 1 ) {
                    // All nodes in the cycle have at least one ciritical requirement?
                    boolean allCritical = true;
                    // Critical requirements of nodes on the cycle
                    List<Flow> criticalRequirementsInCycle = new ArrayList<Flow>();
                    // Verify if all nodes in cycle has critical requirements and collect them
                    Iterator<Node> nodes = cycle.iterator();
                    while ( allCritical && nodes.hasNext() ) {
                        Node node = nodes.next();
                        // All critical requirements in a cycle
                        boolean nodeHasCritical = false;
                        Iterator<Flow> requirements = node.requirements();
                        while ( requirements.hasNext() ) {
                            Flow requirement = requirements.next();
                            if ( requirement.isCritical() ) {
                                nodeHasCritical = true;
                                criticalRequirementsInCycle.add( requirement );
                            }
                        }
                        if ( !nodeHasCritical ) {
                            allCritical = false;
                        }
                    }
                    // If all nodes in the cycle have critical requirements,
                    // add these requirements to the set
                    if ( allCritical ) {
                        cyclicCriticalRequirements.addAll( criticalRequirementsInCycle );
                    }
                }
            }
            // There are cycles where all nodes have critical requirements
            if ( !cyclicCriticalRequirements.isEmpty() ) {
                issue = new Issue( Issue.SYSTEMIC, scenario );
                issue.setDescription( "Potential deadlock if "
                        + getRequirementDescriptions( cyclicCriticalRequirements )
                        + " fails." );
                issue.setRemediation( "Provide redundancy for these critical flows." );
            }
        }
        return issue;
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
