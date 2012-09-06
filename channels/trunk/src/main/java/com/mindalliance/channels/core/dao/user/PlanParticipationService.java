package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.core.query.QueryService;

import java.util.List;

/**
 * Plan participation service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/12/12
 * Time: 2:32 PM
 */
public interface PlanParticipationService extends GenericSqlService<PlanParticipation,Long> {

    PlanParticipation addParticipation( String username, Plan plan, ChannelsUser participatingUser, Actor actor );

    List<PlanParticipation> getParticipations( Plan plan, ChannelsUserInfo userInfo, QueryService queryService );

    List<PlanParticipation> getParticipations( Plan plan, Actor actor, QueryService queryService );

    PlanParticipation getParticipation( Plan plan, ChannelsUserInfo userInfo, Actor actor, QueryService queryService );

    List<Actor> listUserDesignatedActors( QueryService queryService );

    boolean canBeDesignated( Plan plan, Actor actor );
    
    boolean isDesignated( Plan plan, Actor actor );

    void removeParticipation( String username, Plan plan, PlanParticipation participation );

    List<PlanParticipation> getAllParticipations( Plan plan, QueryService queryService );

    boolean references( Plan plan, ModelObject mo, QueryService queryService );

    /**
     * Get the actors a user could participate and is not already.
     *
     * @param user a user
     * @param queryService a query service
     * @return a list of actors
     */
    List<Actor> findOpenActors( final ChannelsUser user, final QueryService queryService );

    boolean isParticipationAvailable( Actor actor, ChannelsUser user, QueryService queryService );

    /**
     * Delete all participations by a user.
     * @param userInfo a user info for which participation is to terminated
     * @param username user deleting the participations
     */
    void deleteAllParticipations( ChannelsUserInfo userInfo, String username );
}
