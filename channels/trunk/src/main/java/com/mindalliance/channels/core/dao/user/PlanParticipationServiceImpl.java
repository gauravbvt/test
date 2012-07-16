package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the plan participation service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/12/12
 * Time: 2:34 PM
 */
public class PlanParticipationServiceImpl
        extends GenericSqlServiceImpl<PlanParticipation, Long>
        implements PlanParticipationService {

    public PlanParticipationServiceImpl() {
    }

    @Override
    @Transactional
    public PlanParticipation addParticipation( String username, Plan plan, ChannelsUser participatingUser, Actor actor ) {
        if ( canBeDesignated( plan, actor ) ) {
            PlanParticipation planParticipation = new PlanParticipation( username, plan, participatingUser, actor );
            save( planParticipation );
            return planParticipation;
        } else {
            return null;
        }
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getParticipations(
            Plan plan,
            ChannelsUserInfo userInfo,
            QueryService queryService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
//        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "participant", userInfo ) );
        criteria.addOrder( Order.desc( "created" ) );
        return validParticipations( (List<PlanParticipation>) criteria.list(), queryService );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getParticipations( Plan plan, Actor actor, QueryService queryService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
//        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "actorId", actor.getId() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return validParticipations( (List<PlanParticipation>) criteria.list(), queryService );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public PlanParticipation getParticipation(
            Plan plan,
            ChannelsUserInfo userInfo,
            Actor actor,
            QueryService queryService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
//        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "participant", userInfo ) );
        criteria.add( Restrictions.eq( "actorId", actor.getId() ) );
        criteria.addOrder( Order.desc( "created" ) );
        List<PlanParticipation> participations = validParticipations(
                (List<PlanParticipation>) criteria.list(),
                queryService );
        if ( participations.isEmpty() )
            return null;
        else
            return participations.get( 0 );
    }

    @Override
    @Transactional( readOnly = true )
    public List<Actor> listUserDesignatedActors( QueryService queryService ) {
        Set<Long> ids = new HashSet<Long>();
        for ( PlanParticipation planParticipation : list() ) {
            ids.add( planParticipation.getActorId() );
        }
        List<Actor> actors = new ArrayList<Actor>();
        for ( long id : ids ) {
            try {
                Actor actor = queryService.find( Actor.class, id );
                actors.add( actor );
            } catch ( NotFoundException e ) {
                // ignore obsolete id
            }
        }
        return actors;
    }

    @Override
    @Transactional( readOnly = true )
    public boolean canBeDesignated( Plan plan, Actor actor ) {
        return !actor.isSingularParticipation()
                || !isDesignated( plan, actor );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isDesignated( Plan plan, Actor actor ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
//        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "actorId", actor.getId() ) );
        return !criteria.list().isEmpty();
    }

    @Override
    @Transactional
    @SuppressWarnings( "unchecked" )
    public void removeParticipation( String username, Plan plan, PlanParticipation participation ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        //       criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "participant", participation.getParticipant() ) );
        criteria.add( Restrictions.eq( "actorId", participation.getActorId() ) );
        for ( PlanParticipation planParticipation : (List<PlanParticipation>) criteria.list() ) {
            delete( planParticipation );
        }
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getAllParticipations( Plan plan, QueryService queryService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        //       criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        return validParticipations( (List<PlanParticipation>) criteria.list(), queryService );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean references( Plan plan, ModelObject mo, QueryService queryService ) {
        return mo instanceof Actor && !getParticipations( plan, (Actor) mo, queryService ).isEmpty();
    }

    private List<PlanParticipation> validParticipations(
            List<PlanParticipation> planParticipations,
            QueryService queryService ) {
        List<PlanParticipation> results = new ArrayList<PlanParticipation>();
        for ( PlanParticipation planParticipation : planParticipations ) {
            try {
                if ( planParticipation.getParticipant() != null ) {
                    queryService.find( Actor.class, planParticipation.getActorId() ); // exception if not found
                    results.add( planParticipation );
                }
            } catch ( NotFoundException e ) {
                // ignore
            }
        }
        return results;
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<Actor> findOpenActors( final ChannelsUser user, final QueryService queryService ) {
        return (List<Actor>) CollectionUtils.select(
                queryService.listActualEntities( Actor.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Actor actor = (Actor) object;
                        return queryService.getPlanParticipationService().isParticipationAvailable(
                                actor,
                                user,
                                queryService );
                    }
                }
        );
    }


    @Override
    @Transactional( readOnly = true )
    public boolean isParticipationAvailable( Actor actor, ChannelsUser user, QueryService queryService ) {
        Plan plan = queryService.getPlan();
        List<PlanParticipation> currentParticipations = getParticipations(
                plan,
                user.getUserInfo(),
                queryService );
        return actor != null
                && !actor.isUnknown()
                && actor.isOpenParticipation()
                && !alreadyParticipatingAs( actor, currentParticipations )
                && !isSingularAndTaken( actor, plan, queryService )
                && queryService.meetsPreEmploymentConstraint( actor, currentParticipations );
    }

    private boolean alreadyParticipatingAs( final Actor actor, List<PlanParticipation> currentParticipations ) {
        return CollectionUtils.exists(
                currentParticipations,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (PlanParticipation) object ).getActorId() == actor.getId();
                    }
                } );
    }

    private boolean isSingularAndTaken( Actor actor, Plan plan, QueryService queryService ) {
        return actor.isSingularParticipation()
                && !getParticipations( plan, actor, queryService ).isEmpty();
    }



}
