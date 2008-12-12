package com.mindalliance.channels.graph;

import org.jgrapht.Graph;

import java.util.Set;
import java.io.Writer;

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

    void export( Writer writer, Graph<V, E> graph );

}
