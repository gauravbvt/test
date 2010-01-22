// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import static com.mindalliance.mindpeer.model.User.State;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;

import java.util.Date;

/**
 * ...
 */
public class TestUser {

    private User user;

    /**
     * Create a new TestUser instance.
     */
    public TestUser() {
    }

    /**
     * ...
     */
    @Before
    public void init() {
        user = new User();
        user.setId( 2L );
        user.setUsername( "bob" );
        user.setPassword( "pwd" );
        user.setEmail( "bla" );
        user.setProfile( new Profile( user ) );
    }

    /**
     * ...
     */
    @Test
    public void testGetters() {
        assertEquals( 2L, (long) user.getId() );
        assertEquals( "bob", user.getUsername() );
        assertEquals( "pwd", user.getPassword() );
        assertEquals( "bla", user.getEmail() );
    }

    /**
     * ...
     */
    @Test
    public void testStates() {
        // Bogus tests for 100% coverage...
        assertEquals( 3, State.values().length );
        assertEquals( State.Registered, State.valueOf( "Registered" ) );
        assertEquals( State.Unconfirmed, State.valueOf( "Unconfirmed" ) );
        assertEquals( State.Terminated, State.valueOf( "Terminated" ) );

        assertTrue( user.isCredentialsNonExpired() );

        assertTrue( user.isEnabled() );
        assertTrue( user.isAccountNonExpired() );
        assertFalse( user.isAccountNonLocked() );
        assertNotNull( user.getConfirmation() );
        assertEquals( State.Unconfirmed, user.getState() );

        user.setConfirmation( null );
        assertNull( user.getConfirmation() );
        assertTrue( user.isEnabled() );
        assertTrue( user.isAccountNonExpired() );
        assertTrue( user.isAccountNonLocked() );
        assertEquals( State.Registered, user.getState() );

        user.setEnabled( false );
        assertFalse( user.isEnabled() );
        assertFalse( user.isAccountNonExpired() );
        assertFalse( user.isAccountNonLocked() );
        assertEquals( State.Terminated, user.getState() );
    }

    /**
     * ...
     */
    @Test
    public void testHashCode() {
        assertEquals( user.getId().hashCode(), user.hashCode() );
    }

    /**
     * ...
     */
    @Test
    public void testEquals() {
        assertTrue( user.equals( user ) );

        assertFalse( user.equals( "bob" ) );
        assertFalse( user.equals( null ) );

        User u = new User();
        u.setId( 2L );
        assertTrue( user.equals( u ) );
        u.setId( 3L );
        assertFalse( user.equals( u ) );
    }

    /**
     * ...
     */
    @Test
    public void testDate() {
        assertSame( user.getCreated(), user.getLastModified() );

        Date d = new Date();
        user.setCreated( d );
        assertSame( d, user.getCreated() );

        user.setLastModified( d );
        assertSame( d, user.getLastModified() );
    }

    /**
     * ...
     */
    @Test
    public void testAuthorities() {
        GrantedAuthority[] authorities = user.getAuthorities();
        assertEquals( 1, authorities.length );
        assertEquals( "ROLE_USER", authorities[0].getAuthority() );

        User a = new User();
        a.setId( 1L );
        GrantedAuthority[] authorities2 = a.getAuthorities();
        assertEquals( 2, authorities2.length );
        assertEquals( "ROLE_USER", authorities2[0].getAuthority() );
        assertEquals( "ROLE_ADMIN", authorities2[1].getAuthority() );

    }

}
