package com.mindalliance.channels.dao;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Wrapper for per-plan dao-related operations.
 */
public class PlanDao extends Memory {
    /**
     * Name of file contining last id used in plan or and in journal, if any.
     */
    private static final String LAST_ID_FILE = "lastid";
    /**
     * Name of persisted data file.
     */
    public static final String DATA_FILE = "data.xml";

    /**
     * Name of command journal file.
     */
    private static final String JOURNAL_FILE = "journal.xml";

    /**
     * Name of the default event.
     */
    private static final String DEFAULT_EVENT_NAME = "UNNAMED";

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    /**
     * The wrapped plan.
     */
    private final Plan plan;

    /**
     * Pending commands attached to wrapped plan.
     */
    private Journal journal = new Journal();

    /**
     * The plan manager.
     */
    private final PlanManager planManager;

    //-------------------------------------
    PlanDao( PlanManager planManager, Plan plan ) {
        this.plan = plan;
        this.planManager = planManager;
    }

    @Override
    public Plan getPlan() {
        return plan;
    }

    @Override
    public PlanManager getPlanManager() {
        return planManager;
    }

    public synchronized Journal getJournal() {
        return journal;
    }

    /**
     * @return the id generator
     */
    @Override
    public IdGenerator getIdGenerator() {
        return planManager.getIdGenerator();
    }

    /**
     * Load plan data.
     *
     * @param importer the loading mechanism
     * @throws IOException on loading errors
     */
    public synchronized void load( Importer importer ) throws IOException {
        Long lastId = readLastAssignedId();
        if ( lastId > 0L ) getIdGenerator().setLastAssignedId( lastId, getPlan() );
        File dataFile = getDataFile();
        if ( dataFile.length() > 0L ) {
            logger.info( "Importing snapshot for plan {}", plan );
            FileInputStream in = null;
            try {
                in = new FileInputStream( dataFile );
                importer.importPlan( in );
            } finally {
                if ( in != null )
                    in.close();
            }
        }
        journal = loadJournal( importer );
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
            logger.warn( "IO failure", e );
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
        File baseDirectory = planManager.getDataDirectory().getFile();
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
        if ( getPlan().isDevelopment() && importer != null )
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
            updateLastIdRecord();
        }
    }

    private void takeSnapshot( Exporter exporter ) throws IOException {
        logger.info( "Taking snapshot of plan {}", plan.getUri() );

        // Make backup
        File dataFile = getDataFile();
        if ( dataFile.length() > 0L ) {
            String backupPath = dataFile.getAbsolutePath() + "_" + System.currentTimeMillis();
            File backup = new File( backupPath );
            dataFile.renameTo( backup );
        }

        // snap
        FileOutputStream out = null;
        try {
            out = new FileOutputStream( getDataFile() );
            exporter.export( plan, out );
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
            updateLastIdRecord();
        } finally {
            if ( out != null )
                out.close();
        }
    }

    /**
     * Remove persistent files, for test purposes.
     *
     * @throws IOException on errors
     */
    public synchronized void reset() throws IOException {
        getJournalFile().delete();
        getDataFile().delete();
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
            if ( command.forcesSnapshot() || journal.size() >= planManager.getSnapshotThreshold() )
                save( exporter );
            else {
                journal.addCommand( command );
                saveJournal( exporter );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( "Failed to save journal", e );
        }
    }

    private void updateLastIdRecord() throws IOException {
        long lastId = getIdGenerator().getLastAssignedId( getPlan() );
        getLastIdFile().delete();
        PrintWriter out = null;
        try {
            out = new PrintWriter( new FileOutputStream( getLastIdFile() ) );
            out.print( lastId );
        } finally {
            if ( out != null )
                out.close();
        }
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
     * @param queryService to use for validation
     */
    public void validate( QueryService queryService ) {
        // Make sure there is at least one event per plan
        List<Event> incidents = plan.getIncidents();
        if ( incidents.isEmpty() ) {
            Event unnamedEvent = findOrCreate( Event.class, DEFAULT_EVENT_NAME, null );
            plan.addIncident( unnamedEvent );
        } else if ( incidents.size() > 1 ) {
            // Remove UNNAMED event if not referenced
            Event event = find( Event.class, DEFAULT_EVENT_NAME );
            if ( event != null && queryService.getReferenceCount( event ) <= 1 ) {
                incidents.remove( event );
                remove( event );
            }
        }
        // Make sure there is at least one phase.
        if ( plan.getPhases().isEmpty() ) {
            plan.addDefaultPhase( queryService );
        }
        // Make sure there is at least one scenario per plan
        if ( !list( Scenario.class ).iterator().hasNext() )
            plan.addScenario( queryService.createScenario() );
    }
}
