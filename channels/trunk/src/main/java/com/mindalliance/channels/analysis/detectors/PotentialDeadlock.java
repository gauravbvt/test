package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.graph.GraphBuilder;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.DirectedGraph;

import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

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
     * Detect cycles where at least one flow is a critical requirement.
     *
     * @param modelObject -- the ModelObject being analyzed
     * @return an Issue or null of none detected
     */
    public Issue detectIssue( ModelObject modelObject ) {
        Issue issue = null;
        Scenario scenario = (Scenario) modelObject;
        GraphBuilder graphBuilder = Project.graphBuilder();
        DirectedGraph<Node, Flow> digraph = graphBuilder.buildDirectedGraph( scenario );
        CycleDetector<Node, Flow> cycleDetector = new CycleDetector<Node, Flow>( digraph );
        Set<Node> cycleNodes = cycleDetector.findCycles();
        if ( !cycleNodes.isEmpty() ) {
            List<Flow> criticalRequirements = new ArrayList<Flow>();
            for ( Node node : cycleNodes ) {
                Iterator<Flow> requirements = node.requirements();
                while ( requirements.hasNext() ) {
                    Flow requirement = requirements.next();
                    if ( requirement.isCritical() ) {
                        criticalRequirements.add( requirement );
                    }
                }
            }
            if ( !criticalRequirements.isEmpty() ) {
                issue = new Issue( Issue.SYSTEMIC, scenario );
                issue.setDescription( "Potential deadlock if "
                        + getRequirementDescriptions( criticalRequirements )
                        + " fails." );
                issue.setRemediation( "Provide redundancy for these critical flows." );
            }
        }
        return issue;
    }

    /**
     * Construct a string describing the list of requirements.
     * @param requirements -- list of flows
     * @return a string description
     */
    private String getRequirementDescriptions( List<Flow> requirements ) {
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
