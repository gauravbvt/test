// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.discoveries;

import com.mindalliance.channels.models.Assertion;
import com.mindalliance.channels.models.Excludable;
import com.mindalliance.channels.support.GUID;

/**
 * Assertion that something is mutually exclusive with something else.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Excluded extends Assertion {

    private Excludable mutuallyExcluded;

    /**
     * Default constructor.
     */
    public Excluded() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Excluded( GUID guid ) {
        super( guid );
    }

    /**
     * Return the mutually excluded object.
     */
    public Excludable getMutuallyExcluded() {
        return mutuallyExcluded;
    }

    /**
     * Set the mutually excluded object.
     * @param mutuallyExcluded the mutually excluded object
     */
    public void setMutuallyExcluded( Excludable mutuallyExcluded ) {
        this.mutuallyExcluded = mutuallyExcluded;
    }
}
