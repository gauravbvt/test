// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import java.util.Set;

import org.acegisecurity.annotation.Secured;

/**
 * Convenience wrapper for system-level objects.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @composed "" - "*" User
 * @composed "" - "*" Project
 */
public interface System {

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
     * Get the projects defined in this system.
     */
    Set<Project> getProjects();

    /**
     * Get the projects for which a given is participating.
     * @param user the given user
     * @return the appropriate projects
     */
    Set<Project> getProjects( User user );

    /**
     * Find a project of given name.
     * @param name the given name
     * @return null if not found.
     */
    Project getProject( String name );

    /**
     * Add a new project.
     * @param project the new project.
     */
    @Secured( "ROLE_ADMIN" )
    void addProject( Project project );

    /**
     * Remove a project.
     * @param project the project to get rid of.
     */
    @Secured( "ROLE_ADMIN" )
    void removeProject( Project project );
}
