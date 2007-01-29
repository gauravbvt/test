// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.impl;

import java.beans.PropertyVetoException;

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
}
