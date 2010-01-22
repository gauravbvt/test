// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.IUser;
import com.mindalliance.mindpeer.model.Profile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * ...
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class TestDataInitializer {

    private DataInitializer di;

    @Autowired
    private UserDao userDao;

    /**
     * Create a new TestDataInitializer instance.
     */
    public TestDataInitializer() {
    }

    @Before
    public void init() {
        di = new DataInitializer();
        di.setUserDao( userDao );
    }

    @Test
    @Transactional
    @Rollback
    public void testInit() {
        di.dataInit();
        assertEquals( 2L, (long) userDao.countAll() );

        IUser denis = userDao.findByName( "denis" );
        assertNotNull( denis );
        Profile profile = denis.getProfile();
        assertNotNull( profile );
        assertEquals( denis, profile.getUser() );
    }
}
