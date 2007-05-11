// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.beans.PropertyVetoException;

import org.junit.Test;

import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.user.UserImpl;

/**
 * A test for the security contracts of SystemImpl.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class SystemImplSecurityTest extends AbstractSecurityTest {

    @Test
    public void testAddUser_1() throws PropertyVetoException, UserExistsException {
        UserImpl u = new UserImpl("User2","user2","user2",new String[]{ 
                "ROLE_USER" } );
        
        system.addUser( u );
    }
    
    @Test
    public void testAddUser_2() throws PropertyVetoException, UserExistsException {
        UserImpl u = new UserImpl("User2","user2","user2",new String[]{ 
                "ROLE_USER" } );
        
        login( "admin", "admin" );
        system.addUser( u );
    }
}
