// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

import com.mindalliance.channels.Named;
import com.mindalliance.channels.data.Described;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * ReferenceData data.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class ReferenceData extends AbstractJavaBean
    implements Named, Described {

    private String name;
    private String description;

    /**
     * Default constructor.
     */
    public ReferenceData() {
    }

    /**
     * Sort according to name.
     * @param o the other data to compare to.
     */
    public int compareTo( Named o ) {
        return name.compareTo( o.getName() );
    }

    /**
     * Return the value of description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description.
     * @param description The new value of description
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * Return the value of name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name.
     * @param name The new value of name
     */
    public void setName( String name ) {
        this.name = name;
    }

    /** Provide a printed form. */
    @Override
    public String toString() {
        return getName();
    }

}
