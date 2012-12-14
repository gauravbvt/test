package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
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
    private OrganizationParticipationService organizationorganizationParticipationServiceService;

    @Autowired
    private PlanManager planManager;


    public ParticipationManagerImpl() {
    }

    @Override
    public List<Agency> getAllKnownAgencies( PlanCommunity planCommunity ) {
        Set<Agency> agencies = new HashSet<Agency>();
        PlanService planService = planCommunity.getPlanService();
        // fixed
        for ( Organization organization : planService.listActualEntities( Organization.class, true ) ) {
            if ( !organization.isPlaceHolder() )
                agencies.add( new Agency( organization ) );
        }
        // registered as placeholder
        agencies.addAll( organizationorganizationParticipationServiceService.listRegisteredAgencies( planCommunity ) );
        // registered by community but not registered as placeholders
        for ( RegisteredOrganization registeredOrganization
                : registeredOrganizationService.getAllRegisteredOrganizations( planCommunity ) ) {
            if ( !registeredOrganization.isFixedOrganization()
                    && organizationorganizationParticipationServiceService.findRegistrationsFor(
                    registeredOrganization,
                    planCommunity ).isEmpty() ) {
                agencies.add( new Agency( registeredOrganization, planCommunity ) );
            }
        }
        return new ArrayList<Agency>( agencies );
    }

    @Override
    public List<Agent> getAllKnownAgents( PlanCommunity planCommunity ) {
        Set<Agent> agents = new HashSet<Agent>();
        PlanService planService = planCommunity.getPlanService();
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
        for ( Agency agency : organizationorganizationParticipationServiceService.listRegisteredAgencies( planCommunity ) ) {
            if ( agency.isRegisteredByCommunity() )
                agents.addAll( agency.getAgents( planCommunity ) );
        }
        return new ArrayList<Agent>( agents );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Agent> findSelfAssignableOpenAgents( final PlanCommunity planCommunity, final ChannelsUser user ) {
        PlanService planService = planCommunity.getPlanService();
        List<Agent> agents = new ArrayList<Agent>();
        // In fixed organizations
        for ( Actor actor : planService.listActualEntities( Actor.class ) ) {
            Agent agent = new Agent( actor );
            if ( isParticipationSelfAssignable(
                    agent,
                    user,
                    planCommunity ) ) {
                agents.add( agent );
            }
        }
        // In registered organizations
        for ( Agency agency : organizationorganizationParticipationServiceService.listRegisteredAgencies( planCommunity ) ) {
            for ( Agent agent : agency.getAgents( planCommunity ) ) {
                if ( isParticipationSelfAssignable(
                        agent,
                        user,
                        planCommunity ) ) {
                    agents.add( agent );
                }
            }
        }
        return agents;
    }

    @Override
    public boolean isParticipationAvailable( Agent agent, ChannelsUser user, PlanCommunity planCommunity ) {
        List<UserParticipation> currentParticipations = userParticipationService.getUserParticipations(
                user,
                planCommunity );
        List<UserParticipation> activeParticipations = userParticipationService.getActiveUserParticipations(
                user,
                planCommunity );
        return agent.isValid( planCommunity )
                && !alreadyParticipatingAs( agent, currentParticipations )
                && !isSingularAndTaken( agent, planCommunity )
                && meetsPreEmploymentConstraint( agent, activeParticipations, planCommunity );
    }

    @Override
    public boolean isParticipationSelfAssignable( Agent agent, ChannelsUser user, PlanCommunity planCommunity ) {
        return agent.isParticipationUserAssignable()
                && isParticipationAvailable( agent, user, planCommunity );
    }

    @Override
    public boolean meetsPreEmploymentConstraint(
            Agent agent,
            List<UserParticipation> activeParticipations,
            PlanCommunity planCommunity ) {
        if ( !agent.isParticipationRestrictedToEmployed() ) return true;
        List<Agency> agentEmployers = findAllEmployersOfAgent( agent, planCommunity );
        List<Agency> participationEmployers = new ArrayList<Agency>();
        for ( UserParticipation participation : activeParticipations ) {
            Agent participationAgent = participation.getAgent( planCommunity );
            if ( participationAgent != null && !participationAgent.isOpenParticipation() )
                participationEmployers.addAll( findAllEmployersOfAgent( participationAgent, planCommunity ) );
        }
        return !Collections.disjoint( agentEmployers, participationEmployers );
    }

    @Override
    public List<Agent> findAllSupervisorsOf( Agent agent, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        Set<Agent> supervisors = new HashSet<Agent>();
        Actor actor = agent.getActor();
        // in fixed organizations
        for ( Actor supervisor : planService.findAllFixedSupervisorsOf( actor ) ) {
            supervisors.add( new Agent( supervisor ) );
        }
        // in registered organizations
        // When a fixed organization is registered under a placeholder,
        // it has the jobs it derives from the placeholder in addition to the jobs it already defines.
        for ( Agency agency : organizationorganizationParticipationServiceService.listRegisteredAgencies( planCommunity ) ) {
            for ( Job job : agency.getPlaceholderJobs( planCommunity ) ) {
                if ( new Agent( job.getActor(), agency, planCommunity ).equals( agent ) ) {
                    if ( job.getSupervisor() != null ) {
                        Agent supervisor = new Agent( job.getSupervisor(), agency, planCommunity );
                        supervisors.add( supervisor );
                    }
                }
            }
            for ( Job job : agency.getFixedJobs( planCommunity ) ) {
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
    public List<Agency> findAllEmployersOfAgent( Agent agent, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        Set<Agency> employers = new HashSet<Agency>();
        // fixed organizations
        for ( Employment employment : planService.findAllEmploymentsForActor( agent.getActor() ) ) {
            Organization org = employment.getOrganization();
            if ( !org.isPlaceHolder() ) {
                employers.add( new Agency( org ) );
            }
        }
        // registered organization, if one
        OrganizationParticipation registration = agent.getOrganizationParticipation();
        if ( registration != null ) {
            employers.add( new Agency( registration, planCommunity ) );
        }
        return new ArrayList<Agency>( employers );
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

    private boolean isSingularAndTaken( Agent agent, PlanCommunity planCommunity ) {
        return agent.isSingularParticipation()
                && !userParticipationService.getParticipationsAsAgent( agent, planCommunity ).isEmpty();
    }

    @Override
    public boolean areCollaborators(   // todo
                                       PlanCommunity planCommunity,
                                       final ChannelsUser user,
                                       final ChannelsUser otherUser ) {  // Does one have commitments or co-assignments with the other?
        return false;
    }

    @Override
    public boolean isSupervisorOf(
            final PlanCommunity planCommunity,
            ChannelsUser user,
            ChannelsUser otherUser ) {
        final List<Agent> userAgents = userParticipationService.listAgentsUserParticipatesAs( user, planCommunity );
        final List<Agent> otherUserAgents = userParticipationService.listAgentsUserParticipatesAs( otherUser, planCommunity );
        return CollectionUtils.exists(
                otherUserAgents,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Agent otherUserAgent = (Agent) object;
                        List<Agent> supervisorsOfOtherUser = findAllSupervisorsOf( otherUserAgent, planCommunity );
                        return !CollectionUtils.intersection( supervisorsOfOtherUser, userAgents ).isEmpty();
                    }
                }
        );
    }

    @Override
    public boolean isSupervisedBy(
            final PlanCommunity planCommunity,
            ChannelsUser user,
            ChannelsUser otherUser ) {
        return isSupervisorOf( planCommunity, otherUser, user );
    }

    @Override
    public boolean areColleagues(   // have a common employer
                                    PlanCommunity planCommunity,
                                    ChannelsUser user,
                                    ChannelsUser otherUser ) {
        final List<Agent> userAgents = userParticipationService.listAgentsUserParticipatesAs( user, planCommunity );
        final List<Agent> otherUserAgents = userParticipationService.listAgentsUserParticipatesAs( otherUser, planCommunity );
        Set<Agency> userEmployers = new HashSet<Agency>();
        for ( Agent userAgent : userAgents ) {
            userEmployers.addAll( findAllEmployersOfAgent( userAgent, planCommunity ) );
        }
        Set<Agency> otherUserEmployers = new HashSet<Agency>();
        for ( Agent otherUserAgent : otherUserAgents ) {
            otherUserEmployers.addAll( findAllEmployersOfAgent( otherUserAgent, planCommunity ) );
        }
        return !CollectionUtils.intersection( userEmployers, otherUserEmployers ).isEmpty();
    }

    @Override
    public boolean hasAuthorityOverParticipation(
            final PlanCommunity planCommunity,
            ChannelsUser user,
            UserParticipation userParticipation ) {
        ChannelsUserInfo otherUserInfo = userParticipation.getParticipant();
        // A user can unilaterally terminate his/her own participation.
        if ( user.getUserInfo().equals( otherUserInfo ) ) return true;
        // else, does the user participate as one of the agents supervising the participation agent?
        final List<Agent> userAgents = userParticipationService.listAgentsUserParticipatesAs( user, planCommunity );
        final Agent particpationAgent = userParticipation.getAgent( planCommunity );
        List<Agent> supervisorAgents = findAllSupervisorsOf( particpationAgent, planCommunity );
        return !CollectionUtils.intersection( userAgents, supervisorAgents ).isEmpty();
    }

    @Override
    public Plan getPlan( String communityUri, int planVersion ) {
        return planManager.getPlan( communityUri, planVersion ); // todo - change when single planCommunity is not implied by plan
    }


}
