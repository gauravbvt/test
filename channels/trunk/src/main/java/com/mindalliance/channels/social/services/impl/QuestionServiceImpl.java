package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.services.QuestionService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/6/12
 * Time: 9:42 AM
 */
@Repository
public class QuestionServiceImpl extends GenericSqlServiceImpl<Question, Long> implements QuestionService {

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional
    public List<Question> listQuestions( Questionnaire questionnaire ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "questionnaire", questionnaire ) );
        criteria.addOrder( Order.asc( "index" ) );
        return (List<Question>) criteria.list();

    }

    @Override
    @Transactional
    public void moveUp( Question question ) {
        int index = question.getIndex();
        if ( index > 0 ) {
            for ( Question q : listQuestions( question.getQuestionnaire() ) ) {
                if ( q.getIndex() == index - 1 ) {
                    q.setIndex( index );
                    save( q );
                    break;
                }
            }
            question.setIndex( index - 1 );
            save( question );
        }
    }

    @Override
    @Transactional
    public void moveDown( Question question ) {
        int index = question.getIndex();
        List<Question> questions = listQuestions( question.getQuestionnaire() );
        if ( index < questions.size() - 1 ) {
            for ( Question q : questions ) {
                if ( q.getIndex() == index + 1 ) {
                    q.setIndex( index );
                    save( q );
                    break;
                }
            }
            question.setIndex( index + 1 );
            save( question );
        }
    }

    @Override
    @Transactional
    public Question addNewQuestion( ChannelsUser user, Questionnaire questionnaire ) {
        int index = listQuestions( questionnaire ).size();
        Question question = new Question( user.getUsername(), questionnaire );
        question.setIndex( index );
        save( question );
        return question;
    }


    @Override
    @Transactional
    public void moveUpAnswerChoice( Question question, String choice ) {
        List<String> choices = question.getAnswerChoices();
        question.setAnswerChoices( ChannelsUtils.moveUp( choice, choices ) );
        save( question );
    }

    @Override
    @Transactional
    public void moveDownAnswerChoice( Question question, String choice ) {
        List<String> choices = question.getAnswerChoices();
        question.setAnswerChoices( ChannelsUtils.moveDown( choice, choices ) );
        save( question );
    }

    @Override
    @Transactional
    public void deleteAnswerChoice( Question question, String choice ) {
        List<String> choices = question.getAnswerChoices();
        choices.remove( choice );
        question.setAnswerChoices( choices );
        save( question );
    }

    @Override
    public void addAnswerChoice( Question question, String choice ) {
        List<String> choices = question.getAnswerChoices();
        if ( !choices.contains( choice ) ) choices.add( choice );
        question.setAnswerChoices( choices );
        save( question );
    }
}
