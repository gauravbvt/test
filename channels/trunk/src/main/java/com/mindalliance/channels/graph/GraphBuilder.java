package com.mindalliance.channels.graph;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.StringWriter;
import java.util.Iterator;

/**
 * Builds the graph structure of a scenario
 *
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 3:02:40 PM
 */
public class GraphBuilder {

    private Scenario scenario;

    /**
     * Constructor
     * @param scenario -- the scenario to be graphed
     */
    public GraphBuilder(Scenario scenario) {
        this.scenario = scenario;
    }

    /**
     * Build a graph from the scenario
     * @return a DirectedGraph
     */
    private DirectedGraph<Node, Flow> getDirectedGraph() {
        DirectedGraph<Node, Flow> dgraph = new DefaultDirectedGraph<Node, Flow>(Flow.class);
        // add flows as edges
        Iterator<Flow> flows = scenario.flows();
        while(flows.hasNext()) {
            Flow flow = flows.next();
            dgraph.addEdge(flow.getSource(), flow.getTarget(), flow);
        }
        // add nodes as vertices
        Iterator<Node> nodes = scenario.iterator();
        while(nodes.hasNext()) {
            dgraph.addVertex(nodes.next());  // added if not part of a flow
        }
        return dgraph;
    }

    /**
     * Produces a description of a graph in DOT format
     * @param selectedNode -- the selected Node
     * @return a String
     */
    public String getDot(Node selectedNode) {
        DirectedGraph<Node, Flow> dgraph = getDirectedGraph();
        StyledDOTExporter<Node, Flow> styledDotExporter = new StyledDOTExporter<Node, Flow>(getVertexIDProvider(),
                                                                                            getVertexLabelProvider(),
                                                                                            getEdgeLabelProvider(),
                                                                                            getDOTAttributeProvider());
        styledDotExporter.setHighlightedVertex(selectedNode);
        StringWriter writer = new StringWriter();
        styledDotExporter.export(writer, dgraph);
        return writer.toString();
    }

    private DOTAttributeProvider<Node, Flow> getDOTAttributeProvider() {
        return new DOTAttributeProvider<Node,Flow>() {
          public String getGraphAttributes() {
                return "";
            }
          public String getVertexAttributes(Node vertex, boolean highlighted) {
               StringBuilder sb = new StringBuilder();
                if (vertex.isPart()) {
                  sb.append("shape=box");
                }
              else if (vertex.isConnector()) {
                    sb.append("shape=point");
                }
              else { // scenarioNode
                    sb.append("shape=egg");
                }
              if (highlighted) {
                  sb.append(",style=bold");
              }
              return sb.toString();
            }
          public String getEdgeAttributes(Flow edge, boolean highlighted) {
                return "";
            }
        };
    }

    private EdgeNameProvider<Flow> getEdgeLabelProvider() {
        return new EdgeNameProvider<Flow>() {
            public String getEdgeName(Flow flow) {
                return flow.getName();
            }
        };
    }

    private VertexNameProvider<Node> getVertexLabelProvider() {
        return new VertexNameProvider<Node>() {
            public String getVertexName(Node node) {
                return node.getName();
            }
        };
    }

    private VertexNameProvider<Node> getVertexIDProvider() {
        return new VertexNameProvider<Node>() {
            public String getVertexName(Node node) {
                return ""+node.getId();
            }
        };
    }
}
