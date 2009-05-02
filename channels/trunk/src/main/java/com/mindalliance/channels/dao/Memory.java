package com.mindalliance.channels.dao;

import com.mindalliance.channels.AbstractService;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An in-memory, no-transactions implementation of a store.
 */
public final class Memory extends AbstractService implements Dao {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( Memory.class );
    /**
     * Name of persisted data file.
     */
    private static final String DATA_FILE = "data.xml";
    /**
     * Name of command journal file.
     */
    private static final String JOURNAL_FILE = "journal.xml";
    /**
     * Number of commands journaled before a snapshot is taken on next command.
     */
    private int snapshotThreshold = 10;
    /**
     * Journal contains commands yet to be persisted.
     */
    private Journal journal = new Journal();
    /**
     * Directory where to persist all data.
     */
    private String dataDirectoryPath;

    /**
     * Counter for generated modelobjects id.
     */
    private long idCounter = 1L;

    /**
     * The scenarios, for convenience...
     */
    // private Set<Scenario> scenarios = new HashSet<Scenario>();

    /**
     * For each plan id, ModelObjects indexed by plan.
     */
    private Map<Long, Map<Long, ModelObject>> idIndexMaps = new HashMap<Long, Map<Long, ModelObject>>();

    /**
     * ModelObjects, indexed by id.
     */
    // private Map<Long, ModelObject> idIndex = new HashMap<Long, ModelObject>( INITIAL_CAPACITY );
    public Memory() {
    }

    public void setDataDirectoryPath( String dataDirectoryPath ) {
        this.dataDirectoryPath = dataDirectoryPath;
    }

    public int getSnapshotThreshold() {
        return snapshotThreshold;
    }

    public void setSnapshotThreshold( int snapshotThreshold ) {
        this.snapshotThreshold = snapshotThreshold;
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
    }

