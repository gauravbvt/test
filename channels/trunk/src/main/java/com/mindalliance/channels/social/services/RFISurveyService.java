package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFISurvey;

import java.util.List;

/**
 * RFI survey service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/12
 * Time: 11:16 AM
 */
public interface RFISurveyService extends GenericSqlService<RFISurvey, Long> {

    /**
     * Get all surveys for a plan, possibly restricting to open ones about a given what the questionnaire is about.
     *
     * @param plan     a plan
     * @param onlyOpen a boolean
     * @param about    a string
     * @return a list of surveys
     */
    List<RFISurvey> select( Plan plan, boolean onlyOpen, String about );

    /**
     * Get all surveys on a model object.
     *
     * @param plan        a plan
     * @param modelObject a model object
     * @return a list of surveys
     */
    List<RFISurvey> select( Plan plan, ModelObject modelObject );

    /**
     * Find response metrics for a survey.
     *
     * @param plan             a plan
     * @param rfiSurvey        a survey
     * @param answerSetService the answer set service
     * @param rfiService       an rfi service
     * @return a string like "105c 95i 3d" (105 completed, 95 incomplete 3 declined)
     */
    String findResponseMetrics( Plan plan, RFISurvey rfiSurvey, RFIService rfiService, AnswerSetService answerSetService );

    /**
     * Creates (i.e. launches) an RFISurvey on a model object using a questionnaire.
     *
     * @param plan          a plan
     * @param username      a string
     * @param questionnaire a questionnaire
     * @param modelObject   a model object
     * @return a survey
     */
    RFISurvey launch( Plan plan, String username, Questionnaire questionnaire, ModelObject modelObject );

    /**
     * Whether remediation of an issue is already surveyed (closed or ongoing).
     *
     * @param issue        an issue
     * @param queryService a query service
     * @return a rfi survey
     */
    RFISurvey findRemediationSurvey( Plan plan, final Issue issue, final QueryService queryService );

    /**
     * Find surveys using a given questionnaire.
     *
     * @param plan          a plan
     * @param questionnaire a questionnaire
     * @return a list of surveys
     */
    List<RFISurvey> findSurveys( Plan plan, Questionnaire questionnaire );

}
