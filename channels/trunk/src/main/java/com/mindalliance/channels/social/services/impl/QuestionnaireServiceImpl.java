package com.mindalliance.channels.social.services.impl;

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
    public List<Questionnaire> select( Plan plan, String typeName, Questionnaire.Status status ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        if ( typeName != null && !typeName.isEmpty() ) {
            criteria.add( Restrictions.eq( "about",  typeName ) );
        }
        if ( status != null ) {
            criteria.add( Restrictions.eq( "status",  status ) );
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

}
