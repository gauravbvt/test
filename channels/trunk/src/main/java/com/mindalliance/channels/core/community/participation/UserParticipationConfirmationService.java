package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.orm.service.GenericSqlService;

import java.util.List;

/**
 * Supervised user plan participation confirmation service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/23/12
 * Time: 12:00 PM
 */
public interface UserParticipationConfirmationService extends GenericSqlService<UserParticipationConfirmation,Long> {

    List<UserParticipationConfirmation> getParticipationConfirmations( CommunityService communityService );

    List<UserParticipationConfirmation> getParticipationConfirmations( UserParticipation userParticipation );

    List<UserParticipationConfirmation> getParticipationConfirmations( Agent supervisor, CommunityService communityService );

    void addParticipationConfirmation(
            UserParticipation userParticipation,
            Agent supervisor,
            ChannelsUser user,
            CommunityService communityService
    );

    void removeParticipationConfirmation(
            UserParticipation userParticipation,
            Agent supervisor,
            CommunityService communityService );

    boolean isConfirmedBy( UserParticipation userParticipation, Agent supervisor );

    void deleteConfirmations( UserParticipation participation, CommunityService communityService );

    boolean isConfirmedByAllSupervisors( UserParticipation userParticipation, CommunityService communityService );

    boolean isConfirmationByUserRequired( UserParticipation userParticipation, ChannelsUser user, CommunityService communityService );

    List<UserParticipationConfirmation> listUserParticipationsConfirmedBy(
            ChannelsUser user,
            final CommunityService communityService );

    boolean isValid( UserParticipationConfirmation confirmation, CommunityService communityService );
}

