package com.mindalliance.channels.query;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Agreement;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.Dissemination;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.EventTiming;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Flow.Restriction;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.model.InvalidEntityKindException;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.model.Subject;
import com.mindalliance.channels.model.Tag;
import com.mindalliance.channels.model.Transformation;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.nlp.Matcher;
import com.mindalliance.channels.nlp.Proximity;
import com.mindalliance.channels.nlp.SemanticMatcher;
import com.mindalliance.channels.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Query service instance.
 */
public class DefaultQueryService implements QueryService, InitializingBean {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultQueryService.class );

    /**
     * The plan manager.
     */
    private PlanManager planManager;

    /**
     * An attachment manager.
     */
    private AttachmentManager attachmentManager;

    /**
     * Semantic matcher.
     */
    private SemanticMatcher semanticMatcher;

    /**
     * File user details service.
     */
    private UserService userService;

    //=============================================

    public DefaultQueryService(
            PlanManager planManager,
            AttachmentManager attachmentManager,
            SemanticMatcher semanticMatcher,
            UserService userService ) {
        this.planManager = planManager;
        this.attachmentManager = attachmentManager;
        this.semanticMatcher = semanticMatcher;
        this.userService = userService;
    }

    /**
     * Required for CGLIB proxies...
     */
    DefaultQueryService() {
    }

    @Override
    public PlanManager getPlanManager() {
        return planManager;
    }

    public SemanticMatcher getSemanticMatcher() {
        return semanticMatcher;
    }

    public void setSemanticMatcher( SemanticMatcher semanticMatcher ) {
        this.semanticMatcher = semanticMatcher;
    }

    public void setUserService( UserService userService ) {
        this.userService = userService;
    }

    /**
     * Get the persistence store accessor.
     *
     * @return the dao
     */
    @Override
    public PlanDao getDao() {
        return planManager.getDao( getPlan() );
    }

    @Override
    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    @Override
    public void onDestroy() {
        // Do nothing
    }

    /**
     * Make sure plans are valid initialized with some proper http://bit.ly/24Reg.
     */
    @Override
    public void afterPropertiesSet() {
        planManager.assignPlans();
    }

    @Override
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        try {
            return getDao().find( clazz, id );
        } catch ( NotFoundException exc ) {
            try {
                return findUnknown( clazz, id );
            } catch ( NotFoundException e ) {
                return findUniversal( clazz, id );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    private <T extends ModelObject> T findUniversal( Class<T> clazz, long id ) throws NotFoundException {
        if ( clazz.isAssignableFrom( Actor.class )
                && ModelEntity.getUniversalTypeFor( Actor.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Actor.class );
        else if ( clazz.isAssignableFrom( Event.class )
                && ModelEntity.getUniversalTypeFor( Event.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Event.class );
        else if ( clazz.isAssignableFrom( Organization.class )
                && ModelEntity.getUniversalTypeFor( Organization.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Organization.class );
        else if ( clazz.isAssignableFrom( Place.class )
                && ModelEntity.getUniversalTypeFor( Place.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Place.class );
        else if ( clazz.isAssignableFrom( Role.class )
                && ModelEntity.getUniversalTypeFor( Role.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Role.class );
        else if ( clazz.isAssignableFrom( TransmissionMedium.class )
                && ModelEntity.getUniversalTypeFor( TransmissionMedium.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( TransmissionMedium.class );
        else if ( clazz.isAssignableFrom( Participation.class )
                && ModelEntity.getUniversalTypeFor( Participation.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Participation.class );
        else
            throw new NotFoundException();
    }

    @SuppressWarnings( "unchecked" )
    private <T extends ModelObject> T findUnknown( Class<T> clazz, long id ) throws
            NotFoundException {
        if ( clazz.isAssignableFrom( Actor.class ) && Actor.UNKNOWN.getId() == id )
            return (T) Actor.UNKNOWN;
        else if ( clazz.isAssignableFrom( Event.class ) && Event.UNKNOWN.getId() == id )
            return (T) Event.UNKNOWN;
        else if ( clazz.isAssignableFrom( Organization.class ) && Organization.UNKNOWN.getId() == id )
            return (T) Organization.UNKNOWN;
        else if ( clazz.isAssignableFrom( Place.class ) && Place.UNKNOWN.getId() == id )
            return (T) Place.UNKNOWN;
        else if ( clazz.isAssignableFrom( Role.class ) && Role.UNKNOWN.getId() == id )
            return (T) Role.UNKNOWN;
        else if ( clazz.isAssignableFrom( TransmissionMedium.class ) && TransmissionMedium.UNKNOWN.getId() == id )
            return (T) TransmissionMedium.UNKNOWN;
        else if ( clazz.isAssignableFrom( Participation.class ) && Participation.UNKNOWN.getId() == id )
            return (T) Participation.UNKNOWN;
        else
            throw new NotFoundException();
    }


    @Override
    public <T extends ModelObject> List<T> list( Class<T> clazz ) {
        return getDao().list( clazz );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> listTypeEntities( Class<T> clazz ) {
        return (List<T>) CollectionUtils.select(
                list( clazz ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (T) object ).isType();
                    }
                }
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> listActualEntities( Class<T> clazz ) {
        return (List<T>) CollectionUtils.select(
                list( clazz ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (T) object ).isActual();
                    }
                }
        );
    }

    @Override
    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelEntity> List<T> listReferencedEntities( Class<T> clazz ) {
        return (List<T>) CollectionUtils.select( list( clazz ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        ModelEntity entity = (ModelEntity) object;
                        return entity.isImmutable() && !entity.isUnknown()
                                || isReferenced( entity );
                    }
                } );
    }

    @Override
    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelEntity> List<T> listEntitiesNarrowingOrEqualTo( final T entity ) {
        final Place place = getPlan().getLocale();
        return (List<T>) CollectionUtils.select(
                list( entity.getClass() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (ModelEntity) object ).narrowsOrEquals( entity, place );
                    }
                }
        );
    }

    @Override
    public <T extends ModelEntity> T safeFindOrCreate( Class<T> clazz, String name ) {
        return findOrCreate( clazz, name, null );
    }

    @Override
    public <T extends ModelEntity> T safeFindOrCreate( Class<T> clazz, String name, Long id ) {
        if ( name != null && !name.trim().isEmpty() ) {
            String root = ChannelsUtils.stripExtraBlanks( name );
            if ( !name.equals( root ) ) {
                LOG.warn( "\"" + name + "\""
                        + " of " + clazz.getSimpleName()
                        + "[" + id + "]"
                        + " stripped to \"" + root + "\"" );
            }
            String candidateName = root;
            int i = 1;
            while ( i < 10 ) {
                try {
                    return findOrCreate( clazz, candidateName, id );
                } catch ( InvalidEntityKindException ignored ) {
                    LOG.warn( "Entity name conflict creating actual " + candidateName );
                    candidateName = root + " (" + i + ')';
                    i++;
                }
            }
            LOG.warn( "Unable to create actual " + root );
        }
        return null;
    }


    @Override
    public <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name ) {
        return findOrCreate( clazz, name, null );
    }

    @Override
    public <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name, Long id ) {
        T result;
        // If entity can only be a type, find or create a type.
        if ( !ModelEntity.canBeActualOrType( clazz )
                && ModelEntity.defaultKindFor( clazz ).equals( ModelEntity.Kind.Type ) )
            result = findOrCreateType( clazz, name, id );

        else if ( ModelEntity.getUniversalType( name, clazz ) == null ) {
            result = getDao().findOrCreate( clazz, name, id );
            if ( result.isType() ) {
                throw new InvalidEntityKindException(
                        clazz.getSimpleName() + ' ' + name + " is a type" );
            }
            result.setActual();

        } else
            throw new InvalidEntityKindException(
                    clazz.getSimpleName() + ' ' + name + " is a type" );

        return result;
    }

    @Override
    public Boolean entityExists( Class<? extends ModelEntity> clazz, String name, ModelEntity.Kind kind ) {
        ModelEntity entity = ModelEntity.getUniversalType( name, clazz );
        if ( entity == null ) entity = getDao().find( clazz, name );
        return entity != null && entity.getKind().equals( kind );
    }

    @Override
    public <T extends ModelEntity> T findActualEntity( Class<T> entityClass, String name ) {
        T result = null;
        T entity = getDao().find( entityClass, name );
        if ( entity != null ) {
            if ( entity.isActual() ) result = entity;
        }
        return result;
    }

    @Override
    public <T extends ModelEntity> T findEntityType( Class<T> entityClass, String name ) {
        T result = null;
        T entity = getDao().find( entityClass, name );
        if ( entity != null ) {
            if ( entity.isType() ) result = entity;
        }
        return result;
    }

    @Override
    public <T extends ModelEntity> T safeFindOrCreateType( Class<T> clazz, String name, Long id ) {
        T entityType = null;
        if ( name != null && !name.trim().isEmpty() ) {
            String candidateName = name.trim();
            boolean success = false;
            int i = 0;
            while ( !success ) {
                try {
                    entityType = findOrCreateType( clazz, candidateName, id );
                    success = true;
                } catch ( InvalidEntityKindException ignored ) {
                    LOG.warn( "Entity name conflict creating type {}", candidateName );
                    candidateName = name.trim() + " type";
                    if ( i > 0 ) candidateName = candidateName + " (" + i + ")";
                    i++;
                }
            }
        }
        return entityType;
    }

    @Override
    public <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name ) {
        return findOrCreateType( clazz, name, null );
    }

    @Override
    public <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name, Long id ) {
        T entityType = ModelEntity.getUniversalType( name, clazz );
        if ( entityType == null ) {
            entityType = getDao().findOrCreate( clazz, name, id );
            if ( entityType.isActual() )
                throw new InvalidEntityKindException( clazz.getSimpleName() + ' ' + name + " is actual" );
            entityType.setType();
        }
        return entityType;
    }

    @Override
    public void add( ModelObject object ) {
        getDao().add( object );
    }

    @Override
    public void add( ModelObject object, Long id ) {
        getDao().add( object, id );
    }

    @Override
    public void update( ModelObject object ) {
        getDao().update( object );
    }

    @Override
    public void remove( ModelObject object ) {
        object.beforeRemove( this );
        getDao().remove( object );
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
    public Boolean isInitiated( Segment segment ) {
        return !findInitiators( segment ).isEmpty();
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

    private static void addUniqueChannels( Set<Channel> result, List<Channel> candidates ) {
        for ( Channel channel : candidates ) {
            TransmissionMedium medium = channel.getMedium();
            if ( containsInvalidChannel( result, medium ) )
                result.remove( new Channel( medium, "" ) );
            if ( medium.isBroadcast() || !containsValidChannel( result, medium ) )
                result.add( channel );
        }
    }

    // QUERIES (no change to model)

    @Override
    public Segment getDefaultSegment() {
        return toSortedList( list( Segment.class ) ).get( 0 );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends ModelObject> List<T> findAllReferencing( final ModelObject mo, Class<T> clazz ) {
        return (List<T>) CollectionUtils.select(
                findAllModelObjects( clazz ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (ModelObject) object ).references( mo );
                    }
                }
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Boolean isReferenced( final ModelObject mo ) {
        if ( mo instanceof Participation ) {
            // Participations are not referenced per se but are not obsolete if they name a registered user.
            return ( (Participation) mo ).hasUser( this );
        } else {
            boolean hasReference = false;
            Iterator classes = ModelObject.referencingClasses().iterator();
            if ( getPlan().references( mo ) ) return true;
            while ( !hasReference && classes.hasNext() ) {
                List<? extends ModelObject> mos = findAllModelObjects( (Class<? extends ModelObject>) classes.next() );
                hasReference = CollectionUtils.exists(
                        mos,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (ModelObject) object ).references( mo );
                            }
                        }
                );
            }
            return hasReference;
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Integer countReferences( final ModelObject mo ) {
        Set<ModelObject> referencers = new HashSet<ModelObject>();
        if ( mo instanceof Participation ) {
            // Participations are not referenced per se but are not obsolete if they name a registered user.
            if ( ( (Participation) mo ).hasUser( this ) ) referencers.add( mo );
        } else {
            boolean hasReference = false;
            Iterator classes = ModelObject.referencingClasses().iterator();
            if ( getPlan().references( mo ) ) referencers.add( getPlan() );
            while ( classes.hasNext() ) {
                List<? extends ModelObject> mos = findAllModelObjects( (Class<? extends ModelObject>) classes.next() );
                for ( ModelObject ref : mos ) {
                    if ( ref.references( mo ) ) referencers.add( ref );
                }
            }
        }
        return referencers.size();
    }

    @Override
    public Boolean isReferenced( final Classification classification ) {
        boolean hasReference = CollectionUtils.exists( listActualEntities( Actor.class ),
                new Predicate() {
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
        // Specs from plan segment parts
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
        Place locale = getPlan().getLocale();
        List<ResourceSpec> list = new ArrayList<ResourceSpec>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( spec.narrowsOrEquals( specable, locale ) )
                list.add( spec );
        }
        return list;
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
                                getPlan().getLocale() ) ) {
                            // sends
                            Play play = new Play( part, flow, true );
                            plays.add( play );
                        }
                    }
                    if ( flow.getTarget().isPart() ) {
                        Part part = (Part) flow.getTarget();
                        if ( part.resourceSpec().matchesOrSubsumes( resourceSpec, specific,
                                getPlan().getLocale() ) ) {
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
    public List<Issue> findAllUserIssues( ModelObject identifiable ) {
        return getDao().findAllUserIssues( identifiable );
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
            return partSpec.hasEntityOrBroader( entity, getPlan().getLocale() );
        }
    }


    @Override
    public List<Flow> findAllRelatedFlows( ResourceSpec resourceSpec, Boolean asSource ) {
        List<Flow> relatedFlows = new ArrayList<Flow>();
        Place locale = getPlan().getLocale();
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
    @SuppressWarnings( {"unchecked"} )
    public List<Actor> findAllActualActors( ResourceSpec resourceSpec ) {
        Place locale = getPlan().getLocale();
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
                    if ( !actor.isUnknown() && !actor.isArchetype() ) actors.add( actor );
                }
            }
        }

        return new ArrayList<Actor>( actors );
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
                            spec.getOrganization(), spec.getJurisdiction() ) ) );
        }

        if ( spec.getJurisdiction() != null )
            addUniqueChannels( channels, findAllChannelsFor(
                    new ResourceSpec( spec.getActor(), spec.getRole(),
                            spec.getOrganization(), null ) ) );

        if ( spec.getRole() != null )
            addUniqueChannels( channels, findAllChannelsFor(
                    new ResourceSpec( spec.getActor(), null,
                            spec.getOrganization(), spec.getJurisdiction() ) ) );

        Organization organization = spec.getOrganization();
        if ( organization != null ) {
            addUniqueChannels( channels, organization.getEffectiveChannels() );
            addUniqueChannels( channels, findAllChannelsFor(
                    new ResourceSpec( spec.getActor(), spec.getRole(),
                            null, spec.getJurisdiction() ) ) );
        }

        return toSortedList( channels );
    }

    private static boolean containsValidChannel( Set<Channel> channels, TransmissionMedium medium ) {
        for ( Channel channel : channels )
            if ( medium.equals( channel.getMedium() ) && channel.isValid() )
                return true;
        return false;
    }

    private static boolean containsInvalidChannel( Set<Channel> channels, TransmissionMedium medium ) {
        for ( Channel channel : channels )
            if ( channel.getMedium().equals( medium ) && !channel.isValid() )
                return true;
        return false;
    }

    @Override
    public List<Job> findUnconfirmedJobs( Organization organization ) {
        Place locale = getPlan().getLocale();
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
                        && (  partOrg.isType() && organization.narrowsOrEquals( partOrg, locale )
                                || partOrg.isActual() && organization.equals( partOrg )  ) ) {

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

    private static <T extends Comparable> List<T> toSortedList( Collection<T> objects ) {
        List<T> results = new ArrayList<T>( objects );
        Collections.sort( results );
        return results;
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
    public List<Role> findAllRolesOf( Actor actor ) {
        Set<Role> roles = new HashSet<Role>();
        Place place = getPlan().getLocale();
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
                }
            }
            return new ArrayList<ModelObject>( segmentObjects );
        }
    }

    /*
    */
