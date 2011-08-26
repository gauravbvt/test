// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.engine.query;

import com.mindalliance.channels.core.attachments.AttachmentManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.UserService;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.engine.nlp.SemanticMatcher;

/**
 * A query service targeted to a single plan.
 */
public class PlanService extends DefaultQueryService {

    private final Plan plan;

    public PlanService(
            PlanManager planManager, SemanticMatcher semanticMatcher, UserService userService, Plan plan,
            AttachmentManager attachmentManager ) {

        super( planManager, attachmentManager, semanticMatcher, userService );
        this.plan = plan;
    }

    @Override
    public Plan getPlan() {
        return plan;
    }
}
