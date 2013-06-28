package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.db.data.users.UserRecord;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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

//    @Autowired
    private UserParticipationConfirmationService userParticipationConfirmationService;

//    @Autowired
    private ParticipationManager participationManager;

//    @Autowired
    private OrganizationParticipationService organizationParticipationService;

    public UserParticipationServiceImpl() {
    }

    @Override
    @Transactional
    public UserParticipation addParticipation(
            String username,
            ChannelsUser participatingUser,
            Agent agent,
            CommunityService communityService
    ) {
        if ( isParticipationNotFull( agent, communityService ) ) {
            UserParticipation userParticipation = new UserParticipation(
                    username,
                    participatingUser,
                    agent,
                    communityService.getPlanCommunity() );
            save( userParticipation );
            communityService.clearCache();
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
            CommunityService communityService
    ) {
        if ( isParticipationNotFull( agent, communityService ) ) {
            UserParticipation userParticipation = new UserParticipation(
                    username,
                    participatingUser,
                    agent,
                    communityService.getPlanCommunity() );
            userParticipation.setAccepted( true );
            save( userParticipation );
            communityService.clearCache();
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
            CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        if ( communityService != null )  // null means wild card
            criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.eq( "participant", user.getUserRecord() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return communityService == null
                ? (List<UserParticipation>) criteria.list()
                : validate( (List<UserParticipation>) criteria.list(), communityService );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getActiveUserParticipations(
            ChannelsUser user,
            final CommunityService communityService ) {
        return (List<UserParticipation>) CollectionUtils.select(
                getUserParticipations( user, communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isActive( (UserParticipation) object, communityService );
                    }
                }
        );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isActive( UserParticipation userParticipation, CommunityService communityService ) {
        Agent agent = userParticipation.getAgent( communityService );
        if ( agent == null || !userParticipation.isAccepted() ) return false;
        if ( userParticipation.isSupervised( communityService ) ) {
            return userParticipationConfirmationService.isConfirmedByAllSupervisors( userParticipation, communityService );
        } else
            return !( agent.isParticipationRestrictedToEmployed()
                    && !isValidatedByEmployment( userParticipation, communityService ) );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getParticipationsAsAgent( Agent agent, CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
//        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "actorId", agent.getActorId() ) );
        if ( agent.getOrganizationParticipation() == null )
            criteria.add( Restrictions.isNull( "organizationParticipation" ) );
        else
            criteria.add( Restrictions.eq( "organizationParticipation", agent.getOrganizationParticipation() ) );
        criteria.addOrder( Order.desc( "created" ) );
        return validate( (List<UserParticipation>) criteria.list(), communityService );
    }


    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public UserParticipation getParticipation(
            ChannelsUser user,
            Agent agent,
            CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.eq( "participant", user.getUserRecord() ) );
        criteria.add( Restrictions.eq( "actorId", agent.getActorId() ) );
        if ( agent.getOrganizationParticipation() == null )
            criteria.add( Restrictions.isNull( "organizationParticipation" ) );
        else
            criteria.add( Restrictions.eq( "organizationParticipation", agent.getOrganizationParticipation() ) );
        criteria.addOrder( Order.desc( "created" ) );
        List<UserParticipation> participations = validate(
                (List<UserParticipation>) criteria.list(),
                communityService );
        if ( participations.isEmpty() )
            return null;
        else
            return participations.get( 0 );
    }

    @Override
    @Transactional( readOnly = true )
    public List<Agent> listAgentsParticipatedAs( CommunityService communityService ) {
        Set<Agent> agents = new HashSet<Agent>();
        for ( UserParticipation userParticipation : list() ) {
            Agent agent = userParticipation.getAgent( communityService );
            if ( agent != null )
                agents.add( agent );
        }
        return new ArrayList<Agent>( agents );
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isParticipationNotFull( Agent agent, CommunityService communityService ) {
        if ( agent.isAnyNumberOfParticipants() ) return true;
        else {
            int count = getParticipationsAsAgent( agent, communityService ).size();
            return count < agent.getMaxParticipation();
        }
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isParticipatedAs( Agent agent, CommunityService communityService ) {
        return !getParticipationsAsAgent( agent, communityService ).isEmpty();
    }

    @Override
    @Transactional
    @SuppressWarnings( "unchecked" )
    public void removeParticipation( String username, UserParticipation participation, CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        if ( communityService != null )  // null = wild card -- to be used only when deleting all participation of a user
            criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        //       criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        criteria.add( Restrictions.eq( "participant", participation.getParticipant() ) );
        criteria.add( Restrictions.eq( "actorId", participation.getActorId() ) );
        for ( UserParticipation userParticipation : (List<UserParticipation>) criteria.list() ) {
            delete( userParticipation );
        }
        if ( communityService != null )
            communityService.clearCache();
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getAllParticipations( CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        //       criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        return validate( (List<UserParticipation>) criteria.list(), communityService );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getAllActiveParticipations( final CommunityService communityService ) {
        return (List<UserParticipation>) CollectionUtils.select(
                getAllParticipations( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation userParticipation = (UserParticipation) object;
                        return isActive( userParticipation, communityService );
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
            CommunityService communityService ) {
        return validate( userParticipations, communityService );
    }

    // User already participates as an agent that has an employer in common with the agent of given participation of his/hers.
    private boolean isValidatedByEmployment(
            final UserParticipation userParticipation,
            final CommunityService communityService ) {
        final Agent agent = userParticipation.getAgent( communityService );
        if ( agent == null ) return false;
        final List<Agency> employers = participationManager.findAllEmployersOfAgent( agent, communityService );
        return CollectionUtils.exists(
                getUserParticipations( new ChannelsUser( /*userParticipation.getParticipant()*/ ), communityService ),  // assuming, perhaps wrongly, they are all valid to avoid infinite loops from isValid(...)
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation otherParticipation = (UserParticipation) object;
                        if ( otherParticipation.equals( userParticipation ) ) {
                            return false;
                        } else {
                            Agent otherAgent = otherParticipation.getAgent( communityService );
                            if ( otherAgent == null || agent.equals( otherAgent ) ) return false;
                            List<Agency> otherEmployers =
                                    participationManager.findAllEmployersOfAgent( otherAgent, communityService );
                            return !CollectionUtils.intersection( employers, otherEmployers ).isEmpty();
                        }
                    }
                }
        );
    }


    @Override
    @Transactional( readOnly = true )
    public List<Agent> listAgentsUserParticipatesAs( ChannelsUser user, CommunityService communityService ) {
        Set<Agent> agents = new HashSet<Agent>();
        List<UserParticipation> participationList = getActiveUserParticipations( user, communityService );
        for ( UserParticipation participation : participationList ) {
            agents.add( participation.getAgent( communityService ) );
        }
        return new ArrayList<Agent>( agents );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getParticipationsSupervisedByUser(
            final ChannelsUser user,
            final CommunityService communityService ) {
        return (List<UserParticipation>) CollectionUtils.select(
                getAllParticipations( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation userParticipation = (UserParticipation) object;
                        Agent participationAgent = userParticipation.getAgent( communityService );
                        if ( participationAgent == null
                                || !participationAgent.isSupervisedParticipation() ) return false;
                        List<Agent> supervisors = participationManager.findAllSupervisorsOf(
                                participationAgent,
                                communityService );
                        if ( supervisors.isEmpty() ) return false;
                        List<Agent> userAgents = listAgentsUserParticipatesAs( user, communityService );
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
            CommunityService communityService ) {
        List<Agent> supervisorsUserParticipatesAs = new ArrayList<Agent>();
        List<Agent> agentsUserParticipatesAs = listAgentsUserParticipatesAs( user, communityService );
        Agent participationAgent = userParticipation.getAgent( communityService );
        if ( participationAgent != null ) {
            List<Agent> allSupervisorsOfAgent =
                    participationManager.findAllSupervisorsOf( participationAgent, communityService );
            supervisorsUserParticipatesAs.addAll( CollectionUtils.intersection(
                    agentsUserParticipatesAs,
                    allSupervisorsOfAgent ) );
        }
        return supervisorsUserParticipatesAs;
    }

    @Override
    @Transactional( readOnly = true )
    public List<String> listSupervisorsToNotify( UserParticipation userParticipation, CommunityService communityService ) {
        List<String> usernames = new ArrayList<String>();
        if ( userParticipation.isSupervised( communityService ) ) {
            Agent agent = userParticipation.getAgent( communityService );
            if ( agent != null ) {
                List<String> usernamesNotified = userParticipation.usersNotifiedToValidate();
                for ( Agent supervisor : participationManager.findAllSupervisorsOf( agent, communityService ) ) {
                    if ( !userParticipationConfirmationService.isConfirmedBy( userParticipation, supervisor ) ) {
                        for ( UserRecord supervisorUser : findUsersParticipatingAs( supervisor, communityService ) ) {
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
    public List<UserRecord> findUsersParticipatingAs( Agent agent, CommunityService communityService ) {
        PlanCommunity planCommunity = communityService.getPlanCommunity();
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        criteria.add( Restrictions.eq( "actorId", agent.getActorId() ) );
        if ( agent.getOrganizationParticipation() == null )
            criteria.add( Restrictions.isNull( "organizationParticipation" ) );
        else
            criteria.add( Restrictions.eq( "organizationParticipation", agent.getOrganizationParticipation() ) );
        List<UserParticipation> participations = validate( (List<UserParticipation>) criteria.list(), communityService );
        Set<UserRecord> userInfos = new HashSet<UserRecord>();
        for ( UserParticipation participation : participations ) {
            if ( isActive( participation, communityService ) ) {
               // userInfos.add( participation.getParticipant() );
            }
        }
        return new ArrayList<UserRecord>( userInfos );
    }

    @Override
    @Transactional
    public boolean deleteParticipation( ChannelsUser user, Agent agent, CommunityService communityService ) {
        boolean success = false;
        if ( agent != null ) {
            for ( UserParticipation participation : getParticipationsAsAgent( agent, communityService ) ) {
                if ( participation.getParticipantUsername().equals( user.getUsername() ) ) {
                    userParticipationConfirmationService.deleteConfirmations( participation, communityService );
                    delete( participation );
                    communityService.clearCache();
                    success = true;
                }

            }
        }
        return success;
    }

    @SuppressWarnings( "unchecked" )
    private List<UserParticipation> listMatching( UserParticipation participation, CommunityService communityService ) {
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
        return validate( (List<UserParticipation>) criteria.list(), communityService );
    }

    @Override
    @Transactional
    public void accept( UserParticipation participation, CommunityService communityService ) {
        List<UserParticipation> matches = listMatching( participation, communityService );
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
        communityService.clearCache();
    }

    @Override
    @Transactional
    public void refuse( UserParticipation participation, CommunityService communityService ) {
        for ( UserParticipation userParticipation : listMatching( participation, communityService ) ) {
            if ( participation.isRequested() ) {
                userParticipation.setAccepted( false );
                save( userParticipation );
            } else {
                delete( userParticipation );
                communityService.clearCache();
            }
        }
        communityService.clearCache();
    }

    @Override
    @Transactional( readOnly = true )
    public boolean isUserParticipatingAs( ChannelsUser user, Agent agent, CommunityService communityService ) {
        return listAgentsUserParticipatesAs( user, communityService ).contains( agent );
    }

    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> listUserParticipationIn( OrganizationParticipation organizationParticipation,
                                                            CommunityService communityService ) {
        OrganizationParticipation orgParticipation = organizationParticipationService
                .findOrganizationParticipation( organizationParticipation.getRegisteredOrganization().getName( communityService ),
                        organizationParticipation.getPlaceholderOrganization( communityService ),
                        communityService );
        if ( orgParticipation == null ) {
            return new ArrayList<UserParticipation>();
        } else {
            Session session = getSession();
            Criteria criteria = session.createCriteria( getPersistentClass() );
            criteria.add( Restrictions.eq( "communityUri", organizationParticipation.getCommunityUri() ) );
            criteria.add( Restrictions.eq( "organizationParticipation", orgParticipation ) );
            return validate( (List<UserParticipation>) criteria.list(), communityService );
        }
    }


    @Override
    @Transactional( readOnly = true )
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> listUserParticipationsAwaitingConfirmationBy(
            ChannelsUser user,
            final CommunityService communityService ) {
        final List<UserParticipationConfirmation> allConfirmations = new ArrayList<UserParticipationConfirmation>(  );
             //   communityService.getUserParticipationConfirmationService().getParticipationConfirmations( communityService );
        final List<Agent> userAgents = listAgentsUserParticipatesAs(
                user,
                communityService );
        return (List<UserParticipation>) CollectionUtils.select(
                getParticipationsSupervisedByUser( user, communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final UserParticipation supervisedParticipation = (UserParticipation) object;
                        return !CollectionUtils.exists(
                                allConfirmations,
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        UserParticipationConfirmation confirmation = (UserParticipationConfirmation) object;
                                        Agent supervisor = confirmation.getSupervisor( communityService );
                                        return confirmation.getUserParticipation()
                                                .equals( supervisedParticipation )
                                                && supervisor != null
                                                && userAgents.contains( supervisor );
                                    }
                                }
                        );
                    }
                } );

    }

    @Override
    @Transactional( readOnly = true )
    public boolean isValid( UserParticipation userParticipation, CommunityService communityService ) {
        return userParticipation != null
                && userParticipation.getParticipant() != null
                && communityService.getPlanService().exists( Actor.class, userParticipation.getActorId(), userParticipation.getCreated() )
                && ( userParticipation.getOrganizationParticipation() == null
                || organizationParticipationService.isValid( userParticipation.getOrganizationParticipation(), communityService ) );
    }

    @SuppressWarnings( "unchecked" )
    private List<UserParticipation> validate(
            List<UserParticipation> userParticipations,
            final CommunityService communityService ) {
        return (List<UserParticipation>) CollectionUtils.select(
                userParticipations,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isValid( (UserParticipation) object, communityService );
                    }
                }
        );
    }


}
