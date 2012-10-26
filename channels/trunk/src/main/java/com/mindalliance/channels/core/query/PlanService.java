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
import com.mindalliance.channels.core.dao.user.PlanParticipationValidationService;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.nlp.SemanticMatcher;
import com.mindalliance.channels.social.services.SurveysDAO;

/**
 * A query service targeted to a single plan.
 */
public class PlanService extends DefaultQueryService {

    private Plan plan;

    private String serverUrl;

    public PlanService() {
    }

    public PlanService( PlanManager planManager, SemanticMatcher semanticMatcher, ChannelsUserDao userDao,
                        AttachmentManager attachmentManager, PlanParticipationService planParticipationService,
                        PlanParticipationValidationService planParticipationConfirmationService, SurveysDAO surveysDao,
                        Plan plan ) {

        this( planManager,
                semanticMatcher,
                userDao,
                attachmentManager,
                planParticipationService,
                planParticipationConfirmationService,
                surveysDao );
        this.plan = plan;
    }

    public PlanService( PlanManager planManager, SemanticMatcher semanticMatcher, ChannelsUserDao userDao,
                        AttachmentManager attachmentManager, PlanParticipationService planParticipationService,
                        PlanParticipationValidationService planParticipationConfirmationService, SurveysDAO surveysDao ) {

        super( planManager,
                attachmentManager,
                semanticMatcher,
                userDao,
                planParticipationService,
                planParticipationConfirmationService,
                surveysDao );
    }

    @Override
    public Plan getPlan() {
        return plan;
    }

    public void setPlan( Plan plan ) {
        this.plan = plan;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl( String serverUrl ) {
        this.serverUrl = serverUrl;
    }


}
