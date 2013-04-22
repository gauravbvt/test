package com.mindalliance.channels.graph;

import com.mindalliance.channels.core.community.CommunityService;
import org.jgrapht.Graph;

import java.io.Writer;
import java.util.Set;

/**
 * Styled DOT exporter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 11, 2008
 * Time: 1:57:07 PM
 */
public interface StyledDOTExporter<V,E> {

    void setHighlightedVertices( Set<V> vertices);

    void setHighlightedEdges( Set<E> edges);

    void export( CommunityService communityService, Writer writer, Graph<V, E> graph ) throws InterruptedException;

}
