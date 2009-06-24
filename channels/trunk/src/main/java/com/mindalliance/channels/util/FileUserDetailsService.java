package com.mindalliance.channels.util;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
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
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * A user details service that keeps in sync with changes to the underlying user definition file.
 */
public class FileUserDetailsService implements UserDetailsService {

    /** All the user details. */
    private Map<String,Details> details;

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

    public FileUserDetailsService() {
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
        UserDetails result = details.get( username );
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
            details = new HashMap<String, Details>();
            for ( String username : properties.stringPropertyNames() ) {
                String values = properties.getProperty( username );
                StringTokenizer tokens = new StringTokenizer( values, "," );
                Details d = new Details( username );
                d.setPassword( tokens.nextToken() );
                d.setDefaultPlan( Long.parseLong( tokens.nextToken() ) );
                while ( tokens.hasMoreTokens() )
                    d.addRole( tokens.nextToken() );
                details.put( username, d );
            }
        } finally {
            if ( inputStream != null )
                inputStream.close();
        }

        if ( userFile != null && !userFile.exists() )
            writeUserDefinitions();

        lastModified = userFile == null ? System.currentTimeMillis() : userFile.lastModified();
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
        for ( Details d : details.values() )
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

    //==============================================================
    /**
     * The authentication details of a user.
     * @TODO See if this can be moved into User class.
     */
    public static class Details implements UserDetails {

        /** The admin role string. */
        private static final String ROLE_ADMIN = "ROLE_ADMIN";

        /** The planner role string. */
        private static final String ROLE_PLANNER = "ROLE_PLANNER";

        /** The user role string. Implied if user is listed in user list. */
        private static final String ROLE_USER = "ROLE_USER";

        /** The username, presumably unique. */
        private String username;

        /** The password hash */
        private String password;

        /** True if the user is an administrator. */
        private boolean admin;

        /** True if the user is a planner. False if a regular user. */
        private boolean planner;

        /** The selected plan for this user. */
        private long defaultPlan;

        private Details( String username ) {
            this.username = username;
        }

        /**
         * Returns the authorities granted to the user. Cannot return <code>null</code>.
         * @return the authorities (never <code>null</code>)
         */
        public GrantedAuthority[] getAuthorities() {
            List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
            if ( isAdmin() )
                result.add( new GrantedAuthorityImpl( ROLE_ADMIN ) );
            if ( isPlanner() )
                result.add( new GrantedAuthorityImpl( ROLE_PLANNER ) );

            result.add( new GrantedAuthorityImpl( ROLE_USER ) );

            return result.toArray( new GrantedAuthority[ result.size() ] );
        }

        public boolean isEnabled() {
            return true;
        }

        public String getPassword() {
            return password;
        }

        private void setPassword( String password ) {
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        /**
         * Indicates whether the user's account has expired.
         * An expired account cannot be authenticated.
         *
         * @return <code>true</code> if the user's account is valid (ie non-expired),
         * <code>false</code> if no longer valid (ie expired)
         */
        public boolean isAccountNonExpired() {
            return true;
        }

        /**
         * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
         *
         * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
         */
        public boolean isAccountNonLocked() {
            return true;
        }

        /**
         * Indicates whether the user's credentials (password) has expired.
         * Expired credentials prevent authentication.
         *
         * @return <code>true</code> if the user's credentials are valid (ie non-expired),
         * <code>false</code> if no longer valid (ie expired)
         */
        public boolean isCredentialsNonExpired() {
            return true;
        }

        public boolean isAdmin() {
            return admin;
        }

        private void setAdmin( boolean admin ) {
            this.admin = admin;
        }

        public boolean isPlanner() {
            return planner;
        }

        private void setPlanner( boolean planner ) {
            this.planner = planner;
        }

        public long getDefaultPlan() {
            return defaultPlan;
        }

        private void setDefaultPlan( long defaultPlan ) {
            this.defaultPlan = defaultPlan;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;
            if ( obj == null || getClass() != obj.getClass() )
                return false;
            Details details = (Details) obj;
            return username.equals( details.getUsername() );
        }

        @Override
        public int hashCode() {
            return username.hashCode();
        }

        private void addRole( String roleString ) {
            if ( ROLE_ADMIN.equals( roleString ) )
                setAdmin( true );
            else
                setPlanner( ROLE_PLANNER.equals( roleString ) );
        }

        /**
         * Create string value for property file.
         * @return a new string
         */
        private String propertyString() {
            StringWriter buffer = new StringWriter();
            buffer.write( getPassword() );
            buffer.write( "," );
            buffer.write( Long.toString( getDefaultPlan() ) );

            for ( GrantedAuthority a : getAuthorities() ) {
                buffer.write( "," );
                buffer.write( a.getAuthority() );
            }

            return buffer.toString();
        }
    }

}
