// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * A user's focus.
 */
@Entity
public class Focus extends NamedModelObject implements Countable {

    private static final long serialVersionUID = 7659094183274619107L;

    @ManyToOne
    private User user;

    /**
     * Create a new Focus instance.
     */
    public Focus() {
    }

    /**
     * Create a new Focus instance.
     *
     * @param name the given name
     */
    public Focus( String name ) {
        super( name );
    }

    /**
     * Return the Focus's user.
     * @return the value of user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user of this Focus.
     * @param user the new user value.
     *
     */
    public void setUser( User user ) {
        this.user = user;
    }

    /**
     * Return the Focus's size.
     * @return the value of size
     */
    // TODO implement focus.size
    @Transient
    public int getSize() {
        return 0;
    }

    /**
     * Return the count to show in the listing.
     * @return the value of count
     */
    // TODO implement focus.expertCount
    @Transient
    public int getCount() {
        return 0;
    }
}
