/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.services;

import java.util.List;
import java.util.Set;

import org.acegisecurity.annotation.Secured;
import org.acegisecurity.userdetails.UserDetailsService;

import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.user.UserImpl;

public interface RegistryService extends Service, UserDetailsService {

    /**
     * Logs a user in.
     * 
     * @param user
     * @param password
     */
    @Secured( "ROLE_RUN_AS_SYSTEM")
    void login( String user, String password );

    /**
     * Logs current user out.
     */
    void logout();

    /**
     * Return the users of this system.
     */
    Set<User> getUsers();

    /**
     * Test if a given user is managed by the system.
     * 
     * @param username
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
    @Secured( "ROLE_ADMIN")
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
    @Secured( "ROLE_ADMIN")
    void removeAdministrator( User user );

    /**
     * Find all users who have authority over an element
     */
    List<User> getAuthoritativeUsers( Element element );

    /**
     * Register a new user
     * 
     * @param name
     * @param username
     * @param password
     * @return a user
     */
    User registerAdministrator( String name, String username, String password )
            throws UserExistsException;

    /**
     * Register a new user as administrator
     * 
     * @param name
     * @param username
     * @param password
     * @return a user
     */
    User registerUser( String name, String username, String password )
            throws UserExistsException;

    /**
     * Returns whether a user is registered
     * 
     * @param user
     * @return
     */
    boolean isUserRegistered( User user );

}
