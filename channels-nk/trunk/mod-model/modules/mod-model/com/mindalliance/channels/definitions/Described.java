// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.definitions;

/**
 * Something with a name and description.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Described extends Comparable<Described> {

    /**
     * Return the name of the object, as it would appear
     * in a list.
     */
    String getName();

    /**
     * Get free-form long textual description.
     */
    String getDescription();
}
