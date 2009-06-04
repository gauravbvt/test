package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.graph.TriggerGraphBuilder;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Cycle detected in part triggering.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 1:50:43 PM
 */
public class CyclicTriggering extends AbstractIssueDetector {

    public CyclicTriggering() {
    }

    /**
      * {@inheritDoc}
      */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        GraphBuilder<Part, Flow> graphBuilder = new TriggerGraphBuilder( scenario );
        DirectedGraph<Part, Flow> digraph = graphBuilder.buildDirectedGraph( );
        StrongConnectivityInspector<Part, Flow> sci =
                new StrongConnectivityInspector<Part, Flow>( digraph );
        List<Set<Part>> cycles = sci.stronglyConnectedSets();
        if ( !cycles.isEmpty() ) {
            // For each cycle where all nodes have at least one critical requirement,
            // collect all critical requirements of nodes in the cycle.
            for ( Set<Part> cycle : cycles ) {
                if ( cycle.size() > 1 ) {
                    Issue issue = makeIssue( Issue.STRUCTURAL, scenario );
                    StringBuilder sb = new StringBuilder();
                    sb.append("These parts trigger each other in a loop: ");
                    int count = 0;
                    for (Part part : cycle) {
                        count++;
                        sb.append("\"");
                        sb.append(part.getTitle());
                        sb.append("\"");
                        if (count < cycle.size())
                            sb.append(", ");
                        else
                            sb.append(".");
                    }
                    issue.setDescription(sb.toString());
                    issue.setRemediation("Break the loop by having one of these tasks not trigger the other.");
                    issue.setSeverity( Issue.Level.Major);
                    issues.add(issue);
                }
            }
        }
        return issues;
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
    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Cyclic task triggering";
    }
}
