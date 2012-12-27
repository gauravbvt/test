package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
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

    List<UserParticipationConfirmation> getParticipationConfirmations( PlanCommunity planCommunity );

    List<UserParticipationConfirmation> getParticipationConfirmations( UserParticipation userParticipation );

    List<UserParticipationConfirmation> getParticipationConfirmations( Agent supervisor, PlanCommunity planCommunity );

    void addParticipationConfirmation(
            UserParticipation userParticipation,
            Agent supervisor,
            ChannelsUser user
    );

    void removeParticipationConfirmation(
            UserParticipation userParticipation,
            Agent supervisor );

    boolean isConfirmedBy( UserParticipation userParticipation, Agent supervisor );

    void deleteConfirmations( UserParticipation participation );

    boolean isConfirmedByAllSupervisors( UserParticipation userParticipation, PlanCommunity planCommunity );

    boolean isConfirmationByUserRequired( UserParticipation userParticipation, ChannelsUser user, PlanCommunity planCommunity );

    List<UserParticipationConfirmation> listUserParticipationsConfirmedBy(
            ChannelsUser user,
            final PlanCommunity planCommunity );
}
