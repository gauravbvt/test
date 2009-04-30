package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.export.Exporter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An in-memory, no-transactions implementation of a store.
 */
public final class Memory implements Dao {

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
    private Set<Scenario> scenarios = new HashSet<Scenario>();

    /**
     * ModelObjects, indexed by id.
     */
    private Map<Long, ModelObject> idIndex = new HashMap<Long, ModelObject>( INITIAL_CAPACITY );

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
    @SuppressWarnings( {"unchecked"} )
    public <T extends ModelObject> List<T> list( final Class<T> clazz ) {
        return (List<T>) CollectionUtils.select( idIndex.values(), new Predicate() {
            public boolean evaluate( Object object ) {
                return clazz.isAssignableFrom( object.getClass() );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    public void add( ModelObject object ) {
        if ( idIndex.containsKey( object.getId() ) )
            throw new DuplicateKeyException();

        object.setId( newId() );
        idIndex.put( object.getId(), object );

        if ( object instanceof Scenario )
            scenarios.add( (Scenario) object );

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
        return (long) scenarios.size();
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
            idIndex.remove( object.getId() );
    }

    private void remove( Scenario scenario ) {
        if ( scenarios.size() > 1 ) {
            scenarios.remove( scenario );
            idIndex.remove( scenario.getId() );

            scenario.disconnect();
        }
    }


    private ModelObject find( long id ) throws NotFoundException {
        ModelObject result = idIndex.get( id );
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
        try {
            Map<Long, Long> idMap = new HashMap<Long, Long>();
            Importer importer = Channels.instance().getImporter();
            File dataFile = getDataFile();
            if ( dataFile.length() > 0 ) {
                LOG.info( "Importing app snapshot" );
                idMap = importer.importAll( new FileInputStream( dataFile ) );
            }
            // Load and run journaled commands
            Journal journal = loadJournal();
            Commander commander = Channels.instance().getCommander();
            commander.setReplaying( true );
            commander.setIdMap( idMap );
            if ( !journal.isEmpty() ) {
                LOG.info( "Replaying journaled commands" );
                for ( Command command : journal.getCommands() ) {
                    commander.doCommand( command );
                }
            }
            journal.reset();
            commander.reset();
            LOG.info( "Persisted app reloaded." );
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
        try {
            takeSnapshot();
            getJournalFile().delete();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onDestroy() {
        afterInitialize();
    }

    private File getDataFile() throws IOException {
        File dataDirectory = getDataDirectory();
        File dataFile = new File( dataDirectory.getPath() + File.separator + DATA_FILE );
        if ( !dataFile.exists() ) {
            dataFile.createNewFile();
        }
        return dataFile;
    }

    private File getDataDirectory() {
        File directory;
        if ( dataDirectoryPath != null ) {
            directory = new File( dataDirectoryPath );
            if ( !directory.exists() ) {
                directory.mkdir();
            }
        } else {
            throw new RuntimeException( "Data directory path is not set." );
        }
        return directory;
    }

    private File getJournalFile() throws IOException {
        File dataDirectory = getDataDirectory();
        File journalFile = new File( dataDirectory.getPath() + File.separator + JOURNAL_FILE );
        if ( !journalFile.exists() ) {
            journalFile.createNewFile();
        }
        return journalFile;
    }

    private Journal loadJournal() {
        Journal journal;
        try {
            File journalFile = getJournalFile();
            Importer importer = Channels.instance().getImporter();
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
        if ( command.isMemorable() ) {
            // Update snapshot if required
            if ( journal.size() >= snapshotThreshold ) {
                try {
                    takeSnapshot();
                    getJournalFile().delete();
                    journal.reset();
                } catch ( IOException e ) {
                    throw new RuntimeException( "Failed to take snapshot", e );

                }
            }
            // else save journaled commands
            else {
                journal.addCommand( command );
                try {
                    saveJournal();
                } catch ( IOException e ) {
                    throw new RuntimeException( "Failed to save journal", e );
                }
            }
        }
    }

    private void takeSnapshot() throws IOException {
        LOG.info( "Taking app snapshot" );
        // Make backup
        File dataFile = getDataFile();
        if ( dataFile.length() > 0 ) {
            String backupPath = dataFile.getAbsolutePath() + "_" + System.currentTimeMillis();
            File backup = new File( backupPath );
            dataFile.renameTo( backup );
        }
        // snap
        dataFile = getDataFile();
        assert dataFile.length() == 0;
        Exporter exporter = Channels.instance().getExporter();
        exporter.exportAll( new FileOutputStream( dataFile ) );
    }

    private void saveJournal() throws IOException {
        File journalFile = getJournalFile();
        journalFile.delete();
        journalFile = getJournalFile();
        assert journalFile.length() == 0;
        Exporter exporter = Channels.instance().getExporter();
        exporter.exportJournal( journal, new FileOutputStream( journalFile ) );
    }

    /**
     * For testing.
     */
    public void reset() {
        try {
            File journalFile = getJournalFile();
            journalFile.delete();
            File dataFile = getDataFile();
            dataFile.delete();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    // Not used yet
    public void stop() {
        try {
            takeSnapshot();
            getJournalFile().delete();
            LOG.info("Pre-shutdown snapshot taken.");
        } catch ( IOException e ) {
            LOG.warn( "Failed to take snapshot", e );
        }
    }

}
