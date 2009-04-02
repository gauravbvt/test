package com.mindalliance.channels.graph;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.network.ScenarioRelationship;
import org.jgrapht.DirectedGraph;

import java.util.List;

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
