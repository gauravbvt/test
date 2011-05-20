// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.dao;

import org.springframework.mail.MailSender;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

/**
 * ...
 */
public interface UserService {

    @Secured( "ROLE_ADMIN" )
    User createUser( String name ) throws DuplicateKeyException;

    @Secured( "ROLE_ADMIN" )
    void deleteUser( User user );

    /**
     * Load user details for a given username.
     *
     * @param username the user name.
     * @return the details
     */
    UserDetails loadUserByUsername( String username );

    /**
     * Write the user definition in memory to the disk storage.
     *
     * @throws java.io.IOException on write errors
     */
    void save() throws IOException;

    /**
     * Get user by username.
     *
     * @param userName a string
     * @return a user or null
     */
    User getUserNamed( String userName );

    /**
     * Return users sorted by username.
     *
     * @return the users
     */
    List<User> getUsers();

    /**
     * Get all user who are planners for a given plan.
     *
     * @param uri the plan uri
     * @return a list of users
     */
    List<User> getPlanners( String uri );

    /**
     * Get all usernames for a given plan.
     *
     * @param uri the plan uri
     * @return a list of strings
     */
    List<String> getUsernames( String uri );

    /**
     * Get a sorted list of all user names.
     *
     * @return a list
     */
    List<String> getUsernames();

    /**
     * Get all users (regulars and planners) of a given plan.
     *
     * @param uri the plan's uri
     * @return the list
     */
    List<User> getUsers( String uri );

    /**
     * Change the user's password and email a notice.
     *
     * @param user       a user
     * @param mailSender a mail sender service
     * @return a boolean indicating success
     */
    boolean changePassword( User user, PlanManager planManager, MailSender mailSender );
}
