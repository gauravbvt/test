package com.mindalliance.channels.playbook.pages.graphs;

import com.mindalliance.channels.playbook.graph.DirectedGraph;
import com.mindalliance.channels.playbook.graph.NetworkingGraph;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.pages.SelectionManager;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 29, 2008
 * Time: 11:15:11 AM
 */
public class NetworkPanel extends GraphPanel {

    public NetworkPanel(String id, IModel container, SelectionManager masterSelection) {
        super(id, container, masterSelection);
    }

    protected DirectedGraph makeDirectedGraph(Container container) {
        return new NetworkingGraph(container);
    }
}
