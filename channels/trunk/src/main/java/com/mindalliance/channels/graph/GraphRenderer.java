package com.mindalliance.channels.graph;

import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 1:05:18 PM
 * @param <V> a vertex class
 * @param <E> an edge class
 */
public interface GraphRenderer<V, E> {

    /**
     * Highlight a vertex
     *
     * @param vertex -- a vertex
     */
    void highlightVertex( V vertex );

    /**
     * Highlight an edge
     *
     * @param edge -- an edge
     */
    void highlightEdge( E edge );

    /**
     * Remove all highlights
     */
    void resetHighlight();

    /**
     * @param graph                -- a Graph
     * @param format               -- an output format (png, svg, imap etc.)
     * @param output               -- the rendered graph
     * @param dotExporter         -- a DOT generator
     * @throws DiagramException -- if generation fails
     */
    void render( Graph<V, E> graph,
                 StyledDOTExporter<V,E> dotExporter,
                 String format,
                 OutputStream output ) throws DiagramException;

    /**
     * Set highlights to given edge and vertex.
     * @param vertex a vertex. Clear if null
     * @param edge an edge. Clear if null
     */
    void highlight( V vertex, E edge );
}
