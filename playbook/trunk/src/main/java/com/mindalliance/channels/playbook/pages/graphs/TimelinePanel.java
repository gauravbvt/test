package com.mindalliance.channels.playbook.pages.graphs;

import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.graph.Timeline;
import com.mindalliance.channels.playbook.graph.DirectedGraph;
import com.mindalliance.channels.playbook.pages.SelectionManager;
import org.apache.wicket.model.IModel;
import org.apache.log4j.Logger;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 5, 2008
 * Time: 1:00:07 PM
 */
public class TimelinePanel extends GraphPanel {
    
    public TimelinePanel(String id, Container container, SelectionManager masterSelection) {
        super(id, container, masterSelection);
    }

    protected DirectedGraph makeDirectedGraph(Container container) {
        return new Timeline(container);
    }

}
