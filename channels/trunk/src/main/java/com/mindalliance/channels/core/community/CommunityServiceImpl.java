package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.OrganizationParticipationService;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationConfirmationService;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityAssignments;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Community service implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/12
 * Time: 3:24 PM
 */
public class CommunityServiceImpl implements CommunityService {

    private PlanCommunity planCommunity;
    private PlanService planService;
    private Analyst analyst;
    private UserParticipationService userParticipationService;
    private UserParticipationConfirmationService userParticipationConfirmationService;
    private OrganizationParticipationService organizationParticipationService;

    public CommunityServiceImpl() {}

    public CommunityServiceImpl(
            Analyst analyst,
            UserParticipationService userParticipationService,
            UserParticipationConfirmationService userParticipationConfirmationService,
            OrganizationParticipationService organizationParticipationService ) {
        this.analyst = analyst;
        this.userParticipationService = userParticipationService;
        this.userParticipationConfirmationService = userParticipationConfirmationService;
        this.organizationParticipationService = organizationParticipationService;
    }

    public PlanCommunity getPlanCommunity() {
        return planCommunity;
    }

    public void setPlanCommunity( PlanCommunity planCommunity ) {
        this.planCommunity = planCommunity;
    }

    @Override
    public UserParticipationService getUserParticipationService() {
        return userParticipationService;
    }

    @Override
    public UserParticipationConfirmationService getUserParticipationConfirmationService() {
        return userParticipationConfirmationService;
    }

    public OrganizationParticipationService getOrganizationParticipationService() {
        return organizationParticipationService;
    }

    @Override
    public PlanService getPlanService() {
        return planService;
    }

    public void setPlanService( PlanService planService ) {
        this.planService = planService;
    }

    @Override
    public Analyst getAnalyst() {
        return analyst;
    }

    @Override
    public List<ChannelsUser> findUsersParticipatingAs( Actor actor ) {
        Set<ChannelsUser> users = new HashSet<ChannelsUser>();
        List<UserParticipation> participations = userParticipationService.getParticipationsAsAgent( new Agent( actor ), planCommunity );
        ChannelsUserDao userDao = planService.getUserDao();
        for ( UserParticipation participation : participations ) {
            if ( !actor.isSupervisedParticipation()
                    || userParticipationConfirmationService.isConfirmedByAllSupervisors( participation, planCommunity ) ) {
                ChannelsUser user = userDao.getUserNamed( participation.getParticipant().getUsername() );
                if ( user != null ) {
                    users.add( user );
                }
            }
        }
        return new ArrayList<ChannelsUser>( users );
    }

    @Override
    public Boolean meetsPreEmploymentConstraint( Actor actor,
                                                 List<UserParticipation> activeParticipations ) {
        if ( !actor.isParticipationRestrictedToEmployed() ) return true;
        List<Organization> actorEmployers = planService.findDirectAndIndirectEmployers(
                planService.findAllEmploymentsForActor( actor ) );
        List<Organization> myPlannedEmployers = new ArrayList<Organization>();
        for ( UserParticipation participation : activeParticipations ) {
            Actor participationActor = participation.getAgent( planCommunity ).getActor( );
            if ( participationActor != null && !participationActor.isOpenParticipation() )
                myPlannedEmployers.addAll( planService.findDirectAndIndirectEmployers(
                        planService.findAllEmploymentsForActor( participationActor ) ) );
        }
        return !Collections.disjoint( myPlannedEmployers, actorEmployers );
    }

    ///////////////////////

    @Override
    public CommunityCommitments getAllCommitments( Boolean includeToSelf ) {
        CommunityCommitments commitments = new CommunityCommitments( getCommunityLocale() );
        CommunityAssignments allAssignments = getAllAssignments();
        for ( Flow flow: getPlanService().findAllFlows() ) {
            if ( flow.isSharing() && !flow.isProhibited() ) {
                CommunityAssignments beneficiaries = allAssignments.assignedTo( (Part) flow.getTarget() );
                for ( CommunityAssignment committer : allAssignments.assignedTo( (Part) flow.getSource() ) ) {
                    Agent committerAgent = committer.getAgent();
                    for ( CommunityAssignment beneficiary : beneficiaries ) {
                        if ( ( includeToSelf || !committerAgent.equals( beneficiary.getAgent() ) )
                                && allowsCommitment( committer, beneficiary, flow.getRestriction() ) ) {
                            commitments.add( new CommunityCommitment( committer, beneficiary, flow ) );
                        }
                    }
                }

            }
        }
        return commitments;
    }

    @Override
    public CommunityCommitments findAllCommitments( Flow flow, Boolean includeToSelf ) {
        CommunityCommitments commitments = new CommunityCommitments( getCommunityLocale() );
        CommunityAssignments allAssignments = getAllAssignments();
        if ( flow.isSharing() && !flow.isProhibited() ) {
            CommunityAssignments beneficiaries = allAssignments.assignedTo( (Part) flow.getTarget() );
            for ( CommunityAssignment committer : allAssignments.assignedTo( (Part) flow.getSource() ) ) {
                Agent committerAgent = committer.getAgent();
                for ( CommunityAssignment beneficiary : beneficiaries ) {
                    if ( ( includeToSelf || !committerAgent.equals( beneficiary.getAgent() ) )
                            && allowsCommitment( committer, beneficiary, flow.getRestriction() ) ) {
                        commitments.add( new CommunityCommitment( committer, beneficiary, flow ) );
                    }
                }
            }

        }
        return commitments;
    }

