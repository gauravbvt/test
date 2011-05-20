package com.mindalliance.channels.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.annotation.Secured;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * A user details service that keeps in sync with changes to the underlying user definition file.
 */
public class FileUserDetailsService implements UserDetailsService, UserService {

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
    private Resource base;

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

    @Override
    @Secured( "ROLE_ADMIN" )
    public synchronized User createUser( String name ) throws DuplicateKeyException {
        if ( users.containsKey( name ) )
            throw new DuplicateKeyException();

        User user = new User( new UserInfo( name, "x,User,user@example.com" ) );
        users.put( name, user );
        return user;
    }

    @Override
    @Secured( "ROLE_ADMIN" )
    public synchronized void deleteUser( User user ) {
        users.remove( user.getUsername() );
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

    /**
     * Load user details for a given username.
     *
     * @param username the user name.
     * @return the details
     */
    @Override
    public UserDetails loadUserByUsername( String username ) {
        User user = readUsers().get( username );
        if ( user == null )
            throw new UsernameNotFoundException(
                MessageFormat.format( "Unknown username: {0}", username ) );

        return user;
    }

    private static Map<String, User> readDetails( Properties properties ) {
        Map<String, User> result = new HashMap<String, User>();
        for ( String userName : properties.stringPropertyNames() ) {
            String values = properties.getProperty( userName );
            result.put( userName, new User( new UserInfo( userName, values ) ) );
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
    @Override
    public synchronized void  save() throws IOException {
        long time = System.currentTimeMillis();
        synchronized ( this ) {
            lastModified = time;

            Properties props = new Properties();
            for ( User def : users.values() )
                props.setProperty( def.getUsername(), def.getUserInfo().toString() );

            FileOutputStream stream = new FileOutputStream( userFile );
            try {
                props.store( stream, " Copied from " + defaultDefinitions.getURI() );
            } finally {
                stream.close();
            }
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
        try {
            userFile = userDefinitions == null ? null : new File( base.getFile(), userDefinitions );
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to specify user file", e );
        }
    }

    public synchronized Resource getBase() {
        return base;
    }

    public synchronized void setBase( Resource base ) {
        this.base = base;
        try {
            File dir = base.getFile();
            dir.mkdirs();
        } catch ( IOException e ) {
            LOG.error( "Unable to create " + base, e );
        }
    }

    /**
     * Get user by username.
     *
     * @param userName a string
     * @return a user or null
     */
    @Override
    public User getUserNamed( String userName ) {
        return readUsers().get( userName );
    }

    @Override
    public List<User> getUsers() {
        List<User> result = new ArrayList<User>( readUsers().values() );
        Collections.sort( result, new Comparator<User>() {
            @Override
            public int compare( User o1, User o2 ) {
                return o1.getUsername().compareTo( o2.getUsername() );
            }
        } );
        return result;
    }

    /**
     * Get all user who are planners for a given plan.
     *
     * @param uri the plan uri
     * @return a list of users
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
    public List<User> getUsers( String uri ) {
        Collection<User> collection = readUsers().values();
        List<User> result = new ArrayList<User>( collection.size() );
        for ( User user : collection )
            if ( user.isParticipant( uri ) )
                result.add( user );
        return result;
    }

    @Override
    public boolean changePassword( User user, PlanManager planManager, MailSender mailSender ) {
        boolean success = false;
        String newPassword = makeNewPassword();
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo( user.getEmail() );
        String fromAddress =  planManager.getDefaultSupportCommunity();
        email.setFrom( fromAddress );
        email.setReplyTo( fromAddress );
        String subject = "New password";
        email.setSubject( subject );
        String text = "Dear "
                + user.getFullName()
                + " ("
                + user.getUsername()
                + "),\n\n"
                + "Your new Channels password is\n\n"
                + "--------\n"
                + newPassword
                + "\n--------"
                + "\n\nFor further assistance, please contact us at "
                + fromAddress
                + ".";
        email.setText( text );
        LOG.info( fromAddress
                + " emailing \"" + subject + "\" to "
                + user.getEmail() );
        try {
            mailSender.send( email );
            user.getUserInfo().setPassword( newPassword );
            save();
            success = true;
        } catch ( Exception exc ) {
            LOG.warn( fromAddress
                    + " failed to email server error ", exc );
        }
        return success;
    }

    private String makeNewPassword() {
        StringBuilder sb = new StringBuilder( );
        int min = 'a';
        int max = 'z';
        Random random = new Random( );
        for ( int i=0; i<8; i++ )  {
           int c = random.nextInt( max - min ) + min;
            sb.append( (char)c );
        }
        return sb.toString();
    }

}
