// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Plan.Status;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.StringTokenizer;

/**
 * A plan definition with all its versions.
 */
public class PlanDefinition extends Observable {

    /**
     * Name of persisted data file.
     */
    public static final String DATA_FILE = "data.xml";

    /**
     * Name of file containing last id used in plan or and in journal, if any.
     */
    public static final String LAST_ID_FILE = "lastid";

    /**
     * Name of command journal file.
     */
    public static final String JOURNAL_FILE = "journal.xml";

    /** The logger... */
    private static final Logger LOG = LoggerFactory.getLogger( PlanDefinition.class );

    /** The plan's uri. */
    private final String uri;

    /** The version in production (or null if none). */
    private Version productionVersion;

    /** The version in development. */
    private Version developmentVersion;

    /** The plan's name. */
    private String name;

    /** The plan's client. */
    private String client;

    /** The actual plan versions, indexed by version number. */
    private final Map<Integer, Version> versions =
            Collections.synchronizedMap( new HashMap<Integer, Version>() );

    /** The directory where plan versions are saved. */
    private File planDirectory;

    /** High water mark for plan version numbers. Computed from directory. */
    private int maxVersion;

    //---------------------------
    public PlanDefinition( String uri, String values ) {
        this( uri );

        StringTokenizer tokens = new StringTokenizer( values, "|" );
        name = tokens.nextToken();
        if ( tokens.hasMoreTokens() )
            client = tokens.nextToken();
    }

    public PlanDefinition( String uri ) {
        this.uri = uri;
    }

    /**
     * Initialize versions by reading from the given data directory.
     * @param dataDirectory the data directory
     * @throws IOException on initialization errors
     */
    public void initialize( Resource dataDirectory ) throws IOException {
        planDirectory = new File( dataDirectory.getFile(), ChannelsUtils.sanitize( uri ) );
        if ( planDirectory.mkdirs() )
            LOG.debug( "Created {}", planDirectory );

        versions.clear();
        maxVersion = 0;

        File[] subDirs = planDirectory.listFiles( new FileFilter() {
            @Override
            public boolean accept( File pathname ) {
                return pathname.isDirectory();
            }
        } );
        if ( subDirs == null || subDirs.length == 0 )
            versions.put( ++maxVersion, new Version( 1 ) );
        else
            for ( File dir : subDirs ) {
                try {
                    int version = Integer.parseInt( dir.getName() );
                    maxVersion = Math.max( maxVersion, version );
                    versions.put( version, new Version( version, dir ) );
                } catch ( NumberFormatException ignored ) {
                    LOG.warn( "Invalid directory under {}: {}", planDirectory, dir );
                }
            }

        developmentVersion = get( maxVersion );
        developmentVersion.setStatus( Status.DEVELOPMENT );
        productionVersion = get( previousVersion( maxVersion ) );
        if ( productionVersion != null )
            productionVersion.setStatus( Status.PRODUCTION );
    }

    private int previousVersion( int version ) {
        for ( int v = version - 1 ; v > 0 ; v-- )
            if ( versions.containsKey( v ) )
                return v;

        return 0;
    }

    /**
     * Add a new development version and put current development one (if it exists) into
     * production.
     * @return the new development version
     * @throws IOException on errors while copying data
     */
    public Version productize() throws IOException {
        if ( productionVersion != null )
            productionVersion.setStatus( Status.RETIRED );

        productionVersion = developmentVersion;

        maxVersion += 1;
        Version result = new Version( maxVersion );
        versions.put( maxVersion, result );

        if ( productionVersion != null ) {
            productionVersion.setStatus( Status.PRODUCTION );

            File oldVersionDir = productionVersion.getVersionDirectory();
            // Copy files from old to new
            File data = new File( oldVersionDir, DATA_FILE );
            if ( data.exists() )
                FileUtils.copyFileToDirectory( data, result.getVersionDirectory() );

            File surveys = new File( oldVersionDir, "surveys" );
            if ( surveys.exists() )
                FileUtils.copyFileToDirectory( surveys, result.getVersionDirectory() );

            File uploads = new File( oldVersionDir, "uploads" );
            if ( uploads.exists() )
                FileUtils.copyDirectoryToDirectory( uploads, result.getVersionDirectory() );
        }

        developmentVersion = result;
        developmentVersion.setStatus( Status.DEVELOPMENT );

        notifyObservers();
        return result;
    }

    /**
     * Get plan version for given version number.
     * @param version the number
     * @return the plan version information or null if no such version exists
     */
    public Version get( int version ) {
        return versions.get( version );
    }

    public String getUri() {
        return uri;
    }

    public File getPlanDirectory() {
        return planDirectory;
    }

    public String getClient() {
        return client;
    }

    /**
     * Set the client description.
     * @param client the new description
     */
    public void setClient( String client ) {
        if ( this.client != client && ( this.client == null || !this.client.equals( client ) ) )
            setChanged();

        this.client = client;
        notifyObservers();
    }

