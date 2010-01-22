// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test method access restrictions.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class TestSecurity {

    @Autowired
    private IUser user;

    /**
     * Create a new TestSecurity instance.
     */
    public TestSecurity() {
    }

    @Test
    public void testUser() {
        assertTrue( user.isEnabled() );

        // TODO write tests for checking @Secured requirements
    }
}
