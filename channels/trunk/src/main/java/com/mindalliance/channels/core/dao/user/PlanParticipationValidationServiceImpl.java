package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/23/12
 * Time: 12:07 PM
 */
public class PlanParticipationValidationServiceImpl
        extends GenericSqlServiceImpl<PlanParticipationValidation, Long>
        implements PlanParticipationValidationService {

    public PlanParticipationValidationServiceImpl() {
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<PlanParticipationValidation> getParticipationValidations(
            Plan plan ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<PlanParticipationValidation>) criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<PlanParticipationValidation> getParticipationValidations(
            PlanParticipation planParticipation ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planParticipation", planParticipation ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<PlanParticipationValidation>) criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<PlanParticipationValidation> getParticipationValidations(
            Plan plan,
            Actor supervisor ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        criteria.add( Restrictions.eq( "supervisorId", supervisor.getId() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<PlanParticipationValidation>) criteria.list();
    }

    @Override
    @Transactional
    public void addParticipationValidation( PlanParticipation planParticipation,
                                            Actor supervisor,
                                            ChannelsUser user ) {
        PlanParticipationValidation validation = new PlanParticipationValidation(
                planParticipation,
                supervisor.getId(),
                user.getUsername() );
        save( validation );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional
    public void removeParticipationValidation( PlanParticipation planParticipation, Actor supervisor ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planParticipation", planParticipation ) );
        criteria.add( Restrictions.eq( "supervisorId", supervisor.getId() ) );
        for ( PlanParticipationValidation validation : (List<PlanParticipationValidation>) criteria.list() ) {
            delete( validation );
        }
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isValidatedBy( PlanParticipation planParticipation, Actor supervisor ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planParticipation", planParticipation ) );
        criteria.add( Restrictions.eq( "supervisorId", supervisor.getId() ) );
        return !criteria.list().isEmpty();
    }

    @Override
    @Transactional
    public void deleteValidations( PlanParticipation participation ) {
        for (PlanParticipationValidation validation : getParticipationValidations( participation ) ) {
            delete( validation );
        }
    }

}
