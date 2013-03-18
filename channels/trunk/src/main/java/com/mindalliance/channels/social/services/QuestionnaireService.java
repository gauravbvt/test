package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.rfi.Questionnaire;

import java.util.List;

/**
 * Questionnaire service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 2:19 PM
 */
public interface QuestionnaireService extends GenericSqlService<Questionnaire, Long> {
    /**
     * Find all questionnaires of given (or not) status about a type of model objects or about any type.
     *
     *
     * @param communityService a plan community service
     * @param about a string, or null for any
     * @param status   the status (null, Active, or Inactive)
     * @param includeGenerated include generated questionnaires
     * @return a list of questionnaires
     */
    List<Questionnaire> select( CommunityService communityService, String about, Questionnaire.Status status, boolean includeGenerated );

    /**
     * Find all questionnaires that can be used in surveys about a model object.
     * Exclude issue remediation questionnaires.
     *
     * @param communityService        a plan community service
     * @param modelObject a model object
     * @return a list of questionnaires
     */
    List<Questionnaire> findApplicableQuestionnaires( CommunityService communityService, ModelObject modelObject );

    /**
     * Find the remediation questionnaire for an issue.
     *
     * @param communityService  a plan community service
     * @param issue an issue
     * @return a questionnaire or null
     */
    Questionnaire findRemediationQuestionnaire( CommunityService communityService, Issue issue );

    /**
     * Whether a questionnaire has been used.
     * @param communityService a community service
     * @param questionnaire a questionnaire
     * @return whether used
     */
    boolean isUsed( CommunityService communityService, Questionnaire questionnaire );

    /**
     * Delete a given questionnaire if inactive and never used.
     * @param communityService a community service
     * @param questionnaire a questionnaire
     * @return whether deleted
     */
    boolean deleteIfAllowed( CommunityService communityService, Questionnaire questionnaire );
}
