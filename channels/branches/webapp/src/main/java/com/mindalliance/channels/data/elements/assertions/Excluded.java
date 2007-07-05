/*
 * Created on May 7, 2007
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.Excludable;
import com.mindalliance.channels.util.GUID;

/**
 * Assertion that something is mutually exclusive with something else.
 * 
 * @author jf
 */
public class Excluded extends Assertion {

    private Excludable mutuallyExcluded;

    public Excluded() {
        super();
    }

    public Excluded( GUID guid ) {
        super( guid );
    }

    /**
     * Get the Excludable target of the assertion.
     * 
     * @return
     */
    public Excludable getExcludable() {
        return (Excludable) getAbout();
    }

    /**
     * @return the mutuallyExcluded
     */
    public Excludable getMutuallyExcluded() {
        return mutuallyExcluded;
    }

    /**
     * @param mutuallyExcluded the mutuallyExcluded to set
     */
    public void setMutuallyExcluded( Excludable mutuallyExcluded ) {
        this.mutuallyExcluded = mutuallyExcluded;
    }

}
