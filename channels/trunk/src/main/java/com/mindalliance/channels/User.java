// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import java.util.List;

import org.acegisecurity.userdetails.UserDetails;

import com.mindalliance.channels.data.Named;
import com.mindalliance.channels.data.elements.resources.Role;

/**
 * A user of the system.
 *
 * The current user is associated with the current thread
 * through acegi definitions.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface User extends UserDetails, Named {

    /**
     * Return the short login name of the user.
     */
    String getUsername();
    /**
     * Return the roles played by the user, if any.
     * @return
     */
    List<Role> getRoles();
}
