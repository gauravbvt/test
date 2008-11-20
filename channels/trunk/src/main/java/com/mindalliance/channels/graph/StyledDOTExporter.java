package com.mindalliance.channels.graph;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.Graph;

import java.io.Writer;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 4:15:11 PM
 */
public class StyledDOTExporter<V, E> extends DOTExporter<V, E> {

    DOTStyleProvider<V, E> dotStyleProvider;

    public StyledDOTExporter(DOTStyleProvider<V, E> dotStyleProvider) {
        super();
        this.dotStyleProvider = dotStyleProvider;
    }

    @Override
    public void export(Writer writer, Graph<V,E> graph) {
        super.export(writer, graph);
    }


}
