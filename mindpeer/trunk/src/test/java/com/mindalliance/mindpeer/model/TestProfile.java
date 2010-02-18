// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import com.mindalliance.mindpeer.SecuredAspects;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

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

        SecuredAspects.bypass( true );
    }

    /**
     * Silly tests for coverage.
     */
    @Test
    public void testAccessors() {
        assertEquals( user, profile.getUser() );

        String s = "Some string";
        profile.setFullName( s );
        assertEquals( s, profile.getFullName() );

        profile.setUser( null );
        assertNull( profile.getUser() );

        profile.setOrganisation( s );
        assertEquals( s, profile.getOrganisation() );

        profile.setPhone( s );
        assertEquals( s, profile.getPhone() );

        profile.setFax( s );
        assertEquals( s, profile.getFax() );

        profile.setWebsite( s );
        assertEquals( s, profile.getWebsite() );

        profile.setDesignation( s );
        assertEquals( s, profile.getDesignation() );

        profile.setLocation( s );
        assertEquals( s, profile.getLocation() );

        HashSet<Tag> tags = new HashSet<Tag>();
        profile.setInterests( tags );
        assertSame( tags, profile.getInterests() );
    }

}
