package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.TriggerGraphBuilder;
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
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        GraphBuilder<Part, Flow> graphBuilder = new TriggerGraphBuilder( segment );
        DirectedGraph<Part, Flow> digraph = graphBuilder.buildDirectedGraph();
        StrongConnectivityInspector<Part, Flow> sci =
                new StrongConnectivityInspector<Part, Flow>( digraph );
        List<Set<Part>> cycles = sci.stronglyConnectedSets();
        if ( !cycles.isEmpty() ) {
            // For each cycle where all nodes have at least one critical receive,
            // collect all critical receives of nodes in the cycle.
            for ( Set<Part> cycle : cycles ) {
                if ( cycle.size() > 1 ) {
                    Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, segment );
                    StringBuilder sb = new StringBuilder();
                    sb.append( "These tasks trigger each other in a loop: " );
                    int count = 0;
                    for ( Part part : cycle ) {
                        count++;
                        sb.append( "\"" );
                        sb.append( part.getTitle() );
                        sb.append( "\"" );
                        if ( count < cycle.size() )
                            sb.append( ", " );
                        else
                            sb.append( "." );
                    }
                    issue.setDescription( sb.toString() );
                    issue.setRemediation( "Break the loop by having one of these tasks not trigger the other." );
                    issue.setSeverity( Level.Low );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Segment;
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
    protected String getKindLabel() {
        return "Task triggered, directly or not, by task it triggers";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
