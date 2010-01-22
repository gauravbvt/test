// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import org.springframework.security.annotation.Secured;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * A user profile.
 *
 * Profile properties will be used in faceted searches.
 */
@Entity
public class Profile extends ModelObject {

    @OneToOne
    private User user;

    private String fullName;

    /**
     * Create a new Profile instance.
     */
    public Profile() {
    }

    /**
     * Create a new profile instance for a given user
     * @param user of type User
     */
    public Profile( User user ) {
        this.user = user;
    }

    /**
     * Return the user's full name.
     * @return the value of fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name of the associated user.
     * @param fullName the new full name.
     */
    @Secured( { "ROLE_ADMIN", "USER==user" } )
    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }

    /**
     * Return the profile's associated user.
     * @return the value of user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user of this profile.
     * @param user the new user value.
     */
    @Secured( { "ROLE_ADMIN", "ROLE_RUN_AS_SYSTEM" } )
    public void setUser( User user ) {
        this.user = user;
    }
}
