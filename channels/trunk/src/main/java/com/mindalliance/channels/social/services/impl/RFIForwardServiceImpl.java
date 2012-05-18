package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFIForward;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.RFIForwardService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the RFIForward service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/12
 * Time: 2:14 PM
 */
@Repository
public class RFIForwardServiceImpl extends GenericSqlServiceImpl<RFIForward, Long> implements RFIForwardService {

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<String> findForwarderUsernames( RFI rfi ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "rfi", rfi ) );
        List<RFIForward> forwards = (List<RFIForward>) criteria.list();
        Set<String> usernames = new HashSet<String>();
        for ( RFIForward forward : forwards ) {
            usernames.add( forward.getUsername() );
        }
        return new ArrayList<String>( usernames );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<String> findForwardedTo( RFI rfi ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "rfi", rfi ) );
        List<RFIForward> forwards = (List<RFIForward>) criteria.list();
        Set<String> emails = new HashSet<String>();
        for ( RFIForward forward : forwards ) {
            emails.add( forward.getForwardToEmail() );
        }
        return new ArrayList<String>( emails );
    }


    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFIForward> select( Plan plan, final RFISurvey rfiSurvey ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        return (List<RFIForward>) CollectionUtils.select(
                criteria.list(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        RFIForward forward = (RFIForward) object;
                        return forward.getRfi().getRfiSurvey().equals( rfiSurvey );
                    }
                }
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<RFIForward> findForwardsTo( String surveyedUsername, final RFISurvey rfiSurvey ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "surveyedUsername", surveyedUsername ) );
        return (List<RFIForward>) CollectionUtils.select(
                criteria.list(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (RFIForward) object ).getRfi().getRfiSurvey().equals( rfiSurvey );
                    }
                }
        );
    }
}
