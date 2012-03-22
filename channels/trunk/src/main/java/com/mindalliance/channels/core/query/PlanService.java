/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.nlp.SemanticMatcher;

/**
 * A query service targeted to a single plan.
 */
public class PlanService extends DefaultQueryService {

    private Plan plan;

    public PlanService() {
    }

    public PlanService( PlanManager planManager, SemanticMatcher semanticMatcher, ChannelsUserDao userDao,
                        AttachmentManager attachmentManager, PlanParticipationService planParticipationService,
                        Plan plan ) {

        this( planManager, semanticMatcher, userDao, attachmentManager, planParticipationService );
        this.plan = plan;
    }

    public PlanService( PlanManager planManager, SemanticMatcher semanticMatcher, ChannelsUserDao userDao,
                        AttachmentManager attachmentManager, PlanParticipationService planParticipationService ) {

        super( planManager, attachmentManager, semanticMatcher, userDao, planParticipationService );
    }

    @Override
    public Plan getPlan() {
        return plan;
    }

    public void setPlan( Plan plan ) {
        this.plan = plan;
    }

 }
