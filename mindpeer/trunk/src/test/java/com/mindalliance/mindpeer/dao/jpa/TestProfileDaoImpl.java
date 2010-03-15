// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.dao.jpa;

import com.mindalliance.mindpeer.dao.ProfileDao;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
@ContextConfiguration( locations = { "/applicationContext.xml", "/integratedTestContext.xml" } )
public class TestProfileDaoImpl {

    @Autowired
    private ProfileDao profileDao;

    public TestProfileDaoImpl() {
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByName() {
        assertNull( profileDao.findByName( null ) );
        assertNull( profileDao.findByName( "bla" ) );
        
        assertNotNull( profileDao.findByName( "guest" ) );
    }

}
