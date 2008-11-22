package com.mindalliance.channels.graph;

import org.jgrapht.Graph;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

import java.io.InputStream;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 1:05:18 PM
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
     * @param vertexIDProvider     -- a vertex ID provider
     * @param vertexLabelProvider  -- a vertex label provider
     * @param edgeLabelProvider    -- an edge lable provider
     * @param dotAttributeProvider -- a DOT attribute provider
     * @param urlProvider          -- a URL provider
     * @param format               -- an output format (png, svg, imap etc.)
     * @return an InputStream on the rendered graph
     * @throws DiagramException -- if generation fails
     */
    InputStream render( Graph<V, E> graph,
                        VertexNameProvider<V> vertexIDProvider,
                        VertexNameProvider<V> vertexLabelProvider,
                        EdgeNameProvider<E> edgeLabelProvider,
                        DOTAttributeProvider<V, E> dotAttributeProvider,
                        URLProvider<V, E> urlProvider,
                        String format ) throws DiagramException;

}
