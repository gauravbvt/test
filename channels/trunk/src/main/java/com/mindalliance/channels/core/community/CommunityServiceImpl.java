package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.CommunityPlanner;
import com.mindalliance.channels.core.community.participation.CommunityPlannerService;
import com.mindalliance.channels.core.community.participation.OrganizationParticipationService;
import com.mindalliance.channels.core.community.participation.ParticipationAnalyst;
import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.community.participation.UserParticipationConfirmationService;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityAssignments;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.AbstractModelObjectDao;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Community service implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/12
 * Time: 3:24 PM
 */
public class CommunityServiceImpl implements CommunityService {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( CommunityServiceImpl.class );


    private PlanCommunity planCommunity;
    private PlanService planService;
    private Analyst analyst;
    private UserParticipationService userParticipationService;
    private UserParticipationConfirmationService userParticipationConfirmationService;
    private OrganizationParticipationService organizationParticipationService;
    private PlanCommunityManager planCommunityManager;
    private ParticipationManager participationManager;
    private ChannelsUserDao userDao;
    private CommunityPlannerService communityPlannerService;

    public CommunityServiceImpl() {}

    public CommunityServiceImpl(
            Analyst analyst,
            UserParticipationService userParticipationService,
            UserParticipationConfirmationService userParticipationConfirmationService,
            OrganizationParticipationService organizationParticipationService,
            PlanCommunityManager planCommunityManager,
            ParticipationManager participationManager,
            ChannelsUserDao userDao,
            CommunityPlannerService communityPlannerService ) {
        this.analyst = analyst;
        this.userParticipationService = userParticipationService;
        this.userParticipationConfirmationService = userParticipationConfirmationService;
        this.organizationParticipationService = organizationParticipationService;
        this.planCommunityManager = planCommunityManager;
        this.participationManager = participationManager;
        this.userDao = userDao;
        this.communityPlannerService = communityPlannerService;
    }

    @Override
    public PlanCommunity getPlanCommunity() {
        return planCommunity;
    }

    @Override
    public Plan getPlan() {
        return getPlanService().getPlan();
    }

    @Override
    public void setPlanCommunity( PlanCommunity planCommunity ) {
        this.planCommunity = planCommunity;
    }

    @Override
    public AbstractModelObjectDao getDao() {
        if ( getPlanCommunity().isDomainCommunity() ) {
            return getPlanService().getDao();
        } else {
            return planCommunityManager.getDao( getPlanCommunity() );
        }
    }

    @Override
    public UserParticipationService getUserParticipationService() {
        return userParticipationService;
    }

    @Override
    public UserParticipationConfirmationService getUserParticipationConfirmationService() {
        return userParticipationConfirmationService;
    }

    @Override
    public OrganizationParticipationService getOrganizationParticipationService() {
        return organizationParticipationService;
    }

    @Override
    public ParticipationManager getParticipationManager() {
        return participationManager;
    }

    @Override
    public ChannelsUserDao getUserDao() {
        return userDao;
    }

    private void setParticipationManager( ParticipationManager participationManager ) {
        this.participationManager = participationManager;
    }

    @Override
    public PlanService getPlanService() {
        return planService;
    }

    @Override
    public void setPlanService( PlanService planService ) {
        this.planService = planService;
    }

    @Override
    public Analyst getAnalyst() {
        return analyst;
    }

    @Override
    public <T extends ModelObject> T find( Class<T> clazz, long id, Date dateOfRecord ) throws NotFoundException {
        return (T) getDao().find( clazz, id, dateOfRecord );
    }

    @Override
    public boolean exists( Class<? extends ModelObject> clazz, Long id, Date dateOfRecord ) {
        try {
            return id != null && find( clazz, id, dateOfRecord ) != null;
        } catch ( NotFoundException e ) {
            LOG.warn( "Does not exist: " + clazz.getSimpleName() + " at " + id + " recorded on " + dateOfRecord );
            return false;
        }
    }


