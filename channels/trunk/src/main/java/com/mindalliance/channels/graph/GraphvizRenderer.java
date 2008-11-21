package com.mindalliance.channels.graph;

import org.jgrapht.Graph;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 9:56:53 AM
 * <p/>
 * Renders a dot-formatted diagram specification using graphviz.
 */
public class GraphvizRenderer<V, E> implements GraphRenderer<V, E> {

    private String dotPath = "/usr/bin/dot";   // configured

    private Set<V> highlightedVertices;
    private Set<E> highlightedEdges;

    public GraphvizRenderer() {
        resetHighlight();
    }

    public String getDotPath() {
        return dotPath;
    }

    public void setDotPath(String path) {
        dotPath = path;
    }

    public void highlightVertex(V vertex) {
        highlightedVertices.add(vertex);
    }

    public void highlightEdge(E edge) {
        highlightedEdges.add(edge);
    }

    public void resetHighlight() {
        highlightedVertices = new HashSet<V>();
        highlightedEdges = new HashSet<E>();
    }

    public InputStream render(Graph<V,E> graph,
                              VertexNameProvider<V> vertexIDProvider,
                              VertexNameProvider<V> vertexLabelProvider,
                              EdgeNameProvider<E> edgeLabelProvider,
                              DOTAttributeProvider<V,E> dotAttributeProvider,
                              URLProvider<V,E> urlProvider,
                              String format) throws DiagramException {
        String dot = getDOT(graph,
                            vertexIDProvider,
                            vertexLabelProvider,
                            edgeLabelProvider,
                            dotAttributeProvider,
                            urlProvider);
        return doRender(dot, format);
    }

    /**
     * Renders a graph specified in DOT in a given format.
     *
     * @param dot    Graph description in DOT language
     * @param format a Grpahviz output format ("png", "svg", "imap" etc.)
     * @return an InputStream with the generated output
     * @throws DiagramException if generation fails
     */
    private InputStream doRender(String dot, String format) throws DiagramException {
        String command = getDotPath() + " -T" + format;
        Process p = null;
        int exitValue;
        try {
            // start process
            p = Runtime.getRuntime().exec(command);
            // send dot string
            OutputStream input = p.getOutputStream();  // process input
            StringReader reader = new StringReader(dot);
            int c;
            while ((c = reader.read()) > 0) {
                input.write(c);
            }
            input.flush();
            input.close();
            // wait for process to complete
            exitValue = p.waitFor();   // assumes the dot always terminates
            if (exitValue != 0) {
                // grab error if any
                BufferedInputStream error = new BufferedInputStream(p.getErrorStream());  // process error
                StringBuilder buffer = new StringBuilder();
                while (error.available() != 0) {
                    buffer.append((char) error.read());
                }
                String errorMessage = buffer.toString().trim();
                throw new Exception(errorMessage);
            }
            return p.getInputStream();   // return process output stream
        } catch (Exception e) {
            System.err.println(e);   // TODO -- replace with logging
            if (p != null) p.destroy();
            throw new DiagramException("Diagram generation failed", e);
        }
    }

    /**
     * Produces a description of a graph in DOT format
     *
     * @param graph -- the graph to be converted to DOT format
     * @param vertexIDProvider  -- a name provider
     * @param vertexLabelProvider  -- a label provider
     * @param edgeLabelProvider  -- a label provider
     * @param dotAttributeProvider  -- a "style" attribute provider
     * @param urlProvider -- a URL provider
     * @return a String in DOT format
     */
    public String getDOT(Graph<V, E> graph,
                         VertexNameProvider<V> vertexIDProvider,
                         VertexNameProvider<V> vertexLabelProvider,
                         EdgeNameProvider<E> edgeLabelProvider,
                         DOTAttributeProvider<V,E> dotAttributeProvider,
                         URLProvider<V,E> urlProvider) {
        StyledDOTExporter<V,E> styledDotExporter = new StyledDOTExporter<V,E>(  vertexIDProvider,
                                                                                vertexLabelProvider,
                                                                                edgeLabelProvider,
                                                                                dotAttributeProvider,
                                                                                urlProvider);
        styledDotExporter.setHighlightedVertices(highlightedVertices);
        styledDotExporter.setHighlightedEdges(highlightedEdges);
        StringWriter writer = new StringWriter();
        styledDotExporter.export(writer, graph);
        return writer.toString();
    }

}
