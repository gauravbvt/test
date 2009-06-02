package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Plan index panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 1, 2009
 * Time: 11:41:35 AM
 */
public class PlanIndexPanel extends AbstractCommandablePanel {

    public PlanIndexPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super(id, model,expansions);
        init();
    }

    private void init() {
        // TODO
    }

    /**
     * Get the plan being edited.
     * @return a plan
     */
    public Plan getPlan() {
        return (Plan)getModel().getObject();
    }
    
}
