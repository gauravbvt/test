package com.mindalliance.channels.db.services.communities;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import com.mindalliance.channels.db.data.communities.QUserParticipationConfirmation;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.communities.UserParticipationConfirmation;
import com.mindalliance.channels.db.repositories.UserParticipationConfirmationRepository;
import com.mindalliance.channels.db.services.AbstractDataService;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

/**
 * User plan community participation confirmation service implementation.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/20/13
 * Time: 12:07 PM
 */
@Component
public class UserParticipationConfirmationServiceImpl
        extends AbstractDataService<UserParticipationConfirmation>
        implements UserParticipationConfirmationService {

    @Autowired
    private UserParticipationConfirmationRepository repository;

    @Autowired
    private UserParticipationService userParticipationService;

    @Autowired
    private ParticipationManager participationManager;

    @Autowired
    private OrganizationParticipationService organizationParticipationService;

    public UserParticipationConfirmationServiceImpl() {
    }

    @Override
    public UserParticipationConfirmation load( String uid ) {
        return repository.findOne( uid );
    }

    @Override
    public void save( UserParticipationConfirmation userParticipationConfirmation ) {
        repository.save( userParticipationConfirmation );
    }

    private void delete( UserParticipationConfirmation userParticipationConfirmation ) {
        repository.delete( userParticipationConfirmation );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<UserParticipationConfirmation> getParticipationConfirmations(
            CommunityService communityService ) {
        QUserParticipationConfirmation qUserParticipationConfirmation
                = QUserParticipationConfirmation.userParticipationConfirmation;
        return toList(
                repository.findAll(
                        qUserParticipationConfirmation.classLabel.eq( UserParticipationConfirmation.class.getSimpleName() )
                                .and( qUserParticipationConfirmation.communityUri.eq( communityService.getPlanCommunity().getUri() ) ),
                        qUserParticipationConfirmation.created.desc()
                )
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<UserParticipationConfirmation> getParticipationConfirmations(
            UserParticipation userParticipation ) {
        QUserParticipationConfirmation qUserParticipationConfirmation
                = QUserParticipationConfirmation.userParticipationConfirmation;
        return toList(
                repository.findAll(
                        qUserParticipationConfirmation.classLabel.eq( UserParticipationConfirmation.class.getSimpleName() )
                                .and( qUserParticipationConfirmation.userParticipationUid.eq( userParticipation.getUid() ) ),
                        qUserParticipationConfirmation.created.desc()
                )
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<UserParticipationConfirmation> getParticipationConfirmations(
            Agent supervisor,
            CommunityService communityService ) {
        OrganizationParticipation supervisorParticipation = supervisor.getOrganizationParticipation();
        QUserParticipationConfirmation qUserParticipationConfirmation
                = QUserParticipationConfirmation.userParticipationConfirmation;
        BooleanBuilder bb = new BooleanBuilder();
        bb.and( qUserParticipationConfirmation.classLabel.eq( UserParticipationConfirmation.class.getSimpleName() ) )
                .and( qUserParticipationConfirmation.communityUri.eq( communityService.getPlanCommunity().getUri() ) );
        if ( supervisorParticipation == null ) {
            bb.and( qUserParticipationConfirmation.organizationParticipationUid.isNull() );
        } else {
            bb.and( qUserParticipationConfirmation.organizationParticipationUid.eq( supervisorParticipation.getUid() ) );
        }
        return toList(
                repository.findAll(
                        bb,
                        qUserParticipationConfirmation.created.desc()
                )
        );
    }

    @Override
    public void addParticipationConfirmation( UserParticipation userParticipation,
                                              Agent supervisor,
                                              ChannelsUser user,
                                              CommunityService communityService ) {
        UserParticipationConfirmation confirmation = new UserParticipationConfirmation(
                userParticipation,
                supervisor,
                user.getUsername() );
        save( confirmation );
        communityService.clearCache();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void removeParticipationConfirmation( UserParticipation userParticipation,
                                                 Agent supervisor,
                                                 CommunityService communityService ) {
         for ( UserParticipationConfirmation confirmation : getConfirmations( userParticipation, supervisor ) ) {
            delete( confirmation );
        }
        communityService.clearCache();

    }

    @Override
    public Boolean isConfirmedBy( UserParticipation userParticipation, Agent supervisor ) {
         return !getConfirmations( userParticipation, supervisor ).isEmpty();
    }

    private List<UserParticipationConfirmation> getConfirmations( UserParticipation userParticipation,
                                                                  Agent supervisor ) {
        OrganizationParticipation supervisorParticipation = supervisor.getOrganizationParticipation();
        QUserParticipationConfirmation qUserParticipationConfirmation
                = QUserParticipationConfirmation.userParticipationConfirmation;
        BooleanBuilder bb = new BooleanBuilder();
        bb.and( qUserParticipationConfirmation.classLabel.eq( UserParticipationConfirmation.class.getSimpleName() ) )
                .and( qUserParticipationConfirmation.userParticipationUid.eq( userParticipation.getUid() ) )
                .and( qUserParticipationConfirmation.supervisorId.eq( supervisor.getActorId() ) );
        if ( supervisorParticipation == null ) {
            bb.and( qUserParticipationConfirmation.organizationParticipationUid.isNull() );
        } else {
            bb.and( qUserParticipationConfirmation.organizationParticipationUid.eq( supervisorParticipation.getUid() ) );
        }
        return toList(
                repository.findAll(
                        bb,
                        qUserParticipationConfirmation.created.desc()
                )
        );
    }

    @Override
    public void deleteConfirmations( UserParticipation participation, CommunityService communityService ) {
        for ( UserParticipationConfirmation validation : getParticipationConfirmations( participation ) ) {
            delete( validation );
        }
        communityService.clearCache();
    }

    @Override
    public Boolean isConfirmedByAllSupervisors(
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
    public Boolean isConfirmationByUserRequired(
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
    @SuppressWarnings( "unchecked" )
    public List<UserParticipationConfirmation> listUserParticipationsConfirmedBy(
            ChannelsUser user,
            final CommunityService communityService ) {
        final List<UserParticipationConfirmation> allConfirmations =
                communityService.getUserParticipationConfirmationService().getParticipationConfirmations( communityService );
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
    public Boolean isValid( UserParticipationConfirmation confirmation, CommunityService communityService ) {
        return confirmation != null &&
                userParticipationService.isValid( confirmation.getUserParticipation( communityService ), communityService )
                && confirmation.getSupervisor( communityService ) != null
                && organizationParticipationService.isValid( confirmation.getOrganizationParticipation( communityService ), communityService );
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
