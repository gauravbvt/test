package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.model.UserIssue;
import org.apache.commons.collections.iterators.IteratorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Wrapper for per-plan dao-related operations.
 */
public class PlanDao extends AbstractDao {

    /**
     * Lowest id for mutable model objects.
     */
    public static final long IMMUTABLE_RANGE = -1000L;

    /**
     * Name of the default event.
     */
    private static final String DEFAULT_EVENT_NAME = "unnamed";

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanDao.class );

    /**
     * The wrapped plan.
     */
    private Plan plan;

    /**
     * Pending commands attached to wrapped plan.
     */
    private Journal journal = new Journal();

    /** The user service. */
    private UserService userService;

    /** The plan version. */
    private final PlanDefinition.Version version;

    private IdGenerator idGenerator;

    //-------------------------------------
    PlanDao( PlanDefinition.Version version ) {
        this.version = version;
    }

    public PlanDefinition.Version getVersion() {
        return version;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Plan getPlan() {
        if ( plan == null )
            throw new IllegalStateException( "Plan not loaded" );
        return plan;
    }

    public synchronized boolean isLoaded() {
        return plan != null;
    }

    private void unload( Exporter exporter ) {
        try {
            synchronized ( this ) {
                save( exporter );
                plan = null;
            }
        } catch ( IOException e ) {
            LOG.warn( "Unable to unload " + version + ". Plan remains in memory.", e );
        }
    }

    /**
     * Save the plan to the file repository.
     * @param exporter where to export
     * @throws IOException on errors
     */
    public synchronized void save( Exporter exporter ) throws IOException {
        if ( isLoaded() ) {
            takeSnapshot( exporter );
            version.getJournalFile().delete();
            journal.reset();
            version.setLastId( idGenerator.getLastAssignedId( plan ) );
        }
    }

    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator( IdGenerator idGenerator ) {
        this.idGenerator = idGenerator;
    }

    public synchronized Journal getJournal() {
        return journal;
    }

    public UserService getUserDetailsService() {
        return userService;
    }

    public void setUserDetailsService( UserService userService ) {
        this.userService = userService;
    }

    private Journal loadJournal( Importer importer ) throws IOException {
        FileInputStream inputStream = null;
        if ( version.isDevelopment() && importer != null )
            try {
                File journalFile = version.getJournalFile();
                if ( journalFile.length() > 0L ) {
                    inputStream = new FileInputStream( journalFile );
                    return importer.importJournal( inputStream );
                }

            } finally {
                if ( inputStream != null )
                    inputStream.close();
            }

        return new Journal();
    }

    private void takeSnapshot( Exporter exporter ) throws IOException {
        LOG.info( "Taking snapshot of plan {}", plan.getUri() );

        // Make backup
        File dataFile = version.getDataFile();
        if ( dataFile.length() > 0L ) {
            String backupPath = dataFile.getAbsolutePath() + '_' + System.currentTimeMillis();
            File backup = new File( backupPath );
            dataFile.renameTo( backup );
        }

        // snap
        FileOutputStream out = null;
        try {
            out = new FileOutputStream( version.getDataFile() );
            exporter.export( out );
        } finally {
            if ( out != null )
                out.close();
        }
    }

    /**
     * Save outstanding journal entries.
     *
     * @param exporter the persistence mechanism
     * @throws IOException on errors
     */
    void saveJournal( Exporter exporter ) throws IOException {
        version.getJournalFile().delete();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream( version.getJournalFile() );
            exporter.export( getJournal(), out );
            version.setLastId( idGenerator.getLastAssignedId( plan ) );
        } finally {
            if ( out != null )
                out.close();
        }
    }

    /**
     * Validate the underlying plan.
     */
    void validate() {
        // Make sure there is at least one event per plan
        List<Event> incidents = plan.getIncidents();
        if ( incidents.isEmpty() ) {
            Event unnamedEvent = findOrCreate( Event.class, DEFAULT_EVENT_NAME, null );
            unnamedEvent.setType();
            plan.addIncident( unnamedEvent );

        } else if ( incidents.size() > 1 ) {
            // Remove default event if not referenced
            Event event = find( Event.class, DEFAULT_EVENT_NAME );
            if ( event != null && getReferenceCount( event ) <= 1 ) {
                incidents.remove( event );
                remove( event );
            }
        }

        // Make sure there is at least one phase.
        if ( plan.getPhases().isEmpty() )
            addDefaultPhase();

        // Make sure there is at least one segment per plan
        if ( !list( Segment.class ).iterator().hasNext() )
            plan.addSegment( createSegment( null, null ) );
    }

    /**
     * Add default phase to plan.
     */
    private void addDefaultPhase() {
        Phase phase = findOrCreate( Phase.class, Plan.DEFAULT_PHASE_NAME, null );
        phase.setActual();
        phase.setTiming( Plan.DEFAULT_PHASE_TIMING );

        plan.addPhase( phase );
    }

    public Segment createSegment( Long id, Long defaultPartId ) {
        Segment newSegment = new Segment();
        add( newSegment, id );

        newSegment.setName( Segment.DEFAULT_NAME );
        newSegment.setDescription( Segment.DEFAULT_DESCRIPTION );
        // Make sure a plan segment responds to an event.
        newSegment.setEvent( plan.getDefaultEvent() );
        newSegment.setPhase( plan.getDefaultPhase() );
        createPart( newSegment, defaultPartId );
        return newSegment;
    }

    /**
     * Get reference count for event.
     *
     * @param event an event
     * @return an int
     */
    private int getReferenceCount( Event event ) {
        int count = 0;
        for ( Event incident : plan.getIncidents() ) {
            if ( incident.equals( event ) ) count++;
        }
        // look in plan segments
        for ( Segment segment : list( Segment.class ) ) {
            if ( event.equals( segment.getEvent() ) ) count++;
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.initiatesEvent() && event.equals( part.getInitiatedEvent() ) ) count++;
            }
        }
        return count;
    }


    @SuppressWarnings( { "unchecked" } )
    public Iterator<ModelEntity> iterateEntities() {
        Set<? extends ModelObject> referencers = getReferencingObjects();
        Class<?>[] classes = {
            TransmissionMedium.class, Actor.class, Role.class, Place.class, Organization.class,
            Event.class, Phase.class, Participation.class
        };

        Iterator<? extends ModelEntity>[] iterators = new Iterator[ classes.length ];
        for ( int i = 0; i < classes.length; i++ ) {
            Class<? extends ModelEntity> clazz = (Class<? extends ModelEntity>) classes[i];
            iterators[i] = listReferencedEntities( clazz, referencers ).iterator();
        }
        return (Iterator<ModelEntity>) new IteratorChain( iterators );
    }

    private <T extends ModelEntity> List<T> listReferencedEntities(
            Class<T> clazz, Set<? extends ModelObject> referencers ) {

        Collection<T> inputCollection = list( clazz );
        List<T> answer = new ArrayList<T>( inputCollection.size() );

        for ( T item : inputCollection )
            if ( item.isImmutable() && !item.isUnknown()
                 || isReferenced( item, referencers ) )
                answer.add( item );

        return answer;
    }

    @SuppressWarnings( { "unchecked", "RawUseOfParameterizedType" } )
    private boolean isReferenced( ModelObject mo, Set<? extends ModelObject> referencingObjects ) {
        if ( mo instanceof Participation ) {
            // Participations are not referenced per se but are not obsolete if they name a
            // registered user.
            Participation participation = (Participation) mo;
            User user = userService.getUserNamed( participation.getUsername() );
            return user != null;

        } else if ( plan.references( mo ) )
            return true;

        for ( ModelObject object : referencingObjects )
            if ( object.references( mo ) )
                return true;

        return false;
    }

    @SuppressWarnings( { "unchecked" } )
    private Set<? extends ModelObject> getReferencingObjects() {
        Set<? extends ModelObject> referencingObjects = new HashSet<ModelObject>();
        for ( Class refClass : ModelObject.referencingClasses() )
            referencingObjects.addAll( findAllModelObjects( refClass ) );
        return referencingObjects;
    }

    @SuppressWarnings( { "unchecked" } )
    private <T extends ModelObject> List<T> findAllModelObjects( Class<T> clazz ) {
        List<T> domain;
        boolean isPart = Part.class.isAssignableFrom( clazz );
        boolean isFlow = Flow.class.isAssignableFrom( clazz );

        if ( isPart || isFlow ) {
            domain = new ArrayList<T>();
            for ( Segment segment : list( Segment.class ) ) {
                Iterator<T> items = (Iterator<T>) ( isPart ? segment.parts() : segment.flows() );
                while ( items.hasNext() )
                    domain.add( items.next() );
            }

        } else
            domain = list( clazz );

        return domain;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Issue> findAllUserIssues( ModelObject modelObject ) {
        List<Issue> foundIssues = new ArrayList<Issue>();
        for ( UserIssue userIssue : list( UserIssue.class ) ) {
            if ( userIssue.getAbout().getId() == modelObject.getId() )
                foundIssues.add( userIssue );
        }
        return foundIssues;
    }


    /**
     * Define all immutable entities (not plan dependent).
     * @param media predefined media
     */
    public void defineImmutableEntities( List<TransmissionMedium> media ) {
        // TODO cleanup UNKNOWN mess
        long id = idGenerator.getLastAssignedId( plan );
        idGenerator.setLastAssignedId( IMMUTABLE_RANGE, plan );
        Actor.UNKNOWN = findOrCreate( Actor.class, Actor.UnknownName, null );
        Actor.UNKNOWN.makeImmutable();
        Event.UNKNOWN = findOrCreate( Event.class, Event.UnknownName, null );
        Event.UNKNOWN.makeImmutable();
        Organization.UNKNOWN = findOrCreate( Organization.class, Organization.UnknownName, null );
        Organization.UNKNOWN.setActual();
        Organization.UNKNOWN.makeImmutable();
        Phase.UNKNOWN = findOrCreate( Phase.class, Phase.UnknownName, null );
        Phase.UNKNOWN.makeImmutable();
        // Unknown place
        Place.UNKNOWN = findOrCreate( Place.class, Place.UnknownPlaceName, null );
        Place.UNKNOWN.makeImmutable();
        // Administrative area types
        Place administrativeArea = findOrCreateType( Place.class, Place.ADMINISTRATIVE_AREA, null );
        administrativeArea.makeImmutable();
        Place.Country = findOrCreateType( Place.class, Place.COUNTRY, null );
        Place.Country.addTag( administrativeArea );
        Place.Country.makeImmutable();
        Place.State = findOrCreateType( Place.class, Place.STATE, null );
        Place.State.addTag( administrativeArea );
        Place.State.setWithin( Place.Country );
        Place.State.makeImmutable();
        Place.County = findOrCreateType( Place.class, Place.COUNTY, null );
        Place.County.addTag( administrativeArea );
        Place.County.setWithin( Place.State );
        Place.County.makeImmutable();
        Place.City = findOrCreateType( Place.class, Place.CITY, null );
        Place.City.addTag( administrativeArea );
        Place.City.setWithin( Place.County );
        Place.City.makeImmutable();
        Role.UNKNOWN = findOrCreate( Role.class, Role.UnknownName, null );
        Role.UNKNOWN.makeImmutable();
        TransmissionMedium.UNKNOWN = findOrCreateType( TransmissionMedium.class,
                                                       TransmissionMedium.UnknownName, null );
        TransmissionMedium.UNKNOWN.makeImmutable();
        for ( TransmissionMedium medium : media ) {
            add( medium );
            medium.makeImmutable();
        }
        Participation.UNKNOWN = findOrCreateType( Participation.class,
                                                  Participation.UnknownName, null );
        Participation.UNKNOWN.makeImmutable();

        // Make sure that there is one participation per user
        if ( userService != null )
            for ( String username : userService.getUsernames( plan.getUri() ) ) {
                Participation p = findOrCreate( Participation.class, username, null );
                p.setActual();
            }

        idGenerator.setLastAssignedId( id, plan );
    }

    /**
     * Load persisted plan.
     * @param importer what to use for importing
     * @return the loaded plan
     * @throws IOException on error
     */
    public synchronized Plan load( Importer importer ) throws IOException {
        FileInputStream in = null;
        try {
            idGenerator.setLastAssignedId( version.getLastId(), plan );
            File dataFile = version.getDataFile();
            if ( dataFile.exists() ) {
                LOG.info( "Importing snapshot for plan {} from {}",
                          plan, dataFile.getAbsolutePath() );
                in = new FileInputStream( dataFile );
                importer.importPlan( in );

                journal = loadJournal( importer );
            }
            add( plan, plan.getId() );
            validate();


        } finally {
            if ( in != null )
                in.close();
        }

        return plan;
    }

    void resetPlan() {
        plan = version.createPlan( idGenerator );
    }
}
