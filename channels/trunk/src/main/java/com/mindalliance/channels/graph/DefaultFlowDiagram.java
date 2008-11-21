package com.mindalliance.channels.graph;

import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Flow;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.text.MessageFormat;

import org.apache.wicket.markup.html.link.ImageMap;
import org.jgrapht.Graph;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 2:56:50 PM
 */
public class DefaultFlowDiagram implements FlowDiagram {

    static private final String PNG = "png";
    static private final String SVG = "svg";
    static private final String IMAGE_MAP = "imap";

    private DefaultGraphBuilder graphBuilder;
    private GraphRenderer<Node, Flow> graphRenderer;
    private String urlFormat = "?scenario={0}&node={1}";  // 0: scenario id, 1: node id


    /**
     * Constructor
     */
    public DefaultFlowDiagram() {
    }

    public void setGraphBuilder(DefaultGraphBuilder graphBuilder) {
        this.graphBuilder = graphBuilder;
    }

    public void setGraphRenderer(GraphRenderer<Node,Flow> graphRenderer) {
        this.graphRenderer = graphRenderer;
    }

    public String getUrlFormat() {
        return urlFormat;
    }

    public void setUrlFormat(String urlFormat) {
        this.urlFormat = urlFormat;
    }

    /**
     * Produces the PNG stream of a directed graph diagram of the scenario.
     *
     * @param scenario     A scenario
     * @param selectedNode The scenario node currently selected
     * @return An InputStream
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    public InputStream getPNG(Scenario scenario, Node selectedNode) throws DiagramException {
        Graph<Node, Flow> graph = graphBuilder.buildScenarioGraph(scenario);
        graphRenderer.highlightVertex(selectedNode);
        return render(graph, PNG, scenario);
    }

    /**
     * Produces the SVG stream of a directed graph diagram of the scenario.
     *
     * @param scenario     A scenario
     * @param selectedNode The scenario node currently selected
     * @return An InputStream
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    public InputStream getSVG(Scenario scenario, Node selectedNode) throws DiagramException {
        Graph<Node, Flow> graph = graphBuilder.buildScenarioGraph(scenario);
        graphRenderer.highlightVertex(selectedNode);
        return render(graph, SVG, scenario);
    }


    /**
     * Gets an image map component for a directed graph diagram of the scenario
     *
     * @param scenario     A scenario
     * @param selectedNode The scenario node currently selected
     * @return an ImageMap
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    public ImageMap getImageMap(Scenario scenario, Node selectedNode) throws DiagramException {
        Graph<Node, Flow> graph = graphBuilder.buildScenarioGraph(scenario);
        InputStream in = render(graph, IMAGE_MAP, scenario);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new DiagramException("Failed to read generated diagram", e);
        }
        String map = sb.toString();
        map = map.replace("base referer", "base referer "); // patches apparent bug in dot
        System.out.println(map);
        return new ImageMap(map);
    }

    private InputStream render(Graph<Node, Flow> graph, String format, Scenario scenario) throws DiagramException {
        return graphRenderer.render(graph,
                getVertexIDProvider(),
                getVertexLabelProvider(),
                getEdgeLabelProvider(),
                getDOTAttributeProvider(),
                getUrlProvider(scenario),
                format
        );
    }

    private URLProvider<Node, Flow> getUrlProvider(final Scenario scenario) {
        return new URLProvider<Node, Flow>() {
            public String getVertexURL(Node vertex) {
                Object[] args = {scenario.getId(), vertex.getId()};
                return MessageFormat.format(urlFormat, args);
            }

            public String getEdgeURL(Flow edge) {
                return null;
            }
        };
    }

    private DOTAttributeProvider<Node, Flow> getDOTAttributeProvider() {
        return new DOTAttributeProvider<Node, Flow>() {
            public List<DOTAttribute> getGraphAttributes() {
                return new DOTAttribute("rankdir", "LR").asList();
            }

            public List<DOTAttribute> getVertexAttributes(Node vertex, boolean highlighted) {
                List<DOTAttribute> list = DOTAttribute.emptyList();
                if (vertex.isPart()) {
                    list.add(new DOTAttribute("shape", "box"));
                } else if (vertex.isConnector()) {
                    list.add(new DOTAttribute("shape", "point"));
                } else { // scenarioNode
                    list.add(new DOTAttribute("shape", "egg"));
                }
                if (highlighted) {
                    list.add(new DOTAttribute("style", "bold"));
                }
                return list;
            }

            public List<DOTAttribute> getEdgeAttributes(Flow edge, boolean highlighted) {
                List<DOTAttribute> list = DOTAttribute.emptyList();
                if (edge.isAskedFor()) {
                    list.add(new DOTAttribute("style", "dotted"));
                }
                return list;
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
                return "" + node.getId();
            }
        };
    }


}