/*
    public List<Actor> findActualActors( Organization organization, Role role, Segment segment ) {
        return findActualActors( organization, role, null, segment );
    }

    */
/*
    public List<Actor> findActualActors( Organization organization, Role role, Place jurisdiction, Segment segment ) {
        Set<Actor> actors = new HashSet<Actor>();
        boolean noActorRoleFound = false;

        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            boolean sameOrg = Organization.UNKNOWN.equals( organization ) ?
                    part.getOrganization() == null
                    : organization.equals( part.getOrganization() );
            boolean sameRole = Role.UNKNOWN.equals( role ) ?
                    part.getRole() == null
                    : role.equals( part.getRole() );
            boolean sameJurisdiction = jurisdiction == null ||
                    Place.UNKNOWN.equals( jurisdiction )
                    ? part.getJurisdiction() == null
                    : jurisdiction.equals( part.getJurisdiction() );
            if ( sameOrg && sameRole && sameJurisdiction ) {
                Actor actor = part.getActor();
                if ( actor != null && actor.isActual() )
                    actors.add( part.getActor() );
                else
                    noActorRoleFound = true;
            }
        }
        return noActorRoleFound ? findActualActors( organization, role )
                : toSortedList( actors );
    }

*/

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
    public List<Job> findAllConfirmedJobs( Specable specable ) {
        List<Job> jobs = new ArrayList<Job>();
        Place locale = getPlan().getLocale();
        for ( Organization org : listActualEntities( Organization.class ) )
            for ( Job job : org.getJobs() )
                if ( job.resourceSpec( org ).narrowsOrEquals( specable, locale ) )
                    jobs.add( job );

        return jobs;
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
    public Boolean findIfPartStarted( Part part ) {
        return doFindIfPartStarted( part, new HashSet<ModelObject>() );
    }

    private boolean doFindIfPartStarted( Part part, Set<ModelObject> visited ) {
        if ( visited.contains( part ) ) return false;
        visited.add( part );
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
    public Boolean findIfSegmentStarted( Segment segment ) {
        return doFindIfSegmentStarted( segment, new HashSet<ModelObject>() );

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
        Plan plan = getPlan();
        if ( segment == null ) {
            segments = plan.getSegments();
        } else {
            segments = new HashSet<Segment>();
            segments.add( segment );
        }

        Place locale = plan.getLocale();
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
    public List<Part> findAllPartsWithExactLocation( Place place ) {
        List<Part> list = new ArrayList<Part>();
        if ( place != null ) {
            for ( Part part : findAllParts() ) {
                if ( Place.samePlace( part.getLocation(), place ) )
                    list.add( part );
            }
        }
        return list;
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
                                && Matcher.getInstance().same( receive.getName(), other.getName() );
                    }
                    if ( !connected ) unconnectedNeeds.add( receive );
                }
            }
        }
        return unconnectedNeeds;
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
                                && Matcher.getInstance().same( send.getName(), other.getName() );
                    }
                    if ( !used ) unusedCapabilities.add( send );
                }
            }
        }
        return unusedCapabilities;
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
        return Matcher.getInstance().same( send.getName(), need.getName() )
                &&
                ( need.getEois().isEmpty()
                        || hasCommonEOIs( send, need ) );
    }

    private boolean doFindIfSegmentStarted( Segment segment, Set<ModelObject> visited ) {
        if ( getPlan().isIncident( segment.getEvent() ) ) return true;
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
    public String getTitle( Actor actor ) {
        for ( Job job : findAllJobs( null, actor ) ) {
            String title = job.getTitle().trim();
            if ( !title.isEmpty() )
                return title;
        }

        return "";
    }

    @Override
    public List<Event> findPlannedEvents() {
        Plan plan = getPlan();
        List<Event> plannedEvents = new ArrayList<Event>();
        for ( Event event : listReferencedEntities( Event.class ) ) {
            if ( !plan.isIncident( event ) ) plannedEvents.add( event );
        }
        return plannedEvents;
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
    @SuppressWarnings( "unchecked" )
    public List<Employment> findAllEmploymentsForRole( final Role role ) {
        final Place locale = getPlan().getLocale();
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
    public List<Flow> findAllSharingFlows( Segment segment ) {
        Set<Flow> flows = new HashSet<Flow>();
        List<Segment> segments = new ArrayList<Segment>();
        if ( segment == null ) {
            segments.addAll( getPlan().getSegments() );
        } else {
            segments.add( segment );
        }
        for ( Segment seg : segments ) {
            flows.addAll( seg.getAllSharingFlows() );
        }
        return new ArrayList<Flow>( flows );
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
    @SuppressWarnings( "unchecked" )
    public <T extends ModelObject> List<T> findAllModelObjects( Class<T> clazz ) {
        List<T> domain;
        if ( Part.class.isAssignableFrom( clazz ) ) {
            domain = (List<T>) findAllParts();
        } else if ( Flow.class.isAssignableFrom( clazz ) ) {
            domain = (List<T>) findAllFlows();
        } else {
            domain = list( clazz );
        }
        return domain;
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
    public List<? extends ModelObject> findAllModelObjectsIn( Place place ) {
        List<ModelObject> inPlace = new ArrayList<ModelObject>();
        Place locale = getPlan().getLocale();
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
                if ( part.getLocation() != null && part.getLocation().narrowsOrEquals( place, locale ) )
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
    public List<Hierarchical> findRoots( Hierarchical hierarchical ) {
        Set<Hierarchical> roots = findRoots( hierarchical, new HashSet<Hierarchical>() );
        return new ArrayList<Hierarchical>( roots );
    }

    private Set<Hierarchical> findRoots(
            Hierarchical hierarchical,
            Set<Hierarchical> visited ) {
        Set<Hierarchical> roots = new HashSet<Hierarchical>();
        if ( !visited.contains( hierarchical ) ) {
            visited.add( hierarchical );
            List<? extends Hierarchical> superiors = hierarchical.getSuperiors();
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
    public List<Hierarchical> findAllDescendants( Hierarchical hierarchical ) {
        Set<Hierarchical> descendants = new HashSet<Hierarchical>();
        for ( ModelObject mo : findAllModelObjects() ) {
            if ( mo instanceof Hierarchical && hasAncestor(
                    (Hierarchical) mo,
                    hierarchical,
                    new HashSet<Hierarchical>() ) ) {
                descendants.add( (Hierarchical) mo );
            }
        }
        return new ArrayList<Hierarchical>( descendants );
    }

    private static boolean hasAncestor(
            Hierarchical hierarchical, final Hierarchical other, final Set<Hierarchical> visited ) {

        if ( visited.contains( hierarchical ) )
            return false;

        visited.add( hierarchical );
        List<? extends Hierarchical> superiors = hierarchical.getSuperiors();
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
    public Boolean isSemanticMatch( String text, String otherText, Proximity proximity ) {
        return Matcher.getInstance().same( text, otherText )
                || semanticMatcher.matches( text.trim(), otherText.trim(), proximity );
    }

    @Override
    public Boolean likelyRelated( String text, String otherText ) {
        return Matcher.getInstance().matches( text, otherText )
                || isSemanticMatch( StringUtils.uncapitalize( text ),
                StringUtils.uncapitalize( otherText ),
                Proximity.HIGH );
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

    private List<Flow> findSharingFlowsMatchingNeed( Part part, Flow need ) {
        List<Flow> sharings = new ArrayList<Flow>();
        String info = need.getName();
        Iterator<Flow> incoming = part.receives();
        while ( incoming.hasNext() ) {
            Flow in = incoming.next();
            if ( in.isSharing()
                    && Matcher.getInstance().same( in.getName(), info )
                    && Flow.Restriction.matchedBy( need.getRestriction(), in.getRestriction() ) ) {
                sharings.add( in );
            }
        }
        return sharings;
    }

    @Override
    public Level computePartPriority( Part part ) {
        return getPartPriority( part, new ArrayList<Part>() );
    }

    @Override
    public Level computeSharingPriority( Flow flow ) {
        assert flow.isSharing();
        if ( flow.isEssential( false, this ) )
            return computePartPriority( (Part) flow.getTarget() );
        else
            return Level.Low;
    }

    @Override
    public Plan getPlan() {
        return User.plan();
    }

    private Level getPartPriority( Part part, List<Part> visited ) {
        visited.add( part );
        Level max = Level.Low;
        for ( Goal goal : part.getGoals() ) {
            if ( goal.getLevel().getOrdinal() > max.getOrdinal() )
                max = goal.getLevel();
        }
        if ( part.isTerminatesEventPhase() ) {
            for ( Goal goal : part.getSegment().getTerminatingRisks() ) {
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
    public List<Goal> findAllGoalsImpactedByFailure( Part part ) {
        return findAllGoalsImpactedByFailure( part, new ArrayList<Part>() );
    }

    private List<Goal> findAllGoalsImpactedByFailure( Part part, List<Part> visited ) {
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
    public List<String> findAllPlanners() {
        return (List<String>) CollectionUtils.collect(
                userService.getPlanners( getPlan().getUri() ),
                TransformerUtils.invokerTransformer( "getUsername" )
        );
    }

    @Override
    public String findUserFullName( String userName ) {
        if ( userService == null ) {
            System.out.println( "OOPS!" );
        }
        User user = userService.getUserNamed( userName );
        if ( user != null ) {
            return user.getFullName();
        } else {
            return null;
        }
    }

    @Override
    public String findUserEmail( String userName ) {
        User user = userService.getUserNamed( userName );
        if ( user != null ) {
            return user.getEmail();
        } else {
            return null;
        }
    }

    @Override
    public String findUserRole( String userName ) {
        User user = userService.getUserNamed( userName );
        if ( user != null ) {
            return user.getRole( user.getPlanUri() );
        } else {
            return null;
        }
    }

    @Override
    public String findUserNormalizedFullName( String userName ) {
        User user = userService.getUserNamed( userName );
        if ( user != null ) {
            return user.getNormalizedFullName();
        } else {
            return "?";
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
    public List<? extends ModelEntity> findAllNarrowingOrEqualTo( final ModelEntity entity ) {
        final Place locale = getPlan().getLocale();
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

    @Override
    public Boolean isInvolved( Organization organization ) {
        return !getAssignments().with( organization ).isEmpty();
    }

    @Override
    public Boolean isInvolvementExpected( Organization organization ) {
        return getPlan().getOrganizations().contains( organization );
    }

    @Override
    public List<Part> findAllPartsPlayedBy( Organization organization ) {
        return getAssignments().with( organization ).getParts();
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
        Place locale = getPlan().getLocale();
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
                                                part.getJurisdiction() ) ),
                                part );
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
                                                            part.getJurisdiction() ) ),
                                            part );
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
                                                    part.getJurisdiction() ) ),
                                    part );
                            if ( !isProhibited( assignment, parts ) )
                                result.add( assignment );
                        }
                    }
                }
            }
        }
        return new ArrayList<Assignment>( result );
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

    private boolean containsParentOf( Set<Assignment> assignments, final Organization org ) {
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


    @Override
    public List<Commitment> findAllCommitments( Flow flow ) {
        return findAllCommitments( flow, false );
    }

    @Override
    public List<Commitment> findAllCommitments( Flow flow, Boolean allowCommitmentsToSelf ) {
        return findAllCommitments( flow, allowCommitmentsToSelf, getAssignments( true ) );
    }


    @Override
    public List<Commitment> findAllCommitments( Flow flow, Boolean selfCommits, Assignments assignments ) {

        Set<Commitment> commitments = new HashSet<Commitment>();
        if ( flow.isSharing() && !flow.isProhibited() ) {
            List<Flow> allFlows = findAllFlows();
            Place locale = getPlan().getLocale();
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

    public List<Commitment> findAllCommitmentsOf(
            Specable specable,
            Assignments assignments,
            List<Flow> allFlows ) {
        Place locale = getPlan().getLocale();
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

    public List<Commitment> findAllCommitmentsTo(
            Specable specable,
            Assignments assignments,
            List<Flow> allFlows ) {
        Place locale = getPlan().getLocale();
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
        Place locale = getPlan().getLocale();
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

    private Boolean isImplicitlyProhibited( Flow sharing, List<Flow> allFlows ) {
        boolean prohibited = false;
        Iterator<Flow> flows = allFlows.iterator();
        while ( !prohibited && flows.hasNext() ) {
            Flow other = flows.next();
            prohibited = other.isProhibited() && other.overrides( sharing, getPlan().getLocale() );
        }
        return prohibited;
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
            if ( commitment.isBetweenOrganizations() ) {
                Agreement agreement = Agreement.from( commitment );
                encompassed.addAll( (List<Agreement>) CollectionUtils.select(
                        agreements,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return encompasses( Agreement.from( commitment ),
                                        (Agreement) object );
                            }
                        } )
                );
                agreements.add( agreement );
            }
        }
        return (List<Agreement>) CollectionUtils.subtract( agreements, encompassed );
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
                if ( commitment.isBetweenOrganizations() && covers( agreement, commitment ) )
                    results.add( commitment );
        }
        return new ArrayList<Commitment>( results );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Flow> findEssentialFlowsFrom( Part part, Boolean assumeFails ) {
        // Find all downstream important flows, avoiding circularities
        List<Flow> importantFlows = part.findImportantFlowsFrom( new HashSet<Part>() );
        // Iteratively trim "end flows" to non-useful parts
        final List<Flow> essentialFlows = keepEssentialFlows( importantFlows );
        // if not assume fails, retain only the flows without alternates.
        if ( assumeFails ) {
            return essentialFlows;
        } else {
            return (List<Flow>) CollectionUtils.select(
                    essentialFlows,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Flow flow = (Flow) object;
                            List<Flow> alternates = getAlternates( flow );
                            return alternates.isEmpty()
                                    || CollectionUtils.isSubCollection( alternates, essentialFlows );
                        }
                    }
            );
        }
    }

    @Override
    public List<Part> findFailureImpacts( SegmentObject segmentObject, Boolean assumeFails ) {
        if ( segmentObject instanceof Flow ) {
            Flow flow = (Flow) segmentObject;
            if ( ( (Flow) segmentObject ).isEssential( assumeFails, this ) ) {
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


    @SuppressWarnings( "unchecked" )
    private List<Flow> keepEssentialFlows( final List<Flow> importantFlows ) {
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
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> findAllActualEntitiesMatching(
            Class<T> entityClass,
            final T entityType ) {
        assert entityType.isType();
        final Place locale = getPlan().getLocale();
        return (List<T>) CollectionUtils.select(
                findAllModelObjects( entityClass ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        T entity = (T) object;
                        return entity.isActual() && entity.narrowsOrEquals( entityType, locale );
                    }
                }
        );
    }

    @Override
    /** {@inheritDoc} */
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
    /** {@inheritDoc} */
    public UserService getUserService() {
        return userService;
    }

    @Override
    /** {@inheritDoc} */
    public Participation findParticipation( final String username ) {
        return (Participation) CollectionUtils.find(
                getDao().list( Participation.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Participation) object ).getUsername().equals( username );
                    }
                }
        );
    }

    @Override
    /** {@inheritDoc} */
    public Boolean isCoveredByAgreement( final Commitment commitment ) {
        return CollectionUtils.exists(
                commitment.getCommitter().getOrganization().getAgreements(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return covers( ( (Agreement) object ),
                                commitment );
                    }
                }
        );
    }

    @Override
    /** {@inheritDoc} */
    public Boolean isAgreementRequired( Commitment commitment ) {
        return commitment.getCommitter().getOrganization().isAgreementsRequired()
                && commitment.isBetweenOrganizations();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    /** {@inheritDoc} */
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
                } );
    }

    @Override
    public Boolean covers( Agreement agreement, Commitment commitment ) {
        Flow sharing = commitment.getSharing();
        Assignment beneficiary = commitment.getBeneficiary();
        if ( beneficiary.getOrganization().narrowsOrEquals(
                agreement.getBeneficiary(), getPlan().getLocale() )
                && Matcher.getInstance().same( agreement.getInformation(), sharing.getName() ) ) {
            List<ElementOfInformation> eois = agreement.getEois();
            if ( eois.isEmpty() || subsetOf( sharing.getEois(), eois ) ) {
                String usage = agreement.getUsage();
                if ( usage.isEmpty()
                        || isSemanticMatch( usage, beneficiary.getPart().getTask(), Proximity.HIGH ) )
                    return true;
            }
        }
        return false;
    }

    @Override
    public Boolean encompasses( Agreement agreement, Agreement other ) {
        if ( other.getBeneficiary().narrowsOrEquals( agreement.getBeneficiary(), getPlan().getLocale() )
                && Matcher.getInstance().same( agreement.getInformation(), other.getInformation() ) ) {
            String usage = agreement.getUsage();
            if ( usage.isEmpty() || isSemanticMatch( usage, other.getUsage(), Proximity.HIGH ) )
                return subsetOf( other.getEois(), agreement.getEois() );
        }

        return false;
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
        List<ElementOfInformation> eois = flow.getEois();
        final Matcher matcher = Matcher.getInstance();
        final List<ElementOfInformation> otherEois = otherFlow.getEois();
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
                                        return matcher.same( eoi, otherEoi );
                                    }
                                } );
                    }
                } );
    }

    /**
     * Whether none in a list eois is without a strong match with some in another list.
     *
     * @param eois     a list of elements of information
     * @param superset a list of elements of information
     * @return a boolean
     */
    @Override
    public Boolean subsetOf(
            List<ElementOfInformation> eois, final List<ElementOfInformation> superset ) {
        final Matcher matcher = Matcher.getInstance();
        return !CollectionUtils.exists(
                eois,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final String eoiContent = ( (ElementOfInformation) object ).getContent();
                        return !CollectionUtils.exists(
                                superset,
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        String otherEoiContent = ( (ElementOfInformation) object ).getContent();
                                        return matcher.same( eoiContent, otherEoiContent );
                                    }
                                }
                        );
                    }
                }
        );
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
                        && Matcher.getInstance().same( flow.getName(), alternate.getName() )
                        && subsetOf( flow.getEois(), alternate.getEois() ) )
                    answer.add( alternate );
            }
        }

        return answer;
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
        List<? extends Hierarchical> toOrgSuperiors = toOrg.getSuperiors();
        if ( toOrgSuperiors.contains( fromOrg ) )
            return Organization.FamilyRelationship.Ancestor;
        List<? extends Hierarchical> fromOrgSuperiors = fromOrg.getSuperiors();
        if ( fromOrgSuperiors.contains( toOrg ) )
            return Organization.FamilyRelationship.Descendant;
        if ( !CollectionUtils.intersection( fromOrgSuperiors, toOrgSuperiors ).isEmpty() )
            return Organization.FamilyRelationship.Cousin;
        return Organization.FamilyRelationship.None;
    }

    @Override
    public boolean cleanup( Class<? extends ModelObject> clazz, String name ) {
        ModelObject mo = getDao().find( clazz, name.trim() );
        if ( mo == null || !mo.isEntity() || mo.isUnknown()
                || mo.isImmutable() || !mo.isUndefined()
                || isReferenced( mo ) )
            return false;

        LOG.info( "Removing unused " + mo.getClass().getSimpleName() + ' ' + mo );
        remove( mo );
        return true;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<ElementOfInformation> findCommonEOIs( Flow flow, Flow otherFlow ) {
        List<ElementOfInformation> commonEOIs = new ArrayList<ElementOfInformation>();
        List<ElementOfInformation> shorter;
        List<ElementOfInformation> longer;
        final Matcher matcher = Matcher.getInstance();
        if ( flow.getEois().size() <= otherFlow.getEois().size() ) {
            shorter = flow.getEois();
            longer = otherFlow.getEois();
        } else {
            longer = flow.getEois();
            shorter = otherFlow.getEois();
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
                                return matcher.same(
                                        ( (ElementOfInformation) object ).getContent(),
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
                    disseminations,
                    new Predicate() {
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

    private ElementOfInformation disseminatingEoi( final Flow flow, final Subject subject ) {
        final Matcher matcher = Matcher.getInstance();
        return (ElementOfInformation) CollectionUtils.find(
                flow.getEois(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return matcher.same( flow.getName(), subject.getInfo() )
                                && matcher.same(
                                subject.getContent(),
                                ( (ElementOfInformation) object ).getContent() );
                    }
                }
        );
    }

    private List<Dissemination> findDisseminationsInFlow(
            Flow flow,
            Subject subject,
            boolean showTargets,
            Part startPart,
            Subject startSubject ) {
        List<Dissemination> disseminations = new ArrayList<Dissemination>();
        Matcher matcher = Matcher.getInstance();
        for ( ElementOfInformation eoi : flow.getEois() ) {
            Transformation xform = eoi.getTransformation();
            if ( xform.isNone() || subject.isRoot() ) {
                if ( matcher.same( flow.getName(), subject.getInfo() )
                        && matcher.same( eoi.getContent(), subject.getContent() ) ) {
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
                                new Subject( flow.getName(), eoi.getContent(), eoi.isTimeSensitive() ),
                                startPart,
                                startSubject,
                                showTargets ) );
                    }
                } else {
                    if ( matcher.same( flow.getName(), subject.getInfo() )
                            && matcher.same( eoi.getContent(), subject.getContent() ) ) {
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

    @Override
    /** @{inheritDoc} */
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
    /** @{inheritDoc} */
    public Assignments getAssignments( Boolean includeUnknowns, Boolean includeProhibited ) {
        Place locale = getPlan().getLocale();
        Assignments result = new Assignments( locale );
        Set<Assignment> assignments = new HashSet<Assignment>();
        for ( Segment segment : list( Segment.class ) )
            for ( Iterator<Part> pi = segment.parts(); pi.hasNext(); )
                assignments.addAll( findAllAssignments( pi.next(), includeUnknowns, includeProhibited ) );

        result.add( assignments );
        return result;
    }

    @Override
    /** @{inheritDoc} */
    public Assignments getAssignments( Boolean includeUnknowns ) {
        return getAssignments( includeUnknowns, false );
    }

    @Override
    /** @{inheritDoc} */
    public Assignments getAssignments() {
        return getAssignments( true );
    }


    @Override
    /** @{inheritDoc} */
    public List<Tag> findTagDomain() {
        Set<Tag> domain = new HashSet<Tag>();
        for ( ModelObject mo : findAllModelObjects() ) {
            List<Tag> tags = mo.getTags();
            for ( Tag tag : tags ) {
                if ( tag.isInfoStandard() ) {
                    domain.add( tag );
                } else {
                    for ( String s : tag.getAllComponents() ) {
                        domain.add( new Tag( s ) );
                    }
                }
            }
        }
        return new ArrayList<Tag>( domain );
    }

    @Override
    /** @{inheritDoc} */
    public List<Part> findAllOverridingParts( Part part, List<Part> parts ) {
        List<Part> overridingParts = new ArrayList<Part>();
        Place locale = getPlan().getLocale();
        for ( Part p : parts ) {
            if ( p.overrides( part, locale ) ) {
                overridingParts.add( p );
            }
        }
        return overridingParts;
    }

    @Override
    /** @{inheritDoc} */
    public List<Part> findAllOverriddenParts( Part part, List<Part> parts ) {
        List<Part> overriddenParts = new ArrayList<Part>();
        Place locale = getPlan().getLocale();
        for ( Part p : parts ) {
            if ( part.overrides( p, locale ) ) {
                overriddenParts.add( p );
            }
        }
        return overriddenParts;
    }

    @Override
    /** @{inheritDoc} */
    public Boolean isOverridden( Part part ) {
        return !findAllOverridingParts( part,
                findSynonymousParts( part ) ).isEmpty();
    }

    @Override
    public Boolean isOverriding( Part part ) {
        return !findAllOverriddenParts( part, findSynonymousParts( part ) ).isEmpty();
    }

    @Override
    /** @{inheritDoc} */
    public Boolean isOverridden( Flow flow ) {
        return !findAllOverridingFlows( flow, findAllFlows() ).isEmpty();
    }

    @Override
    public Boolean isOverriding( Flow flow ) {
        return !findAllOverriddenFlows( flow, findAllFlows() ).isEmpty();
    }

    private List<Flow> findAllOverridingFlows( Flow sharing, List<Flow> allFlows ) {
        List<Flow> overridingFlows = new ArrayList<Flow>();
        if ( sharing.isSharing() ) {
            Place locale = getPlan().getLocale();
            for ( Flow s : allFlows ) {
                if ( s.isSharing() && s.overrides( sharing, locale ) ) {
                    overridingFlows.add( s );
                }
            }
        }
        return overridingFlows;
    }

    private List<Flow> findAllOverriddenFlows( Flow sharing, List<Flow> allFlows ) {
        List<Flow> overriddenFlows = new ArrayList<Flow>();
        if ( sharing.isSharing() ) {
            Place locale = getPlan().getLocale();
            for ( Flow s : allFlows ) {
                if ( s.isSharing() && sharing.overrides( s, locale ) ) {
                    overriddenFlows.add( s );
                }
            }
        }
        return overriddenFlows;
    }

    @Override
    public List<Flow> findOverriddenSharingSends( Part part ) {
        List<Flow> overriddenSends = new ArrayList<Flow>();
        final Place locale = getPlan().getLocale();
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
    public List<Flow> findOverriddenSharingReceives( Part part ) {
        List<Flow> overriddenReceives = new ArrayList<Flow>();
        final Place locale = getPlan().getLocale();
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
    public List<Part> findSynonymousParts( Part part ) {
        List<Part> matchingParts = new ArrayList<Part>();
        for ( Segment segment : getPlan().getSegments() ) {
            for ( Part other : segment.listParts() ) {
                if ( Matcher.getInstance().same( part.getTask(), other.getTask() ) ) {
                    matchingParts.add( other );
                }
            }
        }
        return matchingParts;
    }

    public boolean allowsCommitment(
            Assignment committer, Assignment beneficiary, Place locale, Flow flow ) {

        Restriction restriction = flow.getRestriction();
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
                            || ( !committerOrg.narrowsOrEquals( beneficiaryOrg, locale )
                            && !beneficiaryOrg.narrowsOrEquals( committerOrg, locale ) );

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
                            || ( !committerLocation.narrowsOrEquals( beneficiaryLocation, locale )
                            && !beneficiaryLocation.narrowsOrEquals( committerLocation, locale ) );

                case Supervisor:
                    return ModelObject.isNullOrUnknown( committer.getActor() )
                            || ModelObject.isNullOrUnknown( beneficiary.getActor() )
                            || hasSupervisor( committer.getActor(), beneficiary.getActor(), committerOrg );
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

    private boolean hasSupervisor( final Actor actor, final Actor supervisor, Organization org ) {
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

    @Override
    public List<Part> findAllInitiators( EventTiming eventTiming ) {
        List<Part> initiators = new ArrayList<Part>();
        Event event = eventTiming.getEvent();
        boolean concurrent = eventTiming.getTiming() == Phase.Timing.Concurrent;
        Place locale = getPlan().getLocale();
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
    public List<Flow> findAllCapabilitiesNamed( String name ) {
        List<Flow> capabilities = new ArrayList<Flow>();
        Matcher matcher = Matcher.getInstance();
        for ( Flow flow : findAllFlows() ) {
            if ( flow.isCapability() && matcher.same( flow.getName(), name ) ) {
                capabilities.add( flow );
            }
        }
        return capabilities;
    }

    @Override
    public List<User> findUsersParticipatingAs( Actor actor ) {
        List<User> users = new ArrayList<User>();
        for ( String userName : getUserService().getUsernames( getPlan().getUri() ) ) {
            Participation participation = findParticipation( userName );
            if ( participation != null
                    && participation.getActor() != null
                    && participation.getActor().equals( actor ) ) {
                users.add( getUserService().getUserNamed( userName ) );
            }
        }
        return users;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Participation> findAllParticipationsFor( final Actor actor ) {
        return (List<Participation>) CollectionUtils.select(
                list( Participation.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Actor assigned = ( (Participation) object ).getActor();
                        return assigned != null && assigned.equals( actor );
                    }
                }
        );
    }
}

