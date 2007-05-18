// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.acegisecurity.annotation.Secured;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.ProviderManager;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.dao.DaoAuthenticationProvider;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.user.Alert;
import com.mindalliance.channels.data.user.Certification;
import com.mindalliance.channels.data.user.Conversation;
import com.mindalliance.channels.data.user.Todo;
import com.mindalliance.channels.data.user.UserImpl;
import com.mindalliance.channels.data.user.UserRequest;
import com.mindalliance.channels.data.user.UserTypes;
import com.mindalliance.channels.services.RegistryService;

import static com.mindalliance.channels.data.user.UserTypes.AdminType;
import static com.mindalliance.channels.data.user.UserTypes.UserType;

/**
 * All user related data; their profiles and alerts/todos targeted at them.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Registry extends AbstractQueryable implements RegistryService {

    /**
     * Users, indexed by username.
     */
    private Map<String,User> usernames = new TreeMap<String,User>();
    private Map<User,UserTypes> userRights = new TreeMap<User,UserTypes>();
    private List<Conversation> conversations = new ArrayList<Conversation>();
    private List<Alert> alerts = new ArrayList<Alert>();
    private List<Todo> todos = new ArrayList<Todo>();
    private List<UserRequest> userRequests = new ArrayList<UserRequest>();
    private List<Certification> certifications =
                                        new ArrayList<Certification>();

    /**
     * Listen and approve username changes to ensure uniqueness.
     */
    private VetoableChangeListener usernameListener =
        new VetoableChangeListener() {

            public void vetoableChange( PropertyChangeEvent evt )
                throws PropertyVetoException {

                if ( usernames.containsKey( evt.getNewValue() ) )
                    throw new PropertyVetoException( MessageFormat.format(
                            "User named {0} already exists",
                            evt.getNewValue() ),
                            evt );
                else {
                    // Reindex
                    User user = usernames.get( evt.getOldValue() );
                    usernames.remove( evt.getOldValue() );
                    usernames.put( (String) evt.getNewValue(), user );
                }
            }
        };

    /**
     * Default constructor.
     */
    public Registry() {
    }

    /**
     * Default constructor.
     * @param system the system
     */
    public Registry( System system ) {
        super( system );
        usernames = new HashMap<String, User>();
        userRights = new HashMap<User, UserTypes>();
        conversations = new ArrayList<Conversation>();
        alerts = new ArrayList<Alert>();
        todos = new ArrayList<Todo>();
        userRequests = new ArrayList<UserRequest>();
        certifications = new ArrayList<Certification>();
    }

    /**
     * Set the value of administrators. If using this, make sure it is
     * called <i>after</i> a call to setUser()...
     *
     * @param administrators The new value of administrators
     * @throws UserExistsException if new usernames are not distinct.
     */
    @Secured( "ROLE_RUN_AS_SYSTEM" )
    public void setAdministrators( Set<User> administrators )
        throws UserExistsException {

        if ( administrators == null )
            throw new NullPointerException();

        // Make sure all admins are also userRight
        for ( User user : administrators ) {
            addAdministrator( user );
        }
    }

    /**
     * Give administrative rights to a user. Implies an addUser().
     *
     * @param user the user to promote.
     * @throws UserExistsException if a user by the same username
     *             already exists
     */
    @Secured( { "ROLE_ADMIN" } )
    public void addAdministrator( User user ) throws UserExistsException {
        addUser( user, AdminType );
    }

    /**
     * Revoke administrative rights from a user.
     *
     * @param user the user to demote.
     */
    @Secured( { "ROLE_ADMIN" } )
    public void removeAdministrator( User user ) {
        this.userRights.put( user, UserType );
    }

    /**
     * Check if given user is an administrator of the system.
     *
     * @param user the given user
     * @return true if an administrator.
     */
    @Secured( { "ROLE_USER" } )
    public boolean isAdministrator( User user ) {
        return AdminType.equals( this.userRights.get( user ) );
    }

    /**
     * Return the value of userRight.
     */
    @Secured( { "ROLE_USER" } )
    public Set<User> getUsers() {
        return Collections.unmodifiableSortedSet(
            new TreeSet<User>(
                this.userRights.keySet() ) );
    }

    /**
     * Set the value of users. This call resets existing users (and
     * administrators).
     *
     * @param users The new users.
     * @throws UserExistsException if usernames in list are not
     *             distinct
     */
    @Secured( "ROLE_RUN_AS_SYSTEM" )
    public void setUsers( Set<User> users ) throws UserExistsException {
        if ( users == null )
            throw new NullPointerException();

        // TODO Fix resetting the admins?

        for ( User user : this.usernames.values() )
            ( (JavaBean) user ).removeVetoableChangeListener( "username",
                    this.usernameListener );

        this.userRights.clear();
        this.usernames.clear();
        for ( User user : users )
            addUser( user );
    }

    /**
     * Add a user to the system. Note: once added, userRight cannot be
     * removed, just disabled.
     *
     * @param user the new user.
     * @throws UserExistsException on duplicate username
     */
    @Secured( { "ROLE_USER" } )
    public void addUser( User user ) throws UserExistsException {
        addUser( user, UserType );
    }

    /**
     * Add a user of the given type.
     *
     * @param user the user
     * @param type the type
     * @throws UserExistsException if a user by that name already exists
     */
    private void addUser( User user, UserTypes type )
        throws UserExistsException {

        if ( this.usernames.containsKey( user.getUsername() ) )
            throw new UserExistsException( MessageFormat.format(
                    "User {0} already exists", user.getUsername() ) );

        this.usernames.put( user.getUsername(), user );
        this.userRights.put( user, type );
        ( (JavaBean) user ).addVetoableChangeListener( "username",
                this.usernameListener );
    }

    /**
     * Test if a given user is managed by the system.
     *
     * @param user the given user
     * @return true if the user is known to the system.
     */
    @Secured( { "ROLE_USER" } )
    public boolean isUserRegistered( User user ) {
        return this.userRights.containsKey( user );
    }

    /**
     * Get the Acegi user details given a short user name.
     *
     * @param username the user name
     */
    public UserDetails loadUserByUsername( String username ) {

        User user = this.usernames.get( username );
        if ( user == null )
            throw new UsernameNotFoundException( MessageFormat.format(
                    "User {0} not found", username ) );
        return user;
    }

    /**
     * Return the value of administrators.
     */
    public Set<User> getAdministrators() {
        SortedSet<User> result = new TreeSet<User>();
        for ( Entry<User, UserTypes> entry : this.userRights.entrySet() ) {
            if ( entry.getValue().equals( AdminType ) )
                result.add( entry.getKey() );
        }

        return Collections.unmodifiableSortedSet( result );
    }

    /**
     * Return whether the user name is already taken.
     * @param username the username
     */
    public boolean isUserNameTaken( String username ) {
        return usernames.containsKey( username );
    }

    /**
     * Logs a user in.
     * @param user the user
     * @param password the password
     */
    @Secured( "ROLE_RUN_AS_SYSTEM" )
    public void login( String user, String password ) {

        DaoAuthenticationProvider ap = new DaoAuthenticationProvider();
        ap.setUserDetailsService( this );

        ProviderManager providerManager = new ProviderManager();
        providerManager.setProviders(
            Arrays.asList( new AuthenticationProvider[] { ap } ) );

        // Create and store the Acegi SecureContext into the
        // ContextHolder.
        SecurityContextImpl secureContext = new SecurityContextImpl();
        secureContext.setAuthentication(
            providerManager.doAuthentication(
                new UsernamePasswordAuthenticationToken( user, password ) ) );
        SecurityContextHolder.setContext( secureContext );
    }

    /**
     * Logs current user out.
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Give administrative rights to a user. Implies an addUser().
     *
     * @param user the user to promote.
     * @throws UserExistsException if a user with given username
     *             already exists
     */
    @Secured( { "ROLE_ADMIN" } )
    public void makeAdministrator( User user ) throws UserExistsException {
        addAdministrator( user );
    }

    /**
     * Register a new administrator.
     *
     * @param name the user's full name
     * @param username the user's short name
     * @param password the password
     * @return the new user
     * @exception UserExistsException when username is already taken
     */
    public User registerAdministrator(
        String name, String username, String password )
        throws UserExistsException {

        UserImpl user = new UserImpl(
                name, username, password,
                new String[] { "ROLE_USER", "ROLE_ADMIN" } );
        addAdministrator( user );
        return user;
    }

    /**
     * Register a new user.
     *
     * @param name the user's full name
     * @param username the user's short name
     * @param password the password
     * @return the new user
     * @exception UserExistsException when username is already taken
     */
    public User registerUser( String name, String username, String password )
        throws UserExistsException {

        UserImpl user = new UserImpl( name, username, password,
                new String[] { "ROLE_USER" } );
        addUser( user );
        return user;
    }

    /**
     * Return the value of alerts.
     */
    public List<Alert> getAlerts() {
        return this.alerts;
    }

    /**
     * Return the list of alerts pertaining to a given user.
     * @param user the user
     */
    public List<Alert> getAlerts( User user ) {
        // TODO filter alerts for given user
        return getAlerts();
    }

    /**
     * Set the value of alerts.
     * @param alerts The new value of alerts
     */
    public void setAlerts( List<Alert> alerts ) {
        this.alerts = alerts;
    }

    /**
     * Add an alert.
     * @param alert the alert
     */
    public void addAlert( Alert alert ) {
        this.alerts.add( alert );
    }

    /**
     * Remove an alert.
     * @param alert the alert
     */
    public void removeAlert( Alert alert ) {
        this.alerts.remove( alert );
    }

    /**
     * Return the value of certifications.
     */
    public List<Certification> getCertifications() {
        return this.certifications;
    }

    /**
     * Set the value of certifications.
     * @param certifications The new value of certifications
     */
    public void setCertifications( List<Certification> certifications ) {
        this.certifications = certifications;
    }

    /**
     * Add a certification.
     * @param certification the certification
     */
    public void addCertification( Certification certification ) {
        this.certifications.add( certification );
    }

    /**
     * Remove a certification.
     * @param certification the certification
     */
    public void removeCertification( Certification certification ) {
        this.certifications.remove( certification );
    }

    /**
     * Return the value of conversations.
     */
    public List<Conversation> getConversations() {
        return this.conversations;
    }

    /**
     * Set the value of conversations.
     * @param conversations The new value of conversations
     */
    public void setConversations( List<Conversation> conversations ) {
        this.conversations = conversations;
    }

    /**
     * Return the value of todos.
     */
    public List<Todo> getTodos() {
        return this.todos;
    }

    /**
     * Set the value of todos.
     * @param todos The new value of todos
     */
    public void setTodos( List<Todo> todos ) {
        this.todos = todos;
    }

    /**
     * Return the value of userRequests.
     */
    public List<UserRequest> getUserRequests() {
        return this.userRequests;
    }

    /**
     * Set the value of userRequests.
     * @param userRequests The new value of userRequests
     */
    public void setUserRequests( List<UserRequest> userRequests ) {
        this.userRequests = userRequests;
    }
}
