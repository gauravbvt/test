package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.dao.DuplicateKeyException;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import org.springframework.mail.MailSender;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/7/12
 * Time: 1:55 PM
 */
public interface ChannelsUserDao extends GenericSqlService<ChannelsUserInfo, Long>, UserDetailsService {

    @Secured( "ROLE_ADMIN" )
    ChannelsUser createUser( String username ) throws DuplicateKeyException;

    @Secured( "ROLE_ADMIN" )
    ChannelsUser createUser( String username, String email ) throws DuplicateKeyException;

    @Secured( "ROLE_ADMIN" )
    void deleteUser( ChannelsUser user );

    /**
     * Get user by email.
     *
     * @param identifier a string
     * @return a user or null
     */
    ChannelsUser getUserNamed( String identifier );

    /**
     * Return users sorted by username.
     *
     * @return the users
     */
    List<ChannelsUser> getUsers();

    /**
     * Get all user who are planners for a given plan.
     *
     * @param uri the plan uri
     * @return a list of users
     */
    List<ChannelsUser> getPlanners( String uri );

    /**
     * Get all user names (email addresses) for a given plan.
     *
     * @param uri the plan uri
     * @return a list of strings
     */
    List<String> getUsernames( String uri );

    /**
     * Get a sorted list of all user names (email addresses).
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
    List<ChannelsUser> getUsers( String uri );

    /**
     * Change the user's password and email a notice.
     *
     * @param user        a user
     * @param planManager a plan manager
     * @param mailSender  a mail sender service
     * @return a boolean indicating success
     */
    boolean changePassword( ChannelsUser user, PlanManager planManager, MailSender mailSender );

    /**
     * Create a user info.
     *
     * @param username a string
     * @param password as string (not encoded)
     * @param fullName a string
     * @param email    a string
     * @return a user info
     */
    ChannelsUserInfo createUserInfo( String username, String password, String fullName, String email );

    /**
     * Update a persisted user info from the values of another.
     *
     * @param userInfo a persisted user info
     * @param update   another user info with undigested password
     */
    void updateIdentity( ChannelsUserInfo userInfo, ChannelsUserInfo update );

    /**
     * Whether username of a participant in given plan.
     *
     * @param username a string
     * @param planUri  a string
     * @return a boolean
     */
    boolean isParticipant( final String username, String planUri );

    /**
     * Whether username of a participant in given plan.
     *
     * @param username a string
     * @param planUri  a string
     * @return a boolean
     */
    boolean isPlanner( final String username, String planUri );

    /**
     * Get a user's full name.
     *
     * @param username a username
     * @return a string
     */
    String getFullName( String username );

    /**
     * Create a new user given an email address.
     * @param email a string
     * @return a clear password or null if user creation failed
     */
    String makeNewUserFromEmail( String email );
}
