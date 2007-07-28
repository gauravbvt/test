// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

/**
 * Caused by something.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 * @param <T> the type of the cause
 */
public interface Caused<T extends Occurrence> extends Timed {

    /**
     * Get the cause.
     */
    Cause<T> getCause();

    /**
     * Set the cause.
     * @param cause the cause
     */
    void setCause( Cause<T> cause );

}
