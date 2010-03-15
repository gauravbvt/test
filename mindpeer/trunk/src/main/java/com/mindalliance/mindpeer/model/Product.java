// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

/**
 * Generic product.
 */
@Entity
@Inheritance
public abstract class Product extends NamedModelObject implements Countable {

    private static final long serialVersionUID = -7864575677669441984L;

    @ManyToOne
    private Profile profile;

    /**
     * Create a new product instance.
     * @param name the given name
     */
    protected Product( String name ) {
        super( name );
    }

    /**
     * Return the product's profile.
     * @return the value of profile
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * Sets the profile of this product.
     * @param profile the new profile value.
     */
    public void setProfile( Profile profile ) {
        this.profile = profile;
    }

    /**
     * Return the count to show in the listing.
     * @return the value of count
     */
    // TODO implement product count
    public int getCount() {
        return 0;
    }
}
