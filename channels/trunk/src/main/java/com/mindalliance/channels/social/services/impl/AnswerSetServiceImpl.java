package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.rfi.AnswerSet;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.AnswerSetService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the answer service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/11/12
 * Time: 10:42 AM
 */
@Repository
public class AnswerSetServiceImpl extends GenericSqlServiceImpl<AnswerSet, Long> implements AnswerSetService {

    @Override
    @Transactional( readOnly = true )
    public int getAnswerCount( Question question ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "question", question ) );
        return criteria.list().size();
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isCompleted( final RFI rfi ) {
        RFISurvey survey = rfi.getRfiSurvey();
        List<Question> questions = survey.getQuestionnaire().getQuestions();
        // No required, unanswered questions
        return !CollectionUtils.exists(
                questions,
                // unanswered, required question
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Question question = (Question) object;
                        if ( question.isAnswerRequired() ) {
                            AnswerSet answerSet = findAnswers( rfi, question );
                            return answerSet == null || answerSet.isEmpty();
                        } else {
                            return false;
                        }

                    }
                }
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public AnswerSet findAnswers( RFI rfi, Question question ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "rfi", rfi ) );
        criteria.add( Restrictions.eq( "question", question ) );
        List<AnswerSet> result =  criteria.list();
        return result.isEmpty()
                ? null
                : result.get( 0 );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public boolean isIncomplete( RFI rfi ) {
        return !isCompleted( rfi );
    }

}
