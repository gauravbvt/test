package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.communities.OrganizationParticipationService;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
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
    private ParticipationAnalyst participationAnalyst;


    public ParticipationManagerImpl() {
    }

    public ParticipationAnalyst getParticipationAnalyst() {
        return participationAnalyst;
    }

    public void setParticipationAnalyst( ParticipationAnalyst participationAnalyst ) {
        this.participationAnalyst = participationAnalyst;
    }

    @Override
    public List<Agency> getAllKnownAgencies( CommunityService communityService ) {
        Set<Agency> agencies = new HashSet<Agency>();
        PlanService planService = communityService.getPlanService();
        // fixed
        for ( Organization organization : planService.listActualEntities( Organization.class, true ) ) {
            if ( !organization.isPlaceHolder() )
                agencies.add( new Agency( organization ) );
        }
        // registered as placeholder
        agencies.addAll( organizationParticipationService.listParticipatingAgencies( communityService ) );
        // registered by community but not registered as placeholders
        for ( RegisteredOrganization registeredOrganization
                : registeredOrganizationService.getAllRegisteredOrganizations( communityService ) ) {
            if ( !registeredOrganization.isFixedOrganization()
                    && organizationParticipationService.findAllParticipationBy(
                    registeredOrganization,
                    communityService ).isEmpty() ) {
                agencies.add( new Agency( registeredOrganization, communityService ) );
            }
        }
        return new ArrayList<Agency>( agencies );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Agency> findAgenciesParticipatingAs( final Organization placeholder, final CommunityService communityService ) {
        return (List<Agency>) CollectionUtils.select(
                getAllKnownAgencies( communityService ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Organization org = ( (Agency) object ).getPlaceholder( communityService );
                        return org != null && org.equals( placeholder );
                    }
                }
        );
    }

    @Override
    public Agency findAgencyNamed( String agencyName, CommunityService communityService ) {
        PlanService planService = communityService.getPlanService();
        // fixed
        Organization organization = planService.findActualEntity( Organization.class, agencyName );
        if ( organization != null && !organization.isPlaceHolder() )
            return new Agency( organization );
        // registered as placeholder
        for ( Agency agency : organizationParticipationService.listParticipatingAgencies( communityService ) ) {
            if ( agency.getName().equals( agencyName ) )
                return agency;
        }
        // registered by community but not registered as placeholders
        for ( RegisteredOrganization registeredOrganization
                : registeredOrganizationService.getAllRegisteredOrganizations( communityService ) ) {
            if ( !registeredOrganization.isFixedOrganization()
                    && organizationParticipationService.findAllParticipationBy(
                    registeredOrganization,
                    communityService ).isEmpty() ) {
                if ( registeredOrganization.getName( communityService ).equals( agencyName ) )
                    return new Agency( registeredOrganization, communityService );
            }
        }
        return null;
    }

    @Override
    public List<Agent> getAllKnownAgents( CommunityService communityService ) {
        Set<Agent> agents = new HashSet<Agent>();
        PlanService planService = communityService.getPlanService();
        // Fixed actors
        for ( Actor actor : planService.listActualEntities( Actor.class, true ) ) {
            boolean employedInNonPlaceHolder = CollectionUtils.exists(
                    planService.findAllEmploymentsForActor( actor ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return !( (Employment) object ).getOrganization().isPlaceHolder();
                        }
                    }
            );
            if ( employedInNonPlaceHolder ) {
                agents.add( new Agent( actor ) );
            }
        }
        // Registered agents
        for ( Agency agency : organizationParticipationService.listParticipatingAgencies( communityService ) ) {
            if ( agency.isRegisteredByCommunity( communityService ) )
                agents.addAll( agency.getAgents( communityService ) );
        }
        return new ArrayList<Agent>( agents );
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
        PlanService planService = communityService.getPlanService();
        List<Agent> agents = new ArrayList<Agent>();
        // In fixed organizations
        for ( Actor actor : planService.listActualEntities( Actor.class ) ) {
            if ( actorHasNonPlaceholderEmployers( actor, communityService ) ) {
                Agent agent = new Agent( actor );
                if ( isParticipationSelfAssignable(
                        agent,
                        user,
                        communityService ) ) {
                    agents.add( agent );
                }
            }

        }
        // In registered organizations
        for ( Agency agency : organizationParticipationService.listParticipatingAgencies( communityService ) ) {
            for ( Agent agent : agency.getAgents( communityService ) ) {
                if ( isParticipationSelfAssignable(
                        agent,
                        user,
                        communityService ) ) {
                    agents.add( agent );
                }
            }
        }
        return agents;
    }

    private boolean actorHasNonPlaceholderEmployers( Actor actor, CommunityService communityService ) {
        return CollectionUtils.exists(
                communityService.getPlanService().findAllEmploymentsForActor( actor ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (Employment) object ).getOrganization().isPlaceHolder();
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
    public List<Agent> findAllSupervisorsOf( Agent agent, CommunityService communityService ) {
        PlanService planService = communityService.getPlanService();
        Set<Agent> supervisors = new HashSet<Agent>();
        Actor actor = agent.getActor();
        // in fixed organizations
        for ( Actor supervisor : planService.findAllFixedSupervisorsOf( actor ) ) {
            supervisors.add( new Agent( supervisor ) );
        }
        // in registered organizations
        // When a fixed organization is registered under a placeholder,
        // it has the jobs it derives from the placeholder in addition to the jobs it already defines.
        for ( Agency agency : organizationParticipationService.listParticipatingAgencies( communityService ) ) {
            for ( Job job : agency.getPlaceholderJobs( communityService ) ) {
                if ( new Agent( job.getActor(), agency, communityService ).equals( agent ) ) {
                    if ( job.getSupervisor() != null ) {
                        Agent supervisor = new Agent( job.getSupervisor(), agency, communityService );
                        supervisors.add( supervisor );
                    }
                }
            }
            for ( Job job : agency.getFixedJobs( communityService ) ) {
                if ( actor.equals( job.getActor() ) ) {
                    if ( job.getSupervisor() != null ) {
                        Agent supervisor = new Agent( job.getSupervisor() );
                        supervisors.add( supervisor );
                    }
                }
            }
        }
        return new ArrayList<Agent>( supervisors );
    }

    @Override
    public List<Agency> findAllEmployersOfAgent( Agent agent, CommunityService communityService ) {
        PlanService planService = communityService.getPlanService();
        Set<Agency> employers = new HashSet<Agency>();
        // fixed organizations
        for ( Employment employment : planService.findAllEmploymentsForActor( agent.getActor() ) ) {
            Organization org = employment.getOrganization();
            if ( !org.isPlaceHolder() ) {
                employers.add( new Agency( org ) );
            }
        }
        // registered organization, if one
        OrganizationParticipation organizationParticipation = agent.getOrganizationParticipation();
        if ( organizationParticipation != null ) {
            employers.add( new Agency( organizationParticipation, communityService ) );
        }
        return new ArrayList<Agency>( employers );
    }

    @Override
    public List<CommunityEmployment> findAllEmploymentsForAgent( Agent agent, CommunityService communityService ) {
        PlanService planService = communityService.getPlanService();
        List<CommunityEmployment> employments = new ArrayList<CommunityEmployment>();
        Organization agencyPlaceholder = agent.isFromOrganizationParticipation()
                ? agent.getOrganizationParticipation().getPlaceholderOrganization( communityService )
                : null;
        for ( Employment employment : planService.findAllEmploymentsForActor( agent.getActor() ) ) {
            Organization org = employment.getOrganization();
            if ( !org.isPlaceHolder() ) {
                employments.add( new CommunityEmployment( employment, agent, new Agency( org ), communityService ) );
            } else {
                if ( agencyPlaceholder != null && agencyPlaceholder.equals( org ) ) {
                    employments.add( new CommunityEmployment(
                            employment,
                            agent,
                            new Agency( agent.getOrganizationParticipation(), communityService ),
                            communityService ) );
                }
            }
        }
        return employments;
    }

    @Override
    public List<CommunityEmployment> findAllEmploymentsBy( Agency agency, CommunityService communityService ) {
        List<CommunityEmployment> employments = new ArrayList<CommunityEmployment>();
        OrganizationParticipation organizationParticipation = agency.getOrganizationParticipation();
        Organization fixedOrganization = agency.getFixedOrganization();
        if ( organizationParticipation != null || fixedOrganization != null ) {
            for ( Job job : agency.getAllJobs( communityService ) ) {
                Agent agent = fixedOrganization != null
                        ? new Agent( job.getActor() )
                        : new Agent( job.getActor(), organizationParticipation, communityService );
                Organization organization =
                        fixedOrganization != null
                                ? fixedOrganization
                                : organizationParticipation.getPlaceholderOrganization( communityService );
                Employment employment = new Employment( organization, job );
                CommunityEmployment communityEmployment = new CommunityEmployment(
                        employment,
                        agent,
                        agency,
                        communityService
                );
                employments.add( communityEmployment );
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
    @SuppressWarnings("unchecked")
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
    public Agency findAgencyById( String id, CommunityService communityService ) throws NotFoundException {
        Agency agency = null;
        try {
            long longId = Long.parseLong( id );
            Organization org = communityService.getPlanService().find( Organization.class, longId ); // todo - COMMUNITY - id could have shifted since recorded
            agency = new Agency( org );
        } catch ( NumberFormatException e ) {
            OrganizationParticipation orgParticipation = organizationParticipationService.load( id );
            if ( orgParticipation != null ) {
                agency = new Agency( orgParticipation, communityService );
            }
        }
        if ( agency == null ) throw new NotFoundException();
        return agency;
    }

    @Override
    public OrganizationParticipation getOrganizationParticipation( String uid ) {
        return organizationParticipationService.load( uid );
    }

    @Override
    public RegisteredOrganization getRegisteredOrganization( String uid ) {
        return registeredOrganizationService.load( uid );
    }

    @Override
    public UserParticipation getUserParticipation( String uid ) {
        return userParticipationService.load( uid );
    }
}
