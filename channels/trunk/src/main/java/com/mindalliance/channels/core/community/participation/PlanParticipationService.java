package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.GenericSqlService;

import java.util.List;

/**
 * Plan participation service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/12/12
 * Time: 2:32 PM
 */
public interface PlanParticipationService extends GenericSqlService<PlanParticipation, Long> {

    PlanParticipation addParticipation( String username, Plan plan, ChannelsUser participatingUser, Actor actor );

    PlanParticipation addAcceptedParticipation( String username, Plan plan, ChannelsUser participatingUser, Actor actor );

    List<PlanParticipation> getUserParticipations( ChannelsUserInfo userInfo, PlanCommunity planCommunity );

    List<PlanParticipation> getActiveUserParticipations( ChannelsUserInfo userInfo, PlanCommunity planCommunity );

    List<PlanParticipation> getParticipationsAsActor( Actor actor, PlanCommunity planCommunity );

    PlanParticipation getParticipation( ChannelsUserInfo userInfo, Actor actor, PlanCommunity planCommunity );

    List<Actor> listUserDesignatedActors( PlanCommunity planCommunity );

    boolean canBeDesignated( Plan plan, Actor actor );

    boolean isDesignated( Plan plan, Actor actor );

    void removeParticipation( String username, Plan plan, PlanParticipation participation );

    List<PlanParticipation> getAllParticipations( PlanCommunity planCommunity );

    List<PlanParticipation> getAllActiveParticipations( PlanCommunity planCommunity );

    boolean references( ModelObject mo, PlanCommunity planCommunity );

    boolean isActive( PlanParticipation planParticipation, PlanCommunity planCommunity );

    /**
     * Get the actors a user could participate and is not already.
     *
     * @param user         a user
     * @param planCommunity a plan service
     * @return a list of actors
     */
    List<Actor> findOpenActors( ChannelsUser user, final PlanCommunity planCommunity );

    boolean isParticipationOpenAndAvailable( Actor actor, ChannelsUser user, PlanCommunity planCommunity );

    /**
     * Delete all participations by a user.
     *
     * @param userInfo a user info for which participation is to terminated
     * @param username user deleting the participations
     */
    void deleteAllParticipations( ChannelsUserInfo userInfo, String username );

    List<PlanParticipation> validParticipations(
            List<PlanParticipation> planParticipations,
            PlanCommunity planCommunity );

    boolean isValidatedByAllSupervisors( PlanParticipation planParticipation, PlanCommunity planCommunity );

    List<Actor> listActorsUserParticipatesAs( ChannelsUser user, PlanCommunity planCommunity );

    List<PlanParticipation> getParticipationsSupervisedByUser( ChannelsUser user, PlanCommunity planCommunity );

    List<Actor> listSupervisorsUserParticipatesAs(
            PlanParticipation planParticipation,
            ChannelsUser user,
            PlanCommunity planCommunity );

    List<String> listSupervisorsToNotify( PlanParticipation planParticipation, PlanCommunity planCommunity );

    List<ChannelsUserInfo> findUsersParticipatingAs( Actor actor, PlanCommunity planCommunity );

    void deleteParticipation( ChannelsUserInfo participant, Actor actor, PlanCommunity planCommunity );


    void accept( PlanParticipation participation );

    void refuse( PlanParticipation participation );

}
