package com.mindalliance.channels.dao;

import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
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
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Wrapper for per-plan dao-related operations.
 */
public class PlanDao extends Memory {

    /**
     * Name of persisted data file.
     */
    public static final String DATA_FILE = "data.xml";

    /**
     * Name of file contining last id used in plan or and in journal, if any.
     */
    private static final String LAST_ID_FILE = "lastid";

    /**
     * Name of command journal file.
     */
    private static final String JOURNAL_FILE = "journal.xml";

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
    private final Plan plan;

    /**
     * Pending commands attached to wrapped plan.
     */
    private Journal journal = new Journal();

    /** The id generator. */
    private final IdGenerator idGenerator;

    /** Base directory of data. */
    private final File baseDirectory;

    /** Interval of commands at which snapshots are taken. */
    private int snapshotThreshold = 10;

    private FileUserDetailsService userDetailsService;

    /**
     * Lowest id for mutable model objects.
     */
    public static final long IMMUTABLE_RANGE = -1000L;

    //-------------------------------------
    PlanDao( Plan plan, File baseDirectory, IdGenerator idGenerator ) {
        this.plan = plan;
        this.idGenerator = idGenerator;
        this.baseDirectory = baseDirectory;
    }

    @Override
    public Plan getPlan() {
        return plan;
    }

    public synchronized int getSnapshotThreshold() {
        return snapshotThreshold;
    }

    public synchronized void setSnapshotThreshold( int snapshotThreshold ) {
        this.snapshotThreshold = snapshotThreshold;
    }

    public synchronized Journal getJournal() {
        return journal;
    }

    /**
     * @return the id generator
     */
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public FileUserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService( FileUserDetailsService userDetailsService ) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Whether the plan is persisted.
     *
     * @return a boolean
     */
    public boolean isPersisted() {
        try {
            return new File( getPlanStoreDirectory(), DATA_FILE ).exists();
        } catch ( IOException e ) {
            LOG.warn( "IO failure", e );
            return false;
        }
    }

    /**
     * Get the location of the wrapped plan's xml file.
     *
     * @return a file
     * @throws IOException on errors
     */
    private File getDataFile() throws IOException {
        File dataFile = new File( getPlanStoreDirectory(), DATA_FILE );
        if ( !dataFile.exists() )
            dataFile.createNewFile();
        return dataFile;
    }

    /**
     * Get the location of the wrapped plan's data.
     *
     * @return a directory
     * @throws IOException on error
     */
    public File getPlanStoreDirectory() throws IOException {
        File directory = new File(
                baseDirectory,
                sanitize( plan.getUri() ) + File.separator + plan.getVersion() );
        if ( !directory.exists() )
            directory.mkdirs();

        return directory;
    }

    public static String sanitize( String name ) {
        return name.replaceAll( "\\W", "_" );
    }

    /**
     * Get the location of the journal file for the wrapped plan.
     *
     * @return a directory
     * @throws IOException on errors
     */
    private File getJournalFile() throws IOException {
        File data = getPlanStoreDirectory();
        File journalFile = new File( data.getPath(), JOURNAL_FILE );
        if ( !journalFile.exists() )
            journalFile.createNewFile();
        return journalFile;
    }

