// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

/**
 * Something with a name. Anything with a name can be compared with
 * another also with a name.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Named extends Comparable<Named> {

    /**
     * Return the name of the object.
     */
    String getName();
}
