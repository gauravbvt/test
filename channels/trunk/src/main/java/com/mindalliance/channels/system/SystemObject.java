// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.beans.PropertyVetoException;

import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * An object in the system that has a unique name for each instance
 * (within a subclass).
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 */
public abstract class SystemObject
        extends AbstractJavaBean
        implements Comparable<SystemObject> {

    private String name;

    /**
     * Default constructor.
     */
    public SystemObject() {
        super();
    }

    /**
     * Default constructor.
     * @param name the unique name of this object
     * @throws PropertyVetoException if name conflicts with other objects.
     */
    public SystemObject( String name ) throws PropertyVetoException {
        this();
        setName( name );
    }

    /**
     * Return the value of name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the value of name.
     * @param name The new value of name
     * @throws PropertyVetoException if name conflicts with other objects.
     */
    public void setName( String name ) throws PropertyVetoException {
        if ( name == null )
            throw new NullPointerException();
        this.name = name;
    }

    /**
     * Compares this object with the specified object for order.
     * @param o the object to compare to
     */
    public int compareTo( SystemObject o ) {
        return getName().compareTo( o.getName() );
    }

    /**
     * Returns a string representation of the object.
     */
    @Override
    public String toString() {
        return getName();
    }
}
