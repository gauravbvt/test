package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.community.participation.RegisteredOrganization;
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
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Nameable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A plan community.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/12
 * Time: 3:23 PM
 */
public class PlanCommunity implements Nameable, Identifiable {

    private static final String UNNAMED = "UNNAMED";

    private CommunityServiceImpl communityService;
    private String name;
    private String description;
    private Place communityLocale;
    private ParticipationManager participationManager;

    public PlanCommunity(  // todo - should only be invoked once per community instantiated.
            PlanService planService,
            Analyst analyst,
            UserParticipationService userParticipationService,
            UserParticipationConfirmationService userParticipationConfirmationService,
            ParticipationManager participationManager ) {
        this.participationManager = participationManager;
        communityService = new CommunityServiceImpl( this, planService, analyst, userParticipationService, userParticipationConfirmationService );
    }

    public Place getCommunityLocale() {
        return communityLocale;
    }

    public void setCommunityLocale( Place communityLocale ) {
        this.communityLocale = communityLocale;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getName() {
        return name == null ? UNNAMED : name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public CommunityServiceImpl getCommunityService() {
        return communityService;
    }

    public UserParticipationService getUserParticipationService() {
        return communityService.getUserParticipationService();
    }

    public PlanService getPlanService() {
        return communityService.getPlanService();
    }

    public ChannelsUserDao getUserDao() {
        return getPlanService().getUserDao();
    }

    public Analyst getAnalyst() {
        return communityService.getAnalyst();
    }

    public Plan getPlan() {
        return getPlanService().getPlan();
    }

    public UserParticipationConfirmationService getUserParticipationConfirmationService() {
        return communityService.getUserParticipationConfirmationService();
    }

    public boolean isCustodianOf( ChannelsUser user, Organization placeholder ) {
        if ( !placeholder.isPlaceHolder() ) return false;
        if ( user.isPlanner( getPlan().getUri() ) ) return true;
        Actor custodian = placeholder.getCustodian();
        return custodian != null
                && getUserParticipationService().isUserParticipatingAs( user, new Agent( custodian ), this );
    }

    public String getUri() {
        return getPlan().getUri(); // todo - change when not only one implied community per plan
    }

    public int getPlanVersion() {
        return getPlan().getVersion();
    }

    public ParticipationManager getParticipationManager() {
        return participationManager;
    }

    public String getPlanUri() {
        return getPlan().getUri();
    }

    ////////


    @Override
    public String getClassLabel() {
        return getClass().getSimpleName();
    }

    @Override
    public long getId() {
        return getPlan().getId();
    }

    @Override
    public String getTypeName() {
        return "Plan community";
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    public boolean isCommunityLeader( ChannelsUser user ) {
        return user.isPlanner( getPlan().getUri() );   // todo have non-planners be community leaders as well or instead
    }

    public boolean isOrganizationLeader( ChannelsUser user, RegisteredOrganization registeredOrganization ) {
        return isCommunityLeader( user ); // todo - change when organization leaders implemented
    }

    public CommunityAssignments getAllAssignments() {
        CommunityAssignments assignments = new CommunityAssignments( getCommunityLocale() );
        List<Agent> allAgents = participationManager.getAllKnownAgents( this );
        for ( Assignment planAssignment : getPlanService().getAssignments( false, false ) ) {
            Actor actor = planAssignment.getActor();
            Organization employer = planAssignment.getOrganization();
            for ( Agent agent : allAgents ) {
                if ( agent.getActor().equals( actor ) ) {
                    CommunityEmployment employment;
                    if ( agent.isRegisteredInPlaceholder( employer, this ) ) {
                        employment = new CommunityEmployment(
                                planAssignment.getEmployment(),
                                agent,
                                new Agency( agent.getOrganizationParticipation(), this ),
                                this );
                    } else {
                        employment = new CommunityEmployment(
                                planAssignment.getEmployment(),
                                agent,
                                new Agency( employer ),
                                this );
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

    public CommunityCommitments getAllCommitments( boolean includeToSelf ) {
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

    public CommunityCommitments findAllCommitments( Flow flow, boolean includeToSelf ) {
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


    @SuppressWarnings( "unchecked" )
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


    private boolean allowsCommitment( CommunityAssignment committer,
                                      CommunityAssignment beneficiary,
                                      Flow.Restriction restriction ) {
        if ( restriction != null ) {
            Agency committerAgency = committer.getAgency();
            Agency beneficiaryAgency = beneficiary.getAgency();
            Place committerLocation = committer.getLocation( this );
            Place beneficiaryLocation = beneficiary.getLocation( this );
            switch( restriction ) {

                case SameTopOrganization:
                    return committerAgency.getTopAgency( this )
                        .equals( beneficiaryAgency.getTopAgency( this ) );

                case SameOrganization:
                    return committerAgency.equals( beneficiaryAgency );

                case DifferentOrganizations:
                    return !committerAgency.equals( beneficiaryAgency );

                case DifferentTopOrganizations:
                    return !committerAgency.getTopAgency( this )
                            .equals( beneficiaryAgency.getTopAgency( this ));

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
                agency.getAllJobs( this ),
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


    public boolean canHaveParentAgency( final String name, String parentName ) {

            if ( parentName == null ) return true;
            // circularity test
            boolean nonCircular = !parentName.equals( name )
                    && !CollectionUtils.exists(
                    findAncestors( parentName ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Agency) object ).getName( ).equals( name );
                        }
                    } );
            if ( !nonCircular ) return false;
            // placeholder parent test
            Agency agency = getParticipationManager().findAgencyNamed( name, this );
            Agency parentAgency = getParticipationManager().findAgencyNamed( parentName, this );
            if ( agency == null || parentAgency == null ) return false; // should not happen
            Organization placeholder = agency.getPlaceholder( this );
            if ( placeholder != null ) {
                Organization parentPlaceholder = parentAgency.getPlaceholder( this );
                return ChannelsUtils.areEqualOrNull( placeholder.getParent(), parentPlaceholder );
            }
            return true;
        }

    public List<Agency> findAncestors( String agencyName ) {
        List<Agency> visited = new ArrayList<Agency>();
        Agency agency = participationManager.findAgencyNamed( agencyName, this );
        if ( agency != null )
            return safeFindAncestors( agency, visited );
        else
            return new ArrayList<Agency>();
    }

    private List<Agency> safeFindAncestors(
            Agency agency,
            List<Agency> visited ) {
        List<Agency> ancestors = new ArrayList<Agency>();
        if ( !visited.contains( agency ) ) {
            if ( agency != null ) {
                Agency parentAgency = agency.getParent( this );
                if ( parentAgency != null && !visited.contains( parentAgency ) ) {
                    visited.add( parentAgency );
                    ancestors.add( parentAgency );
                    ancestors.addAll( safeFindAncestors( parentAgency, visited ) );
                }
            }
        }
        return ancestors;
    }


}
