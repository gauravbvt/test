package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.InternalFlow;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.Iterator;

/**
 * A graph of parts as nodes and trigger flows as edges.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 10, 2009
 * Time: 2:09:45 PM
 */
public class TriggerGraphBuilder implements GraphBuilder<Part, Flow> {
    /**
     * A scenario to be analyzed for part trigger cycles.
     */
    private Scenario scenario;

    public TriggerGraphBuilder( Scenario scenario ) {
        this.scenario = scenario;
    }


    public DirectedGraph<Part, Flow> buildDirectedGraph() {
        DirectedGraph<Part, Flow> digraph = new DirectedMultigraph<Part, Flow>(
                new EdgeFactory<Part, Flow>() {
                    /**
                     * Separate id generator for diagram-based flows.
                     */
                    private long IdCounter = 1L;
                    public Flow createEdge( Part sourceVertex, Part targetVertex ) {
                        InternalFlow flow = new InternalFlow( sourceVertex, targetVertex, "" );
                        flow.setId( IdCounter++ );
                        return flow;
                    }

                } );
        populateScenarioGraph( digraph, scenario );
        return digraph;
    }

    private void populateScenarioGraph( DirectedGraph<Part, Flow> digraph, Scenario scenario ) {
        // Add triggering flows as edges, with triggering -- flow --> triggered
        Iterator<Flow> flows = scenario.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if (flow.isTriggeringToTarget()) {
                Node source = flow.getSource();
                Node target = flow.getTarget();
                if (source.isPart() && target.isPart()) {
                    digraph.addVertex( (Part)source );
                    digraph.addVertex( (Part)target );
                    digraph.addEdge( (Part)source, (Part)target, flow );
                }
            }
            if (flow.isTriggeringToSource()) {
                Node source = flow.getSource();
                Node target = flow.getTarget();
                if (source.isPart() && target.isPart()) {
                    digraph.addVertex( (Part)source );
                    digraph.addVertex( (Part)target );
                    digraph.addEdge( (Part)target, (Part)source, flow );
                }
            }
        }

    }
}
