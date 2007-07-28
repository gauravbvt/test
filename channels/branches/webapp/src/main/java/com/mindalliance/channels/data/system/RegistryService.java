// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import java.util.List;
import java.util.Set;

import org.acegisecurity.annotation.Secured;
import org.acegisecurity.userdetails.UserDetailsService;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.profiles.Person;
import com.mindalliance.channels.data.support.Service;

/**
 * The registry interface.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface RegistryService
    extends Service, UserDetailsService, JavaBean {

    /**
     * Logs a user in.
     *
     * @param user the user
     * @param password the password
     */
    @Secured( "ROLE_RUN_AS_SYSTEM" )
    void login( String user, String password );

    /**
     * Logs current user out.
     */
    void logout();

    /**
     * Returns the authenticated user (or null).
     */
    User getAuthenticatedUser();

    /**
     * Return the users of this system.
     */
    Set<User> getUsers();

    /**
     * Test if a given user is managed by the system.
     *
     * @param username the username
     * @return true if the user is known to the system.
     */
    boolean isUserNameTaken( String username );

    /**
     * Return the administrators of the system.
     */
    Set<User> getAdministrators();

    /**
     * Give administrative rights to a user. Implies an addUser().
     *
     * @param user the user to promote.
     * @throws UserExistsException if a user with given username
     *             already exists
     */
    @Secured( "ROLE_ADMIN" )
    void makeAdministrator( User user ) throws UserExistsException;

    /**
     * Check if given user is an administrator of the system.
     *
     * @param user the given user
     * @return true if an administrator.
     */
    boolean isAdministrator( User user );

    /**
     * Revoke administrative rights from a user.
     *
     * @param user the user to demote.
     */
    @Secured( "ROLE_ADMIN" )
    void removeAdministrator( User user );

    /**
     * Register a new administrator.
     *
     * @param name the user's full name
     * @param username the user's short name
     * @param password the password
     * @return the new user
     * @exception UserExistsException when username is already taken
     */
    User registerAdministrator( String name, String username, String password )
        throws UserExistsException;

    /**
     * Register a new administrator.
     *
     * @param name the user's full name
     * @param username the user's short name
     * @param password the password
     * @return the new user
     * @exception UserExistsException when username is already taken
     */
    User registerUser( String name, String username, String password )
        throws UserExistsException;

    /**
     * Returns whether a user is registered.
     * @param user the user
     */
    boolean isUserRegistered( User user );

    /**
     * Return the list of alerts pertaining to a given user.
     * @param user the user
     */
    List<Alert> getAlerts( User user );

    /**
     * Return the persons.
     */
    Set<Person> getPersons();

    /**
     * Set the persons.
     * @param persons the persons
     */
    void setPersons( Set<Person> persons );

    /**
     * Add a person.
     * @param person the person
     */
    void addPerson( Person person );

    /**
     * Remove a person.
     * @param person the person
     */
    void removePerson( Person person );
}
