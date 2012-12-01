package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.core.query.PlanService;
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
            ChannelsUserInfo userInfo,
            PlanCommunity planCommunity ) {
        Session session = getSession();
        Plan plan = planCommunity.getPlan();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        if ( plan != null )  // null means wild card
            criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
//        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "participant", userInfo ) );
        criteria.addOrder( Order.desc( "created" ) );
        return validParticipations( (List<PlanParticipation>) criteria.list(), planCommunity );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getActiveUserParticipations(
            ChannelsUserInfo userInfo,
            final PlanCommunity planCommunity ) {
        return (List<PlanParticipation>) CollectionUtils.select(
                getUserParticipations( userInfo, planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isActive( (PlanParticipation) object, planCommunity );
                    }
                }
        );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isActive( PlanParticipation planParticipation, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        Actor actor = planParticipation.getActor( planService );
        if ( actor == null || !planParticipation.isAccepted() ) return false;
        if ( planParticipation.isSupervised( planService ) ) {
            return isValidatedByAllSupervisors( planParticipation, planCommunity );
        } else
            return !actor.isParticipationRestrictedToEmployed()
                    || isValidatedByEmployment( planParticipation, planCommunity );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getParticipationsAsActor( Actor actor, PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", planCommunity.getPlan().getUri() ) );
//        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "actorId", actor.getId() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return validParticipations( (List<PlanParticipation>) criteria.list(), planCommunity );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public PlanParticipation getParticipation(
            ChannelsUserInfo userInfo,
            Actor actor,
            PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", planCommunity.getPlan().getUri() ) );
//        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "participant", userInfo ) );
        criteria.add( Restrictions.eq( "actorId", actor.getId() ) );
        criteria.addOrder( Order.desc( "created" ) );
        List<PlanParticipation> participations = validParticipations(
                (List<PlanParticipation>) criteria.list(),
                planCommunity );
        if ( participations.isEmpty() )
            return null;
        else
            return participations.get( 0 );
    }

    @Override
    @Transactional( readOnly = true )
    public List<Actor> listUserDesignatedActors( PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        Set<Long> ids = new HashSet<Long>();
        for ( PlanParticipation planParticipation : list() ) {
            ids.add( planParticipation.getActorId() );
        }
        List<Actor> actors = new ArrayList<Actor>();
        for ( long id : ids ) {
            try {
                Actor actor = planService.find( Actor.class, id );
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
    public List<PlanParticipation> getAllParticipations( PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", planCommunity.getPlan().getUri() ) );
        //       criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        return validParticipations( (List<PlanParticipation>) criteria.list(), planCommunity );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getAllActiveParticipations( final PlanCommunity planCommunity ) {
        return (List<PlanParticipation>) CollectionUtils.select(
                getAllParticipations( planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        PlanParticipation planParticipation = (PlanParticipation) object;
                        return isActive( planParticipation, planCommunity );
                    }
                }
        );
    }


    @Override
    @Transactional
    public void deleteAllParticipations( ChannelsUserInfo userInfo, String username ) {
        // PlanCommunity is null b/c agent-exists validation of participation not wanted.
        for ( PlanParticipation participation : getUserParticipations( userInfo, null ) ) {
            removeParticipation( username, null, participation );
        }
    }


    @Override
    @Transactional( readOnly = true )
    public boolean references( ModelObject mo, PlanCommunity planCommunity ) {
        return mo instanceof Actor && !getParticipationsAsActor( (Actor) mo, planCommunity ).isEmpty();
    }

    public List<PlanParticipation> validParticipations(
            List<PlanParticipation> planParticipations,
            PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        List<PlanParticipation> results = new ArrayList<PlanParticipation>();
        for ( PlanParticipation planParticipation : planParticipations ) {
            try {
                if ( planParticipation.getParticipant() != null ) {
                    if ( planService != null )
                        planService.find( Actor.class, planParticipation.getActorId() ); // exception if not found
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
            PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        // Find all supervisors for participation's actor
        Actor actor = planParticipation.getActor( planService );
        if ( actor == null ) return false;
        List<Actor> supervisors = planService.findAllSupervisorsOf( actor );
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
            final PlanParticipation planParticipation,
            final PlanCommunity planCommunity ) {
        final PlanService planService = planCommunity.getPlanService();
        Actor actor = planParticipation.getActor( planService );
        if ( actor == null ) return false;
        final List<Organization> employers = planService.findEmployers( actor );
        return CollectionUtils.exists(
                getUserParticipations(planParticipation.getParticipant(), planCommunity ),  // assuming, perhaps wrongly, they are all valid to avoid infinite loops from isValid(...)
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        PlanParticipation otherParticipation = (PlanParticipation) object;
                        if ( otherParticipation.equals( planParticipation ) ) {
                            return false;
                        } else {
                            if ( otherParticipation.isSupervised( planService )
                                    && !isValidatedByAllSupervisors( otherParticipation, planCommunity ) ) {
                                return false;
                            } else {
                                Actor otherActor = otherParticipation.getActor( planService );
                                if ( otherActor == null ) return false;
                                List<Organization> otherEmployers = planService.findEmployers( otherActor );
                                return !CollectionUtils.intersection( employers, otherEmployers ).isEmpty();
                            }
                        }
                    }
                }
        );
    }


    @Override
    @Transactional( readOnly = true )
    public List<Actor> listActorsUserParticipatesAs( ChannelsUser user, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        Set<Actor> actors = new HashSet<Actor>();
        List<PlanParticipation> participationList = getActiveUserParticipations( user.getUserInfo(), planCommunity );
        for ( PlanParticipation participation : participationList ) {
            actors.add( participation.getActor( planService ) );
        }
        return new ArrayList<Actor>( actors );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<PlanParticipation> getParticipationsSupervisedByUser(
            final ChannelsUser user,
            final PlanCommunity planCommunity ) {
        final PlanService planService = planCommunity.getPlanService();
        return (List<PlanParticipation>) CollectionUtils.select(
                getAllParticipations( planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        PlanParticipation planParticipation = (PlanParticipation) object;
                        Actor participationActor = planParticipation.getActor( planService );
                        if ( participationActor == null ) return false;
                        List<Actor> supervisors = planService.findAllSupervisorsOf( participationActor );
                        if ( supervisors.isEmpty() ) return false;
                        List<Actor> userActors = listActorsUserParticipatesAs( user, planCommunity );
                        return !Collections.disjoint( supervisors, userActors );
                    }
                } );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<Actor> listSupervisorsUserParticipatesAs(
            PlanParticipation planParticipation,
            ChannelsUser user,
            PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        List<Actor> supervisorsUserParticipatesAs = new ArrayList<Actor>();
        List<Actor> actorsUserParticipatesAs = listActorsUserParticipatesAs( user, planCommunity );
        Actor participationActor = planParticipation.getActor( planService );
        if ( participationActor != null ) {
            List<Actor> allSupervisorsOfActor = planService.findAllSupervisorsOf( participationActor );
            supervisorsUserParticipatesAs.addAll( CollectionUtils.intersection(
                    actorsUserParticipatesAs,
                    allSupervisorsOfActor ) );
        }
        return supervisorsUserParticipatesAs;
    }

    @Override
    @Transactional( readOnly = true )
    public List<String> listSupervisorsToNotify( PlanParticipation planParticipation, PlanCommunity planCommunity ) {
        List<String> usernames = new ArrayList<String>();
        PlanService planService = planCommunity.getPlanService();
        if ( planParticipation.isSupervised( planService ) ) {
            Actor actor = planParticipation.getActor( planService );
            if ( actor != null ) {
                List<String> usernamesNotified = planParticipation.usersNotifiedToValidate();
                for ( Actor supervisor : planService.findAllSupervisorsOf( actor ) ) {
                    if ( !planParticipationValidationService.isValidatedBy( planParticipation, supervisor ) ) {
                        for ( ChannelsUserInfo supervisorUser : findUsersParticipatingAs( supervisor, planCommunity ) ) {
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
    public List<ChannelsUserInfo> findUsersParticipatingAs( Actor actor, PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", planCommunity.getPlan().getUri() ) );
        criteria.add( Restrictions.eq( "actorId", actor.getId() ) );
        //       criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        List<PlanParticipation> participations = (List<PlanParticipation>) criteria.list();
        Set<ChannelsUserInfo> userInfos = new HashSet<ChannelsUserInfo>();
        for ( PlanParticipation participation : participations ) {
            if ( isActive( participation, planCommunity ) ) {
                userInfos.add( participation.getParticipant() );
            }
        }
        return new ArrayList<ChannelsUserInfo>( userInfos );
    }


    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<Actor> findOpenActors( final ChannelsUser user, final PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        return (List<Actor>) CollectionUtils.select(
                planService.listActualEntities( Actor.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Actor actor = (Actor) object;
                        return isParticipationOpenAndAvailable(
                                actor,
                                user,
                                planCommunity );
                    }
                }
        );
    }


    @Override
    @Transactional( readOnly = true )
    public boolean isParticipationOpenAndAvailable( Actor actor, ChannelsUser user, PlanCommunity planCommunity ) {
        CommunityService communityService = planCommunity.getCommunityService();
        List<PlanParticipation> currentParticipations = getUserParticipations(
                user.getUserInfo(),
                planCommunity );
        List<PlanParticipation> activeParticipations = getActiveUserParticipations(
                user.getUserInfo(),
                planCommunity );
        return actor != null
                && !actor.isUnknown()
                && actor.isParticipationUserAssignable()
                && !alreadyParticipatingAs( actor, currentParticipations )
                && !isSingularAndTaken( actor, planCommunity )
                && communityService.meetsPreEmploymentConstraint( actor, activeParticipations );
    }

    @Override
    @Transactional
    public void deleteParticipation( ChannelsUserInfo userInfo, Actor actor, PlanCommunity planCommunity ) {
        if ( actor != null ) {
            for ( PlanParticipation participation : getParticipationsAsActor( actor, planCommunity ) ) {
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

    private boolean isSingularAndTaken( Actor actor, PlanCommunity planCommunity ) {
        return actor.isSingularParticipation()
                && !getParticipationsAsActor( actor, planCommunity ).isEmpty();
    }


}