    @Override
    public boolean canHaveParentAgency( final String name, String parentName ) {
        if ( parentName == null ) return true;
        // circularity test
        boolean nonCircular = !parentName.equals( name )
                && !CollectionUtils.exists(
                findAncestors( parentName ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Agency) object ).getName().equals( name );
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

    @Override
    public List<Agency> findAncestors( String agencyName ) {
        List<Agency> visited = new ArrayList<Agency>();
        Agency agency = getParticipationManager().findAgencyNamed( agencyName, this );
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


    public ParticipationAnalyst getParticipationAnalyst() {
        return getParticipationManager().getParticipationAnalyst();
    }

    @Override
    public Boolean isCommunityPlanner( ChannelsUser user ) {
        return user.isAdmin() || communityPlannerService.isPlanner( user, this );
    }

    @Override
    public List<ChannelsUser> getCommunityPlanners() {
        List<ChannelsUser> users = new ArrayList<ChannelsUser>(  );
        for ( CommunityPlanner communityPlanner : communityPlannerService.listPlanners( this ) ) {
            users.add( new ChannelsUser( communityPlanner.getUserInfo() ) );
        }
        return users;
    }

    @Override
    public <T extends ModelObject> List<T> list( Class<T> clazz ) {
        return getDao().list( clazz );
    }

    @Override
    public <T extends ModelEntity> List<T> listActualEntities( Class<T> clazz, boolean mustBeReferenced ) {
        return getDao().listActualEntities( clazz, mustBeReferenced );
    }

    @Override
    public <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name ) {
        return getDao().findOrCreate( clazz, name );
    }

    @Override
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        return getDao().find( clazz, id );
    }

    @Override
    public <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name, long id ) {
        return getDao().findOrCreate( clazz, name, id );
    }

    @Override
    public <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name, long id ) {
        return getDao().findOrCreateType( clazz, name, id );
    }

    @Override
    public void update( ModelObject mo ) {
        getDao().update( mo );
    }

    @Override
    public void add( ModelObject modelObject, Long id ) {
        getDao().add( modelObject, id );
    }

    @Override
    public <T extends ModelEntity> T findEntityType( Class<T> entityClass, String name ) {
        return getDao().findEntityType( entityClass, name );
    }

    @Override
    public <T extends ModelEntity> T findActualEntity( Class<T> entityClass, String name ) {
        return getDao().findActualEntity( entityClass, name );
    }

    @Override
    public boolean isForDomain() {
        return getPlanCommunity().isDomainCommunity();
    }

    @Override
    public void remove( ModelObject object ) {
        beforeRemove( object );
        getDao().remove( object );
    }

    private void beforeRemove( ModelObject object ) {
        // Do nothing
    }

    @Override
    public <T extends ModelEntity> T safeFindOrCreate( Class<T> clazz, String name ) {
        return safeFindOrCreate( clazz, name, null );
    }

    @Override
    public <T extends ModelEntity> T safeFindOrCreate( Class<T> clazz, String name, Long id ) {
        return getDao().safeFindOrCreate( clazz, name, id );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Issue> listUserIssues( final ModelObject modelObject ) {
        return (List<Issue>)CollectionUtils.select(
                list( UserIssue.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((Issue)object).getAbout().equals( modelObject );
                    }
                }
        );
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
        List<Agent> allAgents = getParticipationManager().getAllKnownAgents( this );
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

    @Override
    public boolean isCustodianOf( ChannelsUser user, Organization placeholder ) {
        if ( !placeholder.isPlaceHolder() ) return false;
        if ( user.isPlanner( getPlan().getUri() ) ) return true;
        Actor custodian = placeholder.getCustodian();
        return custodian != null
                && getUserParticipationService().isUserParticipatingAs( user, new Agent( custodian ), this );
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

    @Override
    public void onDestroy() {
        // Do nothing
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

                case Supervised: // beneficiary has committer as supervisor
                    return hasSupervisor( beneficiary.getAgent(), committer.getAgent(), beneficiaryAgency );

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



    ///////////////////////////

    private Place getCommunityLocale() {
        return getPlanCommunity().getCommunityLocale();
    }


}
