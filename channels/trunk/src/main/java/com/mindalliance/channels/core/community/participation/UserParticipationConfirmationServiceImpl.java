package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

//    @Autowired
    private UserParticipationService userParticipationService;

//    @Autowired
    private ParticipationManager participationManager;

//    @Autowired
    private OrganizationParticipationService organizationParticipationService;

    public UserParticipationConfirmationServiceImpl() {
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true )
    public List<UserParticipationConfirmation> getParticipationConfirmations(
            CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
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
            CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
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
                                              ChannelsUser user,
                                              CommunityService communityService ) {
        UserParticipationConfirmation validation = new UserParticipationConfirmation(
                userParticipation,
                supervisor,
                user.getUsername() );
        save( validation );
        communityService.clearCache();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional
    public void removeParticipationConfirmation( UserParticipation userParticipation, Agent supervisor, CommunityService communityService ) {
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
        communityService.clearCache();
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
    public void deleteConfirmations( UserParticipation participation, CommunityService communityService ) {
        for ( UserParticipationConfirmation validation : getParticipationConfirmations( participation ) ) {
            delete( validation );
        }
        communityService.clearCache();
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isConfirmedByAllSupervisors(
            UserParticipation userParticipation,
            final CommunityService communityService ) {
        // Find all supervisors for participation's agent
        Agent agent = userParticipation.getAgent( communityService );
        if ( agent == null ) return false;
        List<Agent> supervisors = participationManager.findAllSupervisorsOf( agent, communityService );
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
                                    .getSupervisor( communityService ).equals( supervisor );
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
            CommunityService communityService ) {
        if ( !userParticipation.isSupervised( communityService ) ) return false;
        // Find all supervisors user is assigned to that supervise the participation.
        if ( userParticipation.getAgent( communityService ) == null ) return false;
        List<Agent> supervisors = userParticipationService.listSupervisorsUserParticipatesAs(
                userParticipation,
                user,
                communityService );
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

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipationConfirmation> listUserParticipationsConfirmedBy(
            ChannelsUser user,
            final CommunityService communityService ) {
        final List<UserParticipationConfirmation> allConfirmations = new ArrayList<UserParticipationConfirmation>(  );
            //    communityService.getUserParticipationConfirmationService().getParticipationConfirmations( communityService );
        final List<Agent> userAgents = userParticipationService.listAgentsUserParticipatesAs(
                user,
                communityService );
        // Find all plan participation confirmations made by a supervisor user participates as (= confirmed)
        return (List<UserParticipationConfirmation>) CollectionUtils.select(
                allConfirmations,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipationConfirmation confirmation = (UserParticipationConfirmation) object;
                        Agent supervisor = confirmation.getSupervisor( communityService );
                        return supervisor != null && userAgents.contains( supervisor );
                    }
                }
        );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isValid( UserParticipationConfirmation confirmation, CommunityService communityService ) {
        return confirmation != null &&
                userParticipationService.isValid( confirmation.getUserParticipation(), communityService )
                && confirmation.getSupervisor( communityService ) != null
                && organizationParticipationService.isValid( confirmation.getOrganizationParticipation(), communityService );
    }


    @SuppressWarnings( "unchecked" )
    private List<UserParticipationConfirmation> validate(
            List<UserParticipationConfirmation> userParticipationConfirmation,
            final CommunityService communityService ) {
        return (List<UserParticipationConfirmation>) CollectionUtils.select(
                userParticipationConfirmation,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isValid( (UserParticipationConfirmation) object, communityService );
                    }
                }
        );
    }


}
