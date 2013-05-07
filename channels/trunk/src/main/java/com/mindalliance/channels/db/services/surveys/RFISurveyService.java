package com.mindalliance.channels.db.services.surveys;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.db.data.surveys.Questionnaire;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.db.services.DataService;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/13
 * Time: 3:25 PM
 */
public interface RFISurveyService extends DataService<RFISurvey> {

    /**
     * List all active surveys in a plan.
     *
     * @param communityService    a plan community service
     * @return a list of surveys
     */
    List<RFISurvey> listActive( CommunityService communityService );

    /**
     * Get all surveys for a plan, possibly restricting to open ones about a given what the questionnaire is about.
     *
     * @param communityService    a plan community service
     * @param onlyOpen a boolean
     * @param about    a string
     * @return a list of surveys
     */
    List<RFISurvey> select( CommunityService communityService, boolean onlyOpen, String about );

    /**
     * Get all surveys on a model object.
     *
     * @param communityService    a plan community service
     * @param modelObject a model object
     * @return a list of surveys
     */
    List<RFISurvey> select( CommunityService communityService, ModelObject modelObject );

    /**
     * Creates (i.e. launches) an RFISurvey on a model object using a questionnaire.
     *
     * @param communityService    a plan community service
     * @param username      a string
     * @param questionnaire a questionnaire
     * @param modelObject   a model object
     * @return a survey
     */
    RFISurvey launch( CommunityService communityService, String username, Questionnaire questionnaire, ModelObject modelObject );

    /**
     * Whether remediation of an issue is already surveyed (closed or ongoing).
     *
     * @param communityService a plan community service
     * @param issue        an issue
     * @return a rfi survey
     */
    RFISurvey findRemediationSurvey( CommunityService communityService, final Issue issue );

    /**
     * Find surveys using a given questionnaire.
     *
     * @param communityService    a plan community service
     * @param questionnaire a questionnaire
     * @return a list of surveys
     */
    List<RFISurvey> findSurveys( CommunityService communityService, Questionnaire questionnaire );

    /**
     * Toggle the activation status of a survey.
     *
     * @param rfiSurvey a survey
     */
    void toggleActivation( RFISurvey rfiSurvey );
}
