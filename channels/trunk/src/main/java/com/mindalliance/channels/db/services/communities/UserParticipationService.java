package com.mindalliance.channels.db.services.communities;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.DataService;

import java.util.List;

/**
 * User community participation service.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 3:12 PM
 */
public interface UserParticipationService extends DataService<UserParticipation> {

    UserParticipation addParticipation( String username, ChannelsUser participatingUser, Agent agent, CommunityService communityService );

    UserParticipation addAcceptedParticipation( String username, ChannelsUser participatingUser, Agent agent, CommunityService communityService );

    List<UserParticipation> getUserParticipations( ChannelsUser user, CommunityService communityService );

    List<UserParticipation> getActiveUserParticipations( ChannelsUser user, CommunityService communityService );

    List<UserParticipation> getParticipationsAsAgent( Agent agent, CommunityService communityService );

    UserParticipation getParticipation( ChannelsUser user, Agent agent, CommunityService communityService );

    Boolean isParticipationNotFull( Agent agent, CommunityService communityService );

    Boolean isParticipatedAs( Agent agent, CommunityService communityService );

    List<Agent> listAgentsParticipatedAs( CommunityService communityService );

    void removeParticipation( String username, UserParticipation participation, CommunityService communityService );

    List<UserParticipation> getAllParticipations( CommunityService communityService );

    List<UserParticipation> getAllActiveParticipations( CommunityService communityService );

    Boolean isActive( UserParticipation userParticipation, CommunityService communityService );

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

    Boolean deleteParticipation( ChannelsUser user, Agent agent, CommunityService communityService );


    void accept( UserParticipation participation, CommunityService communityService );

    void refuse( UserParticipation participation, CommunityService communityService );

    // accepted and confirmed (i.e. active)
    Boolean isUserParticipatingAs( ChannelsUser user, Agent agent, CommunityService communityService );

    List<UserParticipation> listUserParticipationIn(
            OrganizationParticipation organizationParticipation,
            CommunityService communityService );

    List<UserParticipation> listUserParticipationsAwaitingConfirmationBy(
            ChannelsUser user,
            CommunityService communityService );

    Boolean isValid( UserParticipation userParticipation, CommunityService communityService );

}
