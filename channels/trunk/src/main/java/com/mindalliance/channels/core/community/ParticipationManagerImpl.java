package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.communities.UserParticipationConfirmation;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.communities.OrganizationParticipationService;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import com.mindalliance.channels.db.services.communities.UserParticipationConfirmationService;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Plan participation manager implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/12
 * Time: 2:35 PM
 */
public class ParticipationManagerImpl implements ParticipationManager {

    @Autowired
    private UserParticipationService userParticipationService;

    @Autowired
    private RegisteredOrganizationService registeredOrganizationService;

    @Autowired
    private OrganizationParticipationService organizationParticipationService;

    @Autowired
    private UserParticipationConfirmationService userParticipationConfirmationService;

    @Autowired
    private PlanManager planManager;

    @Autowired
    private PlanCommunityManager planCommunityManager;

    @Autowired
    private CommunityServiceFactory communityServiceFactory;

    @Autowired
    private ParticipationAnalyst participationAnalyst;

    @Autowired
    private UserRecordService userRecordService;

    private Map<String, List<Agency>> allAgencies;

    private Map<String, List<Agent>> allAgents;

    private Map<String, List<UserParticipation>> allLinkedUserParticipations;


    public ParticipationManagerImpl() {
    }

    public void clearCache() {
        allAgencies = null;
        allAgents = null;
        allLinkedUserParticipations = null;
    }

