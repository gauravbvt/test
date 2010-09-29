// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

/**
 * Something that can be turned into a resource spec.
 * @see ResourceSpec
 */
public interface Specable {

    /**
     * Get the implied actor.
     * @return the actor, or null if any
     */
    Actor getActor();

    /**
     * Get the implied role.
     * @return the role, or null if any
     */
    Role getRole();

    /**
     * Get the implied organization.
     * @return the organization, or null if any
     */
    Organization getOrganization();

    /**
     * Get the implied jurisdiction.
     * @return the jurisdiction, or null if any
     */
    Place getJurisdiction();
}
