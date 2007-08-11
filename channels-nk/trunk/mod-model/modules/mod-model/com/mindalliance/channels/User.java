// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import org.acegisecurity.userdetails.UserDetails;

import com.beanview.annotation.PropertyOptions;

/**
 * A user of the system.
 *
 * The current user is associated with the current thread
 * through acegi definitions.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision: 290 $
 */
public interface User extends UserDetails, JavaBean {

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
     * Return the full name of the user.
     */
    String getName();

    /**
     * Return if a user is a standard user.
     */
    @PropertyOptions( ignore = true )
    boolean isStandardUser();

    /**
     * Return if a user is an administrator.
     */
    @PropertyOptions( ignore = true )
    boolean isAdmin();
}
