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
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.communities.OrganizationParticipationService;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    private PlanManager planManager;

    @Autowired
    private PlanCommunityManager planCommunityManager;

    @Autowired
    private CommunityServiceFactory communityServiceFactory;

    @Autowired
    private ParticipationAnalyst participationAnalyst;

    @Autowired
    private UserRecordService userRecordService;

    private List<Agency> allAgencies;

    private List<Agent> allAgents;


    public ParticipationManagerImpl() {
    }

    public void clearCache() {
        allAgencies = null;
        allAgents = null;
    }

    public ParticipationAnalyst getParticipationAnalyst() {
        return participationAnalyst;
    }

    public void setParticipationAnalyst( ParticipationAnalyst participationAnalyst ) {
        this.participationAnalyst = participationAnalyst;
    }

    @Override
    public List<Agency> getAllKnownAgencies( CommunityService communityService ) {
        if ( allAgencies == null ) {
            Set<Agency> agencies = new HashSet<Agency>();
            for ( RegisteredOrganization registeredOrganization
                    : registeredOrganizationService.getAllRegisteredOrganizations( communityService ) ) {
                agencies.add( new Agency( registeredOrganization, communityService ) );
            }
            allAgencies = new ArrayList<Agency>( agencies );
        }
        return allAgencies;
    }

    @Override
    @SuppressWarnings( "unchecked" )
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
    public List<Agent> getAllKnownAgents( CommunityService communityService ) {
        if ( allAgents == null ) {
            Set<Agent> agents = new HashSet<Agent>();
            for ( Agency agency : getAllKnownAgencies( communityService ) ) {
                agents.addAll( agency.getAgents( communityService ) );
            }
            allAgents = new ArrayList<Agent>( agents );
        }
        return allAgents;
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
    @SuppressWarnings( "unchecked" )
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
        List<UserParticipation> currentParticipations = userParticipationService.getUserParticipations(
                user,
                communityService );
        List<UserParticipation> activeParticipations = userParticipationService.getActiveUserParticipations(
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
    @SuppressWarnings( "unchecked" )
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
    @SuppressWarnings( "unchecked" )
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
    @SuppressWarnings( "unchecked" )
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
        final List<Agent> userAgents = userParticipationService.listAgentsUserParticipatesAs( user, communityService );
        final List<Agent> otherUserAgents = userParticipationService.listAgentsUserParticipatesAs( otherUser, communityService );
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
        final List<Agent> userAgents = userParticipationService.listAgentsUserParticipatesAs( user, communityService );
        final List<Agent> otherUserAgents = userParticipationService.listAgentsUserParticipatesAs( otherUser, communityService );
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
        final List<Agent> userAgents = userParticipationService.listAgentsUserParticipatesAs( user, communityService );
        List<Agent> supervisorAgents = findAllSupervisorsOf( participationAgent, communityService );
        return !CollectionUtils.intersection( userAgents, supervisorAgents ).isEmpty();
    }


    @Override
    public Plan getPlan( String communityUri, int planVersion ) {
        return planManager.getPlan( communityUri, planVersion ); // todo - change when single planCommunity is not implied by plan
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Agent> findAllUnassignedAgents( final CommunityService communityService ) {
        return (List<Agent>) CollectionUtils.select(
                getAllKnownAgents( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Agent agent = (Agent) object;
                        return !CollectionUtils.exists(
                                userParticipationService.getAllParticipations( communityService ),
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

    @SuppressWarnings( "unchecked" )
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
    @SuppressWarnings( "unchecked" )
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
        for ( UserRecord userRecord : userParticipationService.findUsersParticipatingAs( agent, communityService ) ) {
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
        if ( !userParticipationService.getUserParticipations( user, communityService ).isEmpty() ) {
            return false;
        } else {
            userRecordService.leaveCommunity( user, communityService.getPlanCommunity() );
            return true;
        }
    }

    @Override
    public Boolean isUserParticipatingAsAgents( ChannelsUser user, CommunityService communityService ) {
        return !userParticipationService.getUserParticipations( user, communityService ).isEmpty();
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


}
