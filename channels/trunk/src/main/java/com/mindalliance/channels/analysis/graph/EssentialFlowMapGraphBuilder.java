package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.ScenarioObject;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Critical flow map graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 15, 2010
 * Time: 2:07:20 PM
 */
public class EssentialFlowMapGraphBuilder implements GraphBuilder<Node, Flow> {
    /**
     * Scenario object presumed to fail.
     */
    private ScenarioObject scenarioObject;
    /**
     * Whether all alternates to downstream sharing flows are presumed to also fail.
     */
    boolean assumeFails;

    public EssentialFlowMapGraphBuilder( ScenarioObject scenarioObject, boolean assumeFails ) {
        this.scenarioObject = scenarioObject;
        this.assumeFails = assumeFails;
    }

    public DirectedGraph<Node, Flow> buildDirectedGraph() {
        DirectedGraph<Node, Flow> digraph = new DirectedMultigraph<Node, Flow>(
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
         populateScenarioGraph( digraph, scenarioObject, assumeFails );
         return digraph;
    }

    private void populateScenarioGraph(
            DirectedGraph<Node, Flow> graph,
            ScenarioObject scenarioObject,
            boolean assumeFails ) {
        List<Flow> essentialFlows = scenarioObject.getEssentialFlows( assumeFails );
        for (Flow flow : essentialFlows ) {
            graph.addVertex( flow.getSource() );
            graph.addVertex( flow.getTarget() );
            graph.addEdge( flow.getSource(), flow.getTarget(), flow );
        }
    }
}
