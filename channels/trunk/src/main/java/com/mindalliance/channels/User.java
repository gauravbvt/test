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
public interface User extends UserDetails, JavaBean, Named {

    /**
     * The standard user role.
     */
    String USER_ROLE = "USER_ROLE";

    /**
     * The standard administrator role.
     */
    String ADMIN_ROLE = "ADMIN_ROLE";

    /**
     * Return the short login name of the user.
     */
    String getUsername();

    /**
     * Return if a user is a standard user.
     */
    boolean isStandardUser();

    /**
     * Return if a user is an administrator.
     */
    boolean isAdmin();
}
