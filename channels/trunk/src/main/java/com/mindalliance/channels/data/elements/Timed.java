// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements;

import com.mindalliance.channels.data.support.Duration;

/**
 * Something that happens at some point after "time zero".
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Timed {

    /**
     * Get the duration.
     */
    Duration getDuration();

    /**
     * Returns the delta from the scenario time zero.
     */
    Duration getTime();

    /**
     * Return whether this occurrence starts after another starts.
     * @param occurrence the occurrence
     */
    boolean isAfter( Occurrence occurrence );

    /**
     * Return whether this occurrence starts after another starts.
     * @param occurrence the occurrence
     */
    boolean isBefore( Occurrence occurrence );

    /**
     * Return whether this occurrence overlaps another.
     * @param occurrence the occurrence
     */
    boolean isDuring( Occurrence occurrence );
}
