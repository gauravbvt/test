package com.mindalliance.channels.graph;

import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Scenario;
import org.jgrapht.Graph;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 2:28:55 PM
 */
public interface GraphBuilder {

    Graph<Node, Flow> buildScenarioGraph(Scenario scenario);

    // other graphs here
}
