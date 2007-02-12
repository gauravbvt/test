// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import java.util.Set;

/**
 * A group of users (or groups).
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface Group extends User {

    /**
     * Return the members of this group.
     */
    Set<User> getMembers();
}
