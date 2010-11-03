// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.query;

import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.attachments.AttachmentManager;

/**
 * A query service targeted to a single plan.
 */
public class PlanService extends DefaultQueryService {

    private Plan plan;

    public PlanService( PlanManager planManager, AttachmentManager attachmentManager, Plan plan ) {
        super( planManager, attachmentManager );
        this.plan = plan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Plan getPlan() {
        return plan;
    }
}
