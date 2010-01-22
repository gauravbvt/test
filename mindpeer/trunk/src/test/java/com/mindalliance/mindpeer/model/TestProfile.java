// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

/**
 * ...
 */
public class TestProfile {

    private Profile profile;

    private User user;

    /**
     * Create a new TestProfile instance.
     */
    public TestProfile() {
    }

    @Before
    public void init() {
        user = new User();
        profile = user.getProfile();
    }

    /**
     * Silly tests for coverage.
     */
    @Test
    public void testAccessors() {
        assertEquals( user, profile.getUser() );

        String s = "Joe Blow";
        profile.setFullName( s );
        assertEquals( s, profile.getFullName() );

        profile.setUser( null );
        assertNull( profile.getUser() );
    }

}
