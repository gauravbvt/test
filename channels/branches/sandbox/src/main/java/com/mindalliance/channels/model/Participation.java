package com.mindalliance.channels.model;

import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.query.DefaultQueryService;
import com.mindalliance.channels.query.QueryService;

/**
 * A participation represents a Channels user who is also an actor.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2010
 * Time: 2:36:56 PM
 */
public class Participation extends AbstractUnicastChannelable {

    /**
     * Unknown participation.
     * Not meaningful but needed for consistency with other entities.
     */
    public static Participation UNKNOWN;

    /**
     * Name of unknown participation.
     */
    public static String UnknownName = "(unknown)";

    /**
     * Name of the user, if any, represented by this actor.
     */
    private String username;
    /**
     * The actual actor, possibly an archetype, played by the user.
     */
    private Actor actor;

    public Participation( String username ) {
        this.username = username;
    }

    public Participation() {
    }

    /**
     * {@inheritDoc}
     * Uses username as name.
     */
    public String getName() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     * Sets username.
     */
    public void setName( String name ) {
        username = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    /**
     * {@inheritDoc}
     */
    public boolean references( final ModelObject mo ) {
        return super.references( mo )
                || ModelObject.areIdentical( actor, mo );
    }

    /**
     * Get user's full name.
     *
     * @param queryService a query service
     * @return a string
     */
    public String getUserFullName( QueryService queryService ) {
        return queryService.findUserFullName( username );
    }

    /**
     * Get user's email.
     *
     * @param queryService a query service
     * @return a string
     */
    public String getUserEmail( QueryService queryService ) {
        return queryService.findUserEmail( username );
    }

    /**
     * Whether this participation's username corresponds to a registered user.
     *
     * @param defaultQueryService a query service
     * @return a boolean
     */
    public boolean hasUser( DefaultQueryService defaultQueryService ) {
        return defaultQueryService.findUserRole( username ) != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndefined() {
        return username == null;
    }

}
