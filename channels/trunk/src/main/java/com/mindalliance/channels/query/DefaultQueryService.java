package com.mindalliance.channels.query;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.ImagingService;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.SemanticMatcher;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Agreement;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Connector;
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
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.model.UserIssue;
import com.mindalliance.channels.nlp.Proximity;
import com.mindalliance.channels.util.FileUserDetailsService;
import com.mindalliance.channels.util.Matcher;
import com.mindalliance.channels.util.Play;
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
     * Imaging service.
     */
    private ImagingService imagingService;
    /**
     * File user details service.
     */
    private FileUserDetailsService userDetailsService;

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

    public ImagingService getImagingService() {
        return imagingService;
    }

    public void setImagingService( ImagingService imagingService ) {
        this.imagingService = imagingService;
    }

    public void setUserDetailsService( FileUserDetailsService userDetailsService ) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Find the dao for the selected plan of the current user.
     *
     * @return the dao
     */
    public Dao getDao() {
        try {
            return planManager.getDao( planManager.getCurrentPlan() );
        } catch ( NotFoundException e ) {
            LOG.error( "No plan found", e );
            throw new RuntimeException( e );
        }
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        getDao().flush();
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
        planManager.validate( this );
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
    private <T extends ModelObject> T findUnknown( Class<T> clazz, long id ) throws NotFoundException {
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
                    public boolean evaluate( Object obj ) {
                        return ( (T) obj ).isType();
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
                    public boolean evaluate( Object obj ) {
                        return ( (T) obj ).isActual();
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
                    public boolean evaluate( Object obj ) {
                        ModelEntity entity = (ModelEntity) obj;
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
                    public boolean evaluate( Object obj ) {
                        return ( (ModelEntity) obj ).narrowsOrEquals( entity );
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
        T entity = null;
        if ( name != null && !name.trim().isEmpty() ) {
            String candidateName = name.trim();
            boolean success = false;
            int i = 1;
            while ( !success ) {
                try {
                    entity = findOrCreate( clazz, candidateName, id );
                    success = true;
                } catch ( InvalidEntityKindException e ) {
                    LOG.warn( "Entity name conflict creating actual " + candidateName );
                    candidateName = candidateName + " (" + i + ")";
                    i++;
                }
            }
        }
        return entity;
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
        // If entity can only be a type, find or create a type.
        if ( !ModelEntity.canBeActualOrType( clazz ) ) {
            if ( ModelEntity.defaultKindFor( clazz ).equals( ModelEntity.Kind.Type ) ) {
                return findOrCreateType( clazz, name, id );
            }
        }
        if ( ModelEntity.getUniversalType( name, clazz ) != null )
            throw new InvalidEntityKindException( clazz.getSimpleName() + " " + name + " is a type" );
        T actualEntity = getDao().findOrCreate( clazz, name, id );
        if ( actualEntity.isType() )
            throw new InvalidEntityKindException( clazz.getSimpleName() + " " + name + " is a type" );
        actualEntity.setActual();
        return actualEntity;
    }

    /**
     * {@inheritDoc}
     */
    public boolean entityExists( Class<ModelEntity> clazz, String name, ModelEntity.Kind kind ) {
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
                throw new InvalidEntityKindException( clazz.getSimpleName() + " " + name + " is actual" );
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
        Segment newSegment = new Segment();
        if ( id == null )
            getDao().add( newSegment );
        else
            getDao().add( newSegment, id );
        newSegment.setName( Segment.DEFAULT_NAME );
        newSegment.setDescription( Segment.DEFAULT_DESCRIPTION );
        // Make sure a plan segment responds to an event.
        newSegment.setEvent( planManager.getCurrentPlan().getDefaultEvent() );
        newSegment.setPhase( planManager.getCurrentPlan().getDefaultPhase( this ) );
        newSegment.setQueryService( this );
        createPart( newSegment, defaultPartId );
        return newSegment;
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
        Part result = getDao().createPart( segment, id );
        segment.addNode( result );
        return result;
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

        if ( isInternal( source, target ) ) {
            result = getDao().createInternalFlow( source, target, name, id );
            source.addSend( result );
            target.addReceive( result );

        } else if ( isExternal( source, target ) ) {
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

    private static boolean isInternal( Node source, Node target ) {
        Segment segment = source.getSegment();
        return segment != null && segment.equals( target.getSegment() );
    }

    private static boolean isExternal( Node source, Node target ) {
        Segment segment = source.getSegment();
        return segment != null
                && !segment.equals( target.getSegment() )
                && ( target.isConnector() || source.isConnector() );
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public <T extends ModelObject> List<T> findAllReferencing( final ModelObject mo, Class<T> clazz ) {
        return (List<T>) CollectionUtils.select(
                findAllModelObjects( clazz ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (ModelObject) obj ).references( mo );
                    }
                }
        );
    }

    /**
     * Whether a model object is referenced.
     *
     * @param mo a model object
     * @return a boolean
     */
    @SuppressWarnings( "unchecked" )
    public Boolean isReferenced( final ModelObject mo ) {
        boolean hasReference = false;
        Iterator classes = ModelObject.referencingClasses().iterator();
        if ( planManager.getCurrentPlan().references( mo ) ) return true;
        while ( !hasReference && classes.hasNext() ) {
            List<? extends ModelObject> mos = findAllModelObjects( (Class<? extends ModelObject>) classes.next() );
            hasReference = CollectionUtils.exists(
                    mos,
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return ( (ModelObject) obj ).references( mo );
                        }
                    }
            );
        }
        return hasReference;
    }

    public Boolean isReferenced( final Classification classification ) {
        boolean hasReference = CollectionUtils.exists(
                this.listActualEntities( Actor.class ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Actor) obj ).getClearances().contains( classification );
                    }
                }
        );
        hasReference = hasReference || CollectionUtils.exists(
                findAllFlows(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Flow) obj ).getClassifications().contains( classification );
                    }
                }
        );
        return hasReference;
    }


    /**
     * {@inheritDoc}
     */
    public int getReferenceCount( Event event ) {
        int count = 0;
        for ( Event incident : planManager.getCurrentPlan().getIncidents() ) {
            if ( incident.equals( event ) ) count++;
        }
        // look in plan segments
        for ( Segment segment : list( Segment.class ) ) {
            if ( segment.getEvent().equals( event ) ) count++;
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.initiatesEvent() && part.getInitiatedEvent().equals( event ) ) count++;
            }
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    public Segment findSegment( String name ) throws NotFoundException {
        for ( Segment s : getDao().list( Segment.class ) ) {
            if ( name.equals( s.getName() ) )
                return s;
        }

        throw new NotFoundException();
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourceSpecs() {
        Set<ResourceSpec> result = new HashSet<ResourceSpec>();
        // Specs from entities
        for ( Actor actor : list( Actor.class ) ) {
            result.add( ResourceSpec.with( actor ) );
        }
        for ( Role role : list( Role.class ) ) {
            result.add( ResourceSpec.with( role ) );
        }
        for ( Organization organization : list( Organization.class ) ) {
            result.add( ResourceSpec.with( organization ) );
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
    public List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( ResourceSpec resourceSpec ) {
        List<ResourceSpec> list = new ArrayList<ResourceSpec>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( spec.narrowsOrEquals( resourceSpec ) )
                list.add( spec );
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourcesBroadeningOrEqualTo( ResourceSpec resourceSpec ) {
        List<ResourceSpec> list = new ArrayList<ResourceSpec>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( resourceSpec.narrowsOrEquals( spec ) )
                list.add( spec );
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllContacts( ResourceSpec resourceSpec, boolean isSelf ) {
        Set<ResourceSpec> contacts = new HashSet<ResourceSpec>();
        if ( isSelf ) {
            contacts.addAll( findAllResourcesNarrowingOrEqualTo( resourceSpec ) );
        } else {
            List<Play> plays = findAllPlays( resourceSpec );
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
    public List<Play> findAllPlays( ResourceSpec resourceSpec ) {
        return findAllPlays( resourceSpec, false );
    }

    /**
     * {@inheritDoc}
     */
    public List<Play> findAllPlays( ResourceSpec resourceSpec, boolean specific ) {
        Set<Play> plays = new HashSet<Play>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( Play.hasPlay( flow ) ) {
                    if ( flow.getSource().isPart() ) {
                        Part part = (Part) flow.getSource();
                        if ( part.resourceSpec().matches( resourceSpec, specific ) ) {
                            // sends
                            Play play = new Play( part, flow, true );
                            plays.add( play );
                        }
                    }
                    if ( flow.getTarget().isPart() ) {
                        Part part = (Part) flow.getTarget();
                        if ( part.resourceSpec().matches( resourceSpec, specific ) ) {
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
        List<Issue> foundIssues = new ArrayList<Issue>();
        for ( UserIssue userIssue : list( UserIssue.class ) ) {
            if ( userIssue.getAbout().getId() == identifiable.getId() )
                foundIssues.add( userIssue );
        }
        return foundIssues;
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> findAllIssuesFor( ResourceSpec resourceSpec, boolean specific ) {
        Analyst analyst = Channels.instance().getAnalyst();
        return analyst.findAllIssuesFor( resourceSpec, specific );
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResponsibilitiesOf( Actor actor ) {
        List<ResourceSpec> responsibilities = new ArrayList<ResourceSpec>();
        List<ResourceSpec> resourceSpecs = this.findAllResourcesNarrowingOrEqualTo(
                ResourceSpec.with( actor ) );
        for ( ResourceSpec resourceSpec : resourceSpecs ) {
            ResourceSpec responsibility = new ResourceSpec( resourceSpec );
            assert responsibility.getActor() == actor;
            if ( !responsibility.isAnyRole() ) {
                responsibility.setActor( null );
                responsibilities.add( responsibility );
            }
        }
        return responsibilities;
    }

    /**
     * /**
     * {@inheritDoc}
     */
    public SegmentRelationship findSegmentRelationship(
            Segment fromSegment,
            Segment toSegment ) {
        List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
        List<Part> initiators = new ArrayList<Part>();
        List<Part> terminators = new ArrayList<Part>();
        Iterator<Flow> flows = fromSegment.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( flow.isExternal() ) {
                ExternalFlow externalFlow = (ExternalFlow) flow;
                if ( externalFlow.getConnector().getSegment() == toSegment
                        && !externalFlow.isPartTargeted() ) {
                    externalFlows.add( externalFlow );
                }
            }
        }
        flows = toSegment.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( flow.isExternal() ) {
                ExternalFlow externalFlow = (ExternalFlow) flow;
                if ( externalFlow.getConnector().getSegment() == fromSegment
                        && externalFlow.isPartTargeted() ) {
                    externalFlows.add( externalFlow );
                }
            }
        }
        for ( Part part : findInitiators( toSegment ) ) {
            if ( part.getSegment().equals( fromSegment ) ) initiators.add( part );
        }
        for ( Part part : findExternalTerminators( toSegment ) ) {
            if ( part.getSegment().equals( fromSegment ) ) terminators.add( part );
        }
        if ( externalFlows.isEmpty() && initiators.isEmpty() && terminators.isEmpty() ) {
            return null;
        } else {
            SegmentRelationship segmentRelationship = new SegmentRelationship(
                    fromSegment,
                    toSegment );
            segmentRelationship.setExternalFlows( externalFlows );
            segmentRelationship.setInitiators( initiators );
            segmentRelationship.setTerminators( terminators );
            return segmentRelationship;
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelEntity> EntityRelationship<T> findEntityRelationship( T fromEntity, T toEntity ) {
        List<Flow> entityFlows = new ArrayList<Flow>();
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( flow.getSource().isPart() && flow.getTarget().isPart() ) {
                    Part sourcePart = (Part) flow.getSource();
                    Part targetPart = (Part) flow.getTarget();
                    if ( isExecutedBy( sourcePart, fromEntity )
                            && isExecutedBy( targetPart, toEntity ) ) {
                        entityFlows.add( flow );
                    }
                }
            }
        }
        if ( entityFlows.isEmpty() ) {
            return null;
        } else {
            EntityRelationship<T> entityRel = new EntityRelationship<T>( fromEntity, toEntity );
            entityRel.setFlows( entityFlows );
            return entityRel;
        }

    }

    private boolean isExecutedBy( Part part, ModelEntity entity ) {
        ResourceSpec partSpec = part.resourceSpec();
        if ( entity instanceof Actor && entity.isActual() ) {
            List<Actor> allPlayers = findAllActualActors( partSpec );
            if ( allPlayers.isEmpty() )
                return entity.isUnknown();
            else
                return allPlayers.contains( (Actor) entity );
        } else {
            return partSpec.hasEntity( entity );
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
                        && resourceSpec.narrowsOrEquals( ( (Part) node ).resourceSpec() ) )
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
        Set<Actor> actors = new HashSet<Actor>();
        // If the resource spec is anyone, then return no actor,
        // else it would return every actor known to the app
        if ( !resourceSpec.isAnyone() ) {
            Iterator<ResourceSpec> specs = findAllResourceSpecs().iterator();
            Iterator<ResourceSpec> actorSpecs = new FilterIterator( specs, new Predicate() {
                public boolean evaluate( Object object ) {
                    Actor actor = ( (ResourceSpec) object ).getActor();
                    return actor != null && actor.isActual();
                }
            } );
            while ( actorSpecs.hasNext() ) {
                ResourceSpec actorResourceSpec = actorSpecs.next();
                if ( actorResourceSpec.narrowsOrEquals( resourceSpec ) )
                    actors.add( actorResourceSpec.getActor() );
            }
        }

        return new ArrayList<Actor>( actors );
    }

    private void visitParts( Set<Part> visited, ResourceSpec spec, Segment segment, boolean exactMatch ) {
        // Add matches
        for ( Segment s : getSegments( segment ) )
            for ( Iterator<Part> partIterator = s.parts(); partIterator.hasNext(); ) {
                Part part = partIterator.next();
                if ( spec.matches( part.resourceSpec(), exactMatch ) )  // was exactMatch == true
                    visited.add( part );
            }

        Actor actor = spec.getActor();
        Organization organization = spec.getOrganization();
        Role role = spec.getRole();
        Place jurisdiction = spec.getJurisdiction();

        if ( actor != null ) {
            if ( role == null ) {
                // Add parts with all of actor's roles
                for ( ResourceSpec job : findAllJobSpecs( organization, actor ) ) {
                    ResourceSpec s = new ResourceSpec( spec );
                    s.setRole( job.getRole() );
                    s.setJurisdiction( job.getJurisdiction() );
                    s.setOrganization( job.getOrganization() );
                    visitParts( visited, s, segment, exactMatch );
                }
            } else {
                // Add regular role parts
                ResourceSpec s = new ResourceSpec( spec );
                s.setActor( null );
                visitParts( visited, s, segment, exactMatch );
            }
        }

        if ( organization != null ) {

            if ( role == null && actor == null && jurisdiction == null ) {
                for ( Role r : findRolesIn( organization ) )
                    for ( Actor a : findActors( organization, r, segment ) ) {
                        ResourceSpec s = new ResourceSpec( spec );
                        if ( Actor.UNKNOWN.equals( a ) )
                            s.setRole( r );
                        else
                            s.setActor( a );
                        visitParts( visited, s, segment, exactMatch );
                    }
            }

            ResourceSpec s = new ResourceSpec( spec );
            s.setOrganization( organization.getParent() );
            visitParts( visited, s, segment, exactMatch );

        }

        if ( jurisdiction != null ) {
            // look for parts with no specific jurisdiction
            // TODO process geo inclusions
            ResourceSpec s = new ResourceSpec( spec );
            s.setJurisdiction( null );
            visitParts( visited, s, segment, exactMatch );
        }
    }

    private List<Segment> getSegments( Segment segment ) {
        List<Segment> segments;
        if ( segment == null )
            segments = list( Segment.class );
        else {
            segments = new ArrayList<Segment>();
            segments.add( segment );
        }
        return segments;
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

            ResourceSpec s = new ResourceSpec( spec );
            s.setActor( null );
            addUniqueChannels( channels, findAllChannelsFor( s ) );
        }

        if ( spec.getJurisdiction() != null ) {
            ResourceSpec s = new ResourceSpec( spec );
            s.setJurisdiction( null );
            addUniqueChannels( channels, findAllChannelsFor( s ) );
        }

        if ( spec.getRole() != null ) {
            ResourceSpec s = new ResourceSpec( spec );
            s.setRole( null );
            addUniqueChannels( channels, findAllChannelsFor( s ) );
        }

        Organization organization = spec.getOrganization();
        if ( organization != null ) {
            addUniqueChannels( channels, organization.getEffectiveChannels() );

            ResourceSpec s = new ResourceSpec( spec );
            s.setOrganization( organization.getParent() );
            addUniqueChannels( channels, findAllChannelsFor( s ) );
        }

        return toSortedList( channels );
    }

    private static boolean containsValidChannel( Set<Channel> channels, TransmissionMedium medium ) {
        for ( Channel channel : channels )
            if ( channel.getMedium().equals( medium ) && channel.isValid() )
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
                        && resourceSpec.narrowsOrEquals( contactedPart.resourceSpec() ) )
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
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( spec.getRole() != null ) {
                if ( spec.getActor() != null && actor.narrowsOrEquals( spec.getActor() )
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
    public List<Actor> findActors( Organization organization, Role role ) {
        ResourceSpec resourceSpec = new ResourceSpec();
        resourceSpec.setRole( role );
        resourceSpec.setOrganization( organization );

        // Find all actors in role for organization
        Set<Actor> actors = new HashSet<Actor>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( spec.getActor() != null ) {
                boolean sameOrg = Organization.UNKNOWN.equals( organization ) ?
                        spec.getOrganization() == null
                        : organization.equals( spec.getOrganization() );
                boolean sameRole = Role.UNKNOWN.equals( role ) ?
                        spec.getRole() == null
                        : role.equals( spec.getRole() );
                if ( sameOrg && sameRole )
                    actors.add( spec.getActor() );
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

    /**
     * {@inheritDoc}
     */
    public List<Actor> findActors( Organization organization, Role role, Segment segment ) {
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

            if ( sameOrg && sameRole ) {
                if ( part.getActor() != null )
                    actors.add( part.getActor() );
                else
                    noActorRoleFound = true;
            }
        }
        return noActorRoleFound ? findActors( organization, role )
                : toSortedList( actors );
    }

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

    private List<ResourceSpec> findAllJobSpecs( Organization organization, Actor actor ) {
        List<ResourceSpec> jobs = new ArrayList<ResourceSpec>();
        List<Organization> orgs;
        if ( organization == null )
            orgs = listActualEntities( Organization.class );
        else {
            orgs = new ArrayList<Organization>();
            if ( organization.isActual() ) {
                orgs.add( organization );
            } else {
                orgs.addAll( findAllActualEntitiesMatching( Organization.class, organization ) );
            }
        }

        for ( Organization org : orgs ) {
            for ( Job job : org.getJobs() ) {
                if ( actor.equals( job.getActor() ) ) {
                    jobs.add( job.resourceSpec( org ) );
                }
            }
            for ( Job job : findUnconfirmedJobs( org ) ) {
                if ( actor.equals( job.getActor() ) ) {
                    jobs.add( job.resourceSpec( org ) );
                }
            }
        }
        return jobs;
    }

    /**
     * {@inheritDoc}
     */
    public List<Job> findAllConfirmedJobs( ResourceSpec resourceSpec ) {
        List<Job> jobs = new ArrayList<Job>();
        for ( Organization org : listActualEntities( Organization.class ) ) {
            for ( Job job : org.getJobs() ) {
                if ( job.resourceSpec( org ).narrowsOrEquals( resourceSpec ) ) {
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
    public List<Part> findAllParts( Segment segment, ResourceSpec resourceSpec, boolean exactMatch ) {
        Set<Part> list = new HashSet<Part>();
        visitParts( list, resourceSpec, segment, exactMatch );
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
                                && Matcher.same( receive.getName(), other.getName() );
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
                                && Matcher.same( send.getName(), other.getName() );
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
        return Matcher.same( send.getName(), need.getName() )
                &&
                Matcher.hasCommonEOIs(
                        send,
                        need,
                        this );
    }

    private boolean doFindIfSegmentStarted( Segment segment, Set<ModelObject> visited ) {
        if ( planManager.getCurrentPlan().isIncident( segment.getEvent() ) ) return true;
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
        Plan plan = planManager.getCurrentPlan();
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
            if ( part.isTerminatesEventPhase() || part.getGoals().contains( goal ) ) {
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

        ResourceSpec spec = ResourceSpec.with( actor );
        for ( Segment s : list( Segment.class ) ) {
            List<Part> parts = findAllParts( s, spec, false );
            if ( !parts.isEmpty() )
                result.add( s );
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<Actor> findActors( Segment segment ) {
        Set<Actor> actors = new HashSet<Actor>();
        for ( Iterator<Part> pi = segment.parts(); pi.hasNext(); ) {
            Part p = pi.next();
            actors.addAll( findAllActualActors( p.resourceSpec() ) );
        }

        List<Actor> result = new ArrayList<Actor>( actors );
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
                    public Object transform( Object obj ) {
                        return ( (Actor) obj ).getLastName();
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
                    public boolean evaluate( Object obj ) {
                        Role empRole = ( (Employment) obj ).getRole();
                        return empRole != null && empRole.narrowsOrEquals( role );
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
                    public boolean evaluate( Object obj ) {
                        Actor empActor = ( (Employment) obj ).getActor();
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
    public List<Issue> findAllIssues( Analyst analyst ) {
        List<Issue> allIssues = new ArrayList<Issue>();
        // allIssues.addAll( analyst.listIssues( getPlan(), true ) );
        for ( ModelObject mo : list( ModelObject.class ) ) {
            allIssues.addAll( analyst.listIssues( mo, true ) );
        }
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                allIssues.addAll( analyst.listIssues( parts.next(), true ) );
            }
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                allIssues.addAll( analyst.listIssues( flows.next(), true ) );
            }
        }
        return allIssues;
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> findAllUnwaivedIssues( Analyst analyst ) {
        List<Issue> allUnwaivedIssues = new ArrayList<Issue>();
        // allUnwaivedIssues.addAll( analyst.listUnwaivedIssues( getPlan(), true ) );
        for ( ModelObject mo : list( ModelObject.class ) ) {
            allUnwaivedIssues.addAll( analyst.listUnwaivedIssues( mo, true ) );
        }
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                allUnwaivedIssues.addAll( analyst.listUnwaivedIssues( parts.next(), true ) );
            }
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                allUnwaivedIssues.addAll( analyst.listUnwaivedIssues( flows.next(), true ) );
            }
        }
        return allUnwaivedIssues;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public <T extends ModelEntity> List<T> listEntitiesWithUnknown( Class<T> entityClass ) {
        List<T> allEntities = new ArrayList<T>( list( entityClass ) );
        ModelEntity unknown =
                entityClass == Actor.class
                        ? Actor.UNKNOWN
                        : entityClass == Event.class
                        ? Event.UNKNOWN
                        : entityClass == Organization.class
                        ? Organization.UNKNOWN
                        : entityClass == Place.class
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
                    public boolean evaluate( Object obj ) {
                        Event repondedEvent = ( (Segment) obj ).getEvent();
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
    public List<Attachment> findAllAttachments() {
        List<Attachment> allAttachments = new ArrayList<Attachment>();
        for ( ModelObject mo : findAllModelObjects() ) {
            allAttachments.addAll( mo.getAttachments() );
        }
        return allAttachments;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> findAllAttached() {
        Set<String> allAttachedUrls = new HashSet<String>();
        for ( Attachment attachment : findAllAttachments() ) {
            allAttachedUrls.add( attachment.getUrl() );
        }
        return new ArrayList<String>( allAttachedUrls );
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
    @SuppressWarnings( "unchecked" )
    public List<Place> findAllPlacesWithin( final Place place ) {
        return (List<Place>) CollectionUtils.select(
                listActualEntities( Place.class ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Place other = (Place) obj;
                        return
                                !other.equals( place )
                                        && ( other.isInside( place )
                                        || place.isRegion() && other.isGeoLocatedIn( place.geoLocate() ) );
                    }
                } );
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends ModelObject> findAllModelObjectsIn( Place place ) {
        List<ModelObject> inPlace = new ArrayList<ModelObject>();
        for ( Organization org : list( Organization.class ) ) {
            if ( org.getLocation() != null && org.getLocation().narrowsOrEquals( place ) )
                inPlace.add( org );
        }
        for ( Event event : listReferencedEntities( Event.class ) ) {
            if ( event.getScope() != null && event.getScope().narrowsOrEquals( place ) )
                inPlace.add( event );
        }
        for ( Place p : list( Place.class ) ) {
            if ( !p.equals( place ) && p.narrowsOrEquals( place ) )
                inPlace.add( p );
        }
        for ( Segment segment : list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.getLocation() != null && part.getLocation().narrowsOrEquals( place ) )
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

    private boolean hasAncestor(
            Hierarchical hierarchical,
            final Hierarchical other,
            final Set<Hierarchical> visited ) {
        if ( !visited.contains( hierarchical ) ) {
            visited.add( hierarchical );
            List<Hierarchical> superiors = hierarchical.getSuperiors();
            return !superiors.isEmpty()
                    && ( superiors.contains( other )
                    || CollectionUtils.find(
                    superiors,
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return hasAncestor( (Hierarchical) obj, other, visited );
                        }
                    } ) != null );
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void replayJournals( Commander commander ) {
        getPlanManager().replayJournals( this, commander );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSemanticMatch( String text, String otherText, Proximity proximity ) {
        return Matcher.same( text, otherText )
                || semanticMatcher.matches( text.trim(), otherText.trim(), proximity );
    }

    /**
     * {@inheritDoc}
     */
    public boolean likelyRelated( String text, String otherText ) {
        return
                Matcher.matches( text, otherText ) ||
                        isSemanticMatch(
                                StringUtils.uncapitalize( text ),
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
                    && part.resourceSpec().narrowsOrEquals( p.resourceSpec() ) ) {
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
            if ( in.getSource().isPart() && Matcher.matches( in.getName(), flowName ) ) {
                commitments.add( in );
            }
        }
        return commitments;
    }

    /**
     * {@inheritDoc}
     */
    /**
     * {@inheritDoc}
     */
    public Level getPartPriority( Part part ) {
        return getPartPriority( part, new ArrayList<Part>() );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Actor> findAllActorsAsUser( final String userName ) {
        return (List<Actor>) CollectionUtils.select(
                listActualEntities( Actor.class ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        String name = ( (Actor) obj ).getUserName();
                        return name != null && name.equals( userName );
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    public Plan getCurrentPlan() {
        return planManager.getCurrentPlan();
    }

    private Level getPartPriority( Part part, List<Part> visited ) {
        visited.add( part );
        Level max = Level.Low;
        for ( Goal risk : part.getMitigations() ) {
            if ( risk.getLevel().getOrdinal() > max.getOrdinal() )
                max = risk.getLevel();
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
    public String findIconName( Part part, String imagesDirName ) {
        String iconName = null;
        if ( part.getActor() != null ) {
            if ( part.getActor().isType() ) {
                Actor knownActor = part.getKnownActualActor();
                if ( knownActor != null ) {
                    iconName = imagingService.getIconPath( knownActor );
                }
            }
            if ( iconName == null ) {
                iconName = imagingService.getIconPath( part.getActor() );
            }
            if ( iconName == null ) {
                iconName = imagesDirName + "/" + ( part.isSystem() ? "system" : "person" );
            }
        } else if ( part.getRole() != null ) {
            Actor knownActor = part.getKnownActualActor();
            boolean onePlayer = knownActor != null;
            if ( onePlayer ) {
                iconName = imagingService.getIconPath( knownActor );
                if ( iconName == null ) {
                    iconName = imagesDirName + "/" + ( part.isSystem() ? "system" : "person" );
                }
            } else {
                iconName = imagingService.getIconPath( part.getRole() );
                if ( iconName == null ) {
                    iconName = imagesDirName + "/" + ( part.isSystem() ? "system" : "role" );
                }
            }
        } else if ( part.getOrganization() != null ) {
            iconName = imagingService.getIconPath( part.getOrganization() );
            if ( iconName == null ) {
                iconName = imagesDirName + "/" + "organization";
            }
        } else {
            iconName = imagesDirName + "/" + "unknown";
        }
        return iconName;
    }

    /**
     * {@inheritDoc}
     */
    public String findIconName( ModelObject modelObject, String imagesDirName ) {
        String iconName = imagingService.getIconPath( modelObject );
        if ( iconName == null ) {
            if ( modelObject instanceof Actor ) {
                iconName = imagesDirName + "/" + ( ( (Actor) modelObject ).isSystem() ? "system" : "person" );
            } else if ( modelObject instanceof Role ) {
                iconName = imagesDirName + "/" + "role";
            } else if ( modelObject instanceof Organization ) {
                iconName = imagesDirName + "/" + "organization";
            } else {
                iconName = imagesDirName + "/" + "unknown";
            }
        }
        return iconName;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<String> findAllPlanners() {
        return (List<String>) CollectionUtils.collect(
                userDetailsService.getAllPlanners(),
                TransformerUtils.invokerTransformer( "getUsername" )
        );
    }

    /**
     * {@inheritDoc}
     */
    public String findUserFullName( String userName ) {
        User user = userDetailsService.getUserNamed( userName );
        if ( user != null ) {
            return user.getFullName();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String findUserNormalizedFullName( String userName ) {
        User user = userDetailsService.getUserNamed( userName );
        if ( user != null ) {
            return user.getNormalizedFullName();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<String> findAllPlanUsernames() {
        return userDetailsService.getAllPlanUsernames();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Segment> findAllSegmentsForPhase( final Phase phase ) {
        return (List<Segment>) CollectionUtils.select(
                list( Segment.class ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Segment) obj ).getPhase().equals( phase );
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
                    public boolean evaluate( Object obj ) {
                        if ( ( (ModelEntity) obj ).getTags().contains( entityType ) ) {
                            return true;
                        } else if ( obj instanceof Event ) {
                            if ( ModelObject.areIdentical(
                                    ( (Event) obj ).getScope(),
                                    entityType ) )
                                return true;
                        } else if ( obj instanceof Organization ) {
                            if ( ModelObject.areIdentical(
                                    ( (Organization) obj ).getLocation(),
                                    entityType ) )
                                return true;
                            if ( ModelObject.areIdentical(
                                    ( (Organization) obj ).getParent(),
                                    entityType ) )
                                return true;
                        } else if ( obj instanceof Place ) {
                            if ( ( (Place) obj ).getMustContain().references( entityType ) )
                                return true;
                            if ( ( (Place) obj ).getMustBeContainedIn().references( entityType ) )
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
                    public boolean evaluate( Object obj ) {
                        Part part = (Part) obj;
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
                    public boolean evaluate( Object obj ) {
                        return ( (ModelEntity) obj ).narrowsOrEquals( entity );
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
                    public boolean evaluate( Object obj ) {
                        Organization partOrg = ( (Part) obj ).getOrganization();
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
        return planManager.getCurrentPlan().getOrganizations().contains( organization );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Part> findAllPartsPlayedBy( final Organization organization ) {
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
            if ( employment.playsPart( part ) )
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
                if ( employment.playsPart( part ) )
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
                    if ( source.getActor().equals( beneficiary.getActor() )
                            || commitment.passesClearanceTest() ) {
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
        for ( Flow flow : findAllRelatedFlows( ResourceSpec.with( actor ), false ) ) {
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
                            public boolean evaluate( Object obj ) {
                                return ( (ExternalFlow) obj ).getTarget().equals( part );
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
                                return Agreement.from( commitment ).encompasses(
                                        (Agreement) object,
                                        DefaultQueryService.this );
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
                if ( commitment.isBetweenOrganizations() && agreement.covers( commitment, this ) )
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
                            List<Flow> alternates = flow.getAlternates();
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
            if ( ( (Flow) segmentObject ).isEssential( assumeFails ) ) {
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
                                IteratorUtils.toList( target.sends() ) ).isEmpty();
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

    /**
     * {@inheritDoc}
     */
    public List<Organization> findAllInvolvedOrganizations( Segment segment ) {
        Set<Organization> organizations = new HashSet<Organization>();
        Iterator<Part> parts = segment.parts();
        boolean hasUnknown = false;
        while ( parts.hasNext() ) {
            Part part = parts.next();
            Organization organization = part.getOrganization();
            if ( organization != null ) {
                if ( organization.isActual() ) {
                    organizations.add( organization );
                } else {
                    organizations.addAll( findAllActualEntitiesMatching( Organization.class, organization ) );
                }
            } else {
                hasUnknown = true;
            }

        }
        List<Organization> results = new ArrayList<Organization>( organizations );
        Collections.sort( results );
        if ( hasUnknown )
            results.add( Organization.UNKNOWN );
        return results;
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
                        return entity.isActual() && entity.narrowsOrEquals( entityType );
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


}

