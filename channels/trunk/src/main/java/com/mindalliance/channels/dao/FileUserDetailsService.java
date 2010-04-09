package com.mindalliance.channels.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * A user details service that keeps in sync with changes to the underlying user definition file.
 */
public class FileUserDetailsService implements UserDetailsService {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( FileUserDetailsService.class );

    /**
     * Users, indexed by username.
     */
    private Map<String, User> users;

    /**
     * The actual user definitions (file name).
     */
    private String userDefinitions;

    /**
     * Base for relative user definitions.
     */
    private String base = System.getProperty( "user.home" );

    /**
     * The file for the user definitions.
     */
    private File userFile;

    /**
     * Initial data for user definitions, when initializing system.
     */
    private Resource defaultDefinitions;

    /**
     * Last modification date for the user definitions. Used to monitor external changes to
     * the users file.
     */
    private long lastModified;


    //---------------------------------------------
    public FileUserDetailsService() {
    }

    private synchronized Map<String, User> readUsers() {
        try {
            if ( userFile == null || !userFile.exists() || userFile.lastModified() > lastModified )
                load();

            return users;

        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get user details", e );
        }
    }

    /**
     * Load user details for a given username.
     *
     * @param username the user name.
     * @return the details
     */
    public UserDetails loadUserByUsername( String username ) {
        User user = readUsers().get( username );
        if ( user == null )
            throw new UsernameNotFoundException(
                MessageFormat.format( "Unknown username: {0}", username ) );

        return user;
    }

    private void load() throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = findInputStream();

            Properties properties = new Properties();
            properties.load( inputStream );
            users = readDetails( properties );

            // Save default definitions to current definitions, if need be
            if ( userFile != null && !userFile.exists() )
                save();

            lastModified = userFile == null ? System.currentTimeMillis() : userFile.lastModified();

        } finally {
            if ( inputStream != null )
                inputStream.close();
        }
    }

    private static Map<String, User> readDetails( Properties properties ) {
        Map<String, User> result = new HashMap<String, User>();
        for ( String username : properties.stringPropertyNames() ) {
            String values = properties.getProperty( username );
            result.put( username, new User( new UserInfo( username, values ) ) );
        }
        return result;
    }

    private InputStream findInputStream() throws IOException {
        InputStream inputStream;
        if ( userFile != null && userFile.exists() ) {
            LOG.debug( "Reading user definitions from {}", userFile.getAbsolutePath() );
            inputStream = new FileInputStream( userFile );

        } else if ( defaultDefinitions != null && defaultDefinitions.exists() ) {
            LOG.debug( "Reading default user definitions from {}", defaultDefinitions.getURI() );
            inputStream = defaultDefinitions.getInputStream();

        } else
            throw new IllegalStateException( "No user readable user definitions" );

        return inputStream;
    }

    /**
     * Write the user definition in memory to the disk storage.
     *
     * @throws IOException on write errors
     */
    public synchronized void save() throws IOException {
        Properties props = new Properties();
        for ( User d : users.values() )
            props.setProperty( d.getUsername(), d.getUserInfo().toString() );

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream( userFile );
            props.store( stream, " Copied from " + defaultDefinitions.getURI() );
        } finally {
            if ( stream != null )
                stream.close();
        }
        LOG.debug( "Wrote user definitions to {}", userFile.getAbsolutePath() );
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
     *
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

    /**
     * Get user by username.
     *
     * @param userName a string
     * @return a user or null
     */
    public User getUserNamed( String userName ) {
        return readUsers().get( userName );
    }

    public List<User> getUsers() {
        return new ArrayList<User>( readUsers().values() );
    }

    /**
     * Get all user who are planners for a given plan.
     *
     * @param uri the plan uri
     * @return a list of users
     */
    public List<User> getPlanners( String uri ) {
        Collection<User> userList = readUsers().values();
        List<User> result = new ArrayList<User>( userList.size() );

        for ( User user : userList )
            if ( user.isPlanner( uri ) )
                result.add( user );

        return result;
    }

    /**
     * Get all usernames for a given plan.
     *
     * @param uri the plan uri
     * @return a list of strings
     */
    public List<String> getUsernames( String uri ) {
        Collection<User> userList = readUsers().values();
        List<String> result = new ArrayList<String>( userList.size() );
        for ( User user : userList )
            if ( user.isParticipant( uri ) )
                result.add( user.getUsername() );

        return result;
    }

    /**
     * Get a sorted list of all user names.
     * @return a list
     */
    public List<String> getUsernames() {
        List<String> result = new ArrayList<String>( readUsers().keySet() );
        Collections.sort( result );
        return result;
    }

    /**
     * Get all users (regulars and planners) of a given plan.
     * @param uri the plan's uri
     * @return the list
     */
    public List<User> getUsers( String uri ) {
        Collection<User> collection = readUsers().values();
        List<User> result = new ArrayList<User>( collection.size() );
        for ( User user : collection )
            if ( user.isParticipant( uri ) )
                result.add( user );
        return result;
    }
}
