package com.mindalliance.channels.graph;

import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.Graph;

import java.io.Writer;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 4:15:11 PM
 */
public class StyledDOTExporter<V, E>  {

    private V highlightedVertex;
    private E highlightedEdge;

    private VertexNameProvider<V> vertexIDProvider;
    private VertexNameProvider<V> vertexLabelProvider;
    private EdgeNameProvider<E> edgeLabelProvider;
    DOTAttributeProvider<V, E> dotStyleProvider;

    public StyledDOTExporter() {
        this(new IntegerNameProvider<V>(), null, null, null);
    }


    public StyledDOTExporter(VertexNameProvider<V> vertexIDProvider,
                             VertexNameProvider<V> vertexLabelProvider,
                             EdgeNameProvider<E> edgeLabelProvider,
                             DOTAttributeProvider<V, E> dotStyleProvider) {
        this.vertexIDProvider = vertexIDProvider;
        this.vertexLabelProvider = vertexLabelProvider;
        this.edgeLabelProvider = edgeLabelProvider;
        this.dotStyleProvider = dotStyleProvider;
    }

    public void setHighlightedVertex(V highlightedVertex) {
        this.highlightedVertex = highlightedVertex;
    }

    public void setHighlightedEdge(E highlightedEdge) {
        this.highlightedEdge = highlightedEdge;
    }

    public void export(Writer writer, Graph<V,E> graph) {
        // TODO
    }



}
