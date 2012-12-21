package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.NotFoundException;
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
public class UserParticipationServiceImpl
        extends GenericSqlServiceImpl<UserParticipation, Long>
        implements UserParticipationService {

    @Autowired
    private UserParticipationConfirmationService userParticipationConfirmationService;

    @Autowired
    private ParticipationManager participationManager;

    @Autowired
    private OrganizationParticipationService organizationParticipationService;

    public UserParticipationServiceImpl() {
    }

    @Override
    @Transactional
    public UserParticipation addParticipation(
            String username,
            ChannelsUser participatingUser,
            Agent agent,
            PlanCommunity planCommunity
    ) {
        if ( canBeParticipatedAs( agent, planCommunity ) ) {
            UserParticipation userParticipation = new UserParticipation(
                    username,
                    participatingUser,
                    agent,
                    planCommunity );
            save( userParticipation );
            return userParticipation;
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public UserParticipation addAcceptedParticipation(
            String username,
            ChannelsUser participatingUser,
            Agent agent,
            PlanCommunity planCommunity
    ) {
        if ( canBeParticipatedAs( agent, planCommunity ) ) {
            UserParticipation userParticipation = new UserParticipation(
                    username,
                    participatingUser,
                    agent,
                    planCommunity );
            userParticipation.setAccepted( true );
            save( userParticipation );
            return userParticipation;
        } else {
            return null;
        }
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getUserParticipations(
            ChannelsUser user,
            PlanCommunity planCommunity ) {
        Session session = getSession();
        Plan plan = planCommunity.getPlan();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        if ( plan != null )  // null means wild card
            criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "participant", user.getUserInfo() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return validParticipations( (List<UserParticipation>) criteria.list(), planCommunity );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getActiveUserParticipations(
            ChannelsUser user,
            final PlanCommunity planCommunity ) {
        return (List<UserParticipation>) CollectionUtils.select(
                getUserParticipations( user, planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isActive( (UserParticipation) object, planCommunity );
                    }
                }
        );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isActive( UserParticipation userParticipation, PlanCommunity planCommunity ) {
        Agent agent = userParticipation.getAgent( planCommunity );
        if ( agent == null || !userParticipation.isAccepted() ) return false;
        if ( userParticipation.isSupervised( planCommunity ) ) {
            return userParticipationConfirmationService.isConfirmedByAllSupervisors( userParticipation, planCommunity );
        } else
            return !( agent.isParticipationRestrictedToEmployed()
                    && !isValidatedByEmployment( userParticipation, planCommunity ) );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getParticipationsAsAgent( Agent agent, PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
//        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "actorId", agent.getActor().getId() ) );
        if ( agent.getOrganizationParticipation() == null )
            criteria.add( Restrictions.isNull( "organizationParticipation" ) );
        else
            criteria.add( Restrictions.eq( "organizationParticipation", agent.getOrganizationParticipation() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return validParticipations( (List<UserParticipation>) criteria.list(), planCommunity );
    }


    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public UserParticipation getParticipation(
            ChannelsUser user,
            Agent agent,
            PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "participant", user.getUserInfo() ) );
        criteria.add( Restrictions.eq( "actorId", agent.getActor().getId() ) );
        if ( agent.getOrganizationParticipation() == null )
            criteria.add( Restrictions.isNull( "organizationParticipation" ) );
        else
            criteria.add( Restrictions.eq( "organizationParticipation", agent.getOrganizationParticipation() ) );
        criteria.addOrder( Order.desc( "created" ) );
        List<UserParticipation> participations = validParticipations(
                (List<UserParticipation>) criteria.list(),
                planCommunity );
        if ( participations.isEmpty() )
            return null;
        else
            return participations.get( 0 );
    }

    @Override
    @Transactional( readOnly = true )
    public List<Agent> listAgentsParticipatedAs( PlanCommunity planCommunity ) {
        Set<Agent> agents = new HashSet<Agent>();
        for ( UserParticipation userParticipation : list() ) {
            Agent agent = userParticipation.getAgent( planCommunity );
            if ( agent != null )
                agents.add( agent );
        }
        return new ArrayList<Agent>( agents );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean canBeParticipatedAs( Agent agent, PlanCommunity planCommunity ) {
        return !( agent.isSingularParticipation() && isParticipatedAs( agent, planCommunity ) );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isParticipatedAs( Agent agent, PlanCommunity planCommunity ) {
        return !getParticipationsAsAgent( agent, planCommunity ).isEmpty();
    }

    @Override
    @Transactional
    @SuppressWarnings( "unchecked" )
    public void removeParticipation( String username, UserParticipation participation, PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        if ( planCommunity != null )  // null = wild card -- to be used only when deleting all participation of a user
            criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        //       criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "participant", participation.getParticipant() ) );
        criteria.add( Restrictions.eq( "actorId", participation.getActorId() ) );
        for ( UserParticipation userParticipation : (List<UserParticipation>) criteria.list() ) {
            delete( userParticipation );
        }
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getAllParticipations( PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        //       criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        return validParticipations( (List<UserParticipation>) criteria.list(), planCommunity );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getAllActiveParticipations( final PlanCommunity planCommunity ) {
        return (List<UserParticipation>) CollectionUtils.select(
                getAllParticipations( planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation userParticipation = (UserParticipation) object;
                        return isActive( userParticipation, planCommunity );
                    }
                }
        );
    }


    @Override
    @Transactional
    public void deleteAllParticipations( ChannelsUser user, String username ) {
        // PlanCommunity is null b/c agent-exists validation of participation not wanted.
        for ( UserParticipation participation : getUserParticipations( user, null ) ) {
            removeParticipation( username, participation, null );
        }
    }


    public List<UserParticipation> validParticipations(
            List<UserParticipation> userParticipations,
            PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        List<UserParticipation> results = new ArrayList<UserParticipation>();
        for ( UserParticipation userParticipation : userParticipations ) {
            try {
                if ( userParticipation.getParticipant() != null ) {
                    if ( planService != null )
                        planService.find( Actor.class, userParticipation.getActorId() ); // exception if not found
                    results.add( userParticipation );
                }
            } catch ( NotFoundException e ) {
                // ignore
            }
        }
        return results;
    }

    // User already participates as an agent that has an employer in common with the agent of given participation of his/hers.
    private boolean isValidatedByEmployment(
            final UserParticipation userParticipation,
            final PlanCommunity planCommunity ) {
        final Agent agent = userParticipation.getAgent( planCommunity );
        if ( agent == null ) return false;
        final List<Agency> employers = participationManager.findAllEmployersOfAgent( agent, planCommunity );
        return CollectionUtils.exists(
                getUserParticipations( new ChannelsUser( userParticipation.getParticipant() ), planCommunity ),  // assuming, perhaps wrongly, they are all valid to avoid infinite loops from isValid(...)
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation otherParticipation = (UserParticipation) object;
                        if ( otherParticipation.equals( userParticipation ) ) {
                            return false;
                        } else {
                            Agent otherAgent = otherParticipation.getAgent( planCommunity );
                            if ( otherAgent == null || agent.equals( otherAgent ) ) return false;
                            List<Agency> otherEmployers =
                                    participationManager.findAllEmployersOfAgent( otherAgent, planCommunity );
                            return !CollectionUtils.intersection( employers, otherEmployers ).isEmpty();
                        }
                    }
                }
        );
    }


    @Override
    @Transactional( readOnly = true )
    public List<Agent> listAgentsUserParticipatesAs( ChannelsUser user, PlanCommunity planCommunity ) {
        Set<Agent> agents = new HashSet<Agent>();
        List<UserParticipation> participationList = getActiveUserParticipations( user, planCommunity );
        for ( UserParticipation participation : participationList ) {
            agents.add( participation.getAgent( planCommunity ) );
        }
        return new ArrayList<Agent>( agents );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getParticipationsSupervisedByUser(
            final ChannelsUser user,
            final PlanCommunity planCommunity ) {
        return (List<UserParticipation>) CollectionUtils.select(
                getAllParticipations( planCommunity ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation userParticipation = (UserParticipation) object;
                        Agent participationAgent = userParticipation.getAgent( planCommunity );
                        if ( participationAgent == null ) return false;
                        List<Agent> supervisors = participationManager.findAllSupervisorsOf(
                                participationAgent,
                                planCommunity );
                        if ( supervisors.isEmpty() ) return false;
                        List<Agent> userAgents = listAgentsUserParticipatesAs( user, planCommunity );
                        return !Collections.disjoint( supervisors, userAgents );
                    }
                } );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<Agent> listSupervisorsUserParticipatesAs(
            UserParticipation userParticipation,
            ChannelsUser user,
            PlanCommunity planCommunity ) {
        List<Agent> supervisorsUserParticipatesAs = new ArrayList<Agent>();
        List<Agent> agentsUserParticipatesAs = listAgentsUserParticipatesAs( user, planCommunity );
        Agent participationAgent = userParticipation.getAgent( planCommunity );
        if ( participationAgent != null ) {
            List<Agent> allSupervisorsOfAgent =
                    participationManager.findAllSupervisorsOf( participationAgent, planCommunity );
            supervisorsUserParticipatesAs.addAll( CollectionUtils.intersection(
                    agentsUserParticipatesAs,
                    allSupervisorsOfAgent ) );
        }
        return supervisorsUserParticipatesAs;
    }

    @Override
    @Transactional( readOnly = true )
    public List<String> listSupervisorsToNotify( UserParticipation userParticipation, PlanCommunity planCommunity ) {
        List<String> usernames = new ArrayList<String>();
        if ( userParticipation.isSupervised( planCommunity ) ) {
            Agent agent = userParticipation.getAgent( planCommunity );
            if ( agent != null ) {
                List<String> usernamesNotified = userParticipation.usersNotifiedToValidate();
                for ( Agent supervisor : participationManager.findAllSupervisorsOf( agent, planCommunity ) ) {
                    if ( !userParticipationConfirmationService.isConfirmedBy( userParticipation, supervisor ) ) {
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
    public List<ChannelsUserInfo> findUsersParticipatingAs( Agent agent, PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "actorId", agent.getActor().getId() ) );
        if ( agent.getOrganizationParticipation() == null )
            criteria.add( Restrictions.isNull( "organizationParticipation" ) );
        else
            criteria.add( Restrictions.eq( "organizationParticipation", agent.getOrganizationParticipation() ) );
        List<UserParticipation> participations = (List<UserParticipation>) criteria.list();
        Set<ChannelsUserInfo> userInfos = new HashSet<ChannelsUserInfo>();
        for ( UserParticipation participation : participations ) {
            if ( isActive( participation, planCommunity ) ) {
                userInfos.add( participation.getParticipant() );
            }
        }
        return new ArrayList<ChannelsUserInfo>( userInfos );
    }

    @Override
    @Transactional
    public boolean deleteParticipation( ChannelsUser user, Agent agent, PlanCommunity planCommunity ) {
        boolean success = false;
        if ( agent != null ) {
            for ( UserParticipation participation : getParticipationsAsAgent( agent, planCommunity ) ) {
                if ( participation.getParticipantUsername().equals( user.getUsername() ) ) {
                    userParticipationConfirmationService.deleteConfirmations( participation );
                    delete( participation );
                    success = true;
                }

            }
        }
        return success;
    }

    @SuppressWarnings( "unchecked" )
    private List<UserParticipation> listMatching( UserParticipation participation ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", participation.getCommunityUri() ) );
        criteria.add( Restrictions.eq( "participant", participation.getParticipant() ) );
        criteria.add( Restrictions.eq( "actorId", participation.getActorId() ) );
        if ( participation.getOrganizationParticipation() == null ) {
            criteria.add( Restrictions.isNull( "organizationParticipation" ) );
        } else {
            criteria.add( Restrictions.eq( "organizationParticipation", participation.getOrganizationParticipation() ) );
        }
        return (List<UserParticipation>) criteria.list();
    }

    @Override
    @Transactional
    public void accept( UserParticipation participation ) {
        List<UserParticipation> matches = listMatching( participation );
        if ( matches.isEmpty() ) {
            UserParticipation userParticipation = new UserParticipation( participation );
            userParticipation.setAccepted( true );
            save( userParticipation );
        } else {
            for ( UserParticipation userParticipation : matches ) {
                userParticipation.setAccepted( true );
                save( userParticipation );
            }
        }

    }

    @Override
    @Transactional
    public void refuse( UserParticipation participation ) {
        for ( UserParticipation userParticipation : listMatching( participation ) ) {
            if ( participation.isRequested() ) {
                userParticipation.setAccepted( false );
                save( userParticipation );
            } else {
                delete( userParticipation );
            }
        }
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isUserParticipatingAs( ChannelsUser user, Agent agent, PlanCommunity planCommunity ) {
        return listAgentsUserParticipatesAs( user, planCommunity ).contains( agent );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> listUserParticipationIn( OrganizationParticipation organizationParticipation,
                                                            PlanCommunity planCommunity ) {
        OrganizationParticipation orgParticipation = organizationParticipationService
                .findOrganizationParticipation( organizationParticipation.getRegisteredOrganization().getName( planCommunity ),
                        organizationParticipation.getPlaceholderOrganization( planCommunity ),
                        planCommunity );
        if ( orgParticipation == null ) {
            return new ArrayList<UserParticipation>();
        } else {
            Session session = getSession();
            Criteria criteria = session.createCriteria( getPersistentClass() );
            criteria.add( Restrictions.eq( "communityUri", organizationParticipation.getCommunityUri() ) );
            criteria.add( Restrictions.eq( "organizationParticipation", orgParticipation ) );
            return (List<UserParticipation>) criteria.list();
        }
    }


}
