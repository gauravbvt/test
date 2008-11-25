package com.mindalliance.channels.graph;

import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 25, 2008
 * Time: 1:42:16 PM
 *
 * A provider of graph, vertex and edge attribute providers
 * @param <V> a vertex class
 * @param <E> an edge class
 */
public interface MetaProvider<V,E> {

    /**
     * Gets a URLProvider
     * @return a URLProvider
     */
    URLProvider<V,E> getURLProvider();

    /**
     * Gets a DOT attribute provider
     * @return a DOT attribute provider
     */
    DOTAttributeProvider<V,E> getDOTAttributeProvider();

    /**
     * Gets an edge label provider
     * @return a an edge label provider
     */
    EdgeNameProvider<E> getEdgeLabelProvider();

    /**
     * Gets an edge label provider
     * @return an edge label provider
     */
    VertexNameProvider<V> getVertexLabelProvider();

    /**
     * Gets a vertex id provider
     * @return a vertex id provider
     */
    VertexNameProvider<V> getVertexIDProvider();


}
