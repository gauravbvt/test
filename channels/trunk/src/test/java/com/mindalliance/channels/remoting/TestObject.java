// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

import java.beans.PropertyVetoException;

import com.mindalliance.channels.remoting.AbstractRemotableBean;
import com.mindalliance.channels.remoting.GUID;
import com.mindalliance.channels.remoting.GUIDFactory;

/**
 * A test object.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class TestObject extends AbstractRemotableBean
    implements TestRemoteInterface {

    private int age;
    private String name;
    private String key;
    private boolean ok;

    /**
     * @param guid
     */
    public TestObject( GUID guid ) {
        super( guid );
    }

    /**
     * @param factory
     */
    public TestObject( GUIDFactory factory ) {
        super( factory );
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
        int old = this.age;
        super.fireVetoableChange( "age", old, age );
        this.age = age;
        super.firePropertyChange( "age", old, age );
    }

    public void setKey( String key ) throws PropertyVetoException {
        String old = this.key;
        super.fireVetoableChange( "key", old, key );
        this.key = key;
        super.firePropertyChange( "key", old, key );
    }

    public void setName( String name ) {
        String old = this.name;
        this.name = name;
        super.firePropertyChange( "name", old, name );
    }

    public void setOk( boolean ok ) throws PropertyVetoException {
        boolean old = this.ok;
        super.fireVetoableChange( "ok", old, ok );
        this.ok = ok;
        super.firePropertyChange( "ok", old, ok );
    }
}
