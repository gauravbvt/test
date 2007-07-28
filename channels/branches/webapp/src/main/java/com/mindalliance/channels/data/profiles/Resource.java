// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.profiles;

import com.mindalliance.channels.data.definitions.Category.Taxonomy;
import com.mindalliance.channels.data.definitions.Situation;
import com.mindalliance.channels.data.definitions.TypedObject;
import com.mindalliance.channels.data.support.GUID;

/**
 * Base implementation for resources.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class Resource extends TypedObject {

    private boolean operational;

    /**
     * Default constructor.
     */
    public Resource() {
        this( null, Taxonomy.Any );
    }

    /**
     * Default constructor.
     * @param guid the guid
     * @param taxonomy the taxonomy
     */
    public Resource( GUID guid, Taxonomy taxonomy ) {
        super( guid, taxonomy );
    }

    /**
     * Test if this resource is operational in a given circumstance.
     * @param circumstance the circumstance
     */
    public boolean isOperationalIn( Situation circumstance ) {
        // TODO
        return true;
    }

    /**
     * Return the value of operational.
     */
    public boolean isOperational() {
        return this.operational;
    }

    /**
     * Set the value of operational.
     * @param operational The new value of operational
     */
    public void setOperational( boolean operational ) {
        this.operational = operational;
    }
}
