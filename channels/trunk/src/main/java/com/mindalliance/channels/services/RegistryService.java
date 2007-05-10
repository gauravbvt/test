/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.services;

import java.util.List;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.acegisecurity.annotation.Secured;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;

import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.Element;

public interface RegistryService extends Service {

    /**
     * Return the users of this system.
     */
    Set<User> getUsers();

    /**
     * Add a user to the system.
     * Note: once added, users cannot be removed, just disabled.
     * @param user the new user.
     * @throws UserExistsException on duplicate usernames
     */
    @Secured( "ROLE_ADMIN" )
    void addUser( User user ) throws UserExistsException;

    /**
     * Test if a given user is managed by the system.
     * @param user the given user
     * @return true if the user is known to the system.
     */
    boolean isUser( User user );

    /**
     * Return the administrators of the system.
     */
    Set<User> getAdministrators();

    /**
     * Give administrative rights to a user. Implies an addUser().
     * @param user the user to promote.
     * @throws UserExistsException if a user with given username already
     * exists
     */
    @Secured( "ROLE_ADMIN" )
    void addAdministrator( User user ) throws UserExistsException;

    /**
     * Check if given user is an administrator of the system.
     * @param user the given user
     * @return true if an administrator.
     */
    boolean isAdministrator( User user );

    /**
     * Revoke administrative rights from a user.
     * @param user the user to demote.
     */
    @Secured( "ROLE_ADMIN" )
    void removeAdministrator( User user );

    /**
     * Find all users who have authority over an element
     */
	List<User> getAuthoritativeUsers(Element element);

	
	/**
	 * Get authenticated user in current thread or null if none.
	 * @return
	 */
	User getAuthenticatedUser();

}
