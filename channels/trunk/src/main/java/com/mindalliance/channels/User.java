// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

/**
 * A user of the system.
 *
 * The current user is associated with the current thread
 * through acegi definitions.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface User {

    /**
     * Return the display name of a user.
     */
    String getName();

    /**
     * Test if a user is a member of a given group.
     * @param group the group
     */
    boolean isMemberOf( Group group );
}
