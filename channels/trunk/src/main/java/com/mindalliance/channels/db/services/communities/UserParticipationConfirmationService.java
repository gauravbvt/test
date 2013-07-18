package com.mindalliance.channels.db.services.communities;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.communities.UserParticipationConfirmation;
import com.mindalliance.channels.db.services.DataService;

import java.util.List;

/**
 * Supervised user plan participation confirmation service.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/20/13
 * Time: 12:05 PM
 */
public interface UserParticipationConfirmationService  extends DataService<UserParticipationConfirmation> {

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

    Boolean isConfirmedBy( UserParticipation userParticipation, Agent supervisor );

    void deleteConfirmations( UserParticipation participation, CommunityService communityService );

    Boolean isConfirmedByAllSupervisors( UserParticipation userParticipation, CommunityService communityService );

    Boolean isConfirmationByUserRequired( UserParticipation userParticipation, ChannelsUser user, CommunityService communityService );

    List<UserParticipationConfirmation> listUserParticipationsConfirmedBy(
            ChannelsUser user,
            final CommunityService communityService );

    Boolean isValid( UserParticipationConfirmation confirmation, CommunityService communityService );

}