    @Override
    public CommunityAssignments getAllAssignments() {
        CommunityAssignments assignments = new CommunityAssignments( getCommunityLocale() );
        List<Agent> allAgents = getPlanCommunity().getParticipationManager().getAllKnownAgents( getPlanCommunity() );
        for ( Assignment planAssignment : getPlanService().getAssignments( false, false ) ) {
            Actor actor = planAssignment.getActor();
            Organization employer = planAssignment.getOrganization();
            for ( Agent agent : allAgents ) {
                if ( agent.getActor().equals( actor ) ) {
                    CommunityEmployment employment;
                    if ( agent.isRegisteredInPlaceholder( employer, getPlanCommunity() ) ) {
                        employment = new CommunityEmployment(
                                planAssignment.getEmployment(),
                                agent,
                                new Agency( agent.getOrganizationParticipation(), getPlanCommunity() ),
                                getPlanCommunity() );
                    } else {
                        employment = new CommunityEmployment(
                                planAssignment.getEmployment(),
                                agent,
                                new Agency( employer ),
                                getPlanCommunity() );
                    }
                    CommunityAssignment assignment = new CommunityAssignment(
                            employment,
                            planAssignment.getPart() );
                    assignments.add( assignment );
                }
            }
        }
        return assignments;
    }



    @SuppressWarnings( "unchecked" )
    @Override
    public CommunityCommitments findAllBypassCommitments( final Flow flow ) {
        assert flow.isSharing();
        CommunityCommitments commitments = new CommunityCommitments( getCommunityLocale() );
        if ( flow.isCanBypassIntermediate() ) {
            List<Flow> bypassFlows;
            if ( flow.isNotification() ) {
                Part intermediate = (Part) flow.getTarget();
                bypassFlows = (List<Flow>) CollectionUtils.select(
                        intermediate.getAllSharingSends(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return flow.containsAsMuchAs( ( (Flow) object ) );
                            }
                        }
                );
            } else { // request-reply
                Part intermediate = (Part) flow.getSource();
                bypassFlows = (List<Flow>) CollectionUtils.select(
                        intermediate.getAllSharingReceives(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (Flow) object ).containsAsMuchAs( flow );
                            }
                        }
                );
            }
            for ( Flow byPassFlow : bypassFlows ) {
                commitments.addAll( findAllCommitments( byPassFlow, true ) );
            }
        }
        return commitments;
    }

    @Override
    public void clearCache() {
        // do nothing - AOP advice does the work.
    }

    private boolean allowsCommitment( CommunityAssignment committer,
                                      CommunityAssignment beneficiary,
                                      Flow.Restriction restriction ) {
        if ( restriction != null ) {
            Agency committerAgency = committer.getAgency();
            Agency beneficiaryAgency = beneficiary.getAgency();
            Place committerLocation = committer.getLocation( getPlanCommunity() );
            Place beneficiaryLocation = beneficiary.getLocation( getPlanCommunity() );
            switch( restriction ) {

                case SameTopOrganization:
                    return committerAgency.getTopAgency( getPlanCommunity() )
                            .equals( beneficiaryAgency.getTopAgency( getPlanCommunity() ) );

                case SameOrganization:
                    return committerAgency.equals( beneficiaryAgency );

                case DifferentOrganizations:
                    return !committerAgency.equals( beneficiaryAgency );

                case DifferentTopOrganizations:
                    return !committerAgency.getTopAgency( getPlanCommunity() )
                            .equals( beneficiaryAgency.getTopAgency( getPlanCommunity() ));

                case SameLocation:
                    return ModelObject.isNullOrUnknown( committerLocation )
                            || ModelObject.isNullOrUnknown( beneficiaryLocation )
                            || committerLocation.narrowsOrEquals( beneficiaryLocation, getCommunityLocale() )
                            || beneficiaryLocation.narrowsOrEquals( committerLocation, getCommunityLocale() );

                case SameOrganizationAndLocation:
                    return committerAgency.equals( beneficiaryAgency )
                            && ( ModelObject.isNullOrUnknown( committerLocation )
                            || ModelObject.isNullOrUnknown( beneficiaryLocation )
                            || committerLocation.narrowsOrEquals( beneficiaryLocation, getCommunityLocale() )
                            || beneficiaryLocation.narrowsOrEquals( committerLocation, getCommunityLocale() ) );

                case DifferentLocations:
                    return ModelObject.isNullOrUnknown( committerLocation )
                            || ModelObject.isNullOrUnknown( beneficiaryLocation )
                            || !committerLocation.narrowsOrEquals( beneficiaryLocation, getCommunityLocale() )
                            || !beneficiaryLocation.narrowsOrEquals( committerLocation, getCommunityLocale() );

                case Supervisor:
                    return hasSupervisor( committer.getAgent(), beneficiary.getAgent(), committerAgency );

                case Self:
                    return committer.getAgent().equals( beneficiary.getAgent() );

                case Other:
                    return !committer.getAgent().equals( beneficiary.getAgent() );
            }
        }
        return true;
    }

    private boolean hasSupervisor( final Agent agent, final Agent supervisor, Agency agency ) {
        return CollectionUtils.exists(
                agency.getAllJobs( getPlanCommunity() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Job job = (Job) object;
                        return job.getActor().equals( agent.getActor() )
                                && job.getSupervisor() != null
                                && job.getSupervisor().equals( supervisor.getActor() );
                    }
                }
        );
    }



    ///////////////////////////

    private Place getCommunityLocale() {
        return getPlanCommunity().getCommunityLocale();
    }


}
