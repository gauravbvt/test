/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.dao.ModelDao;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Agreement;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Delay;
import com.mindalliance.channels.core.model.Dissemination;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Flow.Restriction;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.model.Tag;
import com.mindalliance.channels.core.model.Transformation;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.AssetField;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.nlp.Proximity;
import com.mindalliance.channels.core.nlp.SemanticMatcher;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.engine.analysis.graph.AssetSupplyRelationship;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Query service instance.
 */
public abstract class DefaultQueryService implements QueryService {

    // Todo - Move all methods with side-effects to PlanDao

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultQueryService.class );

    /**
     * An attachment manager.
     */
    private AttachmentManager attachmentManager;

    /**
     * The plan manager.
     */
    private ModelManager modelManager;

    /**
     * Semantic matcher.
     */
    private SemanticMatcher semanticMatcher;

    /**
     * File user details service.
     */
    private UserRecordService userDao;

    /**
     * SurveysDAO
     */
    private SurveysDAO surveysDAO;

    //-------------------------------

    /**
     * Required for CGLIB proxies...
     */
    DefaultQueryService() {
    }

    protected DefaultQueryService(
            ModelManager modelManager,
            AttachmentManager attachmentManager,
            SemanticMatcher semanticMatcher,
            UserRecordService userDao,
            SurveysDAO surveysDao
    ) {
        this.modelManager = modelManager;
        this.attachmentManager = attachmentManager;
        this.semanticMatcher = semanticMatcher;
        this.userDao = userDao;
        this.surveysDAO = surveysDao;
    }

    //-------------------------------
    @Override
    public void add( ModelObject object ) {
        getDao().add( object );
    }

    @Override
    public void add( ModelObject object, Long id ) {
        getDao().add( object, id );
    }

    @Override
    public boolean allowsCommitment( final Assignment committer,
                                     final Assignment beneficiary,
                                     final Place locale,
                                     Flow flow ) {
        List<Restriction> restrictions = flow.getRestrictions();
        if ( restrictions.isEmpty() ) {
            return true;
        } else {
            return !CollectionUtils.exists(
                    restrictions,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return !allowsCommitment( committer, beneficiary, locale, (Flow.Restriction) object );
                        }
                    }
            );
        }
    }

    @Override
    public boolean allowsCommitment( final Assignment committer,
                                     final Assignment beneficiary,
                                     final Place locale,
                                     Collection<Restriction> restrictions ) {
        if ( restrictions.isEmpty() ) {
            return true;
        } else {
            return !CollectionUtils.exists(
                    restrictions,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return !allowsCommitment( committer, beneficiary, locale, (Flow.Restriction) object );
                        }
                    }
            );
        }
    }


    private boolean allowsCommitment( Assignment committer,
                                      Assignment beneficiary,
                                      Place locale,
                                      Restriction restriction ) {

        if ( restriction != null ) {
            Organization committerOrg = committer.getOrganization();
            Organization beneficiaryOrg = beneficiary.getOrganization();
            Place committerLocation = committer.getLocation();
            Place beneficiaryLocation = beneficiary.getLocation();
            switch ( restriction ) {

                case SameTopOrganization:
                    return ModelObject.isNullOrUnknown( committerOrg )
                            || ModelObject.isNullOrUnknown( beneficiaryOrg )
                            || committerOrg.getTopOrganization()
                            .equals( beneficiaryOrg.getTopOrganization() );

                case SameOrganization:
                    return ModelObject.isNullOrUnknown( committerOrg )
                            || ModelObject.isNullOrUnknown( beneficiaryOrg )
                            || committerOrg.narrowsOrEquals( beneficiaryOrg, locale )
                            || beneficiaryOrg.narrowsOrEquals( committerOrg, locale );

                case DifferentOrganizations:
                    return ModelObject.isNullOrUnknown( committerOrg )
                            || ModelObject.isNullOrUnknown( beneficiaryOrg )
                            || !committerOrg.narrowsOrEquals( beneficiaryOrg, locale )
                            && !beneficiaryOrg.narrowsOrEquals( committerOrg, locale );

                case DifferentTopOrganizations:
                    return ModelObject.isNullOrUnknown( committerOrg )
                            || ModelObject.isNullOrUnknown( beneficiaryOrg )
                            || !committerOrg.getTopOrganization()
                            .equals( beneficiaryOrg.getTopOrganization() );

                case SameLocation:
                    return ModelObject.isNullOrUnknown( committerLocation )
                            || ModelObject.isNullOrUnknown( beneficiaryLocation )
                            || committerLocation.narrowsOrEquals( beneficiaryLocation, locale )
                            || beneficiaryLocation.narrowsOrEquals( committerLocation, locale );

                case DifferentLocations:
                    return ModelObject.isNullOrUnknown( committerLocation )
                            || ModelObject.isNullOrUnknown( beneficiaryLocation )
                            || !committerLocation.narrowsOrEquals( beneficiaryLocation, locale )
                            && !beneficiaryLocation.narrowsOrEquals( committerLocation, locale );

                case Supervisor:
                    return ModelObject.isNullOrUnknown( committer.getActor() )
                            || ModelObject.isNullOrUnknown( beneficiary.getActor() )
                            || hasSupervisor( committer.getActor(), beneficiary.getActor(), committerOrg );

                case Supervised:
                    return ModelObject.isNullOrUnknown( committer.getActor() )
                            || ModelObject.isNullOrUnknown( beneficiary.getActor() )
                            || hasSupervisor( beneficiary.getActor(), committer.getActor(), beneficiaryOrg );

                case Self:
                    return ModelObject.isNullOrUnknown( committer.getActor() )
                            || ModelObject.isNullOrUnknown( beneficiary.getActor() )
                            || committer.getActor().equals( beneficiary.getActor() );

                case Other:
                    return ModelObject.isNullOrUnknown( committer.getActor() )
                            || ModelObject.isNullOrUnknown( beneficiary.getActor() )
                            || !committer.getActor().equals( beneficiary.getActor() );

            }
        }

        return true;
    }

    private static boolean hasSupervisor( final Actor actor, final Actor supervisor, Organization org ) {
        return CollectionUtils.exists(
                org.getJobs(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Job job = (Job) object;
                        return job.getActor().equals( actor )
                                && job.getSupervisor() != null
                                && job.getSupervisor().equals( supervisor );
                    }
                }
        );
    }

    private void beforeRemove( Segment segment ) {
        getCollaborationModel().removeSegment( segment );
    }

    private void beforeRemove( Role role ) {
        for ( Job job : findAllConfirmedJobs( role ) )
            job.setRole( null );
        for ( Part part : findAllParts( null, role, true ) )
            part.setRole( null );
    }

    private void beforeRemove( Actor actor ) {
        for ( Job job : findAllConfirmedJobs( actor ) )
            job.setActor( null );
        for ( Part part : findAllParts( null, actor, true ) )
            part.setActor( null );
    }

    private void beforeRemove( Place place ) {
        for ( Job job : findAllConfirmedJobs( place ) )
            job.setJurisdiction( null );
        for ( Part part : findAllParts( null, place, true ) )
            part.setJurisdiction( null );
        for ( Part part : findAllPartsWithExactLocation( place ) )
            part.setLocation( null );
        for ( Place p : list( Place.class ) )
            if ( place.equals( p.getWithin() ) )
                p.setWithin( null );
    }

    @Override
    public boolean cleanup( Class<? extends ModelObject> clazz, String name ) {
        return getDao().cleanup( clazz, name );
    }

    @Override
    public Level computePartPriority( Part part ) {
        return getPartPriority( part, new ArrayList<Part>() );
    }

    private static Level getPartPriority( Part part, List<Part> visited ) {
        visited.add( part );
        Level max = Level.Low;
        for ( Goal goal : part.getGoals() ) {
            if ( goal.getLevel().getOrdinal() > max.getOrdinal() )
                max = goal.getLevel();
        }
        if ( part.isTerminatesEventPhase() ) {
            for ( Goal goal : part.getSegment().getTerminatingGoals() ) {
                if ( goal.getLevel().getOrdinal() > max.getOrdinal() )
                    max = goal.getLevel();
            }
        }
        for ( Flow flow : part.requiredSends() ) {
            if ( flow.getTarget().isPart() ) {
                Part target = (Part) flow.getTarget();
                if ( !visited.contains( target ) ) {
                    Level priority = getPartPriority( target, visited );
                    if ( priority.getOrdinal() > max.getOrdinal() )
                        max = priority;
                }
            }
        }
        return max;
    }

    @Override
    public Level computeSharingPriority( Flow flow ) {
        assert flow.isSharing();
        if ( isEssential( flow, false ) ) {
            return computePartPriority( (Part) flow.getTarget() );
        } else {
            return Level.Low;
        }
    }

    @Override
    public Flow connect( Node source, Node target, String name ) {
        return connect( source, target, name, null );
    }

    @Override
    public Flow connect( Node source, Node target, String name, Long id ) {
        Flow result;
        if ( Flow.isInternal( source, target ) ) {
            result = getDao().createInternalFlow( source, target, name, id );
            source.addSend( result );
            target.addReceive( result );

        } else if ( Flow.isExternal( source, target ) ) {
            result = getDao().createExternalFlow( source, target, name, id );
            if ( source.isConnector() ) {
                target.addReceive( result );
                ( (Connector) source ).addExternalFlow( (ExternalFlow) result );
            } else {
                source.addSend( result );
                ( (Connector) target ).addExternalFlow( (ExternalFlow) result );
            }

        } else
            throw new IllegalArgumentException();

        return result;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Integer countReferences( final ModelObject mo ) {
        int count = 0;
        Iterator classes = ModelObject.referencingClasses().iterator();
        while ( classes.hasNext() ) {
            List<? extends ModelObject> mos = findAllModelObjects( (Class<? extends ModelObject>) classes.next() );
            for ( ModelObject ref : mos ) {
                if ( ref.references( mo ) ) count++;
            }
        }
        return count;
    }

    @Override
    public Boolean covers( Agreement agreement, Commitment commitment ) {
        Flow sharing = commitment.getSharing();
        Assignment beneficiary = commitment.getBeneficiary();
        if ( beneficiary.getOrganization().narrowsOrEquals( agreement.getBeneficiary(), getPlanLocale() )
                && Matcher.same( agreement.getInformation(), sharing.getName() ) ) {
            List<ElementOfInformation> eois = agreement.getEois();
            if ( eois.isEmpty() || subsetOf( sharing.getEffectiveEois(), eois ) ) {
                String usage = agreement.getUsage();
                String otherText = beneficiary.getPart().getTask();
                if ( usage.isEmpty()
                        || Matcher.same( usage, otherText )
                        || semanticMatcher.matches( usage.trim(), otherText.trim(), Proximity.HIGH ) )
                    return true;
            }
        }
        return false;
    }

    @Override
    public Connector createConnector( Segment segment ) {
        return createConnector( segment, null );
    }

    @Override
    public Connector createConnector( Segment segment, Long id ) {
        return segment.addNode( getDao().createConnector( segment, id ) );
    }

    @Override
    public Part createPart( Segment segment ) {
        return createPart( segment, null );
    }

    @Override
    public Part createPart( Segment segment, Long id ) {
        return getDao().createPart( segment, id );
    }

    @Override
    public Segment createSegment() {
        return createSegment( null );
    }

    @Override
    public Segment createSegment( Long id ) {
        return createSegment( id, null );
    }

    @Override
    public Segment createSegment( Long id, Long defaultPartId ) {
        return getDao().createSegment( id, defaultPartId );
    }

    @Override
    public Boolean encompasses( Agreement agreement, Agreement other ) {
        if ( other.getBeneficiary().narrowsOrEquals( agreement.getBeneficiary(), getPlanLocale() )
                && Matcher.same( agreement.getInformation(), other.getInformation() ) ) {
            String usage = agreement.getUsage();
            String otherText = other.getUsage();
            if ( usage.isEmpty() || Matcher.same( usage, otherText ) || semanticMatcher.matches(
                    usage.trim(),
                    otherText.trim(),
                    Proximity.HIGH ) )
                return subsetOf( other.getEois(), agreement.getEois() );
        }

        return false;
    }

    @Override
    public <T extends ModelObject> List<T> list( Class<T> clazz ) {
        return getDao().list( clazz );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> listKnownEntities( Class<T> clazz ) {
        return getDao().listKnownEntities( clazz );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> listActualEntities( Class<T> clazz, Boolean mustBeReferenced ) {
        return getDao().listActualEntities( clazz, mustBeReferenced );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> listActualEntities( Class<T> clazz ) {
        return getDao().listActualEntities( clazz );
    }


    @Override
    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelEntity> List<T> listEntitiesNarrowingOrEqualTo( final T entity ) {
        final Place locale = getPlanLocale();
        return getDao().listEntitiesNarrowingOrEqualTo( entity, locale );
    }


    @Override
    public Boolean entityExists( Class<? extends ModelEntity> clazz, String name, ModelEntity.Kind kind ) {
        return getDao().entityExists( clazz, name, kind );
    }

    @Override
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        return getDao().find( clazz, id );
    }

    @Override
    public <T extends ModelEntity> T findActualEntity( Class<T> entityClass, String name ) {
        return getDao().findActualEntity( entityClass, name );
    }

    @Override
    public <T extends ModelEntity> List<T> findAllActualEntitiesMatching(
            Class<T> entityClass,
            final T entityType ) {
        final Place locale = getPlanLocale();
        return getDao().findAllActualEntitiesMatching( entityClass, entityType, locale );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends ModelObject> List<T> findAllModelObjects( Class<T> clazz ) {
        List<T> domain;
        if ( Part.class.isAssignableFrom( clazz ) ) {
            domain = (List<T>) findAllParts();
        } else if ( Flow.class.isAssignableFrom( clazz ) ) {
            domain = (List<T>) findAllFlows();
        } else {
            domain = getDao().findAllModelObjects( clazz );
        }
        return domain;
    }

    @Override
    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelEntity> List<T> listReferencedEntities( Class<T> clazz ) {
        return getDao().listReferencedEntities( clazz );
    }


    @Override
    public <T extends ModelEntity> List<T> listKnownEntities(
            Class<T> entityClass,
            Boolean mustBeReferenced,
            Boolean includeImmutables ) {
        return getDao().listKnownEntities( entityClass, mustBeReferenced, includeImmutables );
    }


    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> listTypeEntities( Class<T> clazz ) {
        return getDao().listTypeEntities( clazz );
    }

    @Override
    public <T extends ModelEntity> List<T> listTypeEntities( Class<T> clazz, Boolean mustBeReferenced ) {
        return getDao().listTypeEntities( clazz, mustBeReferenced );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Boolean isReferenced( final ModelObject mo ) {
        if ( getCollaborationModel().references( mo ) )
            return true;
        else
            return getDao().isReferenced( mo );
    }


    @Override
    public <T extends ModelEntity> T retrieveEntity(
            Class<T> entityClass, Map<String, Object> state, String key ) {
        Object[] vals = ( (Collection<?>) state.get( key ) ).toArray();
        String name = (String) vals[0];
        boolean type = (Boolean) vals[1];
        if ( type ) {
            return findOrCreateType( entityClass, name );
        } else {
            return findOrCreate( entityClass, name );
        }
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
    public <T extends ModelEntity> T safeFindOrCreateType( Class<T> clazz, String name ) {
        return safeFindOrCreateType( clazz, name, null );
    }

    @Override
    public <T extends ModelEntity> T safeFindOrCreateActual( Class<T> clazz, String name ) {
        return safeFindOrCreateActual( clazz, name, null );
    }

    @Override
    public <T extends ModelEntity> T safeFindOrCreateActual( Class<T> clazz, String name, Long id ) {
        return getDao().safeFindOrCreateActual( clazz, name, id );
    }


    @Override
    public <T extends ModelEntity> T safeFindOrCreateType( Class<T> clazz, String name, Long id ) {
        return getDao().safeFindOrCreateType( clazz, name, id );
    }


//////////////////////////////////////////////////////////////////////

    @Override
    public Boolean isReferenced( final Classification classification ) {
        boolean hasReference = CollectionUtils.exists(
                listActualEntities( Actor.class ), new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Actor) object ).getClearances().contains( classification );
                    }
                }
        );
        hasReference = hasReference || CollectionUtils.exists(
                findAllFlows(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Flow) object ).getClassifications().contains( classification );
                    }
                }
        );
        return hasReference;
    }


    @Override
    public List<Part> findAchievers( Segment segment, Goal goal ) {
        List<Part> achievers = new ArrayList<Part>();
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( goal.isRiskMitigation() && part.isTerminatesEventPhase()
                    || part.getGoals().contains( goal ) ) {
                achievers.add( part );
            }
        }
        return achievers;
    }

    @Override
    @SuppressWarnings( {"unchecked"} )
    public List<Actor> findAllActualActors( ResourceSpec resourceSpec ) {
        Place locale = getPlanLocale();
        Set<Actor> actors = new HashSet<Actor>();
        // If the resource spec is anyone, then return no actor,
        // else it would return every actor known to the app
        if ( !resourceSpec.isAnyone() ) {
            Iterator<ResourceSpec> specs = findAllResourceSpecs().iterator();
            Iterator<ResourceSpec> actorSpecs = new FilterIterator( specs, new Predicate() {
                @Override
                public boolean evaluate( Object object ) {
                    Actor actor = ( (Specable) object ).getActor();
                    return actor != null && actor.isActual();
                }
            } );
            while ( actorSpecs.hasNext() ) {
                ResourceSpec actorResourceSpec = actorSpecs.next();
                if ( actorResourceSpec.narrowsOrEquals( resourceSpec, locale ) ) {
                    Actor actor = actorResourceSpec.getActor();
                    if ( !actor.isUnknown() /*&& !actor.isArchetype()*/ ) actors.add( actor );
                }
            }
        }

        return new ArrayList<Actor>( actors );
    }

    @Override
    public List<Assignment> findAllAssignments( Part part, Boolean includeUnknowns ) {
        if ( part.isEmpty() )
            return new ArrayList<Assignment>();
        else
            return findAllAssignments( part, includeUnknowns, false );
    }

    @Override
    public List<Assignment> findAllAssignments( Part part, Boolean includeUnknowns, Boolean includeProhibited ) {
        Place locale = getPlanLocale();
        Set<Assignment> result = new HashSet<Assignment>();
        if ( !part.isEmpty() ) {
            if ( includeProhibited || !part.isProhibited() ) {
                List<Part> parts = findSynonymousParts( part );
                for ( Employment e : findAllEmployments( part, locale ) ) {
                    Assignment assignment = new Assignment( e, part );
                    if ( !isProhibited( assignment, parts ) )
                        result.add( assignment );
                }

                if ( includeUnknowns
                        && result.isEmpty()
                        && !part.resourceSpec().isAnyone()
                        && part.getActorOrUnknown().isUnknown() ) {
                    Organization partOrg = part.getOrganizationOrUnknown();
                    if ( partOrg.isUnknown() ) {
                        Assignment assignment = new Assignment(
                                new Employment( Actor.UNKNOWN,
                                        partOrg,
                                        new Job( Actor.UNKNOWN,
                                                part.getRoleOrUnknown(),
                                                part.getJurisdiction() )
                                ),
                                part
                        );
                        if ( !isProhibited( assignment, parts ) )
                            result.add( assignment );

                    } else if ( partOrg.isType() ) {
                        for ( Organization actualOrg : listActualEntities( Organization.class ) ) {
                            if ( !containsParentOf( result, actualOrg ) ) {
                                if ( actualOrg.getAllTypes().contains( partOrg ) ) {
                                    Assignment assignment = new Assignment(
                                            new Employment( Actor.UNKNOWN,
                                                    actualOrg,
                                                    new Job( Actor.UNKNOWN,
                                                            part.getRoleOrUnknown(),
                                                            part.getJurisdiction() )
                                            ),
                                            part
                                    );
                                    if ( !isProhibited( assignment, parts ) )
                                        result.add( assignment );
                                }
                            }
                        }
                        if ( result.isEmpty() ) {
                            Assignment assignment = new Assignment(
                                    new Employment( Actor.UNKNOWN,
                                            partOrg,
                                            new Job( Actor.UNKNOWN,
                                                    part.getRoleOrUnknown(),
                                                    part.getJurisdiction() )
                                    ),
                                    part
                            );
                            if ( !isProhibited( assignment, parts ) )
                                result.add( assignment );
                        }
                    }
                }
            }
        }
        return new ArrayList<Assignment>( result );
    }

    private static boolean containsParentOf( Set<Assignment> assignments, final Organization org ) {
        return CollectionUtils.exists(
                assignments,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Assignment assignment = (Assignment) object;
                        Organization other = assignment.getOrganization();
                        return !other.isUnknown() && org.ancestors().contains( other );
                    }
                }
        );
    }

    private boolean isProhibited( Assignment assignment, List<Part> parts ) {
        Part part = assignment.getPart();
        boolean prohibited = false;
        Iterator<Part> overridingParts = findAllOverridingParts( part, parts ).iterator();
        while ( !prohibited && overridingParts.hasNext() ) {
            Part overridingPart = overridingParts.next();
            if ( overridingPart.isProhibited() ) {
                List<Assignment> overridingAssignments = findAllAssignments( overridingPart, true );
                prohibited = overridingAssignments.contains( assignment );
            }
        }
        return prohibited;
    }

    @Override
    public List<Flow> findAllCapabilitiesNamed( String name ) {
        List<Flow> capabilities = new ArrayList<Flow>();
        for ( Flow flow : findAllFlows() )
            if ( flow.isCapability() && Matcher.same( flow.getName(), name ) )
                capabilities.add( flow );
        return capabilities;
    }

    /**
     * Find all relevant channels for a given resource spec.
     *
     * @param spec the spec
     * @return the channels
     */
    @Override
    public List<Channel> findAllChannelsFor( ResourceSpec spec ) {
        Set<Channel> channels = new HashSet<Channel>();

        for ( Segment segment : list( Segment.class ) )
            for ( Iterator<Flow> flows = segment.flows(); flows.hasNext(); ) {
                Flow flow = flows.next();
                Part p = flow.getContactedPart();
                if ( p != null && spec.equals( p.resourceSpec() ) )
                    addUniqueChannels( channels, flow.getEffectiveChannels() );
            }

        if ( spec.getActor() != null ) {
            addUniqueChannels( channels, spec.getActor().getEffectiveChannels() );
            addUniqueChannels( channels, findAllChannelsFor(
                    new ResourceSpec( null, spec.getRole(),
                            spec.getOrganization(), spec.getJurisdiction() )
            ) );
        }

        if ( spec.getJurisdiction() != null )
            addUniqueChannels( channels, findAllChannelsFor(
                    new ResourceSpec( spec.getActor(), spec.getRole(),
                            spec.getOrganization(), null )
            ) );

        if ( spec.getRole() != null )
            addUniqueChannels( channels, findAllChannelsFor(
                    new ResourceSpec( spec.getActor(), null,
                            spec.getOrganization(), spec.getJurisdiction() )
            ) );

        Organization organization = spec.getOrganization();
        if ( organization != null ) {
            addUniqueChannels( channels, organization.getEffectiveChannels() );
            addUniqueChannels( channels, findAllChannelsFor(
                    new ResourceSpec( spec.getActor(), spec.getRole(),
                            null, spec.getJurisdiction() )
            ) );
        }

        return toSortedList( channels );
    }

    private static void addUniqueChannels( Set<Channel> result, List<Channel> candidates ) {
        for ( Channel channel : candidates ) {
            TransmissionMedium medium = channel.getMedium();
            if ( containsInvalidChannel( result, medium ) )
                result.remove( new Channel( medium, "" ) );
            if ( medium.isBroadcast() || !containsValidChannel( result, medium ) )
                result.add( channel );
        }
    }

    private static boolean containsInvalidChannel( Set<Channel> channels, TransmissionMedium medium ) {
        for ( Channel channel : channels )
            if ( channel.getMedium().equals( medium ) && !channel.isValid() )
                return true;
        return false;
    }

    private static boolean containsValidChannel( Set<Channel> channels, TransmissionMedium medium ) {
        for ( Channel channel : channels )
            if ( medium.equals( channel.getMedium() ) && channel.isValid() )
                return true;
        return false;
    }

    private static <T extends Comparable> List<T> toSortedList( Collection<T> objects ) {
        List<T> results = new ArrayList<T>( objects );
        Collections.sort( results );
        return results;
    }

    @Override
    public List<Commitment> findAllCommitments() {
        List<Commitment> allCommitments = new ArrayList<Commitment>();
        for ( Flow flow : findAllFlows() ) {
            allCommitments.addAll( findAllCommitments( flow ) );
        }
        return allCommitments;
    }

    @Override
    public List<Commitment> findAllCommitments( Boolean includeToSelf ) {
        List<Commitment> allCommitments = new ArrayList<Commitment>();
        for ( Flow flow : findAllFlows() ) {
            allCommitments.addAll( findAllCommitments( flow, includeToSelf ) );
        }
        return allCommitments;
    }

    @Override
    public List<Commitment> findAllCommitments( Boolean includeToSelf, Boolean includeUnknowns ) {
        List<Commitment> allCommitments = new ArrayList<Commitment>();
        for ( Flow flow : findAllFlows() ) {
            allCommitments.addAll( findAllCommitments( flow, includeToSelf, includeUnknowns ) );
        }
        return allCommitments;
    }


    @Override
    public List<Commitment> findAllCommitments( Flow flow ) {
        return findAllCommitments( flow, false );
    }

    @Override
    public List<Commitment> findAllCommitments( Flow flow, Boolean allowCommitmentsToSelf ) {
        return findAllCommitments( flow, allowCommitmentsToSelf, getAssignments( true ) );
    }

    @Override
    public List<Commitment> findAllCommitments( Flow flow, Boolean allowCommitmentsToSelf, Boolean includeUnknowns ) {
        return findAllCommitments( flow, allowCommitmentsToSelf, getAssignments( includeUnknowns ) );
    }


    @Override
    public List<Commitment> findAllCommitments( Flow flow, Boolean selfCommits, Assignments assignments ) {

        Set<Commitment> commitments = new HashSet<Commitment>();
        if ( flow.isSharing() && !flow.isProhibited() ) {
            List<Flow> allFlows = findAllFlows();
            Place locale = getPlanLocale();
            Assignments beneficiaries = assignments.assignedTo( (Part) flow.getTarget() );
            for ( Assignment committer : assignments.assignedTo( (Part) flow.getSource() ) ) {
                Actor committerActor = committer.getActor();
                for ( Assignment beneficiary : beneficiaries ) {
                    if ( ( selfCommits || !committerActor.equals( beneficiary.getActor() ) )
                            && allowsCommitment( committer, beneficiary, locale, flow ) )

                        addCommitment( new Commitment( committer, beneficiary, flow ),
                                commitments, locale, allFlows );
                }
            }
        }
        return new ArrayList<Commitment>( commitments );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Commitment> findAllBypassCommitments( final Flow flow ) {
        assert flow.isSharing();
        Set<Commitment> commitments = new HashSet<Commitment>();
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
                commitments.addAll( findAllCommitments( byPassFlow, false, false ) );
            }
        }
        return new ArrayList<Commitment>( commitments );
    }


    private void addCommitment(
            Commitment commitment, Set<Commitment> commitments, Place locale, List<Flow> allFlows ) {

        if ( !isImplicitlyProhibited( commitment, allFlows )
                && !commitments.contains( commitment ) ) {

            // TODO - This is where the time goes. How can this be optimized?  -->
            Flow commitmentSharing = commitment.getSharing();
            List<Commitment> toRemove = new ArrayList<Commitment>( commitments.size() );
            for ( Commitment c : commitments ) {
                Flow flow = c.getSharing();
                if ( flow.overrides( commitmentSharing, locale ) )
                    return;
                else if ( commitmentSharing.overrides( flow, locale ) )
                    toRemove.add( c );
            }

            commitments.removeAll( toRemove );
            // <--
            commitments.add( commitment );
        }
    }

    private boolean isImplicitlyProhibited( Commitment commitment, List<Flow> allFlows ) {
        boolean prohibited = false;
        Flow sharing = commitment.getSharing();
        Iterator<Flow> overriddingFlows = findAllOverridingFlows( sharing, allFlows ).iterator();
        Place locale = getPlanLocale();
        while ( !prohibited && overriddingFlows.hasNext() ) {
            Flow overridingFlow = overriddingFlows.next();
            if ( overridingFlow.isProhibited() ) {
                ResourceSpec committerRes = commitment.getCommitter().getResourceSpec();
                ResourceSpec beneficiaryRes = commitment.getBeneficiary().getResourceSpec();
                prohibited = committerRes.narrowsOrEquals( ( (Part) overridingFlow.getSource() ).resourceSpec(), locale )
                        && beneficiaryRes.narrowsOrEquals( ( (Part) overridingFlow.getTarget() ).resourceSpec(), locale );
            }
        }
        return prohibited;
    }

    @Override
    public List<Commitment> findAllCommitmentsCoveredBy(
            Agreement agreement,
            Organization organization,
            Assignments assignments,
            List<Flow> allFlows ) {

        Set<Commitment> results = new HashSet<Commitment>();
        for ( Actor actor : getAssignments().with( organization ).getActualActors() ) {
            List<Commitment> commitments = findAllCommitmentsOf(
                    actor,
                    assignments,
                    allFlows );
            for ( Commitment commitment : commitments )
                if ( commitment.isBetweenUnrelatedOrganizations() && covers( agreement, commitment ) )
                    results.add( commitment );
        }
        return new ArrayList<Commitment>( results );
    }

    @Override
    public List<Agreement> findAllConfirmedAgreementsCovering( Commitment commitment ) {
        List<Agreement> agreements = new ArrayList<Agreement>();
        Organization committerOrg = commitment.getCommitter().getOrganization();
        for ( Organization agreeingOrg : committerOrg.selfAndAncestors() ) {
            for ( Agreement agreement : agreeingOrg.getAgreements() ) {
                Agreement agreementFromCommitment = Agreement.from( commitment );
                if ( agreementFromCommitment != null && encompasses( agreement, agreementFromCommitment ) ) {
                    agreements.add( agreement );
                }
            }
        }
        return agreements;
    }

    @Override
    public List<Commitment> findAllCommitmentsOf(
            Specable specable,
            Assignments assignments,
            List<Flow> allFlows ) {
        Place locale = getPlanLocale();
        Set<Commitment> commitments = new HashSet<Commitment>();
        for ( Flow flow : allFlows ) {
            if ( flow.isSharing() ) {
                Assignments committers = assignments.assignedTo( (Part) flow.getSource() ).with( specable );
                Assignments beneficiaries = assignments.assignedTo( (Part) flow.getTarget() );
                for ( Assignment committer : committers ) {
                    for ( Assignment beneficiary : beneficiaries ) {
                        if ( !committer.getActor().equals( beneficiary.getActor() )
                                && allowsCommitment( committer, beneficiary, locale, flow ) ) {
                            Commitment commitment = new Commitment(
                                    committer,
                                    beneficiary,
                                    flow );
                            addCommitment( commitment, commitments, locale, allFlows );
                        }
                    }
                }
            }
        }
        return new ArrayList<Commitment>( commitments );
    }

    @Override
    public List<Commitment> findAllCommitmentsTo(
            Specable specable,
            Assignments assignments,
            List<Flow> allFlows ) {
        Place locale = getPlanLocale();
        Set<Commitment> commitments = new HashSet<Commitment>();
        for ( Flow flow : allFlows ) {
            if ( flow.isSharing() ) {
                Assignments committers = assignments.assignedTo( (Part) flow.getSource() );
                Assignments beneficiaries = assignments.assignedTo( (Part) flow.getTarget() ).with( specable );
                for ( Assignment committer : committers ) {
                    for ( Assignment beneficiary : beneficiaries ) {
                        if ( !committer.getActor().equals( beneficiary.getActor() )
                                && allowsCommitment( committer, beneficiary, locale, flow ) ) {
                            Commitment commitment = new Commitment(
                                    committer,
                                    beneficiary,
                                    flow );
                            addCommitment( commitment, commitments, locale, allFlows );
                        }
                    }
                }
            }
        }
        return new ArrayList<Commitment>( commitments );
    }

    @Override
    public List<Job> findAllConfirmedJobs( Specable specable ) {
        List<Job> jobs = new ArrayList<Job>();
        Place locale = getPlanLocale();
        for ( Organization org : listActualEntities( Organization.class ) )
            for ( Job job : org.getJobs() )
                if ( job.resourceSpec( org ).narrowsOrEquals( specable, locale ) )
                    jobs.add( job );

        return jobs;
    }

    @Override
    public List<ResourceSpec> findAllContacts( Specable specable, Boolean isSelf ) {
        Set<ResourceSpec> contacts = new HashSet<ResourceSpec>();
        if ( isSelf ) {
            contacts.addAll( findAllResourcesNarrowingOrEqualTo( specable ) );
        } else {
            List<Play> plays = findAllPlays( specable );
            for ( Play play : plays ) {
                ResourceSpec partSpec = play.getPart().resourceSpec();
                ResourceSpec otherPartSpec = play.getOtherPart().resourceSpec();
                if ( !partSpec.isAnyone() ) contacts.add( partSpec );
                if ( !otherPartSpec.isAnyone() ) contacts.add( otherPartSpec );
            }
        }
        return new ArrayList<ResourceSpec>( contacts );
    }

    @Override
    public List<Hierarchical> findAllDescendants( Hierarchical hierarchical ) {
        Set<Hierarchical> descendants = new HashSet<Hierarchical>();
        for ( ModelObject mo : findAllModelObjects() ) {
            if ( !mo.isUnknown() && mo instanceof Hierarchical && hasAncestor(
                    (Hierarchical) mo,
                    hierarchical,
                    new HashSet<Hierarchical>() ) ) {
                descendants.add( (Hierarchical) mo );
            }
        }
        return new ArrayList<Hierarchical>( descendants );
    }

    private boolean hasAncestor(
            Hierarchical hierarchical, final Hierarchical other, final Set<Hierarchical> visited ) {

        if ( visited.contains( hierarchical ) )
            return false;

        visited.add( hierarchical );
        List<? extends Hierarchical> superiors = hierarchical.getSuperiors( this );
        Object superior = CollectionUtils.find( superiors, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return hasAncestor( (Hierarchical) object, other, visited );
            }
        } );

        return !superiors.isEmpty()
                && ( superiors.contains( other ) || superior != null );
    }

    @Override
    public List<Dissemination> findAllDisseminations(
            SegmentObject segmentObject,
            Subject subject,
            Boolean showTargets ) {
        List<Dissemination> disseminations = new ArrayList<Dissemination>();
        if ( segmentObject instanceof Part ) {
            findAllDisseminationsFromPart(
                    (Part) segmentObject,
                    subject,
                    Transformation.Type.Identity,
                    new Delay(),
                    showTargets,
                    (Part) segmentObject,
                    subject,
                    disseminations );
        } else {
            Flow flow = (Flow) segmentObject;
            Part startPart = (Part) ( showTargets ? flow.getSource() : flow.getTarget() );
            findAllDisseminationsFromFlow(
                    flow,
                    subject,
                    showTargets,
                    startPart,
                    subject,
                    disseminations );
        }
        return disseminations;
    }

    private void findAllDisseminationsFromPart(
            Part part,
            Subject subject,
            Transformation.Type cumulativeTranformation,
            Delay cumulativeDelay,
            boolean showTargets,
            Part startPart,
            Subject startSubject,
            List<Dissemination> disseminations ) {
        List<Flow> candidates = showTargets
                ? part.getAllSharingSends()
                : part.getAllSharingReceives();
        for ( final Flow candidate : candidates ) {
            boolean covered = CollectionUtils.exists(
                    disseminations, new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Dissemination) object ).getFlow().equals( candidate );
                        }
                    }
            );
            if ( !covered && !candidate.isProhibited() ) {
                Part disseminationPart = (Part) ( showTargets
                        ? candidate.getTarget()
                        : candidate.getSource() );
                Delay taskDelay = disseminationPart.getCompletionTime();
                List<Dissemination> immediateDisseminations = findDisseminationsInFlow(
                        candidate,
                        subject,
                        showTargets,
                        startPart,
                        startSubject
                );
                for ( Dissemination immediateDissemination : immediateDisseminations ) {
                    if ( !disseminations.contains( immediateDissemination ) ) {
                        immediateDissemination.addToDelay( cumulativeDelay );
                        if ( immediateDissemination.getFlow().isIfTaskFails() ) {
                            immediateDissemination.addToDelay( taskDelay );
                        }
                        disseminations.add( immediateDissemination );
                        Subject nextSubject = showTargets
                                ? immediateDissemination.getSubject()
                                : immediateDissemination.getTransformedSubject();
                        findAllDisseminationsFromPart(
                                disseminationPart,
                                nextSubject,
                                cumulativeTranformation.combineWith( immediateDissemination.getTransformationType() ),
                                immediateDissemination.getDelay(),
                                showTargets,
                                startPart,
                                startSubject,
                                disseminations );
                    }
                }
            }
        }
    }

    private static List<Dissemination> findDisseminationsInFlow( Flow flow, Subject subject, boolean showTargets,
                                                                 Part startPart, Subject startSubject ) {
        List<Dissemination> disseminations = new ArrayList<Dissemination>();
        for ( ElementOfInformation eoi : flow.getEffectiveEois() ) {
            Transformation xform = eoi.getTransformation();
            if ( xform.isNone() || subject.isRoot() ) {
                if ( Matcher.same( flow.getName(), subject.getInfo() )
                        && Matcher.same( eoi.getContent(), subject.getContent() ) ) {
                    Dissemination dissemination = new Dissemination(
                            flow,
                            xform.getType(),
                            flow.getMaxDelay(),
                            new Subject( subject ),
                            new Subject( subject ),
                            startPart,
                            startSubject,
                            showTargets );
                    dissemination.setRoot( subject.isRoot() );
                    disseminations.add( dissemination );
                }
            } else {
                if ( showTargets ) {
                    if ( xform.getSubjects().contains( subject ) ) {
                        disseminations.add( new Dissemination(
                                flow,
                                xform.getType(),
                                flow.getMaxDelay(),
                                subject,
                                new Subject( flow.getName(), eoi.getContent() ),
                                startPart,
                                startSubject,
                                showTargets ) );
                    }
                } else {
                    if ( Matcher.same( flow.getName(), subject.getInfo() )
                            && Matcher.same( eoi.getContent(), subject.getContent() ) ) {
                        for ( Subject transformedSubject : xform.getSubjects() ) {
                            disseminations.add( new Dissemination(
                                    flow,
                                    xform.getType(),
                                    flow.getMaxDelay(),
                                    transformedSubject,
                                    new Subject( subject ),
                                    startPart,
                                    startSubject,
                                    showTargets ) );
                        }
                    }
                }
            }
        }
        return disseminations;
    }

    private void findAllDisseminationsFromFlow(
            Flow flow,
            Subject subject,
            boolean showTargets,
            Part startPart,
            Subject startSubject,
            List<Dissemination> disseminations ) {
        ElementOfInformation eoi = disseminatingEoi( flow, subject );
        List<Dissemination> immediateDisseminations = new ArrayList<Dissemination>();
        if ( !flow.isProhibited() && eoi != null ) {
            if ( showTargets ) {
                immediateDisseminations.add( new Dissemination(
                        flow,
                        Transformation.Type.Identity,
                        flow.getMaxDelay(),
                        subject,
                        subject,
                        startPart,
                        startSubject,
                        showTargets ) );
            } else {
                Transformation xform = eoi.getTransformation();
                if ( xform.isNone() ) {
                    immediateDisseminations.add( new Dissemination(
                            flow,
                            Transformation.Type.Identity,
                            flow.getMaxDelay(),
                            subject,
                            subject,
                            startPart,
                            startSubject,
                            showTargets ) );
                } else {
                    for ( Subject transformedSubject : xform.getSubjects() ) {
                        immediateDisseminations.add( new Dissemination(
                                flow,
                                xform.getType(),
                                flow.getMaxDelay(),
                                transformedSubject,
                                subject,
                                startPart,
                                startSubject,
                                showTargets ) );
                    }
                }
            }
            Node node = showTargets
                    ? flow.getTarget()
                    : flow.getSource();
            if ( node.isPart() ) {
                for ( Dissemination immediateDissemination : immediateDisseminations ) {
                    if ( !disseminations.contains( immediateDissemination ) ) {
                        disseminations.add( immediateDissemination );
                        Subject newSubject = showTargets
                                ? immediateDissemination.getSubject()
                                : immediateDissemination.getTransformedSubject();
                        findAllDisseminationsFromPart(
                                (Part) node,
                                newSubject,
                                immediateDissemination.getTransformationType(),
                                immediateDissemination.getDelay(),
                                showTargets,
                                startPart,
                                startSubject,
                                disseminations );
                    }
                }
            }
        }
    }

    private static ElementOfInformation disseminatingEoi( final Flow flow, final Subject subject ) {
        return (ElementOfInformation) CollectionUtils.find(
                flow.getEffectiveEois(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return Matcher.same( flow.getName(), subject.getInfo() )
                                && Matcher.same( subject.getContent(), ( (ElementOfInformation) object ).getContent() );
                    }
                }
        );
    }

    @Override
    public List<Employment> findAllEmployments( Part part, Place locale ) {
        Set<Employment> employments = new HashSet<Employment>();
        // From confirmed jobs matching the part
        for ( Organization org : listActualEntities( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( ModelEntity.implies( job.getActor(), part.getActor(), locale )
                        && ModelEntity.implies( job.getRole(), part.getRole(), locale )
                        && ModelEntity.implies( org, part.getOrganization(), locale )
                        && ModelEntity.implies( job.getJurisdiction(), part.getJurisdiction(), locale ) ) {
                    employments.add( new Employment( job.getActor(), org, job ) );
                }
            }
        }
        // Inferred from the part
        if ( part.hasActualActor() && part.hasActualOrganization() ) {
            Actor actor = part.getActor();
            Role partRole = part.hasRole() ? part.getRole() : Role.UNKNOWN;
            Job j = new Job( actor, partRole, part.getJurisdiction() );
            employments.add( new Employment( actor, part.getOrganization(), j ) );
        }

        return new ArrayList<Employment>( employments );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Employment> findAllEmploymentsForActor( final Actor actor ) {
        return (List<Employment>) CollectionUtils.select(
                findAllEmploymentsWithKnownActors(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object obj ) {
                        Employment employment = (Employment) obj;
                        Actor empActor = employment.getActor();
                        return employment.getRole() != null && empActor != null && empActor.equals( actor );
                    }
                }
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Employment> findAllEmploymentsForRole( final Role role ) {
        final Place locale = getPlanLocale();
        return (List<Employment>) CollectionUtils.select(
                findAllEmploymentsWithKnownActors(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Role empRole = ( (Employment) object ).getRole();
                        return empRole != null && empRole.narrowsOrEquals( role, locale );
                    }
                }
        );
    }

    @Override
    public List<Employment> findAllEmploymentsIn( Organization organization ) {
        List<Employment> employments = new ArrayList<Employment>();
        List<Organization> orgs = listEntitiesNarrowingOrEqualTo( organization );
        for ( Organization org : orgs ) {
            for ( Job job : org.getJobs() ) {
                employments.add( new Employment( job.getActor(), org, job ) );
            }
            for ( Job job : findUnconfirmedJobs( org ) ) {
                employments.add( new Employment( job.getActor(), org, job ) );
            }
        }
        return employments;
    }

    @Override
    public List<Employment> findAllEmploymentsWithKnownActors() {
        Set<Actor> employed = new HashSet<Actor>();
        List<Employment> employments = new ArrayList<Employment>();
        for ( Organization org : listActualEntities( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                employments.add( new Employment( job.getActor(), org, job ) );
                employed.add( job.getActor() );
            }
            for ( Job job : findUnconfirmedJobs( org ) ) {
                employments.add( new Employment( job.getActor(), org, job ) );
                employed.add( job.getActor() );
            }
        }
        for ( Actor actor : listActualEntities( Actor.class ) ) {
            if ( !employed.contains( actor ) ) {
                employments.add( new Employment( actor ) );
            }
        }
        return employments;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<? extends ModelEntity> findAllEntitiesIn( Place place ) {
        return (List<ModelEntity>) CollectionUtils.select(
                findAllModelObjectsIn( place ),
                PredicateUtils.invokerPredicate( "isEntity" ) );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<? extends ModelEntity> findAllEntitiesIn( Phase phase ) {
        return (List<ModelEntity>) CollectionUtils.select(
                findAllModelObjectsIn( phase ),
                PredicateUtils.invokerPredicate( "isEntity" ) );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<? extends ModelEntity> findAllEntitiesIn( TransmissionMedium medium ) {
        return (List<ModelEntity>) CollectionUtils.select(
                findAllModelObjectsIn( medium ),
                PredicateUtils.invokerPredicate( "isEntity" ) );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<? extends ModelEntity> findAllEntitiesIn( MaterialAsset asset ) {
        return (List<ModelEntity>) CollectionUtils.select(
                findAllModelObjectsIn( asset ),
                PredicateUtils.invokerPredicate( "isEntity" ) );
    }

    @Override
    public List<String> findAllEntityNames( Class<? extends ModelEntity> aClass ) {
        Set<String> allNames = new HashSet<String>();
        for ( ModelObject mo : listReferencedEntities( aClass ) ) {
            allNames.add( mo.getName() );
        }
        return toSortedList( allNames );
    }

    @Override
    public List<String> findAllEntityNames(
            Class<? extends ModelEntity> aClass,
            ModelEntity.Kind kind ) {
        Set<String> allNames = new HashSet<String>();
        for ( ModelEntity entity : listReferencedEntities( aClass ) ) {
            if ( entity.getKind().equals( kind ) )
                allNames.add( entity.getName() );
        }
        return toSortedList( allNames );
    }

    @Override
    public List<Flow> findAllFlows() {
        List<Flow> allFlows = new ArrayList<Flow>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                allFlows.add( flows.next() );
            }
        }
        return allFlows;
    }

    @Override
    public List<String> findAllGeonames() {
        Set<String> geonames = new HashSet<String>();
        for ( Place place : list( Place.class ) ) {
            String geoname = place.getGeoname();
            if ( geoname != null && !geoname.isEmpty() )
                geonames.add( geoname );
        }
        return new ArrayList<String>( geonames );
    }

    @Override
    public List<Goal> findAllGoalsImpactedByFailure( Part part ) {
        return findAllGoalsImpactedByFailure( part, new ArrayList<Part>() );
    }

    private static List<Goal> findAllGoalsImpactedByFailure( Part part, List<Part> visited ) {
        List<Goal> goals = new ArrayList<Goal>();
        visited.add( part );
        for ( Goal goal : part.getGoals() ) {
            if ( !goal.isImpliedIn( goals ) )
                goals.add( goal );
        }
        for ( Flow flow : part.requiredSends() ) {
            if ( flow.getTarget().isPart() ) {
                Part target = (Part) flow.getTarget();
                if ( !visited.contains( target ) ) {
                    List<Goal> flowGoals = findAllGoalsImpactedByFailure( target, visited );
                    for ( Goal flowGoal : flowGoals ) {
                        if ( !flowGoal.isImpliedIn( goals ) )
                            goals.add( flowGoal );
                    }
                }
            }
        }
        return goals;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Agreement> findAllImpliedAgreementsOf(
            Organization organization,
            Assignments assignments,
            List<Flow> allFlows ) {
        List<Agreement> agreements = new ArrayList<Agreement>();
        List<Agreement> encompassed = new ArrayList<Agreement>();
        List<Commitment> commitments = findAllCommitmentsOf(
                organization,
                assignments,
                allFlows );
        for ( final Commitment commitment : commitments ) {
            if ( ModelObject.areIdentical( commitment.getCommitter().getOrganization(), organization ) )
                if ( commitment.isBetweenUnrelatedOrganizations() ) {
                    final Agreement agreement = Agreement.from( commitment );
                    if ( agreement != null ) {
                        encompassed.addAll( (List<Agreement>) CollectionUtils.select(
                                        agreements,
                                        new Predicate() {
                                            @Override
                                            public boolean evaluate( Object object ) {
                                                return encompasses( agreement,
                                                        (Agreement) object );
                                            }
                                        }
                                )
                        );
                        agreements.add( agreement );
                    }
                }
        }
        return (List<Agreement>) CollectionUtils.subtract( agreements, encompassed );
    }

    @Override
    public Boolean isAgreedToIfRequired( Commitment commitment ) {
        Organization org = commitment.getCommitter().getOrganization();
        if ( org.isEffectiveAgreementsRequired() && commitment.isBetweenUnrelatedOrganizations() ) {
            final Agreement requiredAgreement = Agreement.from( commitment );
            return requiredAgreement != null && CollectionUtils.exists(
                    org.getAgreements(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return encompasses( (Agreement) object, requiredAgreement );
                        }
                    }
            );
        } else {
            // agreement not required
            return true;
        }
    }

    @Override
    public List<Part> findAllInitiators( EventTiming eventTiming ) {
        List<Part> initiators = new ArrayList<Part>();
        Event event = eventTiming.getEvent();
        boolean concurrent = eventTiming.getTiming() == Phase.Timing.Concurrent;
        Place locale = getPlanLocale();
        for ( Part part : findAllParts() ) {
            if ( concurrent ) {
                Event initiatedEvent = part.getInitiatedEvent();
                if ( initiatedEvent != null && event.narrowsOrEquals( initiatedEvent, locale ) ) {
                    initiators.add( part );
                }
            } else {
                // post-event
                Segment segment = part.getSegment();
                if ( part.isTerminatesEventPhase()
                        && event.narrowsOrEquals( segment.getEvent(), locale )
                        && segment.getPhase().isConcurrent() ) {
                    initiators.add( part );
                }
            }
        }
        return initiators;
    }

    @Override
    public List<String> findAllJobTitles() {
        Set<String> titles = new HashSet<String>();
        for ( Organization organization : listActualEntities( Organization.class ) ) {
            for ( Job job : organization.getJobs() ) {
                titles.add( job.getTitle() );
            }
        }
        return toSortedList( titles );
    }

    @Override
    public List<Job> findAllJobs( Organization organization, Actor actor ) {
        List<Job> jobs = new ArrayList<Job>();
        List<Organization> orgs;
        if ( organization == null )
            orgs = listActualEntities( Organization.class );
        else {
            assert organization.isActual();
            orgs = new ArrayList<Organization>();
            orgs.add( organization );
        }

        for ( Organization org : orgs ) {
            for ( Job job : org.getJobs() ) {
                if ( actor.equals( job.getActor() ) ) {
                    jobs.add( job );
                }
            }
            for ( Job job : findUnconfirmedJobs( org ) ) {
                if ( actor.equals( job.getActor() ) ) {
                    jobs.add( job );
                }
            }
        }
        return jobs;
    }

    @Override
    public List<ModelObject> findAllModelObjects() {
        List<ModelObject> allModelObjects = new ArrayList<ModelObject>();
        allModelObjects.addAll( list( ModelObject.class ) );
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                allModelObjects.add( parts.next() );
            }
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                allModelObjects.add( flows.next() );
            }
        }
        return allModelObjects;
    }


    @Override
    public List<ModelObject> findAllModelObjectsDirectlyRelatedToEvent( Event event ) {
        Set<ModelObject> mos = new HashSet<ModelObject>();
        for ( Segment segment : list( Segment.class ) ) {
            if ( segment.getEvent().equals( event ) ) {
                mos.add( segment );
            }
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.initiatesEvent() && part.getInitiatedEvent().equals( event ) ) {
                    mos.add( part );
                }
                if ( segment.getEvent().equals( event ) && part.isTerminatesEventPhase() ) {
                    mos.add( part );
                }
            }
        }
        return new ArrayList<ModelObject>( mos );
    }

    @Override
    public List<? extends ModelObject> findAllModelObjectsIn( Place place ) {
        List<ModelObject> inPlace = new ArrayList<ModelObject>();
        Place locale = getPlanLocale();
        for ( Organization org : list( Organization.class ) ) {
            if ( org.getLocation() != null && org.getLocation().narrowsOrEquals( place, locale ) )
                inPlace.add( org );
        }
        for ( Event event : listReferencedEntities( Event.class ) ) {
            if ( event.getScope() != null && event.getScope().narrowsOrEquals( place, locale ) )
                inPlace.add( event );
        }
        for ( Place p : list( Place.class ) ) {
            if ( !p.equals( place ) && p.narrowsOrEquals( place, locale ) )
                inPlace.add( p );
        }
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                Place location = part.getKnownLocation();
                if ( location != null && location.narrowsOrEquals( place, locale ) )
                    inPlace.add( part );
            }
        }
        return inPlace;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<? extends ModelObject> findAllModelObjectsIn( Phase phase ) {
        List<ModelObject> inPhase = new ArrayList<ModelObject>();
        for ( Segment segment : list( Segment.class ) ) {
            if ( segment.getPhase().equals( phase ) ) {
                inPhase.add( segment );
                inPhase.addAll( IteratorUtils.toList( segment.parts() ) );
                inPhase.addAll( IteratorUtils.toList( segment.flows() ) );
            }
        }
        return inPhase;
    }

    @Override
    public List<? extends ModelObject> findAllModelObjectsIn( TransmissionMedium medium ) {
        return medium.getEffectiveDelegatedToMedia();
    }

    @Override
    public List<? extends ModelObject> findAllModelObjectsIn( MaterialAsset asset ) {
        return asset.getDependencies();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<? extends ModelEntity> findAllNarrowingOrEqualTo( final ModelEntity entity ) {
        final Place locale = getPlanLocale();
        return (List<? extends ModelEntity>) CollectionUtils.select(
                list( entity.getClass() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (ModelEntity) object ).narrowsOrEquals( entity, locale );
                    }
                }
        );
    }

    private List<Flow> findAllOverriddenFlows( Flow sharing, List<Flow> allFlows ) {
        List<Flow> overriddenFlows = new ArrayList<Flow>();
        if ( sharing.isSharing() ) {
            Place locale = getPlanLocale();
            for ( Flow s : allFlows ) {
                if ( s.isSharing() && sharing.overrides( s, locale ) ) {
                    overriddenFlows.add( s );
                }
            }
        }
        return overriddenFlows;
    }

    @Override
    public List<Part> findAllOverriddenParts( Part part, List<Part> parts ) {
        List<Part> overriddenParts = new ArrayList<Part>();
        Place locale = getPlanLocale();
        for ( Part p : parts ) {
            if ( part.overrides( p, locale ) ) {
                overriddenParts.add( p );
            }
        }
        return overriddenParts;
    }

    private List<Flow> findAllOverridingFlows( Flow sharing, List<Flow> allFlows ) {
        List<Flow> overridingFlows = new ArrayList<Flow>();
        if ( sharing.isSharing() ) {
            Place locale = getPlanLocale();
            for ( Flow s : allFlows ) {
                if ( s.isSharing() && s.overrides( sharing, locale ) ) {
                    overridingFlows.add( s );
                }
            }
        }
        return overridingFlows;
    }

    @Override
    public List<Part> findAllOverridingParts( Part part, List<Part> parts ) {
        List<Part> overridingParts = new ArrayList<Part>();
        Place locale = getPlanLocale();
        for ( Part p : parts ) {
            if ( p.overrides( part, locale ) ) {
                overridingParts.add( p );
            }
        }
        return overridingParts;
    }

    @Override
    public List<Part> findAllParts() {
        List<Part> list = new ArrayList<Part>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                list.add( parts.next() );
            }
        }
        return list;
    }

    @Override
    public List<Part> findAllParts( Segment segment, Specable specable, Boolean exactMatch ) {
        Set<Part> list = new HashSet<Part>();
        Set<Segment> segments;
        CollaborationModel collaborationModel = getCollaborationModel();
        if ( segment == null ) {
            segments = collaborationModel.getSegments();
        } else {
            segments = new HashSet<Segment>();
            segments.add( segment );
        }

        Place locale = getPlanLocale();
        for ( Segment seg : segments ) {
            for ( Iterator<Part> parts = seg.parts(); parts.hasNext(); ) {
                Part part = parts.next();
//                if ( part.resourceSpec().matches( specable, exactMatch, plan ) ) {
                if ( part.resourceSpec().matchesOrSubsumes( specable, exactMatch, locale ) ) {
                    list.add( part );
                }
            }
        }
        // visitParts( list, resourceSpec, segment, exactMatch );
        return toSortedList( list );
    }

    @Override
    public List<Part> findAllPartsPlayedBy( Specable specable ) {
        return getAssignments().with( specable ).getParts();
    }

    @Override
    public List<Flow> findAllFlowsInvolving( ModelEntity modelEntity ) {
        List<Flow> flows = new ArrayList<Flow>();
        for ( Flow flow : findAllFlows() ) {
            if ( flow.references( modelEntity ) )
                flows.add( flow );
        }
        return flows;
    }

    @Override
    public List<Part> findAllPartsWithExactLocation( Place place ) {
        List<Part> list = new ArrayList<Part>();
        if ( place != null ) {
            for ( Part part : findAllParts() ) {
                if ( Place.samePlace( part.getKnownLocation(), place ) )
                    list.add( part );
            }
        }
        return list;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<String> findAllPlanners() {
        return (List<String>) CollectionUtils.collect(
                userDao.getDevelopers( getCollaborationModel().getUri() ),
                TransformerUtils.invokerTransformer( "getUsername" )
        );
    }

    @Override
    public List<Play> findAllPlays( Specable specable ) {
        return findAllPlays( specable, false );
    }

    @Override
    public List<Play> findAllPlays( Specable resourceSpec, Boolean specific ) {
        Set<Play> plays = new HashSet<Play>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( Play.hasPlay( flow ) ) {
                    if ( flow.getSource().isPart() ) {
                        Part part = (Part) flow.getSource();
                        if ( part.resourceSpec().matchesOrSubsumes( resourceSpec, specific,
                                getPlanLocale() ) ) {
                            // sends
                            Play play = new Play( part, flow, true );
                            plays.add( play );
                        }
                    }
                    if ( flow.getTarget().isPart() ) {
                        Part part = (Part) flow.getTarget();
                        if ( part.resourceSpec().matchesOrSubsumes( resourceSpec, specific,
                                getPlanLocale() ) ) {
                            // receives
                            Play play = new Play( part, flow, false );
                            plays.add( play );
                        }
                    }
                }
            }
        }
        return new ArrayList<Play>( plays );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends ModelObject> List<T> findAllReferencing( final ModelObject referenced, final Class<T> clazz ) {
        List<T> referencers = new ArrayList<T>();
        if ( CollaborationModel.class.isAssignableFrom( clazz ) ) {
            CollaborationModel collaborationModel = getCollaborationModel();
            if ( collaborationModel.references( referenced ) ) {
                referencers.add( (T) collaborationModel );
            }
        } else {
            for ( ModelObject referencer : findAllModelObjects( clazz ) ) {   // does not include plan
                if ( referencer.references( referenced ) ) {
                    referencers.add( (T) referencer );
                }
            }
        }
        return referencers;
    }


    @Override
    public List<Flow> findAllRelatedFlows( ResourceSpec resourceSpec, Boolean asSource ) {
        List<Flow> relatedFlows = new ArrayList<Flow>();
        Place locale = getPlanLocale();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                Node node = asSource ? flow.getSource() : flow.getTarget();
                if ( node.isPart()
                        && resourceSpec.narrowsOrEquals( ( (Part) node ).resourceSpec(), locale ) )
                    relatedFlows.add( flow );
            }
        }
        return relatedFlows;
    }

    @Override
    public List<ResourceSpec> findAllResourceSpecs() {
        Set<ResourceSpec> result = new HashSet<ResourceSpec>();
        // Specs from entities
        for ( Actor actor : list( Actor.class ) ) {
            result.add( new ResourceSpec( actor ) );
        }
        for ( Role role : list( Role.class ) ) {
            result.add( new ResourceSpec( role ) );
        }
        for ( Organization organization : list( Organization.class ) ) {
            result.add( new ResourceSpec( organization ) );
            result.addAll( organization.jobResourceSpecs() );
        }
        // Specs from segment parts
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                ResourceSpec partResourceSpec = part.resourceSpec();
                if ( !partResourceSpec.isAnyone() ) {
                    result.add( partResourceSpec );
                }
            }
        }

        return new ArrayList<ResourceSpec>( result );
    }

    @Override
    public List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( Specable specable ) {
        Place locale = getPlanLocale();
        List<ResourceSpec> list = new ArrayList<ResourceSpec>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( spec.narrowsOrEquals( specable, locale ) )
                list.add( spec );
        }
        return list;
    }

    @Override
    public List<Role> findAllRolesOf( Actor actor ) {
        Set<Role> roles = new HashSet<Role>();
        Place place = getPlanLocale();
        for ( Specable spec : findAllResourceSpecs() ) {
            if ( spec.getRole() != null ) {
                if ( spec.getActor() != null && actor.narrowsOrEquals( spec.getActor(), place )
                        || actor.isUnknown() && spec.getActor() == null )
                    roles.add( spec.getRole() );
            }
        }
        return new ArrayList<Role>( roles );
    }

    @Override
    public List<Connector> findAllSatificers( Flow need ) {
        List<Connector> satisficers = new ArrayList<Connector>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part != need.getTarget() ) {
                    Iterator<Flow> sends = part.sends();
                    while ( sends.hasNext() ) {
                        Flow send = sends.next();
                        if ( send.getTarget().isConnector()
                                && satisfiesNeed( send, need ) ) {
                            satisficers.add( (Connector) send.getTarget() );
                        }
                    }
                }
            }
        }
        return satisficers;
    }

    private boolean satisfiesNeed( Flow send, Flow need ) {
        return Matcher.same( send.getName(), need.getName() )
                && Restriction.satisfy( send.getRestrictions(), need.getRestrictions() )
                && isEOIsCoveredBy( need.getEffectiveEois(), send.getEffectiveEois() );
    }

    @SuppressWarnings( "unchecked" )
    private static boolean isEOIsCoveredBy( List<ElementOfInformation> coveredEois, final List<ElementOfInformation> coveringEois ) {
        if ( coveredEois.isEmpty() ) {
            return true;
        } else {
            List<ElementOfInformation> uncoveredEOIs =
                    (List<ElementOfInformation>) CollectionUtils.select(
                            coveredEois,
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object obj ) {
                                    final String coveredEOI = ( (ElementOfInformation) obj ).getContent();
                                    return !CollectionUtils.exists(
                                            coveringEois,
                                            new Predicate() {
                                                @Override
                                                public boolean evaluate( Object object ) {
                                                    String coveringEOI = ( (ElementOfInformation) object ).getContent();
                                                    return Matcher.same( coveredEOI, coveringEOI );
                                                }
                                            }
                                    );
                                }
                            }
                    );
            return uncoveredEOIs.isEmpty();
        }
    }

    @Override
    public List<ModelObject> findAllSegmentObjectsInvolving( ModelEntity entity ) {
        if ( entity instanceof Event ) {
            return findAllModelObjectsDirectlyRelatedToEvent( (Event) entity );
        } else {
            Set<ModelObject> segmentObjects = new HashSet<ModelObject>();
            for ( Segment segment : list( Segment.class ) ) {
                Iterator<Part> parts = segment.parts();
                while ( parts.hasNext() ) {
                    Part part = parts.next();
                    if ( part.resourceSpec().hasEntity( entity ) ) {
                        segmentObjects.add( part );
                        Iterator<Flow> sends = part.sends();
                        while ( sends.hasNext() ) segmentObjects.add( sends.next() );
                        Iterator<Flow> receives = part.receives();
                        while ( receives.hasNext() ) segmentObjects.add( receives.next() );
                    }
                    if ( ModelObject.areIdentical( part.getFunction(), entity ) ) {
                        segmentObjects.add( part );
                    }
                    if ( entity instanceof MaterialAsset
                            && part.getAssetConnections().references( (MaterialAsset) entity ) ) {
                        segmentObjects.add( part );
                    }
                }
            }
            return new ArrayList<ModelObject>( segmentObjects );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Segment> findAllSegmentsForPhase( final Phase phase ) {
        return (List<Segment>) CollectionUtils.select(
                list( Segment.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Segment) object ).getPhase().equals( phase );
                    }
                }
        );
    }

    @Override
    public List<Flow> findAllSharingFlows( Segment segment ) {
        List<Segment> segments = new ArrayList<Segment>();
        if ( segment == null ) {
            segments.addAll( getCollaborationModel().getSegments() );
        } else {
            segments.add( segment );
        }
        Set<Flow> flows = new HashSet<Flow>();
        for ( Segment seg : segments ) {
            flows.addAll( seg.getAllSharingFlows() );
        }
        return new ArrayList<Flow>( flows );
    }

    @Override
    public List<Flow> findAllSharingsAddressingNeed( Flow need ) {
        assert need.isNeed();
        List<Flow> sharings = new ArrayList<Flow>();
        // Find all synonymous sharings to the part
        Part needyPart = (Part) need.getTarget();
        sharings.addAll( findSharingFlowsMatchingNeed( needyPart, need ) );
        // Find all synonymous sharings to applicable anonymous parts within the plan segment
        /* for ( Part part : findAnonymousPartsMatching( needyPart ) ) {
            sharings.addAll( findSharingFlowsMatchingNeed( part, need ) );
        }*/
        return sharings;
    }

    private static List<Flow> findSharingFlowsMatchingNeed( Part part, Flow need ) {
        List<Flow> sharings = new ArrayList<Flow>();
        String info = need.getName();
        for ( Flow in : part.getAllSharingReceives() ) {
            if ( in.isSharing()
                    && Matcher.same( in.getName(), info )
                    && isEOIsCoveredBy( need.getEffectiveEois(), in.getEffectiveEois() )
                    // in's restrictions match the need's restrictions
                    && capabilityRestrictionsMatchNeedRestrictions( in.getRestrictions(), need.getRestrictions() ) ) {
                sharings.add( in );
            }
        }
        return sharings;
    }

    private static boolean capabilityRestrictionsMatchNeedRestrictions( final List<Restriction> capabilityRestrictions,
                                                                        List<Restriction> needRestrictions ) {
        if ( capabilityRestrictions.isEmpty() && needRestrictions.isEmpty() ) {
            return true;
        } else if ( capabilityRestrictions.size() != needRestrictions.size() ) { // ASSUMPTION: no redundancy in either list
            return false;
        } else {
            return !CollectionUtils.exists(
                    needRestrictions,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            final Restriction needRestriction = ( (Restriction) object );
                            return !CollectionUtils.exists(
                                    capabilityRestrictions,
                                    new Predicate() {
                                        @Override
                                        public boolean evaluate( Object object ) {
                                            return Restriction.implies( ( (Restriction) object ), needRestriction );
                                        }
                                    }
                            );
                        }
                    }
            );
        }
    }

    @Override
    public List<Employment> findAllSupervisedBy( Actor actor ) {
        return findAllSupervisedBy( actor, new HashSet<Actor>() );
    }

    private List<Employment> findAllSupervisedBy( Actor actor, Set<Actor> visited ) {
        List<Employment> employments = new ArrayList<Employment>();
        if ( !visited.contains( actor ) ) {
            visited.add( actor );
            for ( Organization org : listActualEntities( Organization.class ) ) {
                for ( Job job : org.getJobs() ) {
                    if ( job.getSupervisor() != null && job.getSupervisor().equals( actor ) ) {
                        Employment employment = new Employment( org, job );
                        if ( !employments.contains( employment ) ) {
                            employments.add( employment );
                            employments.addAll( findAllSupervisedBy( job.getActor(), visited ) );
                        }
                    }
                }
            }
        }
        return employments;
    }

    @Override
    public List<String> findAllTasks() {
        Set<String> tasks = new HashSet<String>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                tasks.add( parts.next().getTask() );
            }
        }
        return toSortedList( tasks );
    }

    @Override
    public List<Part> findCausesOf( Event event ) {
        List<Part> causes = new ArrayList<Part>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                Event causedEvent = part.getInitiatedEvent();
                if ( causedEvent != null && causedEvent.equals( event ) ) {
                    causes.add( part );
                }
            }
        }
        return causes;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<ElementOfInformation> findCommonEOIs( Flow flow, Flow otherFlow ) {
        List<ElementOfInformation> commonEOIs = new ArrayList<ElementOfInformation>();
        List<ElementOfInformation> shorter;
        List<ElementOfInformation> longer;
        if ( flow.getEffectiveEois().size() <= otherFlow.getEffectiveEois().size() ) {
            shorter = flow.getEffectiveEois();
            longer = otherFlow.getEffectiveEois();
        } else {
            longer = flow.getEffectiveEois();
            shorter = otherFlow.getEffectiveEois();
        }
        for ( final ElementOfInformation eoi : shorter ) {
            ElementOfInformation matching = (ElementOfInformation) CollectionUtils.find(
                    longer,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return object.equals( eoi );
                        }
                    }
            );
            if ( matching == null ) {
                matching = (ElementOfInformation) CollectionUtils.find(
                        longer,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return Matcher.same( ( (ElementOfInformation) object ).getContent(),
                                        eoi.getContent() );
                            }
                        }
                );
            }
            if ( matching != null ) {
                commonEOIs.add( ElementOfInformation.merge( eoi, matching ) );
            }
        }
        return commonEOIs;
    }

    @Override
    public List<Organization> findEmployers( Actor actor ) {
        List<Organization> employers = new ArrayList<Organization>();
        String actorName = actor.getName();
        for ( Organization org : listActualEntities( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( job.getActorName().equals( actorName ) ) {
                    if ( !employers.contains( org ) ) employers.add( org );
                }
            }
        }
        return employers;
    }

    @Override
    public <T extends ModelEntity> T findEntityType( Class<T> entityClass, String name ) {
        return getDao().findEntityType( entityClass, name );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Flow> findEssentialFlowsFrom( Part part, Boolean assumeAlternatesFail ) {
        // Find all downstream important flows, avoiding circularity
        List<Flow> importantFlows = findImportantFlowsFrom( part, new HashSet<Part>(), assumeAlternatesFail );
        // Iteratively trim "end flows" to non-useful parts
        // if not assume fails, retain only the flows without alternates.
        return keepEssentialFlows( importantFlows );
    }

    /**
     * Find all important flows downstream of a given part, without circularity.
     *
     * @param part                 a part
     * @param visited              already visited parts
     * @param assumeAlternatesFail a boolean
     * @return a list of important flows
     */
    private List<Flow> findImportantFlowsFrom( Part part, Set<Part> visited, boolean assumeAlternatesFail ) {
        if ( visited.contains( part ) ) {
            return Collections.emptyList();
        } else {
            visited.add( part );
            Set<Flow> importantFlows = new HashSet<Flow>();
            for ( Flow flow : part.getAllSharingSends() ) {
                if ( flow.isImportant()
                        && ( assumeAlternatesFail
                        || getAlternates( flow ).isEmpty()
                        && CollectionUtils.subtract( flow.intermediatedSources(), visited ).isEmpty() ) ) {
                    importantFlows.add( flow );
                    importantFlows.addAll( findImportantFlowsFrom(
                            (Part) flow.getTarget(),
                            visited, assumeAlternatesFail ) );
                }
            }
            return new ArrayList<Flow>( importantFlows );
        }
    }


    @SuppressWarnings( "unchecked" )
    private static List<Flow> keepEssentialFlows( final List<Flow> importantFlows ) {
        List<Flow> nonEssentialFlows = (List<Flow>) CollectionUtils.select(
                importantFlows,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Flow flow = (Flow) object;
                        Part target = (Part) flow.getTarget();
                        return !target.isUseful()
                                && CollectionUtils.intersection(
                                importantFlows,
                                target.getAllSharingSends() ).isEmpty();
                    }
                }
        );
        if ( nonEssentialFlows.isEmpty() ) {
            return importantFlows;
        } else {
            List<Flow> remainingFlows = (List<Flow>) CollectionUtils.subtract(
                    importantFlows,
                    nonEssentialFlows );
            return keepEssentialFlows( remainingFlows );
        }
    }

    @Override
    public List<Part> findExternalTerminators( Segment segment ) {
        List<Part> terminators = new ArrayList<Part>();
        for ( Segment sc : list( Segment.class ) ) {
            if ( !sc.equals( segment ) ) {
                Iterator<Part> parts = sc.parts();
                while ( parts.hasNext() ) {
                    Part part = parts.next();
                    if ( segment.isTerminatedBy( part ) )
                        terminators.add( part );
                }
            }
        }
        return terminators;
    }

    @Override
    public List<Part> findFailureImpacts( SegmentObject segmentObject, Boolean assumeFails ) {
        if ( segmentObject instanceof Flow ) {
            Flow flow = (Flow) segmentObject;
            if ( isEssential( flow, assumeFails ) ) {
                Part target = (Part) flow.getTarget();
                List<Part> impacted = findPartFailureImpacts( target, assumeFails );
                if ( target.isUseful() && !impacted.contains( target ) ) {
                    impacted.add( target );
                }
                return impacted;
            } else {
                return new ArrayList<Part>();
            }
        } else {
            return findPartFailureImpacts( (Part) segmentObject, assumeFails );
        }
    }

    private List<Part> findPartFailureImpacts( Part part, boolean assumeFails ) {
        Set<Part> impactedParts = new HashSet<Part>();
        for ( Flow flow : findEssentialFlowsFrom( part, assumeFails ) ) {
            Part candidate = (Part) flow.getTarget();
            if ( candidate.isUseful() ) {
                impactedParts.add( candidate );
            }
        }
        return new ArrayList<Part>( impactedParts );
    }

    @Override
    public Organization.FamilyRelationship findFamilyRelationship( Organization fromOrg, Organization toOrg ) {
        if ( ModelObject.areIdentical( fromOrg, toOrg ) )
            return Organization.FamilyRelationship.Identity;
        if ( fromOrg.getParent() == null || toOrg.getParent() == null )
            return Organization.FamilyRelationship.None;
        if ( ModelObject.areIdentical( fromOrg, toOrg.getParent() ) )
            return Organization.FamilyRelationship.Parent;
        if ( ModelObject.areIdentical( toOrg, fromOrg.getParent() ) )
            return Organization.FamilyRelationship.Child;
        if ( ModelObject.areIdentical( fromOrg.getParent(), toOrg.getParent() ) )
            return Organization.FamilyRelationship.Sibling;
        List<? extends Hierarchical> toOrgSuperiors = toOrg.getSuperiors( this );
        if ( toOrgSuperiors.contains( fromOrg ) )
            return Organization.FamilyRelationship.Ancestor;
        List<? extends Hierarchical> fromOrgSuperiors = fromOrg.getSuperiors( this );
        if ( fromOrgSuperiors.contains( toOrg ) )
            return Organization.FamilyRelationship.Descendant;
        if ( !CollectionUtils.intersection( fromOrgSuperiors, toOrgSuperiors ).isEmpty() )
            return Organization.FamilyRelationship.Cousin;
        return Organization.FamilyRelationship.None;
    }

    @Override
    public Boolean findIfPartStarted( Part part ) {
        return doFindIfPartStarted( part, new HashSet<ModelObject>() );
    }

    private boolean doFindIfPartStarted( Part part, Set<ModelObject> visited ) {
        if ( visited.contains( part ) ) return false;
        visited.add( part );
        if ( part.isOngoing() ) return true;
        if ( part.isStartsWithSegment() ) {
            return doFindIfSegmentStarted( part.getSegment(), visited );
        } else {
            boolean started = false;
            Iterator<Flow> receives = part.receives();
            while ( !started && receives.hasNext() ) {
                Flow receive = receives.next();
                if ( receive.isTriggeringToTarget() ) {
                    Node source = receive.getSource();
                    started = source.isPart() && doFindIfPartStarted( (Part) source, visited );
                }
            }
            if ( !started ) {
                Iterator<Flow> sends = part.sends();
                while ( !started && sends.hasNext() ) {
                    Flow send = sends.next();
                    // A task-triggering request from target of response.
                    if ( send.isTriggeringToSource() ) {
                        Node target = send.getTarget();
                        started = target.isPart() && doFindIfPartStarted( (Part) target, visited );
                    }
                }
            }
            return started;
        }
    }

    @Override
    public List<Part> findPartsPreceding( Part part ) {
        Set<Part> precedingParts = findAllTriggeringParts( part, new HashSet<ModelObject>() );
        // initial parts in same segment
        for ( Part segmentPart : part.getSegment().listParts() ) {
            if ( segmentPart.isOngoing() ) precedingParts.add( segmentPart );
            if ( segmentPart.isStartsWithSegment() ) precedingParts.add( segmentPart );
        }
        // initial parts in segment with same scenario
        for ( Segment sameScenarioSegment : findSegmentsWithScenariosMatching( part.getSegment() ) ) {
            for ( Part segmentPart : sameScenarioSegment.listParts() ) {
                if ( segmentPart.isOngoing() ) precedingParts.add( segmentPart );
                if ( segmentPart.isStartsWithSegment() ) precedingParts.add( segmentPart );
            }
        }
        // all parts in segments of preceding scenarios
        for ( Segment precedingSegment : findSegmentsPreceding( part.getSegment() ) ) {
            for ( Part precedingSegmentPart : precedingSegment.listParts() ) {
                precedingParts.add( precedingSegmentPart );
            }
        }
        return new ArrayList<Part>( precedingParts );
    }

    private List<Segment> findSegmentsWithScenariosMatching( Segment segment ) {
        List<Segment> matchingSegments = new ArrayList<Segment>();
        for ( Segment other : getCollaborationModel().getSegments() ) {
            if ( !other.equals( segment ) ) {
                if ( segment.getEventPhase().narrowsOrEquals( other.getEventPhase(), getPlanLocale() ) ) {
                    // todo - what about event timing contexts?
                    matchingSegments.add( other );
                }
            }
        }
        return matchingSegments;
    }

    private Set<Part> findAllTriggeringParts( Part part, Set<ModelObject> visited ) {
        Set<Part> precedingParts = new HashSet<Part>();
        if ( !visited.contains( part ) ) {
            visited.add( part );
            // triggering parts
            Iterator<Flow> receives = part.getAllSharingReceives().iterator();
            while ( receives.hasNext() ) {
                Flow receive = receives.next();
                if ( receive.isNotification() && receive.isTriggeringToTarget() ) {
                    Node source = receive.getSource();
                    if ( source.isPart() && !visited.contains( (Part) source ) ) {
                        precedingParts.add( (Part) source );
                        precedingParts.addAll( findAllTriggeringParts( (Part) source, visited ) );
                    }
                }
            }
            Iterator<Flow> sends = part.getAllSharingSends().iterator();
            while ( sends.hasNext() ) {
                Flow send = sends.next();
                if ( send.isAskedFor() && send.isTriggeringToSource() ) {
                    Node target = send.getTarget();
                    if ( target.isPart() && !visited.contains( (Part) target ) ) {
                        precedingParts.add( (Part) target );
                        precedingParts.addAll( findAllTriggeringParts( (Part) target, visited ) );
                    }
                }
            }
        }
        assert !precedingParts.contains( part );
        return precedingParts;
    }

    @SuppressWarnings( "unchecked" )
    private List<Segment> findSegmentsPreceding( final Segment segment ) {
        return (List<Segment>) CollectionUtils.select(
                getCollaborationModel().getSegments(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Segment otherSegment = (Segment) object;
                        return otherSegment.precedes( segment );
                    }
                }
        );
    }


    private boolean doFindIfSegmentStarted( Segment segment, Set<ModelObject> visited ) {
        if ( getCollaborationModel().isIncident( segment.getEvent() ) ) return true;
        if ( visited.contains( segment ) ) return false;
        visited.add( segment );
        boolean started = false;
        Iterator<Part> initiators = findInitiators( segment ).iterator();
        while ( !started && initiators.hasNext() ) {
            started = doFindIfPartStarted( initiators.next(), visited );
        }
        return started;
    }

    @Override
    public Boolean findIfSegmentStarted( Segment segment ) {
        return doFindIfSegmentStarted( segment, new HashSet<ModelObject>() );

    }

    @Override
    public List<Part> findInitiators( Segment segment ) {
        List<Part> initiators = new ArrayList<Part>();
        for ( Segment sc : list( Segment.class ) ) {
            if ( sc != segment ) {
                Iterator<Part> parts = sc.parts();
                while ( parts.hasNext() ) {
                    Part part = parts.next();
                    if ( segment.isInitiatedBy( part ) ) initiators.add( part );
                }
            }
        }
        return initiators;
    }


    @Override
    public <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name ) {
        return getDao().findOrCreate( clazz, name );
    }

    @Override
    public <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name, Long id ) {
        return getDao().findOrCreate( clazz, name, id );
    }

    @Override
    public <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name ) {
        return getDao().findOrCreateType( clazz, name );
    }

    @Override
    public <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name, Long id ) {
        return getDao().findOrCreateType( clazz, name, id );
    }

    @Override
    public List<Flow> findOverriddenSharingReceives( Part part ) {
        List<Flow> overriddenReceives = new ArrayList<Flow>();
        final Place locale = getPlanLocale();
        List<Part> parts = findSynonymousParts( part );
        for ( Part overriding : findAllOverridingParts( part, parts ) ) {
            for ( final Flow sharingReceive : part.getAllSharingReceives() ) {
                boolean matched = CollectionUtils.exists(
                        overriding.getAllSharingReceives(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (Flow) object ).matchesInfoOf( sharingReceive, locale );
                            }
                        }
                );
                if ( matched ) {
                    overriddenReceives.add( sharingReceive );
                }
            }
        }

        return overriddenReceives;
    }

    @Override
    public List<Flow> findOverriddenSharingSends( Part part ) {
        List<Flow> overriddenSends = new ArrayList<Flow>();
        final Place locale = getPlanLocale();
        List<Part> parts = findSynonymousParts( part );
        for ( Part overriding : findAllOverridingParts( part, parts ) ) {
            for ( final Flow sharingSend : part.getAllSharingSends() ) {
                boolean matched = CollectionUtils.exists(
                        overriding.getAllSharingSends(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (Flow) object ).matchesInfoOf( sharingSend, locale );
                            }
                        }
                );
                if ( matched ) {
                    overriddenSends.add( sharingSend );
                }
            }
        }
        return overriddenSends;
    }


    @Override
    public List<Part> findPartsInitiatingEvent( Event event ) {
        List<Part> initiatingParts = new ArrayList<Part>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                Event initiatedEvent = part.getInitiatedEvent();
                if ( initiatedEvent != null && initiatedEvent.equals( event ) )
                    initiatingParts.add( part );
            }
        }
        return initiatingParts;
    }

    @Override
    public List<Part> findPartsStartingWithEventIn( Segment segment ) {
        List<Part> startedParts = new ArrayList<Part>();
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isStartsWithSegment() )
                startedParts.add( part );
        }
        return startedParts;
    }

    @Override
    public List<Part> findPartsTerminatingEventPhaseIn( Segment segment ) {
        List<Part> terminatingParts = new ArrayList<Part>();
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isTerminatesEventPhase() )
                terminatingParts.add( part );
        }
        return terminatingParts;
    }

    @Override
    public List<Event> findPlannedEvents() {
        CollaborationModel collaborationModel = getCollaborationModel();
        List<Event> plannedEvents = new ArrayList<Event>();
        for ( Event event : listReferencedEntities( Event.class ) ) {
            if ( !collaborationModel.isIncident( event ) ) plannedEvents.add( event );
        }
        return plannedEvents;
    }

    @Override
    public List<Hierarchical> findRoots( Hierarchical hierarchical ) {
        Set<Hierarchical> roots = findRoots( hierarchical, new HashSet<Hierarchical>() );
        return new ArrayList<Hierarchical>( roots );
    }

    private Set<Hierarchical> findRoots( Hierarchical hierarchical, Set<Hierarchical> visited ) {
        Set<Hierarchical> roots = new HashSet<Hierarchical>();
        if ( !visited.contains( hierarchical ) ) {
            visited.add( hierarchical );
            List<? extends Hierarchical> superiors = hierarchical.getSuperiors( this );
            if ( superiors.isEmpty() ) {
                roots.add( hierarchical );
            } else {
                for ( Hierarchical superior : superiors ) {
                    roots.addAll( findRoots( superior, visited ) );
                }
            }
        }
        return roots;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Segment> findSegmentsRespondingTo( final Event event ) {
        return (List<Segment>) CollectionUtils.select(
                list( Segment.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Event repondedEvent = ( (Segment) object ).getEvent();
                        return repondedEvent != null && repondedEvent.equals( event );
                    }
                }
        );
    }

    @Override
    public List<Part> findSynonymousParts( Part part ) {
        List<Part> matchingParts = new ArrayList<Part>();
        for ( Segment segment : getCollaborationModel().getSegments() ) {
            for ( Part other : segment.listParts() ) {
                if ( Matcher.same( part.getTask(), other.getTask() ) ) {
                    matchingParts.add( other );
                }
            }
        }
        return matchingParts;
    }


    @Override
    public List<Tag> findTagDomain() {
        Set<Tag> domain = new HashSet<Tag>();
        for ( ModelObject mo : findAllModelObjects() ) {
            List<Tag> tags = mo.getTags();
            for ( Tag tag : tags ) {
                for ( String s : tag.getAllComponents() ) {
                    domain.add( new Tag( s ) );
                }
            }
        }
        return new ArrayList<Tag>( domain );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<ModelEntity> findTaskedEntities(
            Segment segment,
            Class entityClass,
            final ModelEntity.Kind kind ) {
        List<ModelEntity> entities =
                segment == null
                        ? list( entityClass )
                        : listEntitiesTaskedInSegment( entityClass, segment, kind );
        return (List<ModelEntity>) CollectionUtils.select(
                entities,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        ModelEntity entity = (ModelEntity) object;
                        return !entity.isUnknown()
                                && entity.getKind().equals( kind );
                    }
                }
        );
    }

    @Override
    public List<Part> findTerminators( Segment segment ) {
        List<Part> terminators = new ArrayList<Part>();
        for ( Segment sc : list( Segment.class ) ) {
            Iterator<Part> parts = sc.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( segment.isTerminatedBy( part ) )
                    terminators.add( part );
            }
        }
        return terminators;
    }

    @Override
    public List<Part> findTerminatorsOf( Event event ) {
        List<Part> terminators = new ArrayList<Part>();
        for ( Segment segment : list( Segment.class ) ) {
            if ( segment.getEvent().equals( event ) && segment.getPhase().isConcurrent() ) {
                Iterator<Part> parts = segment.parts();
                while ( parts.hasNext() ) {
                    Part part = parts.next();
                    if ( part.isTerminatesEventPhase() ) {
                        terminators.add( part );
                    }
                }
            }
        }
        return terminators;
    }

    @Override
    public List<Job> findUnconfirmedJobs( Organization organization ) {
        Place locale = getPlanLocale();
        Set<Job> unconfirmedJobs = new HashSet<Job>();
        List<Job> confirmedJobs = organization.getJobs();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                Actor actor = part.getActor();
                ResourceSpec partSpec = part.resourceSpec();
                Organization partOrg = part.getOrganizationOrUnknown();
                if ( actor != null
                        && actor.isActual()
                        && !partOrg.isUnknown()
                        && ( partOrg.isType() && organization.narrowsOrEquals( partOrg, locale )
                        || partOrg.isActual() && organization.equals( partOrg ) ) ) {

                    Job job = Job.from( new ResourceSpec( actor,
                            partSpec.getRole(),
                            organization,
                            partSpec.getJurisdiction() ) );
                    if ( !confirmedJobs.contains( job ) )
                        unconfirmedJobs.add( job );
                }
            }
        }
        return new ArrayList<Job>( unconfirmedJobs );
    }

    @Override
    public List<Actor> findSupervised( Actor supervisor ) {
        return findAllSupervisedSafe( supervisor, null, new HashSet<Actor>() );
    }

    private List<Actor> findAllSupervisedSafe( Actor supervisor, Organization org, Set<Actor> visited ) {
        Set<Actor> allSupervised = new HashSet<Actor>();
        if ( !visited.contains( supervisor ) ) {
            visited.add( supervisor );
            for ( Actor actor : listActualEntities( Actor.class ) ) {
                for ( Employment employment : findAllEmploymentsForActor( actor ) ) {
                    Actor sup = employment.getSupervisor();
                    if ( sup != null
                            && sup.equals( supervisor )
                            && ( org == null || employment.getOrganization().equals( org ) ) ) {
                        allSupervised.add( actor );
                        allSupervised.addAll( findAllSupervisedSafe( actor, employment.getOrganization(), visited ) );
                    }
                }
            }
        }
        return new ArrayList<Actor>( allSupervised );
    }

    @Override
    public List<Flow> findUnconnectedNeeds( Part part ) {
        List<Flow> unconnectedNeeds = new ArrayList<Flow>();
        Iterator<Flow> receives = part.receives();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            if ( receive.getSource().isConnector() ) {
                if ( !( (Connector) receive.getSource() ).externalFlows().hasNext() ) {
                    Iterator<Flow> others = part.receives();
                    boolean connected = false;
                    while ( !connected && others.hasNext() ) {
                        Flow other = others.next();
                        Node source = other.getSource();
                        connected = !source.isConnector()
                                && Matcher.same( receive.getName(), other.getName() );
                    }
                    if ( !connected ) unconnectedNeeds.add( receive );
                }
            }
        }
        return unconnectedNeeds;
    }

    @Override
    public List<Flow[]> findUntappedSatisfactions( final Part part ) {
        List<Flow[]> untapped = new ArrayList<Flow[]>();
        for ( Flow need : part.getNeeds() ) {
            for ( Connector connector : findAllSatificers( need ) ) {
                boolean connected = CollectionUtils.exists(
                        IteratorUtils.toList( connector.externalFlows() ),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (ExternalFlow) object ).getTarget().equals( part );
                            }
                        }
                );
                if ( !connected ) {
                    Flow[] satisfaction = new Flow[2];
                    satisfaction[0] = need;
                    satisfaction[1] = connector.getInnerFlow();
                    untapped.add( satisfaction );
                }
            }
        }
        return untapped;
    }

    @Override
    public List<Flow> findUnusedCapabilities( Part part ) {
        List<Flow> unusedCapabilities = new ArrayList<Flow>();
        Iterator<Flow> sends = part.sends();
        while ( sends.hasNext() ) {
            Flow send = sends.next();
            if ( send.getTarget().isConnector() ) {
                if ( !( (Connector) send.getTarget() ).externalFlows().hasNext() ) {
                    // A capability
                    Iterator<Flow> others = part.sends();
                    boolean used = false;
                    while ( !used && others.hasNext() ) {
                        Flow other = others.next();
                        Node target = other.getTarget();
                        used = !target.isConnector()
                                && Matcher.same( send.getName(), other.getName() );
                    }
                    if ( !used ) unusedCapabilities.add( send );
                }
            }
        }
        return unusedCapabilities;
    }

    @Override
    public String findUserEmail( String userName ) {
        ChannelsUser user = userDao.getUserWithIdentity( userName );
        if ( user != null ) {
            return user.getEmail();
        } else {
            return null;
        }
    }

    @Override
    public String findUserFullName( String userName ) {
        if ( userName == null || userName.isEmpty() ) return null;
        ChannelsUser user = userDao.getUserWithIdentity( userName );
        if ( user != null ) {
            return user.getFullName();
        } else {
            return null;
        }
    }

    @Override
    public String findUserNormalizedFullName( String userName ) {
        ChannelsUser user = userDao.getUserWithIdentity( userName );
        if ( user != null ) {
            return user.getNormalizedFullName( false );
        } else {
            return "?";
        }
    }

    @Override
    public String findUserRole( String userName ) {
        ChannelsUser user = userDao.getUserWithIdentity( userName );
        if ( user != null ) {
            return user.getRole( user.getPlanUri() );
        } else {
            return null;
        }
    }

    /**
     * Get alternate flows.
     *
     * @param flow a flow
     * @return a list of flows @param flow
     */
    @Override
    public List<Flow> getAlternates( Flow flow ) {
        // TODO - Revise for transformations
        List<Flow> answer = new ArrayList<Flow>();
        if ( flow.isSharing() ) {
            Part target = (Part) flow.getTarget();
            for ( Iterator<Flow> it = target.receives(); it.hasNext(); ) {
                Flow alternate = it.next();
                if ( !alternate.equals( flow ) && alternate.isSharing()
                        && Matcher.same( flow.getName(), alternate.getName() )
                        && subsetOf( flow.getEffectiveEois(), alternate.getEffectiveEois() ) )
                    answer.add( alternate );
            }
        }

        return answer;
    }

    @Override
    public Assignments getAssignments() {
        return getAssignments( true );
    }

    @Override
    public Assignments getAssignments( Boolean includeUnknowns ) {
        return getAssignments( includeUnknowns, false );
    }

    @Override
    public Assignments getAssignments( Boolean includeUnknowns, Boolean includeProhibited ) {
        Place locale = getPlanLocale();
        Assignments result = new Assignments( locale );
        Set<Assignment> assignments = new HashSet<Assignment>();
        for ( Segment segment : list( Segment.class ) )
            for ( Iterator<Part> pi = segment.parts(); pi.hasNext(); )
                assignments.addAll( findAllAssignments( pi.next(), includeUnknowns, includeProhibited ) );

        result.add( assignments );
        return result;
    }

    /**
     * Get the persistence store accessor.
     *
     * @return the dao
     */
    @Override
    public ModelDao getDao() {
        return modelManager.getDao( getCollaborationModel() );
    }

    // QUERIES (no change to model)

    @Override
    public Segment getDefaultSegment() {
        return toSortedList( list( Segment.class ) ).get( 0 );
    }

    /**
     * Get extended title for the part.
     *
     * @param sep  separator string
     * @param part a part
     * @return a string
     */
    public String getFullTitle( String sep, Part part ) {
        String label = "";
        if ( part.getActor() != null ) {
            label += part.getActor().getName();
        }
        if ( part.getRole() != null ) {
            if ( !label.isEmpty() )
                label += sep;
            if ( !label.isEmpty() )
                label += "as ";
            label += part.getRole().getName();
        }
        if ( part.getJurisdiction() != null ) {
            if ( !label.isEmpty() )
                label += sep + "for ";
            label += part.getJurisdiction().getName();
        }
        if ( part.getOrganization() != null ) {
            if ( !label.isEmpty() )
                label += sep + "in ";
            label += part.getOrganization().getName();
        }
        if ( !label.isEmpty() )
            label += sep;
        label += part.getTask();
        if ( part.isRepeating() ) {
            label += " (every " + part.getRepeatsEvery() + ")";
        }
        return label;
    }

    /**
     * Get extended title for the part.
     *
     * @param part a part
     * @return a string
     */
    public String getTooltip( Part part ) {
        String label = "";
        if ( part.getActor() != null ) {
            label += part.getActor().getName();
        }
        if ( part.getRole() != null ) {
            if ( !label.isEmpty() )
                label += " ";
            if ( !label.isEmpty() )
                label += "as ";
            label += part.getRole().getName();
        }
        if ( part.getJurisdiction() != null ) {
            if ( !label.isEmpty() )
                label += " " + "for ";
            label += part.getJurisdiction().getName();
        }
        if ( part.getOrganization() != null ) {
            if ( !label.isEmpty() )
                label += " " + "in ";
            label += part.getOrganization().getName();
        }
        if ( !label.isEmpty() )
            label += " - ";
        label += part.getTask();
        if ( part.isRepeating() ) {
            label += " (every " + part.getRepeatsEvery() + ")";
        }
        return label;
    }


    @Override
    public String getSimplifiedTitle( String sep, Part part, int maxLineLength ) {
        String label = "";
        if ( part.getActor() != null ) {
            label += part.getActor().getName();
        } else {
            if ( part.getRole() != null ) {
                if ( !label.isEmpty() )
                    label += sep;
                label += part.getRole().getName();
            }
        }
        if ( !label.isEmpty() )
            label += sep;
        label += ChannelsUtils.split( part.getTask(), sep, 3, maxLineLength );
        return label;
    }


    @Override
    public Commitments getAllCommitments() {
        return Commitments.all( this );
    }

    @Override
    public Commitments getAllCommitments( Boolean includeToSelf ) {
        return Commitments.all( this, includeToSelf );
    }

    @Override
    public Commitments getAllCommitments( Boolean includeToSelf, Boolean includeUnknowns ) {
        return Commitments.all( this, includeToSelf, includeUnknowns );

    }


    @Override
    @SuppressWarnings( "unchecked" )
    public Actor getKnownActualActor( Part part ) {
        List<Actor> knownActors = (List<Actor>) CollectionUtils.collect( findAllAssignments( part, false ),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        Assignment assignment = (Assignment) input;
                        return assignment.getActor();
                    }
                }
        );

        return knownActors.size() == 1 ? knownActors.get( 0 ) : null;
    }

    @Override
    public CollaborationModel getCollaborationModel() {
        return ChannelsUser.plan();
    }

    @Override
    public String getTitle( Actor actor ) {
        for ( Job job : findAllJobs( null, actor ) ) {
            String title = job.getTitle().trim();
            if ( !title.isEmpty() )
                return title;
        }

        return "";
    }

    /**
     * Instantiate a gaol from a serialization map.
     *
     * @param map a map
     * @return a goal
     */
    @Override
    public Goal goalFromMap( Map<String, Object> map ) {
        Goal goal = new Goal();
        goal.setCategory( Goal.Category.valueOf( (String) map.get( "category" ) ) );
        goal.setDescription( (String) map.get( "description" ) );
        goal.setLevel( Level.valueOf( (String) map.get( "level" ) ) );
        goal.setPositive( (Boolean) map.get( "positive" ) );
        goal.setEndsWithSegment( (Boolean) map.get( "ends" ) );
        goal.setOrganization( retrieveEntity( Organization.class, map, "organization" ) );
        return goal;
    }

    @Override
    public AssetConnection assetConnectionFromMap( Map<String, Object> map ) {
        AssetConnection assetConnection = new AssetConnection();
        assetConnection.setType( AssetConnection.Type.valueOf( (String) map.get( "type" ) ) );
        assetConnection.setAsset( retrieveEntity( MaterialAsset.class, map, "asset" ) );
        assetConnection.setConsuming( (Boolean) map.get( "consuming" ) );
        assetConnection.setCritical( (Boolean) map.get( "critical" ) );
        assetConnection.setForwarding( (Boolean) map.get( "forwarding" ) );
        return assetConnection;
    }

    /**
     * Whether there are common EOIs in two free-form texts.
     *
     * @param flow      a flow
     * @param otherFlow a flow
     * @return a boolean
     */
    @Override
    public Boolean hasCommonEOIs( Flow flow, Flow otherFlow ) {
        List<ElementOfInformation> eois = flow.getEffectiveEois();
        final List<ElementOfInformation> otherEois = otherFlow.getEffectiveEois();
        return CollectionUtils.exists(
                eois,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final String eoi = ( (ElementOfInformation) object ).getContent();
                        return CollectionUtils.exists(
                                otherEois,
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        String otherEoi = ( (ElementOfInformation) object ).getContent();
                                        return Matcher.same( eoi, otherEoi );
                                    }
                                }
                        );
                    }
                }
        );
    }

    @Override
    public List<String> findAllEoiNames() {
        Set<String> eoiNames = new HashSet<String>();
        for ( Flow flow : findAllFlows() ) {
            for ( ElementOfInformation eoi : flow.getEffectiveEois() ) {
                eoiNames.add( ChannelsUtils.smartUncapitalize( eoi.getContent() ) );
            }
        }
        List<String> result = new ArrayList<String>( eoiNames );
        Collections.sort( result );
        return result;
    }


    @Override
    public Boolean isAgreementRequired( Commitment commitment ) {
        return commitment.getCommitter().getOrganization().isEffectiveAgreementsRequired()
                && commitment.isBetweenUnrelatedOrganizations();
    }

    @Override
    public Boolean isCoveredByAgreement( final Commitment commitment ) {
        return CollectionUtils.exists(
                commitment.getCommitter().getOrganization().getAgreements(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return covers(
                                (Agreement) object,
                                commitment );
                    }
                }
        );
    }

    /**
     * Whether the flow could be essential to risk mitigation.
     *
     * @param flow        the flow
     * @param assumeFails whether alternate flows are assumed
     * @return a boolean
     */
    @Override
    public Boolean isEssential( Flow flow, boolean assumeFails ) {
        return flow.isImportant()
                && ( assumeFails || getAlternates( flow ).isEmpty() )
                && !isSharingWithSelf( flow );
    }

    @Override
    public Boolean isExecutedBy( Part part, final ModelEntity entity ) {
        if ( entity.isActual() ) {
            if ( part.resourceSpec().hasEntity( entity ) ) return true;
            List<Assignment> assignments = findAllAssignments( part, false );
            return CollectionUtils.exists(
                    assignments,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Assignment assignment = (Assignment) object;
                            return assignment.hasEntity( entity );
                        }
                    }
            );
        } else {
            ResourceSpec partSpec = part.resourceSpec();
            return partSpec.hasEntityOrBroader( entity, getPlanLocale() );
        }
    }

    @Override
    public Boolean isInitiated( Segment segment ) {
        return !findInitiators( segment ).isEmpty();
    }

    @Override
    public Boolean isInvolved( ModelEntity modelEntity, Assignments assignments, Commitments commitments ) {
        return modelEntity.isInvolvedIn( assignments, commitments );
    }

    @Override
    public Boolean isInvolvementExpected( ModelEntity modelEntity ) {
        return getCollaborationModel().getInvolvements().contains( modelEntity );
    }

    @Override
    public Boolean isOverridden( Part part ) {
        return !findAllOverridingParts(
                part, findSynonymousParts( part ) ).isEmpty();
    }

    @Override
    public Boolean isOverridden( Flow flow ) {
        return !findAllOverridingFlows( flow, findAllFlows() ).isEmpty();
    }

    @Override
    public Boolean isOverriding( Part part ) {
        return !findAllOverriddenParts( part, findSynonymousParts( part ) ).isEmpty();
    }

    @Override
    public Boolean isOverriding( Flow flow ) {
        return !findAllOverriddenFlows( flow, findAllFlows() ).isEmpty();
    }

    /**
     * Whether a flow is a sharing flow where source actor is target actor.
     *
     * @param flow a flow
     * @return a boolean
     */
    @Override
    public boolean isSharingWithSelf( Flow flow ) {
        boolean sharingWithSelf = false;
        if ( flow.isSharing() ) {
            Actor onlySource = getKnownActualActor( (Part) flow.getSource() );
            if ( onlySource != null ) {
                Actor onlyTarget = getKnownActualActor( (Part) flow.getTarget() );
                if ( onlyTarget != null ) {
                    sharingWithSelf = onlySource.equals( onlyTarget );
                }
            }
        }
        return sharingWithSelf;
    }

    @Override
    public Boolean likelyRelated( String text, String otherText ) {
        String text1 = StringUtils.uncapitalize( text );
        String otherText1 = StringUtils.uncapitalize( otherText );
        return Matcher.matches( text, otherText ) || Matcher.same( text1, otherText1 ) || semanticMatcher.matches(
                text1.trim(), otherText1.trim(), Proximity.HIGH );
    }

    @Override
    public Boolean likelyRelated( Tag tag, Tag other ) {
        if ( tag.equals( other ) ) return true;
        List<String> elements = tag.getElements();
        List<String> otherElements = other.getElements();
        Iterator<String> shorter = elements.size() < otherElements.size()
                ? elements.iterator()
                : otherElements.iterator();
        Iterator<String> longer = elements.size() >= otherElements.size()
                ? elements.iterator()
                : otherElements.iterator();
        boolean similar = true;
        while ( similar && shorter.hasNext() ) {
            similar = likelyRelated( shorter.next(), longer.next() );
        }
        return similar;
    }


    @Override
    public <T extends ModelEntity> List<T> listEntitiesTaskedInSegment(
            Class<T> entityClass,
            Segment segment,
            ModelEntity.Kind kind ) {
        Set<T> result = new HashSet<T>();
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() ) {
            List<Assignment> assignments = findAllAssignments( parts.next(), false );
            for ( Assignment assignment : assignments ) {
                T entity = assignment.getActualEntityAssigned( entityClass );
                if ( kind.equals( ModelEntity.Kind.Actual ) ) {
                    if ( entity != null ) result.add( entity );
                } else {
                    result.addAll( assignment.getEntityTypesAssigned( entityClass ) );
                }
            }
        }
