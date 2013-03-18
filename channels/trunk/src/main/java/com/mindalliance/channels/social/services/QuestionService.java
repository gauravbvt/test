package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.Questionnaire;

import java.util.List;

/**
 * Question service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/6/12
 * Time: 9:41 AM
 */
public interface QuestionService extends GenericSqlService<Question, Long> {

    /**
     * List all questions in a questionnaire.
     *
     * @param questionnaire a questionnaire
     * @return a list of questions
     */
    List<Question> listQuestions( Questionnaire questionnaire );

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
}
