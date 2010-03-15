// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.model;

/**
 * An object that can appear in a right list.
 */
public interface Countable {

    /**
     * Return the name to show in the link.
     * @return the value of name
     */
    String getName();

    /**
     * Return the count to show in a listing.
     * @return the value of count
     */
    int getCount();
}
