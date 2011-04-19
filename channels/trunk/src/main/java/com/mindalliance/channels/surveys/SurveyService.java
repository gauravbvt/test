package com.mindalliance.channels.surveys;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.social.SurveyResponse;

import java.util.List;

/**
 * A survey service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 21, 2009
 * Time: 11:00:51 AM
 */
public interface SurveyService {

    /**
     * Create a new survey of a given type in current plan on an identifiable, or get current, not closed one.
     *
     * @param type         a survey type
     * @param identifiable an identifiable
     * @param plan         a plan
     * @return a survey
     * @throws SurveyException if fails
     */
    Survey getOrCreateSurvey( Survey.Type type, Identifiable identifiable, Plan plan ) throws SurveyException;

    /**
     * Whether one or more surveys of a given type, created, launched or closed, is already associated with an identifiable.
     *
     * @param type         a survey type
     * @param identifiable an identifiable
     * @return a boolean
     */
    boolean isSurveyed( Survey.Type type, Identifiable identifiable );

    /**
     * Add contacts to a survey.
     *
     * @param survey    a survey
     * @param usernames a list of user names
     * @param plan      a plan
     * @throws SurveyException if fails
     */
    void inviteContacts( Survey survey, List<String> usernames, Plan plan ) throws SurveyException;

    /**
     * Whether the survey can still be associated with an existing issue.
     *
     * @param survey a survey
     * @return a boolean
     */
    boolean isRelevant( Survey survey );

    /**
     * Find current issue the survey is about.
     *
     * @param survey a survey
     * @return an identifiable or null
     */
    Identifiable findIdentifiable( Survey survey );      // todo - needed?

    /**
     * Delete not-yet-launched survey.
     *
     * @param survey a survey
     * @throws SurveyException if the survey can not be deleted
     */
    void deleteSurvey( Survey survey ) throws SurveyException;

    /**
     * Launch the survey.
     *
     * @param survey a survey
     * @param plan   a plan
     * @throws SurveyException if fails
     */
    void launchSurvey( Survey survey, Plan plan ) throws SurveyException;

    /**
     * Close the survey.
     *
     * @param survey a survey
     * @param plan   a plan
     * @throws SurveyException if fails
     */
    void closeSurvey( Survey survey, Plan plan ) throws SurveyException;

    /**
     * Get summary and access data about a survey.
     *
     * @param survey a survey
     * @param plan   a plan
     * @return survey data
     * @throws SurveyException if fails
     */
    SurveyData getSurveyData( Survey survey, Plan plan ) throws SurveyException;

    /**
     * Return all known surveys for the current plan.
     *
     * @return a list of surveys
     */
    List<Survey> getSurveys();

    /**
     * Get service API key.
     *
     * @param plan a plan
     * @return a string
     */
    String getApiKey( Plan plan );

    /**
     * Get service user key.
     *
     * @param plan a plan
     * @return a string
     */
    String getUserKey( Plan plan );

    /**
     * Get service template key.
     *
     * @param plan a plan
     * @return a string
     */
    String getTemplate( Plan plan );

    /**
     * Get service email address.
     *
     * @param plan a plan
     * @return a string
     */
    String getDefaultEmailAddress( Plan plan );

    /**
     * Get user service.
     *
     * @return a user service
     */
    UserService getUserService();

    /**
     * Get analyst.
     *
     * @return an analyst
     */
    Analyst getAnalyst();

    /**
     * Find a user's survey responses, including TBD (non-existent) responses.
     * @param user  a user
     * @param maxNumber  an int
     * @param showCompleted a boolean
     * @return  a list of survey responses
     * @throws SurveyException if fails
     */
    List<SurveyResponse> findSurveysResponses( User user, int maxNumber, boolean showCompleted ) throws SurveyException;
}
