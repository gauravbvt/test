package com.mindalliance.channels.pages.components;

import org.apache.wicket.model.IModel;

import java.util.Set;

import com.mindalliance.channels.Scenario;

/**
 * A table with external flows.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2009
 * Time: 7:38:33 PM
 */
public class ExternalFlowsPanel extends AbstractTablePanel {

    private IModel<Scenario> toScenarioModel;

    public ExternalFlowsPanel(
            String id,
            IModel<Scenario> fromScenarioModel,
            IModel<Scenario> toScenarioModel,
            int pageSize,
            Set<Long> expansions ) {
        super( id, fromScenarioModel, pageSize, expansions );
        init();
    }

    private void init() {
        // TODO
    }
}
