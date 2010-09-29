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
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
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
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.nlp.Matcher;
import com.mindalliance.channels.nlp.Proximity;
import com.mindalliance.channels.nlp.SemanticMatcher;
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
    public static final Logger LOG = LoggerFactory.getLogger( DefaultQueryService.class );

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

    public DefaultQueryService( PlanManager planManager, AttachmentManager attachmentManager ) {
        this.planManager = planManager;
        this.attachmentManager = attachmentManager;
    }

    /**
     * Required for CGLIB proxies...
     */
    DefaultQueryService() {
    }

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
    public PlanDao getDao() {
        return planManager.getDao( User.plan() );
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    /**
     * {@inheritDoc}
     */
    public void onDestroy() {
        // Do nothing
    }

    /**
     * Make sure plans are valid initialized with some proper http://bit.ly/24Reg.
     */
    public void afterPropertiesSet() {
        planManager.assignPlans();
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        try {
            return getDao().find( clazz, id );
        } catch ( NotFoundException e ) {
            return findUnknown( clazz, id );
        }
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

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> List<T> list( Class<T> clazz ) {
        return getDao().list( clazz );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> listTypeEntities( Class<T> clazz ) {
        return (List<T>) CollectionUtils.select(
                list( clazz ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (T) object ).isType();
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> listActualEntities( Class<T> clazz ) {
        return (List<T>) CollectionUtils.select(
                list( clazz ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (T) object ).isActual();
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelEntity> List<T> listReferencedEntities( Class<T> clazz ) {
        return (List<T>) CollectionUtils.select( list( clazz ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        ModelEntity entity = (ModelEntity) object;
                        return entity.isImmutable() && !entity.isUnknown()
                                || isReferenced( entity );
                    }
                } );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelEntity> List<T> listEntitiesNarrowingOrEqualTo( final T entity ) {
        return (List<T>) CollectionUtils.select(
                list( entity.getClass() ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (ModelEntity) object ).narrowsOrEquals( entity, User.current().getPlan() );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<ModelEntity> iterateEntities() {
        List<Iterator> entityIterators = new ArrayList<Iterator>();
        entityIterators.add( listReferencedEntities( TransmissionMedium.class ).iterator() );
        entityIterators.add( listReferencedEntities( Actor.class ).iterator() );
        entityIterators.add( listReferencedEntities( Role.class ).iterator() );
        entityIterators.add( listReferencedEntities( Place.class ).iterator() );
        entityIterators.add( listReferencedEntities( Organization.class ).iterator() );
        entityIterators.add( listReferencedEntities( Event.class ).iterator() );
        entityIterators.add( listReferencedEntities( Phase.class ).iterator() );
        entityIterators.add( listReferencedEntities( Participation.class ).iterator() );
        return (Iterator<ModelEntity>) IteratorUtils.chainedIterator( entityIterators );
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelEntity> T safeFindOrCreate( Class<T> clazz, String name ) {
        return findOrCreate( clazz, name, null );
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelEntity> T safeFindOrCreate( Class<T> clazz, String name, Long id ) {
        if ( name != null && !name.trim().isEmpty() ) {
            String root = name.trim();
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


    /**
     * {@inheritDoc}
     */
    public <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name ) {
        return findOrCreate( clazz, name, null );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public boolean entityExists( Class<? extends ModelEntity> clazz, String name, ModelEntity.Kind kind ) {
        ModelEntity entity = ModelEntity.getUniversalType( name, clazz );
        if ( entity == null ) entity = getDao().find( clazz, name );
        return entity != null && entity.getKind().equals( kind );
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelEntity> T safeFindOrCreateType( Class<T> clazz, String name ) {
        return safeFindOrCreate( clazz, name, null );
    }

    /**
     * {@inheritDoc}
     */
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
                } catch ( InvalidEntityKindException e ) {
                    LOG.warn( "Entity name conflict creating type " + candidateName );
                    candidateName = name.trim() + " type";
                    if ( i > 0 ) candidateName = candidateName + " (" + i + ")";
                    i++;
                }
            }
        }
        return entityType;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name ) {
        return findOrCreateType( clazz, name, null );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public void add( ModelObject object ) {
        getDao().add( object );
    }

    /**
     * {@inheritDoc}
     */
    public void add( ModelObject object, Long id ) {
        getDao().add( object, id );
    }

    /**
     * {@inheritDoc}
     */
    public void update( ModelObject object ) {
        getDao().update( object );
    }

    /**
     * {@inheritDoc}
     */
    public void remove( ModelObject object ) {
        object.beforeRemove( this );
        getDao().remove( object );
    }

    /**
     * {@inheritDoc}
     */
    public Segment createSegment() {
        return createSegment( null );
    }

    /**
     * {@inheritDoc}
     */
    public Segment createSegment( Long id ) {
        return createSegment( id, null );
    }

    /**
     * {@inheritDoc}
     */
    public Segment createSegment( Long id, Long defaultPartId ) {
        return getDao().createSegment( id, defaultPartId );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInitiated( Segment segment ) {
        return !findInitiators( segment ).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    public Connector createConnector( Segment segment ) {
        return createConnector( segment, null );
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector( Segment segment, Long id ) {
        Connector result = getDao().createConnector( segment, id );
        segment.addNode( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Part createPart( Segment segment ) {
        return createPart( segment, null );
    }

    /**
     * {@inheritDoc}
     */
    public Part createPart( Segment segment, Long id ) {
        return getDao().createPart( segment, id );
    }

    /**
     * {@inheritDoc}
     */
    public Flow connect( Node source, Node target, String name ) {
        return connect( source, target, name, null );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public Flow replicate( Flow flow, boolean isSend ) {
        Flow result = isSend ? connect( flow.getSource(),
                createConnector( flow.getSource().getSegment() ),
                flow.getName() )
                : connect( createConnector( flow.getTarget().getSegment() ),
                flow.getTarget(),
                flow.getName() );
        result.initFrom( flow );
        return result;
    }

    // QUERIES (no change to model)

    /**
     * {@inheritDoc}
     */
    public Segment getDefaultSegment() {
        return toSortedList( list( Segment.class ) ).get( 0 );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public <T extends ModelObject> List<T> findAllReferencing( final ModelObject mo, Class<T> clazz ) {
        return (List<T>) CollectionUtils.select(
                findAllModelObjects( clazz ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (ModelObject) object ).references( mo );
                    }
                }
        );
    }

    /**
     * Whether the model object is referenced in another model object.
     *
     * @param mo a model object
     * @return a boolean
     */
    @SuppressWarnings( "unchecked" )
    public Boolean isReferenced( final ModelObject mo ) {
        if ( mo instanceof Participation ) {
            // Participations are not referenced per se but are not obsolete if they name a registered user.
            return ( (Participation) mo ).hasUser( this );
        } else {
            boolean hasReference = false;
            Iterator classes = ModelObject.referencingClasses().iterator();
            if ( User.plan().references( mo ) ) return true;
            while ( !hasReference && classes.hasNext() ) {
                List<? extends ModelObject> mos = findAllModelObjects( (Class<? extends ModelObject>) classes.next() );
                hasReference = CollectionUtils.exists(
                        mos,
                        new Predicate() {
                            public boolean evaluate( Object object ) {
                                return ( (ModelObject) object ).references( mo );
                            }
                        }
                );
            }
            return hasReference;
        }
    }

    public Boolean isReferenced( final Classification classification ) {
        boolean hasReference = CollectionUtils.exists(
                this.listActualEntities( Actor.class ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Actor) object ).getClearances().contains( classification );
                    }
                }
        );
        hasReference = hasReference || CollectionUtils.exists(
                findAllFlows(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Flow) object ).getClassifications().contains( classification );
                    }
                }
        );
        return hasReference;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( Specable specable ) {
        Plan plan = User.current().getPlan();
        List<ResourceSpec> list = new ArrayList<ResourceSpec>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( spec.narrowsOrEquals( specable, plan ) )
                list.add( spec );
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourcesBroadeningOrEqualTo( ResourceSpec resourceSpec ) {
        Plan plan = User.current().getPlan();
        List<ResourceSpec> list = new ArrayList<ResourceSpec>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( resourceSpec.narrowsOrEquals( spec, plan ) )
                list.add( spec );
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllContacts( Specable specable, boolean isSelf ) {
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

    /**
     * {@inheritDoc}
     */
    public List<Play> findAllPlays( Specable specable ) {
        return findAllPlays( specable, false );
    }

    /**
     * {@inheritDoc}
     */
    public List<Play> findAllPlays( Specable resourceSpec, boolean specific ) {
        Set<Play> plays = new HashSet<Play>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( Play.hasPlay( flow ) ) {
                    if ( flow.getSource().isPart() ) {
                        Part part = (Part) flow.getSource();
                        if ( part.resourceSpec().matches( resourceSpec, specific, User.current().getPlan() ) ) {
                            // sends
                            Play play = new Play( part, flow, true );
                            plays.add( play );
                        }
                    }
                    if ( flow.getTarget().isPart() ) {
                        Part part = (Part) flow.getTarget();
                        if ( part.resourceSpec().matches( resourceSpec, specific, User.current().getPlan() ) ) {
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

    /**
     * {@inheritDoc}
     */
    public List<Issue> findAllUserIssues( ModelObject identifiable ) {
        return getDao().findAllUserIssues( identifiable );
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelEntity> List<T> listEntitiesTaskedInSegment(
            Class<T> entityClass,
            Segment segment,
            ModelEntity.Kind kind ) {
        List<T> entities = kind.equals( ModelEntity.Kind.Actual )
                ? listActualEntities( entityClass )
                : listTypeEntities( entityClass );
        Set<T> result = new HashSet<T>();
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
        return new ArrayList<T>( result );
    }

    public boolean isExecutedBy( Part part, final ModelEntity entity ) {
        if ( entity.isActual() ) {
            List<Assignment> assignments = findAllAssignments( part, false );
            return part.resourceSpec().hasEntity( entity )
                    ||
                    CollectionUtils.exists(
                            assignments,
                            new Predicate() {
                                public boolean evaluate( Object object ) {
                                    Assignment assignment = (Assignment) object;
                                    return assignment.hasEntity( entity );
                                }
                            }
                    );
        } else {
            ResourceSpec partSpec = part.resourceSpec();
            /*if ( entity instanceof Actor && entity.isActual() ) {
                List<Actor> allPlayers = findAllActualActors( partSpec );
                if ( allPlayers.isEmpty() )
                    return entity.isUnknown();
                else
                    return allPlayers.contains( (Actor) entity );
            } else {*/
            return partSpec.hasEntityOrBroader( entity, User.current().getPlan() );
            //           }
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<Flow> findAllRelatedFlows( ResourceSpec resourceSpec, boolean asSource ) {
        List<Flow> relatedFlows = new ArrayList<Flow>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                Node node = asSource ? flow.getSource() : flow.getTarget();
                if ( node.isPart()
                        && resourceSpec.narrowsOrEquals( ( (Part) node ).resourceSpec(), User.current().getPlan() ) )
                    relatedFlows.add( flow );
            }
        }
        return relatedFlows;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public List<Actor> findAllActualActors( ResourceSpec resourceSpec ) {
        Plan plan = User.current().getPlan();
        Set<Actor> actors = new HashSet<Actor>();
        // If the resource spec is anyone, then return no actor,
        // else it would return every actor known to the app
        if ( !resourceSpec.isAnyone() ) {
            Iterator<ResourceSpec> specs = findAllResourceSpecs().iterator();
            Iterator<ResourceSpec> actorSpecs = new FilterIterator( specs, new Predicate() {
                public boolean evaluate( Object object ) {
                    Actor actor = ( (Specable) object ).getActor();
                    return actor != null && actor.isActual();
                }
            } );
            while ( actorSpecs.hasNext() ) {
                ResourceSpec actorResourceSpec = actorSpecs.next();
                if ( actorResourceSpec.narrowsOrEquals( resourceSpec, plan ) ) {
                    Actor actor = actorResourceSpec.getActor();
                    if ( !actor.isUnknown() && !actor.isArchetype() ) actors.add( actor );
                }
            }
        }

        return new ArrayList<Actor>( actors );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public List<Organization> findAllActualOrganizations( ResourceSpec resourceSpec ) {
        Set<Organization> organizations = new HashSet<Organization>();
        // If the resource spec is anyone, then return no organization,
        // else it would return every organization known to the app
        if ( !resourceSpec.isAnyone() ) {
            Iterator<ResourceSpec> specs = findAllResourceSpecs().iterator();
            Iterator<ResourceSpec> orgSpecs = new FilterIterator( specs, new Predicate() {
                public boolean evaluate( Object object ) {
                    Organization organization = ( (Specable) object ).getOrganization();
                    return organization != null && organization.isActual();
                }
            } );
            while ( orgSpecs.hasNext() ) {
                ResourceSpec orgResourceSpec = orgSpecs.next();
                if ( orgResourceSpec.narrowsOrEquals( resourceSpec, User.current().getPlan() ) ) {
                    Organization organization = orgResourceSpec.getOrganization();
                    if ( !organization.isUnknown() ) organizations.add( organization );
                }
            }
        }

        return new ArrayList<Organization>( organizations );
    }

    /**
     * Find all relevant channels for a given resource spec.
     *
     * @param spec the spec
     * @return the channels
     */
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

    /**
     * {@inheritDoc}
     */
    public List<Flow> findAllFlowsContacting( ResourceSpec resourceSpec ) {
        List<Flow> flows = new ArrayList<Flow>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Flow> segmentFlows = segment.flows();
            while ( segmentFlows.hasNext() ) {
                Flow flow = segmentFlows.next();
                Part contactedPart = flow.getContactedPart();
                if ( contactedPart != null
                        && resourceSpec.narrowsOrEquals( contactedPart.resourceSpec(), User.current().getPlan() ) )
                    flows.add( flow );
            }
        }
        return flows;
    }

    /**
     * {@inheritDoc}
     */
    public List<Job> findUnconfirmedJobs( Organization organization ) {
        Set<Job> unconfirmedJobs = new HashSet<Job>();
        List<Job> confirmedJobs = organization.getJobs();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( organization.equals( part.getOrganizationOrUnknown() ) ) {
                    ResourceSpec resourceSpec = part.resourceSpec();
                    if ( resourceSpec.hasJob() ) {
                        Job job = Job.from( resourceSpec );
                        if ( job != null && !confirmedJobs.contains( job ) )
                            unconfirmedJobs.add( job );
                    }
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

    /**
     * {@inheritDoc}
     */
    public List<String> findAllJobTitles() {
        Set<String> titles = new HashSet<String>();
        for ( Organization organization : listActualEntities( Organization.class ) ) {
            for ( Job job : organization.getJobs() ) {
                titles.add( job.getTitle() );
            }
        }
        return toSortedList( titles );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public List<String> findAllEntityNames( Class<? extends ModelEntity> aClass ) {
        Set<String> allNames = new HashSet<String>();
        for ( ModelObject mo : listReferencedEntities( aClass ) ) {
            allNames.add( mo.getName() );
        }
        return toSortedList( allNames );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public List<Role> findAllRolesOf( Actor actor ) {
        Set<Role> roles = new HashSet<Role>();
        for ( Specable spec : findAllResourceSpecs() ) {
            if ( spec.getRole() != null ) {
                if ( spec.getActor() != null && actor.narrowsOrEquals( spec.getActor(), User.current().getPlan() )
                        || ( actor.isUnknown() && spec.getActor() == null ) )
                    roles.add( spec.getRole() );
            }
        }
        return new ArrayList<Role>( roles );
    }

    /**
     * {@inheritDoc}
     */
    public List<Actor> findAllActorsInOrganization( Organization organization ) {
        Set<Actor> actors = new HashSet<Actor>();
        for ( Employment employment : findAllEmploymentsIn( organization ) ) {
            actors.add( employment.getActor() );
        }
        return new ArrayList<Actor>( actors );
    }

    /**
     * {@inheritDoc}
     */
    public List<ModelObject> findAllSegmentObjectsInvolving( ModelEntity entity ) {
        if ( entity instanceof Event ) {
            return this.findAllModelObjectsDirectlyRelatedToEvent( (Event) entity );
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

    /**
     * {@inheritDoc}
     */
    public List<Actor> findActualActors( Organization organization, Role role ) {
        // Find all actors in role for organization
        Set<Actor> actors = new HashSet<Actor>();
        for ( Specable spec : findAllResourceSpecs() ) {
            if ( spec.getActor() != null ) {
                boolean sameOrg = Organization.UNKNOWN.equals( organization ) ?
                        spec.getOrganization() == null
                        : organization.equals( spec.getOrganization() );
                boolean sameRole = Role.UNKNOWN.equals( role ) ?
                        spec.getRole() == null
                        : role.equals( spec.getRole() );
                if ( sameOrg && sameRole ) {
                    Actor actor = spec.getActor();
                    if ( actor.isActual() )
                        actors.add( actor );
                }
            }
        }

        if ( actors.isEmpty() )
            actors.add( Actor.UNKNOWN );

        return toSortedList( actors );
    }

    /**
     * {@inheritDoc}
     */
    public List<Role> findRolesIn( Organization organization ) {
        Set<Role> roles = new HashSet<Role>();
        for ( Organization org : listEntitiesNarrowingOrEqualTo( organization ) ) {
            for ( Employment employment : findAllEmploymentsIn( org ) ) {
                roles.add( employment.getRole() );
            }
        }
        boolean hasUnknown = roles.contains( Role.UNKNOWN );
        roles.remove( Role.UNKNOWN );
        List<Role> list = toSortedList( roles );
        if ( hasUnknown )
            list.add( Role.UNKNOWN );
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<Organization> findOrganizations() {
        List<Organization> orgs = toSortedList( list( Organization.class ) );

        if ( !findRolesIn( Organization.UNKNOWN ).isEmpty() )
            orgs.add( Organization.UNKNOWN );

        return orgs;
    }

    /**
     * {@inheritDoc}
     */
    public List<Actor> findRelevantActors( Part part, Flow flow ) {
        Set<Actor> actors = new HashSet<Actor>();

        boolean partIsSource = flow.getSource().equals( part );
        Node node = partIsSource ? flow.getTarget() : flow.getSource();
        if ( node.isConnector() ) {
            Iterator<ExternalFlow> xFlows = ( (Connector) node ).externalFlows();
            while ( xFlows.hasNext() ) {
                ExternalFlow xFlow = xFlows.next();
                actors.addAll( findAllActualActors( xFlow.getPart().resourceSpec() ) );
            }
        } else {
            Part otherPart = (Part) node;
            if ( otherPart.getActor() == null )
                actors.addAll( findAllActualActors( otherPart.resourceSpec() ) );
        }

        return toSortedList( actors );
    }

/*
    */
/**
 * {@inheritDoc}
 */
/*
    public List<Actor> findActualActors( Organization organization, Role role, Segment segment ) {
        return findActualActors( organization, role, null, segment );
    }

    */
/**
 * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public List<Job> findAllConfirmedJobs( Specable specable ) {
        List<Job> jobs = new ArrayList<Job>();
        for ( Organization org : listActualEntities( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( job.resourceSpec( org ).narrowsOrEquals( specable, User.current().getPlan() ) ) {
                    jobs.add( job );
                }
            }
        }
        return jobs;
    }

    /**
     * {@inheritDoc
     */
    public List<String> findJobTitles( Actor actor ) {
        String actorName = actor.getName();
        List<String> titles = new ArrayList<String>();
        for ( Organization org : listActualEntities( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( job.getActorName().equals( actorName ) ) {
                    String title = job.getTitle();
                    if ( !title.isEmpty() ) titles.add( title );
                }
            }
        }
        return titles;
    }

    /**
     * {@inheritDoc
     */
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

    /**
     * {@inheritDoc}
     */
    public boolean findIfPartStarted( Part part ) {
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

    /**
     * {@inheritDoc}
     */
    public boolean findIfSegmentStarted( Segment segment ) {
        return doFindIfSegmentStarted( segment, new HashSet<ModelObject>() );

    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public List<Part> findAllParts( Segment segment, Specable specable, boolean exactMatch ) {
        Set<Part> list = new HashSet<Part>();
        Set<Segment> segments;
        if ( segment == null ) {
            segments = getCurrentPlan().getSegments();
        } else {
            segments = new HashSet<Segment>();
            segments.add( segment );
        }

        Plan plan = User.current().getPlan();
        for ( Segment seg : segments ) {
            for ( Iterator<Part> parts = seg.parts(); parts.hasNext(); ) {
                Part part = parts.next();
                if ( part.resourceSpec().matches( specable, exactMatch, plan ) ) {
                    list.add( part );
                }
            }
        }
        // visitParts( list, resourceSpec, segment, exactMatch );
        return toSortedList( list );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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
        if ( User.plan().isIncident( segment.getEvent() ) ) return true;
        if ( visited.contains( segment ) ) return false;
        visited.add( segment );
        boolean started = false;
        Iterator<Part> initiators = findInitiators( segment ).iterator();
        while ( !started && initiators.hasNext() ) {
            started = doFindIfPartStarted( initiators.next(), visited );
        }
        return started;
    }

    public String getTitle( Actor actor ) {
        for ( Job job : findAllJobs( null, actor ) ) {
            String title = job.getTitle().trim();
            if ( !title.isEmpty() )
                return title;
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    public List<Event> findPlannedEvents() {
        Plan plan = User.plan();
        List<Event> plannedEvents = new ArrayList<Event>();
        for ( Event event : listReferencedEntities( Event.class ) ) {
            if ( !plan.isIncident( event ) ) plannedEvents.add( event );
        }
        return plannedEvents;
    }

    /**
     * {@inheritDoc}
     */
    public List<Part> findAchievers( Segment segment, Goal goal ) {
        List<Part> achievers = new ArrayList<Part>();
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( ( goal.isRiskMitigation() && part.isTerminatesEventPhase() ) || part.getGoals().contains( goal ) ) {
                achievers.add( part );
            }
        }
        return achievers;
    }

    /**
     * {@inheritDoc}
     */
    public List<Segment> findSegments( Actor actor ) {
        List<Segment> result = new ArrayList<Segment>();

        for ( Segment s : list( Segment.class ) ) {
            List<Part> parts = findAllParts( s, actor, false );
            if ( !parts.isEmpty() )
                result.add( s );
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<Actor> findActualActors( Segment segment ) {
        Set<Actor> actors = new HashSet<Actor>();
        for ( Iterator<Part> pi = segment.parts(); pi.hasNext(); ) {
            Part p = pi.next();
            actors.addAll( findAllActualActors( p.resourceSpec() ) );
        }

        List<Actor> result = new ArrayList<Actor>( actors );
        Collections.sort( result );
        return result;
    }

    public List<Organization> findActualOrganizations( Segment segment ) {
        Set<Organization> organizations = new HashSet<Organization>();
        for ( Iterator<Part> pi = segment.parts(); pi.hasNext(); ) {
            Part p = pi.next();
            organizations.addAll( findAllActualOrganizations( p.resourceSpec() ) );
        }

        List<Organization> result = new ArrayList<Organization>( organizations );
        Collections.sort( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<String> findAllActorLastNames() {
        return (List<String>) CollectionUtils.collect(
                listActualEntities( Actor.class ),
                new Transformer() {
                    public Object transform( Object input ) {
                        return ( (Actor) input ).getLastName();
                    }
                } );
    }

    /**
     * {@inheritDoc}
     */
    public List<Employment> findAllEmploymentsWithKnownActors() {
        Set<Actor> employed = new HashSet<Actor>();
        List<Employment> employments = new ArrayList<Employment>();
        List<Organization> orgs = new ArrayList<Organization>( listActualEntities( Organization.class ) );
        orgs.add( Organization.UNKNOWN );
        for ( Organization org : orgs ) {
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Employment> findAllEmploymentsForRole( final Role role ) {
        return (List<Employment>) CollectionUtils.select(
                findAllEmploymentsWithKnownActors(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Role empRole = ( (Employment) object ).getRole();
                        return empRole != null && empRole.narrowsOrEquals( role, User.current().getPlan() );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Employment> findAllEmploymentsForActor( final Actor actor ) {
        return (List<Employment>) CollectionUtils.select(
                findAllEmploymentsWithKnownActors(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Actor empActor = ( (Employment) object ).getActor();
                        return empActor != null && empActor.equals( actor );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    public List<String> findAllFlowNames() {
        Set<String> names = new HashSet<String>();
        for ( Flow flow : findAllFlows() ) {
            names.add( flow.getName() );
        }
        return new ArrayList<String>( names );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> listEntitiesWithUnknown( Class<T> clazz ) {
        List<T> allEntities = new ArrayList<T>( list( clazz ) );
        ModelEntity unknown =
                clazz == Actor.class
                        ? Actor.UNKNOWN
                        : clazz == Event.class
                        ? Event.UNKNOWN
                        : clazz == Organization.class
                        ? Organization.UNKNOWN
                        : clazz == Place.class
                        ? Place.UNKNOWN
                        : Role.UNKNOWN;
        allEntities.add( (T) unknown );
        return allEntities;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Segment> findSegmentsRespondingTo( final Event event ) {
        return (List<Segment>) CollectionUtils.select(
                list( Segment.class ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Event repondedEvent = ( (Segment) object ).getEvent();
                        return repondedEvent != null && repondedEvent.equals( event );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public List<String> findAllGeonames() {
        Set<String> geonames = new HashSet<String>();
        for ( Place place : list( Place.class ) ) {
            String geoname = place.getGeoname();
            if ( geoname != null && !geoname.isEmpty() )
                geonames.add( geoname );
        }
        return new ArrayList<String>( geonames );
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends ModelObject> findAllModelObjectsIn( Place place ) {
        List<ModelObject> inPlace = new ArrayList<ModelObject>();
        for ( Organization org : list( Organization.class ) ) {
            if ( org.getLocation() != null && org.getLocation().narrowsOrEquals( place, User.current().getPlan() ) )
                inPlace.add( org );
        }
        for ( Event event : listReferencedEntities( Event.class ) ) {
            if ( event.getScope() != null && event.getScope().narrowsOrEquals( place, User.current().getPlan() ) )
                inPlace.add( event );
        }
        for ( Place p : list( Place.class ) ) {
            if ( !p.equals( place ) && p.narrowsOrEquals( place, User.current().getPlan() ) )
                inPlace.add( p );
        }
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.getLocation() != null && part.getLocation().narrowsOrEquals( place, User.current().getPlan() ) )
                    inPlace.add( part );
            }
        }
        return inPlace;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public <T extends ModelObject> List<T> findAllReferencesTo( Place place, Class<T> clazz ) {
        List<T> references = new ArrayList<T>();
        if ( clazz.isAssignableFrom( Organization.class ) ) {
            for ( Organization org : list( Organization.class ) ) {
                if ( org.getLocation() != null && org.getLocation().equals( place ) )
                    references.add( (T) org );
                for ( Job job : org.getJobs() ) {
                    if ( job.getJurisdiction() != null && job.getJurisdiction().equals( place ) )
                        references.add( (T) org );
                }
            }
        }
        if ( clazz.isAssignableFrom( Event.class ) ) {
            for ( Event event : listReferencedEntities( Event.class ) ) {
                if ( event.getScope() != null && event.getScope().equals( place ) )
                    references.add( (T) event );
            }
        }
        if ( clazz.isAssignableFrom( Place.class ) ) {
            for ( Place p : list( Place.class ) ) {
                if ( !p.equals( place ) && p.equals( place ) )
                    references.add( (T) p );
                if ( p.getWithin() != null && p.getWithin().equals( place ) )
                    references.add( (T) p );
                if ( p.getMustBeContainedIn().references( place ) )
                    references.add( (T) p );
                if ( p.getMustContain().references( place ) )
                    references.add( (T) p );
            }
        }
        if ( clazz.isAssignableFrom( Part.class ) ) {
            for ( Segment segment : list( Segment.class ) ) {
                Iterator<Part> parts = segment.parts();
                while ( parts.hasNext() ) {
                    Part part = parts.next();
                    if ( part.getLocation() != null && part.getLocation().equals( place ) )
                        references.add( (T) part );
                    if ( part.getJurisdiction() != null && part.getJurisdiction().equals( place ) )
                        references.add( (T) part );
                }
            }
        }
        return references;
    }

    /**
     * {@inheritDoc}
     */
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
            List<Hierarchical> superiors = hierarchical.getSuperiors();
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

    /**
     * {@inheritDoc}
     */
    public List<Hierarchical> findAllDescendants( Hierarchical hierarchical ) {
        Set<Hierarchical> descendants = new HashSet<Hierarchical>();
        for ( ModelObject mo : this.findAllModelObjects() ) {
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
        List<Hierarchical> superiors = hierarchical.getSuperiors();
        Object superior = CollectionUtils.find( superiors, new Predicate() {
            public boolean evaluate( Object object ) {
                return hasAncestor( (Hierarchical) object, other, visited );
            }
        } );

        return !superiors.isEmpty()
                && ( superiors.contains( other ) || superior != null );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSemanticMatch( String text, String otherText, Proximity proximity ) {
        return Matcher.getInstance().same( text, otherText )
                || semanticMatcher.matches( text.trim(), otherText.trim(), proximity );
    }

    /**
     * {@inheritDoc}
     */
    public boolean likelyRelated( String text, String otherText ) {
        return Matcher.getInstance().matches( text, otherText )
                || isSemanticMatch( StringUtils.uncapitalize( text ),
                StringUtils.uncapitalize( otherText ),
                Proximity.HIGH );
    }

    /**
     * {@inheritDoc}
     */
    public List<Flow> findAllSharingsAddressing( Flow need ) {
        List<Flow> commitments = new ArrayList<Flow>();
        assert need.getSource().isConnector();
        // Find all synonymous commitments to the part
        Part needyPart = (Part) need.getTarget();
        commitments.addAll( findMatchingCommitmentsTo( needyPart, need.getName() ) );
        // Find all synonymous commitments to applicable anonymous parts within the plan segment
        for ( Part part : findAnonymousPartsMatching( needyPart ) ) {
            commitments.addAll( findMatchingCommitmentsTo( part, need.getName() ) );
        }
        return commitments;
    }

    /**
     * {@inheritDoc}
     */
    public List<Part> findAnonymousPartsMatching( Part part ) {
        List<Part> anonymousParts = new ArrayList<Part>();
        Iterator<Part> parts = part.getSegment().parts();
        while ( parts.hasNext() ) {
            Part p = parts.next();
            if ( !part.equals( p )
                    && p.getTask().isEmpty()
                    && part.resourceSpec().narrowsOrEquals( p.resourceSpec(), User.current().getPlan() ) ) {
                anonymousParts.add( p );
            }
        }
        return anonymousParts;
    }

    private List<Flow> findMatchingCommitmentsTo( Part part, String flowName ) {
        List<Flow> commitments = new ArrayList<Flow>();
        Iterator<Flow> incoming = part.receives();
        while ( incoming.hasNext() ) {
            Flow in = incoming.next();
            if ( in.getSource().isPart() && Matcher.getInstance().matches( in.getName(),
                    flowName ) ) {
                commitments.add( in );
            }
        }
        return commitments;
    }

    /**
     * {@inheritDoc}
     */
    public Level computePartPriority( Part part ) {
        return getPartPriority( part, new ArrayList<Part>() );
    }

    /**
     * {@inheritDoc}
     */
    public Level computeSharingPriority( Flow flow ) {
        assert flow.isSharing();
        if ( flow.isEssential( false, this ) )
            return computePartPriority( (Part) flow.getTarget() );
        else
            return Level.Low;
    }

    /**
     * {@inheritDoc}
     */
    public Plan getCurrentPlan() {
        return User.plan();
    }

    private Level getPartPriority( Part part, List<Part> visited ) {
        visited.add( part );
        Level max = Level.Low;
        for ( Goal goal : part.getGoals() ) {
            if ( goal.getLevel().getOrdinal() > max.getOrdinal() )
                max = goal.getLevel();
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<String> findAllPlanners() {
        return (List<String>) CollectionUtils.collect(
                userService.getPlanners( User.plan().getUri() ),
                TransformerUtils.invokerTransformer( "getUsername" )
        );
    }

    /**
     * {@inheritDoc}
     */
    public String findUserFullName( String userName ) {
        User user = userService.getUserNamed( userName );
        if ( user != null ) {
            return user.getFullName();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String findUserEmail( String userName ) {
        User user = userService.getUserNamed( userName );
        if ( user != null ) {
            return user.getEmail();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String findUserRole( String userName ) {
        User user = userService.getUserNamed( userName );
        if ( user != null ) {
            return user.getRole( user.getPlanUri() );
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String findUserNormalizedFullName( String userName ) {
        User user = userService.getUserNamed( userName );
        if ( user != null ) {
            return user.getNormalizedFullName();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Segment> findAllSegmentsForPhase( final Phase phase ) {
        return (List<Segment>) CollectionUtils.select(
                list( Segment.class ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Segment) object ).getPhase().equals( phase );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<? extends ModelEntity> findAllEntitiesIn( Place place ) {
        return (List<ModelEntity>) CollectionUtils.select(
                findAllModelObjectsIn( place ),
                PredicateUtils.invokerPredicate( "isEntity" ) );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<? extends ModelEntity> findAllEntitiesIn( Phase phase ) {
        return (List<ModelEntity>) CollectionUtils.select(
                findAllModelObjectsIn( phase ),
                PredicateUtils.invokerPredicate( "isEntity" ) );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> findAllEntitiesReferencingType(
            final ModelEntity entityType,
            Class<T> entityClass ) {
        assert entityType.isType();
        return (List<T>) CollectionUtils.select(
                list( entityClass ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        if ( ( (ModelEntity) object ).getTags().contains( entityType ) ) {
                            return true;
                        } else if ( object instanceof Event ) {
                            if ( ModelObject.areIdentical(
                                    ( (Event) object ).getScope(),
                                    entityType ) )
                                return true;
                        } else if ( object instanceof Organization ) {
                            if ( ModelObject.areIdentical(
                                    ( (Organization) object ).getLocation(),
                                    entityType ) )
                                return true;
                            if ( ModelObject.areIdentical(
                                    ( (Organization) object ).getParent(),
                                    entityType ) )
                                return true;
                        } else if ( object instanceof Place ) {
                            if ( ( (Place) object ).getMustContain().references( entityType ) )
                                return true;
                            if ( ( (Place) object ).getMustBeContainedIn().references( entityType ) )
                                return true;
                        }
                        return false;
                    }
                }
        );
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Part> findAllPartsReferencingType( final ModelEntity entityType ) {
        assert entityType.isType();
        return (List<Part>) CollectionUtils.select(
                findAllParts(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Part part = (Part) object;
                        return ModelObject.areIdentical( part.getLocation(), entityType )
                                || ModelObject.areIdentical( part.getActor(), entityType )
                                || ModelObject.areIdentical( part.getRole(), entityType )
                                || ModelObject.areIdentical( part.getOrganization(), entityType )
                                || ModelObject.areIdentical( part.getJurisdiction(), entityType );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<? extends ModelEntity> findAllNarrowingOrEqualTo( final ModelEntity entity ) {
        return (List<? extends ModelEntity>) CollectionUtils.select(
                list( entity.getClass() ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (ModelEntity) object ).narrowsOrEquals( entity, User.current().getPlan() );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isInvolved( final Organization organization ) {
        return CollectionUtils.exists(
                findAllParts(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Organization partOrg = ( (Part) object ).getOrganization();
                        return partOrg != null
                                && ( partOrg.equals( organization )
                                || partOrg.ancestors().contains( organization ) );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isInvolvementExpected( Organization organization ) {
        return User.plan().getOrganizations().contains( organization );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Part> findAllPartsPlayedBy( final Organization organization ) {
        Set<Part> allParts = new HashSet<Part>();
        for (Assignment assignment : findAllAssignments( organization )) {
            allParts.add( assignment.getPart() );
        }
        return new ArrayList<Part>( allParts );
/*
        return (List<Part>) CollectionUtils.select(
                findAllParts(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Part part = (Part) obj;
                        Organization org = part.getOrganization();
                        return org != null
                                && ( org.equals( organization )
                                || org.ancestors().contains( organization ) );
                    }
                }
        );
*/
    }

    /**
     * {@inheritDoc}
     */
    public List<Assignment> findAllAssignments( final Part part, final Boolean includeUnknownActors ) {
        List<Employment> employments = findAllEmploymentsWithKnownActors();
        if ( includeUnknownActors ) {
            employments.addAll( findAllEmploymentsWithUnknownActors() );
        }
        List<Assignment> assignments = new ArrayList<Assignment>();
        for ( Employment employment : employments ) {
            if ( employment.playsPart( part, User.current().getPlan() ) )
                assignments.add( new Assignment( employment, part ) );
        }
        return assignments;
    }

    /**
     * {@inheritDoc}
     */
    public List<Assignment> findAllAssignments( Actor actor ) {
        Set<Assignment> assignments = new HashSet<Assignment>();
        List<Employment> employments = findAllEmploymentsForActor( actor );
        List<Part> parts = findAllParts();
        for ( Employment employment : employments ) {
            for ( Part part : parts ) {
                if ( employment.playsPart( part, User.current().getPlan() ) )
                    assignments.add( new Assignment( employment, part ) );
            }
        }
        return new ArrayList<Assignment>( assignments );
    }

    /**
     * {@inheritDoc}
     */
    public List<Assignment> findAllAssignments( Organization org ) {
        Set<Assignment> assignments = new HashSet<Assignment>();
        List<Employment> employments = this.findAllEmploymentsIn( org );
        List<Part> parts = findAllParts();
        for ( Employment employment : employments ) {
            for ( Part part : parts ) {
                if ( employment.playsPart( part, User.current().getPlan() ) )
                    assignments.add( new Assignment( employment, part ) );
            }
        }
        return new ArrayList<Assignment>( assignments );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Assignment> findAllAssignments( Actor actor, Segment segment ) {
        Set<Assignment> assignments = new HashSet<Assignment>();
        List<Employment> employments = findAllEmploymentsForActor( actor );
        List<Part> parts = (List<Part>) IteratorUtils.toList( segment.parts() );
        for ( Employment employment : employments ) {
            for ( Part part : parts ) {
                if ( employment.playsPart( part, User.current().getPlan() ) )
                    assignments.add( new Assignment( employment, part ) );
            }
        }
        return new ArrayList<Assignment>( assignments );
    }

    private List<Employment> findAllEmploymentsWithUnknownActors() {
        Set<Employment> employments = new HashSet<Employment>();
        for ( Part p : findAllParts() ) {
            if ( !p.resourceSpec().isAnyone() && p.getActorOrUnknown().isUnknown() ) {
                Employment employment = new Employment(
                        Actor.UNKNOWN,
                        p.getOrganizationOrUnknown(),
                        new Job( Actor.UNKNOWN, p.getRoleOrUnknown(), p.getJurisdiction() ) );
                employments.add( employment );
            }
        }
        return new ArrayList<Employment>( employments );
    }

    /**
     * {@inheritDoc}
     */
    public List<Commitment> findAllCommitments( Flow flow ) {
        Set<Commitment> commitments = new HashSet<Commitment>();
        if ( flow.isSharing() ) {
            List<Assignment> sources = findAllAssignments( (Part) flow.getSource(), false );
            List<Assignment> beneficiaries = findAllAssignments( (Part) flow.getTarget(), true );
            for ( Assignment source : sources ) {
                for ( Assignment beneficiary : beneficiaries ) {
                    Commitment commitment = new Commitment( source, beneficiary, flow );
                    if ( !source.getActor().equals( beneficiary.getActor() )
                            && !flow.isProhibited()
                           // && commitment.passesClearanceTest() 
                            ) {
                        commitments.add( commitment );
                    }
                }
            }
        }
        return new ArrayList<Commitment>( commitments );
    }

    /**
     * {@inheritDoc}
     */
    public List<Commitment> findAllCommitmentsOf( Actor actor ) {
        Set<Commitment> commitments = new HashSet<Commitment>();
        for ( Assignment assignment : findAllAssignments( actor ) ) {
            Iterator<Flow> flows = assignment.getPart().flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( flow.isSharing() && flow.getSource().equals( assignment.getPart() ) ) {
                    for ( Assignment beneficiary : findAllAssignments( (Part) flow.getTarget(), true ) ) {
                        Commitment commitment = new Commitment(
                                assignment,
                                beneficiary,
                                flow );
                        if ( commitment.passesClearanceTest() ) commitments.add( commitment );
                    }
                }
            }
        }
        return new ArrayList<Commitment>( commitments );
    }

    /**
     * {@inheritDoc}
     */
    public List<Commitment> findAllCommitmentsOf( Organization organization ) {
        Set<Commitment> commitments = new HashSet<Commitment>();
        List<Actor> actorsInOrganization = findAllActorsInOrganization( organization );
        for ( Actor actor : actorsInOrganization ) {
            for ( final Commitment commitment : findAllCommitmentsOf( actor ) ) {
                if ( commitment.getCommitter().getOrganization().equals( organization ) ) {
                    commitments.add( commitment );
                }
            }
        }
        return new ArrayList<Commitment>( commitments );
    }

    /**
     * {@inheritDoc}
     */
    public List<Commitment> findAllCommitmentsTo( Actor actor ) {
        Set<Commitment> commitments = new HashSet<Commitment>();
        for ( Flow flow : findAllRelatedFlows( new ResourceSpec( actor ), false ) ) {
            commitments.addAll( findAllCommitments( flow ) );
        }
        return new ArrayList<Commitment>( commitments );
    }

    /**
     * {@inheritDoc}
     */
    public List<Flow[]> findUntappedSatisfactions( final Part part ) {
        List<Flow[]> untapped = new ArrayList<Flow[]>();
        for ( Flow need : part.getNeeds() ) {
            for ( Connector connector : findAllSatificers( need ) ) {
                boolean connected = CollectionUtils.exists(
                        IteratorUtils.toList( connector.externalFlows() ),
                        new Predicate() {
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Agreement> findAllImpliedAgreementsOf( Organization organization ) {
        List<Agreement> agreements = new ArrayList<Agreement>();
        List<Agreement> encompassed = new ArrayList<Agreement>();
        for ( final Commitment commitment : findAllCommitmentsOf( organization ) ) {
            if ( commitment.isBetweenOrganizations() ) {
                Agreement agreement = Agreement.from( commitment );
                encompassed.addAll( (List<Agreement>) CollectionUtils.select(
                        agreements,
                        new Predicate() {
                            public boolean evaluate( Object object ) {
                                return DefaultQueryService.this.encompasses( Agreement.from( commitment ),
                                        (Agreement) object );
                            }
                        } )
                );
                agreements.add( agreement );
            }
        }
        return (List<Agreement>) CollectionUtils.subtract( agreements, encompassed );
    }

    /**
     * {@inheritDoc}
     */
    public List<Commitment> findAllCommitmentsCoveredBy(
            Agreement agreement,
            Organization organization ) {
        Set<Commitment> commitments = new HashSet<Commitment>();
        List<Actor> actorsInOrganization = findAllActorsInOrganization( organization );
        for ( Actor actor : actorsInOrganization ) {
            for ( Commitment commitment : findAllCommitmentsOf( actor ) ) {
                if ( commitment.isBetweenOrganizations() && this.covers( agreement,
                        commitment ) )
                    commitments.add( commitment );
            }
        }
        return new ArrayList<Commitment>( commitments );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Flow> findEssentialFlowsFrom( Part part, boolean assumeFails ) {
        // Find all downstream important flows, avoiding circularities
        final List<Flow> importantFlows = findImportantFlowsFrom( part, new HashSet<Part>() );
        // Iteratively trim "end flows" to non-useful parts
        final List<Flow> essentialFlows = keepEssentialFlows( importantFlows );
        // if not assume fails, retain only the flows without alternates.
        if ( assumeFails ) {
            return essentialFlows;
        } else {
            return (List<Flow>) CollectionUtils.select(
                    essentialFlows,
                    new Predicate() {
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

    /**
     * {@inheritDoc}
     */
    public List<Part> findFailureImpacts( SegmentObject segmentObject, boolean assumeFails ) {
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


    @SuppressWarnings( "unchecked" )
    // Find all important flows downstream of part, without circularities.
    private List<Flow> findImportantFlowsFrom( Part part, final Set<Part> visited ) {
        List<Flow> flows = (List<Flow>) CollectionUtils.select(
                part.getAllSharingSends(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Flow flow = (Flow) object;
                        return flow.isImportant() && !visited.contains( (Part) flow.getTarget() );
                    }
                }
        );
        if ( !flows.isEmpty() ) {
            List<Part> parts = (List<Part>) CollectionUtils.collect(
                    flows,
                    new Transformer() {
                        public Object transform( Object input ) {
                            return ( (Flow) input ).getTarget();
                        }
                    }
            );
            Set<Part> newVisited = new HashSet<Part>();
            newVisited.addAll( visited );
            newVisited.add( part );
            for ( Part nextPart : parts ) {
                flows.addAll( findImportantFlowsFrom( nextPart, newVisited ) );
            }
        }
        return flows;
    }

    @SuppressWarnings( "unchecked" )
    /**
     * {@inheritDoc}
     */
    public <T extends ModelEntity> List<T> findAllActualEntitiesMatching(
            Class<T> entityClass,
            final T entityType ) {
        assert entityType.isType();
        return (List<T>) CollectionUtils.select(
                findAllModelObjects( entityClass ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        T entity = (T) object;
                        return entity.isActual() && entity.narrowsOrEquals( entityType, User.current().getPlan() );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    public List<Part> findAllAssignedParts( Actor actor ) {
        List<Assignment> assignments = findAllAssignments( actor );
        Set<Part> parts = new HashSet<Part>();
        for ( Assignment assignment : assignments ) {
            parts.add( assignment.getPart() );
        }
        return new ArrayList<Part>( parts );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Part> findAllAssignedParts( final Segment segment, Actor actor ) {
        return (List<Part>) CollectionUtils.select(
                findAllAssignedParts( actor ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Part) object ).getSegment().equals( segment );
                    }
                } );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * {@inheritDoc}
     */
    public Participation findParticipation( final String username ) {
        return (Participation) CollectionUtils.find(
                getDao().list( Participation.class ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Participation) object ).getUsername().equals( username );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    public List<ModelEntity> findEntities(
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
                    public boolean evaluate( Object object ) {
                        ModelEntity entity = (ModelEntity) object;
                        return !entity.isUnknown()
                                && entity.getKind().equals( kind );
                    }
                } );
    }

    /**
     * {@inheritDoc}
     */
    public boolean covers( Agreement agreement, Commitment commitment ) {
        Flow sharing = commitment.getSharing();
        Assignment beneficiary = commitment.getBeneficiary();
        if ( beneficiary.getOrganization().narrowsOrEquals(
                agreement.getBeneficiary(), getDao().getPlan() )
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

    /**
     * {@inheritDoc}
     */
    public boolean encompasses( Agreement agreement, Agreement other ) {
        Plan plan = getDao().getPlan();
        if ( other.getBeneficiary().narrowsOrEquals( agreement.getBeneficiary(), plan )
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
    public boolean hasCommonEOIs( Flow flow, Flow otherFlow ) {
        List<ElementOfInformation> eois = flow.getEois();
        final List<ElementOfInformation> otherEois = otherFlow.getEois();
        return CollectionUtils.exists(
                eois,
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        final String eoi = ( (ElementOfInformation) object ).getContent();
                        return CollectionUtils.exists(
                                otherEois,
                                new Predicate() {
                                    public boolean evaluate( Object object ) {
                                        String otherEoi = ( (ElementOfInformation) object ).getContent();
                                        return isSemanticMatch( eoi, otherEoi, Proximity.HIGH );
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
    public boolean subsetOf(
            List<ElementOfInformation> eois, final List<ElementOfInformation> superset ) {
        return !CollectionUtils.exists(
                eois,
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        final String eoi = ( (ElementOfInformation) object ).getContent();
                        return !CollectionUtils.exists(
                                superset,
                                new Predicate() {
                                    public boolean evaluate( Object object ) {
                                        final String otherEoi = ( (ElementOfInformation) object ).getContent();
                                        return isSemanticMatch( eoi, otherEoi, Proximity.HIGH );
                                    }
                                }
                        );
                    }
                }
        );
    }

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
    public List<Flow> getAlternates( Flow flow ) {
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

    /**
     * {@inheritDoc}
     */
    public Organization.FamilyRelationship findFamilyRelationship( Organization fromOrg, Organization toOrg ) {
        if ( ModelObject.areIdentical ( fromOrg, toOrg ) )
            return Organization.FamilyRelationship.Identity;
        if ( fromOrg.getParent() == null || toOrg.getParent() == null )
            return Organization.FamilyRelationship.None;
        if ( ModelObject.areIdentical( fromOrg, toOrg.getParent() ) )
            return Organization.FamilyRelationship.Parent;
        if ( ModelObject.areIdentical( toOrg, fromOrg.getParent() ) )
            return Organization.FamilyRelationship.Child;
        if ( ModelObject.areIdentical( fromOrg.getParent(), toOrg.getParent() ) )
            return Organization.FamilyRelationship.Sibling;
        List<Hierarchical> toOrgSuperiors = toOrg.getSuperiors();
        if ( toOrgSuperiors.contains( fromOrg ) )
            return Organization.FamilyRelationship.Ancestor;
        List<Hierarchical> fromOrgSuperiors = fromOrg.getSuperiors();
        if ( fromOrgSuperiors.contains( toOrg ) )
            return Organization.FamilyRelationship.Descendant;
        if ( !CollectionUtils.intersection( fromOrgSuperiors, toOrgSuperiors ).isEmpty() )
            return Organization.FamilyRelationship.Cousin;
        return Organization.FamilyRelationship.None;
    }

    /**
     * {@inheritDoc}
     */
    public boolean cleanup( Class<? extends ModelObject> clazz, String name ) {
        ModelObject mo = getDao().find( clazz, name.trim() );
        if ( mo == null  || !mo.isEntity() || mo.isUnknown()
                 || mo.isImmutable() || !mo.isUndefined()
                 || isReferenced( mo ) )
            return false;

        LOG.info( "Removing unused " + mo.getClass().getSimpleName() + ' ' + mo );
        remove( mo );
        return true;
    }

}

