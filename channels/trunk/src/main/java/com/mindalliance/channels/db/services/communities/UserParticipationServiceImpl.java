package com.mindalliance.channels.db.services.communities;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import com.mindalliance.channels.db.data.communities.QUserParticipation;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.communities.UserParticipationConfirmation;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.repositories.UserParticipationRepository;
import com.mindalliance.channels.db.services.AbstractDataService;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/20/13
 * Time: 12:29 PM
 */
@Component
public class UserParticipationServiceImpl
        extends AbstractDataService<UserParticipation>
        implements UserParticipationService {

    @Autowired
    private UserParticipationRepository repository;

    @Autowired
    private UserParticipationConfirmationService userParticipationConfirmationService;

    @Autowired
    private ParticipationManager participationManager;

    @Autowired
    private RegisteredOrganizationService registeredOrganizationService;

    public UserParticipationServiceImpl() {
    }

    @Override
    public UserParticipation load( String uid ) {
        return repository.findOne( uid );
    }

    @Override
    public void save( UserParticipation userParticipation ) {
        repository.save( userParticipation );
    }

    private List<UserParticipation> list() {
        return toList( repository.findAll() );
    }

    private void delete( UserParticipation userParticipation ) {
        repository.delete( userParticipation );
    }

    @Override
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
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getUserParticipations( // todo - add indirect user participations
                                                          ChannelsUser user,
                                                          CommunityService communityService ) {
        QUserParticipation qUserParticipation = QUserParticipation.userParticipation;
        BooleanBuilder bb = new BooleanBuilder();
        bb.and( qUserParticipation.classLabel.eq( UserParticipation.class.getSimpleName() ) );
        if ( communityService != null ) {  // null = wild card
            bb.and( qUserParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) );
        }
        bb.and( qUserParticipation.participantUsername.eq( user.getUsername() ) );
        List<UserParticipation> participationList = toList(
                repository.findAll( bb, qUserParticipation.created.desc() ) );
        return communityService == null
                ? participationList
                : validate( participationList, communityService );

    }

    @Override
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
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getActiveSupervisedParticipations( ChannelsUser user, final CommunityService communityService ) {
        return (List<UserParticipation>) CollectionUtils.select(
                getParticipationsSupervisedByUser( user, communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isActive( (UserParticipation) object, communityService );
                    }
                }
        );
    }

    @Override
    public Boolean isActive( UserParticipation userParticipation, CommunityService communityService ) {
        Agent agent = userParticipation.getAgent( communityService );
        if ( agent == null || !userParticipation.isAccepted() ) return false;
        if ( userParticipation.isSupervised( communityService ) ) {
            return userParticipationConfirmationService.isConfirmedByAllSupervisors( userParticipation, communityService );
        } else
            return !( agent.isParticipationRestrictedToEmployed()
                    && !isValidatedByEmployment( userParticipation, communityService ) );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getParticipationsAsAgent( Agent agent, CommunityService communityService ) {
        QUserParticipation qUserParticipation = QUserParticipation.userParticipation;
        String registeredOrganizationUid = agent.getRegisteredOrganizationUid();
        BooleanBuilder bb = new BooleanBuilder();
        bb.and( qUserParticipation.classLabel.eq( UserParticipation.class.getSimpleName() ) )
                .and( qUserParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                .and( qUserParticipation.actorId.eq( agent.getActorId() ) );
        //    bb.and( qUserParticipation.registeredOrganizationUid.eq( agent.getRegisteredOrganizationUid() ) );
        return validate(
                toList(
                        repository.findAll( bb, qUserParticipation.created.desc() )
                ), communityService );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public UserParticipation getParticipation(
            ChannelsUser user,
            Agent agent,
            CommunityService communityService ) {
        QUserParticipation qUserParticipation = QUserParticipation.userParticipation;
        String registeredOrganizationUid = agent.getRegisteredOrganizationUid();
        BooleanBuilder bb = new BooleanBuilder();
        bb.and( qUserParticipation.classLabel.eq( UserParticipation.class.getSimpleName() ) )
                .and( qUserParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                .and( qUserParticipation.participantUsername.eq( user.getUsername() ) )
                .and( qUserParticipation.actorId.eq( agent.getActorId() ) );
        bb.and( qUserParticipation.registeredOrganizationUid.eq( agent.getRegisteredOrganizationUid() ) );
        List<UserParticipation> participationList = validate(
                toList(
                        repository.findAll( bb, qUserParticipation.created.desc() )
                ), communityService );
        return participationList.isEmpty() ? null : participationList.get( 0 );
    }

    @Override
    public Boolean isParticipationNotFull( Agent agent, CommunityService communityService ) {
        if ( agent.isAnyNumberOfParticipants() ) return true;
        else {
            int count = getParticipationsAsAgent( agent, communityService ).size();
            return count < agent.getMaxParticipation();
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void removeParticipation( String username, UserParticipation participation, CommunityService communityService ) {
        QUserParticipation qUserParticipation = QUserParticipation.userParticipation;
        BooleanBuilder bb = new BooleanBuilder();
        bb.and( qUserParticipation.classLabel.eq( UserParticipation.class.getSimpleName() ) );
        if ( communityService != null ) {  // null = wild card -- to be used only when deleting all participation of a user
            bb.and( qUserParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) );
        }
        bb.and( qUserParticipation.participantUsername.eq( participation.getParticipantUsername() ) )
                .and( qUserParticipation.actorId.eq( participation.getActorId() ) );
        List<UserParticipation> participationList = toList( repository.findAll( bb ) );
        for ( UserParticipation userParticipation : participationList ) {
            delete( userParticipation );
        }
        if ( communityService != null ) {
            communityService.clearCache();
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getAllParticipations( CommunityService communityService ) {
        QUserParticipation qUserParticipation = QUserParticipation.userParticipation;
        return validate(
                toList(
                        repository.findAll(
                                qUserParticipation.classLabel.eq( UserParticipation.class.getSimpleName() )
                                        .and( qUserParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                        )
                ), communityService );
    }

    @Override
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
    public void deleteAllParticipations( ChannelsUser user, String username ) {
        // PlanCommunity is null b/c agent-exists validation of participation not wanted.
        for ( UserParticipation participation : getUserParticipations( user, null ) ) {
            removeParticipation( username, participation, null );
        }
    }

    // User already participates as an agent that has an employer in common with the agent of given participation of his/hers.
    private boolean isValidatedByEmployment(
            final UserParticipation userParticipation,
            final CommunityService communityService ) {
        final Agent agent = userParticipation.getAgent( communityService );
        if ( agent == null ) return false;
        final List<Agency> employers = participationManager.findAllEmployersOfAgent( agent, communityService );
        return CollectionUtils.exists(
                getUserParticipations( new ChannelsUser( userParticipation.getParticipant( communityService ) ), communityService ),  // assuming, perhaps wrongly, they are all valid to avoid infinite loops from isValid(...)
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
    public List<Agent> listAgentsUserParticipatesAs( ChannelsUser user, CommunityService communityService ) {
        Set<Agent> agents = new HashSet<Agent>();
        List<UserParticipation> participationList = getActiveUserParticipations( user, communityService );
        for ( UserParticipation participation : participationList ) {
            agents.add( participation.getAgent( communityService ) );
        }
        return new ArrayList<Agent>( agents );
    }

    @Override
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
    public List<String> listSupervisorsToNotify( UserParticipation userParticipation, CommunityService communityService ) {
        List<String> usernames = new ArrayList<String>();
        if ( userParticipation.isSupervised( communityService ) ) {
            Agent agent = userParticipation.getAgent( communityService );
            if ( agent != null ) {
                List<String> usernamesNotified = userParticipation.usersNotifiedToValidate();
                for ( Agent supervisor : participationManager.findAllSupervisorsOf( agent, communityService ) ) {
                    if ( !userParticipationConfirmationService.isConfirmedBy( userParticipation, supervisor ) ) {
                        for ( UserRecord supervisorUser : findUsersActivelyParticipatingAs( supervisor, communityService ) ) {
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
    @SuppressWarnings( "unchecked" )
    public List<UserRecord> findUsersActivelyParticipatingAs( Agent agent, CommunityService communityService ) {
        List<UserParticipation> participationList = findAllUserParticipationsAs( agent, communityService );
        Set<UserRecord> userInfos = new HashSet<UserRecord>();
        for ( UserParticipation participation : participationList ) {
            if ( isActive( participation, communityService ) ) {
                userInfos.add( participation.getParticipant( communityService ) );
            }
        }
        return new ArrayList<UserRecord>( userInfos );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<UserRecord> findUsersParticipatingAs( Agent agent, CommunityService communityService ) {
        List<UserParticipation> participationList = findAllUserParticipationsAs( agent, communityService );
        Set<UserRecord> userInfos = new HashSet<UserRecord>();
        for ( UserParticipation participation : participationList ) {
            userInfos.add( participation.getParticipant( communityService ) );
        }
        return new ArrayList<UserRecord>( userInfos );
    }

    private List<UserParticipation> findAllUserParticipationsAs( Agent agent, CommunityService communityService ) {
        QUserParticipation qUserParticipation = QUserParticipation.userParticipation;
        BooleanBuilder bb = new BooleanBuilder();
        bb.and( qUserParticipation.classLabel.eq( UserParticipation.class.getSimpleName() ) )
                .and( qUserParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                .and( qUserParticipation.actorId.eq( agent.getActorId() ) );
        bb.and( qUserParticipation.registeredOrganizationUid.eq( agent.getRegisteredOrganizationUid() ) );
        return validate(
                toList(
                        repository.findAll( bb, qUserParticipation.created.desc() )
                ), communityService );
    }

    @Override
    public Boolean deleteParticipation( ChannelsUser user, Agent agent, CommunityService communityService ) {
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
        QUserParticipation qUserParticipation = QUserParticipation.userParticipation;
        String registeredOrganizationUid
                = participation.getRegisteredOrganizationUid();
        BooleanBuilder bb = new BooleanBuilder();
        bb.and( qUserParticipation.classLabel.eq( UserParticipation.class.getSimpleName() ) )
                .and( qUserParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                .and( qUserParticipation.participantUsername.eq( participation.getParticipantUsername() ) )
                .and( qUserParticipation.actorId.eq( participation.getActorId() ) );
        bb.and( qUserParticipation.registeredOrganizationUid.eq( participation.getRegisteredOrganizationUid() ) );
        return validate(
                toList(
                        repository.findAll( bb, qUserParticipation.created.desc() )
                ), communityService );

    }

    @Override
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
    public Boolean isUserActivelyParticipatingAs( ChannelsUser user, Agent agent, CommunityService communityService ) {
        return listAgentsUserParticipatesAs( user, communityService ).contains( agent );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> listUserParticipationIn( OrganizationParticipation organizationParticipation,
                                                            CommunityService communityService ) {
        String registeredOrganizationUid = organizationParticipation.getRegisteredOrganizationUid();
        Organization placeholder = organizationParticipation.getPlaceholderOrganization( communityService );
        if ( placeholder == null ) {
            return new ArrayList<UserParticipation>();
        } else {
            QUserParticipation qUserParticipation = QUserParticipation.userParticipation;
            List<UserParticipation> userParticipationList = validate(
                    toList(
                            repository.findAll(
                                    qUserParticipation.classLabel.eq( UserParticipation.class.getSimpleName() )
                                            .and( qUserParticipation.communityUri.eq( communityService.getPlanCommunity().getUri() ) )
                                            .and( qUserParticipation.registeredOrganizationUid.eq( registeredOrganizationUid ) )
                            )
                    ), communityService );
            final List<Long> actorIds = (List<Long>) CollectionUtils.collect(
                    placeholder.getJobs(),
                    new Transformer() {
                        @Override
                        public Object transform( Object input ) {
                            return ( (Job) input ).getActor().getId();
                        }
                    } );
            return (List<UserParticipation>) CollectionUtils.select(
                    userParticipationList,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return actorIds.contains( ( (UserParticipation) object ).getActorId() );
                        }
                    }
            );
        }
    }


    @Override
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> listUserParticipationsAwaitingConfirmationBy(
            ChannelsUser user,
            final CommunityService communityService ) {
        final List<UserParticipationConfirmation> allConfirmations =
                communityService.getUserParticipationConfirmationService().getParticipationConfirmations( communityService );
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
                                        return confirmation.getUserParticipation( communityService )
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
    public Boolean isValid( UserParticipation userParticipation, CommunityService communityService ) {
        return userParticipation != null
                && userParticipation.getParticipant( communityService ) != null
                && communityService.getPlanService().exists( Actor.class, userParticipation.getActorId(), userParticipation.getCreated() )
                && registeredOrganizationService.isValid( userParticipation.getRegisteredOrganization( communityService ), communityService );
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
