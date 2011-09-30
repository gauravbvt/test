package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.model.Model;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/29/11
 * Time: 2:15 PM
 */
public class PlanRequiredNetworkingPanel extends AbstractUpdatablePanel {
    public PlanRequiredNetworkingPanel( String id, Model<Plan> planModel, Set<Long> expansions ) {
        super( id, planModel, expansions );
        init();
    }

    private void init() {
        // todo
    }
}
