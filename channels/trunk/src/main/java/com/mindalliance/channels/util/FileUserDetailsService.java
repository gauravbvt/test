package com.mindalliance.channels.util;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    /**
     * The plan manager.
     */
    private final PlanManager planManager;

    /**
     * Users, indexed by username.
     */
    private Map<String, User> details = new HashMap<String, User>();

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

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    //---------------------------------------------
    public FileUserDetailsService( PlanManager planManager ) {
        this.planManager = planManager;
    }

    /**
     * Load user details for a given username.
     *
     * @param username the user name.
     * @return the details
     */
    public synchronized UserDetails loadUserByUsername( String username ) {
        try {
            if ( userFile == null || !userFile.exists() || userFile.lastModified() > lastModified )
                readUserDefinitions();

            User user = details.get( username );
            if ( user == null )
                throw new UsernameNotFoundException(
                    MessageFormat.format( "Unknown username: {0}", username ) );

            return user;

        } catch ( IOException e ) {
            String msg = "Unable to get modification date";
            logger.error( msg, e );
            throw new RuntimeException( msg, e );
        }
    }

    private synchronized void readUserDefinitions() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = findInputStream();
            properties.load( inputStream );
            details = readDetails( properties );
        } finally {
            if ( inputStream != null )
                inputStream.close();
        }
        if ( userFile != null && !userFile.exists() )
            writeUserDefinitions();

        lastModified = userFile == null ? System.currentTimeMillis() : userFile.lastModified();
    }

    private Map<String, User> readDetails( Properties properties ) {
        Map<String, User> result = new HashMap<String, User>();
        for ( String username : properties.stringPropertyNames() ) {
            String values = properties.getProperty( username );
            StringTokenizer tokens = new StringTokenizer( values, "," );
            User user = new User( username );
            user.setPassword( tokens.nextToken() );
            user.setFullName( tokens.nextToken() );
            user.setEmail( tokens.nextToken() );
            while ( tokens.hasMoreTokens() ) {
                String token = tokens.nextToken();
                if ( token.startsWith( "[" ) )
                    parsePlanAccess( token, user );
                else {
                    // admin role or default role for all accessible plans
                    if ( token.equals( User.ROLE_ADMIN ) ) {
                        for ( Plan plan : getPlanManager().getPlans() ) {
                            user.setPlanAccess( plan.getUri(), true );
                            planManager.addUser( user );
                        }
                    }
                    user.addRole( token );
                }
            }
            user.setPlan( getDefaultPlan( user ) );
            result.put( username, user );
        }
        return result;
    }

    // e.g. [mindalliance.com/channels/plans/sci|ROLE_PLANNER]
    // e.g. [mindalliance.com/channels/plans/sci]
    private void parsePlanAccess( String string, User user ) {
        assert string.endsWith( "]" );
        String planAccess = string.substring( 1, string.length() - 1 );
        StringTokenizer tokens = new StringTokenizer( planAccess, "|" );
        String uri = tokens.nextToken();
        boolean planner = false;
        if ( tokens.hasMoreTokens() ) {
            planner = tokens.nextToken().equals( User.ROLE_PLANNER );
        }
        user.setPlanAccess( uri, planner );
        planManager.addUser( user );
    }

    /**
     * Get a plan the uer can edit, else one the user can read, else the default plan.
     * @param user a user
     * @return a plan, or null if none
     */
    public Plan getDefaultPlan( User user ) {
        List<Plan> plans = findPlannablePlans( user );
        if ( plans.isEmpty() ) {
            plans = findReadablePlans( user );
            if ( plans.isEmpty() ) {
                logger.warn( "No default plan for user " + user.getUsername() );
                return null;
            }
        }

        return plans.get( 0 );
    }

    /**
     * Find all development plans a user can edit.
     * @param user  a user
     * @return  a list of versioned plans
     */
    @SuppressWarnings( "unchecked" )
    public List<Plan> findPlannablePlans( final User user ) {
        List<Plan> plans = planManager.getPlans();
        return (List<Plan>)CollectionUtils.select(
                plans,
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Plan plan = (Plan) object;
                        return plan.isDevelopment() && user.isPlanner( plan );
                    }
                }
        );
    }

    /**
     * Find all production plans a user can read.
     * @param user  a user
     * @return  a list of versioned plans
     */
    @SuppressWarnings( "unchecked" )
    public List<Plan> findReadablePlans( final User user ) {
        List<Plan> plans = planManager.getPlans();
        return (List<Plan>)CollectionUtils.select(
                plans,
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Plan plan = (Plan)object;
                        return plan.isProduction() && user.isParticipant( plan );
                    }
                }
        );
    }

/*
    private Plan getPlan( String planId, String username ) {
        try {
            return planManager.get( Long.parseLong( planId ) );
        } catch ( NotFoundException ignored ) {
            logger.warn( "Using default plan for user " + username );
            return planManager.getCurrentPlan();
        }
    }
*/

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
     *
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
     * Return the service's planManager.
     * @return the planManager
     */
    public PlanManager getPlanManager() {
        return planManager;
    }

    /**
     * Get user by username.
     *
     * @param userName a string
     * @return a user or null
     */
    public User getUserNamed( String userName ) {
        return details.get( userName );
    }

    public List<User> getAllUsers() {
        return new ArrayList<User>( details.values() );
    }

    /**
     * Get all user who are planners for the current plan.
     *
     * @return a list of users
     */
    @SuppressWarnings( "unchecked" )
    public List<User> getAllPlanners() {
        return (List<User>) CollectionUtils.select(
                getAllUsers(),
                PredicateUtils.invokerPredicate( "isPlanner" )
        );
    }

    /**
     * Get all usernames for current plan.
     *
     * @return a list of strings
     */
    @SuppressWarnings( "unchecked" )
    public List<String> getAllPlanUsernames() {
        return (List<String>) CollectionUtils.select(
                new ArrayList<String>( details.keySet() ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        User user = getUserNamed( (String) object );
                        return user.isParticipant( User.current().getPlan() );
                    }
                }
        );
    }
}
