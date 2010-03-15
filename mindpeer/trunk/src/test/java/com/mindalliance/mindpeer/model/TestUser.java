// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import com.mindalliance.mindpeer.SecuredAspects;
import static com.mindalliance.mindpeer.model.User.State;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

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
        SecuredAspects.bypass( true );

        user = new User();
        user.setId( 2L );
        user.setUsername( "bob" );
        user.setPassword( "pwd" );
        user.setEmail( "bla" );
        user.setProfile( new Profile() );
    }

    @After
    public void reset() {
        SecuredAspects.bypass( false );
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
        assertFalse( user.isConfirmed() );

        assertEquals( 0, user.getFocusList().size() );
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
        assertFalse( user.isAccountNonLocked() );
        assertEquals( State.Unconfirmed, user.getState() );

        user.setConfirmed( true );
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
        // may  fail because of aspect
        // assertEquals( user.getCreated(), user.getLastModified() );

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
        Collection<GrantedAuthority> authorities = user.getAuthorities();
        assertEquals( 1, authorities.size() );
        assertEquals( "ROLE_USER", authorities.iterator().next().getAuthority() );

        User a = new User();
        a.setId( 1L );
        Collection<GrantedAuthority> authorities2 = a.getAuthorities();
        assertEquals( 2, authorities2.size() );
        Iterator<GrantedAuthority> i = authorities2.iterator();
        assertEquals( "ROLE_USER", i.next().getAuthority() );
        assertEquals( "ROLE_ADMIN", i.next().getAuthority() );

    }

}
