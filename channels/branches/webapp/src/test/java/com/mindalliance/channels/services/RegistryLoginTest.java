// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.services;

import static org.junit.Assert.*;

import java.beans.PropertyVetoException;

import org.junit.Before;
import org.junit.Test;

import com.mindalliance.channels.AbstractSecurityTest;
import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.system.RegistryService;

public class RegistryLoginTest extends AbstractSecurityTest {

    private RegistryService registryService;
    
    @Before
    public void setUp()  throws PropertyVetoException, UserExistsException {
        super.setUp();
        registryService = system.getRegistryService();
    }
    
    /**
     * Tests that the login is effective.
     */
    @Test
    public void loginUser() {
        registryService.login( "user", "user" );
        try {
            User user = registryService.getAuthenticatedUser();
            assertEquals( user.getUsername(), "user" );
        }
        finally {
            registryService.logout();
        }
    }

    /**
     * Tests no login.
     */
    @Test
    public void noLogin() {
        assertNull( registryService.getAuthenticatedUser() );
    }
}
