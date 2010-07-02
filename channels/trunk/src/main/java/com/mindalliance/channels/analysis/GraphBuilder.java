package com.mindalliance.channels.analysis;

import org.jgrapht.DirectedGraph;

/**
 * Graph builder.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 2:28:55 PM
 */
public interface GraphBuilder<V,E> {

    /**
     * Build a directed graph
     *
     * @return a Graph
     */
    DirectedGraph<V, E> buildDirectedGraph( );


}