    @Override
    public List<UserParticipation> getUserParticipations( ChannelsUser user, CommunityService communityService ) {
        Set<UserParticipation> allUserParticipations = new HashSet<UserParticipation>();
        allUserParticipations.addAll( userParticipationService.getUserParticipations( user, communityService ) );
        allUserParticipations.addAll( findAllLinkedUserParticipations( user, communityService ) ); // always active
        return new ArrayList<UserParticipation>( allUserParticipations );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getActiveUserParticipations( ChannelsUser user,
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
    public Boolean isActive( UserParticipation userParticipation, CommunityService communityService ) {
        if ( userParticipation.isLinked() ) return true;
        Agent agent = userParticipation.getAgent( communityService );
        if ( agent == null || !userParticipation.isAccepted() ) return false;
        if ( userParticipation.isSupervised( communityService ) ) {
            return userParticipationConfirmationService.isConfirmedByAllSupervisors( userParticipation, communityService );
        } else
            return !( agent.isParticipationRestrictedToEmployed()
                    && !isValidatedByEmployment( userParticipation, communityService ) );
    }

    // User already participates as an agent that has an employer in common with the agent of given participation of his/hers.
    private boolean isValidatedByEmployment(
            final UserParticipation userParticipation,
            final CommunityService communityService ) {
        final Agent agent = userParticipation.getAgent( communityService );
        if ( agent == null ) return false;
        final List<Agency> employers = findAllEmployersOfAgent( agent, communityService );
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
                                    findAllEmployersOfAgent( otherAgent, communityService );
                            return !CollectionUtils.intersection( employers, otherEmployers ).isEmpty();
                        }
                    }
                }
        );
    }


    @Override
    @SuppressWarnings( "unchecked" )
    public List<UserParticipation> getActiveUserSupervisedParticipations( ChannelsUser user,
                                                                          final CommunityService communityService ) {
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
                        List<Agent> supervisors = findAllSupervisorsOf(
                                participationAgent,
                                communityService );
                        if ( supervisors.isEmpty() ) return false;
                        List<Agent> userAgents = listAgentsUserParticipatesAs( user, communityService );
                        return !Collections.disjoint( supervisors, userAgents );
                    }
                } );
    }


    @Override
    public List<UserParticipation> getParticipationsAsAgent( Agent agent, CommunityService communityService ) {
        Set<UserParticipation> allUserParticipations = new HashSet<UserParticipation>();
        allUserParticipations.addAll( userParticipationService.getParticipationsAsAgent( agent, communityService ) );
        allUserParticipations.addAll( findAllLinkedUserParticipationsAsAgent( agent, communityService ) );  // always active
        return new ArrayList<UserParticipation>( allUserParticipations );
    }

    @Override
    public UserParticipation getParticipation( ChannelsUser user, Agent agent, CommunityService communityService ) {
        UserParticipation userParticipation = userParticipationService.getParticipation( user, agent, communityService );
        return userParticipation != null
                ? userParticipation
                : getLinkedParticipation( user, agent, communityService );
    }

    @Override
    public List<UserParticipation> getAllParticipations( CommunityService communityService ) {
        Set<UserParticipation> allUserParticipations = new HashSet<UserParticipation>();
        allUserParticipations.addAll( userParticipationService.getAllParticipations( communityService ) );
        allUserParticipations.addAll( findAllLinkedParticipations( communityService ) );  // always active
        return new ArrayList<UserParticipation>( allUserParticipations );
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
    public List<Agent> listAgentsUserParticipatesAs( ChannelsUser user, CommunityService communityService ) {
        Set<Agent> agents = new HashSet<Agent>();
        List<UserParticipation> participationList = getActiveUserParticipations( user, communityService );
        for ( UserParticipation participation : participationList ) {
            agents.add( participation.getAgent( communityService ) );
        }
        return new ArrayList<Agent>( agents );
    }

    @Override
    public Boolean isUserActivelyParticipatingAs( ChannelsUser user, Agent agent, CommunityService communityService ) {
        return listAgentsUserParticipatesAs( user, communityService ).contains( agent );
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
                    findAllSupervisorsOf( participationAgent, communityService );
            supervisorsUserParticipatesAs.addAll( CollectionUtils.intersection(
                    agentsUserParticipatesAs,
                    allSupervisorsOfAgent ) );
        }
        return supervisorsUserParticipatesAs;
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
    @SuppressWarnings( "unchecked" )
    public List<UserRecord> findUsersActivelyParticipatingAs( Agent agent, CommunityService communityService ) {
        List<UserParticipation> participationList = getParticipationsAsAgent( agent, communityService );
        Set<UserRecord> userInfos = new HashSet<UserRecord>();
        for ( UserParticipation participation : participationList ) {
            if ( isActive( participation, communityService ) ) {
                userInfos.add( participation.getParticipant( communityService ) );
            }
        }
        return new ArrayList<UserRecord>( userInfos );
    }

    @Override
    public List<String> listSupervisorsToNotify( UserParticipation userParticipation, CommunityService communityService ) {
        List<String> usernames = new ArrayList<String>();
        if ( userParticipation.isSupervised( communityService ) ) {
            Agent agent = userParticipation.getAgent( communityService );
            if ( agent != null ) {
                List<String> usernamesNotified = userParticipation.usersNotifiedToValidate();
                for ( Agent supervisor : findAllSupervisorsOf( agent, communityService ) ) {
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
    public List<UserRecord> findUsersParticipatingAs( Agent agent, CommunityService communityService ) {
        List<UserParticipation> participationList = getParticipationsAsAgent( agent, communityService );
        Set<UserRecord> userInfos = new HashSet<UserRecord>();
        for ( UserParticipation participation : participationList ) {
            userInfos.add( participation.getParticipant( communityService ) );
        }
        return new ArrayList<UserRecord>( userInfos );
    }


    @SuppressWarnings( "unchecked" )
    private synchronized List<UserParticipation> findAllLinkedParticipations( final CommunityService communityService ) {
        String uri = communityService.getPlanCommunity().getUri();
        if ( allLinkedUserParticipations == null ) {
            allLinkedUserParticipations = new HashMap<String, List<UserParticipation>>();
        }
        if ( allLinkedUserParticipations.get( uri ) == null ) {
            List<UserParticipation> linkedUserParticipationList = new ArrayList<UserParticipation>();
            allLinkedUserParticipations.put( uri, linkedUserParticipationList );
            // Find all active, primary user participations
            List<UserParticipation> primaryActiveUserParticipations = findAllActivePrimaryUserParticipations( communityService );
            userParticipationService.getAllParticipations( communityService );
            PlanCommunity planCommunity = communityService.getPlanCommunity();
            for ( Agency agency : getAllKnownAgencies( communityService ) ) {
                for ( Job job : agency.getAllJobs( communityService ) ) {
                    if ( job.isLinked() ) {
                        Agent agent = new Agent( job.getActor(), agency.getRegisteredOrganization(), communityService );
                        for ( UserParticipation primaryActive : primaryActiveUserParticipations ) {
                            if ( primaryActive.getActorId() == agent.getActorId() ) {
                                UserParticipation linkedUserParticipation = new UserParticipation(
                                        ChannelsUser.ANONYMOUS_USERNAME,
                                        new ChannelsUser( primaryActive.getParticipant( communityService ) ),
                                        agent,
                                        planCommunity
                                );
                                linkedUserParticipation.setLinked( true );
                                if ( !linkedUserParticipationList.contains( linkedUserParticipation ) ) {
                                    linkedUserParticipationList.add( linkedUserParticipation );
                                }
                            }
                        }
                    }
                }
            }

        }
        return allLinkedUserParticipations.get( uri );
    }

    @SuppressWarnings( "unchecked" )
    private List<UserParticipation> findAllActivePrimaryUserParticipations( final CommunityService communityService ) {
        return (List<UserParticipation>) CollectionUtils.select(
                userParticipationService.getAllParticipations( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isActive( (UserParticipation) object, communityService );
                    }
                }
        );
    }


    @SuppressWarnings( "unchecked" )
    private List<UserParticipation> findAllLinkedUserParticipations( ChannelsUser user,
                                                                     CommunityService communityService ) {
        final String username = user.getUsername();
        return (List<UserParticipation>) CollectionUtils.select(
                findAllLinkedParticipations( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation linkedUserParticipation = (UserParticipation) object;
                        return linkedUserParticipation.getParticipantUsername().equals( username );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<UserParticipation> findAllLinkedUserParticipationsAsAgent( Agent agent,
                                                                            CommunityService communityService ) {
        final long actorId = agent.getActorId();
        final String registereOrganizationUid = agent.getRegisteredOrganizationUid();
        return (List<UserParticipation>) CollectionUtils.select(
                findAllLinkedParticipations( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation linkedUserParticipation = (UserParticipation) object;
                        return linkedUserParticipation.getActorId() == actorId
                                && linkedUserParticipation.getRegisteredOrganizationUid().equals( registereOrganizationUid );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private UserParticipation getLinkedParticipation( ChannelsUser user, Agent agent, CommunityService communityService ) {
        final String username = user.getUsername();
        final long actorId = agent.getActorId();
        final String registereOrganizationUid = agent.getRegisteredOrganizationUid();
        List<UserParticipation> results = (List<UserParticipation>) CollectionUtils.select(
                findAllLinkedParticipations( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        UserParticipation linkedUserParticipation = (UserParticipation) object;
                        return linkedUserParticipation.getActorId() == actorId
                                && linkedUserParticipation.getRegisteredOrganizationUid().equals( registereOrganizationUid )
                                && linkedUserParticipation.getParticipantUsername().equals( username );
                    }
                }
        );
        return results.isEmpty()
                ? null
                : results.get( 0 );
    }

    public ParticipationAnalyst getParticipationAnalyst() {
        return participationAnalyst;
    }

    public void setParticipationAnalyst( ParticipationAnalyst participationAnalyst ) {
        this.participationAnalyst = participationAnalyst;
    }

    @Override
    public synchronized List<Agency> getAllKnownAgencies( CommunityService communityService ) {
        String uri = communityService.getPlanCommunity().getUri();
        if ( allAgencies == null ) {
            allAgencies = new HashMap<String, List<Agency>>();
        }
        if ( allAgencies.get( uri ) == null ) {
            Set<Agency> agencies = new HashSet<Agency>();
            for ( RegisteredOrganization registeredOrganization
                    : registeredOrganizationService.getAllRegisteredOrganizations( communityService ) ) {
                agencies.add( new Agency( registeredOrganization, communityService ) );
            }
            allAgencies.put( uri, new ArrayList<Agency>( agencies ) );
        }
        return allAgencies.get( uri );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Agency> findAgenciesParticipatingAs( final Organization placeholder, final CommunityService communityService ) {
        return (List<Agency>) CollectionUtils.select(
                getAllKnownAgencies( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Agency) object ).participatesAsPlaceholder( placeholder );
                    }
                }
        );
    }

    @Override
    public Agency findAgencyNamed( final String agencyName, CommunityService communityService ) {
        return (Agency) CollectionUtils.find(
                getAllKnownAgencies( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Agency) object ).getName().equals( agencyName );
                    }
                }
        );
    }

    @Override
    public synchronized List<Agent> getAllKnownAgents( CommunityService communityService ) {
        String uri = communityService.getPlanCommunity().getUri();
        if ( allAgents == null ) {
            allAgents = new HashMap<String, List<Agent>>();
        }
        if ( allAgents.get( uri ) == null ) {
            Set<Agent> agents = new HashSet<Agent>();
            for ( Agency agency : getAllKnownAgencies( communityService ) ) {
                agents.addAll( agency.getAgents( communityService ) );
            }
            allAgents.put( uri, new ArrayList<Agent>( agents ) );
        }
        return allAgents.get( uri );
    }

    @Override
    public Agent findAgentNamed( final String name, CommunityService communityService ) {
        return (Agent) CollectionUtils.find(
                getAllKnownAgents( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Agent) object ).getName().equals( name );
                    }
                }
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Agent> findSelfAssignableOpenAgents( final CommunityService communityService, final ChannelsUser user ) {
        return (List<Agent>) CollectionUtils.select(
                getAllKnownAgents( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return isParticipationSelfAssignable(
                                (Agent) object,
                                user,
                                communityService );
                    }
                }
        );
    }

    @Override
    public boolean isParticipationAvailable( Agent agent, ChannelsUser user, CommunityService communityService ) {
        List<UserParticipation> currentParticipations = getUserParticipations(
                user,
                communityService );
        List<UserParticipation> activeParticipations = getActiveUserParticipations(
                user,
                communityService );
        return agent.isValid( communityService )
                && !alreadyParticipatingAs( agent, currentParticipations )
                && !isParticipationFull( agent, communityService )
                && meetsPreEmploymentConstraint( agent, activeParticipations, communityService );
    }

    @Override
    public boolean isParticipationSelfAssignable( Agent agent, ChannelsUser user, CommunityService communityService ) {
        return agent.isParticipationUserAssignable()
                && isParticipationAvailable( agent, user, communityService );
    }

    @Override
    public boolean meetsPreEmploymentConstraint(
            Agent agent,
            List<UserParticipation> activeParticipations,
            CommunityService communityService ) {
        if ( !agent.isParticipationRestrictedToEmployed() ) return true;
        List<Agency> agentEmployers = findAllEmployersOfAgent( agent, communityService );
        List<Agency> participationEmployers = new ArrayList<Agency>();
        for ( UserParticipation participation : activeParticipations ) {
            Agent participationAgent = participation.getAgent( communityService );
            if ( participationAgent != null && !participationAgent.isOpenParticipation() )
                participationEmployers.addAll( findAllEmployersOfAgent( participationAgent, communityService ) );
        }
        return !Collections.disjoint( agentEmployers, participationEmployers );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Agent> findAllSupervisorsOf( Agent agent, CommunityService communityService ) {
        List<Agent> allOtherAgents = new ArrayList<Agent>( getAllKnownAgents( communityService ) );
        allOtherAgents.remove( agent );
        final Agency agency = agent.getAgency();
        final List<Agency> ancestorAgencies = agent.getAgency().ancestors( communityService );
        final List<Actor> supervisorActors = communityService.getPlanService().findAllSupervisorsOf( agent.getActor() );
        return (List<Agent>) CollectionUtils.select(
                allOtherAgents,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Agent other = (Agent) object;
                        Agency othersAgency = other.getAgency();
                        return supervisorActors.contains( other.getActor() )
                                && ( othersAgency.equals( agency ) || ancestorAgencies.contains( othersAgency ) );
                    }
                }
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Agency> findAllEmployersOfAgent( final Agent agent, final CommunityService communityService ) {
        return (List<Agency>) CollectionUtils.select(
                getAllKnownAgencies( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Agency) object ).getAgents( communityService ).contains( agent );
                    }
                }
        );
    }

    @Override
    public List<CommunityEmployment> findAllEmploymentsForAgent( Agent agent, CommunityService communityService ) {
        List<CommunityEmployment> communityEmployments = new ArrayList<CommunityEmployment>();
        List<Agency> employers = findAllEmployersOfAgent( agent, communityService );
        for ( Agency agency : employers ) {
            for ( Job job : agency.getAllJobsFor( agent, communityService ) ) {
                communityEmployments.add( new CommunityEmployment( job, agent, communityService ) );
            }
        }
        return communityEmployments;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CommunityEmployment> findAllEmploymentsBy( Agency agency, CommunityService communityService ) {
        List<CommunityEmployment> employments = new ArrayList<CommunityEmployment>();
        List<Job> allJobs = agency.getAllJobs( communityService );
        for ( final Agent agent : agency.getAgents( communityService ) ) {
            List<Job> agentJobs = (List<Job>) CollectionUtils.select(
                    allJobs,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Job) object ).getActor().equals( agent.getActor() );
                        }
                    }
            );
            for ( Job job : agentJobs ) {
                employments.add( new CommunityEmployment( job, agent, communityService ) );
            }
        }
        return employments;
    }


    private boolean alreadyParticipatingAs( final Agent agent,
                                            List<UserParticipation> currentParticipations ) {
        return CollectionUtils.exists(
                currentParticipations,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (UserParticipation) object ).isForAgent( agent );
                    }
                } );
    }

    private boolean isParticipationFull( Agent agent, CommunityService communityService ) {
        return !userParticipationService.isParticipationNotFull( agent, communityService );
    }

    @Override
    public boolean areCollaborators(   // todo
                                       CommunityService communityService,
                                       final ChannelsUser user,
                                       final ChannelsUser otherUser ) {  // Does one have commitments or co-assignments with the other?
        return false;
    }

    @Override
    public boolean isSupervisorOf(
            final CommunityService communityService,
            ChannelsUser user,
            ChannelsUser otherUser ) {
        final List<Agent> userAgents = listAgentsUserParticipatesAs( user, communityService );
        final List<Agent> otherUserAgents = listAgentsUserParticipatesAs( otherUser, communityService );
        return CollectionUtils.exists(
                otherUserAgents,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Agent otherUserAgent = (Agent) object;
                        List<Agent> supervisorsOfOtherUser = findAllSupervisorsOf( otherUserAgent, communityService );
                        return !CollectionUtils.intersection( supervisorsOfOtherUser, userAgents ).isEmpty();
                    }
                }
        );
    }

    @Override
    public boolean isSupervisedBy(
            final CommunityService communityService,
            ChannelsUser user,
            ChannelsUser otherUser ) {
        return isSupervisorOf( communityService, otherUser, user );
    }

    @Override
    public boolean areColleagues(   // have a common employer
                                    CommunityService communityService,
                                    ChannelsUser user,
                                    ChannelsUser otherUser ) {
        final List<Agent> userAgents = listAgentsUserParticipatesAs( user, communityService );
        final List<Agent> otherUserAgents = listAgentsUserParticipatesAs( otherUser, communityService );
        Set<Agency> userEmployers = new HashSet<Agency>();
        for ( Agent userAgent : userAgents ) {
            userEmployers.addAll( findAllEmployersOfAgent( userAgent, communityService ) );
        }
        Set<Agency> otherUserEmployers = new HashSet<Agency>();
        for ( Agent otherUserAgent : otherUserAgents ) {
            otherUserEmployers.addAll( findAllEmployersOfAgent( otherUserAgent, communityService ) );
        }
        return !CollectionUtils.intersection( userEmployers, otherUserEmployers ).isEmpty();
    }

    @Override
    public boolean hasAuthorityOverParticipation(
            final CommunityService communityService,
            ChannelsUser user,
            UserParticipation userParticipation ) {
        return hasAuthorityOverParticipation(
                communityService,
                user,
                userParticipation.getParticipant( communityService ),
                userParticipation.getAgent( communityService )
        );
    }

    @Override
    public boolean hasAuthorityOverParticipation(
            final CommunityService communityService,
            ChannelsUser user,
            UserRecord participantInfo,
            Agent participationAgent ) {
        // A user can unilaterally terminate his/her own participation.
        if ( user.getUserRecord().equals( participantInfo ) ) return true;
        // else, does the user participate as one of the agents supervising the participation agent?
        final List<Agent> userAgents = listAgentsUserParticipatesAs( user, communityService );
        List<Agent> supervisorAgents = findAllSupervisorsOf( participationAgent, communityService );
        return !CollectionUtils.intersection( userAgents, supervisorAgents ).isEmpty();
    }


    @Override
    public Plan getPlan( String communityUri, int planVersion ) {
        return planManager.getPlan( communityUri, planVersion );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Agent> findAllUnassignedAgents( final CommunityService communityService ) {
        return (List<Agent>) CollectionUtils.select(
                getAllKnownAgents( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Agent agent = (Agent) object;
                        return !CollectionUtils.exists(
                                getAllParticipations( communityService ),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        return ( (UserParticipation) object ).getAgent( communityService ).equals( agent );
                                    }
                                }
                        );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    private List<Organization> findAllPlaceholders( CommunityService communityService ) {
        return (List<Organization>) CollectionUtils.select(
                communityService.getPlanService().listActualEntities( Organization.class, true ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Organization) object ).isPlaceHolder();
                    }
                } );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Organization> findAllUnassignedPlaceholders( final CommunityService communityService ) {
        return (List<Organization>) CollectionUtils.select(
                findAllPlaceholders( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Organization placeholder = (Organization) object;
                        return organizationParticipationService
                                .listAgenciesParticipatingAs( placeholder, communityService ).isEmpty();
                    }
                }
        );
    }

    @Override
    public RegisteredOrganization getTopRegisteredOrganization( RegisteredOrganization registeredOrganization,
                                                                CommunityService communityService ) {
        List<RegisteredOrganization> ancestors = registeredOrganizationService.findAncestors(
                registeredOrganization.getName( communityService ),
                communityService );
        if ( ancestors.isEmpty() )
            return registeredOrganization;
        else
            return ancestors.get( ancestors.size() - 1 );
    }

    public List<RegisteredOrganization> ancestorsOf( RegisteredOrganization registeredOrganization,
                                                     CommunityService communityService ) {
        return registeredOrganizationService.findAncestors(
                registeredOrganization.getName( communityService ),
                communityService );
    }

    @Override
    public Agency findAgencyById( final String id, CommunityService communityService ) throws NotFoundException {
        return (Agency) CollectionUtils.find(
                getAllKnownAgencies( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Agency) object ).getUid().equals( id );
                    }
                }
        );
    }

    @Override
    public OrganizationParticipation getOrganizationParticipation( String uid ) {
        return uid == null ? null : organizationParticipationService.load( uid );
    }

    @Override
    public RegisteredOrganization getRegisteredOrganization( String uid ) {
        return registeredOrganizationService.load( uid );
    }

    @Override
    public UserParticipation getUserParticipation( String uid ) {
        return userParticipationService.load( uid );
    }

    @Override
    public boolean isAgencyReferenced( final Agency agency, final CommunityService communityService ) {
        boolean participates = agency.isLocal()
                ? !organizationParticipationService.findAllParticipationBy( agency.getRegisteredOrganization(), communityService ).isEmpty()
                : !organizationParticipationService.findAllParticipationByGlobal( agency.getRegisteredOrganization() ).isEmpty();
        return participates
                || CollectionUtils.exists(
                getAllKnownAgencies( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Agency parentAgency = ( (Agency) object ).getParent( communityService );
                        return parentAgency != null && parentAgency.equals( agency );
                    }
                }
        );
    }

    @Override
    public Boolean canBeMadeGlobal( Agency agency, CommunityService communityService ) {
        if ( agency.getRegisteredOrganization().isFixedOrganization() || agency.isGlobal() ) return false;
        Agency parentAgency = agency.getParent( communityService );
        return parentAgency == null || parentAgency.isGlobal();
    }

    @Override
    public Boolean canBeMadeLocal( Agency agency, CommunityService communityService ) {
        if ( agency.getRegisteredOrganization().isFixedOrganization() || agency.isLocal() ) return false;
        final String uri = communityService.getPlanCommunity().getUri();
        return !CollectionUtils.exists(
                organizationParticipationService.findAllParticipationByGlobal( agency.getRegisteredOrganization() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        OrganizationParticipation participation = (OrganizationParticipation) object;
                        return !participation.getCommunityUri().equals( uri );
                    }
                }
        );
    }

    @Override
    // Whether the actor mapped to by the agent has a primary job in the organization mapped to by the agency.
    // Assumed: If an agent has one or more jobs, it must have exactly one primary job.
    public Boolean isDirectParticipationAllowed( Agent agent, final Agency agency, CommunityService communityService ) {
        return CollectionUtils.exists(
                agency.getAllJobsFor( agent, communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Job) object ).isPrimary();
                    }
                } );
    }

    @Override
    public CommunityEmployment findDirectParticipationEmploymentForParticipationAs( Agent agent, CommunityService communityService ) {
        return (CommunityEmployment) CollectionUtils.find(
                findAllEmploymentsForAgent(
                        agent,
                        communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (CommunityEmployment) object ).getJob().isPrimary();
                    }
                } );
    }

    @Override
    public List<ChannelsUser> findAllUsersParticipatingAs( Agent agent, CommunityService communityService ) {
        List<ChannelsUser> participants = new ArrayList<ChannelsUser>();
        for ( UserRecord userRecord : findUsersParticipatingAs( agent, communityService ) ) {
            participants.add( new ChannelsUser( userRecord ) );
        }
        return participants;
    }

    @Override
    public Boolean userHasJoinedCommunity( ChannelsUser user, CommunityService communityService ) {
        String uri = communityService.getPlanCommunity().getUri();
        return user.getUserRecord().getCommunitiesJoined().contains( uri ) // legacy - implied participation if planner
                || user.isCommunityPlanner( uri )
                || isUserParticipatingAsAgents( user, communityService ); // legacy - implied participation in plan when already participating as agent(s)
    }

    @Override
    public boolean joinCommunity( ChannelsUser user, CommunityService communityService ) {
        if ( userHasJoinedCommunity( user, communityService ) ) {
            return false;
        } else {
            userRecordService.joinCommunity( user, communityService.getPlanCommunity() );
            communityService.clearCache();
            return true;
        }
    }

    @Override
    public boolean leaveCommunity( ChannelsUser user, CommunityService communityService ) {
        if ( !getUserParticipations( user, communityService ).isEmpty() ) {
            return false;
        } else {
            userRecordService.leaveCommunity( user, communityService.getPlanCommunity() );
            return true;
        }
    }

    @Override
    public Boolean isUserParticipatingAsAgents( ChannelsUser user, CommunityService communityService ) {
        return !getUserParticipations( user, communityService ).isEmpty();
    }

    public void registerAllFixedOrganizations() {
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            if ( !planCommunity.isDomainCommunity() ) {
                CommunityService communityService = communityServiceFactory.getService( planCommunity );
                // register fixed organizations
                for ( Organization organization : communityService.getPlanService().listActualEntities( Organization.class ) ) {
                    if ( organization.isFixedOrganization() ) {
                        registeredOrganizationService.findOrAdd(
                                ChannelsUser.current(),
                                organization.getName(),
                                false,
                                communityService );
                    }
                }
            }
        }
    }

    @Override
    public boolean isLinked( Agent agent, CommunityService communityService ) {
        return !CollectionUtils.exists(
               findAllEmploymentsForAgent( agent, communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((CommunityEmployment)object).getJob().isPrimary();
                    }
                }
        );
    }


}
