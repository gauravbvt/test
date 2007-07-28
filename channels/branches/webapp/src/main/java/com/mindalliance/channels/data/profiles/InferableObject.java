// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.profiles;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.definitions.Category.Taxonomy;
import com.mindalliance.channels.data.definitions.TypedObject;
import com.mindalliance.channels.data.support.Element;
import com.mindalliance.channels.data.support.GUID;

/**
 * A generic element that can be inferred by rules (as well as
 * entered by the users).
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public abstract class InferableObject extends TypedObject
    implements Element {

    private boolean inferred;

    /**
     * Default constructor.
     */
    public InferableObject() {
        this( null, Taxonomy.Any );
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public InferableObject( GUID guid ) {
        this( guid, Taxonomy.Any );
    }

    /**
     * Default constructor.
     * @param guid the guid
     * @param taxonomy the taxonomy for categories of this object
     */
    public InferableObject( GUID guid, Taxonomy taxonomy ) {
        super( guid, taxonomy );
    }

    /**
     * Mark this element as inferred.
     * @param inferred the inferred to set
     */
    public void setInferred( boolean inferred ) {
        this.inferred = inferred;
    }

    /**
     * Test if this element is inferred.
     */
    @PropertyOptions( ignore = true )
    public boolean isInferred() {
        return inferred;
    }
}
