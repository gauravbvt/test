// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.util;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * A test object.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class TestObject extends AbstractJavaBean {

    private int age;
    private String name;
    private String key;
    private boolean ok;
    private List<String> values = new ArrayList<String>();

    /**
     * @param guid
     */
    public TestObject() {
        super();
    }

    /* (non-Javadoc)
     * @see com.mindalliance.channels.remoting.TestRemoteInterface#getAge()
     */
    public int getAge() {
        return this.age;
    }

    /* (non-Javadoc)
     * @see com.mindalliance.channels.remoting.TestRemoteInterface#getKey()
     */
    public String getKey() {
        return this.key;
    }

    /* (non-Javadoc)
     * @see com.mindalliance.channels.remoting.TestRemoteInterface#getName()
     */
    public String getName() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see com.mindalliance.channels.remoting.TestRemoteInterface#isOk()
     */
    public boolean isOk() {
        return this.ok;
    }

    public void setAge( int age ) throws PropertyVetoException {
        this.age = age;
    }

    public void setKey( String key ) throws PropertyVetoException {
        this.key = key;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setOk( boolean ok ) throws PropertyVetoException {
        this.ok = ok;
    }

    /**
     * Return the value of values.
     */
    public List<String> getValues() {
        return this.values;
    }

    /**
     * Set the value of values.
     * @param values The new value of values
     */
    public void setValues( List<String> values ) {
        this.values = values;
    }

    public void addValue( String value ) {
        this.values.add( value );
    }

    public void removeValue( String value ) {
        this.values.remove( value );
    }

    public void addSomething( String something ) {
    }
}
