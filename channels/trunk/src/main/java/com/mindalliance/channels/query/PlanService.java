// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.query;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.nlp.SemanticMatcher;

/**
 * A query service targeted to a single plan.
 */
public class PlanService extends DefaultQueryService {

    private Plan plan;

    public PlanService(
        PlanManager planManager, SemanticMatcher semanticMatcher,
        UserService userService, Plan plan ) {
        super(
                planManager,
                planManager.getAttachmentManager(),
                semanticMatcher,
                userService );
        this.plan = plan;
    }

    @Override
    public Plan getPlan() {
        return plan;
    }
}