    /**
     * {@inheritDoc}
     */
    public void load() {
        // Load app data
        for ( Plan plan : getChannels().getPlans() ) {
            try {
                getChannels().beginUsingPlan( plan );
                loadPlan( plan );
            } finally {
                getChannels().endUsingPlan();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void loadPlan( Plan plan ) {
        // Load plan data
        try {
            Map<Long, Long> idMap = new HashMap<Long, Long>();
            Importer importer = getChannels().getImporter();
            File dataFile = getDataFile( plan );
            if ( dataFile.length() > 0 ) {
                LOG.info( "Importing snapshot for plan " + plan );
                idMap = importer.importAll( new FileInputStream( dataFile ) );
            }
            // Load and run journaled commands
            Journal journal = loadJournal( plan );
            Commander commander = getChannels().getCommander();
            commander.setReplaying( true );
            commander.setIdMap( idMap );
            if ( !journal.isEmpty() ) {
                LOG.info( "Replaying journaled commands for plan " + plan );
                for ( Command command : journal.getCommands() ) {
                    commander.doCommand( command );
                }
            }
            journal.reset();
            commander.reset();
            LOG.info( "Persisted plan reloaded." );
        } catch ( IOException e ) {
            LOG.error( "Failed to load snapshot", e );
        } catch ( CommandException e ) {
            LOG.error( "Failed to replay all commands", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void afterInitialize() {
        for ( Plan plan : getChannels().getPlans() ) {
            try {
                takeSnapshot( plan );
                getJournalFile( plan ).delete();
            } catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onDestroy() {
        afterInitialize();
    }

    private File getDataFile( Plan plan ) throws IOException {
        File dataDirectory = getDataDirectory( plan );
        File dataFile = new File( dataDirectory.getPath() + File.separator + DATA_FILE );
        if ( !dataFile.exists() ) {
            dataFile.createNewFile();
        }
        return dataFile;
    }

    private File getDataDirectory( Plan plan ) {
        File directory;
        if ( dataDirectoryPath != null ) {
            directory = new File( dataDirectoryPath );
            if ( !directory.exists() ) {
                directory.mkdir();
            }
            directory = new File( dataDirectoryPath + "/" + sanitize( plan.getName() ) );
            if ( !directory.exists() ) {
                directory.mkdir();
            }
        } else {
            throw new RuntimeException( "Data directory path is not set." );
        }
        return directory;
    }

    private String sanitize( String name ) {
        return name.replaceAll( "\\W", "_" );
    }

    private File getJournalFile( Plan plan ) throws IOException {
        File dataDirectory = getDataDirectory( plan );
        File journalFile = new File( dataDirectory.getPath() + File.separator + JOURNAL_FILE );
        if ( !journalFile.exists() ) {
            journalFile.createNewFile();
        }
        return journalFile;
    }

    private Journal loadJournal( Plan plan ) {
        Journal journal;
        try {
            File journalFile = getJournalFile( plan );
            Importer importer = getChannels().getImporter();
            if ( journalFile.length() > 0 )
                journal = importer.importJournal( new FileInputStream( journalFile ) );
            else
                journal = new Journal();
        } catch ( IOException e ) {
            LOG.error( "Failed to load journal file", e );
            journal = new Journal();
        }
        return journal;
    }

    /**
     * {@inheritDoc}
     */
    public void onAfterCommand( Command command ) {
        Plan plan = Channels.getPlan();
        if ( command.isMemorable() ) {
            // Update snapshot if required
            if ( journal.size() >= snapshotThreshold ) {
                try {
                    takeSnapshot( plan );
                    getJournalFile( plan ).delete();
                    journal.reset();
                } catch ( IOException e ) {
                    throw new RuntimeException( "Failed to take snapshot", e );

                }
            }
            // else save journaled commands
            else {
                journal.addCommand( command );
                try {
                    saveJournal( plan );
                } catch ( IOException e ) {
                    throw new RuntimeException( "Failed to save journal", e );
                }
            }
        }
    }

    private void takeSnapshot( Plan plan ) throws IOException {
        LOG.info( "Taking snapshot of plan " + plan );
        // Make backup
        File dataFile = getDataFile( plan );
        if ( dataFile.length() > 0 ) {
            String backupPath = dataFile.getAbsolutePath() + "_" + System.currentTimeMillis();
            File backup = new File( backupPath );
            dataFile.renameTo( backup );
        }
        // snap
        dataFile = getDataFile( plan );
        assert dataFile.length() == 0;
        Exporter exporter = getChannels().getExporter();
        exporter.exportAll( new FileOutputStream( dataFile ) );
    }

    private void saveJournal( Plan plan ) throws IOException {
        File journalFile = getJournalFile( plan );
        journalFile.delete();
        journalFile = getJournalFile( plan );
        assert journalFile.length() == 0;
        Exporter exporter = getChannels().getExporter();
        exporter.exportJournal( journal, new FileOutputStream( journalFile ) );
    }

    /**
     * For testing.
     */
    public void reset() {
        for ( Plan plan : getChannels().getPlans() ) {
            try {
                File journalFile = getJournalFile( plan );
                journalFile.delete();
                File dataFile = getDataFile( plan );
                dataFile.delete();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    // Not used yet
    public void stop() {
        for ( Plan plan : getChannels().getPlans() ) {
            try {
                takeSnapshot( plan );
                getJournalFile( plan ).delete();
                LOG.info( "Pre-shutdown snapshot taken." );
            } catch ( IOException e ) {
                LOG.warn( "Failed to take snapshot", e );
            }
        }
    }

    /// CRUD

    private Map<Long, ModelObject> getIdIndex() {
        Map<Long, ModelObject> idIndex = idIndexMaps.get( Channels.getPlan().getId() );
        if ( idIndex == null ) {
            synchronized ( this ) {
                idIndex = new HashMap<Long, ModelObject>();
                idIndexMaps.put( Channels.getPlan().getId(), idIndex );
            }
        }
        return idIndex;
    }

    /**
     * {@inheritDoc}
     */
    public Plan makePlan() {
        Plan plan = new Plan();
        plan.setName( "UNNAMED" );
        plan.setId( newId() );
        return plan;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelObject> List<T> list( final Class<T> clazz ) {
        return (List<T>) CollectionUtils.select( getIdIndex().values(), new Predicate() {
            public boolean evaluate( Object object ) {
                return clazz.isAssignableFrom( object.getClass() );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    public void add( ModelObject object ) {
        if ( getIdIndex().containsKey( object.getId() ) )
            throw new DuplicateKeyException();

        object.setId( newId() );
        getIdIndex().put( object.getId(), object );

        if ( object instanceof Scenario )
            Channels.getPlan().getScenarios().add( (Scenario) object );

    }

    /**
     * {@inheritDoc}
     */
    public void update( ModelObject object ) {
    }

    /**
     * {@inheritDoc}
     */
    public long getScenarioCount() {
        return (long) Channels.getPlan().getScenarios().size();
    }

    /**
     * {@inheritDoc}
     */
    public Part createPart( Scenario scenario ) {
        Part part = new Part();
        part.setId( newId() );
        part.setScenario( scenario );
        return part;
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector( Scenario scenario ) {
        Connector connector = new Connector();
        connector.setId( newId() );
        connector.setScenario( scenario );
        return connector;
    }

    /**
     * {@inheritDoc}
     */
    public ExternalFlow createExternalFlow( Node source, Node target, String name ) {
        ExternalFlow externalFlow = new ExternalFlow( source, target, name );
        externalFlow.setId( newId() );
        return externalFlow;
    }

    /**
     * {@inheritDoc}
     */
    public InternalFlow createInternalFlow( Node source, Node target, String name ) {
        InternalFlow internalFlow = new InternalFlow( source, target, name );
        internalFlow.setId( newId() );
        return internalFlow;
    }

    private long newId() {
        return idCounter++;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        return (T) find( id );
    }

    /**
     * {@inheritDoc}
     */
    public void remove( ModelObject object ) {
        if ( object instanceof Scenario )
            remove( (Scenario) object );
        else
            getIdIndex().remove( object.getId() );
    }

    private void remove( Scenario scenario ) {
        if ( Channels.getPlan().getScenarios().size() > 1 ) {
            Channels.getPlan().getScenarios().remove( scenario );
            getIdIndex().remove( scenario.getId() );

            scenario.disconnect();
        }
    }


    private ModelObject find( long id ) throws NotFoundException {
        ModelObject result = getIdIndex().get( id );
        if ( result == null ) {
            Iterator<Scenario> iterator = list( Scenario.class ).iterator();
            while ( result == null && iterator.hasNext() ) {
                Scenario scenario = iterator.next();
                result = scenario.getNode( id );
                if ( result == null ) {
                    Iterator<Flow> flows = scenario.flows();
                    while ( result == null && flows.hasNext() ) {
                        Flow flow = flows.next();
                        if ( flow.getId() == id ) result = flow;
                    }
                }
            }
        }
        if ( result == null ) throw new NotFoundException();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T find( Class<T> clazz, String name ) {
        for ( T object : list( clazz ) ) {
            if ( object.getName().equals( name ) )
                return object;
        }

        return null;
    }


}
