package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.social.model.rfi.RFISurvey;

/**
 * An issue remediation survey service that combines rfi repositories.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/19/12
 * Time: 10:33 AM
 */
public interface IssueRemediationSurveysDAO {

    /**
     * Get or create a questionnaire and survey for an issue.
     *
     * @param username     a string
     * @param plan         a plan
     * @param queryService a query service
     * @param issue        an issue
     * @return a survey
     */
    RFISurvey getOrCreateRemediationSurvey(
            String username,
            Plan plan,
            QueryService queryService,
            Issue issue );


}
