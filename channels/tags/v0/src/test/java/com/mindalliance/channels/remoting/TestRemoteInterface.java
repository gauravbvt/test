// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.remoting;

import java.beans.PropertyVetoException;

import com.mindalliance.channels.remoting.JavaBean;

/**
 * Test interface that will hopefully cover all cases...
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface TestRemoteInterface extends JavaBean {

    int getAge();
    void setAge( int age ) throws PropertyVetoException;

    boolean isOk();
    void setOk( boolean ok ) throws PropertyVetoException;

    String getName();
    void setName( String name );
    
    String getKey();
    void setKey( String key ) throws PropertyVetoException;

}
