package com.mindalliance.channels.db.services.users;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.DuplicateKeyException;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.DataService;
import org.springframework.mail.MailSender;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/24/13
 * Time: 12:32 PM
 */
public interface UserRecordService extends DataService<UserRecord>, UserDetailsService {

    ChannelsUser createUser( String username, String name, CommunityService communityService ) throws DuplicateKeyException;

    ChannelsUser createUser( String username, String name, String email, CommunityService communityService ) throws DuplicateKeyException;

    /**
     * Create a user info.
     *
     * @param username a string
     * @param password as string (not encoded)
     * @param fullName a string
     * @param email    a string
     * @return a user info
     */
    UserRecord createUserRecord( String username,
                                 String name,
                                 String password,
                                 String fullName,
                                 String email,
                                 CommunityService communityService ) throws DuplicateKeyException;

    @Secured("ROLE_ADMIN")
    void deleteUser( String username, ChannelsUser user, CommunityService communityService );

    /**
     * Get user by email.
     *
     * @param identifier a string
     * @return a user or null
     */
    ChannelsUser getUserWithIdentity( String identifier );

    /**
     * Find user record, enabled or not, with given username.
     *
     * @param username a username
     * @return a user record or null
     */
    UserRecord getUserRecord( String username );


    /**
     * Return users sorted by username.
     *
     * @return the users
     */
    List<ChannelsUser> getAllEnabledUsers();

    /**
     * Get all user who are planners for a given plan.
     *
     * @param uri the plan uri
     * @return a list of users
     */
    List<ChannelsUser> getPlanners( String uri );

    /**
     * Get all users who are explicitly planners for a community given its uri.
     *
     * @param communityUri a community uri
     * @return a list of Channels users
     */
    List<ChannelsUser> getCommunityPlanners( String communityUri );

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
     * Get a list of all user records, disabled or not.
     *
     * @return a list of user records.
     */
    List<UserRecord> getAllUserRecords();

    /**
     * Get all users (regulars and planners) of a given plan or community.
     *
     * @param uri the plan or community uri
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
    boolean changePassword( ChannelsUser user,
                            PlanManager planManager,
                            MailSender mailSender );


    /**
     * Update a persisted user record from the values of another.
     *
     * @param userRecord       a persisted user record
     * @param update           another user info with undigested password
     * @param communityService a community service
     * @return whether there was a successful update
     */
    boolean updateUserRecord( UserRecord userRecord, UserRecord update, CommunityService communityService );

    /**
     * Whether username of a participant in given plan.
     *
     * @param username a string
     * @param planUri  a string
     * @return a boolean
     */
    Boolean isParticipant( final String username, String planUri );

    /**
     * Whether username of a participant in given plan.
     *
     * @param username a string
     * @param planUri  a string
     * @return a boolean
     */
    Boolean isPlanner( final String username, String planUri );

    /**
     * Get a user's full name.
     *
     * @param username a username
     * @return a string
     */
    String getFullName( String username );

    /**
     * Get or create a new user authorized for a plan given an email address.
     *
     * @param email            a string
     * @param communityService a community service
     * @return a clear password or null if user creation failed
     */
    UserRecord getOrMakeUserFromEmail( String email, CommunityService communityService );

    /**
     * Find all users with a given full name.
     *
     * @param name a user full name
     * @return a list of users
     */
    List<ChannelsUser> findAllUsersWithFullName( String name, String uri );

    ////

    List<Channel> findChannels( UserRecord userRecord, CommunityService communityService );

    /**
     * Change the address in an existing contact info.
     *
     * @param userRecord user record
     * @param channel    a channel
     * @param address    new address
     */
    void setAddress( UserRecord userRecord, Channel channel, String address );

    /**
     * Adding a contact info from user.
     *
     * @param username         user name of user doing the adding
     * @param userRecord       user to be added channel to
     * @param channel          a channel
     * @param communityService a community service
     */
    void addChannel( String username, UserRecord userRecord, Channel channel, CommunityService communityService );

    /**
     * Remove a contact info from user.
     *
     * @param userRecord user to be removed channel from
     * @param channel    a channel
     * @param communityService a community service
     */
    void removeChannel( UserRecord userRecord, Channel channel, CommunityService communityService );

    /**
     * Remove all user's contact info.
     *
     * @param userRecord a user record
     * @param communityService a community service
     */
    void removeAllChannels( UserRecord userRecord, CommunityService communityService );

    //////

    /**
     * Authorize a user as planner in a community.
     * Can return null if promotion is not allowed.
     *
     * @param username         who authorizes
     * @param planner          user authorized
     * @param communityService a community service
     * @return a community planner's user record
     */
    UserRecord authorizeCommunityPlanner( String username, ChannelsUser planner, CommunityService communityService );

    /**
     * Remove planner status from user in a community.
     *
     * @param username         who effected the resignation
     * @param planner          resigning user
     * @param communityService a community service
     * @return whether resignation was effective
     */
    boolean resignAsCommunityPlanner( String username, ChannelsUser planner, CommunityService communityService );

    /**
     * Add a first planner to a plan community.
     *
     * @param founder          a Channels user
     * @param planCommunity a plan community
     */
    void addFounder( ChannelsUser founder, PlanCommunity planCommunity );

}
