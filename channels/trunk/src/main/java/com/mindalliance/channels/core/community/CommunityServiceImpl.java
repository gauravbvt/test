package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.IssueDetectionWaiver;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityAssignments;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.AbstractModelObjectDao;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.UserUploadService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.db.data.ContactInfo;
import com.mindalliance.channels.db.services.communities.OrganizationParticipationService;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import com.mindalliance.channels.db.services.communities.UserParticipationConfirmationService;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.messages.FeedbackService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.CollaborationPlanAnalyst;
import com.mindalliance.channels.engine.analysis.Doctor;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.CollaborationPlanPage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
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

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( CommunityServiceImpl.class );

    @Autowired
    private UserParticipationService userParticipationService;
    @Autowired
    private UserParticipationConfirmationService userParticipationConfirmationService;
    @Autowired
    private OrganizationParticipationService organizationParticipationService;
    @Autowired
    private Analyst analyst;
    @Autowired
    private PlanCommunityManager planCommunityManager;
    @Autowired
    private ParticipationManager participationManager;
    @Autowired
    private UserRecordService userRecordService;
    @Autowired
    private RegisteredOrganizationService registeredOrganizationService;
    @Autowired
    private UserUploadService userUploadService;
    @Autowired
    private FeedbackService feedbackService;

    private PlanCommunity planCommunity;
    private PlanService planService;

    public CommunityServiceImpl() {
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
    public FeedbackService getFeedbackService() {
        return feedbackService;
    }

    @Override
    public ParticipationManager getParticipationManager() {
        return participationManager;
    }

    @Override
    public UserRecordService getUserRecordService() {
        return userRecordService;
    }

    @Override
    public RegisteredOrganizationService getRegisteredOrganizationService() {
        return registeredOrganizationService;
    }

    private void setParticipationManager( ParticipationManager participationManager ) {
        this.participationManager = participationManager;
    }

    public UserUploadService getUserUploadService() {
        return userUploadService;
    }

    public void setUserUploadService( UserUploadService userUploadService ) {
        this.userUploadService = userUploadService;
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
        if ( agency.isLocal() && parentAgency.isLocal() ) { // if both local to plan, then parentage is constrained by existing participation
            List<Organization> agencyPlaceholders = agency.getPlaceholders( this );
            // If the agency participates as placeholders, the parent agency is considered if, for all fo the agency's placeholders,:
            // 1. the agency's placeholder has no parent, or
            // 2. the agency's placeholder has the same parent as one of the parent agency candidate's placeholders
            if ( !agencyPlaceholders.isEmpty() ) {
                final List<Organization> parentAgencyPlaceholders = parentAgency.getPlaceholders( this );
                return !CollectionUtils.exists(
                        agencyPlaceholders,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                final Organization agencyPlaceholder = (Organization) object;
                                return agencyPlaceholder.getParent() != null
                                        &&
                                        !CollectionUtils.exists(
                                                parentAgencyPlaceholders,
                                                new Predicate() {
                                                    @Override
                                                    public boolean evaluate( Object object ) {
                                                        Organization parentAgencyPlaceholder = (Organization) object;
                                                        return agencyPlaceholder.getParent().equals( parentAgencyPlaceholder );
                                                    }
                                                }
                                        );
                            }
                        }
                );
            }
        } else if ( !agency.isLocal() && parentAgency.isLocal() ) { // global agency can't have local parent
            return false;
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


    public CollaborationPlanAnalyst getCollaborationPlanAnalyst() {
        return getParticipationManager().getParticipationAnalyst();
    }

    @Override
    public Boolean isCommunityPlanner( ChannelsUser user ) {
        return user.isCommunityPlanner( getPlanCommunity().getUri() );
    }

    @Override
    public List<ChannelsUser> getCommunityPlanners() {
        return userRecordService.getCommunityPlanners( getPlanCommunity().getUri() );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<String> getCommunityPlannerUsernames() {
        return (List<String>) CollectionUtils.collect(
                getCommunityPlanners(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (ChannelsUser) input ).getUsername();
                    }
                }
        );
    }

    @Override
    public List<UserIssue> findAllUserIssues( Identifiable identifiable ) {
        return getDao().findAllUserIssues( identifiable );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends Identifiable> List<T> listKnownIdentifiables( Class<T> clazz ) {
        List<T> results = new ArrayList<T>();
        if ( clazz.isAssignableFrom( ModelObject.class ) ) {
            for ( T mo : (List<T>) list( ModelObject.class ) )
                if ( !( (ModelObject) mo ).isUnknown() ) results.add( mo );
        }
        if ( clazz.isAssignableFrom( Agency.class ) ) {
            results.addAll( (List<T>) getParticipationManager().getAllKnownAgencies( this ) );
        }
        if ( clazz.isAssignableFrom( Agent.class ) ) {
            results.addAll( (List<T>) getParticipationManager().getAllKnownAgents( this ) );
        }
        if ( clazz.isAssignableFrom( ChannelsUser.class ) ) {
            results.addAll( (List<T>) getUserRecordService().getAllEnabledUsers() );
        }
        return results;
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

    @Override
    public List<Place> findUnboundLocationPlaceholders() {
        List<Place> boundPlaceholders = getPlanCommunity().getBoundLocationPlaceholders();
        List<Place> unboundPlaceholders = new ArrayList<Place>();
        for ( Place place : listActualEntities( Place.class, true ) ) {
            if ( place.isPlaceholder() && !boundPlaceholders.contains( place ) ) {
                unboundPlaceholders.add( place );
            }
        }
        return unboundPlaceholders;
    }

    @Override
    public Place resolveLocation( Place place ) {
        if ( place == null ) {
            return null;
        } else if ( place.isPlaceholder() ) {
            Place boundLocation = getPlanCommunity().getLocationBoundTo( place );
            return boundLocation == null ? place : boundLocation;
        } else {
            return place;
        }
    }

    @Override
    public Doctor getDoctor() {
        return isForDomain()
                ? getAnalyst().getDoctor()
                : getCollaborationPlanAnalyst().getDoctor();
    }

    @Override
    public void cleanUp() {
        removeObsoleteIssueDetectionWaivers();
    }

    @Override
    public List<TransmissionMedium> findMissingContactInfoMedia( ChannelsUser user ) {
        List<TransmissionMedium> missingMedia = new ArrayList<TransmissionMedium>();
        Set<TransmissionMedium> knownMedia = new HashSet<TransmissionMedium>();
        for ( ContactInfo contactInfo : user.getUserRecord().getContactInfoList() ) {
            try {
                knownMedia.add( find( TransmissionMedium.class, contactInfo.getTransmissionMediumId() ) );
            } catch ( NotFoundException e ) {
                // Ignore
            }
        }
        List<Agent> userAgents = getParticipationManager().listAgentsUserParticipatesAs( user, this );
        if ( !userAgents.isEmpty() ) {
            Set<TransmissionMedium> requiredMedia = new HashSet<TransmissionMedium>();
            CommunityCommitments communityCommitments = getAllCommitments( false );
            for ( Agent agent : userAgents ) {
                requiredMedia.addAll( findRequireMedia( communityCommitments.to( agent ), true ) ); // agent as beneficiary of notifications
                requiredMedia.addAll( findRequireMedia( communityCommitments.from( agent ), false ) ); // agent as committer to reply to requests
            }
            for ( final TransmissionMedium requiredMedium : requiredMedia ) {
                if ( requiredMedium.isUnicast() && requiredMedium.requiresAddress() ) {
                    boolean known = CollectionUtils.exists(
                            knownMedia,
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object object ) {
                                    TransmissionMedium knownMedium = (TransmissionMedium) object;
                                    return knownMedium.narrowsOrEquals( requiredMedium );
                                }
                            }
                    );
                    if ( !known ) {
                        missingMedia.add( requiredMedium );
                    }
                }
            }
        }
        return missingMedia;
    }

    @Override
    public String makePlanCommunityUrl() {
        String serverUrl = getPlanService().getServerUrl();
        return serverUrl
                + ( serverUrl.endsWith( "/" ) ? "" : "/" )
                + CollaborationPlanPage.COLLAB_PLAN
                + "?"
                + AbstractChannelsWebPage.COLLAB_PLAN_PARM
                + "="
                + planCommunity.getUri();
    }

    @Override
    public String makePlanCommunityParticipationUrl() {
        String serverUrl = getPlanService().getServerUrl();
        return serverUrl
                + ( serverUrl.endsWith( "/" ) ? "" : "/" )
                + CollaborationPlanPage.PARTICIPATION
                + "?"
                + AbstractChannelsWebPage.COLLAB_PLAN_PARM
                + "="
                + planCommunity.getUri();
    }

    private Set<TransmissionMedium> findRequireMedia( CommunityCommitments communityCommitments, boolean isBeneficiary ) {
        Set<TransmissionMedium> media = new HashSet<TransmissionMedium>();
        for ( CommunityCommitment communityCommitment : communityCommitments ) {
            Flow sharing = communityCommitment.getSharing();
            if ( isBeneficiary ) {
                if ( sharing.isNotification() ) {
                    for ( Channel channel : sharing.getEffectiveChannels() ) {
                        media.add( channel.getMedium() );
                    }
                }
            } else {
                if ( sharing.isAskedFor() ) {
                    for ( Channel channel : sharing.getEffectiveChannels() ) {
                        media.add( channel.getMedium() );
                    }
                }
            }
        }
        return media;
    }

    private void removeObsoleteIssueDetectionWaivers() {
        List<IssueDetectionWaiver> obsoleteWaivers = new ArrayList<IssueDetectionWaiver>(  );
        List<Identifiable> knownIdentifiables = listKnownIdentifiables( Identifiable.class );
        for ( final IssueDetectionWaiver issueDetectionWaiver : planCommunity.getIssueDetectionWaivers() ) {
            boolean matched = CollectionUtils.exists(
                    knownIdentifiables,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return issueDetectionWaiver.matches( (Identifiable)object );
                        }
                    });
            if ( !matched )
                obsoleteWaivers.add( issueDetectionWaiver );
        }
        for ( IssueDetectionWaiver obsoleteWaiver : obsoleteWaivers ) {
            planCommunity.removeIssueDetectionWaiver( obsoleteWaiver );
        }
        LOG.debug( "Removed "
                + obsoleteWaivers.size()
                + " obsolete issue detection waivers from "
                + planCommunity.getUri() );
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
        return (List<Issue>) CollectionUtils.select(
                list( UserIssue.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Issue) object ).getAbout().equals( modelObject );
                    }
                }
        );
    }

    ///////////////////////

    @Override
    public CommunityCommitments getAllCommitments( Boolean includeToSelf ) {
        CommunityCommitments commitments = new CommunityCommitments( getCommunityLocale() );
        CommunityAssignments allAssignments = getAllAssignments();
        for ( Flow flow : getPlanService().findAllFlows() ) {
            if ( flow.isSharing() && !flow.isProhibited() ) {
                CommunityAssignments beneficiaries = allAssignments.assignedTo( (Part) flow.getTarget() );
                for ( CommunityAssignment committer : allAssignments.assignedTo( (Part) flow.getSource() ) ) {
                    Agent committerAgent = committer.getAgent();
                    for ( CommunityAssignment beneficiary : beneficiaries ) {
                        if ( ( includeToSelf || !committerAgent.equals( beneficiary.getAgent() ) )
                                && allowsCommitment( committer, beneficiary, flow.getRestrictions() ) ) {
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
                            && allowsCommitment( committer, beneficiary, flow.getRestrictions() ) ) {
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
        for ( Assignment assignment : getPlanService().getAssignments( false, false ) ) {
            Actor assignmentActor = assignment.getActor();
            Organization employerOrganization = assignment.getOrganization();
            for ( Agent agent : allAgents ) {
                if ( agent.getActor().equals( assignmentActor ) ) {
                    if ( agent.getAgency().participatesAs( employerOrganization ) ) {
                        CommunityEmployment employment = new CommunityEmployment(
                                assignment.getEmployment().getJob(),
                                agent,
                                this );
                        CommunityAssignment communityAssignment = new CommunityAssignment(
                                employment,
                                assignment.getPart() );
                        assignments.add( communityAssignment );
                    }
                }
            }
        }
        return assignments;
    }

    @Override
    public boolean isCustodianOf( ChannelsUser user, Organization placeholder ) {
        if ( !placeholder.isPlaceHolder() ) return false;
        if ( user.isPlannerOrAdmin( getPlan().getUri() ) ) return true;
        final Actor custodian = placeholder.getCustodian();
        return custodian != null
                && CollectionUtils.exists(
                getParticipationManager().listAgentsUserParticipatesAs( user, this ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Agent) object ).getActor().equals( custodian );
                    }
                } );
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
        //  AOP advice does some work.
        getParticipationManager().clearCache();
    }

    @Override
    public void onDestroy() {
        // Do nothing
    }

    // None of the restrictions does not allow the commitment.
    private boolean allowsCommitment( final CommunityAssignment committer,
                                      final CommunityAssignment beneficiary,
                                      final List<Flow.Restriction> restrictions ) {
        return restrictions.isEmpty()
                || !CollectionUtils.exists(
                restrictions,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !allowsCommitment( committer, beneficiary, (Flow.Restriction) object );
                    }
                }
        );
    }

    private boolean allowsCommitment( CommunityAssignment committer,
                                      CommunityAssignment beneficiary,
                                      Flow.Restriction restriction ) {
        if ( restriction != null ) {
            Agency committerAgency = committer.getAgency();
            Agency beneficiaryAgency = beneficiary.getAgency();
            Place committerLocation = committer.getLocation( this );
            Place beneficiaryLocation = beneficiary.getLocation( this );
            switch ( restriction ) {

                case SameTopOrganization:
                    return committerAgency.getTopAgency( this )
                            .equals( beneficiaryAgency.getTopAgency( this ) );

                case SameOrganization:
                    return committerAgency.equals( beneficiaryAgency );

                case DifferentOrganizations:
                    return !committerAgency.equals( beneficiaryAgency );

                case DifferentTopOrganizations:
                    return !committerAgency.getTopAgency( this )
                            .equals( beneficiaryAgency.getTopAgency( this ) );

                case SameLocation:
                    return ModelObject.isNullOrUnknown( committerLocation )
                            || ModelObject.isNullOrUnknown( beneficiaryLocation )
                            || committerLocation.narrowsOrEquals( beneficiaryLocation, getCommunityLocale() )
                            || beneficiaryLocation.narrowsOrEquals( committerLocation, getCommunityLocale() );
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
        return getPlanCommunity().getLocale( this );
    }


}