/*
        Set<T> result = new HashSet<T>();
        List<T> entities = kind.equals( ModelEntity.Kind.Actual )
                ? listActualEntities( entityClass )
                : listTypeEntities( entityClass );
        for ( T entity : entities ) {
            if ( !entity.isUnknown() ) {
                Iterator<Flow> flows = segment.flows();
                while ( flows.hasNext() ) {
                    Flow flow = flows.next();
                    if ( flow.getSource().isPart() && flow.getTarget().isPart() ) {
                        Part sourcePart = (Part) flow.getSource();
                        Part targetPart = (Part) flow.getTarget();
                        if ( isExecutedBy( sourcePart, entity )
                                || isExecutedBy( targetPart, entity ) ) {
                            result.add( entity );
                        }
                    }
                }
            }
        }
*/
        return new ArrayList<T>( result );
    }

    @Override
    public void onDestroy() {
        // Do nothing
    }

    @Override
    public void remove( ModelObject object ) {
        beforeRemove( object );
        getDao().remove( object );
    }

    private void beforeRemove( ModelObject object ) {
        if ( Actor.class.isAssignableFrom( object.getClass() ) )
            beforeRemove( (Actor) object );
        else if ( Place.class.isAssignableFrom( object.getClass() ) )
            beforeRemove( (Place) object );
        else if ( Role.class.isAssignableFrom( object.getClass() ) )
            beforeRemove( (Role) object );
        else if ( Segment.class.isAssignableFrom( object.getClass() ) )
            beforeRemove( (Segment) object );
    }

    /**
     * Whether none in a list eois is without a strong match with some in another list.
     *
     * @param eois     a list of elements of information
     * @param superset a list of elements of information
     * @return a boolean
     */
    @Override
    public Boolean subsetOf( List<ElementOfInformation> eois, final List<ElementOfInformation> superset ) {
        return !CollectionUtils.exists( eois, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                final String eoiContent = ( (ElementOfInformation) object ).getContent();
                return !CollectionUtils.exists( superset, new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return Matcher.same( eoiContent, ( (ElementOfInformation) object ).getContent() );
                    }
                } );
            }
        } );
    }

    @Override
    public List<Actor> findAllSupervisorsOf( Actor actor ) {
        Set<Actor> supervisors = new HashSet<Actor>();
        for ( Employment employment : findAllEmploymentsForActor( actor ) ) {
            Actor supervisor = employment.getSupervisor();
            if ( supervisor != null )
                supervisors.add( supervisor );
        }
        return new ArrayList<Actor>( supervisors );
    }

    @Override
    public List<Actor> findAllFixedSupervisorsOf( Actor actor ) {
        Set<Actor> supervisors = new HashSet<Actor>();
        for ( Employment employment : findAllEmploymentsForActor( actor ) ) {
            if ( !employment.getOrganization().isPlaceHolder() ) {
                Actor supervisor = employment.getSupervisor();
                if ( supervisor != null )
                    supervisors.add( supervisor );
            }
        }
        return new ArrayList<Actor>( supervisors );
    }


    @Override
    public String makeNameForNewEntity( Class<? extends ModelEntity> entityClass ) {
        boolean nameTaken = false;
        String baseName = ModelEntity.NEW_NAME;
        String name = baseName;
        int i = 2;
        do {
            nameTaken = getDao().find( entityClass, name ) != null;
            if ( nameTaken ) {
                name = baseName + Integer.toString( i );
                i++;
            }
        } while ( nameTaken );
        return name;
    }

    @Override
    public List<Organization> listPlaceholderOrganizations() {
        return (List<Organization>) CollectionUtils.select(
                listActualEntities( Organization.class, true ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Organization) object ).isPlaceHolder();
                    }
                }
        );
    }

    public List<Organization> listFixedOrganizations() {
        return (List<Organization>) CollectionUtils.select(
                listActualEntities( Organization.class, true ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (Organization) object ).isPlaceHolder();
                    }
                }
        );
    }


    @SuppressWarnings( "unchecked" )
    @Override
    public List<Organization> findDirectAndIndirectEmployers( List<Employment> employments ) {
        Set<Organization> allEmployers = new HashSet<Organization>();
        List<Organization> directEmployers = (List<Organization>) CollectionUtils.collect(
                employments,
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (Employment) input ).getOrganization();
                    }
                }
        );
        for ( Organization org : directEmployers ) {
            allEmployers.addAll( org.selfAndAncestors() );
        }
        return new ArrayList<Organization>( allEmployers );
    }

    @Override
    public void update( ModelObject object ) {
        getDao().update( object );
    }

    //-------------------------------
    @Override
    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    @Override
    public ModelManager getModelManager() {
        return modelManager;
    }

    @Override
    public Place getPlanLocale() {
        return getCollaborationModel().getLocale();
    }

    public SemanticMatcher getSemanticMatcher() {
        return semanticMatcher;
    }

    public void setSemanticMatcher( SemanticMatcher semanticMatcher ) {
        this.semanticMatcher = semanticMatcher;
    }

    @Override
    public UserRecordService getUserInfoService() {
        return userDao;
    }

    public void setUserDao( UserRecordService userDao ) {
        this.userDao = userDao;
    }

    public SurveysDAO getSurveysDAO() {
        return surveysDAO;
    }

    public void setSurveysDAO( SurveysDAO surveysDAO ) {
        this.surveysDAO = surveysDAO;
    }

    @Override
    public boolean exists( Class<? extends ModelObject> clazz, Long id, Date dateOfRecord ) {
        try {
            return id != null && getDao().find( clazz, id, dateOfRecord ) != null;
        } catch ( NotFoundException e ) {
            LOG.warn( "Does not exist: " + clazz.getSimpleName() + " at " + id + " recorded on " + dateOfRecord );
            return false;
        }

    }

    @Override
    public Boolean isEventCausedByATask( final Event event ) {
        return CollectionUtils.exists(
                list( Part.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Part) object ).isInitiatiorOfEvent( event );
                    }
                }
        );
    }

    @Override
    public List<Actor> findAllActorsEmployedBy( Organization organization ) {
        Set<Actor> actors = new HashSet<Actor>();
        for ( Organization org : organization.selfAndAncestors() ) {
            for ( Employment employment : findAllEmploymentsIn( org ) ) {
                actors.add( employment.getActor() );
            }
        }
        return new ArrayList<Actor>( actors );
    }

    @Override
    public List<MaterialAsset> findAllAssetsDirectlyAvailableTo( Assignment assignment,
                                                                 List<AssetSupplyRelationship> allAssetSupplyRelationships ) {
        Set<MaterialAsset> availableAssets = new HashSet<MaterialAsset>();
        // produced
        Part part = assignment.getPart();
        for ( AssetConnection assetConnection : part.getAssetConnections() ) {
            if ( assetConnection.isProducing() ) {
                availableAssets.add( assetConnection.getAsset() );
            }
        }
        // provisioned
        availableAssets.addAll( findAllAssetsProvisionedTo( assignment, allAssetSupplyRelationships ) );
        // stocked by organization assigned to task
        Organization organization = assignment.getOrganization();
        for ( AssetConnection assetConnection : organization.getAssetConnections() ) {
            if ( assetConnection.isStocking() ) {
                availableAssets.add( assetConnection.getAsset() );
            }
        }
        return new ArrayList<MaterialAsset>( availableAssets );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<MaterialAsset> findAllAssetsProvisionedTo( final Assignment assignment,
                                                           List<AssetSupplyRelationship> allAssetSupplyRelationships ) {
        List<AssetSupplyRelationship> supplyRels =
                (List<AssetSupplyRelationship>) CollectionUtils.select(
                        allAssetSupplyRelationships,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (AssetSupplyRelationship) object ).getSupplied().equals( assignment );
                            }
                        }
                );
        Set<MaterialAsset> suppliedAssets = new HashSet<MaterialAsset>();
        for ( AssetSupplyRelationship supplyRel : supplyRels ) {
            suppliedAssets.addAll( supplyRel.getAssets() );
        }
        return new ArrayList<MaterialAsset>( suppliedAssets );
    }

    @Override
    public List<Part> findAllPartsVisibleTo( Part part ) {
        List<Part> visibleParts = new ArrayList<Part>();
        visibleParts.addAll( part.getSegment().listParts() );
        for ( Flow flow : part.getAllSharingReceives() ) {
            if ( flow.isExternal() ) {
                visibleParts.add( (Part) flow.getSource() );
            }
        }
        for ( Flow flow : part.getAllSharingSends() ) {
            if ( flow.isExternal() ) {
                visibleParts.add( (Part) flow.getTarget() );
            }
        }
        visibleParts.remove( part );
        return visibleParts;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<MaterialAsset> findAllUsersOfAssetField( final MaterialAsset materialAsset, final AssetField field ) {
        return (List<MaterialAsset>) CollectionUtils.select(
                listActualEntities( MaterialAsset.class, true ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        MaterialAsset candidate = (MaterialAsset) object;
                        return candidate.narrowsOrEquals( materialAsset )
                                && candidate.getValuedFields().contains( field );
                    }
                }
        );
    }

    @Override
    public List<AssetSupplyRelationship> findAllAssetSupplyRelationships( Assignments allAssignments,
                                                                          Commitments allCommitments ) {
        List<AssetSupplyRelationship> results = new ArrayList<AssetSupplyRelationship>();
        for ( Part part : list( Part.class ) ) {
            for ( MaterialAsset asset : findAssetsProvisionedBy( part ) ) {
                for ( Assignment supplier : allAssignments.assignedTo( part ) )
                    findAssetSupplyRelationships(
                            supplier, // the supplying assignment
                            asset,
                            null, // a demand forwarding assignment
                            results,
                            allCommitments );
            }
        }
        return results;
    }

    private List<MaterialAsset> findAssetsProvisionedBy( Part part ) {
        Set<MaterialAsset> assets = new HashSet<MaterialAsset>();
        for ( Flow flow : part.getAllSharingSends() ) {
            assets.addAll( flow.getAssetConnections().findAssetsProvisioned() );
        }
        return new ArrayList<MaterialAsset>( assets );
    }

    @SuppressWarnings( "unchecked" )
    private void findAssetSupplyRelationships( final Assignment supplier,
                                               final MaterialAsset asset,
                                               final Assignment forwardingAssignment, // not null if traveling down forwarder chain
                                               List<AssetSupplyRelationship> results,
                                               Commitments allCommitments ) { // parts visited

        if ( forwardingAssignment == null || supplier != forwardingAssignment ) { // avoid circularity
            if ( forwardingAssignment == null ) { // look for supply links from supplier assignment to then follow to supplied assignments
                Commitments supplyCommitments = allCommitments.committing( supplier ).provisioning( asset ); // supplying follows sends
                for ( Commitment supplyCommitment : supplyCommitments ) {
                    Assignment nextInChain = supplyCommitment.getBeneficiary();
                    boolean isReceivingForwardedDemand = !allCommitments.initiatedBy( nextInChain )
                            .forwardingDemandFor( asset ).isEmpty();
                    if ( isReceivingForwardedDemand ) {
                        findAssetSupplyRelationships( supplier, asset, nextInChain, results, allCommitments );
                    } else { // direct supply
                        addSupplyRelationship( supplier, nextInChain, asset, results );
                    }
                }
            } else { // looking for demand or demand forwarding received by the current assignment in the chain
                Commitments forwardedDemandCommitments = allCommitments.involvingButNotInitiatedBy( forwardingAssignment ).forwardingDemandFor( asset );
                for ( Commitment forwardedDemandCommitment : forwardedDemandCommitments ) { // go down demand forwarding
                    Assignment nextInChain = forwardedDemandCommitment.getSharing().isNotification() // demands received from non-initiated flows
                            ? forwardedDemandCommitment.getCommitter()
                            : forwardedDemandCommitment.getBeneficiary();
                    findAssetSupplyRelationships( supplier, asset, nextInChain, results, allCommitments );
                }
                Commitments demandCommitments = allCommitments.involvingButNotInitiatedBy( forwardingAssignment ).demanding( asset );
                for ( Commitment demandCommitment : demandCommitments ) { // stop forwarding demand chain at initial demand
                    Assignment supplied = demandCommitment.getSharing().isNotification()
                            ? demandCommitment.getCommitter()
                            : demandCommitment.getBeneficiary();
                    addSupplyRelationship( supplier, supplied, asset, results );
                }
            }

        }
    }

    private void addSupplyRelationship( Assignment supplier,
                                        Assignment supplied,
                                        MaterialAsset asset,
                                        List<AssetSupplyRelationship> results ) {
        AssetSupplyRelationship rel = new AssetSupplyRelationship( supplier, supplied );
        if ( !results.contains( rel ) ) {
            results.add( rel );
        }
        int index = results.indexOf( rel );
        results.get( index ).addAsset( asset );
    }

    @Override
    public Boolean isAssetAvailableToAssignment( final Assignment assignment,
                                                 final MaterialAsset asset,
                                                 final List<AssetSupplyRelationship> assetSupplyRelationships,
                                                 final Assignments allAssignments ) {
        return isAssetDirectlyAvailableToAssignment( assignment, asset, assetSupplyRelationships )
                || !findPrecedingAssignmentsWithAssetDirectlyAvailable(
                assignment,
                asset,
                assetSupplyRelationships,
                allAssignments ).isEmpty();
    }

    @Override
    public Assignments findPrecedingAssignmentsWithAssetDirectlyAvailable( Assignment assignment,
                                                                            final MaterialAsset asset,
                                                                            final List<AssetSupplyRelationship> assetSupplyRelationships,
                                                                            Assignments allAssignments ) {
        Assignments result = new Assignments( getPlanLocale() );
        for ( Part precedingPart : findPartsPreceding( assignment.getPart() ) ) { // find ALL parts preceding this assignment directly or indirectly
            Assignments precedingAssignments = allAssignments.assignedTo( precedingPart ).with( assignment.getActor() );
            for ( Assignment precedingAssignment : precedingAssignments ) {
                boolean isConsumed = precedingAssignment.getPart().getAssetConnections().isConsumed( asset );
                boolean directlyAvailable = !isConsumed
                        && isAssetDirectlyAvailableToAssignment(
                        precedingAssignment,
                        asset,
                        assetSupplyRelationships );
                if ( directlyAvailable ) {
                    result.add( precedingAssignment );
                }
            }
        }
        return result;
    }

    @Override
    public Boolean isAssetDirectlyAvailableToAssignment( Assignment assignment,
                                                          final MaterialAsset asset,
                                                          List<AssetSupplyRelationship> assetSupplyRelationships ) {
        return CollectionUtils.exists(
                findAllAssetsDirectlyAvailableTo(
                        assignment,
                        assetSupplyRelationships ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (MaterialAsset) object ).narrowsOrEquals( asset );
                    }
                }
        );

    }


