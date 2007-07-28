// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.support.AbstractJavaBean;

/**
 * An object about which assertions were made.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 * @param <T> the type of the object of the assertions
 *
 * @copmosed - - * Assertion
 */
public abstract class AssertedObject<T> extends AbstractJavaBean {

    private T object;
    private List<Assertion> assertions = new ArrayList<Assertion>();

    /**
     * Default constructor.
     */
    public AssertedObject() {
        super();
    }

    /**
     * Default constructor.
     * @param object the object of the assertions
     */
    public AssertedObject( T object ) {
        this();
        setObject( object );
    }

    /**
     * Return the object of the assertions.
     */
    public T getObject() {
        return this.object;
    }

    /**
     * Set the object of the assertions.
     * @param object the object
     */
    public void setObject( T object ) {
        this.object = object;
    }

    /**
     * Return the assertions.
     */
    public List<Assertion> getAssertions() {
        return this.assertions;
    }

    /**
     * Set the assertions.
     * @param assertions the assertions
     */
    public void setAssertions( List<Assertion> assertions ) {
        this.assertions = assertions;
    }

    /**
     * Add an assertion.
     * @param assertion the assertion
     */
    public void addAssertion( Assertion assertion ) {
        this.assertions.add( assertion );
    }

    /**
     * Remove an assertion.
     * @param assertion the assertion
     */
    public void removeAssertion( Assertion assertion ) {
        this.assertions.remove( assertion );
    }

    /**
     * Overriden from ....
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return object == null? 0 : object.hashCode();
    }

    /**
     * Overriden from ....
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj the object of the comparison
     */
    @Override
    public boolean equals( Object obj ) {
        boolean result = true;
        if ( this != obj ) {
            if ( obj == null || getClass() != obj.getClass() )
                result = false;
            else {
                final AssertedObject other = (AssertedObject) obj;
                if ( object == null ) {
                    if ( other.object != null )
                        result = false;
                } else if ( !object.equals( other.object ) )
                    result = false;
            }
        }

        return result;
    }
}
