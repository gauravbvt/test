package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;

import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 2:19 PM
 */
public interface RFIService extends GenericSqlService<RFI, Long> {

    /**
     * List all RFIs in a given survey.
     *
     * @param plan      a plan
     * @param rfiSurvey a survey
     * @return a list of rfis
     */
    List<RFI> select( Plan plan, RFISurvey rfiSurvey );

    /**
     * Get the number of RFIs based on a given questionnaire.
     *
     * @param plan          a plan
     * @param questionnaire a questionnaire
     * @return an int
     */
    int getRFICount( Plan plan, Questionnaire questionnaire );

    /**
     * Add or update an RFI.
     *
     * @param plan         a plan
     * @param username     who adds or updates
     * @param rfiSurvey    a survey
     * @param userInfo     user info
     * @param organization an organization
     * @param title        a string
     * @param role         a role
     * @param deadlineDate a date or null if no deadline
     */
    void makeOrUpdateRFI(
            Plan plan,
            String username,
            RFISurvey rfiSurvey,
            ChannelsUserInfo userInfo,
            Organization organization,
            String title,
            Role role,
            Date deadlineDate );

    /**
     * Nag a user to complete an RFI.
     *
     * @param plan         a plan
     * @param username     who nags
     * @param rfiSurvey    a survey
     * @param userInfo     user info
     */
    void nag(
            Plan plan,
            String username,
            RFISurvey rfiSurvey,
            ChannelsUserInfo userInfo );

    /**
     * Find matching RFI RFI.
     *
     * @param plan             a plan
     * @param surveyedUsername who is being surveyed
     * @param rfiSurvey        a survey
     */
    RFI find(
            Plan plan,
            RFISurvey rfiSurvey,
            String surveyedUsername );

    /**
     * Find the usernames of all participants in a survey.
     *
     * @param plan         a plan
     * @param rfiSurvey a survey
     * @return a list of strings
     */
    List<String> findParticipants( Plan plan, RFISurvey rfiSurvey );

    /**
     * Find the RFIs in a given survey with answers.
     *
     * @param plan             a plan
     * @param rfiSurvey        a survey
     * @param answerSetService the answer set service
     * @return a string like "105c 95i 3d" (105 completed, 95 incomplete 3 declined)
     */
    List<RFI> findAnsweringRFIs(
            Plan plan,
            RFISurvey rfiSurvey,
            AnswerSetService answerSetService );

}
