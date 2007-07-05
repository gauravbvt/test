// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.components;

import com.mindalliance.channels.data.Described;
import com.mindalliance.channels.data.reference.TypeSet;
import com.mindalliance.channels.data.reference.Typed;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * A mission statement.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Mission extends AbstractJavaBean implements Described, Typed {

    private TypeSet typeSet;
    private String description;

    /**
     * Default constructor.
     */
    public Mission() {
    }

    /**
     * Set the description.
     * @param description the description to set
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * Return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the typeSet.
     */
    public TypeSet getTypeSet() {
        return typeSet;
    }

    /**
     * Set the typeSet.
     * @param typeSet the typeSet to set
     */
    public void setTypeSet( TypeSet typeSet ) {
        this.typeSet = typeSet;
    }
}
