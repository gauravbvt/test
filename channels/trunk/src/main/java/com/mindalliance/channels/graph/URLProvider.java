package com.mindalliance.channels.graph;

import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 1:34:02 PM
 * A provider of URLs
 * @param <V> a vertex class
 * @param <E> an edge class
 */
public interface URLProvider<V, E> extends Serializable {

    /**
     * The URL for the graph that contains the vertex
     * @param vertex -- a vertex
     * @return a URL string
     */
    String getGraphURL( V vertex );
    /**
     * The vertex's URL. Returns null if none.
     * @param vertex -- a vertex
     * @return a URL string
     */
    String getVertexURL( V vertex );

    /**
     * The edges's URL. Returns null if none.
     * @param edge -- an edge
     * @return a URL string
     */
    String getEdgeURL( E edge );
}