    public String getName() {
        return name;
    }

    /**
     * Set the plan name.
     * @param name the new name
     */
    public void setName( String name ) {
        if ( this.name != name && ( name == null || !name.equals( this.name ) ) )
            setChanged();

        this.name = name;
        notifyObservers();
    }

    public Version getDevelopmentVersion() {
        return developmentVersion;
    }

    public Version getProductionVersion() {
        return productionVersion;
    }

    /**
     * Provide a string representation to use in the plan property file.
     * @return the string representation.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if ( name != null )
            buf.append( name );
        buf.append( '|' );
        if ( client != null )
            buf.append( client );
        return buf.toString();
    }

    /**
     * Delete the physical files for this definition.
     */
    public void delete() {
        try {
            FileUtils.deleteDirectory( planDirectory );
            LOG.debug( "Deleted data for plan {}", uri );
        } catch ( IOException e ) {
            LOG.error( "Unable to delete directory " + planDirectory, e );
        }
    }

    //===========================================================
    /**
     * Storage for a specific plan version.
     */
    public class Version {

        /** The plan version number. */
        private final int number;

        /** The directory where it is stored. */
        private final File versionDirectory;

        /**
         * The plan status, duplicated from Plan.getStatus() to allow queries without actually
         * loading the plan.
         */
        private Status status = Status.RETIRED;

        //-----------------------------
        /**
         * Create a new uninitialized version.
         * @param number the version number
         * @param versionDirectory the version directory storage
         */
        public Version( int number, File versionDirectory ) {
            this.number = number;
            this.versionDirectory = versionDirectory;
            if ( versionDirectory.mkdirs() )
                LOG.debug( "Created {}", versionDirectory );
        }

        public Version( int number ) {
            this( number, new File( planDirectory, Integer.toString( number ) ) );
        }

        public File getVersionDirectory() {
            return versionDirectory;
        }

        public int getNumber() {
            return number;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus( Status status ) {
            this.status = status;
        }

        public boolean isDevelopment() {
            return Status.DEVELOPMENT == status;
        }

        /**
         * Returns a string representation of the object.
         * @return a string representation of the object.
         */
        @Override
        public String toString() {
            return MessageFormat.format( "{0} v.{1} ({2})", getName(), number, status );
        }

        public PlanDefinition getPlanDefinition() {
            return PlanDefinition.this;
        }

        /**
         * Get the location of the wrapped plan's xml file.
         *
         * @return a file
         * @throws IOException on errors
         */
        public File getDataFile() throws IOException {
            return new File( versionDirectory, DATA_FILE );
        }

        /**
         * Get the location of the journal file for the wrapped plan.
         *
         * @return a file
         * @throws IOException on errors
         */
        public File getJournalFile() throws IOException {
            File journalFile = new File( versionDirectory, JOURNAL_FILE );
            if ( journalFile.createNewFile() )
                LOG.debug( "Created {}", journalFile );
            return journalFile;
        }

        /**
         * Get the location of the last id save file.
         * @return a file
         */
        private File getLastIdFile() {
            return new File( versionDirectory, LAST_ID_FILE );
        }

        /**
         * Get the last id save in the file store.
         * @return the last id
         * @throws IOException if unable to read the file
         */
        public synchronized long getLastId() throws IOException {
            Long lastId = IdGenerator.MUTABLE_LOW;
            File lastIdFile = getLastIdFile();
            if ( lastIdFile.exists() ) {
                BufferedReader reader = new BufferedReader( new FileReader( lastIdFile ) );
                try {
                    String lastIdString = reader.readLine();
                    if ( lastIdString != null )
                        lastId = Long.parseLong( lastIdString );
                } finally {
                    reader.close();
                }
            }
            return lastId;
        }

        /**
         * Modify the last id stored in the file store.
         * @param id the new id
         * @throws IOException on save errors
         */
        public synchronized void setLastId( long id ) throws IOException {
            File idFile = getLastIdFile();
            idFile.delete();
            LOG.debug( "Creating plan lastID file at " + idFile.getAbsolutePath() );
            PrintWriter out = new PrintWriter( new FileOutputStream( idFile ) );
            try {
                out.print( id );
            } finally {
                out.close();
            }
        }

        /**
         * Whether this version has some data persisted.
         * @return a boolean
         */
        public boolean isPersisted() {
            return new File( versionDirectory, DATA_FILE ).exists();
        }

        /**
         * Create an unloaded plan from this version.
         * @return a new uninitialized plan
         * @param idGenerator the id generator
         */
        Plan createPlan( IdGenerator idGenerator ) {
            Plan plan = new Plan();

            plan.setId( idGenerator.assignId( null, uri ) );
            plan.setName( name );
            plan.setUri( uri );
            plan.setVersion( number );
            plan.setStatus( status );
            plan.setClient( client );

            return plan;
        }
    }
}
