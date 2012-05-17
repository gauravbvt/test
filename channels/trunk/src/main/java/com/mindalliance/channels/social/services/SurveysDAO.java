package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.social.model.rfi.AnswerSet;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFIForward;
import com.mindalliance.channels.social.model.rfi.RFISurvey;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * Get count of optional questions in an rfi.
     *
     * @param rfi an RFI
     * @return and int
     */
    int getOptionalQuestionCount( RFI rfi );

    /**
     * Get count of optional answers given in an rfi.
     *
     * @param rfi an RFI
     * @return and int
     */
    int getOptionalAnswersCount( RFI rfi );

    Map<String, Integer> findResponseMetrics( Plan plan, final RFISurvey rfiSurvey );

    /**
     * Find the RFIs in a given survey with answers.
     *
     * @param plan      a plan
     * @param rfiSurvey a survey
     * @return a string like "105c 95i 3d" (105 completed, 95 incomplete 3 declined)
     */
    List<RFI> findAnsweringRFIs(
            Plan plan,
            RFISurvey rfiSurvey );


    /**
     * Gives a value between 0 and 100 representing percent of required questions answered.
     *
     * @param rfi an rfi
     * @return an int
     */
    int getPercentCompletion( RFI rfi );

    /**
     * Save answers and delete those marked for removal. Save answerSet.
     *
     * @param answerSet an answer set
     */
    void saveAnswerSet( AnswerSet answerSet );

    /**
     * Whether an rfi is incomplete and late.
     *
     * @param rfi          an rfi
     * @param queryService a query service
     * @param analyst      an analyst
     * @return a boolean
     */
    boolean isOverdue( RFI rfi, QueryService queryService, Analyst analyst );

    /**
     * Process all answers to a question in a survey.
     *
     * @param plan             a plan
     * @param rfiSurvey        a survey
     * @param question         a question
     * @param sharedOnly       a boolean - only answers that are shared vs. all answers
     * @param excludedUsername a string
     * @return a map - text of answer => list of usernames who gave it
     */
    Map<String, Set<String>> processAnswers(
            Plan plan,
            RFISurvey rfiSurvey,
            Question question,
            boolean sharedOnly,
            String excludedUsername );

    /**
     * List answerable questions in a survey.
     *
     * @param rfiSurvey a survey
     * @return a list of questions
     */
    List<Question> listAnswerableQuestions( RFISurvey rfiSurvey );

    /**
     * Calculates the percentage (e.g. 50) of required questions answered.
     *
     * @param rfi an RFI
     * @return a long
     */
    long getPercentRequiredQuestionsAnswered( RFI rfi );

    /**
     * List incomplete but active RFIs in a plan.
     *
     * @param planService a plan service
     * @param analyst     analyst
     * @return a list of RFIs
     */
    List<RFI> listIncompleteActiveRFIs( PlanService planService, Analyst analyst );

    /**
     * Find all completed RFIs in a survey.
     *
     * @param plan a plan
     * @param rfiSurvey a survey
     * @return a list of RFIs
     */
    List<RFI> findAllCompletedRFIs( Plan plan, RFISurvey rfiSurvey );

    /**
     * Find all completed RFIs in a survey.
     *
     * @param plan a plan
     * @param rfiSurvey a survey
     * @return a list of RFIs
     */
    List<RFI> findAllIncompleteRFIs( Plan plan, RFISurvey rfiSurvey );

    /**
     * Find all declined RFIs in a survey.
     *
     * @param plan a plan
     * @param rfiSurvey a survey
     * @return a list of RFIs
     */
    List<RFI> findAllDeclinedRFIs( Plan plan, RFISurvey rfiSurvey );

    /**
     * Find all RFI forwardings in a survey.
     *
     * @param plan a plan
     * @param rfiSurvey a survey
     * @return a list of rfi forwards
     */
    List<RFIForward> findAllRFIForwards( Plan plan, RFISurvey rfiSurvey );

    /**
     * A user forwards and RFI to a list of valid email addresses.
     *
     * @param plan        a plan
     * @param user        a user
     * @param rfi         an rfi
     * @param forwardedTo a list of strings
     * @param message     a string
     * @return a list of string (the new emails the RFI has been forwarded to
     */
    List<String> forwardRFI( Plan plan, ChannelsUser user, RFI rfi, List<String> forwardedTo, String message );
}
