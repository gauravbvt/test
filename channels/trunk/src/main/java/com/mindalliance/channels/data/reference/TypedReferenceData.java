// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

import com.mindalliance.channels.DisplayAs;
import com.mindalliance.channels.data.Typed;

/**
 * A reference data with a type.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class TypedReferenceData extends ReferenceData
    implements Typed {

    private TypeSet typeSet;

    /**
     * Default constructor.
     */
    public TypedReferenceData() {
    }

    /**
     * Return the value of typeSet.
     */
    @DisplayAs( direct = "types:",
            reverse = "type set for {1}",
            reverseMany = "type set for:"
            )
    public TypeSet getTypeSet() {
        return typeSet;
    }

    /**
     * Set the value of typeSet.
     * @param typeSet The new value of typeSet
     */
    public void setTypeSet( TypeSet typeSet ) {
        this.typeSet = typeSet;
    }
}
