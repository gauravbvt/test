// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.services;

import static org.junit.Assert.*;

import org.junit.Test;
import org.acegisecurity.context.SecurityContextHolder;

import com.mindalliance.channels.User;

public class RegistryLoginTest extends AbstractSecurityTest {
	/**
	 * Tests that the login is effective.
	 *
	 */
	@Test
	public void loginUser() {
		registryService.login("user", "user");
		try {
			User user = registryService.getAuthenticatedUser();
			assertEquals(user.getUsername(), "user");
		}
		finally {
			registryService.logout();
		}
	}

	/**
	 * Tests no login.
	 *
	 */
	@Test
	public void noLogin() {
		assertNull(registryService.getAuthenticatedUser());
	}
}