    private Journal loadJournal( Importer importer ) throws IOException {
        FileInputStream inputStream = null;
        if ( plan.isDevelopment() && importer != null )
            try {
                File journalFile = getJournalFile();
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

    /**
     * Persist all plan data.
     *
     * @param exporter the save mechanism
     * @throws IOException on error
     */
    public synchronized void save( Exporter exporter ) throws IOException {
        if ( exporter != null ) {
            takeSnapshot( exporter );
            getJournalFile().delete();
            journal.reset();
            writeLastAssignedId();
        }
    }

    private void takeSnapshot( Exporter exporter ) throws IOException {
        LOG.info( "Taking snapshot of plan {}", plan.getUri() );

        // Make backup
        File dataFile = getDataFile();
        if ( dataFile.length() > 0L ) {
            String backupPath = dataFile.getAbsolutePath() + '_' + System.currentTimeMillis();
            File backup = new File( backupPath );
            dataFile.renameTo( backup );
        }

        // snap
        FileOutputStream out = null;
        try {
            out = new FileOutputStream( getDataFile() );
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
    private void saveJournal( Exporter exporter ) throws IOException {
        getJournalFile().delete();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream( getJournalFile() );
            exporter.export( getJournal(), out );
            writeLastAssignedId();
        } finally {
            if ( out != null )
                out.close();
        }
    }

    /**
     * Callback after a command was executed.
     * Update snapshot if needed.
     *
     * @param command  the command
     * @param exporter the exporter to use
     */
    public synchronized void onAfterCommand( Command command, Exporter exporter ) {
        try {
            if ( command.forcesSnapshot() || journal.size() >= snapshotThreshold )
                save( exporter );
            else {
                journal.addCommand( command );
                saveJournal( exporter );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( "Failed to save journal", e );
        }
    }

    private void writeLastAssignedId() throws IOException {
        File idFile = getLastIdFile();

        idFile.delete();
        PrintWriter out = null;
        try {
            out = new PrintWriter( new FileOutputStream( idFile ) );
            out.print( idGenerator.getLastAssignedId( plan ) );
        } finally {
            if ( out != null )
                out.close();
        }
    }

    private long readLastAssignedId() throws IOException {
        BufferedReader reader = null;
        Long lastId = 0L;
        try {
            File lastIdFile = getLastIdFile();
            if ( lastIdFile.length() > 0L ) {
                reader = new BufferedReader( new FileReader( lastIdFile ) );
                lastId = Long.parseLong( reader.readLine() );
            }
        } finally {
            if ( reader != null )
                reader.close();
        }
        return lastId;
    }

    private File getLastIdFile() throws IOException {
        File data = getPlanStoreDirectory();
        File lastIdFile = new File( data.getPath(), LAST_ID_FILE );
        if ( !lastIdFile.exists() )
            lastIdFile.createNewFile();
        return lastIdFile;
    }

    /**
     * Validate the underlying plan.
     *
     */
    public void validate() {
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
    public void addDefaultPhase() {
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
    public int getReferenceCount( Event event ) {
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

        return (Iterator<ModelEntity>) IteratorUtils.chainedIterator( iterators );
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
            User user = userDetailsService.getUserNamed( participation.getUsername() );
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
     * Define all immutable entities (not plan dependent).
     * @param media predefined media
     */
    public void defineImmutableEntities( List<TransmissionMedium> media ) {
        idGenerator.setLastAssignedId( IMMUTABLE_RANGE, plan );
        Actor.createImmutables( this );
        Event.createImmutables( this );
        Organization.createImmutables( this );
        Place.createImmutables( this );
        Phase.createImmutables( this );
        Role.createImmutables( this );
        TransmissionMedium.createImmutables( media, this );
        Participation.createImmutables( this );

        // Make sure that there is one participation per user
        if ( userDetailsService != null )
            for ( String username : userDetailsService.getUsernames( plan.getUri() ) ) {
                Participation p = findOrCreate( Participation.class, username, null );
                p.setActual();
            }
    }

    /**
     * Import persisted plan.
     * @param importExportFactory the factory
     * @param builtInMedia prebuilt media
     */
    public void importPlan(
            ImportExportFactory importExportFactory, List<TransmissionMedium> builtInMedia ) {

        try {
            // set last id to start of mutable range
            idGenerator.setLastAssignedId( readLastAssignedId(), plan );
            Importer importer = importExportFactory.createImporter( this );

            File dataFile = getDataFile();
            if ( dataFile.length() > 0L ) {
                LOG.info( "Importing snapshot for plan {} from {}", plan, dataFile.getAbsolutePath() );
                FileInputStream in = null;
                try {
                    in = new FileInputStream( dataFile );
                    importer.importPlan( in );
                } finally {
                    if ( in != null )
                        in.close();
                }

                journal = loadJournal( importer );
            }
        } catch ( IOException e ) {
            String msg = MessageFormat.format( "Unable to import plan {0}", plan.getName() );
            LOG.error( msg, e );
            throw new RuntimeException( msg, e );
        }
    }
}
