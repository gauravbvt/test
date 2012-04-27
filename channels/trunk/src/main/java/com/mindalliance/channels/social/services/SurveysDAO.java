package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;

import java.util.List;

/**
 * An issue remediation survey service that combines rfi repositories.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/19/12
 * Time: 10:33 AM
 */
public interface SurveysDAO {

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

    /**
     * Count a user's unanswered, active RFIs in a given plan.
     *
     * @param plan         a plan
     * @param user         a user
     * @param queryService a query service
     * @return an int
     */
    int countUnanswered( Plan plan, ChannelsUser user, QueryService queryService, Analyst analyst );

    /**
     * Count a user's partially answered, active RFIs in a given plan.
     *
     * @param plan         a plan
     * @param user         a user
     * @param queryService a query service
     * @return an int
     */
    int countIncomplete( Plan plan, ChannelsUser user, QueryService queryService, Analyst analyst );

    /**
     * Count a user's overdue, active RFIs in for a given plan.
     *
     * @param plan         a plan
     * @param user         a user
     * @param queryService a query service
     * @return an int
     */
    int countLate( Plan plan, ChannelsUser user, QueryService queryService, Analyst analyst );

    /**
     * Find a user's incomplete, active RFIs in a plan.
     *
     * @param plan         a plan
     * @param user         a user
     * @param queryService a query service
     * @param analyst      an analyst
     * @return a list of RFIs
     */
    List<RFI> findIncompleteRFIs( Plan plan, ChannelsUser user, QueryService queryService, Analyst analyst );

    /**
     * Find a user's completed, active RFIs in a plan.
     *
     * @param plan         a plan
     * @param user         a user
     * @param queryService a query service
     * @param analyst      an analyst
     * @return a list of RFIs
     */
    List<RFI> findCompletedRFIs( Plan plan, ChannelsUser user, QueryService queryService, Analyst analyst );

    /**
     * Whether all required questions in an RFI have been answered.
     *
     * @param rfi an RFI
     * @return a boolean
     */
    boolean isCompleted( final RFI rfi );

    /**
     * Find a user's declined, active RFIs in a plan.
     *
     * @param plan         a plan
     * @param user         a user
     * @param queryService a query service
     * @param analyst      an analyst
     * @return a list of RFIs
     */
    List<RFI> findDeclinedRFIs( Plan plan, ChannelsUser user, QueryService queryService, Analyst analyst );

    /**
     * Get count of required questions in an rfi.
     *
     * @param rfi an RFI
     * @return and int
     */
    int getRequiredQuestionCount( RFI rfi );

    /**
     * Get count of required answers given in an rfi.
     *
     * @param rfi an RFI
     * @return and int
     */
    int getRequiredAnswersCount( RFI rfi );

    String findResponseMetrics( Plan plan, final RFISurvey rfiSurvey );

    /**
     * Find the RFIs in a given survey with answers.
     *
     * @param plan             a plan
     * @param rfiSurvey        a survey
     * @return a string like "105c 95i 3d" (105 completed, 95 incomplete 3 declined)
     */
    List<RFI> findAnsweringRFIs(
            Plan plan,
            RFISurvey rfiSurvey );

}
