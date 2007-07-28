// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.support;

/**
 * Something with a globally unique id.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Unique {

    /**
     * Return the globally unique id.
     */
    GUID getGuid();

}
