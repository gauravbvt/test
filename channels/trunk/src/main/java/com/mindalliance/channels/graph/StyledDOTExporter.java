package com.mindalliance.channels.graph;

import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.Graph;
import org.jgrapht.DirectedGraph;

import java.io.Writer;
import java.io.PrintWriter;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 4:15:11 PM
 */
public class StyledDOTExporter<V, E>  {

    private V highlightedVertex;
    private E highlightedEdge;

    private VertexNameProvider<V> vertexIDProvider;
    private VertexNameProvider<V> vertexLabelProvider;
    private EdgeNameProvider<E> edgeLabelProvider;
    DOTAttributeProvider<V, E> attributeProvider;

    public StyledDOTExporter() {
        this(new IntegerNameProvider<V>(), null, null, null);
    }


    public StyledDOTExporter(VertexNameProvider<V> vertexIDProvider,
                             VertexNameProvider<V> vertexLabelProvider,
                             EdgeNameProvider<E> edgeLabelProvider,
                             DOTAttributeProvider<V, E> attributeProvider) {
        this.vertexIDProvider = vertexIDProvider;
        this.vertexLabelProvider = vertexLabelProvider;
        this.edgeLabelProvider = edgeLabelProvider;
        this.attributeProvider = attributeProvider;
    }

    public void setHighlightedVertex(V highlightedVertex) {
        this.highlightedVertex = highlightedVertex;
    }

    public void setHighlightedEdge(E highlightedEdge) {
        this.highlightedEdge = highlightedEdge;
    }

    public void export(Writer writer, Graph<V,E> g) {
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
            attributeProvider.getGraphAttributes();
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
            if (attributeProvider != null) {
                out.print(",");
                out.print(attributeProvider.getVertexAttributes(v, highlightedVertex == v));
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
            if (attributeProvider != null) {
                out.print(",");
                out.print(attributeProvider.getEdgeAttributes(e, highlightedEdge == e));
            }
            out.println("];");
        }
        // Close graph
        out.println("}");
        out.flush();
    }

    private String getVertexID(V v) {
       return vertexIDProvider.getVertexName(v);  // assume vertex name is DOT-compliant
    }
    

}
