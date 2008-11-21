package com.mindalliance.channels.graph;

import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.Graph;
import org.jgrapht.DirectedGraph;

import java.io.Writer;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 4:15:11 PM
 */
public class StyledDOTExporter<V, E> {

    private Set<V> highlightedVertices;
    private Set<E> highlightedEdges;

    private VertexNameProvider<V> vertexIDProvider;
    private VertexNameProvider<V> vertexLabelProvider;
    private EdgeNameProvider<E> edgeLabelProvider;
    DOTAttributeProvider<V, E> attributeProvider;
    URLProvider<V, E> urlProvider;

    public StyledDOTExporter() {
        this(new IntegerNameProvider<V>(), null, null, null, null);
    }


    public StyledDOTExporter(VertexNameProvider<V> vertexIDProvider,
                             VertexNameProvider<V> vertexLabelProvider,
                             EdgeNameProvider<E> edgeLabelProvider,
                             DOTAttributeProvider<V, E> attributeProvider,
                             URLProvider<V, E> urlProvider) {
        this.vertexIDProvider = vertexIDProvider;
        this.vertexLabelProvider = vertexLabelProvider;
        this.edgeLabelProvider = edgeLabelProvider;
        this.attributeProvider = attributeProvider;
        this.urlProvider = urlProvider;
    }

    public void setHighlightedVertices(Set<V> highlightedVertices) {
        this.highlightedVertices = highlightedVertices;
    }

    public void setHighlightedEdges(Set<E> highlightedEdges) {
        this.highlightedEdges = highlightedEdges;
    }

    public void export(Writer writer, Graph<V, E> g) {
        PrintWriter out = new PrintWriter(writer);
        String indent = "  ";
        String connector;
        // Graph declaration
        if (g instanceof DirectedGraph) {
            connector = " -> ";
            out.println("digraph G {");
        } else {
            connector = " -- ";
            out.println("graph G {");
        }
        if (attributeProvider != null) {
            out.print(asGraphAttributes(attributeProvider.getGraphAttributes()));
        }
        out.println();
        // Vertices
        for (V v : g.vertexSet()) {
            out.print(indent + getVertexID(v));
            out.print("[");
            if (vertexLabelProvider != null) {
                out.print(
                        "label = \"" + vertexLabelProvider.getVertexName(v) + "\"");
            }
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            if (attributeProvider != null) {
                attributes.addAll(attributeProvider.getVertexAttributes(v, highlightedVertices.contains(v)));
            }
            if (urlProvider != null) {
                String url = urlProvider.getVertexURL(v);
                if (url != null) attributes.add(new DOTAttribute("URL", url));
            }
            if (!attributes.isEmpty()) {
            out.print(",");
            out.print(asElementAttributes(attributes));
            }
            out.println("];");
        }
        // Edges
        for (E e : g.edgeSet()) {
            String source = getVertexID(g.getEdgeSource(e));
            String target = getVertexID(g.getEdgeTarget(e));

            out.print(indent + source + connector + target);
            out.print("[");
            if (edgeLabelProvider != null) {
                out.print(
                        "label = \"" + edgeLabelProvider.getEdgeName(e) + "\"");
            }
            List<DOTAttribute> attributes = DOTAttribute.emptyList();
            if (attributeProvider != null) {
                attributes.addAll(attributeProvider.getEdgeAttributes(e, highlightedEdges.contains(e)));
            }
            if (urlProvider != null) {
                String url = urlProvider.getEdgeURL(e);
                if (url != null) attributes.add(new DOTAttribute("URL", url));
            }
            if (!attributes.isEmpty()) {
            out.print(",");
            out.print(asElementAttributes(attributes));
            }
            out.println("];");
        }
        // Close graph
        out.println("}");
        out.flush();
    }

    private String asGraphAttributes(List<DOTAttribute> attributes) {
        StringBuilder sb = new StringBuilder();
        for (DOTAttribute attribute : attributes) {
            sb.append(attribute.toString());
            sb.append(";\n");
        }
        return sb.toString();
    }

    private String asElementAttributes(List<DOTAttribute> attributes) {
        StringBuilder sb = new StringBuilder();
        for (DOTAttribute attribute : attributes) {
            sb.append(attribute.toString());
            sb.append(",");
        }
        return sb.toString();
    }

    private String getVertexID(V v) {
        return vertexIDProvider.getVertexName(v);  // assume vertex name is DOT-compliant
    }


}
