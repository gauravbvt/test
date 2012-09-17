package com.mindalliance.channels.graph;

import com.mindalliance.channels.core.query.QueryService;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 4:16:53 PM
 */
public interface DOTAttributeProvider<V, E> {

    /**
     * Gets semi-colon-separated style declarations for the graph
     *
     * @return the style declarations
     */
    List<DOTAttribute> getGraphAttributes();

    /**
     * Gets semi-colon-separated style declarations for subgraphs
     *
     * @param highlighted a boolean
     * @return the style declarations
     */
    List<DOTAttribute> getSubgraphAttributes( boolean highlighted );

    /**
     * Gets a comma-separated style declarations for a vertex
     *
     *
     * @param queryService
     * @param vertex      -- a vertex
     * @param highlighted -- whether the vertex is to be highlighted
     * @return the style declarations
     */
    List<DOTAttribute> getVertexAttributes( QueryService queryService, V vertex, boolean highlighted );

    /**
     * Gets a comma-separated style declarations for an edge
     *
     *
     * @param queryService
     * @param edge        -- an edge
     * @param highlighted -- whether the edge is to be highlighted
     * @return the style declarations
     */
    List<DOTAttribute> getEdgeAttributes( QueryService queryService, E edge, boolean highlighted );

}