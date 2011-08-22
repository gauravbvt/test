package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.analysis.GraphBuilder;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.EventTiming;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Flow map graph builder. Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary and Confidential.
 * User: jf Date: Apr 2, 2009 Time: 10:13:37 AM
 */
public class FlowMapGraphBuilder implements GraphBuilder<Node, Flow> {

    /** A segment. */
    private final Segment segment;

    /** Whether to include needs and capabilities. */
    private final boolean includeConnectors;

    private final QueryService queryService;

    public FlowMapGraphBuilder( Segment segment, QueryService queryService, boolean includeConnectors ) {
        this.segment = segment;
        this.queryService = queryService;
        this.includeConnectors = includeConnectors;
    }

    @Override
    public DirectedGraph<Node, Flow> buildDirectedGraph() {
        DirectedMultiGraphWithProperties<Node, Flow> digraph =
                new DirectedMultiGraphWithProperties<Node, Flow>( new EdgeFactory<Node, Flow>() {
                    /**
                     * Separate id generator for diagram-based flows.
                     */
                    private long idCounter = 1L;

                    @Override
                    public Flow createEdge( Node sourceVertex, Node targetVertex ) {
                        InternalFlow flow = new InternalFlow( sourceVertex, targetVertex, "" );
                        flow.setId( idCounter++ );
                        return flow;
                    }
                } );

        digraph.setProperty( "overriddenFlows", new ArrayList<Flow>() );
        digraph.setProperty( "overriddenParts", new ArrayList<Part>() );
        populateSegmentGraph( digraph );
        return digraph;
    }

    private void populateSegmentGraph( DirectedMultiGraphWithProperties<Node, Flow> graph ) {

        // add nodes as vertices
        Iterator<Node> nodes = segment.nodes();
        while ( nodes.hasNext() ) {
            Node node = nodes.next();
            if ( !node.isConnector() && disconnected( node ) )
                // will not have edges
                graph.addVertex( node );
        }

        for ( Part initiator : queryService.findInitiators( segment ) )
            graph.addVertex( initiator );
        for ( Part terminator : queryService.findExternalTerminators( segment ) )
            graph.addVertex( terminator );

        for ( EventTiming eventTiming : segment.getContext() )
            for ( Part part : queryService.findAllInitiators( eventTiming ) )
                graph.addVertex( part );

        // add parts/connectors as nodes and flows as edges
        Iterator<Flow> flows = segment.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( includeConnectors || flow.getSource().isPart() )
                graph.addVertex( flow.getSource() );
            if ( includeConnectors || flow.getTarget().isPart() )
                graph.addVertex( flow.getTarget() );
            if ( includeConnectors || flow.isSharing() )
                graph.addEdge( flow.getSource(), flow.getTarget(), flow );
            // add flows between capability connectors and external parts
            if ( flow.isCapability() ) {
                Connector connector = (Connector) flow.getTarget();
                Iterator<ExternalFlow> externalFlows = connector.externalFlows();
                while ( externalFlows.hasNext() ) {
                    ExternalFlow externalFlow = externalFlows.next();
                    graph.addVertex( externalFlow.getPart() );
                    graph.addEdge( externalFlow.getSource(), externalFlow.getTarget(), externalFlow );
                }
            }
        }

        findOverridden( (List<Flow>) graph.getProperty( "overriddenFlows" ),
                        (List<Part>) graph.getProperty( "overriddenParts" ) );
    }

    private void findOverridden( List<Flow> overriddenFlows, List<Part> overriddenParts ) {
        for ( Part part : segment.listParts() ) {
            if ( queryService.isOverridden( part ) ) {
                overriddenParts.add( part );
                for ( Flow impliedFlow : queryService.findOverriddenSharingSends( part ) )
                    overriddenFlows.add( impliedFlow );
                for ( Flow impliedFlow : queryService.findOverriddenSharingReceives( part ) )
                    overriddenFlows.add( impliedFlow );
            }
        }
    }

    private boolean disconnected( Node node ) {
        if ( includeConnectors ) {
            return !node.sends().hasNext() && !node.receives().hasNext();
        } else {
            Iterator<Flow> sends = node.sends();
            boolean notConnected = true;
            while ( notConnected && sends.hasNext() ) {
                notConnected = !sends.next().isSharing();
            }
            Iterator<Flow> receives = node.receives();
            while ( notConnected && receives.hasNext() ) {
                notConnected = !receives.next().isSharing();
            }
            return notConnected;
        }
    }
}
