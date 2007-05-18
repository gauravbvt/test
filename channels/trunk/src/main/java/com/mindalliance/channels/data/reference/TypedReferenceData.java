// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

import com.mindalliance.channels.data.Typed;
import com.mindalliance.channels.data.support.TypeSet;


/**
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
abstract public class TypedReferenceData extends ReferenceData implements Typed {
    
    private TypeSet typeSet = new TypeSet( this.getClass().getSimpleName() );

    /**
     * Default constructor.
     */
    public TypedReferenceData() {
    }

    
    /**
     * Return the value of typeSet.
     */
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
