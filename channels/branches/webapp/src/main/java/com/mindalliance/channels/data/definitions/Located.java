// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.definitions;

/**
 * Something that has a location.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @navassoc - - 0..1 Location
 */
public interface Located {

    /**
     * Return the location.
     */
    Location getLocation();
}
