package com.mindalliance.channels.playbook.pages.graphs;

import com.mindalliance.channels.playbook.graph.DirectedGraph;
import com.mindalliance.channels.playbook.graph.InfoFlow;
import com.mindalliance.channels.playbook.pages.SelectionManager;
import com.mindalliance.channels.playbook.support.models.Container;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 9, 2008
 * Time: 9:32:55 PM
 */
public class InfoFlowPanel  extends GraphPanel {

    public InfoFlowPanel(String id, IModel container, SelectionManager masterSelection) {
        super(id, container, masterSelection);
    }

    protected DirectedGraph makeDirectedGraph(Container container) {
        return new InfoFlow(container);
    }
}
