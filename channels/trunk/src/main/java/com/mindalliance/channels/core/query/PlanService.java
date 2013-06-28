/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.nlp.SemanticMatcher;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.db.services.users.UserRecordService;

/**
 * A query service targeted to a single plan.
 */
public class PlanService extends DefaultQueryService {

    private Plan plan;

    private String serverUrl;

    public PlanService() {
    }

    public PlanService( PlanManager planManager, SemanticMatcher semanticMatcher, UserRecordService userDao,
                        AttachmentManager attachmentManager, SurveysDAO surveysDao,
                        Plan plan ) {

        this( planManager,
                semanticMatcher,
                userDao,
                attachmentManager,
                surveysDao );
        this.plan = plan;
    }

    public PlanService( PlanManager planManager, SemanticMatcher semanticMatcher, UserRecordService userDao,
                        AttachmentManager attachmentManager, SurveysDAO surveysDao ) {

        super( planManager,
                attachmentManager,
                semanticMatcher,
                userDao,
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
