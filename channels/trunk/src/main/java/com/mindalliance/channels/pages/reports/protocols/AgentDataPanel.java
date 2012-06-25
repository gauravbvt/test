package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.entities.AgentData;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Agent data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/23/12
 * Time: 1:26 PM
 */
public class AgentDataPanel extends AbstractDataPanel {
    private AgentData agentData;

    public AgentDataPanel( String id, AgentData agentData ) {
        super( id );
        this.agentData = agentData;
        init();
    }

    private void init() {
        //todo
    }
}
