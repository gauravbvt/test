package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.services.QuestionnaireService;
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
 * Date: 2/16/12
 * Time: 2:22 PM
 */
@Repository
public class QuestionnaireServiceImpl extends GenericSqlServiceImpl<Questionnaire, Long> implements QuestionnaireService {

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<Questionnaire> select(
            Plan plan,
            String about,
            Questionnaire.Status status,
            boolean includeRemediation ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
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
    public List<Questionnaire> findApplicableQuestionnaires( Plan plan, ModelObject modelObject ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "about", modelObject.getClassLabel() ) );
        criteria.add( Restrictions.eq( "status", Questionnaire.Status.ACTIVE ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<Questionnaire>) criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public Questionnaire findRemediationQuestionnaire( Plan plan, Issue issue ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "about", Questionnaire.makeRemediationAbout( issue ) ) );
        criteria.add( Restrictions.eq( "issueKind", issue.getKind() ) );
        criteria.add( Restrictions.eq( "remediatedModelObjectRefString",
                new ModelObjectRef( issue.getAbout() ).asString() ) );
        criteria.add( Restrictions.eq( "status", Questionnaire.Status.ACTIVE ) );
        List<Questionnaire> result = (List<Questionnaire>) criteria.list();
        return result.isEmpty() ? null : result.get( 0 );
    }

}
