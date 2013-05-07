package com.mindalliance.channels.db.services.surveys;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.Questionnaire;
import com.mindalliance.channels.db.services.DataService;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/13
 * Time: 3:16 PM
 */
public interface QuestionnaireService extends DataService<Questionnaire> {
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
    List<Questionnaire> select( CommunityService communityService,
                                String about,
                                Questionnaire.Status status,
                                boolean includeGenerated );

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
     * Delete a given questionnaire if inactive and never used.
     * @param communityService a community service
     * @param questionnaire a questionnaire
     * @return whether deleted
     */
    boolean deleteIfAllowed( CommunityService communityService, Questionnaire questionnaire );

    /**
     * List all questions in a questionnaire.
     *
     * @param questionnaireUid a questionnaire uid
     * @return a list of questions
     */
    List<Question> listQuestions( String questionnaireUid );

    /**
     * Move a question up the list.
     *
     * @param question a question
     */
    void moveUp( Question question );

    /**
     * Move a question down the list.
     *
     * @param question a question
     */
    void moveDown( Question question );

    /**
     * Add a new question at the end.
     *
     * @param user          a user
     * @param questionnaire a questionnaire
     * @return a question
     */
    Question addNewQuestion( ChannelsUser user, Questionnaire questionnaire );

    /**
     * Move a choice in a multiple choice answer to a question up.
     *
     * @param question a question
     * @param choice   a string
     */
    void moveUpAnswerChoice( Question question, String choice );

    /**
     * Move a choice in a multiple choice answer to a question down.
     *
     * @param question a question
     * @param choice   a string
     */
    void moveDownAnswerChoice( Question question, String choice );

    /**
     * Delete a choice from a multiple choice answer to a question.
     *
     * @param question a question
     * @param choice   a string
     */
    void deleteAnswerChoice( Question question, String choice );

    /**
     * Add a choice to a multiple choice answer to a question.
     *
     * @param question a question
     * @param choice   a string
     */
    void addAnswerChoice( Question question, String choice );

    /**
     * Delete all questions in questionnaire if never answered.
     *
     * @param questionnaire a questionnaire
     * @return whether successful
     */
    boolean deleteQuestionsIfUnanswered( Questionnaire questionnaire );

    void deleteQuestion( Question question );

    void saveQuestion( Question question );

    Question refreshQuestion( Question question );

    void updateQuestion( Question question );
}
