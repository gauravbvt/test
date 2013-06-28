package com.mindalliance.channels.db.services.surveys;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.Questionnaire;
import com.mindalliance.channels.db.data.surveys.RFI;
import com.mindalliance.channels.db.data.surveys.RFIForward;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.db.data.users.UserRecord;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/13
 * Time: 3:20 PM
 */
public interface SurveysDAO {
    /**
     * Get or create a questionnaire and survey for an issue.
     *
     * @param username         a string
     * @param communityService a plan community service
     * @param issue            an issue
     * @return a survey
     */
    RFISurvey getOrCreateRemediationSurvey(
            String username,
            CommunityService communityService,
            Issue issue );

    /**
     * Count a user's unanswered, active RFIs in a given plan.
     *
     * @param communityService a plan community service
     * @param user             a user
     * @return an int
     */
    int countUnanswered( CommunityService communityService, ChannelsUser user );

    /**
     * Count a user's partially answered, active RFIs in a given plan.
     *
     * @param communityService a plan community service
     * @param user             a user
     * @return an int
     */
    int countIncomplete( CommunityService communityService, ChannelsUser user );

    /**
     * Count a user's overdue, active RFIs in for a given plan.
     *
     * @param communityService a plan community service
     * @param user             a user
     * @return an int
     */
    int countLate( CommunityService communityService, ChannelsUser user );

    /**
     * Find a user's incomplete, active RFIs in a plan.
     *
     * @param communityService a plan community service
     * @param user             a user
     * @return a list of RFIs
     */
    List<RFI> findIncompleteRFIs( CommunityService communityService, ChannelsUser user );

    /**
     * Find a user's completed, active RFIs in a plan.
     *
     * @param communityService a plan community service
     * @param user             a user
     * @return a list of RFIs
     */
    List<RFI> findCompletedRFIs( CommunityService communityService, ChannelsUser user );

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
     * @param communityService a plan community service
     * @param user             a user
     * @return a list of RFIs
     */
    List<RFI> findDeclinedRFIs( CommunityService communityService, ChannelsUser user );

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

    Map<String, Integer> findResponseMetrics( CommunityService communityService, final RFISurvey rfiSurvey );

    /**
     * Find the RFIs in a given survey with answers.
     *
     * @param communityService a plan community service
     * @param rfiSurvey        a survey
     * @return a string like "105c 95i 3d" (105 completed, 95 incomplete 3 declined)
     */
    List<RFI> findAnsweringRFIs(
            CommunityService communityService,
            RFISurvey rfiSurvey );


    /**
     * Gives a value between 0 and 100 representing percent of required questions answered.
     *
     * @param rfi an rfi
     * @return an int
     */
    int getPercentCompletion( RFI rfi );

    /**
     * Whether an rfi is incomplete and late.
     *
     * @param communityService a plan community service
     * @return a boolean
     */
    boolean isOverdue( CommunityService communityService, RFI rfi );

    /**
     * Process all answers to a question in a survey.
     *
     * @param communityService a plan community service
     * @param rfiSurvey        a survey
     * @param question         a question
     * @param sharedOnly       a boolean - only answers that are shared vs. all answers
     * @param excludedUsername a string
     * @return a map - text of answer => list of usernames who gave it
     */
    Map<String, Set<String>> processAnswers(
            CommunityService communityService,
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
     * @param communityService a plan community service
     * @return a list of RFIs
     */
    List<RFI> listIncompleteActiveRFIs( CommunityService communityService );

    /**
     * Find all completed RFIs in a survey.
     *
     * @param communityService a plan community service
     * @param rfiSurvey        a survey
     * @return a list of RFIs
     */
    List<RFI> findAllCompletedRFIs( CommunityService communityService, RFISurvey rfiSurvey );

    /**
     * Find all completed RFIs in a survey.
     *
     * @param communityService a plan community service
     * @param rfiSurvey        a survey
     * @return a list of RFIs
     */
    List<RFI> findAllIncompleteRFIs( CommunityService communityService, RFISurvey rfiSurvey );

    /**
     * Find all declined RFIs in a survey.
     *
     * @param communityService a plan community service
     * @param rfiSurvey        a survey
     * @return a list of RFIs
     */
    List<RFI> findAllDeclinedRFIs( CommunityService communityService, RFISurvey rfiSurvey );

    /**
     * Find all RFI forwardings in a survey.
     *
     * @param communityService a plan community service
     * @param rfiSurvey        a survey
     * @return a list of rfi forwards
     */
    List<RFIForward> findAllRFIForwards( CommunityService communityService, RFISurvey rfiSurvey );

    /**
     * A user forwards and RFI to a list of valid email addresses.
     * Must be called from a context where the plan is the current user's plan.
     *
     * @param communityService a plan community service
     * @param user             a user
     * @param rfi              an rfi
     * @param forwardedTo      a list of strings
     * @param message          a string
     * @return a list of string (the new emails the RFI has been forwarded to
     */
    List<String> forwardRFI( CommunityService communityService, ChannelsUser user, RFI rfi, List<String> forwardedTo, String message );

    /**
     * Find the user who forwarded an RFI.
     *
     * @param rfiForward an RFI forwarding
     * @return a user info
     */
    UserRecord getForwarder( RFIForward rfiForward );

    /**
     * Find all forwards to the RFI's target user of the RFI's survey.
     *
     * @param rfi an RFI
     * @return a list of RFI forwards
     */
    List<RFIForward> getForwardingsOf( RFI rfi );

    /**
     * Make url to point to an rfi.
     *
     * @param communityService a community service
     * @param rfi              an RFI
     * @param surveyedUser     a Channels user
     * @return a string
     */
    String makeURL( CommunityService communityService, RFI rfi, ChannelsUser surveyedUser );

    /**
     * Find a questionnaire given its uid.
     *
     * @param questionnaireUid a string
     * @return a questionnaire
     */
    Questionnaire findQuestionnaire( String questionnaireUid );

    /**
     * Find a survey by its UID.
     * @param rfiSurveyUid  a string
     * @return a survey
     */
    RFISurvey findRFISurvey( String rfiSurveyUid );
}

