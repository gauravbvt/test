package com.mindalliance.channels.util;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.wicket.util.file.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataRetrievalFailureException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * A user details service that keeps in sync with changes to the underlying user definition file.
 */
public class FileUserDetailsService implements UserDetailsService {

    /** The plan manager. */
    private final PlanManager planManager;

    /** Users, indexed by username. */
    private Map<String,User> details;

    /** The actual user definitions (file name). */
    private String userDefinitions;

    /** Base for relative user definitions. */
    private String base = System.getProperty( "user.home" );

    /** The file for the user definitions. */
    private File userFile;

    /** Initial data for user definitions, when initializing system. */
    private Resource defaultDefinitions;

    /**
     * Last modification date for the user definitions. Used to monitor external changes to
     * the users file.
     */
    private long lastModified;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    //---------------------------------------------
    public FileUserDetailsService( PlanManager planManager ) {
        this.planManager = planManager;
    }

    /**
     * Load user details for a given username.
     * @param username the user name.
     * @return the details
     */
    public synchronized UserDetails loadUserByUsername( String username ) {
        try {
            if ( userFile == null || !userFile.exists()
                 || userFile.lastModified() > lastModified )
                readUserDefinitions();

        } catch ( IOException e ) {
            String msg = "Unable to get modification date";
            logger.error( msg, e );
            throw new DataRetrievalFailureException( msg, e );
        }
        User result = details.get( username );
        if ( result == null )
            throw new UsernameNotFoundException( MessageFormat.format(
                    "Unknown username: {0}", username ) );
        return result;
    }

    private synchronized void readUserDefinitions() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = findInputStream();
            properties.load( inputStream );
            details = new HashMap<String, User>();
            for ( String username : properties.stringPropertyNames() ) {
                String values = properties.getProperty( username );
                StringTokenizer tokens = new StringTokenizer( values, "," );
                User d = new User( username );
                d.setPassword( tokens.nextToken() );

                String planId = tokens.nextToken();

                while ( tokens.hasMoreTokens() )
                    d.addRole( tokens.nextToken() );
                details.put( username, d );
                d.setPlan( getPlan( planId, username ) );
            }
        } finally {
            if ( inputStream != null )
                inputStream.close();
        }

        if ( userFile != null && !userFile.exists() )
            writeUserDefinitions();

        lastModified = userFile == null ? System.currentTimeMillis() : userFile.lastModified();
    }

    private Plan getPlan( String planId, String username ) {
        try {
            return planManager.get( Long.parseLong( planId ) );
        } catch ( NotFoundException ignored ) {
            logger.warn( "Using default plan for user " + username );
            return planManager.getCurrentPlan();
        }
    }

    private InputStream findInputStream() throws IOException {
        InputStream inputStream;
        if ( userFile != null && userFile.exists() ) {
            logger.debug( "Reading user definitions from {}", userFile.getAbsolutePath() );
            inputStream = new FileInputStream( userFile );

        } else if ( defaultDefinitions != null && defaultDefinitions.exists() ) {
            logger.debug( "Reading default user definitions from {}", defaultDefinitions.getURI() );
            inputStream = defaultDefinitions.getInputStream();
        } else {
            logger.warn( "No user readable user definitions" );
            inputStream = new ByteArrayInputStream( new byte[0] );
        }
        return inputStream;
    }

    /**
     * Write the user definition in memory to the disk storage.
     * @throws IOException on write errors
     */
    public synchronized void writeUserDefinitions() throws IOException {
        Properties props = new Properties();
        for ( User d : details.values() )
            props.setProperty( d.getUsername(), d.propertyString() );

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream( userFile );
            props.store( stream, " Copied from " + defaultDefinitions.getURI() );
        } finally {
            if ( stream != null )
                stream.close();
        }
        logger.debug( "Wrote user definitions to {}", userFile.getAbsolutePath() );
    }

    public synchronized Resource getDefaultDefinitions() {
        return defaultDefinitions;
    }

    public synchronized void setDefaultDefinitions( Resource defaultDefinitions ) {
        this.defaultDefinitions = defaultDefinitions;
    }

    public synchronized String getUserDefinitions() {
        return userDefinitions;
    }

    /**
     * Set the user definitions location.
     * @param userDefinitions a file path
     */
    public synchronized void setUserDefinitions( String userDefinitions ) {
        this.userDefinitions = userDefinitions;
        userFile = userDefinitions == null ? null : new File( base, userDefinitions );
    }

    public synchronized String getBase() {
        return base;
    }

    public synchronized void setBase( String base ) {
        this.base = base;
    }

    public PlanManager getPlanManager() {
        return planManager;
    }
}
