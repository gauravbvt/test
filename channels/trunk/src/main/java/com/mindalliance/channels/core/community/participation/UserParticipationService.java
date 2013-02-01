package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.orm.service.GenericSqlService;

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

    UserParticipation addParticipation( String username, ChannelsUser participatingUser, Agent agent, PlanCommunity planCommunity );

    UserParticipation addAcceptedParticipation( String username, ChannelsUser participatingUser, Agent agent, PlanCommunity planCommunity );

    List<UserParticipation> getUserParticipations( ChannelsUser user, PlanCommunity planCommunity );

    List<UserParticipation> getActiveUserParticipations( ChannelsUser user, PlanCommunity planCommunity );

    List<UserParticipation> getParticipationsAsAgent( Agent agent, PlanCommunity planCommunity );

    UserParticipation getParticipation( ChannelsUser user, Agent agent, PlanCommunity planCommunity );

    boolean isParticipationNotFull( Agent agent, PlanCommunity planCommunity );

    boolean isParticipatedAs( Agent agent, PlanCommunity planCommunity );

    List<Agent> listAgentsParticipatedAs( PlanCommunity planCommunity );

    void removeParticipation( String username, UserParticipation participation, PlanCommunity planCommunity );

    List<UserParticipation> getAllParticipations( PlanCommunity planCommunity );

    List<UserParticipation> getAllActiveParticipations( PlanCommunity planCommunity );

    boolean isActive( UserParticipation userParticipation, PlanCommunity planCommunity );

    /**
     * Delete all participations by a user.
     *
     * @param user     a user for which participation is to be terminated
     * @param username user deleting the participations
     */
    void deleteAllParticipations( ChannelsUser user, String username );

    List<UserParticipation> validParticipations(
            List<UserParticipation> userParticipations,
            PlanCommunity planCommunity );

    List<Agent> listAgentsUserParticipatesAs( ChannelsUser user, PlanCommunity planCommunity );

    List<UserParticipation> getParticipationsSupervisedByUser( ChannelsUser user, PlanCommunity planCommunity );

    List<Agent> listSupervisorsUserParticipatesAs(
            UserParticipation userParticipation,
            ChannelsUser user,
            PlanCommunity planCommunity );

    List<String> listSupervisorsToNotify( UserParticipation userParticipation, PlanCommunity planCommunity );

    List<ChannelsUserInfo> findUsersParticipatingAs( Agent agent, PlanCommunity planCommunity );

    boolean deleteParticipation( ChannelsUser user, Agent agent, PlanCommunity planCommunity );


    void accept( UserParticipation participation, PlanCommunity planCommunity );

    void refuse( UserParticipation participation, PlanCommunity planCommunity );

    boolean isUserParticipatingAs( ChannelsUser user, Agent agent, PlanCommunity planCommunity );

    List<UserParticipation> listUserParticipationIn(
            OrganizationParticipation organizationParticipation,
            PlanCommunity planCommunity );

   List<UserParticipation> listUserParticipationsAwaitingConfirmationBy(
            ChannelsUser user,
            PlanCommunity planCommunity);

    boolean isValid( UserParticipation userParticipation, PlanCommunity planCommunity );
}
