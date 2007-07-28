// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import java.util.List;

import com.beanview.annotation.PropertyOptions;

import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.data.support.GUID;

/**
 * A result of something.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class Product extends StorylineElement
    implements Caused<Task>, Storable {

    private Cause<Task> cause;

    /**
     * Default constructor.
     */
    public Product() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Product( GUID guid ) {
        super( guid );
    }

    /**
     * Get the duration.
     * @return NONE
     */
    @PropertyOptions( ignore = true )
    public Duration getDuration() {
        return Duration.NONE;
    }

    /**
     * Test if this happens after an occurrence.
     * @param occurrence the occurrence
     * @return false
     */
    public boolean isAfter( Occurrence occurrence ) {
        return false;
    }

    /**
     * Test if this happens before an occurrence.
     * @param occurrence the occurrence
     * @return false
     */
    public boolean isBefore( Occurrence occurrence ) {
        return false;
    }

    /**
     * Test if this happens during an occurrence.
     * @param occurrence the occurrence
     * @return false
     */
    public boolean isDuring( Occurrence occurrence ) {
        return false;
    }

    /**
     * Get the delta time since time 0.
     */
    @PropertyOptions( ignore = true )
    public Duration getTime() {
        return null;
    }

    /**
     * Get the StoredIn assertions.
     */
    @PropertyOptions( ignore = true )
    public List<StoredIn> getStoredInAssertions() {
        return null;
    }

    /**
     * Return the cause.
     */
    public Cause<Task> getCause() {
        return (Cause<Task>) cause;
    }

    /**
     * Set the cause.
     * @param cause the cause to set
     */
    public void setCause( Cause<Task> cause ) {
        this.cause = cause;
    }

}
