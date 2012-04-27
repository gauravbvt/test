package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.RFISurveyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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
 * Date: 3/7/12
 * Time: 11:18 AM
 */
@Repository
public class RFISurveyServiceImpl extends GenericSqlServiceImpl<RFISurvey, Long> implements RFISurveyService {

    public RFISurveyServiceImpl() {
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFISurvey> select( Plan plan, boolean onlyOpen, String about ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        if ( onlyOpen ) {
            criteria.add( Restrictions.eq( "closed", false ) );
        }
        if ( about != null && !about.isEmpty() ) {
            criteria.add( Restrictions.eq( "about", about ) );
        }
        criteria.addOrder( Order.desc( "created" ) );
        return (List<RFISurvey>) criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFISurvey> select( Plan plan, ModelObject modelObject ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "moRef", new ModelObjectRef( modelObject ).asString() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<RFISurvey>) criteria.list();
    }

    @Override
    @Transactional
    public RFISurvey launch( Plan plan, String username, Questionnaire questionnaire, ModelObject modelObject ) {
        RFISurvey rfiSurvey = new RFISurvey(
                plan,
                username
        );
        if ( questionnaire.isActive() ) {
            rfiSurvey.setQuestionnaire( questionnaire );
            rfiSurvey.setMoRef( modelObject );
            save( rfiSurvey );
            return rfiSurvey;
        } else {
            return null;
        }
    }

    @Override
    @Transactional( readOnly = true )
    public RFISurvey findRemediationSurvey( Plan plan, final Issue issue, final QueryService queryService ) {
        List<RFISurvey> surveys = select( plan, false, Questionnaire.makeRemediationAbout( issue ) );
        return (RFISurvey)CollectionUtils.find(
                surveys,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFISurvey survey = (RFISurvey)object;
                        return survey.getQuestionnaire().isAboutRemediation( issue );
                    }
                }
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFISurvey> findSurveys( Plan plan, Questionnaire questionnaire ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "questionnaire", questionnaire ) );
        return (List<RFISurvey>)criteria.list();
    }

    @Override
    public void toggleActivation( RFISurvey rfiSurvey ) {
        rfiSurvey.setClosed( !rfiSurvey.isClosed() );
        save( rfiSurvey );
    }
}
