package com.mindalliance.channels.graph;

import org.jgrapht.EdgeFactory;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.InternalFlow;

/**
 * Flow factory for graphs.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 11, 2008
 * Time: 11:14:56 AM
 */
public class FlowFactory implements EdgeFactory<Node, Flow> {

    /** Separate id generator for diagram-based flows. */
    private static long IdCounter = 1L;

    /**
     * Constructor
     */
    public FlowFactory() {
    }

    /**
     * Creates a new flow for purposes of analytics.
     *
     * @param sourceVertex a Node
     * @param targetVertex a Node
     * @return a Flow
     */
    public Flow createEdge( Node sourceVertex, Node targetVertex ) {
        InternalFlow flow = new InternalFlow( sourceVertex, targetVertex, "" );
        flow.setId( IdCounter++  );
        return flow;
    }
}
