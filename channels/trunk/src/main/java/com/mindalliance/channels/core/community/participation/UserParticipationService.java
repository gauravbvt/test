package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.db.data.users.UserRecord;

import java.util.List;

/**
 * User plan participation service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/12/12
 * Time: 2:32 PM
 */
public interface UserParticipationService extends GenericSqlService<UserParticipation, Long> {

    UserParticipation addParticipation( String username, ChannelsUser participatingUser, Agent agent, CommunityService communityService );

    UserParticipation addAcceptedParticipation( String username, ChannelsUser participatingUser, Agent agent, CommunityService communityService );

    List<UserParticipation> getUserParticipations( ChannelsUser user, CommunityService communityService );

    List<UserParticipation> getActiveUserParticipations( ChannelsUser user, CommunityService communityService );

    List<UserParticipation> getParticipationsAsAgent( Agent agent, CommunityService communityService );

    UserParticipation getParticipation( ChannelsUser user, Agent agent, CommunityService communityService );

    boolean isParticipationNotFull( Agent agent, CommunityService communityService );

    boolean isParticipatedAs( Agent agent, CommunityService communityService );

    List<Agent> listAgentsParticipatedAs( CommunityService communityService );

    void removeParticipation( String username, UserParticipation participation, CommunityService communityService );

    List<UserParticipation> getAllParticipations( CommunityService communityService );

    List<UserParticipation> getAllActiveParticipations( CommunityService communityService );

    boolean isActive( UserParticipation userParticipation, CommunityService communityService );

    /**
     * Delete all participations by a user.
     *
     * @param user     a user for which participation is to be terminated
     * @param username user deleting the participations
     */
    void deleteAllParticipations( ChannelsUser user, String username );

    List<UserParticipation> validParticipations(
            List<UserParticipation> userParticipations,
            CommunityService communityService );

    List<Agent> listAgentsUserParticipatesAs( ChannelsUser user, CommunityService communityService );

    List<UserParticipation> getParticipationsSupervisedByUser( ChannelsUser user, CommunityService communityService );

    List<Agent> listSupervisorsUserParticipatesAs(
            UserParticipation userParticipation,
            ChannelsUser user,
            CommunityService communityService );

    List<String> listSupervisorsToNotify( UserParticipation userParticipation, CommunityService communityService );

    List<UserRecord> findUsersParticipatingAs( Agent agent, CommunityService communityService );

    boolean deleteParticipation( ChannelsUser user, Agent agent, CommunityService communityService );


    void accept( UserParticipation participation, CommunityService communityService );

    void refuse( UserParticipation participation, CommunityService communityService );

    boolean isUserParticipatingAs( ChannelsUser user, Agent agent, CommunityService communityService );

    List<UserParticipation> listUserParticipationIn(
            OrganizationParticipation organizationParticipation,
            CommunityService communityService );

   List<UserParticipation> listUserParticipationsAwaitingConfirmationBy(
            ChannelsUser user,
            CommunityService communityService );

    boolean isValid( UserParticipation userParticipation, CommunityService communityService );

 }
