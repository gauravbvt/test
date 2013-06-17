package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.dao.IdGenerator;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.StringTokenizer;

/**
 * A community's definition.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/4/13
 * Time: 1:53 PM
 */
public class CommunityDefinition extends Observable {

    // TODO - extract abstract superclass common to PlanDefinition and PlanDefinition.Version.

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

    /**
     * The logger...
     */
    private static final Logger LOG = LoggerFactory.getLogger( CommunityDefinition.class );

    /**
     * The community's uri.
     */
    private final String uri;

    /**
     * The plan's uri
     */
    private String planUri;
    /**
     * The plan's version
     */
    private int planVersion;

    /**
     * The directory where the community is saved.
     */
    private File communityDirectory;


    public CommunityDefinition( String uri, String values ) {
        this.uri = uri;
        StringTokenizer tokens = new StringTokenizer( values, "|" );
        planUri = tokens.nextToken();
        planVersion = Integer.parseInt( tokens.nextToken() );
    }

    public CommunityDefinition( String uri, String planUri, int planVersion ) {
        this.uri = uri;
        this.planUri = planUri;
        this.planVersion = planVersion;
        setChanged();
    }

    /**
     * Initialize versions by reading from the given data directory.
     *
     * @param dataDirectory the data directory
     * @throws java.io.IOException on initialization errors
     */
    public void initialize( Resource dataDirectory ) throws IOException {
        communityDirectory = new File( dataDirectory.getFile(), ChannelsUtils.sanitize( uri ) );
        if ( communityDirectory.mkdirs() )
            LOG.debug( "Created {}", communityDirectory );
    }

    public String getUri() {
        return uri;
    }

    public File getCommunityDirectory() {
        return communityDirectory;
    }

    public String getPlanUri() {
        return planUri;
    }

    public void setPlanUri( String planUri ) {
        if ( planUri != null) {
           if ( !this.planUri.equals( planUri ) )
               setChanged();
            this.planUri = planUri;
            notifyObservers();
        }
    }

    public int getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion( int planVersion ) {
        if ( this.planVersion != planVersion )
            setChanged();
        this.planVersion = planVersion;
        notifyObservers();
    }


    /**
     * Provide a string representation to use in the community property file.
     * @return the string representation.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append( planUri );
        buf.append( '|' );
        buf.append( planVersion );
        return buf.toString();
    }

    /**
     * Delete the physical files for this definition.
     */
    public void delete() {
        try {
            FileUtils.deleteDirectory( communityDirectory );
            LOG.debug( "Deleted data for community {}", uri );
        } catch ( IOException e ) {
            LOG.error( "Unable to delete directory " + communityDirectory, e );
        }
    }

    /**
     * Get the location of the wrapped plan's xml file.
     *
     * @return a file
     * @throws IOException on errors
     */
    public File getDataFile() throws IOException {
        return new File( communityDirectory, DATA_FILE );
    }

    /**
     * Get the location of the journal file for the wrapped plan.
     *
     * @return a file
     * @throws IOException on errors
     */
    public File getJournalFile() throws IOException {
        File journalFile = new File( communityDirectory, JOURNAL_FILE );
        if ( journalFile.createNewFile() )
            LOG.debug( "Created {}", journalFile );
        return journalFile;
    }

    /**
     * Get the location of the last id save file.
     * @return a file
     */
    private File getLastIdFile() {
        return new File( LAST_ID_FILE );
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
        LOG.debug( "Creating community lastID file at " + idFile.getAbsolutePath() );
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
        return new File( DATA_FILE ).exists();
    }

    /**
     * Create an unloaded plan community.
     * @return a new uninitialized plan
     */
    PlanCommunity createPlanCommunity( ) {
        PlanCommunity planCommunity = new PlanCommunity();
        planCommunity.setName( "Unnamed" );
        planCommunity.setUri( uri );
        planCommunity.setPlanUri( planUri );
        planCommunity.setPlanVersion( planVersion );
        setChanged();
        return planCommunity;
    }
}

