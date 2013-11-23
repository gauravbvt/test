package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.FlowMapGraphBuilder;
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
 * Detects a potential deadlock in segment.
 * A deadlock is a cycle of receives and sends where
 * at least one receive is critical
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
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Potential task deadlock (task waiting on itself)";
    }

    /**
     * Detect cycles where all flows are critical.
     *
     *
     * @param communityService
     * @param modelObject -- the ModelObject being analyzed
     * @return a list of Issues
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Segment segment = (Segment) modelObject;
        GraphBuilder<Node,Flow> graphBuilder =
                new FlowMapGraphBuilder( segment, communityService, false );
        DirectedGraph<Node, Flow> digraph = graphBuilder.buildDirectedGraph( );
        StrongConnectivityInspector<Node, Flow> sci =
                new StrongConnectivityInspector<Node, Flow>( digraph );
        List<Set<Node>> cycles = sci.stronglyConnectedSets();
        if ( !cycles.isEmpty() ) {
            // For each cycle where all nodes have at least one critical receive,
            // collect all critical receives of nodes in the cycle.
            for ( Set<Node> cycle : cycles ) {
                if ( cycle.size() > 1 ) {
                    // All nodes in the cycle have at least one ciritical receive?
                    boolean allCritical = true;
                    // Critical receives of nodes on the cycle
                    Set<Flow> criticalReceiveInCycle = new HashSet<Flow>();
                    // Verify if all nodes in cycle has critical receives
                    // uniquely sourced from another node in the cycle, and collect them
                    Iterator<Node> nodes = cycle.iterator();
                    while ( allCritical && nodes.hasNext() ) {
                        Node node = nodes.next();
                        Set<Flow> receives = digraph.incomingEdgesOf( node );
                        // Select receives that are critical and uniquely sourced
                        // from a node in the cycle
                        Set<Flow> criticalAndUnique = findCriticalAndUnique( receives, cycle );
                        if ( criticalAndUnique.isEmpty() ) {
                            allCritical = false;
                        } else {
                            criticalReceiveInCycle.addAll( criticalAndUnique );
                        }
                    }
                    // This is a "critical" cycle
                    if ( allCritical ) {
                        Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, segment );
                        issue.setDescription( "Potential deadlock if any of "
                                + getReceiveDescriptions( criticalReceiveInCycle )
                                + " fails." );
                        issue.setRemediation( "Provide redundancy for at least one of these critical flows." );
                        issue.setSeverity( Level.Medium );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    @SuppressWarnings( "unchecked" )
    // Select receives that are critical and uniquely sourced
    // from a node in the cycle
    private Set<Flow> findCriticalAndUnique( final Set<Flow> receives, final Set<Node> cycle ) {
        Set<Flow> result = new HashSet<Flow>();
        Iterator<Flow> criticalandUniqueFlows =
                new FilterIterator( receives.iterator(), new Predicate() {
                    public boolean evaluate( Object obj ) {
                        boolean critical = false;
                        boolean unique = true;
                        Flow flow = (Flow) obj;
                        if ( flow.isCritical() && cycle.contains( flow.getSource() ) ) {
                            critical = true;
                            for ( Flow otherFlow : receives ) {
                                if ( otherFlow != flow
                                        && Matcher.same( otherFlow.getName(), flow.getName() ) )
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
     * Construct a string describing the list of receives.
     *
     * @param receives -- list of flows
     * @return a string description
     */
    private String getReceiveDescriptions( Set<Flow> receives ) {
        StringBuilder sb = new StringBuilder();
        Iterator<Flow> iterator = receives.iterator();
        while ( iterator.hasNext() ) {
            sb.append( '"' );
            sb.append( iterator.next().getReceiveTitle() );
            sb.append( '"' );
            if ( iterator.hasNext() ) sb.append( " or " );
        }
        return sb.toString();
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
}
