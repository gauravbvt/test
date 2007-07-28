// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import com.mindalliance.channels.data.support.GUID;

/**
 * Request made by a user. A request is either active or inactive.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class UserRequest extends Statement {

    private boolean active;

    /**
     * Default constructor.
     */
    public UserRequest() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public UserRequest( GUID guid ) {
        super( guid );
    }

    /**
     * Return whether the request is in effect.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set whether the request is in effect.
     * @param active the active state
     */
    public void setActive( boolean active ) {
        this.active = active;
    }
}
