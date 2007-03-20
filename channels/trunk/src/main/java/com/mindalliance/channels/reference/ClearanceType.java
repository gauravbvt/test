// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.reference;

/**
 * A kind of clearance.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public abstract class ClearanceType extends Type {

    /**
     * Default constructor.
     */
    public ClearanceType() {
        super();
    }

    /**
     * Default constructor.
     * @param name the name of this clearance
     */
    public ClearanceType( String name ) {
        super( name );
    }
}
