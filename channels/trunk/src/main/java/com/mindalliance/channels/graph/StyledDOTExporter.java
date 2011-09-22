package com.mindalliance.channels.graph;

import com.mindalliance.channels.core.query.QueryService;
import org.jgrapht.Graph;

import java.io.Writer;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 11, 2008
 * Time: 1:57:07 PM
 */
public interface StyledDOTExporter<V,E> {

    void setHighlightedVertices( Set<V> vertices);

    void setHighlightedEdges( Set<E> edges);

    void export( QueryService queryService, Writer writer, Graph<V, E> graph );

}
