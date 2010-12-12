package com.mindalliance.channels.surveys;

import com.mindalliance.channels.model.Issue;

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
     * Create a new survey in current plan on an issue, or get current, not closed one.
     *
     * @param issue an issue
     * @return a survey
     * @throws com.mindalliance.channels.surveys.SurveyException
     *          if fails
     */
    Survey getOrCreateSurvey( Issue issue ) throws SurveyException;

    /**
     * Whether one or more surveys, created, launched or closed, is already associated with an issue.
     *
     * @param issue an issue
     * @return a boolean
     */
    boolean isSurveyed( Issue issue );

    /**
     * Add contacts to a survey.
     *
     * @param survey    a survey
     * @param usernames a list of user names
     * @throws com.mindalliance.channels.surveys.SurveyException
     *          if fails
     */
    void inviteContacts( Survey survey, List<String> usernames ) throws SurveyException;

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
     * @return an issue or null
     */
    Issue findIssue( final Survey survey );

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
     * @throws com.mindalliance.channels.surveys.SurveyException
     *          if fails
     */
    void launchSurvey( Survey survey ) throws SurveyException;

    /**
     * Close the survey.
     *
     * @param survey a survey
     * @throws com.mindalliance.channels.surveys.SurveyException
     *          if fails
     */
    void closeSurvey( Survey survey ) throws SurveyException;

    /**
     * Get summary and access data about a survey.
     *
     * @param survey a survey
     * @return survey data
     * @throws com.mindalliance.channels.surveys.SurveyException
     *          if fails
     */
    SurveyData getSurveyData( Survey survey ) throws SurveyException;

    /**
     * Return all known surveys for the current plan.
     *
     * @return a list of surveys
     */
    List<Survey> getSurveys();

    /**
     * Get service API key.
     * @return a string
     */
    String getApiKey();

    /**
     * Get service user key.
     * @return a string
     */
    String getUserKey();

    /**
     * Get service template key.
     * @return a string
     */
    String getTemplate();

    /**
     * Get service email address.
     * @return a string
     */
    String getDefaultEmailAddress();
}
