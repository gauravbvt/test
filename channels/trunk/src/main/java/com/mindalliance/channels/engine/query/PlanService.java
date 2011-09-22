/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.query;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.UserDao;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.engine.nlp.SemanticMatcher;

/**
 * A query service targeted to a single plan.
 */
public class PlanService extends DefaultQueryService {

    private final Plan plan;

    public PlanService( PlanManager planManager, SemanticMatcher semanticMatcher, UserDao userDao, Plan plan,
                        AttachmentManager attachmentManager ) {

        super( planManager, attachmentManager, semanticMatcher, userDao );
        this.plan = plan;
    }

    @Override
    public Plan getPlan() {
        return plan;
    }
}
