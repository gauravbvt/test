// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.data.support;

import java.beans.PropertyVetoException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.mindalliance.channels.AbstractSecurityTest;
import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.system.Registry;
import com.mindalliance.channels.data.system.System;

/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class AuditedObjectTest extends AbstractSecurityTest {

    private System systemImpl;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws PropertyVetoException, UserExistsException  {
        super.setUp();
        systemImpl = (System) super.system;
    }
    
    @Test
    public void testCreated() {
        assertNotNull( systemImpl.getCreated());
        assertTrue( !systemImpl.getCreated().after( systemImpl.getLastModified() ) );
    }
    
    @Test
    public void testCreator() {
        assertNull( systemImpl.getCreator() );
        assertNull( systemImpl.getLastModifier() );
        
        login( "admin", "admin" );
        Registry r = new Registry();
        User creator = r.getCreator();
        assertNotNull( creator );
        assertEquals( "admin", creator.getUsername() );
    }
    
    @Test
    public void testModifications() {
        login( "admin", "admin" );

        systemImpl.setRegistry( new Registry() );
        assertNotNull( systemImpl.getLastModifier() );
        assertEquals( "admin", systemImpl.getLastModifier().getUsername() );
        assertTrue( systemImpl.getLastModified().after( systemImpl.getCreated() ) );
    }
}
