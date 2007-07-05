// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.user;

import java.beans.PropertyVetoException;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class UserImplTest {

    private UserImpl user;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        user = new UserImpl( "User", "user", "password", new String[]{ "ROLE_USER" } );
    }

    /**
     * Test method for {@link UserImpl#setEnabled(boolean)}.
     */
    @Test
    public final void testSetEnabled() {
        assertTrue( user.isEnabled() );
        assertTrue( user.isAccountNonDisabled() );
        
        user.setEnabled( false );
        assertFalse( user.isEnabled() );
        assertFalse( user.isAccountNonDisabled() );

        user.setEnabled( true );
        assertTrue( user.isEnabled() );
        assertTrue( user.isAccountNonDisabled() );
    }
    
    /**
     * Test method for {@link UserImpl#getGrantedAuthorities()}.
     */
    @Test
    public final void testGetGrantedAuthorities() {
        assertEquals( new String[]{ "ROLE_USER" }, user.getGrantedAuthorities() );
    }
    
    /**
     * Test method for {@link UserImpl#setPassword()}.
     */
    @Test
    public final void testGetSetPassword() {
        assertEquals( "password", user.getPassword() );
        user.setPassword( "bla" );
        assertEquals( "bla", user.getPassword() );
    }
    
    /**
     * Test method for {@link UserImpl#setName()}.
     * @throws PropertyVetoException 
     */
    @Test
    public final void testGetSetName() throws PropertyVetoException {
        user.setName( "Joe Bob" );
        assertEquals( "Joe Bob", user.getName() );
    }
    
    /**
     * Test method for {@link UserImpl#setEmail()}.
     */
    @Test
    public final void testGetSetEmail() {
        assertNull( user.getEmail() );
        user.setEmail( "joe@example.com" );
        assertEquals( "joe@example.com", user.getEmail() );
    }
    
    /**
     * Test method for {@link UserImpl#getGrantedAuthorities()}.
     */
    @Test
    public final void testGetAuthorities() {
        GrantedAuthority[] grantedAuthorities = 
            new GrantedAuthority[]{ new GrantedAuthorityImpl( "ROLE_USER" ) };
        assertEquals( grantedAuthorities, user.getAuthorities() );
        assertEquals( grantedAuthorities, user.getAuthorities() );
    }

    /**
     * Test method for {@link UserImpl#toString()}.
     */
    @Test
    public final void testToString() {
        assertEquals( "User", user.toString() );
    }

}
