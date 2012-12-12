package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.services.QuestionService;
import com.mindalliance.channels.social.services.QuestionnaireService;
import com.mindalliance.channels.social.services.RFISurveyService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 2:22 PM
 */
@Repository
public class QuestionnaireServiceImpl extends GenericSqlServiceImpl<Questionnaire, Long> implements QuestionnaireService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RFISurveyService rfiSurveyService;

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<Questionnaire> select(
            PlanCommunity planCommunity,
            String about,
            Questionnaire.Status status,
            boolean includeRemediation ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        if ( about != null && !about.isEmpty() ) {
            criteria.add( Restrictions.eq( "about", about ) );
        }
        if ( status != null ) {
            criteria.add( Restrictions.eq( "status", status ) );
        }
        if ( !includeRemediation ) {
            criteria.add( Restrictions.isNull( "issueKind" ) );
        }
        criteria.addOrder( Order.desc( "created" ) );
        return (List<Questionnaire>) criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<Questionnaire> findApplicableQuestionnaires( PlanCommunity planCommunity, ModelObject modelObject ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "about", modelObject.getClassLabel() ) );
        criteria.add( Restrictions.eq( "status", Questionnaire.Status.ACTIVE ) );
        criteria.add( Restrictions.isNull( "remediatedModelObjectRefString" ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<Questionnaire>) criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public Questionnaire findRemediationQuestionnaire( PlanCommunity planCommunity, Issue issue ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "about", Questionnaire.makeRemediationAbout( issue ) ) );
        criteria.add( Restrictions.eq( "issueKind", issue.getKind() ) );
        criteria.add( Restrictions.eq( "remediatedModelObjectRefString",
                new ModelObjectRef( issue.getAbout() ).asString() ) );
        criteria.add( Restrictions.eq( "status", Questionnaire.Status.ACTIVE ) );
        List<Questionnaire> result = (List<Questionnaire>) criteria.list();
        return result.isEmpty() ? null : result.get( 0 );
    }

    @Override
    @Transactional
    public void deleteIfNotUsed( PlanCommunity planCommunity, Questionnaire questionnaire ) {
        if ( rfiSurveyService.findSurveys( planCommunity, questionnaire ).isEmpty() ) {
            delete(  questionnaire );
        }
    }

    @Transactional
    public void delete( Questionnaire questionnaire ) {
        for ( Question question : questionnaire.getQuestions() ) {
            questionService.delete( question );
        }
        super.delete( questionnaire );
    }

}
