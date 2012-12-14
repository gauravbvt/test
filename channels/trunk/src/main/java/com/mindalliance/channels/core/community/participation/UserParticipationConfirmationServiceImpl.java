package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * User plan participation confirmation service implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/23/12
 * Time: 12:07 PM
 */
public class UserParticipationConfirmationServiceImpl
        extends GenericSqlServiceImpl<UserParticipationConfirmation, Long>
        implements UserParticipationConfirmationService {

    @Autowired
    private UserParticipationService userParticipationService;

    @Autowired
    private ParticipationManager participationManager;

    public UserParticipationConfirmationServiceImpl() {
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<UserParticipationConfirmation> getParticipationConfirmations(
            PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<UserParticipationConfirmation>) criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<UserParticipationConfirmation> getParticipationConfirmations(
            UserParticipation userParticipation ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "userParticipation", userParticipation ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<UserParticipationConfirmation>) criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<UserParticipationConfirmation> getParticipationConfirmations(
            Agent supervisor,
            PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "supervisorId", supervisor.getActorId() ) );
        if ( supervisor.getOrganizationParticipation() == null )
            criteria.add( Restrictions.isNull( "organizationParticipation" ) );
        else
            criteria.add( Restrictions.eq( "organizationParticipation", supervisor.getOrganizationParticipation() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<UserParticipationConfirmation>) criteria.list();
    }

    @Override
    @Transactional
    public void addParticipationConfirmation( UserParticipation userParticipation,
                                              Agent supervisor,
                                              ChannelsUser user ) {
        UserParticipationConfirmation validation = new UserParticipationConfirmation(
                userParticipation,
                supervisor,
                user.getUsername() );
        save( validation );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional
    public void removeParticipationConfirmation( UserParticipation userParticipation, Agent supervisor ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "userParticipation", userParticipation ) );
        criteria.add( Restrictions.eq( "supervisorId", supervisor.getActorId() ) );
        if ( supervisor.getOrganizationParticipation() == null )
            criteria.add( Restrictions.isNull( "organizationParticipation" ) );
        else
            criteria.add( Restrictions.eq( "organizationParticipation", supervisor.getOrganizationParticipation() ) );
        for ( UserParticipationConfirmation validation : (List<UserParticipationConfirmation>) criteria.list() ) {
            delete( validation );
        }
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isConfirmedBy( UserParticipation userParticipation, Agent supervisor ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "userParticipation", userParticipation ) );
        criteria.add( Restrictions.eq( "supervisorId", supervisor.getActorId() ) );
        if ( supervisor.getOrganizationParticipation() == null )
            criteria.add( Restrictions.isNull( "organizationParticipation" ) );
        else
            criteria.add( Restrictions.eq( "organizationParticipation", supervisor.getOrganizationParticipation() ) );
        return !criteria.list().isEmpty();
    }

    @Override
    @Transactional
    public void deleteConfirmations( UserParticipation participation ) {
        for ( UserParticipationConfirmation validation : getParticipationConfirmations( participation ) ) {
            delete( validation );
        }
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isConfirmedByAllSupervisors(
            UserParticipation userParticipation,
            final PlanCommunity planCommunity ) {
        // Find all supervisors for participation's agent
        Agent agent = userParticipation.getAgent( planCommunity );
        if ( agent == null ) return false;
        List<Agent> supervisors = participationManager.findAllSupervisorsOf( agent, planCommunity );
        boolean validatedByAll = true;
        final List<UserParticipationConfirmation> validations = getParticipationConfirmations( userParticipation );
        // Verify that each supervisor (some user participating as that supervisor) has
        // validated the participation.
        Iterator<Agent> iter = supervisors.iterator();
        while ( validatedByAll && iter.hasNext() ) {
            final Agent supervisor = iter.next();
            validatedByAll = CollectionUtils.exists(
                    validations,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (UserParticipationConfirmation) object )
                                    .getSupervisor( planCommunity ).equals( supervisor );
                        }
                    }
            );
        }
        return validatedByAll;
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isConfirmationByUserRequired(
            final UserParticipation userParticipation,
            ChannelsUser user,
            PlanCommunity planCommunity ) {
        if ( !userParticipation.isSupervised( planCommunity ) ) return false;
        // Find all supervisors user is assigned to that supervise the participation.
        if ( userParticipation.getAgent( planCommunity ) == null ) return false;
        List<Agent> supervisors = userParticipationService.listSupervisorsUserParticipatesAs(
                userParticipation,
                user,
                planCommunity );
        // Verify that the participation is not confirmed by all the supervisors the user participates as.
        return CollectionUtils.exists(
                supervisors,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Agent supervisor = (Agent) object;
                        return !isConfirmedBy( userParticipation, supervisor );
                    }
                }
        );
    }

}
