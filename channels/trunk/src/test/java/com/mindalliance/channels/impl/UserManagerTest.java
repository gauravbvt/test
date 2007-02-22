// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.annotation.Order;

/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class UserManagerTest {

    private UserManager userManager;
    private TestListener listener;
    private UserImpl user;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.userManager = new UserManager();
        this.listener = new TestListener();
        this.userManager.addPropertyChangeListener( this.listener );

        this.user = new UserImpl(
                        "joe", "bla",
                        new GrantedAuthority[]{
                            new GrantedAuthorityImpl( "ROLE_USER" )
                        } );
    }

    /**
     * Test method for {@link UserManager#getUsers()}.
     */
    @Test
    public final void testGetUsers() {
        Map<String, UserImpl> users = this.userManager.getUsers();
        assertNotNull( users );
        assertEquals( 0, users.size() );
    }

    /**
     * Test method for {@link UserManager#setUsers(java.util.Map)}.
     */
    @Test
    public final void testSetUsers_1() {
        Map<String, UserImpl> users = new HashMap<String,UserImpl>();
        users.put( this.user.getName(), this.user );

        this.userManager.setUsers( users );
        assertEquals( 1, this.listener.getPropCount() );
        assertEquals( "users", this.listener.getLastProp().getPropertyName() );
        assertSame( users, this.listener.getLastProp().getNewValue() );
    }

    /**
     * Test method for {@link UserManager#setUsers(java.util.Map)}.
     */
    @Test( expected = NullPointerException.class )
    public final void testSetUsers_2() {
        this.userManager.setUsers( null );
    }

    /**
     * Test method for {@link UserManager#addUser(UserImpl)}.
     */
    @Test
    public final void testAddUser() {
        assertEquals( 0, this.userManager.getUsers().size() );
        this.userManager.addUser( this.user );

        assertEquals( 1, this.userManager.getUsers().size() );
        assertEquals( 1, this.listener.getPropCount() );
        assertEquals( "users", this.listener.getLastProp().getPropertyName() );

        assertSame(
                this.user,
                this.userManager.loadUserByUsername(
                        this.user.getUsername() ) );
    }

    /**
     * Test method for {@link UserManager#removeUser(UserImpl)}.
     */
    @Test
    public final void testRemoveUser() {

        // Empty removal
        this.userManager.removeUser( this.user );
        assertEquals( 1, this.listener.getPropCount() );
        assertEquals( "users", this.listener.getLastProp().getPropertyName() );

        this.userManager.addUser( this.user );
        this.listener.reset();

        this.userManager.removeUser( this.user );
        assertEquals( 1, this.listener.getPropCount() );
        assertEquals( "users", this.listener.getLastProp().getPropertyName() );
        assertEquals( 0, this.userManager.getUsers().size() );
    }

    /**
     * Test method for {@link UserManager#loadUserByUsername(java.lang.String)}.
     */
    @Test( expected = UsernameNotFoundException.class )
    public final void testLoadUserByUsername() {
        this.userManager.loadUserByUsername( this.user.getUsername() );
    }

}
