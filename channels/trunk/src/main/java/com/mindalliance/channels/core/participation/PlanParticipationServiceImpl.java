package com.mindalliance.channels.core.participation;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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

    @Autowired
    private PlanParticipationValidationService planParticipationValidationService;

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
    @Transactional
    public PlanParticipation addAcceptedParticipation( String username, Plan plan, ChannelsUser participatingUser, Actor actor ) {
        if ( canBeDesignated( plan, actor ) ) {
            PlanParticipation planParticipation = new PlanParticipation( username, plan, participatingUser, actor );
            planParticipation.setAccepted( true );
            save( planParticipation );
            return planParticipation;
        } else {
            return null;
        }
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getUserParticipations(
            Plan plan,
            ChannelsUserInfo userInfo,
            QueryService queryService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        if ( plan != null )  // null means wild card
            criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
//        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "participant", userInfo ) );
        criteria.addOrder( Order.desc( "created" ) );
        return validParticipations( (List<PlanParticipation>) criteria.list(), queryService );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getActiveUserParticipations(
            final Plan plan,
            ChannelsUserInfo userInfo,
            final QueryService queryService ) {
        return (List<PlanParticipation>) CollectionUtils.select(
                getUserParticipations( plan, userInfo, queryService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isActive( plan, (PlanParticipation) object, queryService );
                    }
                }
        );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isActive( Plan plan, PlanParticipation planParticipation, QueryService queryService ) {
        Actor actor = planParticipation.getActor( queryService );
        if ( actor == null || !planParticipation.isAccepted() ) return false;
        if ( planParticipation.isSupervised( queryService ) ) {
            return isValidatedByAllSupervisors( planParticipation, queryService );
        } else
            return !actor.isParticipationRestrictedToEmployed()
                    || isValidatedByEmployment( plan, planParticipation, queryService );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getParticipationsAsActor( Plan plan, Actor actor, QueryService queryService ) {
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
        if ( plan != null )  // null = wild card -- to be used only when deleting all participation of a user
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
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getAllActiveParticipations( final Plan plan, final QueryService queryService ) {
        return (List<PlanParticipation>) CollectionUtils.select(
                getAllParticipations( plan, queryService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        PlanParticipation planParticipation = (PlanParticipation) object;
                        return isActive( plan, planParticipation, queryService );
                    }
                }
        );
    }


    @Override
    @Transactional
    public void deleteAllParticipations( ChannelsUserInfo userInfo, String username ) {
        // QueryService is null b/c agent-exists validation of participation not wanted.
        for ( PlanParticipation participation : getUserParticipations( null, userInfo, null ) ) {
            removeParticipation( username, null, participation );
        }
    }


    @Override
    @Transactional( readOnly = true )
    public boolean references( Plan plan, ModelObject mo, QueryService queryService ) {
        return mo instanceof Actor && !getParticipationsAsActor( plan, (Actor) mo, queryService ).isEmpty();
    }

    public List<PlanParticipation> validParticipations(
            List<PlanParticipation> planParticipations,
            QueryService queryService ) {
        List<PlanParticipation> results = new ArrayList<PlanParticipation>();
        for ( PlanParticipation planParticipation : planParticipations ) {
            try {
                if ( planParticipation.getParticipant() != null ) {
                    if ( queryService != null )
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
    public boolean isValidatedByAllSupervisors(
            PlanParticipation planParticipation,
            QueryService queryService ) {
        // Find all supervisors for participation's actor
        Actor actor = planParticipation.getActor( queryService );
        if ( actor == null ) return false;
        List<Actor> supervisors = queryService.findAllSupervisorsOf( actor );
        boolean validated = true;
        final List<PlanParticipationValidation> validations = planParticipationValidationService
                .getParticipationValidations( planParticipation );
        // Verify that each supervisor (some user participating as that supervisor) has
        // validated the participation.
        Iterator<Actor> iter = supervisors.iterator();
        while ( validated && iter.hasNext() ) {
            final Actor supervisor = iter.next();
            validated = CollectionUtils.exists(
                    validations,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (PlanParticipationValidation) object ).getSupervisorId() == supervisor.getId();
                        }
                    }
            );
        }
        return validated;
    }

    private boolean isValidatedByEmployment(
            Plan plan,
            final PlanParticipation planParticipation,
            final QueryService queryService ) {
        Actor actor = planParticipation.getActor( queryService );
        if ( actor == null ) return false;
        final List<Organization> employers = queryService.findEmployers( actor );
        return CollectionUtils.exists(
                getUserParticipations( plan, planParticipation.getParticipant(), queryService ),  // assuming, perhaps wrongly, they are all valid to avoid infinite loops from isValid(...)
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        PlanParticipation otherParticipation = (PlanParticipation) object;
                        if ( otherParticipation.equals( planParticipation ) ) {
                            return false;
                        } else {
                            if ( otherParticipation.isSupervised( queryService )
                                    && !isValidatedByAllSupervisors( otherParticipation, queryService ) ) {
                                return false;
                            } else {
                                Actor otherActor = otherParticipation.getActor( queryService );
                                if ( otherActor == null ) return false;
                                List<Organization> otherEmployers = queryService.findEmployers( otherActor );
                                return !CollectionUtils.intersection( employers, otherEmployers ).isEmpty();
                            }
                        }
                    }
                }
        );
    }


    @Override
    @Transactional( readOnly = true )
    public List<Actor> listActorsUserParticipatesAs( Plan plan, ChannelsUser user, QueryService queryService ) {
        Set<Actor> actors = new HashSet<Actor>();
        List<PlanParticipation> participationList = getActiveUserParticipations( plan, user.getUserInfo(), queryService );
        for ( PlanParticipation participation : participationList ) {
            actors.add( participation.getActor( queryService ) );
        }
        return new ArrayList<Actor>( actors );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getParticipationsSupervisedByUser(
            final ChannelsUser user,
            final Plan plan,
            final QueryService queryService ) {
        return (List<PlanParticipation>) CollectionUtils.select(
                getAllParticipations( plan, queryService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        PlanParticipation planParticipation = (PlanParticipation) object;
                        Actor participationActor = planParticipation.getActor( queryService );
                        if ( participationActor == null ) return false;
                        List<Actor> supervisors = queryService.findAllSupervisorsOf( participationActor );
                        if ( supervisors.isEmpty() ) return false;
                        List<Actor> userActors = listActorsUserParticipatesAs( plan, user, queryService );
                        return !Collections.disjoint( supervisors, userActors );
                    }
                } );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<Actor> listSupervisorsUserParticipatesAs(
            PlanParticipation planParticipation,
            Plan plan,
            ChannelsUser user,
            QueryService queryService ) {
        List<Actor> supervisorsUserParticipatesAs = new ArrayList<Actor>();
        List<Actor> actorsUserParticipatesAs = listActorsUserParticipatesAs( plan, user, queryService );
        Actor participationActor = planParticipation.getActor( queryService );
        if ( participationActor != null ) {
            List<Actor> allSupervisorsOfActor = queryService.findAllSupervisorsOf( participationActor );
            supervisorsUserParticipatesAs.addAll( CollectionUtils.intersection(
                    actorsUserParticipatesAs,
                    allSupervisorsOfActor ) );
        }
        return supervisorsUserParticipatesAs;
    }

    @Override
    @Transactional( readOnly = true )
    public List<String> listSupervisorsToNotify( Plan plan, PlanParticipation planParticipation, QueryService queryService ) {
        List<String> usernames = new ArrayList<String>();
        if ( planParticipation.isSupervised( queryService ) ) {
            Actor actor = planParticipation.getActor( queryService );
            if ( actor != null ) {
                List<String> usernamesNotified = planParticipation.usersNotifiedToValidate();
                for ( Actor supervisor : queryService.findAllSupervisorsOf( actor ) ) {
                    if ( !planParticipationValidationService.isValidatedBy( planParticipation, supervisor ) ) {
                        for ( ChannelsUserInfo supervisorUser : findUsersParticipatingAs( plan, supervisor, queryService ) ) {
                            String supervisorUsername = supervisorUser.getUsername();
                            if ( !usernamesNotified.contains( supervisorUsername ) ) {
                                usernames.add( supervisorUsername );
                            }
                        }
                    }
                }
            }
        }
        return usernames;
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<ChannelsUserInfo> findUsersParticipatingAs( Plan plan, Actor actor, QueryService queryService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        criteria.add( Restrictions.eq( "actorId", actor.getId() ) );
        //       criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        List<PlanParticipation> participations = (List<PlanParticipation>) criteria.list();
        Set<ChannelsUserInfo> userInfos = new HashSet<ChannelsUserInfo>();
        for ( PlanParticipation participation : participations ) {
            if ( isActive( plan, participation, queryService ) ) {
                userInfos.add( participation.getParticipant() );
            }
        }
        return new ArrayList<ChannelsUserInfo>( userInfos );
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
                        return isParticipationOpenAndAvailable(
                                actor,
                                user,
                                queryService );
                    }
                }
        );
    }


    @Override
    @Transactional( readOnly = true )
    public boolean isParticipationOpenAndAvailable( Actor actor, ChannelsUser user, QueryService queryService ) {
        Plan plan = queryService.getPlan();
        List<PlanParticipation> currentParticipations = getUserParticipations(
                plan,
                user.getUserInfo(),
                queryService );
        List<PlanParticipation> activeParticipations = getActiveUserParticipations(
                plan,
                user.getUserInfo(),
                queryService );
        return actor != null
                && !actor.isUnknown()
                && actor.isParticipationUserAssignable()
                && !alreadyParticipatingAs( actor, currentParticipations )
                && !isSingularAndTaken( actor, plan, queryService )
                && queryService.meetsPreEmploymentConstraint( actor, activeParticipations );
    }

    @Override
    @Transactional
    public void deleteParticipation( Plan plan, ChannelsUserInfo userInfo, Actor actor, QueryService queryService ) {
        if ( actor != null ) {
            for ( PlanParticipation participation : getParticipationsAsActor( plan, actor, queryService ) ) {
                if ( participation.getParticipantUsername().equals( userInfo.getUsername() ) ) {
                    planParticipationValidationService.deleteValidations( participation );
                    delete( participation );
                }

            }
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<PlanParticipation> listMatching( PlanParticipation participation ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", participation.getPlanUri() ) );
        criteria.add( Restrictions.eq( "participant", participation.getParticipant() ) );
        criteria.add( Restrictions.eq( "actorId", participation.getActorId() ) );
        return (List<PlanParticipation>) criteria.list();
    }

    @Override
    @Transactional
    public void accept( PlanParticipation participation ) {
        List<PlanParticipation> matches = listMatching( participation );
        if ( matches.isEmpty() ) {
            PlanParticipation planParticipation = new PlanParticipation( participation );
            planParticipation.setAccepted( true );
            save( planParticipation );
        } else {
            for ( PlanParticipation planParticipation : matches ) {
                planParticipation.setAccepted( true );
                save( planParticipation );
            }
        }

    }

    @Override
    @Transactional
    public void refuse( PlanParticipation participation ) {
        for ( PlanParticipation planParticipation : listMatching( participation ) ) {
            if ( participation.isRequested() ) {
                planParticipation.setAccepted( false );
                save( planParticipation );
            } else {
                delete( planParticipation );
            }
        }
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
                && !getParticipationsAsActor( plan, actor, queryService ).isEmpty();
    }


}
