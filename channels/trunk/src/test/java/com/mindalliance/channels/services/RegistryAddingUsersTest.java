// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.services;

import static org.junit.Assert.*;

import java.beans.PropertyVetoException;

import org.junit.Test;

import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;

/**
 * Tests for the security contracts of the Registry service.
 * 
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class RegistryAddingUsersTest extends AbstractSecurityTest {

    /**
     * Adding a new user with logged in user.
     * 
     * @throws PropertyVetoException
     * @throws UserExistsException
     */
    @Test
    public void testAddUserByUser() throws PropertyVetoException,
            UserExistsException {
        registryService.login( "user", "user" );
        User user1 = null;
        try {
            user1 = registryService.registerUser( "User1", "user1", "user1" );
        }
        finally {
            registryService.logout();
        }
        assertFalse( registryService.isAdministrator( user1 ) );
        assertTrue( registryService.isUserRegistered( user1 ) );
    }

    /**
     * Adding an existing user with logged in user.
     * 
     * @throws PropertyVetoException
     * @throws UserExistsException
     */
    @Test( expected = UserExistsException.class)
    public void testAddUserAgainByUser() throws PropertyVetoException,
            UserExistsException {
        registryService.login( "user", "user" );
        User u;
        try {
            u = registryService.registerUser( "A plain old user", "user",
                    "user" );
        }
        finally {
            registryService.logout();
        }
        fail();
    }

    /**
     * Adding a new administrator with logged in administrator.
     * 
     * @throws PropertyVetoException
     * @throws UserExistsException
     */
    @Test
    public void testAddAdminByAdmin() throws PropertyVetoException,
            UserExistsException {
        User user2 = null;
        registryService.login( "admin", "admin" );
        try {
            user2 = registryService.registerAdministrator( "User2", "user2",
                    "user2" );
        }
        finally {
            registryService.logout();
        }
        assertTrue( registryService.isAdministrator( user2 ) );
        assertTrue( registryService.isUserRegistered( user2 ) );
    }

    // TODO - Uncomment when @Secured is functional

    /**
     * Adding a new administrator with logged in user - must raise a
     * security exception
     * 
     * @throws PropertyVetoException
     * @throws UserExistsException
     */
    /*
     * @Test( expected = SecurityException.class ) public void
     * testAddAdminByUser() throws PropertyVetoException,
     * UserExistsException { User u = null; registryService.login(
     * "user", "user" ); try {
     * registryService.registerAdministrator("User3","user3","user3"); }
     * finally {registryService.logout();} fail(); }
     */
    /**
     * Adding a new user with no logged in user
     * 
     * @throws PropertyVetoException
     * @throws UserExistsException
     */
    /*
     * @Test( expected = SecurityException.class ) public void
     * testAddUserByNotLoggedIn() throws PropertyVetoException,
     * UserExistsException { User u; registryService.registerUser(
     * "User4","user4","user4" ); fail(); }
     */
    /**
     * Adding a new admin with no logged in user
     * 
     * @throws PropertyVetoException
     * @throws UserExistsException
     */
    /*
     * @Test( expected = SecurityException.class ) public void
     * testAddAdminByNotLoggedIn() throws PropertyVetoException,
     * UserExistsException { User u;
     * registryService.registerAdministrator( "User5","user5","user5" );
     * fail(); }
     */

}
