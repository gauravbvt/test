package com.mindalliance.channels.graph;

import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Scenario;
import org.jgrapht.Graph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 2:28:55 PM
 */
public interface GraphBuilder {

    /**
     * Build a directed graph representing the scenario
     *
     * @param scenario -- a scenario
     * @return a Graph
     */
    DirectedGraph<Node, Flow> buildDirectedGraph( Scenario scenario );

}
