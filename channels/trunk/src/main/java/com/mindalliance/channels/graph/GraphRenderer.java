package com.mindalliance.channels.graph;

import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Node;

import java.io.InputStream;

import org.jgrapht.Graph;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.ext.EdgeNameProvider;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 1:05:18 PM
 */
public interface GraphRenderer<V,E> {

    void highlightVertex(V vertex);

    void highlightEdge(E edge);

    void resetHighlight();
    
    InputStream render(Graph<V,E> graph,
                              VertexNameProvider<V> vertexIDProvider,
                              VertexNameProvider<V> vertexLabelProvider,
                              EdgeNameProvider<E> edgeLabelProvider,
                              DOTAttributeProvider<V,E> dotAttributeProvider,
                              URLProvider<V,E> urlProvider,
                              String format) throws DiagramException;

}
