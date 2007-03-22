// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.reference;

/**
 * A kind of role.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public abstract class RoleType extends Type {

    /**
     * Default constructor.
     */
    public RoleType() {
    }

    /**
     * Default constructor.
     * @param name the name of the role type
     */
    public RoleType( String name ) {
        super( name );
    }
}
