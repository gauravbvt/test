package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.services.QuestionnaireService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

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
    public List<Questionnaire> select( String typeName, Questionnaire.Status status ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria(  getPersistentClass() );
        if ( typeName != null && !typeName.isEmpty() ) {
            criteria.add( Restrictions.eq( "typeName",  typeName ) );
        }
        if ( status != null ) {
            criteria.add( Restrictions.eq( "status",  status ) );
        }
        criteria.addOrder( Order.desc( "created" ) );
        return (List<Questionnaire>) criteria.list();

    }
}
