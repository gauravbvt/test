// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import org.acegisecurity.userdetails.UserDetails;

/**
 * A user of the system.
 *
 * The current user is associated with the current thread
 * through acegi definitions.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface User extends UserDetails {

    /**
     * Return the display name of a user.
     */
    String getName();

    /**
     * Return the short login name of the user.
     */
    String getUsername();

}