/*

    @SuppressWarnings( "unchecked" )
    private List<Part> safeFindAllSupplied( Part part, final MaterialAsset asset, final boolean forwarded, Set<Part> visited ) {
        List<Part> answer = new ArrayList<Part>();
        if ( !visited.contains( part ) ) {
            Set<Part> suppliedParts = new HashSet<Part>();
            visited.add( part );
            // received and demanding the asset, perhaps forwarding
            List<Flow> receivedProvisioningFlows = (List<Flow>) CollectionUtils.select(
                    part.getAllSharingReceives(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Flow flow = (Flow) object;
                            return flow.isNotification()
                                    && ( forwarded
                                    ? !flow.getAssetConnections().demanding().about( asset ).isEmpty()
                                    : !flow.getAssetConnections().provisioning().about( asset ).isEmpty() );
                        }
                    }
            );
            for ( Flow receivedProvisioningFlow : receivedProvisioningFlows ) {
                Part suppliedPart = (Part) receivedProvisioningFlow.getSource(); // notifying part
                boolean forwarding = !receivedProvisioningFlow.getAssetConnections().about( asset ).forwarding().isEmpty();
                if ( !forwarding ) {
                    suppliedParts.add( suppliedPart );
                } else {
                    suppliedParts.addAll( safeFindAllSupplied( suppliedPart, asset, true, visited ) );
                }
            }
            // sent and demanding the asset, perhaps forwarding
            List<Flow> sentProvisioningFlows = (List<Flow>) CollectionUtils.select(
                    part.getAllSharingSends(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Flow flow = (Flow) object;
                            return flow.isAskedFor()
                                    && ( forwarded
                                    ? !flow.getAssetConnections().demanding().about( asset ).isEmpty()
                                    : !flow.getAssetConnections().provisioning().about( asset ).isEmpty() );
                        }
                    }
            );
            for ( Flow sentProvisioningFlow : sentProvisioningFlows ) {
                Part requestingPart = (Part) sentProvisioningFlow.getTarget(); // replied-to part
                boolean forwarding = !sentProvisioningFlow.getAssetConnections().about( asset ).forwarding().isEmpty();
                if ( !forwarding ) {
                    suppliedParts.add( requestingPart );
                } else {
                    suppliedParts.addAll( safeFindAllSupplied( requestingPart, asset, true, visited ) );
                }
            }
            // If can't find supplied part, return none.
            answer.addAll( suppliedParts );
        }
        return answer;
    }
*/

}

