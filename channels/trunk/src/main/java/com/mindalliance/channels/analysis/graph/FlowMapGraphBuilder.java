package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.analysis.GraphBuilder;
import com.mindalliance.channels.graph.diagrams.DirectedMultiGraphWithProperties;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Flow map graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 2, 2009
 * Time: 10:13:37 AM
 */
public class FlowMapGraphBuilder implements GraphBuilder<Node, Flow> {

    /**
     * A segment.
     */
    private Segment segment;
    /**
     * Whether to include needs and capabilities.
     */
    private boolean includeConnectors;

    private QueryService queryService;

    public FlowMapGraphBuilder( Segment segment, QueryService queryService, boolean includeConnectors ) {
        this.segment = segment;
        this.queryService = queryService;
        this.includeConnectors = includeConnectors;
    }

    public DirectedGraph<Node, Flow> buildDirectedGraph() {
        DirectedMultiGraphWithProperties<Node, Flow> digraph = new DirectedMultiGraphWithProperties<Node, Flow>(
                new EdgeFactory<Node, Flow>() {
                    /**
                     * Separate id generator for diagram-based flows.
                     */
                    private long IdCounter = 1L;

                    public Flow createEdge( Node sourceVertex, Node targetVertex ) {
                        InternalFlow flow = new InternalFlow( sourceVertex, targetVertex, "" );
                        flow.setId( IdCounter++ );
                        return flow;
                    }

                } );
        digraph.setProperty( "overriddenFlows", new ArrayList<Flow>() );
        digraph.setProperty( "overriddenParts", new ArrayList<Part>() );
        populateSegmentGraph( digraph, segment );
        return digraph;
    }

    /**
     * {@inheritDoc}
     */
    private void populateSegmentGraph( Graph<Node, Flow> graph, Segment segment ) {
        // add nodes as vertices
        Iterator<Node> nodes = segment.nodes();
        while ( nodes.hasNext() ) {
            final Node node = nodes.next();
            if ( !node.isConnector() && disconnected( node ) )
                // will not have edges
                graph.addVertex( node );
        }
        for ( Part initiator : queryService.findInitiators( segment ) ) {
            graph.addVertex( initiator );
        }
        for ( Part terminator : queryService.findExternalTerminators( segment ) ) {
            graph.addVertex( terminator );
        }

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
        findOverridden( graph, segment );

    }

    @SuppressWarnings( "unchecked" )
    private void findOverridden(
            Graph<Node, Flow> graph,
            Segment segment ) {
        List<Flow> overriddenFlows = (List<Flow>) ( (DirectedMultiGraphWithProperties) graph )
                .getProperty( "overriddenFlows" );
        List<Part> overriddenParts = (List<Part>) ( (DirectedMultiGraphWithProperties) graph )
                .getProperty( "overriddenParts" );
        for ( Part part : segment.listParts() ) {
            if ( queryService.isOverridden( part ) ) {
                overriddenParts.add( part );
                for ( Flow impliedFlow : queryService.findOverriddenSharingSends( part ) ) {
                    overriddenFlows.add( impliedFlow );
                }
                for ( Flow impliedFlow : queryService.findOverriddenSharingReceives( part ) ) {
                    overriddenFlows.add( impliedFlow );
                }
            }
        }
    }

    private boolean disconnected( Node node ) {
        if ( includeConnectors ) {
            return !node.sends().hasNext() && !node.receives().hasNext();
        } else {
            boolean connected = false;
            Iterator<Flow> sends = node.sends();
            while ( !connected && sends.hasNext() ) {
                connected = sends.next().isSharing();
            }
            Iterator<Flow> receives = node.receives();
            while ( !connected && receives.hasNext() ) {
                connected = receives.next().isSharing();
            }
            return !connected;
        }
    }

}
